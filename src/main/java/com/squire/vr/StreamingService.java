package com.squire.vr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class StreamingService extends Service {
    private static final String TAG = "TRDStreaming";
    private volatile Process rcloneProcess;
    private volatile Process p7zipProcess;
    private volatile boolean isStopped = false;
    private boolean isSuccess = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String releaseName = intent.getStringExtra("releaseName");
        String rcloneUrl = intent.getStringExtra("rcloneUrl");
        String baseUri = intent.getStringExtra("baseUri");
        String savePath = intent.getStringExtra("savePath");
        long totalBytes = intent.getLongExtra("totalBytes", 0);
        String password = intent.getStringExtra("password");
        if (password == null) password = Config.VRP_DEFAULT_PASSWORD; // Fallback
        
        // Fix/Verify MD5 Hash if rcloneUrl is not a valid hash
        if (releaseName != null && (rcloneUrl == null || !rcloneUrl.matches("^[a-fA-F0-9]{32}$"))) {
            try {
                Log.w(TAG, "rcloneUrl is not a hash, calculating MD5 for: " + releaseName);
                MessageDigest md = MessageDigest.getInstance("MD5");
                // VRP Protocol: MD5(releaseName + "\n")
                byte[] digest = md.digest((releaseName + "\n").getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                rcloneUrl = sb.toString();
                Log.i(TAG, "Calculated Hash: " + rcloneUrl);
            } catch (Exception e) {
                Log.e(TAG, "Failed to calculate MD5", e);
            }
        }

        final String finalPass = password; // Do not trim, match RS server
        final String finalReleaseName = releaseName;
        final String finalRcloneUrl = rcloneUrl;
        final String finalBaseUri = baseUri;
        final String finalSavePath = savePath;
        final long finalTotalBytes = totalBytes;

        isStopped = false;
        isSuccess = false;
        
        new Thread(() -> startStreaming(finalReleaseName, finalRcloneUrl, finalBaseUri, finalSavePath, finalTotalBytes, finalPass)).start();
        
        return START_NOT_STICKY;
    }

    private void startStreaming(final String releaseName, String rcloneUrl, String baseUri, String savePath, long estimatedTotalBytes, String password) {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        String p7zipPath = libPath + "/lib7z.so";
        
        // Ensure binaries are executable (Required for Android 10+)
        new File(rclonePath).setExecutable(true);
        new File(p7zipPath).setExecutable(true);

        StringBuilder debugLog = new StringBuilder();
        debugLog.append("Mode: Buffered Streaming (Disk-to-Disk)\n");

        File tempDir = new File(savePath, "temp_archives");
        try {
            if (isStopped) return;
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new Exception("Failed to create temp directory at " + tempDir.getAbsolutePath());
            }

            // 1. Get File List
            JSONArray files = listFiles(rcloneUrl, baseUri);
            if (isStopped) return;
            if (files.length() == 0) {
                broadcastError("No files found for game: " + releaseName);
                return;
            }

            List<JSONObject> sortedFiles = new ArrayList<>();
            long totalDownloadSizeCalculated = 0;
            for (int i = 0; i < files.length(); i++) {
                JSONObject f = files.getJSONObject(i);
                String name = f.optString("Name");
                boolean isArchive = name.matches(".*\\.\\d{3}$") || 
                                  name.endsWith(".7z") || 
                                  name.endsWith(".zip") ||
                                  name.endsWith(".rar") ||
                                  name.endsWith(".obvr");
                
                if (f.optBoolean("IsDir") || !isArchive) continue;
                sortedFiles.add(f);
                totalDownloadSizeCalculated += f.optLong("Size", 0);
            }
            Collections.sort(sortedFiles, (o1, o2) -> o1.optString("Name").compareTo(o2.optString("Name")));

            if (sortedFiles.isEmpty()) {
                broadcastError("No archive files detected in folder.");
                return;
            }

            final long totalDownloadSize = totalDownloadSizeCalculated;
            final long startTime = System.currentTimeMillis();
            long transferred = 0;
            
            // Resume Fix: If we have existing data, we must account for it in "transferred"
            // AND we should NOT reset "startTime" to now, or the speed calc will be wrong.
            // A better way for speed calc on resume is to measure speed based on *session* bytes and time.
            long sessionTransferred = 0;
            long sessionStartTime = System.currentTimeMillis();

            // 2. Download All Parts to Disk
            byte[] buffer = new byte[512 * 1024]; // 512KB buffer
            for (JSONObject file : sortedFiles) {
                if (isStopped) return;
                String fileName = file.optString("Name");
                long size = file.optLong("Size", 0);
                File targetFile = new File(tempDir, fileName);
                
                // RESUME LOGIC: Check if file exists
                long existingSize = 0;
                if (targetFile.exists()) {
                    existingSize = targetFile.length();
                    if (existingSize == size) {
                        debugLog.append("[SKIP] ").append(fileName).append(" (Completed)\n");
                        transferred += size;
                        continue;
                    } else if (existingSize > size) {
                        // Corrupt? Delete and restart
                        targetFile.delete();
                        existingSize = 0;
                    } else {
                        // Partial download - Resume!
                        debugLog.append("[RESUME] ").append(fileName).append(" from ").append(existingSize).append("\n");
                        transferred += existingSize;
                    }
                } else {
                    debugLog.append("[DOWN] ").append(fileName).append("\n");
                }
                
                // Use rclone cat and manual copy for live progress
                // Added --offset for resume support
                List<String> cmd = new ArrayList<>();
                cmd.add(rclonePath);
                cmd.add("cat");
                cmd.add("--config");
                cmd.add("/dev/null");
                cmd.add("--user-agent");
                cmd.add("rclone/v1.68.2");
                cmd.add("--no-check-certificate");
                if (existingSize > 0) {
                    cmd.add("--offset");
                    cmd.add(String.valueOf(existingSize));
                }
                cmd.add("--http-url");
                cmd.add(baseUri);
                cmd.add(":http:" + rcloneUrl + "/" + fileName);

                ProcessBuilder rb = new ProcessBuilder(cmd);
                rb.environment().put("LD_LIBRARY_PATH", libPath);
                
                rcloneProcess = rb.start();
                try (InputStream in = rcloneProcess.getInputStream();
                     FileOutputStream out = new FileOutputStream(targetFile, true)) { // Append mode
                    
                    int bytesRead;
                    long lastUpdate = 0;
                    long lastSync = 0; // For saving state
                    while ((bytesRead = in.read(buffer)) != -1) {
                        if (isStopped) break;
                        out.write(buffer, 0, bytesRead);
                        transferred += bytesRead;
                        sessionTransferred += bytesRead; // Track bytes downloaded THIS session
                        
                        long now = System.currentTimeMillis();
                        if (now - lastUpdate > 500) { // Update every 500ms
                             // Pass session metrics for accurate speed calc
                            broadcastProgress(releaseName, transferred, totalDownloadSize, sessionStartTime, sessionTransferred, "Downloading Archives...", "download");
                            lastUpdate = now;
                        }
                        
                        // PERIODIC SYNC: Force write to disk every 2 seconds to save state
                        if (now - lastSync > 2000) {
                            try { out.getFD().sync(); } catch(Exception e) {}
                            lastSync = now;
                        }
                    }
                }

                if (isStopped) return;
                int exit = rcloneProcess.waitFor();
                if (exit != 0) {
                    throw new Exception("Rclone download failed for " + fileName);
                }
            }

            if (isStopped) return;

            // 3. Extract Files from Disk
            debugLog.append("[EXTRACT] Starting 7z extraction from disk\n");
            
            // Extract using the first file (7z handles multi-part if .001 is pointed to)
            String firstFile = sortedFiles.get(0).optString("Name");
            File firstFilePath = new File(tempDir, firstFile);
            final long extractStartTime = System.currentTimeMillis();

            // Use -bsp1 for progress output on stdout
            ProcessBuilder p7zPb = new ProcessBuilder(p7zipPath, "x", firstFilePath.getAbsolutePath(), 
                                                       "-y", "-o" + savePath, "-p" + password, "-bsp1");
            p7zPb.environment().put("LD_LIBRARY_PATH", libPath);
            p7zipProcess = p7zPb.start();
            
            final StringBuilder p7zError = new StringBuilder();
            
            // Separate thread for progress parsing (7z uses \r for progress)
            new Thread(() -> {
                try (InputStream stm = p7zipProcess.getInputStream()) {
                    byte[] lineBuf = new byte[128];
                    int pos = 0;
                    int b;
                    while ((b = stm.read()) != -1) {
                        if (isStopped) break;
                        if (b == '\r' || b == '\n' || pos >= lineBuf.length - 1) {
                            String line = new String(lineBuf, 0, pos);
                            pos = 0;
                            // Extract percentage using a robust search
                            int pctIdx = line.indexOf('%');
                            if (pctIdx > 0) {
                                int start = pctIdx - 1;
                                while (start >= 0 && Character.isDigit(line.charAt(start))) start--;
                                String numStr = line.substring(start + 1, pctIdx).trim();
                                if (!numStr.isEmpty()) {
                                    int p = Integer.parseInt(numStr);
                                    // Map 0-100% to a virtual "byte count" for ETA calculation
                                    long virtualTotal = 100000;
                                    long virtualCurrent = (long)(virtualTotal * (p / 100.0));
                                    // Extract phase speed is hard to calc accurately without knowing total extract size
                                    // But we can estimate based on virtual progress to give an ETA
                                    broadcastProgress(releaseName, virtualCurrent, virtualTotal, extractStartTime, virtualCurrent, "Extracting (" + p + "%)", "extract");
                                }
                            }
                        } else {
                            lineBuf[pos++] = (byte)b;
                        }
                    }
                } catch (Exception ignored) {}
            }).start();

            new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p7zipProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) p7zError.append(line).append("\n");
                } catch (Exception ignored) {}
            }).start();

            int exitCode = p7zipProcess.waitFor();
            if (isStopped) return;
            if (exitCode == 0) {
                isSuccess = true;
                broadcastComplete(releaseName, savePath);
            } else {
                throw new Exception("7z Exit " + exitCode + "\n7z Err: " + p7zError.toString());
            }

        } catch (Exception e) {
            if (!isStopped) {
                Log.e(TAG, "Buffered Streaming failed", e);
                broadcastError("Error: " + e.getMessage());
            }
        } finally {
            // 4. Cleanup Temp Archives
            // Only cleanup if SUCCESS or if it was NOT a stop (i.e. error)
            // If stopped (paused), keep files for resume
            if (isSuccess) {
                if (tempDir.exists()) {
                    deleteDir(tempDir);
                    debugLog.append("[CLEANUP] Temp folder removed\n");
                }
            }
            
            // 5. Cleanup whole savePath if stopped or failed (BUT NOT IF SUCCESS)
            // Modified: Disable cleanup on stop to allow resuming
            /*
            if (isStopped && !isSuccess) {
                File gameDir = new File(savePath);
                if (gameDir.exists()) {
                    Log.d(TAG, "Cleanup: Deleting partial game folder due to stop: " + savePath);
                    deleteDir(gameDir);
                }
            }
            */
        }
    }
    
    private void deleteDir(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteDir(child);
            }
        }
        file.delete();
    }
    
    private JSONArray listFiles(String hash, String baseUri) throws Exception {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        
        // Ensure binary is executable
        new File(rclonePath).setExecutable(true);
        
        ProcessBuilder pb = new ProcessBuilder(rclonePath, "lsjson", "--config", "/dev/null",
                "--user-agent", "rclone/v1.68.2",
                "--no-check-certificate",
                "--http-url", baseUri, ":http:" + hash + "/");
        pb.environment().put("LD_LIBRARY_PATH", libPath);
        
        Process p = pb.start();
        java.util.Scanner s = new java.util.Scanner(p.getInputStream()).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";
        p.waitFor();
        
        if (p.exitValue() != 0) {
            java.util.Scanner err = new java.util.Scanner(p.getErrorStream()).useDelimiter("\\A");
            String errStr = err.hasNext() ? err.next() : "";
            Log.e(TAG, "lsjson failed: " + errStr);
            throw new Exception("Failed to list files: " + errStr);
        }
        
        return new JSONArray(output);
    }

    private long lastSmoothedSpeed = 0;

    private void broadcastProgress(String name, long current, long total, long sessionStartTime, long sessionBytes, String statusMsg, String phase) {
        if (isStopped) return;
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("name", name);
        intent.putExtra("current", current);
        intent.putExtra("total", total);
        intent.putExtra("statusMsg", statusMsg);
        intent.putExtra("phase", phase);
        
        int percent = total > 0 ? (int)((current * 100) / total) : 0;
        intent.putExtra("progress_percent", percent);
        
        long elapsed = System.currentTimeMillis() - sessionStartTime;
        if (elapsed > 0) {
            long currentAverageSpeed = (sessionBytes * 1000) / elapsed; // bytes per second
            
            // Smoothing logic: Reduce flickering by blending with previous speed
            // If it's the very start (elapsed < 2000ms), trust the average less to avoid spikes
            if (lastSmoothedSpeed == 0 || elapsed < 1000) {
                lastSmoothedSpeed = currentAverageSpeed;
            } else {
                // Alpha = 0.2 (Keep 80% of old speed, add 20% of new average) for stability
            
            // USER REQUEST: Make it "smooth like normal resume".
            // The issue is likely that "sessionStartTime" resets to NOW, so the first few seconds have tiny elapsed time.
            // But we can't trust the previous session's start time because the app was closed.
            // THE FIX: Ignore the first 3 seconds of speed calculation entirely to hide the "warm up" spike.
            // Just show "Calculating..." or hold the previous value (0) until we have stable data.
            
            if (elapsed < 3000) {
                 // Return -1 to indicate "Calculating..." state
                 intent.putExtra("speed", -1L);
            } else {
                 // Use a simple moving average window (last 5 seconds) instead of session average?
                 // For now, sticking to smoothed session average but AFTER the warm-up period.
                 if (lastSmoothedSpeed == 0) lastSmoothedSpeed = currentAverageSpeed;
                 else lastSmoothedSpeed = (long) (lastSmoothedSpeed * 0.9 + currentAverageSpeed * 0.1); // Slower smoothing (90% old)
                 intent.putExtra("speed", lastSmoothedSpeed);
            }
        }
        }
        
        sendBroadcast(intent);
    }

    private void broadcastError(String msg) {
        if (isStopped) return;
        // Suppress "No such file or directory" error if we are stopping/cancelling
        if (msg != null && msg.contains("No such file or directory") && isStopped) return;
        
        Log.e(TAG, "Broadcasting error: " + msg);
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("error", msg);
        sendBroadcast(intent);
    }
    
    private void broadcastComplete(String releaseName, String savePath) {
        if (isStopped) return;
        Log.i(TAG, "Broadcasting completion for: " + releaseName);
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("complete", true);
        intent.putExtra("name", releaseName);
        intent.putExtra("savePath", savePath);
        sendBroadcast(intent);
        
        // Brief delay to ensure broadcast is sent before service dies
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            stopSelf();
        }, 500);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isStopped = true;
        if (rcloneProcess != null) {
            rcloneProcess.destroy();
            rcloneProcess = null;
        }
        if (p7zipProcess != null) {
            p7zipProcess.destroy();
            p7zipProcess = null;
        }
        super.onDestroy();
    }
}
package com.trd.apk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String releaseName = intent.getStringExtra("releaseName");
        String rcloneUrl = intent.getStringExtra("rcloneUrl");
        String baseUri = intent.getStringExtra("baseUri");
        String savePath = intent.getStringExtra("savePath");
        long totalBytes = intent.getLongExtra("totalBytes", 0);
        String password = intent.getStringExtra("password");
        if (password == null) password = Config.VRP_DEFAULT_PASSWORD; // Fallback
        
        final String finalPass = password; // Do not trim, match RS server
        final String finalReleaseName = releaseName;
        final String finalRcloneUrl = rcloneUrl;
        final String finalBaseUri = baseUri;
        final String finalSavePath = savePath;
        final long finalTotalBytes = totalBytes;

        isStopped = false;
        
        new Thread(() -> startStreaming(finalReleaseName, finalRcloneUrl, finalBaseUri, finalSavePath, finalTotalBytes, finalPass)).start();
        
        return START_NOT_STICKY;
    }

    private void startStreaming(final String releaseName, String rcloneUrl, String baseUri, String savePath, long estimatedTotalBytes, String password) {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        String p7zipPath = libPath + "/lib7z.so";

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

            // 2. Download All Parts to Disk
            byte[] buffer = new byte[512 * 1024]; // 512KB buffer
            for (JSONObject file : sortedFiles) {
                if (isStopped) return;
                String fileName = file.optString("Name");
                File targetFile = new File(tempDir, fileName);
                debugLog.append("[DOWN] ").append(fileName).append("\n");
                
                // Use rclone cat and manual copy for live progress
                ProcessBuilder rb = new ProcessBuilder(rclonePath, "cat", "--config", "/dev/null",
                        "--user-agent", "rclone/v1.68.2",
                        "--http-url", baseUri,
                        ":http:" + rcloneUrl + "/" + fileName);
                rb.environment().put("LD_LIBRARY_PATH", libPath);
                
                rcloneProcess = rb.start();
                try (InputStream in = rcloneProcess.getInputStream();
                     FileOutputStream out = new FileOutputStream(targetFile)) {
                    
                    int bytesRead;
                    long lastUpdate = 0;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        if (isStopped) break;
                        out.write(buffer, 0, bytesRead);
                        transferred += bytesRead;
                        
                        long now = System.currentTimeMillis();
                        if (now - lastUpdate > 500) { // Update every 500ms
                            broadcastProgress(releaseName, transferred, totalDownloadSize, startTime, "Downloading Archives...", "download");
                            lastUpdate = now;
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
                                    broadcastProgress(releaseName, virtualCurrent, virtualTotal, extractStartTime, "Extracting (" + p + "%)", "extract");
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
                broadcastComplete(releaseName);
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
            if (tempDir.exists() && !isStopped) {
                File[] fileList = tempDir.listFiles();
                if (fileList != null) {
                    for (File f : fileList) f.delete();
                }
                tempDir.delete();
                debugLog.append("[CLEANUP] Temp folder removed\n");
            }
        }
    }
    
    private JSONArray listFiles(String hash, String baseUri) throws Exception {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        
        ProcessBuilder pb = new ProcessBuilder(rclonePath, "lsjson", "--config", "/dev/null",
                "--user-agent", "rclone/v1.68.2",
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

    private void broadcastProgress(String name, long current, long total, long startTime, String statusMsg, String phase) {
        if (isStopped) return;
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("name", name);
        intent.putExtra("current", current);
        intent.putExtra("total", total);
        intent.putExtra("statusMsg", statusMsg);
        intent.putExtra("phase", phase);
        
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            long speed = (current * 1000) / elapsed; // bytes per second
            intent.putExtra("speed", speed);
        }
        
        sendBroadcast(intent);
    }

    private void broadcastError(String msg) {
        if (isStopped) return;
        Log.e(TAG, "Broadcasting error: " + msg);
        Intent intent = new Intent("com.trd.apk.DOWNLOAD_PROGRESS");
        intent.putExtra("error", msg);
        sendBroadcast(intent);
    }
    
    private void broadcastComplete(String releaseName) {
        if (isStopped) return;
        Log.i(TAG, "Broadcasting completion for: " + releaseName);
        Intent intent = new Intent("com.trd.apk.DOWNLOAD_PROGRESS");
        intent.putExtra("complete", true);
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

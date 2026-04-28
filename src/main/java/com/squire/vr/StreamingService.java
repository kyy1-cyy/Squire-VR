package com.squire.vr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static volatile StreamingService INSTANCE;
    private volatile Process rcloneProcess;
    private volatile Process p7zipProcess;
    private volatile boolean isStopped = false;
    private volatile boolean isPaused = false;
    private boolean isSuccess = false;

    // POSIX signal numbers — Android's android.os.Process.sendSignal accepts
    // raw ints, and signal numbers are stable across Linux/Android.
    private static final int SIGSTOP = 19;
    private static final int SIGCONT = 18;

    /**
     * Suspend the in-flight rclone download. The process is frozen by the
     * kernel — every open HTTP connection, every byte already buffered, and
     * the *.partial scratch files all stay exactly as they are. Returns true
     * if a download was actually paused.
     */
    public static boolean pauseDownload() {
        StreamingService svc = INSTANCE;
        if (svc == null) return false;
        Process p = svc.rcloneProcess;
        if (p == null) return false;
        int pid = svc.getProcessPid(p);
        if (pid <= 0) return false;
        try {
            android.os.Process.sendSignal(pid, SIGSTOP);
            svc.isPaused = true;
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "pauseDownload: failed to send SIGSTOP", t);
            return false;
        }
    }

    /**
     * Continue a previously paused rclone download. Resets speed-smoothing
     * state so the first reading after the pause isn't averaged across the
     * entire pause duration. Returns true if a paused download was resumed.
     */
    public static boolean resumeDownload() {
        StreamingService svc = INSTANCE;
        if (svc == null) return false;
        Process p = svc.rcloneProcess;
        if (p == null) return false;
        if (!svc.isPaused) return false;
        int pid = svc.getProcessPid(p);
        if (pid <= 0) return false;
        try {
            android.os.Process.sendSignal(pid, SIGCONT);
            svc.isPaused = false;
            // Recalibrate the speed/eta sliding window so the first byte
            // delta after resume isn't divided by the whole pause duration.
            svc.lastProgressUpdate = 0;
            svc.lastSmoothedSpeed = 0;
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "resumeDownload: failed to send SIGCONT", t);
            return false;
        }
    }

    public static boolean isDownloadActive() {
        StreamingService svc = INSTANCE;
        return svc != null && svc.rcloneProcess != null;
    }

    private int getProcessPid(Process p) {
        if (p == null) return -1;
        try {
            java.lang.reflect.Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            return f.getInt(p);
        } catch (Throwable ignored) {}
        // Fallback: parse Process.toString(), which on Android is
        // "Process[pid=12345]".
        try {
            String s = p.toString();
            int i = s.indexOf("pid=");
            if (i >= 0) {
                int j = i + 4;
                int end = j;
                while (end < s.length() && Character.isDigit(s.charAt(end))) end++;
                if (end > j) return Integer.parseInt(s.substring(j, end));
            }
        } catch (Throwable ignored) {}
        return -1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        INSTANCE = this;
        String releaseName = intent.getStringExtra("releaseName");
        String rcloneUrl = intent.getStringExtra("rcloneUrl");
        String baseUri = intent.getStringExtra("baseUri");
        String savePath = intent.getStringExtra("savePath");
        long totalBytes = intent.getLongExtra("totalBytes", 0);
        String password = intent.getStringExtra("password");
        if (password == null) password = Config.DEFAULT_PASSWORD; // Fallback
        
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
        debugLog.append("Mode: Rclone Copy (Direct Download)\n");

        File tempDir = new File(savePath, "temp_archives");
        try {
            if (isStopped) return;
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new Exception("Failed to create temp directory at " + tempDir.getAbsolutePath());
            }

            // Use rclone copy like idel CLI - simpler and more reliable
            // Source is :http:/ + hash (MD5 of releaseName + "\n")
            String source = ":http:/" + rcloneUrl;
            
            List<String> cmd = new ArrayList<>();
            cmd.add(rclonePath);
            cmd.add("copy");
            cmd.add(source);
            cmd.add(tempDir.getAbsolutePath());
            cmd.add("--config");
            cmd.add("/dev/null");
            cmd.add("--user-agent");
            cmd.add("rclone/v1.72.1");
            cmd.add("--http-url");
            cmd.add(baseUri);
            cmd.add("--no-check-certificate");
            cmd.add("--stats");
            cmd.add("1s");
            cmd.add("--stats-log-level");
            cmd.add("NOTICE");
            cmd.add("--use-json-log");
            cmd.add("--transfers");
            cmd.add("4");
            cmd.add("--multi-thread-streams");
            cmd.add("4");
            cmd.add("--low-level-retries");
            cmd.add("10");
            cmd.add("--retries");
            cmd.add("5");

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.environment().put("LD_LIBRARY_PATH", libPath);

            // Snapshot what's already on disk before this rclone session.
            //   completedAtStart  = bytes in fully-downloaded archive parts
            //   partialAtStart    = bytes sitting in *.partial scratch files
            // On a fresh download both are 0. On a Pause -> Resume the sum is
            // the percentage we should pick up from. We use the sum as a
            // monotonic floor so the bar can never regress, even if rclone
            // wipes its scratch files and re-downloads them this session.
            final long completedAtStart = calculateCompletedBytes(tempDir);
            final long partialAtStart = calculatePartialBytes(tempDir);
            final long resumeBaseline = completedAtStart + partialAtStart;
            final long sessionStartTime = System.currentTimeMillis();

            // Push an initial progress event so the UI immediately reflects
            // existing bytes when resuming, instead of hovering at 0% until
            // rclone emits its first stats line.
            if (resumeBaseline > 0 && estimatedTotalBytes > 0) {
                long initialTotal = Math.max(estimatedTotalBytes, resumeBaseline);
                broadcastProgress(releaseName, resumeBaseline, initialTotal, sessionStartTime, resumeBaseline, "Resuming...", "download");
            }

            rcloneProcess = pb.start();

            // Compute progress as (completed-files-on-disk) + (bytes rclone
            // has transferred this session). rclone's stats.bytes ticks up in
            // real time as it writes into .partial scratch files, so the bar
            // moves smoothly from byte 1 on a fresh download. On resume we
            // also clamp to resumeBaseline so the bar can't regress while
            // rclone is re-downloading bytes it lost.
            BufferedReader reader = new BufferedReader(new InputStreamReader(rcloneProcess.getErrorStream()));
            String line;
            long totalBytes = estimatedTotalBytes > 0 ? estimatedTotalBytes : 0;
            long lastReportedCurrent = resumeBaseline;

            while ((line = reader.readLine()) != null) {
                if (isStopped) break;

                if (line.trim().startsWith("{")) {
                    try {
                        JSONObject json = new JSONObject(line);
                        if (json.has("stats")) {
                            JSONObject stats = json.getJSONObject("stats");
                            long sessionBytes = stats.optLong("bytes", 0L);
                            long sessionTotalRemaining = stats.optLong("totalBytes", 0L);

                            long completedNow = calculateCompletedBytes(tempDir);
                            long candidate = completedNow + Math.max(0L, sessionBytes);

                            // Monotonic floor — bar never goes backward.
                            long current = Math.max(lastReportedCurrent, candidate);
                            lastReportedCurrent = current;

                            long total = totalBytes;
                            long projected = completedNow + Math.max(sessionTotalRemaining, sessionBytes);
                            if (projected > total) total = projected;
                            if (current > total) total = current;

                            broadcastProgress(releaseName, current, total, sessionStartTime, current, "Downloading...", "download");
                        }
                    } catch (Exception e) {
                        // Ignore JSON parse errors
                    }
                }
            }

            if (isStopped) return;
            int exit = rcloneProcess.waitFor();
            if (exit != 0) {
                throw new Exception("Rclone copy failed with exit code: " + exit);
            }

            if (isStopped) return;

            // 3. Extract Files from Disk
            debugLog.append("[EXTRACT] Starting 7z extraction from disk\n");
            
            // Find first archive file in temp directory
            File[] archiveFiles = tempDir.listFiles((dir, name) -> 
                name.matches(".*\\.\\d{3}$") || 
                name.endsWith(".7z") || 
                name.endsWith(".zip") ||
                name.endsWith(".rar") ||
                name.endsWith(".obvr")
            );
            
            if (archiveFiles == null || archiveFiles.length == 0) {
                throw new Exception("No archive files found after download");
            }
            
            // Sort and pick first
            java.util.Arrays.sort(archiveFiles);
            File firstFilePath = archiveFiles[0];
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
                            String extractLine = new String(lineBuf, 0, pos);
                            pos = 0;
                            // Extract percentage using a robust search
                            int pctIdx = extractLine.indexOf('%');
                            if (pctIdx > 0) {
                                int start = pctIdx - 1;
                                while (start >= 0 && Character.isDigit(extractLine.charAt(start))) start--;
                                String numStr = extractLine.substring(start + 1, pctIdx).trim();
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
                try (java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(p7zipProcess.getErrorStream()))) {
                    String errLine;
                    while ((errLine = errorReader.readLine()) != null) p7zError.append(errLine).append("\n");
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

    // Sum of fully-completed archive parts in a download directory tree.
    // Skips rclone's *.partial scratch files because those represent an
    // in-flight transfer whose byte count is tracked separately.
    private long calculateCompletedBytes(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return 0L;
        long size = 0L;
        File[] files = dir.listFiles();
        if (files == null) return 0L;
        for (File f : files) {
            if (f == null) continue;
            if (f.isDirectory()) {
                size += calculateCompletedBytes(f);
            } else if (f.isFile()) {
                String name = f.getName();
                if (name.endsWith(".partial") || name.endsWith(".partial~")) continue;
                size += f.length();
            }
        }
        return size;
    }

    // Sum of bytes sitting in rclone's *.partial scratch files. Used to
    // establish a "you were here" floor on resume so the progress bar
    // doesn't drop back to 0% if you paused mid-archive part.
    private long calculatePartialBytes(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return 0L;
        long size = 0L;
        File[] files = dir.listFiles();
        if (files == null) return 0L;
        for (File f : files) {
            if (f == null) continue;
            if (f.isDirectory()) {
                size += calculatePartialBytes(f);
            } else if (f.isFile()) {
                String name = f.getName();
                if (name.endsWith(".partial") || name.endsWith(".partial~")) {
                    size += f.length();
                }
            }
        }
        return size;
    }
    
    private JSONArray listFiles(String hash, String baseUri) throws Exception {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        
        // Ensure binary is executable
        new File(rclonePath).setExecutable(true);
        
        ProcessBuilder pb = new ProcessBuilder(rclonePath, "lsjson", "--config", "/dev/null",
                "--user-agent", "rclone/v1.72.1",
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
    private long lastProgressUpdate = 0;
    private long lastProgressBytes = 0;
    private String lastPhase = "";

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
        
        // Reset tracking when phase changes (e.g., download -> extract)
        if (!phase.equals(lastPhase)) {
            lastPhase = phase;
            lastProgressUpdate = 0;
            lastProgressBytes = 0;
            lastSmoothedSpeed = 0;
        }
        
        // Calculate speed using sliding window (last update) for smooth resume
        long now = System.currentTimeMillis();
        if (lastProgressUpdate == 0) {
            // First update - initialize
            lastProgressUpdate = now;
            lastProgressBytes = current;
            intent.putExtra("speed", -1L); // Calculating...
        } else {
            long timeDelta = now - lastProgressUpdate;
            if (timeDelta >= 500) { // Update every 500ms minimum
                long bytesDelta = current - lastProgressBytes;
                if (bytesDelta < 0) bytesDelta = 0; // Handle resume case where current might be higher
                
                long instantSpeed = (bytesDelta * 1000) / timeDelta; // bytes per second
                
                // Smooth the speed with exponential moving average
                if (lastSmoothedSpeed == 0) {
                    lastSmoothedSpeed = instantSpeed;
                } else {
                    // 85% old, 15% new for very smooth transitions
                    lastSmoothedSpeed = (long) (lastSmoothedSpeed * 0.85 + instantSpeed * 0.15);
                }
                
                // Update tracking variables
                lastProgressUpdate = now;
                lastProgressBytes = current;
                
                intent.putExtra("speed", lastSmoothedSpeed);
            } else {
                // Not enough time passed, use last known speed
                intent.putExtra("speed", lastSmoothedSpeed > 0 ? lastSmoothedSpeed : -1L);
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
        // If rclone is currently SIGSTOP'd we still need it to die. Process.destroy()
        // sends SIGKILL on Android, which bypasses SIGSTOP, so this is safe even
        // for a paused process — but bump SIGCONT first to be belt-and-braces.
        if (rcloneProcess != null) {
            try {
                int pid = getProcessPid(rcloneProcess);
                if (pid > 0 && isPaused) android.os.Process.sendSignal(pid, SIGCONT);
            } catch (Throwable ignored) {}
            rcloneProcess.destroy();
            rcloneProcess = null;
        }
        if (p7zipProcess != null) {
            p7zipProcess.destroy();
            p7zipProcess = null;
        }
        isPaused = false;
        if (INSTANCE == this) INSTANCE = null;
        super.onDestroy();
    }
}
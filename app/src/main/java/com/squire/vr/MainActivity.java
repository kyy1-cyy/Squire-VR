package com.squire.vr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private ProgressBar loader;
    private TextView statusText;
    private SwitchCompat autoSideloadSwitch;
    private SharedPreferences prefs;
    private BroadcastReceiver installResultReceiver;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.game_list);
        loader = findViewById(R.id.loader);
        statusText = findViewById(R.id.status_text);
        autoSideloadSwitch = findViewById(R.id.auto_sideload_switch);
        
        prefs = getSharedPreferences("squire_prefs", MODE_PRIVATE);
        autoSideloadSwitch.setChecked(prefs.getBoolean("auto_sideload", true));
        autoSideloadSwitch.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean("auto_sideload", checked).apply();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Listen for final install results (v3.4)
        installResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (InstallReceiver.ACTION_INSTALL_RESULT.equals(intent.getAction())) {
                    boolean success = intent.getBooleanExtra("success", false);
                    boolean obbMoved = intent.getBooleanExtra("obbMoved", false);
                    String obbPath = intent.getStringExtra("obbPath");
                    String pkgName = intent.getStringExtra("pkgName");
                    
                    StringBuilder summary = new StringBuilder();
                    if (success) {
                        summary.append("✅ APK Installed: ").append(pkgName).append("\n\n");
                        if (obbMoved) {
                            summary.append("✅ OBB Moved to:\n").append(obbPath);
                        } else {
                            summary.append("⚠️ OBB: Not Found or skipped.");
                        }
                    } else {
                        summary.append("❌ Installation Failed or Aborted.");
                    }

                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Sideload Summary")
                        .setMessage(summary.toString())
                        .setPositiveButton("Awesome", null)
                        .show();
                }
            }
        };
        registerReceiver(installResultReceiver, new android.content.IntentFilter(InstallReceiver.ACTION_INSTALL_RESULT), Context.RECEIVER_EXPORTED);

        startAppFlow();
    }

    private void startAppFlow() {
        if (checkAndRequestPermissions()) {
            new Thread(() -> {
                verifyBinaries();
                runOnUiThread(this::fetchGames);
            }).start();
        }
    }

    private android.content.BroadcastReceiver downloadReceiver;
    private android.app.ProgressDialog currentDialog;

    @Override
    protected void onResume() {
        super.onResume();
        // Register receiver for download updates
        if (downloadReceiver == null) {
            downloadReceiver = new android.content.BroadcastReceiver() {
                @Override
                public void onReceive(android.content.Context context, Intent intent) {
                    if (currentDialog != null && currentDialog.isShowing()) {
                        if (intent.hasExtra("error")) {
                            currentDialog.dismiss();
                            String err = intent.getStringExtra("error");
                            showError("Download Failed: " + err);
                            return;
                        }

                        String statusMsg = intent.getStringExtra("statusMsg");
                        String phase = intent.getStringExtra("phase");
                        if (statusMsg == null) statusMsg = "Processing...";
                        if (phase == null) phase = "download";

                        if (intent.hasExtra("complete")) {
                            if (currentDialog != null && currentDialog.isShowing()) {
                                if (autoSideloadSwitch.isChecked()) {
                                    String finalSavePath = intent.getStringExtra("savePath");
                                    if (finalSavePath == null) finalSavePath = "/sdcard/Download/" + intent.getStringExtra("name");
                                    
                                    currentDialog.setTitle("Auto-Sideloading...");
                                    currentDialog.setIndeterminate(true);
                                    
                                    AutoInstaller.processExtraction(MainActivity.this, new File(finalSavePath), new AutoInstaller.InstallCallback() {
                                        @Override
                                        public void onStatus(String msg) {
                                            runOnUiThread(() -> {
                                                currentDialog.setMessage(msg);
                                                // Keep it visible while they handle the quest prompt
                                                if (msg.contains("Check Quest Prompt")) {
                                                    currentDialog.setTitle("Squire: Awaiting Install...");
                                                    currentDialog.setIndeterminate(true);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onDone(boolean obbMoved, String obbPath) {
                                            runOnUiThread(() -> {
                                                // We DON'T dismiss here anymore, or we dismiss and wait for the Broadcast
                                                currentDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Sideload sequence staged.", Toast.LENGTH_SHORT).show();
                                            });
                                        }

                                        @Override
                                        public void onError(String msg) {
                                            runOnUiThread(() -> {
                                                currentDialog.dismiss();
                                                showError("Auto-Sideload Error: " + msg);
                                            });
                                        }
                                    });
                                } else {
                                    currentDialog.dismiss();
                                    runOnUiThread(() -> {
                                        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Download Finished")
                                            .setMessage("Your game has been downloaded and extracted successfully!\n\nLocation: /sdcard/Download")
                                            .setPositiveButton("Close", null)
                                            .setCancelable(false)
                                            .show();
                                    });
                                }
                            }
                            return;
                        }

                        long current = intent.getLongExtra("current", 0);
                        long total = intent.getLongExtra("total", 100);
                        if (total <= 0) total = 100;

                        long speed = intent.getLongExtra("speed", 0); // bytes/sec
                        
                        // Update progress bar
                        int progress = (int) ((current * 100) / total);
                        currentDialog.setProgress(progress);
                        
                        // Format speed and status
                        String speedStr = "";
                        if (phase.equals("download")) {
                            double speedMb = speed / (1024.0 * 1024.0);
                            speedStr = String.format("\nSpeed: %.2f MB/s", speedMb);
                        }
                        
                        String etaStr = "";
                        if (speed > 0) {
                            long remainingBytes = total - current;
                            long secondsLeft = remainingBytes / speed;
                            long mins = secondsLeft / 60;
                            long secs = secondsLeft % 60;
                            etaStr = String.format("\nETA: %dm %ds", mins, secs);
                        }

                        if (phase.equals("download")) {
                            double currentMb = current / (1024.0 * 1024.0);
                            double totalMb = total / (1024.0 * 1024.0);
                            currentDialog.setMessage("Status: " + statusMsg + "\n" +
                                                   "Progress: " + String.format("%.2f", currentMb) + "MB / " + String.format("%.2f", totalMb) + "MB (" + progress + "%)" +
                                                   speedStr + etaStr);
                        } else {
                            currentDialog.setMessage("Status: " + statusMsg + etaStr);
                        }
                    }
                }
            };
            registerReceiver(downloadReceiver, new android.content.IntentFilter(Config.ACTION_PROGRESS), android.content.Context.RECEIVER_EXPORTED); // Needs explicit flag on Android 14+
        }

        // If we were waiting for permissions, check again
        if (recyclerView.getAdapter() == null && loader.getVisibility() == View.VISIBLE) {
             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (android.os.Environment.isExternalStorageManager()) {
                    startAppFlow();
                }
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        if (downloadReceiver != null) unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    private boolean checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                runOnUiThread(() -> {
                    statusText.setText("Permission required to install games.");
                    Toast.makeText(this, "Please allow 'All Files Access' to continue", Toast.LENGTH_LONG).show();
                });
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private void verifyBinaries() {
        // Binaries are now bundled as native libs - no extraction needed
        // Just verify they exist
        String rclonePath = getNativeLibPath("librclone.so");
        String p7zPath = getNativeLibPath("lib7z.so");
        
        File rclone = new File(rclonePath);
        File p7z = new File(p7zPath);
        
        if (!rclone.exists() || !p7z.exists()) {
            // Debug: List files in the directory to see what IS there
            File libDir = new File(getApplicationInfo().nativeLibraryDir);
            String[] files = libDir.list();
            StringBuilder sb = new StringBuilder();
            if (files != null) {
                for (String f : files) sb.append(f).append(", ");
            }
            
            showError("Native binaries missing! Found: " + sb.toString());
            return;
        }
        
        updateStatus("Binaries ready...");
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> statusText.setText(status));
    }

    private String getNativeLibPath(String libName) {
        return getApplicationInfo().nativeLibraryDir + "/" + libName;
    }

    private void fetchGames() {
        updateStatus("Fetching VRP Config...");
        new Thread(() -> {
            try {
                // 1. Fetch public config
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(Config.VRP_CONFIG_URL)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Config fetch failed: " + response.code());
                
                String json = response.body().string();
                VrpConfig config = new com.google.gson.Gson().fromJson(json, VrpConfig.class);
                
                if (config.baseUri == null || config.baseUri.isEmpty()) {
                     throw new IOException("Invalid config: missing baseUri");
                }
                
                // Decode Password (Base64)
                if (config.password != null) {
                    try {
                        byte[] decoded = android.util.Base64.decode(config.password, android.util.Base64.DEFAULT);
                        config.password = new String(decoded, "UTF-8");
                        Log.d("TRD", "Password decoded successfully.");
                    } catch (Exception e) {
                        Log.e("TRD", "Password decode failed", e);
                        config.password = Config.VRP_DEFAULT_PASSWORD; // Fallback to known default
                    }
                } else {
                    config.password = Config.VRP_DEFAULT_PASSWORD;
                }
                
                runOnUiThread(() -> {
                    updateStatus("Config loaded. Mirror: " + config.baseUri);
                    downloadMeta(config);
                });
                
            } catch (Exception e) {
                Log.e("TRD", "Config fetch failed, using fallback", e);
                runOnUiThread(() -> {
                     updateStatus("Config failed. Using fallback...");
                     VrpConfig config = new VrpConfig();
                     config.baseUri = Config.MIRROR_BASEURI_URL;
                     downloadMeta(config);
                });
            }
        }).start();
    }

    private void showError(String msg) {
        runOnUiThread(() -> {
            loader.setVisibility(View.GONE);
            statusText.setText("Error occurred (see dialog)");
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
        });
    }

    private void downloadMeta(VrpConfig config) {
        updateStatus("Syncing Meta from VRP...");
        File metaFile = new File(getFilesDir(), "meta.7z");
        
        // Ensure we always delete old meta to force fresh sync
        if (metaFile.exists()) metaFile.delete();
        
        String rclonePath = getNativeLibPath("librclone.so");
        
        // Execute rclone copy :http:/meta.7z meta.7z --http-url baseUri
        new Thread(() -> {
            try {
                String metaUrl = config.baseUri.endsWith("/") ? config.baseUri + "meta.7z" : config.baseUri + "/meta.7z";
                
                ProcessBuilder pb = new ProcessBuilder(
                    rclonePath, "copyurl", metaUrl, new File(getFilesDir(), "meta.7z").getAbsolutePath(),
                    "--config", "/dev/null",
                    "--no-check-certificate"
                );
                
                Process p = pb.start();
                
                // Read stderr
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) log.append(line).append("\n");
                
                int exitCode = p.waitFor();
                
                if (exitCode == 0 && new File(getFilesDir(), "meta.7z").exists()) {
                    extractMeta(config);
                } else {
                    showError("Sync failed (" + exitCode + "):\n" + log.toString());
                }
            } catch (Exception e) {
                showError("Sync error: " + e.getMessage());
            }
        }).start();
    }

    private void extractMeta(VrpConfig config) {
        updateStatus("Extracting game list...");
        File metaFile = new File(getFilesDir(), "meta.7z");
        File outDir = new File(getFilesDir(), ".meta");
        if (!outDir.exists()) outDir.mkdirs();
        
        String p7zPath = getNativeLibPath("lib7z.so");
        
        new Thread(() -> {
            try {
                // 7z x meta.7z -pgL59VfgPxoHR -o.meta -y
                ProcessBuilder pb = new ProcessBuilder(
                    p7zPath, "x", metaFile.getAbsolutePath(),
                    "-p" + Config.VRP_DEFAULT_PASSWORD,
                    "-o" + outDir.getAbsolutePath(),
                    "-y"
                );
                
                // CRITICAL: Tell linker where to find libiconv.so and others!
                pb.environment().put("LD_LIBRARY_PATH", getApplicationInfo().nativeLibraryDir);
                
                Process p = pb.start();
                
                // Read any errors
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    log.append(line).append("\n");
                }
                
                int exitCode = p.waitFor();
                
                File expectedFile = new File(outDir, "VRP-GameList.txt");
                
                // 7-Zip exit code 1 is just a warning, check if file exists
                if ((exitCode == 0 || exitCode == 1) && expectedFile.exists()) {
                    parseGameList(config);
                } else {
                    showError("7z failed (" + exitCode + "):\n" + log.toString());
                }
            } catch (Exception e) {
                showError("Extraction error: " + e.getMessage());
            }
        }).start();
    }

    private void startDownload(Game game) {
        currentDialog = new android.app.ProgressDialog(this);
        currentDialog.setTitle("Downloading: " + game.gameName);
        currentDialog.setMessage("Initializing stream...");
        currentDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        currentDialog.setIndeterminate(false);
        currentDialog.setMax(100);
        currentDialog.setCancelable(false);
        
        currentDialog.setButton(android.content.DialogInterface.BUTTON_NEGATIVE, "Cancel", (d, w) -> {
            stopService(new Intent(this, StreamingService.class));
            d.dismiss();
            Toast.makeText(this, "Download Cancelled", Toast.LENGTH_SHORT).show();
        });
        
        // Remove 0/100
        currentDialog.setProgressNumberFormat(null);
        currentDialog.show();
        
        long totalBytes = 0;
        try {
            // Robust size parsing for total bytes
            String cleanSize = game.sizeMb.trim().toUpperCase();
            double val = Double.parseDouble(cleanSize.replace(" GB", "").replace(" MB", "").trim());
            
            if (cleanSize.contains("GB")) {
                totalBytes = (long)(val * 1024 * 1024 * 1024);
            } else {
                totalBytes = (long)(val * 1024 * 1024);
            }
        } catch (Exception e) {
            totalBytes = 2 * 1024 * 1024 * 1024L; // Default to 2GB if unknown, prevents 0/100
        }

        // Storage Pre-Check (Need 2x totalBytes)
        long requiredSpace = totalBytes * 2;
        File path = android.os.Environment.getExternalStorageDirectory();
        android.os.StatFs stat = new android.os.StatFs(path.getPath());
        long availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();

        if (availableSpace < requiredSpace) {
            currentDialog.dismiss();
            double availableGb = availableSpace / (1024.0 * 1024.0 * 1024.0);
            double requiredGb = requiredSpace / (1024.0 * 1024.0 * 1024.0);
            showError(String.format("Insufficient Storage!\nAvailable: %.2f GB\nRequired (2x Game Size): %.2f GB\n\nPlease free up space and try again.", availableGb, requiredGb));
            return;
        }

        Intent intent = new Intent(this, StreamingService.class);
        intent.putExtra("releaseName", game.releaseName);
        intent.putExtra("rcloneUrl", game.md5Hash); 
        intent.putExtra("baseUri", game.baseUri);
        intent.putExtra("savePath", "/sdcard/Download/" + game.releaseName);
        intent.putExtra("totalBytes", totalBytes);
        intent.putExtra("password", game.password); // Pass the decoded password
        startService(intent);
    }

    private void parseGameList(VrpConfig config) {
        // Silent parsing
        File listFile = new File(getFilesDir(), ".meta/VRP-GameList.txt");
        if (!listFile.exists()) {
            listFile = new File(getFilesDir(), "VRP-GameList.txt");
        }

        try {
            java.util.Scanner scanner = new java.util.Scanner(listFile);
            List<Game> games = new ArrayList<>();
            if (scanner.hasNextLine()) scanner.nextLine(); // skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                // RS/server/index.js schema: 
                // 0: GameName, 1: ReleaseName, 2: PackageName, 3: Version, 4: Date, 5: Size, 6: Downloads
                if (parts.length >= 6) {
                    Game g = new Game();
                    g.gameName = parts[0].trim();
                    g.releaseName = parts[1]; // DO NOT trim, match RS server
                    g.packageName = parts[2].trim();
                    g.versionCode = parts[3].trim();
                     
                    // Parse MB size from Index 5
                    String rawSize = parts[5].trim();
                    g.sizeMb = rawSize;
                    
                    try {
                        String valStr = rawSize.split(" ")[0]; // "2048 MB" -> "2048"
                        double val = Double.parseDouble(valStr);
                        
                        if (rawSize.toUpperCase().contains("GB")) {
                             g.sizeMb = String.format("%.2f GB", val);
                        } else {
                             // If it's pure number, assume MB (server logic)
                             // But usually csv has " MB" suffix? 
                             // RS/server code just takes parts[5] as string
                             if (val > 1000) {
                                  g.sizeMb = String.format("%.2f GB", val / 1024.0);
                             } else {
                                  g.sizeMb = String.format("%.2f MB", val);
                             }
                        }
                    } catch (Exception e) {
                        // Keep raw string if parse fails
                    }
                    
                    // Generate Hash from ReleaseName (Index 1)
                    try {
                        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                        byte[] digest = md.digest((g.releaseName + "\n").getBytes("UTF-8"));
                        StringBuilder hexString = new StringBuilder();
                        for (byte b : digest) {
                            String hex = Integer.toHexString(0xff & b);
                            if (hex.length() == 1) hexString.append('0');
                            hexString.append(hex);
                        }
                        g.md5Hash = hexString.toString();
                    } catch (Exception e) {
                        g.md5Hash = g.releaseName; // Raw name
                    }

                    g.baseUri = config.baseUri;
                    g.password = config.password;
                    games.add(g);
                }
            }
            scanner.close();
            
            allGames = new ArrayList<>(games); // Keep full list for filtering

            runOnUiThread(() -> {
                loader.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 5));
                adapter = new GameAdapter(games, game -> startDownload(game));
                recyclerView.setAdapter(adapter);
                
                // Initialize Search
                androidx.appcompat.widget.SearchView searchView = findViewById(com.squire.vr.R.id.search_bar);
                if (searchView != null) {
                    searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) { return false; }
                        @Override
                        public boolean onQueryTextChange(String newText) {
                            filterGames(newText);
                            return true;
                        }
                    });
                }
            });

        } catch (Exception e) {
            showError("List error: " + e.getMessage());
        }
    }

    private List<Game> allGames;

    private void filterGames(String query) {
        if (allGames == null) return;
        List<Game> filtered = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Game g : allGames) {
            if (g.gameName.toLowerCase().contains(q)) {
                filtered.add(g);
            }
        }
        if (adapter != null) {
            adapter.updateList(filtered);
        }
    }
}

class VrpConfig {
    String baseUri;
    String password;
}

class Game {
    String gameName;
    String releaseName;
    String versionCode;
    String sizeMb;
    String packageName;
    String md5Hash;
    String baseUri;
    String password;
}

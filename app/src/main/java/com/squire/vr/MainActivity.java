package com.squire.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private ProgressBar loader;
    private TextView statusText;
    private SwitchCompat autoSideloadSwitch;
    private Button tabAll, tabInstalled, btnSort;
    private File thumbnailsDir;
    private View bgStatusBar;
    private int currentSortMode = 0; // 0: Default, 1: Last Updated, 2: Size
    private TextView bgStatusText;
    private Button bgStatusViewBtn;
    private java.util.Queue<Game> downloadQueue = new java.util.LinkedList<>();
    private Game currentDownloadingGame = null;
    private android.app.ProgressDialog currentDialog;
    private TextView queueNotification;
    private List<String> sessionCompletedGames = new ArrayList<>();
    private StringBuilder sessionSideloadResults = new StringBuilder();

    private boolean showInstalledOnly = false;
    private String currentQuery = "";
    private SharedPreferences prefs;
    private java.util.Map<String, String> installedPackagesCache = new java.util.HashMap<>();
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
        tabAll = findViewById(R.id.tab_all);
        tabInstalled = findViewById(R.id.tab_installed);
        btnSort = findViewById(R.id.btn_sort);
        
        tabAll.setOnClickListener(v -> {
            showInstalledOnly = false;
            updateTabUI();
            filterGames(currentQuery);
        });

        tabInstalled.setOnClickListener(v -> {
            showInstalledOnly = true;
            updateTabUI();
            filterGames(currentQuery);
        });
        
        btnSort.setOnClickListener(v -> showSortMenu());
        
        updateTabUI();
        
        prefs = getSharedPreferences("squire_prefs", MODE_PRIVATE);
        autoSideloadSwitch.setChecked(prefs.getBoolean("auto_sideload", true));
        autoSideloadSwitch.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean("auto_sideload", checked).apply();
        });

        setupRecyclerView();

        installResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (InstallReceiver.ACTION_INSTALL_RESULT.equals(action)) {
                    if (currentDialog != null && currentDialog.isShowing()) {
                        currentDialog.dismiss();
                    }

                    boolean success = intent.getBooleanExtra("success", false);
                    boolean obbMoved = intent.getBooleanExtra("obbMoved", false);
                    String obbPath = intent.getStringExtra("obbPath");
                    String pkgName = intent.getStringExtra("pkgName");
                    
                    if (success) {
                        sessionSideloadResults.append("✅ ").append(pkgName).append(" [OK]\n");
                        if (obbMoved) sessionSideloadResults.append("   - OBB Moved\n");
                    } else {
                        sessionSideloadResults.append("❌ ").append(pkgName).append(" [FAILED]\n");
                    }

                    if (downloadQueue.isEmpty()) {
                        refreshPackageCache(); // Refresh cache so Updates tab updates
                        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Games Done")
                            .setMessage("Organization: /sdcard/Download/Squire Vr Games/\n\n" + sessionSideloadResults.toString())
                            .setPositiveButton("Awesome", (d, w) -> {
                                sessionSideloadResults.setLength(0);
                                sessionCompletedGames.clear();
                            })
                            .show();
                    }
                    
                    processNextInQueue();
                } else if (InstallReceiver.ACTION_INSTALL_STATUS.equals(action)) {
                    String status = intent.getStringExtra("status");
                    if (currentDialog != null && currentDialog.isShowing()) {
                        currentDialog.setMessage(status);
                    }
                }
            }
        };
        queueNotification = findViewById(R.id.queue_notification);
        bgStatusBar = findViewById(R.id.bg_status_bar);
        bgStatusText = findViewById(R.id.bg_status_text);
        bgStatusViewBtn = findViewById(R.id.bg_status_view_btn);
        
        bgStatusViewBtn.setOnClickListener(v -> {
            if (currentDownloadingGame != null) {
                if (currentDialog == null || !currentDialog.isShowing()) {
                    showProgressDialog(currentDownloadingGame.gameName);
                }
                bgStatusBar.setVisibility(View.GONE);
            }
        });

        android.content.IntentFilter filter = new android.content.IntentFilter();
        filter.addAction(InstallReceiver.ACTION_INSTALL_RESULT);
        filter.addAction(InstallReceiver.ACTION_INSTALL_STATUS);
        registerReceiver(installResultReceiver, filter, Context.RECEIVER_EXPORTED);

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

        startAppFlow();
    }

    private void setupRecyclerView() {
        androidx.recyclerview.widget.GridLayoutManager glm = new androidx.recyclerview.widget.GridLayoutManager(this, 3);
        glm.setSpanSizeLookup(new androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter != null && adapter.getItemViewType(position) == 1) ? 3 : 1;
            }
        });
        recyclerView.setLayoutManager(glm);
        if (adapter == null) {
            adapter = new GameAdapter(new ArrayList<>(), game -> startDownload(game));
        }
        recyclerView.setAdapter(adapter);
    }

    private void startAppFlow() {
        if (checkAndRequestPermissions()) {
            new Thread(() -> {
                refreshPackageCache();
                loadCache(); 
                verifyBinaries();
                runOnUiThread(this::fetchGames);
            }).start();
        }
    }

    private void refreshPackageCache() {
        java.util.Map<String, String> newCache = new java.util.HashMap<>();
        PackageManager pm = getPackageManager();
        List<android.content.pm.PackageInfo> packages = pm.getInstalledPackages(0);
        for (android.content.pm.PackageInfo pi : packages) {
            newCache.put(pi.packageName, pi.versionName);
        }
        installedPackagesCache = newCache;
        runOnUiThread(() -> {
            if (allGames != null) filterGames(currentQuery);
        });
    }

    private android.content.BroadcastReceiver downloadReceiver;

    @Override
    protected void onResume() {

        super.onResume();
        new Thread(this::refreshPackageCache).start();
        if (downloadReceiver == null) {
            downloadReceiver = new android.content.BroadcastReceiver() {
                @Override
                public void onReceive(android.content.Context context, Intent intent) {
                    if (intent.hasExtra("error")) {
                        if (currentDialog != null) currentDialog.dismiss();
                        String err = intent.getStringExtra("error");
                        showError("Download Failed: " + err);
                        return;
                    }

                    String statusMsg = intent.getStringExtra("statusMsg");
                    String phase = intent.getStringExtra("phase");
                    if (statusMsg == null) statusMsg = "Processing...";
                    if (phase == null) phase = "download";

                    if (bgStatusBar.getVisibility() == View.VISIBLE && currentDownloadingGame != null) {
                        int pct = intent.getIntExtra("progress_percent", 0);
                        int queueSize = downloadQueue.size();
                        String queueSuffix = queueSize > 0 ? " (" + queueSize + ")" : "";
                        bgStatusText.setText("Downloading " + currentDownloadingGame.gameName + ": " + pct + "%" + queueSuffix);
                    }

                    if (intent.hasExtra("complete")) {
                        String name = intent.getStringExtra("name");
                        if (name != null) sessionCompletedGames.add(name);

                        if (currentDialog != null && currentDialog.isShowing()) {
                            if (!autoSideloadSwitch.isChecked()) {
                                currentDialog.dismiss();
                            }
                        }

                        if (autoSideloadSwitch.isChecked()) {
                             String finalSavePath = intent.getStringExtra("savePath");
                             if (finalSavePath == null) finalSavePath = "/sdcard/Download/Squire Vr Games/" + intent.getStringExtra("name");
                             
                             if (!currentDialog.isShowing() && bgStatusBar.getVisibility() == View.GONE) {
                                 currentDialog.show();
                             }
                             currentDialog.setTitle("Auto-Sideloading...");
                             currentDialog.setIndeterminate(true);
                             
                             AutoInstaller.processExtraction(MainActivity.this, new File(finalSavePath), new AutoInstaller.InstallCallback() {
                                @Override
                                public void onStatus(String msg) {
                                    runOnUiThread(() -> {
                                        currentDialog.setMessage(msg);
                                        if (msg.contains("Check Quest Prompt")) {
                                            currentDialog.setTitle("Squire: Awaiting Install...");
                                        }
                                    });
                                }
                                @Override
                                public void onDone(boolean obbMoved, String obbPath) {}
                                @Override
                                public void onError(String msg) {
                                    runOnUiThread(() -> {
                                        if (currentDialog != null) currentDialog.dismiss();
                                        sessionSideloadResults.append("❌ ").append(name).append(" (Extraction Failed)\n");
                                        new Thread(() -> {
                                            File gameDir = new File("/sdcard/Download/Squire Vr Games/" + name);
                                            if (gameDir.exists()) deleteRecursive(gameDir);
                                        }).start();
                                        processNextInQueue();
                                    });
                                }
                            });
                        } else {
                            boolean finishedAll = downloadQueue.isEmpty();
                            processNextInQueue();
                            if (finishedAll) {
                                showFinalSummary();
                            }
                        }
                        return;
                    }

                    if (currentDialog != null && currentDialog.isShowing()) {
                        bgStatusBar.setVisibility(View.GONE);
                        long current = intent.getLongExtra("current", 0);
                        long total = intent.getLongExtra("total", 100);
                        if (total <= 0) total = 100;
                        long speed = intent.getLongExtra("speed", 0); 
                        
                        int progress = (int) ((current * 100) / total);
                        currentDialog.setProgress(progress);
                        
                        String speedStr = "";
                        if ("download".equals(phase)) {
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

                        if ("download".equals(phase)) {
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
            registerReceiver(downloadReceiver, new android.content.IntentFilter(Config.ACTION_PROGRESS), android.content.Context.RECEIVER_EXPORTED);
        }

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
        if (installResultReceiver != null) unregisterReceiver(installResultReceiver);
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
        String rclonePath = getNativeLibPath("librclone.so");
        String p7zPath = getNativeLibPath("lib7z.so");
        File rclone = new File(rclonePath);
        File p7z = new File(p7zPath);
        
        if (!rclone.exists() || !p7z.exists()) {
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
                
                if (config.password != null) {
                    try {
                        byte[] decoded = android.util.Base64.decode(config.password, android.util.Base64.DEFAULT);
                        config.password = new String(decoded, "UTF-8");
                    } catch (Exception e) {
                        config.password = Config.VRP_DEFAULT_PASSWORD;
                    }
                } else {
                    config.password = Config.VRP_DEFAULT_PASSWORD;
                }
                
                runOnUiThread(() -> {
                    updateStatus("Config loaded. Mirror: " + config.baseUri);
                    downloadMeta(config);
                });
                
            } catch (Exception e) {
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
        if (metaFile.exists()) metaFile.delete();
        
        String rclonePath = getNativeLibPath("librclone.so");
        
        new Thread(() -> {
            try {
                String metaUrl = config.baseUri.endsWith("/") ? config.baseUri + "meta.7z" : config.baseUri + "/meta.7z";
                
                ProcessBuilder pb = new ProcessBuilder(
                    rclonePath, "copyurl", metaUrl, new File(getFilesDir(), "meta.7z").getAbsolutePath(),
                    "--config", "/dev/null",
                    "--no-check-certificate"
                );
                
                Process p = pb.start();
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
                ProcessBuilder pb = new ProcessBuilder(
                    p7zPath, "x", metaFile.getAbsolutePath(),
                    "-p" + Config.VRP_DEFAULT_PASSWORD,
                    "-o" + outDir.getAbsolutePath(),
                    "-y"
                );
                pb.environment().put("LD_LIBRARY_PATH", getApplicationInfo().nativeLibraryDir);
                Process p = pb.start();
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) log.append(line).append("\n");
                
                int exitCode = p.waitFor();
                File expectedFile = new File(outDir, "VRP-GameList.txt");
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

    private void showProgressDialog(String title) {
        if (currentDialog != null && currentDialog.isShowing()) return;

        currentDialog = new android.app.ProgressDialog(this);
        currentDialog.setTitle("Downloading: " + title);
        currentDialog.setMessage("Initializing stream...");
        currentDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        currentDialog.setIndeterminate(false);
        currentDialog.setMax(100);
        currentDialog.setCancelable(false);
        
        currentDialog.setButton(android.app.ProgressDialog.BUTTON_NEGATIVE, "Background", (d, w) -> {
            bgStatusBar.setVisibility(View.VISIBLE);
            d.dismiss();
        });

        currentDialog.setButton(android.app.ProgressDialog.BUTTON_POSITIVE, "Cancel", (d, w) -> {
            stopService(new Intent(this, StreamingService.class));
            if (currentDownloadingGame != null) {
                String releaseName = currentDownloadingGame.releaseName;
                new Thread(() -> {
                    File gameDir = new File("/sdcard/Download/Squire Vr Games/" + releaseName);
                    if (gameDir.exists()) {
                        Log.d("SquireCleanup", "Deleting canceled download folder: " + gameDir.getAbsolutePath());
                        deleteRecursive(gameDir);
                    }
                }).start();
            }
            d.dismiss();
            processNextInQueue();
        });
        
        currentDialog.show();
    }

    private void startDownload(Game game) {
        if (currentDownloadingGame != null) {
            downloadQueue.add(game);
            runOnUiThread(() -> {
                showQueueNotification(game.gameName);
                if (bgStatusBar.getVisibility() == View.VISIBLE) {
                    int pct = 0; 
                    int queueSize = downloadQueue.size();
                    bgStatusText.setText("Downloading " + currentDownloadingGame.gameName + ": " + pct + "% (" + queueSize + ")");
                }
            });
            return;
        }

        currentDownloadingGame = game;
        showProgressDialog(game.gameName);
        
        long totalBytes = 0;
        try {
            String cleanSize = game.sizeMb.trim().toUpperCase();
            double val = Double.parseDouble(cleanSize.replace(" GB", "").replace(" MB", "").trim());
            if (cleanSize.contains("GB")) totalBytes = (long)(val * 1024 * 1024 * 1024);
            else totalBytes = (long)(val * 1024 * 1024);
        } catch (Exception e) {
            totalBytes = 2 * 1024 * 1024 * 1024L;
        }

        long requiredSpace = totalBytes * 2;
        File path = android.os.Environment.getExternalStorageDirectory();
        android.os.StatFs stat = new android.os.StatFs(path.getPath());
        long availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();

        if (availableSpace < requiredSpace) {
            if (currentDialog != null) currentDialog.dismiss();
            double availableGb = availableSpace / (1024.0 * 1024.0 * 1024.0);
            double requiredGb = requiredSpace / (1024.0 * 1024.0 * 1024.0);
            showError(String.format("Insufficient Storage!\nAvailable: %.2f GB\nRequired (2x Game Size): %.2f GB\n\nPlease free up space and try again.", availableGb, requiredGb));
            processNextInQueue();
            return;
        }

        Intent intent = new Intent(this, StreamingService.class);
        intent.putExtra("releaseName", game.releaseName);
        intent.putExtra("rcloneUrl", game.md5Hash); 
        intent.putExtra("baseUri", game.baseUri);
        intent.putExtra("savePath", "/sdcard/Download/Squire Vr Games/" + game.releaseName);
        intent.putExtra("totalBytes", totalBytes);
        intent.putExtra("password", game.password);
        startService(intent);
    }

    private void showQueueNotification(String gameName) {
        if (queueNotification == null) return;
        
        queueNotification.setText(gameName.toUpperCase() + " ADDED TO QUEUE");
        queueNotification.setVisibility(View.VISIBLE);
        queueNotification.setAlpha(0f);
        
        queueNotification.animate()
            .alpha(1f)
            .setDuration(400)
            .withEndAction(() -> {
                new android.os.Handler().postDelayed(() -> {
                    if (queueNotification != null) {
                        queueNotification.animate()
                            .alpha(0f)
                            .setDuration(600)
                            .withEndAction(() -> queueNotification.setVisibility(View.GONE))
                            .start();
                    }
                }, 5000);
            })
            .start();
    }

    private void processNextInQueue() {
        currentDownloadingGame = null;
        runOnUiThread(() -> bgStatusBar.setVisibility(View.GONE));
        if (!downloadQueue.isEmpty()) {
            final Game next = downloadQueue.poll();
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                startDownload(next);
            }, 1000);
        }
    }

    private void showFinalSummary() {
        if (sessionCompletedGames.isEmpty()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("✅ All downloads finished!\n\n");
        sb.append("Location: /sdcard/Download/Squire Vr Games/\n\n");
        sb.append("Games:\n");
        for (String g : sessionCompletedGames) {
            sb.append("- ").append(g).append("\n");
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Games Done")
            .setMessage(sb.toString())
            .setPositiveButton("Awesome", (d, w) -> sessionCompletedGames.clear())
            .show();
    }

    private void parseGameList(VrpConfig config) {
        File listFile = new File(getFilesDir(), ".meta/VRP-GameList.txt");
        if (!listFile.exists()) listFile = new File(getFilesDir(), "VRP-GameList.txt");

        try (java.util.Scanner scanner = new java.util.Scanner(listFile)) {
            List<Game> games = new ArrayList<>();
            if (scanner.hasNextLine()) scanner.nextLine(); 

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                if (parts.length >= 6) {
                    Game g = new Game();
                    g.gameName = parts[0].trim();
                    g.releaseName = parts[1];
                    g.packageName = parts[2].trim();
                    g.version = parts[3].trim();
                    g.date = parts[4].trim(); 
                    
                    String rawSize = parts[5].trim();
                    g.sizeMb = rawSize;
                    try {
                        String valStr = rawSize.split(" ")[0];
                        double val = Double.parseDouble(valStr);
                        if (rawSize.toUpperCase().contains("GB")) g.sizeMb = String.format("%.2f GB", val);
                        else if (val > 1000) g.sizeMb = String.format("%.2f GB", val / 1024.0);
                        else g.sizeMb = String.format("%.2f MB", val);
                    } catch (Exception e) {}
                    
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
                        g.md5Hash = g.releaseName;
                    }

                    g.baseUri = config.baseUri;
                    g.password = config.password;
                    games.add(g);
                }
            }
            
            if (allGames == null) {
                allGames = new ArrayList<>(games);
            } else {
                java.util.Map<String, Game> gameMap = new java.util.HashMap<>();
                for (Game existing : allGames) gameMap.put(existing.packageName, existing);
                
                List<Game> merged = new ArrayList<>();
                for (Game serverGame : games) {
                    Game existing = gameMap.get(serverGame.packageName);
                    if (existing != null) {
                        existing.gameName = serverGame.gameName;
                        existing.releaseName = serverGame.releaseName;
                        if (!serverGame.version.equals(existing.version)) {
                            existing.version = serverGame.version;
                            existing.thumbnailPath = null; 
                            existing.noteContent = null;
                        }
                        existing.sizeMb = serverGame.sizeMb;
                        existing.date = serverGame.date;
                        merged.add(existing);
                    } else {
                        merged.add(serverGame);
                    }
                }
                allGames = merged;
            }

            new Thread(() -> {
                try {
                    File metaDir = new File(getFilesDir(), ".meta");
                    File thumbnails = findFolder(metaDir, "Thumbnails");
                    if (thumbnails == null) thumbnails = findFolder(metaDir, "thumbnails");
                    File notes = findFolder(metaDir, "notes");
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

                    for (int i = 0; i < allGames.size(); i++) {
                        Game g = allGames.get(i);
                        boolean needsScan = (g.thumbnailPath == null || g.noteContent == null);
                        
                        if (needsScan) {
                            if (thumbnails != null && thumbnails.exists()) {
                                File jpg = new File(thumbnails, g.packageName + ".jpg");
                                if (jpg.exists()) g.thumbnailPath = jpg.getAbsolutePath();
                                else {
                                    File png = new File(thumbnails, g.packageName + ".png");
                                    if (png.exists()) g.thumbnailPath = png.getAbsolutePath();
                                }
                            }
                            if (notes != null && notes.exists()) {
                                File noteFile = new File(notes, g.releaseName + ".txt");
                                if (noteFile.exists()) g.noteContent = readNoteContent(noteFile);
                            }
                        }

                        File gameFolder = findFolder(metaDir, g.releaseName);
                        if (gameFolder != null && gameFolder.exists()) {
                             long maxMtime = 0;
                             File[] metaFiles = gameFolder.listFiles();
                             if (metaFiles != null) {
                                 for (File f : metaFiles) if (f.lastModified() > maxMtime) maxMtime = f.lastModified();
                             }
                             if (maxMtime > 0) g.date = sdf.format(new java.util.Date(maxMtime));
                        }
                        
                        if (i % 50 == 0 || i == allGames.size() - 1) { 
                            runOnUiThread(() -> { if (adapter != null) adapter.notifyDataSetChanged(); });
                        }
                    }
                    saveCache();
                } catch (Throwable e) {}
            }).start();

            runOnUiThread(() -> {
                loader.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                
                if (adapter == null) setupRecyclerView();
                filterGames(currentQuery); 
            });
        } catch (Exception e) {
            showError("List error: " + e.getMessage());
        }
    }

    private List<Game> allGames;

    private void updateTabUI() {
        tabAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF333333));
        tabInstalled.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF333333));
        

        
        if (showInstalledOnly) tabInstalled.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF007AFF));
        else tabAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF007AFF));
    }

    private void filterGames(String query) {
        currentQuery = query;
        if (allGames == null) return;
        List<Game> filtered = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Game g : allGames) {
            boolean matchesSearch = g.gameName.toLowerCase().contains(q);
            boolean matchesFilter = true;
            if (showInstalledOnly) matchesFilter = isPackageInstalled(g.packageName);
            if (matchesSearch && matchesFilter) filtered.add(g);
        }
        
        // Apply Sort
        if (currentSortMode == 1) { // Last Updated
            java.util.Collections.sort(filtered, (g1, g2) -> {
                String d1 = g1.date != null ? g1.date : "";
                String d2 = g2.date != null ? g2.date : "";
                return d2.compareTo(d1); // Descending
            });
        } else if (currentSortMode == 2) { // Size
            java.util.Collections.sort(filtered, (g1, g2) -> Long.compare(parseSize(g2.sizeMb), parseSize(g1.sizeMb)));
        }
        
        if (adapter != null) adapter.updateList(filtered, false);
        
    }

    private void showSortMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, btnSort);
        popup.getMenu().add(0, 0, 0, "Default");
        popup.getMenu().add(0, 1, 1, "Last Updated");
        popup.getMenu().add(0, 2, 2, "Size");
        
        popup.setOnMenuItemClickListener(item -> {
            currentSortMode = item.getItemId();
            filterGames(currentQuery);
            return true;
        });
        popup.show();
    }

    private long parseSize(String sizeStr) {
        if (sizeStr == null) return 0;
        try {
            String clean = sizeStr.trim().toUpperCase();
            double multiplier = 1;
            if (clean.contains("GB")) multiplier = 1024.0 * 1024 * 1024;
            else if (clean.contains("MB")) multiplier = 1024.0 * 1024;
            else if (clean.contains("KB")) multiplier = 1024.0;
            
            String numPart = clean.replace("GB", "").replace("MB", "").replace("KB", "").replace("BYTES", "").trim();
            return (long)(Double.parseDouble(numPart) * multiplier);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getInstalledVersion(String pkgName) {
        return installedPackagesCache.get(pkgName);
    }

    private String readNoteContent(File file) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
             StringBuilder sb = new StringBuilder();
             String line;
             while ((line = br.readLine()) != null) sb.append(line).append("\n");
             return sb.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private File findFolder(File root, String name) {
        if (root == null || !root.exists() || !root.isDirectory()) return null;
        java.util.Queue<File> queue = new java.util.LinkedList<>();
        queue.add(root);
        int foldersChecked = 0;
        while (!queue.isEmpty() && foldersChecked++ < 5000) {
            File current = queue.poll();
            if (current == null) continue;
            if (current.getName().equalsIgnoreCase(name)) return current;
            File[] files = current.listFiles();
            if (files != null) {
                for (File f : files) if (f.isDirectory()) queue.add(f);
            }
        }
        return null;
    }

    private boolean isPackageInstalled(String packageName) {
        return installedPackagesCache.containsKey(packageName);
    }

    private void saveCache() {
        try {
            File cacheFile = new File(getFilesDir(), "cached_games.json");
            String json = new com.google.gson.Gson().toJson(allGames);
            try (java.io.FileWriter writer = new java.io.FileWriter(cacheFile)) {
                writer.write(json);
            }
        } catch (Exception e) {}
    }

    private void loadCache() {
        try {
            File cacheFile = new File(getFilesDir(), "cached_games.json");
            if (!cacheFile.exists()) return;
            java.util.Scanner s = new java.util.Scanner(cacheFile).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            if (json.isEmpty()) return;
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Game>>(){}.getType();
            List<Game> cached = new com.google.gson.Gson().fromJson(json, listType);
            if (cached != null) {
                allGames = new ArrayList<>(cached);
                runOnUiThread(() -> {
                    if (adapter == null) setupRecyclerView();
                    adapter.updateList(allGames, false);
                    loader.setVisibility(View.GONE);
                    statusText.setVisibility(View.GONE);
                });
            }
        } catch (Exception e) {}
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursive(child);
            }
        }
        file.delete();
    }
}

class VrpConfig {
    String baseUri;
    String password;
}

class Game {
    String gameName;
    String releaseName;
    String version;
    String sizeMb;
    String packageName;
    String md5Hash;
    String baseUri;
    String password;
    String thumbnailPath;
    String date;
    String noteContent;
    boolean isExpanded; 
    boolean needsUpdate;
}

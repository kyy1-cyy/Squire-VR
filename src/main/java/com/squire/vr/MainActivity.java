package com.squire.vr;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squire.vr.GameAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.squire.vr.updater.GithubUpdateChecker;
import com.squire.vr.updater.UpdateActivity;
import org.json.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private GameAdapter adapter;
    private List<Game> allGames;
    private ProgressBar bgLoader;
    private View bgStatusBar;
    private TextView bgStatusDetail;
    private TextView bgStatusText;
    // private Button bgStatusViewBtn; // Removed
    private Button btnQueue;
    private Button btnSort;
    private boolean cacheInProgress;
    private int cachePct;
    private Dialog customProgressDialog;
    private ProgressBar dlBgLoader;
    private View dlBgStatusBar;
    private TextView dlBgStatusDetail;
    private TextView dlBgStatusText;
    private Button dlBgStatusViewBtn;
    private ProgressBar dlProgressBar;
    private TextView dlProgressText;
    private TextView dlSpeedEta;
    private TextView dlStatus;
    private TextView dlTitle;
    private BroadcastReceiver downloadReceiver;
    private BroadcastReceiver installResultReceiver;
    private BroadcastReceiver packageChangeReceiver;
    private ProgressBar loader;
    private SharedPreferences prefs;
    private TextView queueNotification;
    private RecyclerView recyclerView;
    private boolean showUpdatesOnly;
    private TextView statusText;
    private TextView syncStatusText;
    private Button tabAll;
    private Button tabInstalled;
    private Button tabUpdates;
    private Button tabFavorites;
    private Button tabUncrackable;
    private Button btnStorageManager;
    private Button btnResync;
    private volatile boolean metadataSyncInProgress = false;
    private File thumbnailsDir;
    private int sortMode = 0;
    private int currentTab = 0;
    private int currentSortMode = 0; // Legacy
    private Queue<Game> downloadQueue = new LinkedList();
    private Game currentDownloadingGame = null;
    private List<String> sessionCompletedGames = new ArrayList();
    private StringBuilder sessionSideloadResults = new StringBuilder();
    private boolean showInstalledOnly = false;
    private boolean showFavoritesOnly = false;
    private boolean showUncrackableOnly = false;
    private Set<String> uncrackablePackages = new HashSet<>();
    private Set<String> favoriteGames = new HashSet<>();
    private String currentQuery = "";
    private final Map<String, float[]> cloudRatings = new HashMap<>();
    private static final String RATING_SUM_PREFIX = "rating_sum_";
    private static final String RATING_COUNT_PREFIX = "rating_count_";
    private static final String RATING_MY_PREFIX = "rating_my_";
    private Map<String, String> installedPackagesCache = new HashMap();
    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).build();

    private boolean isPackageInstalled(String packageName) {
        // PERFORMANCE FIX: If cache is populated, rely on it completely.
        // If it's not in the cache, it's not installed.
        // Only fallback to system call if cache is null.
        if (installedPackagesCache != null && !installedPackagesCache.isEmpty()) {
            return installedPackagesCache.containsKey(packageName);
        }
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private long digitsToLong(String s) {
        if (s == null) {
            return 0L;
        }
        StringBuilder sb = new StringBuilder();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = s.charAt(i);
            if (cCharAt >= '0' && cCharAt <= '9') {
                sb.append(cCharAt);
            }
        }
        String string = sb.toString();
        if (string.length() <= 0) {
            return 0L;
        }
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            return 0L;
        }
    }

    private void fadeOutView(View view) {
        view.animate().alpha(0.0f).setDuration(1000L).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
                view.setAlpha(1.0f);
            }
        }).start();
    }

    private long getInstalledVersionCode(String pkgName) {
        long longVersionCode = -1;
        if (pkgName != null) {
            try {
                longVersionCode = Build.VERSION.SDK_INT >= 28 ? getPackageManager().getPackageInfo(pkgName, 0).getLongVersionCode() : getPackageManager().getPackageInfo(pkgName, 0).versionCode;
            } catch (Exception e) {
            }
        }
        return longVersionCode;
    }

    private void saveInstalledGameDate(String pkgName, String date) {
        if (pkgName == null || date == null || date.length() <= 0) {
            return;
        }
        if (this.prefs == null) {
            this.prefs = getSharedPreferences("squire_prefs", 0);
        }
        this.prefs.edit().putString("installed_date_" + pkgName, date).apply();
    }

    public void selectUpdatesTab() {
        this.currentTab = 2; // Updates
        this.showInstalledOnly = true;
        this.showUpdatesOnly = true;
        this.showFavoritesOnly = false;
        this.showUncrackableOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }
    
    public void selectFavoritesTab() {
        this.currentTab = 3; // Favorites
        this.showInstalledOnly = false;
        this.showUpdatesOnly = false;
        this.showFavoritesOnly = true;
        this.showUncrackableOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    public void selectUncrackableTab() {
        this.currentTab = 4; // Uncrackable
        this.showInstalledOnly = false;
        this.showUpdatesOnly = false;
        this.showFavoritesOnly = false;
        this.showUncrackableOnly = true;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    private void setUpdatesTabText(int updatesCount) {
        String str = "Updates (" + updatesCount + ")";
        SpannableString spannableString = new SpannableString(str);
        int iIndexOf = str.indexOf(40);
        int iIndexOf2 = str.indexOf(41) + 1;
        if (iIndexOf >= 0 && iIndexOf2 > iIndexOf) {
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00C853")), iIndexOf, iIndexOf2, 33);
        }
        this.tabUpdates.setText(spannableString);
    }

    private void updateTabLabels() {
        List<Game> list = this.allGames;
        int size = list != null ? list.size() : 0;
        int i = 0;
        int i2 = 0;
        int favCount = 0;
        HashMap map = new HashMap();
        if (list != null) {
            for (Game game : list) {
                String str = game.packageName;
                long jDigitsToLong = digitsToLong(game.version);
                Long l = (Long) map.get(str);
                if (l == null || jDigitsToLong > l.longValue()) {
                    map.put(str, Long.valueOf(jDigitsToLong));
                }
            }
        }
        if (list != null) {
            for (Game game2 : list) {
                if (game2.isFavorite) {
                    favCount++;
                }
                String str2 = game2.packageName;
                if (isPackageInstalled(str2)) {
                    i++;
                    long installedVersionCode = getInstalledVersionCode(str2);
                    game2.installedVersion = Long.toString(installedVersionCode);
                    Long l2 = (Long) map.get(str2);
                    if (l2 != null) {
                        long jLongValue = l2.longValue();
                        if (digitsToLong(game2.version) == jLongValue && installedVersionCode < jLongValue) {
                            game2.needsUpdate = true;
                            i2++;
                        } else {
                            game2.needsUpdate = false;
                        }
                    } else {
                        game2.needsUpdate = false;
                    }
                } else {
                    game2.needsUpdate = false;
                }
            }
        }
        this.tabAll.setText("All (" + size + ")");
        this.tabInstalled.setText("Installed (" + i + ")");
        this.tabFavorites.setText("Favorites (" + favCount + ")");
        int uncrackableCount = 0;
        if (list != null) {
            for (Game game : list) {
                if (uncrackablePackages.contains(game.packageName)) {
                    uncrackableCount++;
                }
            }
        }
        this.tabUncrackable.setText("Uncrackable (" + uncrackableCount + ")");
        setUpdatesTabText(i2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Load config from SharedPreferences
        loadConfig();
        
        this.recyclerView = (RecyclerView) findViewById(R.id.game_list);
        this.loader = (ProgressBar) findViewById(R.id.loader);
        this.statusText = (TextView) findViewById(R.id.status_text);
        this.syncStatusText = (TextView) findViewById(R.id.sync_status_text);
        this.statusText.setText("");
        this.tabAll = (Button) findViewById(R.id.tab_all);
        this.tabInstalled = (Button) findViewById(R.id.tab_installed);
        this.tabUpdates = (Button) findViewById(R.id.tab_updates);
        this.tabFavorites = (Button) findViewById(R.id.tab_favorites);
        this.tabUncrackable = (Button) findViewById(R.id.tab_uncrackable);
        this.btnSort = (Button) findViewById(R.id.btn_sort);
        this.btnQueue = (Button) findViewById(R.id.btn_queue);
        this.tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$0(view);
            }
        });
        
        // Check if config is set, if not show dialog (only after permissions are granted)
        if (Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()) {
            if (Config.MIRROR_BASEURI_URL.isEmpty() || Config.DEFAULT_PASSWORD.isEmpty() && !isConfigDialogShowing) {
                showConfigDialog();
            }
        }
        this.tabInstalled.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$1(view);
            }
        });
        this.tabUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.selectUpdatesTab();
            }
        });
        this.tabFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.selectFavoritesTab();
            }
        });
        this.tabUncrackable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.selectUncrackableTab();
            }
        });
        
        this.btnStorageManager = findViewById(R.id.btn_storage_manager);
        this.btnStorageManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MainActivity.this, DeviceStorageActivity.class));
                } catch (Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.btnResync = (Button) findViewById(R.id.btn_resync);
        this.btnResync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.resyncMetadata();
            }
        });

        this.btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.this.showCustomSortMenu();
            }
        });
        this.btnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.this.showQueueDialog();
            }
        });
        this.btnSort.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                startActivity(new Intent(MainActivity.this, (Class<?>) SquireManagerActivity.class));
                return true;
            }
        });
        
        this.prefs = getSharedPreferences("squire_prefs", 0);
        this.favoriteGames = this.prefs.getStringSet("favorites", new HashSet<>());
        
        setupRecyclerView();
        this.installResultReceiver = new AnonymousClass1();
        this.queueNotification = (TextView) findViewById(R.id.queue_notification);
        this.bgStatusBar = findViewById(R.id.bg_status_bar);
        this.bgLoader = (ProgressBar) findViewById(R.id.bg_loader);
        this.bgStatusText = (TextView) findViewById(R.id.bg_status_text);
        this.bgStatusDetail = (TextView) findViewById(R.id.bg_status_detail);
        // this.bgStatusViewBtn = (Button) findViewById(R.id.bg_status_view_btn);
        this.dlBgStatusBar = findViewById(R.id.dl_bg_status_bar);
        this.dlBgStatusText = (TextView) findViewById(R.id.dl_bg_status_text);
        this.dlBgStatusDetail = (TextView) findViewById(R.id.dl_bg_status_detail);
        this.dlBgLoader = (ProgressBar) findViewById(R.id.dl_bg_loader);
        this.dlBgStatusViewBtn = (Button) findViewById(R.id.dl_bg_status_view_btn);
        
        /*
        this.bgStatusViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                // MainActivity.this.lambda$onCreate$6(view);
            }
        });
        */
        
        this.dlBgStatusViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$6(view);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(InstallReceiver.ACTION_INSTALL_RESULT);
        filter.addAction(InstallReceiver.ACTION_INSTALL_STATUS);
        registerReceiver(this.installResultReceiver, filter, 2);
        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    MainActivity.this.filterGames(newText);
                    return true;
                }
            });
        }
        checkForUpdates();
    }

    private void checkForUpdates() {
        new GithubUpdateChecker(this).check(new GithubUpdateChecker.UpdateCallback() {
            @Override
            public void onUpdateAvailable(String version, String body, String downloadUrl) {
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("version", version);
                intent.putExtra("body", body);
                intent.putExtra("url", downloadUrl);
                startActivity(intent);
            }

            @Override
            public void onNoUpdate() {
                runOnUiThread(() -> startAppFlow());
            }

            @Override
            public void onError(String error) {
                Log.e("SquireUpdate", "Check failed: " + error);
                runOnUiThread(() -> startAppFlow());
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0(View v) {
        this.currentTab = 0; // All
        this.showInstalledOnly = false;
        this.showUpdatesOnly = false;
        this.showFavoritesOnly = false;
        this.showUncrackableOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    public /* synthetic */ void lambda$onCreate$1(View v) {
        this.currentTab = 1; // Installed
        this.showInstalledOnly = true;
        this.showUpdatesOnly = false;
        this.showFavoritesOnly = false;
        this.showUncrackableOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    public /* synthetic */ void lambda$onCreate$6(View v) {
        if (this.currentDownloadingGame != null) {
            if (this.customProgressDialog == null || !this.customProgressDialog.isShowing()) {
                showCustomProgressDialog(this.currentDownloadingGame.gameName);
            }
            this.dlBgStatusBar.setVisibility(8);
            // this.bgStatusViewBtn.setVisibility(8);
        }
    }

    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (InstallReceiver.ACTION_INSTALL_RESULT.equals(action)) {
                MainActivity.this.dismissDialog();
                boolean success = intent.getBooleanExtra("success", false);
                boolean obbMoved = intent.getBooleanExtra("obbMoved", false);
                intent.getStringExtra("obbPath");
                String pkgName = intent.getStringExtra("pkgName");
                if (success) {
                    MainActivity.this.sessionSideloadResults.append("✅ ").append(pkgName).append(" [OK]\n");
                    MainActivity mainActivity = MainActivity.this;
                    Game game = mainActivity.currentDownloadingGame;
                    if (game != null) {
                        mainActivity.saveInstalledGameDate(pkgName, game.date);
                    }
                    if (obbMoved) {
                        MainActivity.this.sessionSideloadResults.append("   - OBB Moved\n");
                    }
                } else {
                    MainActivity.this.sessionSideloadResults.append("❌ ").append(pkgName).append(" [FAILED]\n");
                }
                if (MainActivity.this.downloadQueue.isEmpty()) {
                    MainActivity.this.refreshPackageCache();
                    new AlertDialog.Builder(MainActivity.this).setTitle("Games Done").setMessage("Organization: /sdcard/Download/Squire Vr Games/\n\n" + MainActivity.this.sessionSideloadResults.toString()).setPositiveButton("Awesome", new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.sessionSideloadResults.setLength(0);
                            MainActivity.this.sessionCompletedGames.clear();
                        }
                    }).show();
                }
                MainActivity.this.processNextInQueue();
                return;
            }
            if (InstallReceiver.ACTION_INSTALL_STATUS.equals(action)) {
                String status = intent.getStringExtra(NotificationCompat.CATEGORY_STATUS);
                MainActivity.this.updateDialogMessage(status);
            }
        }
    }

    private void setupRecyclerView() {
        int widthPx = getResources().getDisplayMetrics().widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int widthDp = (int) (widthPx / density);
        int targetItemDp = 340;
        int cols = Math.max(1, Math.min(3, widthDp / targetItemDp));
        GridLayoutManager glm = new GridLayoutManager(this, cols);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (MainActivity.this.adapter == null || MainActivity.this.adapter.getItemViewType(position) != 1) ? 1 : 3;
            }
        });
        this.recyclerView.setLayoutManager(glm);
        if (this.adapter == null) {
            this.adapter = new GameAdapter(new ArrayList(), new GameAdapter.OnGameUninstallListener() {
                @Override
                public void onDownloadClick(Game game) {
                     // Check if installed/needs update first? 
                     // Logic in startDownload handles logic
                     startDownload(game);
                }

                @Override
                public void onFavoriteToggle(Game game) {
                    MainActivity.this.onFavoriteToggled(game);
                }
                
                @Override
                public void onUninstallClick(Game game) {
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Uninstall Game")
                        .setMessage("Are you sure you want to uninstall " + game.gameName + "?\n\nThis will remove the game and all its data.")
                        .setPositiveButton("Yes, Uninstall", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Uri packageUri = Uri.parse("package:" + game.packageName);
                                    Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                                    uninstallIntent.setData(packageUri);
                                    uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                                    uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    runOnUiThread(() -> {
                                        try {
                                            MainActivity.this.startActivity(uninstallIntent);
                                        } catch (Exception e) {
                                            try {
                                                Intent deleteIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                                deleteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                MainActivity.this.startActivity(deleteIntent);
                                            } catch (Exception ex) {
                                                try {
                                                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", game.packageName, null));
                                                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    MainActivity.this.startActivity(settingsIntent);
                                                } catch (Exception finalEx) {
                                                    android.util.Log.e("SquireUninstall", "Failed to start uninstall", finalEx);
                                                    Toast.makeText(MainActivity.this, "Error: " + finalEx.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    android.util.Log.e("SquireUninstall", "Outer Error", e);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }

                @Override
                public void onRateClick(Game game) {
                    MainActivity.this.onRateGame(game);
                }
            });
        }
        this.recyclerView.setAdapter(this.adapter);
        if (this.adapter != null) {
            this.adapter.setSkeletonMode(true, cols * 4);
        }
    }

    public void onFavoriteToggled(Game game) {
        if (game.isFavorite) {
            favoriteGames.add(game.packageName);
        } else {
            favoriteGames.remove(game.packageName);
        }
        prefs.edit().putStringSet("favorites", favoriteGames).apply();
        
        saveCache();
        updateTabLabels();
        
        if (showFavoritesOnly) {
            filterGames(currentQuery);
        }
    }

    private void startAppFlow() {
        if (checkAndRequestPermissions()) {
            new Thread(new Runnable() {
                @Override
                public final void run() {
                    MainActivity.this.lambda$startAppFlow$8();
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$startAppFlow$8() {
        refreshPackageCache();
        loadCache();
        verifyBinaries();
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                MainActivity.this.fetchGames();
            }
        });
    }

    public void refreshPackageCache() {
        Map<String, String> newCache = new HashMap<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo pi : packages) {
            newCache.put(pi.packageName, pi.versionName);
        }
        this.installedPackagesCache = newCache;
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (MainActivity.this.allGames != null) {
                    MainActivity.this.updateTabLabels();
                    MainActivity.this.filterGames(MainActivity.this.currentQuery);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        
        // Check if we were waiting for permission and now have it
        if (wasWaitingForPermission && Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
            wasWaitingForPermission = false;
            showRestartDialog();
        }
        
        new Thread(new Runnable() {
            @Override
            public final void run() {
                MainActivity.this.refreshPackageCache();
            }
        }).start();
        if (this.packageChangeReceiver == null) {
            this.packageChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action == null) return;
                    Uri data = intent.getData();
                    String pkg = data != null ? data.getSchemeSpecificPart() : null;
                    if (pkg == null || pkg.isEmpty()) return;
                    if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                        if (installedPackagesCache != null) {
                            installedPackagesCache.remove(pkg);
                        }
                        if (allGames != null) {
                            for (Game g : allGames) {
                                if (pkg.equals(g.packageName)) {
                                    g.installedVersion = null;
                                    g.installedDate = null;
                                    g.needsUpdate = false;
                                }
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTabLabels();
                                filterGames(currentQuery);
                                if (adapter != null) adapter.notifyDataSetChanged();
                            }
                        });
                    } else if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.refreshPackageCache();
                            }
                        }).start();
                    }
                }
            };
            IntentFilter pf = new IntentFilter();
            pf.addAction(Intent.ACTION_PACKAGE_REMOVED);
            pf.addAction(Intent.ACTION_PACKAGE_ADDED);
            pf.addAction(Intent.ACTION_PACKAGE_CHANGED);
            pf.addDataScheme("package");
            registerReceiver(this.packageChangeReceiver, pf);
        }
        if (this.downloadReceiver == null) {
            this.downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra("error")) {
                        MainActivity.this.dismissDialog();
                        String err = intent.getStringExtra("error");
                        MainActivity.this.showError("Download Failed: " + err);
                        return;
                    }
                    String statusMsg = intent.getStringExtra("statusMsg");
                    String phase = intent.getStringExtra("phase"); // Was TypedValues.CycleType.S_WAVE_PHASE
                    if (statusMsg == null) statusMsg = "Processing...";
                    if (phase == null) phase = "download";
                    
                    if (MainActivity.this.dlBgStatusBar.getVisibility() == 0 && MainActivity.this.currentDownloadingGame != null) {
                        int pct = intent.getIntExtra("progress_percent", 0);
                        int queueSize = MainActivity.this.downloadQueue.size();
                        String queueSuffix = queueSize > 0 ? " (Queue: " + queueSize + ")" : "";
                        MainActivity.this.dlBgStatusText.setText("Downloading " + MainActivity.this.currentDownloadingGame.gameName);
                        MainActivity.this.dlBgStatusDetail.setText(pct + "% Completed" + queueSuffix);
                    }
                    
                    if (intent.hasExtra("complete")) {
                         String name = intent.getStringExtra("name");
                         if (name != null) MainActivity.this.sessionCompletedGames.add(name);
                         
                         if (MainActivity.this.customProgressDialog != null && MainActivity.this.customProgressDialog.isShowing()) {
                            MainActivity.this.customProgressDialog.dismiss();
                         }
                         
                         boolean finishedAll = MainActivity.this.downloadQueue.isEmpty();
                         MainActivity.this.processNextInQueue();
                         if (finishedAll) {
                            MainActivity.this.showFinalSummary();
                            return;
                         }
                         return;
                    }
                    
                    if (MainActivity.this.customProgressDialog != null && MainActivity.this.customProgressDialog.isShowing()) {
                        MainActivity.this.dlBgStatusBar.setVisibility(8);
                        long current = intent.getLongExtra("current", 0L);
                        long total = intent.getLongExtra("total", 100L);
                        if (total <= 0) total = 100;
                        long speed = intent.getLongExtra("speed", 0L);
                        int progress = (int) ((100 * current) / total);
                        
                        if (MainActivity.this.dlProgressBar != null) {
                            MainActivity.this.dlProgressBar.setIndeterminate(false);
                            MainActivity.this.dlProgressBar.setProgress(progress);
                        }
                        
                        String speedStr = "";
                        if ("download".equals(phase)) {
                            double speedMb = speed / 1048576.0d;
                            speedStr = String.format("%.2f MB/s", speedMb);
                        }
                        
                        String etaStr = "--:--";
                        if (speed > 0) {
                            long remainingBytes = total - current;
                            long secondsLeft = remainingBytes / speed;
                            long mins = secondsLeft / 60;
                            long secs = secondsLeft % 60;
                            etaStr = String.format("%dm %ds", mins, secs);
                        } else if (speed == -1) {
                            speedStr = "Calculating...";
                            etaStr = "--:--";
                        }
                        
                        if (!"download".equals(phase)) {
                            MainActivity.this.updateDialogMessage(statusMsg);
                            if (MainActivity.this.dlProgressText != null) MainActivity.this.dlProgressText.setText("");
                            if (MainActivity.this.dlSpeedEta != null) MainActivity.this.dlSpeedEta.setText(etaStr);
                            return;
                        }
                        
                        double currentMb = current / 1048576.0d;
                        double totalMb = total / 1048576.0d;
                        MainActivity.this.updateDialogMessage(statusMsg);
                        if (MainActivity.this.dlProgressText != null) {
                            MainActivity.this.dlProgressText.setText(progress + "% (" + String.format("%.2f", currentMb) + "MB / " + String.format("%.2f", totalMb) + "MB)");
                        }
                        if (MainActivity.this.dlSpeedEta != null) {
                            MainActivity.this.dlSpeedEta.setText(speedStr + " • " + etaStr);
                        }
                    }
                }
            };
            registerReceiver(this.downloadReceiver, new IntentFilter(Config.ACTION_PROGRESS));
        }
        if (this.recyclerView.getAdapter() == null && this.loader.getVisibility() == 0 && Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
            startAppFlow();
        }
    }

    @Override
    protected void onDestroy() {
        if (this.downloadReceiver != null) {
            unregisterReceiver(this.downloadReceiver);
        }
        if (this.installResultReceiver != null) {
            unregisterReceiver(this.installResultReceiver);
        }
        if (this.packageChangeReceiver != null) {
            unregisterReceiver(this.packageChangeReceiver);
        }
        super.onDestroy();
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
             wasWaitingForPermission = true;
             runOnUiThread(new Runnable() {
                 @Override // java.lang.Runnable
                 public void run() {
                     final Dialog dialog = new Dialog(MainActivity.this);
                     dialog.setContentView(R.layout.dialog_permission);
                     if (dialog.getWindow() != null) {
                         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                     }
                     dialog.setCancelable(false);

                     Button btnGrant = dialog.findViewById(R.id.btn_grant);
                     Button btnExit = dialog.findViewById(R.id.btn_exit);

                     btnGrant.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             dialog.dismiss();
                             try {
                                 Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                 intent.setData(android.net.Uri.parse("package:" + MainActivity.this.getPackageName()));
                                 MainActivity.this.startActivity(intent);
                             } catch (Exception e) {
                                 Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                 MainActivity.this.startActivity(intent);
                             }
                         }
                     });

                     btnExit.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             MainActivity.this.finish();
                         }
                     });

                     dialog.show();
                 }
             });
             return false;
        }
        return true;
    }
    
    private boolean wasWaitingForPermission = false;
    private boolean isConfigDialogShowing = false;
    
    private void showRestartDialog() {
        // Create custom dialog without AlertDialog wrapper for sleek look
        final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(false);
        
        // Main container with dark background
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        container.setBackgroundColor(Color.parseColor("#E6000000")); // 90% black
        
        // Card layout
        android.widget.LinearLayout card = new android.widget.LinearLayout(this);
        card.setOrientation(android.widget.LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        card.setBackgroundColor(Color.parseColor("#1A1A1A"));
        int padding = (int) (32 * getResources().getDisplayMetrics().density);
        card.setPadding(padding, padding, padding, padding);
        
        // Success icon (checkmark)
        android.widget.TextView icon = new android.widget.TextView(this);
        icon.setText("✓");
        icon.setTextSize(48);
        icon.setTextColor(Color.parseColor("#4CAF50"));
        icon.setGravity(android.view.Gravity.CENTER);
        card.addView(icon);
        
        // Title
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("Access Granted");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 16, 0, 0);
        card.addView(title);
        
        // Message
        android.widget.TextView message = new android.widget.TextView(this);
        message.setText("Please restart to apply changes");
        message.setTextSize(14);
        message.setTextColor(Color.parseColor("#888888"));
        message.setGravity(android.view.Gravity.CENTER);
        message.setPadding(0, 8, 0, 0);
        card.addView(message);
        
        // Restart button
        android.widget.Button restartBtn = new android.widget.Button(this);
        restartBtn.setText("Restart Now");
        restartBtn.setTextColor(Color.parseColor("#FFFFFF"));
        restartBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        restartBtn.setPadding(0, 16, 0, 16);
        android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.topMargin = 24;
        restartBtn.setLayoutParams(btnParams);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartApp();
            }
        });
        card.addView(restartBtn);
        
        // Center card in container
        android.widget.FrameLayout.LayoutParams cardParams = new android.widget.FrameLayout.LayoutParams(
            (int) (280 * getResources().getDisplayMetrics().density),
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.gravity = android.view.Gravity.CENTER;
        card.setLayoutParams(cardParams);
        
        container.addView(card);
        dialog.setContentView(container);
        dialog.show();
    }
    
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Runtime.getRuntime().exit(0);
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

    private void updateStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (MainActivity.this.syncStatusText != null) {
                    MainActivity.this.syncStatusText.setText(status);
                }
                if (MainActivity.this.statusText != null) {
                    MainActivity.this.statusText.setText(status);
                }
            }
        });
    }

    private String ratingKey(Game game) {
        if (game.packageName != null && !game.packageName.trim().isEmpty()) return game.packageName.trim();
        return game.releaseName == null ? "" : game.releaseName.trim();
    }

    private void loadRatingForGame(Game game) {
        if (game == null || prefs == null) return;
        String key = ratingKey(game);
        float sum = prefs.getFloat(RATING_SUM_PREFIX + key, 0f);
        int count = prefs.getInt(RATING_COUNT_PREFIX + key, 0);
        game.ratingCount = Math.max(0, count);
        game.ratingAverage = game.ratingCount > 0 ? (sum / game.ratingCount) : 0d;
        game.myRating = prefs.getInt(RATING_MY_PREFIX + key, 0);
        float[] cloud = cloudRatings.get(key);
        if (cloud != null && cloud.length >= 2 && cloud[1] > 0f) {
            game.ratingAverage = cloud[0];
            game.ratingCount = (int) cloud[1];
        }
    }

    private void loadRatingsForGames(List<Game> games) {
        if (games == null) return;
        for (Game g : games) loadRatingForGame(g);
    }

    private void styleRateButtons(Button[] stars, int selected) {
        for (int i = 0; i < stars.length; i++) {
            if (stars[i] == null) continue;
            stars[i].setBackgroundTintList(ColorStateList.valueOf((i + 1) <= selected ? 0xFF007AFF : 0xFF333333));
        }
    }

    private void submitRatingToCloud(Game game, int rating) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("packageName", game.packageName);
                body.put("releaseName", game.releaseName);
                body.put("rating", rating);
                Request request = new Request.Builder()
                        .url(Config.RATINGS_SUBMIT_URL)
                        .post(RequestBody.create(body.toString(), MediaType.parse("application/json; charset=utf-8")))
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) return;
                    String raw = response.body() != null ? response.body().string() : null;
                    if (raw == null || raw.trim().isEmpty()) return;
                    JSONObject json = new JSONObject(raw);
                    String key = ratingKey(game);
                    double avg = json.optDouble("average", -1d);
                    int count = json.optInt("count", -1);
                    if (avg >= 0d && count >= 0) {
                        cloudRatings.put(key, new float[]{(float) avg, (float) count});
                        runOnUiThread(() -> {
                            loadRatingForGame(game);
                            if (adapter != null) adapter.notifyDataSetChanged();
                        });
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void loadCloudRatings() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder().url(Config.RATINGS_LIST_URL).get().build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) return;
                    String body = response.body() != null ? response.body().string() : null;
                    if (body == null || body.trim().isEmpty()) return;
                    JSONObject root = new JSONObject(body);
                    synchronized (cloudRatings) {
                        cloudRatings.clear();
                        java.util.Iterator<String> keys = root.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject entry = root.optJSONObject(key);
                            if (entry == null) continue;
                            float avg = (float) entry.optDouble("average", 0d);
                            int count = entry.optInt("count", 0);
                            cloudRatings.put(key, new float[]{avg, count});
                        }
                    }
                }
                runOnUiThread(() -> {
                    if (allGames != null) loadRatingsForGames(allGames);
                    if (adapter != null) adapter.notifyDataSetChanged();
                });
            } catch (Exception ignored) {}
        }).start();
    }

    private void onRateGame(Game game) {
        if (game == null) return;
        final Dialog rateDialog = new Dialog(this);
        rateDialog.requestWindowFeature(1);
        rateDialog.setContentView(R.layout.dialog_rate_game);
        if (rateDialog.getWindow() != null) {
            rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xCC000000));
        }
        TextView title = rateDialog.findViewById(R.id.rate_title);
        if (title != null) title.setText("Rate " + game.gameName);
        final Button[] stars = new Button[]{
                rateDialog.findViewById(R.id.rate_star_1),
                rateDialog.findViewById(R.id.rate_star_2),
                rateDialog.findViewById(R.id.rate_star_3),
                rateDialog.findViewById(R.id.rate_star_4),
                rateDialog.findViewById(R.id.rate_star_5)
        };
        final int[] selected = new int[]{game.myRating > 0 ? game.myRating : 0};
        styleRateButtons(stars, selected[0]);
        for (int i = 0; i < stars.length; i++) {
            final int value = i + 1;
            if (stars[i] != null) {
                stars[i].setOnClickListener(v -> {
                    selected[0] = value;
                    styleRateButtons(stars, value);
                    String key = ratingKey(game);
                    float sum = prefs.getFloat(RATING_SUM_PREFIX + key, 0f);
                    int count = prefs.getInt(RATING_COUNT_PREFIX + key, 0);
                    int oldMine = prefs.getInt(RATING_MY_PREFIX + key, 0);
                    if (oldMine > 0) sum -= oldMine;
                    else count += 1;
                    sum += value;
                    prefs.edit()
                            .putFloat(RATING_SUM_PREFIX + key, sum)
                            .putInt(RATING_COUNT_PREFIX + key, count)
                            .putInt(RATING_MY_PREFIX + key, value)
                            .apply();
                    loadRatingForGame(game);
                    if (adapter != null) adapter.notifyDataSetChanged();
                    submitRatingToCloud(game, value);
                    rateDialog.dismiss();
                });
            }
        }
        rateDialog.show();
    }

    private String getNativeLibPath(String libName) {
        return getApplicationInfo().nativeLibraryDir + "/" + libName;
    }

    private File findGameListFile(File root) {
        if (root == null || !root.exists()) return null;
        File direct = new File(root, "GameList.txt");
        if (direct.exists()) return direct;
        direct = new File(root, "VRP-GameList.txt");
        if (direct.exists()) return direct;
        File[] items = root.listFiles();
        if (items == null) return null;
        for (File f : items) {
            String lower = f.getName().toLowerCase(Locale.US);
            if (f.isFile() && (lower.contains("gamelist") || lower.endsWith("amelist.txt"))) {
                return f;
            }
        }
        for (File f : items) {
            if (f.isDirectory()) {
                File hit = findGameListFile(f);
                if (hit != null) return hit;
            }
        }
        return null;
    }

    public void fetchGames() {
        if (metadataSyncInProgress) return;
        
        // Check if config is set
        if (Config.MIRROR_BASEURI_URL.isEmpty() || Config.DEFAULT_PASSWORD.isEmpty()) {
            updateStatus("Please configure the app first");
            showConfigDialog();
            return;
        }
        
        // Check if metadata already exists - if so, load from cache instead of re-downloading
        File metaDir = new File(getFilesDir(), ".meta");
        File gameListFile = findGameListFile(metaDir);
        if (gameListFile != null && gameListFile.exists()) {
            // Metadata exists, load from cache
            loadGamesFromCache(gameListFile);
            return;
        }
        
        metadataSyncInProgress = true;
        updateStatus("Loading VrSrc config...");
        new Thread(new Runnable() {
            @Override
            public final void run() {
                final VrpConfig config = new VrpConfig();
                config.baseUri = Config.MIRROR_BASEURI_URL;
                config.password = Config.DEFAULT_PASSWORD;
                runOnUiThread(() -> {
                    MainActivity.this.updateStatus("Config loaded. Mirror: " + config.baseUri);
                    MainActivity.this.downloadMeta(config);
                });
            }
        }).start();
    }

    private void resyncMetadata() {
        if (metadataSyncInProgress) {
            Toast.makeText(this, "Resync already running...", Toast.LENGTH_SHORT).show();
            return;
        }
        metadataSyncInProgress = true;
        if (btnResync != null) btnResync.setEnabled(false);
        updateStatus("Syncing Meta from VrSrc...");
        this.recyclerView.setVisibility(View.GONE);
        View syncOverlay = (View) this.loader.getParent();
        if (syncOverlay != null) syncOverlay.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.VISIBLE);
        final VrpConfig config = new VrpConfig();
        config.baseUri = Config.MIRROR_BASEURI_URL;
        config.password = Config.DEFAULT_PASSWORD;
        downloadMeta(config);
    }

    private void finishMetadataSyncUi() {
        metadataSyncInProgress = false;
        if (btnResync != null) btnResync.setEnabled(true);
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        // Clear extraction status from top left
        if (statusText != null) statusText.setText("");
    }

    public void showError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                View syncOverlay = (View) MainActivity.this.loader.getParent();
                if (syncOverlay != null) syncOverlay.setVisibility(View.GONE);
                MainActivity.this.loader.setVisibility(8);
                if (MainActivity.this.syncStatusText != null) MainActivity.this.syncStatusText.setText("Sync failed");
                MainActivity.this.finishMetadataSyncUi();
                
                // Check if error is config-related and reopen config dialog
                boolean isConfigError = msg.toLowerCase().contains("sync failed") || 
                                       msg.toLowerCase().contains("could not fetch") ||
                                       msg.toLowerCase().contains("not a .7z archive") ||
                                       msg.toLowerCase().contains("archive missing") ||
                                       msg.toLowerCase().contains("wrong password") ||
                                       msg.toLowerCase().contains("data error");
                
                if (isConfigError) {
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Configuration Error")
                        .setMessage("Invalid configuration: " + msg + "\n\nPlease check your Base URI and Password.")
                        .setPositiveButton("Retry", (DialogInterface.OnClickListener) null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                showConfigDialog();
                            }
                        })
                        .show();
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage(msg).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
                }
            }
        });
    }

    public void showCustomProgressDialog(String title) {
        if (customProgressDialog != null && customProgressDialog.isShowing()) return;
        customProgressDialog = new Dialog(this);
        customProgressDialog.requestWindowFeature(1);
        customProgressDialog.setContentView(R.layout.dialog_download);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        customProgressDialog.setCancelable(false);
        dlTitle = customProgressDialog.findViewById(R.id.dl_game_title);
        dlStatus = customProgressDialog.findViewById(R.id.dl_status);
        dlProgressText = customProgressDialog.findViewById(R.id.dl_progress_text);
        dlSpeedEta = customProgressDialog.findViewById(R.id.dl_speed_eta);
        dlProgressBar = customProgressDialog.findViewById(R.id.dl_progress_bar);
        
        Button btnBg = customProgressDialog.findViewById(R.id.btn_bg);
        Button btnPause = customProgressDialog.findViewById(R.id.btn_pause);
        Button btnCancel = customProgressDialog.findViewById(R.id.btn_cancel);
        
        btnBg.setOnClickListener(v -> {
            customProgressDialog.dismiss();
            dlBgStatusBar.setVisibility(0);
            // bgStatusViewBtn.setVisibility(0);
            if (currentDownloadingGame != null) {
                dlBgStatusText.setText("Downloading " + currentDownloadingGame.gameName);
                dlBgStatusDetail.setText("Check details");
            }
        });
        
        if (btnPause != null) {
            btnPause.setOnClickListener(v -> {
                // Pause logic: Stop service, keep files, but DO NOT dismiss dialog immediately
                // Instead, update UI to show "Resume" state
                stopService(new Intent(MainActivity.this, StreamingService.class));
                
                // Update dialog UI to reflect paused state
                dlStatus.setText("Paused");
                dlSpeedEta.setText("0 MB/s • --:--");
                btnPause.setText("Resume");
                
                // Toggle listener to restart download if clicked again
                btnPause.setOnClickListener(v2 -> {
                    // Resume logic: Dismiss current dialog (to reset state) and restart download
                    // The startDownload logic handles resuming automatically if files exist
                    customProgressDialog.dismiss();
                    Game gameToResume = currentDownloadingGame;
                    // FIX: Set currentDownloadingGame to null so startDownload knows it's a new request (resume) and not a queue add
                    currentDownloadingGame = null;
                    if (gameToResume != null) {
                        startDownload(gameToResume);
                    }
                });
            });
        }
        
        btnCancel.setOnClickListener(v -> {
            customProgressDialog.dismiss();
            stopService(new Intent(MainActivity.this, StreamingService.class));
            // FIX: Ensure delete happens!
            if (currentDownloadingGame != null) {
                final String rName = currentDownloadingGame.releaseName;
                new Thread(() -> {
                     // Wait for service to fully release locks
                     try { Thread.sleep(500); } catch (Exception e) {}
                     
                     File dir = new File("/sdcard/Download/Squire Vr Games/" + rName);
                     // Also check temp archives!
                     File tempDir = new File(dir, "temp_archives");
                     if (tempDir.exists()) deleteRecursive(tempDir);
                     if (dir.exists()) deleteRecursive(dir);
                     
                     // IMPORTANT: Notify adapter AFTER deletion to update "Resume" -> "Download" label
                     runOnUiThread(() -> {
                         if (adapter != null) adapter.notifyDataSetChanged();
                     });
                }).start();
            }
            processNextInQueue();
        });
        
        if (dlTitle != null) dlTitle.setText(title);
        customProgressDialog.show();
    }
    
    public void dismissDialog() {
        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            customProgressDialog.dismiss();
        }
    }
    
    public void updateDialogTitle(String title) {
        if (customProgressDialog != null && customProgressDialog.isShowing() && dlTitle != null) {
            dlTitle.setText(title);
        }
    }
    
    public void updateDialogMessage(String msg) {
        if (customProgressDialog != null && customProgressDialog.isShowing() && dlStatus != null) {
            dlStatus.setText(msg);
        }
    }

    public void showCustomSortMenu() {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_sort, (ViewGroup) null);
        popup.setContentView(layout);
        popup.setHeight(-2);
        popup.setWidth(-2);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(0));
        layout.findViewById(R.id.sort_default).setOnClickListener(v -> {
            sortMode = 0;
            currentSortMode = 0;
            filterGames(currentQuery);
            popup.dismiss();
        });
        layout.findViewById(R.id.sort_date).setOnClickListener(v -> {
            sortMode = 1;
            currentSortMode = 1;
            filterGames(currentQuery);
            popup.dismiss();
        });
        layout.findViewById(R.id.sort_size).setOnClickListener(v -> {
            sortMode = 3; // Using 3 for size in my logic
            currentSortMode = 2; // Legacy logic uses 2
            filterGames(currentQuery);
            popup.dismiss();
        });
        layout.findViewById(R.id.sort_popularity).setOnClickListener(v -> {
            sortMode = 2; // Popularity
            currentSortMode = 2;
            filterGames(currentQuery);
            popup.dismiss();
        });
        popup.showAsDropDown(this.btnSort, 0, 10);
    }

    private void showQueueDialog() {
        final Dialog qDialog = new Dialog(this);
        qDialog.requestWindowFeature(1);
        qDialog.setContentView(R.layout.dialog_queue);
        qDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        // FIX: Do not force layout to MATCH_PARENT, let it wrap content to center properly
        // qDialog.getWindow().setLayout(-1, -2);
        
        RecyclerView rv = (RecyclerView) qDialog.findViewById(R.id.queue_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        final List<Game> qList = new ArrayList<>();
        if (this.currentDownloadingGame != null) {
            qList.add(this.currentDownloadingGame);
        }
        qList.addAll(this.downloadQueue);
        if (qList.isEmpty()) {
            qDialog.findViewById(R.id.queue_empty_text).setVisibility(0);
        }
        rv.setAdapter(new RecyclerView.Adapter<QueueViewHolder>() {
            @Override
            public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new QueueViewHolder(MainActivity.this.getLayoutInflater().inflate(R.layout.item_queue, parent, false));
            }

            @Override
            public void onBindViewHolder(QueueViewHolder holder, int position) {
                Game g = (Game) qList.get(position);
                holder.name.setText(g.gameName);
                if (g == MainActivity.this.currentDownloadingGame) {
                    holder.status.setText("Downloading...");
                    holder.status.setTextColor(-16745729);
                    // Hide X for currently downloading game? Or allow cancelling?
                    // User says clicking queue x does nothing. 
                    // This X is probably the one in item_queue.xml.
                    holder.removeBtn.setVisibility(View.GONE); 
                } else {
                    holder.status.setText("Waiting");
                    holder.status.setTextColor(-5592406);
                    holder.removeBtn.setVisibility(View.VISIBLE);
                    holder.removeBtn.setOnClickListener(v -> {
                         // Remove from queue logic
                         MainActivity.this.downloadQueue.remove(g);
                         qList.remove(position);
                         notifyItemRemoved(position);
                         notifyItemRangeChanged(position, qList.size());
                         if (qList.isEmpty()) qDialog.dismiss();
                    });
                }
            }

            @Override
            public int getItemCount() {
                return qList.size();
            }
        });
        qDialog.findViewById(R.id.btn_close_queue).setOnClickListener(v -> qDialog.dismiss());
        // Added X button listener
        View btnX = qDialog.findViewById(R.id.btn_x_close);
        if (btnX != null) btnX.setOnClickListener(v -> qDialog.dismiss());
        
        qDialog.show();
    }
    
    class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        View removeBtn; // Added
        public QueueViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.q_game_name);
            this.status = (TextView) itemView.findViewById(R.id.q_status);
            this.removeBtn = itemView.findViewById(R.id.btn_remove_queue); // Added
        }
    }

    private void downloadMeta(final VrpConfig config) {
        updateStatus("Syncing Meta from VrSrc...");
        File metaFile = new File(getFilesDir(), "meta.7z");
        File etaFile = new File(getFilesDir(), "eta7.7z");
        if (metaFile.exists()) metaFile.delete();
        if (etaFile.exists()) etaFile.delete();
        
        final String rclonePath = getNativeLibPath("librclone.so");
        new Thread(() -> {
            try {
                String[] candidates = new String[]{"meta.7z", "eta7.7z", "ETA7.7z"};
                File downloadedArchive = null;
                int lastExitCode = -1;
                for (String candidate : candidates) {
                    String archiveUrl = (config.baseUri.endsWith("/") ? config.baseUri + candidate : config.baseUri + "/" + candidate);
                    File outArchive = new File(getFilesDir(), candidate.toLowerCase(Locale.US));
                    if (outArchive.exists()) outArchive.delete();
                    ProcessBuilder pb = new ProcessBuilder(
                            rclonePath,
                            "copyurl",
                            archiveUrl,
                            outArchive.getAbsolutePath(),
                            "--config", "/dev/null",
                            "--no-check-certificate",
                            "--progress",
                            "--stats", "1s",
                            "--stats-one-line"
                    );
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String l = line;
                        runOnUiThread(() -> {
                            if (syncStatusText != null) syncStatusText.setText("Resyncing from VrSrc...\n" + l);
                        });
                    }
                    lastExitCode = p.waitFor();
                    if (lastExitCode == 0 && outArchive.exists() && outArchive.length() > 0) {
                        downloadedArchive = outArchive;
                        break;
                    }
                }
                if (downloadedArchive != null) {
                    MainActivity.this.extractMeta(config, downloadedArchive);
                } else {
                    showError("Sync failed (" + lastExitCode + ") - could not fetch meta.7z or eta7.7z");
                }
            } catch (Exception e) {
                showError("Sync error: " + e.getMessage());
            }
        }).start();
    }

    private String decodeArchivePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) return Config.DEFAULT_PASSWORD;
        return rawPassword;
    }

    public void extractMeta(final VrpConfig config, final File archiveFile) {
        updateStatus("Extracting game list...\nETA: --");
        final File metaFile = archiveFile;
        final File outDir = new File(getFilesDir(), ".meta");
        final File tmpOutDir = new File(getFilesDir(), ".meta_new");
        if (tmpOutDir.exists()) deleteRecursive(tmpOutDir);
        tmpOutDir.mkdirs();
        final String p7zPath = getNativeLibPath("lib7z.so");
        final String password = decodeArchivePassword(config.password);

        new Thread(() -> {
            StringBuilder stdoutBuf = new StringBuilder();
            StringBuilder stderrBuf = new StringBuilder();
            try {
                if (!metaFile.exists() || metaFile.length() < 32) {
                    showError("Archive missing or too small: " + metaFile.getAbsolutePath() + " (size=" + (metaFile.exists() ? metaFile.length() : -1) + ")");
                    return;
                }
                byte[] magic = new byte[6];
                try (java.io.FileInputStream fis = new java.io.FileInputStream(metaFile)) {
                    int read = fis.read(magic);
                    if (read < 6 || !(magic[0] == 0x37 && magic[1] == 0x7A && (magic[2] & 0xFF) == 0xBC
                            && (magic[3] & 0xFF) == 0xAF && magic[4] == 0x27 && magic[5] == 0x1C)) {
                        StringBuilder hex = new StringBuilder();
                        for (int i = 0; i < read; i++) hex.append(String.format("%02X ", magic[i] & 0xFF));
                        showError("Downloaded file is NOT a .7z archive.\nFirst bytes: " + hex + "\nSize: " + metaFile.length() + "\nPath: " + metaFile.getAbsolutePath());
                        return;
                    }
                }

                ProcessBuilder pb = new ProcessBuilder(
                        p7zPath,
                        "x",
                        metaFile.getAbsolutePath(),
                        "-p" + password,
                        "-o" + tmpOutDir.getAbsolutePath(),
                        "-y",
                        "-bsp1"
                );
                pb.environment().put("LD_LIBRARY_PATH", getApplicationInfo().nativeLibraryDir);
                Process p = pb.start();
                final long extractStart = System.currentTimeMillis();
                final long[] firstProgressTime = {0};

                Thread errThread = new Thread(() -> {
                    try (BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String el;
                        while ((el = er.readLine()) != null) {
                            stderrBuf.append(el).append('\n');
                            Log.w("SquireExtract", "stderr: " + el);
                        }
                    } catch (Exception ignored) {}
                });
                errThread.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String l = line.trim();
                    stdoutBuf.append(l).append('\n');
                    Log.d("SquireExtract", "stdout: " + l);
                    int pct = -1;
                    try {
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{1,3})%").matcher(l);
                        if (m.find()) pct = Integer.parseInt(m.group(1));
                    } catch (Exception ignored) {}
                    final int pFinal = pct;
                    runOnUiThread(() -> {
                        String statusText;
                        if (pFinal >= 0 && pFinal <= 100) {
                            // Record first progress time for accurate ETA
                            if (firstProgressTime[0] == 0 && pFinal > 0) {
                                firstProgressTime[0] = System.currentTimeMillis();
                            }
                            
                            // Calculate ETA based on simple linear extrapolation
                            int eta = 0;
                            if (pFinal > 0 && pFinal < 100 && firstProgressTime[0] > 0) {
                                long now = System.currentTimeMillis();
                                double elapsedSeconds = (now - firstProgressTime[0]) / 1000.0;
                                
                                if (elapsedSeconds > 0.1) { // Calculate after 0.1 seconds
                                    double percentPerSecond = pFinal / elapsedSeconds;
                                    // Use a minimum rate of 0.1% per second if actual rate is too slow
                                    double effectiveRate = Math.max(percentPerSecond, 0.1);
                                    eta = (int) Math.round((100 - pFinal) / effectiveRate);
                                }
                            }
                            
                            // Ensure ETA is at least 1 second if we have progress
                            if (pFinal > 0 && pFinal < 100 && eta < 1) {
                                eta = 1;
                            }
                            
                            // Show time in seconds or minutes
                            String timeStr;
                            if (eta < 60) {
                                timeStr = eta + "s";
                            } else {
                                timeStr = (eta / 60) + "min";
                            }
                            statusText = "Extracting games list " + pFinal + "% " + timeStr;
                        } else if (!l.isEmpty()) {
                            statusText = "Extracting games list";
                        } else {
                            statusText = "Extracting games list";
                        }
                        if (syncStatusText != null) syncStatusText.setText(statusText);
                        if (statusText != null) MainActivity.this.statusText.setText(statusText);
                    });
                }
                int exitCode = p.waitFor();
                try { errThread.join(2000); } catch (Exception ignored) {}

                File expectedFile = findGameListFile(tmpOutDir);
                if (expectedFile == null) expectedFile = findGameListFile(getFilesDir());

                int retryExitCode = -1;
                if (expectedFile == null) {
                    ProcessBuilder retryPb = new ProcessBuilder(
                            p7zPath,
                            "x",
                            metaFile.getAbsolutePath(),
                            "-o" + tmpOutDir.getAbsolutePath(),
                            "-y",
                            "-bsp1"
                    );
                    retryPb.environment().put("LD_LIBRARY_PATH", getApplicationInfo().nativeLibraryDir);
                    Process retry = retryPb.start();
                    retryExitCode = retry.waitFor();
                    expectedFile = findGameListFile(tmpOutDir);
                    if (expectedFile == null) expectedFile = findGameListFile(getFilesDir());
                }

                if (expectedFile != null && expectedFile.exists()) {
                    File resolvedMeta = expectedFile.getParentFile();
                    if (resolvedMeta != null && !resolvedMeta.equals(tmpOutDir) && resolvedMeta.getParentFile() != null && resolvedMeta.getParentFile().equals(tmpOutDir)) {
                        // File is inside a subfolder of tmpOutDir; that's fine, keep tmpOutDir as is.
                    }
                    if (outDir.exists()) deleteRecursive(outDir);
                    if (!tmpOutDir.renameTo(outDir)) {
                        showError("Extraction finished, but failed to replace metadata folder");
                        return;
                    }
                    MainActivity.this.parseGameList(config);
                } else {
                    String tree = listDirTreeForDebug(tmpOutDir, 0);
                    if (tmpOutDir.exists()) deleteRecursive(tmpOutDir);
                    String err = (stderrBuf.length() > 0 ? stderrBuf.toString() : stdoutBuf.toString());
                    if (err.length() > 800) err = err.substring(0, 800) + "...";
                    showError("7z failed (exit=" + exitCode + ", retry=" + retryExitCode + ")\n\n7z output:\n" + err + "\n\nExtracted tree:\n" + (tree.isEmpty() ? "(empty)" : tree));
                }
            } catch (Throwable e) {
                showError("Extraction error: " + e.getMessage());
            }
        }).start();
    }

    private String listDirTreeForDebug(File dir, int depth) {
        if (dir == null || !dir.exists()) return "";
        StringBuilder sb = new StringBuilder();
        File[] files = dir.listFiles();
        if (files == null) return "";
        for (File f : files) {
            for (int i = 0; i < depth; i++) sb.append("  ");
            sb.append(f.isDirectory() ? "[" : "").append(f.getName()).append(f.isDirectory() ? "]" : "").append('\n');
            if (f.isDirectory() && depth < 3) sb.append(listDirTreeForDebug(f, depth + 1));
        }
        return sb.toString();
    }

    private void startDownload(Game game) {
        if (this.currentDownloadingGame != null) {
            this.downloadQueue.add(game);
            showQueueNotification(game.gameName);
            return;
        }
        
        this.currentDownloadingGame = game;
        showCustomProgressDialog(game.gameName);
        
        // Calculate size
        long totalBytes;
        try {
            String cleanSize = game.sizeMb.trim().toUpperCase();
            double val = Double.parseDouble(cleanSize.replace(" GB", "").replace(" MB", "").trim());
            totalBytes = cleanSize.contains("GB") ? (long) (val * 1024.0d * 1024.0d * 1024.0d) : (long) (val * 1024.0d * 1024.0d);
        } catch (Exception e) {
            totalBytes = 2147483648L; // 2GB default
        }
        
        long requiredSpace = 2 * totalBytes;
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        
        if (availableSpace < requiredSpace) {
            dismissDialog();
            double availableGb = availableSpace / 1.073741824E9d;
            double requiredGb = requiredSpace / 1.073741824E9d;
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
        
        dlBgStatusBar.setVisibility(0);
        dlBgStatusBar.setAlpha(1.0f);
        // bgStatusViewBtn.setVisibility(0);
        int queueSize = downloadQueue.size();
        dlBgStatusText.setText("Downloading " + currentDownloadingGame.gameName);
        dlBgStatusDetail.setText("0% Completed (Queue: " + queueSize + ")");
    }
    
    private void showQueueNotification(String gameName) {
        if (queueNotification == null) return;
        queueNotification.setText(gameName.toUpperCase() + " ADDED TO QUEUE");
        queueNotification.setVisibility(0);
        queueNotification.setAlpha(0.0f);
        queueNotification.animate().alpha(1.0f).setDuration(400L).withEndAction(() -> {
             new Handler().postDelayed(() -> {
                 queueNotification.animate().alpha(0.0f).setDuration(600L).withEndAction(() -> {
                     queueNotification.setVisibility(8);
                 }).start();
             }, 5000L);
        }).start();
    }

    private void processNextInQueue() {
        this.currentDownloadingGame = null;
        runOnUiThread(() -> {
            dlBgStatusBar.setVisibility(8);
            // bgStatusViewBtn.setVisibility(8);
        });
        if (!downloadQueue.isEmpty()) {
            final Game next = downloadQueue.poll();
            new Handler(Looper.getMainLooper()).postDelayed(() -> startDownload(next), 1000L);
        }
    }
    
    private void showFinalSummary() {
        if (sessionCompletedGames.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        sb.append("✅ All downloads finished!\n\n");
        sb.append("Location: /sdcard/Download/Squire Vr Games/\n\n");
        sb.append("Games:\n");
        for (String g : sessionCompletedGames) sb.append("- ").append(g).append("\n");
        
        new AlertDialog.Builder(this).setTitle("Games Done").setMessage(sb.toString()).setPositiveButton("Awesome", (d, i) -> {
            sessionCompletedGames.clear();
        }).show();
    }

    private void loadGamesFromCache(File gameListFile) {
        metadataSyncInProgress = true;
        updateStatus("Loading games from cache...");
        new Thread(() -> {
            try {
                final VrpConfig config = new VrpConfig();
                config.baseUri = Config.MIRROR_BASEURI_URL;
                config.password = Config.DEFAULT_PASSWORD;
                
                parseGameList(config);
                
                runOnUiThread(() -> {
                    MainActivity.this.updateStatus("Games loaded from cache");
                    finishMetadataSyncUi();
                });
            } catch (Throwable e) {
                runOnUiThread(() -> {
                    showError("Failed to load from cache: " + e.getMessage());
                    metadataSyncInProgress = false;
                });
            }
        }).start();
    }

    private void parseGameList(VrpConfig config) throws Throwable {
        File listFile = new File(getFilesDir(), "GameList.txt");
        if (!listFile.exists()) listFile = new File(getFilesDir(), "VRP-GameList.txt");
        if (!listFile.exists()) listFile = new File(getFilesDir(), ".meta/GameList.txt");
        if (!listFile.exists()) listFile = new File(getFilesDir(), ".meta/VRP-GameList.txt");
        if (!listFile.exists()) {
            File found = findGameListFile(new File(getFilesDir(), ".meta"));
            if (found != null) listFile = found;
        }
        
        Scanner scanner = new Scanner(listFile);
        List<Game> games = new ArrayList<>();
        
        // Default indices based on standard VRP/Rookie format
        int nameIdx = 0;
        int releaseIdx = 1;
        int pkgIdx = 2;
        int verIdx = 3;
        int dateIdx = -1; // Usually dynamic
        int sizeIdx = 4;
        int popIdx = 5; 
        
        if (scanner.hasNextLine()) {
            String firstLine = scanner.nextLine();
            // Handle BOM
            if (firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1);
            }
            
            // Heuristic to detect header: Check for known column names
            if (firstLine.toLowerCase().contains("game name") && firstLine.toLowerCase().contains("package name")) {
                // It is a header! Parse indices dynamically.
                String[] cols = firstLine.split(";", -1);
                
                // Reset to -1 to detect what we actually find
                nameIdx = -1; releaseIdx = -1; pkgIdx = -1; verIdx = -1;
                dateIdx = -1; sizeIdx = -1; popIdx = -1;
                
                for (int i = 0; i < cols.length; i++) {
                    String c = cols[i].trim().toLowerCase(Locale.US);
                    if (c.contains("game name") || c.equals("name") || c.equals("title")) nameIdx = i;
                    else if (c.contains("release name") || c.equals("release")) releaseIdx = i;
                    else if (c.contains("package")) pkgIdx = i;
                    else if (c.contains("version")) verIdx = i;
                    else if (c.contains("updated") || c.contains("last update") || c.contains("date")) dateIdx = i;
                    else if (c.contains("size")) sizeIdx = i;
                    else if (c.contains("downloads") || c.contains("popularity") || c.contains("rating")) popIdx = i;
                }
                
                // Restore essential defaults if somehow missed (unlikely if header exists)
                if (nameIdx == -1) nameIdx = 0;
                if (releaseIdx == -1) releaseIdx = 1;
                if (pkgIdx == -1) pkgIdx = 2;
                if (verIdx == -1) verIdx = 3;
            } else {
                // Not a header! Treat as the first game line.
                // Attempt to auto-detect layout (Date vs No Date)
                String[] parts = firstLine.split(";", -1);
                // Check index 4: Is it a Date (contains "-") or Size (Number/MB)?
                if (parts.length > 4) {
                    String p4 = parts[4].trim();
                    if (p4.contains("-") && p4.length() > 5) {
                        // Looks like a date: 2023-01-01
                        dateIdx = 4;
                        sizeIdx = 5;
                        popIdx = 6;
                    } else {
                        // Assume standard Rookie format
                        sizeIdx = 4;
                        popIdx = 5;
                    }
                }
                parseGameLine(firstLine, games, config, nameIdx, releaseIdx, pkgIdx, verIdx, dateIdx, sizeIdx, popIdx);
            }
        }
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parseGameLine(line, games, config, nameIdx, releaseIdx, pkgIdx, verIdx, dateIdx, sizeIdx, popIdx);
        }
        scanner.close();
        
        // DEDUPLICATION: Use ReleaseName as unique key (like Rookie)
        // This prevents removing distinct versions/mods that share a packageName,
        // but filters out exact duplicate entries in the list.
        Map<String, Game> uniqueGames = new HashMap<>();
        for (Game g : games) {
            if (g.releaseName == null || g.releaseName.isEmpty()) continue;
            
            // If duplicate releaseName, keep the one with popularity score if possible
            if (uniqueGames.containsKey(g.releaseName)) {
                Game existing = uniqueGames.get(g.releaseName);
                if (g.popularityScore > existing.popularityScore) {
                    uniqueGames.put(g.releaseName, g);
                }
            } else {
                uniqueGames.put(g.releaseName, g);
            }
        }
        games = new ArrayList<>(uniqueGames.values());
        
        // Merging logic
        if (this.allGames == null) {
            this.allGames = new ArrayList(games);
        } else {
            Map<String, Game> gameMap = new HashMap<>();
            for (Game existing : this.allGames) {
                gameMap.put(existing.packageName, existing);
            }
            for (Game newGame : games) {
                if (gameMap.containsKey(newGame.packageName)) {
                    Game existing = gameMap.get(newGame.packageName);
                    if (existing.thumbnailPath != null) newGame.thumbnailPath = existing.thumbnailPath;
                    if (existing.noteContent != null) newGame.noteContent = existing.noteContent;
                }
            }
            this.allGames = new ArrayList(games);
        }
        loadRatingsForGames(this.allGames);
        loadCloudRatings();
        
        // Start background caching
        new Thread(() -> {
            try {
                File metaDir = new File(getFilesDir(), ".meta");
                
                // OPTIMIZATION: Index folders recursively to find everything (thumbnails, etc)
                // This restores the "clean" loading time and fixes missing thumbnails if they are nested.
                Map<String, File> folderIndex = new HashMap<>();
                Queue<File> queue = new LinkedList<>();
                if (metaDir.exists()) queue.add(metaDir);
                
                int foldersChecked = 0;
                while (!queue.isEmpty() && foldersChecked < 20000) {
                    foldersChecked++;
                    File current = queue.poll();
                    if (current != null) {
                        folderIndex.put(current.getName().toLowerCase(), current);
                        File[] files = current.listFiles();
                        if (files != null) {
                            for (File f : files) if (f.isDirectory()) queue.add(f);
                        }
                    }
                }
                
                File thumbnails = folderIndex.get("thumbnails");
                File notes = folderIndex.get("notes");
                
                cacheInProgress = true;
                cachePct = 0;
                runOnUiThread(() -> {
                    if (adapter != null) adapter.notifyDataSetChanged();
                    updateTabLabels();
                    filterGames(currentQuery);
                    
                    // IMPORTANT: Hide "Extracting..." / Loading status immediately
                    View syncOverlay = (View) loader.getParent();
                    if (syncOverlay != null) syncOverlay.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    finishMetadataSyncUi();
                    
                    bgStatusBar.setVisibility(0);
                    bgLoader.setIndeterminate(false);
                    bgStatusText.setText("Caching library");
                });
                
                for(int i=0; i<allGames.size(); i++) {
                    Game g = allGames.get(i);
                    // Thumbnail/Notes logic using indexed map if possible, or direct file check if path known
                    if (g.thumbnailPath == null) {
                        if (thumbnails != null) {
                            File jpg = new File(thumbnails, g.packageName + ".jpg");
                            if (jpg.exists()) g.thumbnailPath = jpg.getAbsolutePath();
                            else {
                                File png = new File(thumbnails, g.packageName + ".png");
                                if (png.exists()) g.thumbnailPath = png.getAbsolutePath();
                            }
                        }
                    }
                    if (g.noteContent == null) {
                        if (notes != null) {
                            File note = new File(notes, g.releaseName + ".txt");
                            if (note.exists()) g.noteContent = readNoteContent(note);
                        }
                    }
                    
                    // Date update from folder metadata?
                    // Original code used findFolder(metaDir, g.releaseName) for EVERY game.
                    // Now we use the index.
                    if (folderIndex.containsKey(g.releaseName.toLowerCase())) {
                         File gameFolder = folderIndex.get(g.releaseName.toLowerCase());
                         // Logic to check file dates...
                    }
                    
                    cachePct = (i * 100) / allGames.size();
                    if (i % 50 == 0) {
                        final int p = cachePct;
                        runOnUiThread(() -> {
                             bgLoader.setProgress(p);
                             bgStatusDetail.setText(p + "%");
                        });
                    }
                }
                
                cachePct = 100;
                cacheInProgress = false;
                runOnUiThread(() -> fadeOutView(bgStatusBar));
                saveCache();
                
            } catch(Exception e) {}
        }).start();
    }

    private static double parseMetricNumber(String raw) {
        if (raw == null) return -1d;
        String s = raw.trim().toLowerCase(Locale.US).replace(",", "");
        if (s.isEmpty() || s.equals("n/a") || s.equals("-")) return -1d;
        double mul = 1d;
        if (s.endsWith("k")) {
            mul = 1_000d;
            s = s.substring(0, s.length() - 1).trim();
        } else if (s.endsWith("m")) {
            mul = 1_000_000d;
            s = s.substring(0, s.length() - 1).trim();
        } else if (s.endsWith("b")) {
            mul = 1_000_000_000d;
            s = s.substring(0, s.length() - 1).trim();
        }
        String cleaned = s.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) return -1d;
        try {
            return Double.parseDouble(cleaned) * mul;
        } catch (Exception ignored) {
            return -1d;
        }
    }

    private static String tryExtractDate(String[] parts, int preferredIdx) {
        if (preferredIdx >= 0 && preferredIdx < parts.length) {
            String p = parts[preferredIdx].trim();
            if (p.matches("\\d{4}-\\d{2}-\\d{2}.*")) return p.substring(0, 10);
        }
        for (String pRaw : parts) {
            String p = pRaw.trim();
            if (p.matches("\\d{4}-\\d{2}-\\d{2}.*")) return p.substring(0, 10);
        }
        return "-";
    }

    private void parseGameLine(String line, List<Game> games, VrpConfig config, 
                               int nameIdx, int releaseIdx, int pkgIdx, int verIdx, 
                               int dateIdx, int sizeIdx, int popIdx) {
        String[] parts = line.split(";", -1);
        int maxIdx = Math.max(nameIdx, Math.max(releaseIdx, Math.max(pkgIdx, Math.max(verIdx, Math.max(dateIdx, Math.max(sizeIdx, popIdx))))));
        
        if (parts.length > maxIdx || parts.length >= 4) { // At least basic info
            try {
                Game g = new Game();
                // Safe extraction helper
                g.gameName = parts.length > nameIdx ? parts[nameIdx].trim() : "Unknown";
                g.releaseName = parts.length > releaseIdx ? parts[releaseIdx].trim() : "";
                g.packageName = parts.length > pkgIdx ? parts[pkgIdx].trim() : "";
                g.version = parts.length > verIdx ? parts[verIdx].trim() : "";
                if (g.gameName.isEmpty() || "Unknown".equalsIgnoreCase(g.gameName)) {
                    g.gameName = !g.releaseName.isEmpty() ? g.releaseName : g.packageName;
                }
                if (g.version == null || g.version.trim().isEmpty()) {
                    g.version = "-";
                }
                
                // Parse Date
                g.date = tryExtractDate(parts, dateIdx);
                
                // Parse Size
                g.sizeBytes = 0;
                g.sizeMb = "-";
                if (sizeIdx != -1 && parts.length > sizeIdx) {
                    String pSize = parts[sizeIdx].trim();
                    try {
                        String upper = pSize.toUpperCase(Locale.US);
                        String cleanSize = pSize.replaceAll("[^0-9.]", "");
                        if (!cleanSize.isEmpty()) {
                            double val = Double.parseDouble(cleanSize);
                            if (upper.contains("GB")) {
                                g.sizeBytes = (long) (val * 1024d * 1024d * 1024d);
                            } else if (upper.contains("MB")) {
                                g.sizeBytes = (long) (val * 1024d * 1024d);
                            } else if (upper.contains("KB")) {
                                g.sizeBytes = (long) (val * 1024d);
                            } else if (val > 1024d * 1024d * 10d) {
                                // Usually raw bytes in list if very large.
                                g.sizeBytes = (long) val;
                            } else {
                                // Usually MB in older lists.
                                g.sizeBytes = (long) (val * 1024d * 1024d);
                            }
                            if (g.sizeBytes >= 1024L * 1024L * 1024L) {
                                g.sizeMb = String.format(Locale.US, "%.2f GB", g.sizeBytes / (1024d * 1024d * 1024d));
                            } else if (g.sizeBytes > 0) {
                                g.sizeMb = String.format(Locale.US, "%.0f MB", g.sizeBytes / (1024d * 1024d));
                            } else {
                                g.sizeMb = pSize;
                            }
                        } else {
                            g.sizeMb = pSize;
                        }
                    } catch (Exception e) {
                        g.sizeMb = pSize;
                    }
                }
                
                g.popularityScore = 0;
                if (popIdx != -1 && parts.length > popIdx) {
                    double pop = parseMetricNumber(parts[popIdx]);
                    if (pop > 0) g.popularityScore = pop;
                }
                
                // FALLBACK: pick the biggest valid metric-like number outside known columns (usually downloads).
                if (g.popularityScore == 0) {
                    double best = -1d;
                    for (int i = 0; i < parts.length; i++) {
                        if (i == nameIdx || i == releaseIdx || i == pkgIdx || i == verIdx || i == dateIdx || i == sizeIdx) continue;
                        double val = parseMetricNumber(parts[i]);
                        if (val > best) best = val;
                    }
                    if (best > 0) g.popularityScore = best;
                }
                if (g.popularityScore <= 0) {
                    g.popularityScore = 0;
                }
                
                g.md5Hash = g.releaseName;
                g.baseUri = config.baseUri;
                g.password = config.password;
                
                if (favoriteGames != null) {
                    g.isFavorite = favoriteGames.contains(g.packageName);
                }
                
                // STABLE ID
                g.stableId = Objects.hash(g.releaseName, games.size());
                
                if (!g.gameName.isEmpty() && !g.packageName.isEmpty() && !"-".equals(g.packageName)) {
                    games.add(g);
                }
            } catch (Exception e) {
                // Skip malformed lines
            }
        }
    }

    private void updateTabUI() {
        int activeColor = -16745729;
        int inactiveColor = -13421773;
        this.tabAll.setBackgroundTintList(ColorStateList.valueOf(this.currentTab == 0 ? activeColor : inactiveColor));
        this.tabInstalled.setBackgroundTintList(ColorStateList.valueOf(this.currentTab == 1 ? activeColor : inactiveColor));
        this.tabUpdates.setBackgroundTintList(ColorStateList.valueOf(this.currentTab == 2 ? activeColor : inactiveColor));
        this.tabFavorites.setBackgroundTintList(ColorStateList.valueOf(this.currentTab == 3 ? activeColor : inactiveColor));
        this.tabUncrackable.setBackgroundTintList(ColorStateList.valueOf(this.currentTab == 4 ? activeColor : inactiveColor));
    }

    public void filterGames(String query) {
        this.currentQuery = query;
        if (this.allGames == null) return;
        List<Game> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (Game g : this.allGames) {
            boolean matches = true;
            if (!lowerQuery.isEmpty()) {
                matches = g.gameName.toLowerCase().contains(lowerQuery);
            }
            if (matches) {
                if (this.currentTab == 1 && !isPackageInstalled(g.packageName)) {
                    matches = false;
                } else if (this.currentTab == 2 && (!isPackageInstalled(g.packageName) || !g.needsUpdate)) {
                    matches = false;
                } else if (this.currentTab == 3 && !g.isFavorite) {
                    matches = false;
                } else if (this.currentTab == 4 && !this.uncrackablePackages.contains(g.packageName)) {
                    matches = false;
                }
            }
            if (matches) {
                filtered.add(g);
            }
        }
        
        Collections.sort(filtered, new Comparator<Game>() {
            @Override
            public int compare(Game g1, Game g2) {
                if (sortMode == 1) { // Newest
                     String d1 = g1.date != null ? g1.date : "";
                     String d2 = g2.date != null ? g2.date : "";
                     return d2.compareTo(d1);
                } else if (sortMode == 3) { // Size
                     return Long.compare(g2.sizeBytes, g1.sizeBytes);
                } else if (sortMode == 2) { // Popularity
                     return Double.compare(g2.popularityScore, g1.popularityScore);
                } else { // Default
                     String n1 = g1.gameName;
                     String n2 = g2.gameName;
                     boolean u1 = n1.startsWith("_");
                     boolean u2 = n2.startsWith("_");
                     if (u1 && !u2) return -1;
                     if (!u1 && u2) return 1;
                     if (u1 && u2) return n1.compareToIgnoreCase(n2);
                     boolean d1 = Character.isDigit(n1.charAt(0));
                     boolean d2 = Character.isDigit(n2.charAt(0));
                     if (d1 && !d2) return -1;
                     if (!d1 && d2) return 1;
                     return String.CASE_INSENSITIVE_ORDER.compare(n1, n2);
                }
            }
        });
        
        if (this.adapter != null) {
            this.adapter.setSkeletonMode(false, 0);
            this.adapter.updateList(filtered, this.currentTab == 1, this.currentTab == 2);
        }
    }

    private void saveCache() {
        try {
            File cacheFile = new File(getFilesDir(), "cached_games.txt");
            FileWriter writer = new FileWriter(cacheFile);
            List<Game> list = this.allGames;
            if (list != null) {
                for (Game game : list) {
                    writer.write(game.gameName + ";" + game.releaseName + ";" + game.packageName + ";" + game.version + ";" + game.date + ";" + game.sizeMb + ";" + game.thumbnailPath + ";" + game.md5Hash + ";" + game.baseUri + ";" + game.password + ";" + game.popularityScore + ";" + game.sizeBytes + ";" + (game.isFavorite ? "1" : "0") + "\n");
                }
            }
            writer.close();
            prefs.edit().putBoolean("initial_cache_done", true).apply();
        } catch (Exception e) {}
    }

    private void loadCache() {
        try {
            File cacheFile = new File(getFilesDir(), "cached_games.txt");
            if (cacheFile.exists()) {
                Scanner s = new Scanner(cacheFile);
                List<Game> games = new ArrayList<>();
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    String[] parts = line.split(";");
                    if (parts.length >= 6) {
                        Game g = new Game();
                        g.gameName = parts[0].trim();
                        g.releaseName = parts[1];
                        g.packageName = parts[2].trim();
                        g.version = parts[3].trim();
                        g.date = parts[4].trim();
                        g.sizeMb = parts[5].trim();
                        if (parts.length >= 7) {
                            String t = parts[6].trim();
                            if (!t.equals("null")) g.thumbnailPath = t;
                        }
                        if (parts.length >= 8) {
                            String m = parts[7].trim();
                            if (!m.equals("null")) g.md5Hash = m;
                        }
                        if (parts.length >= 9) {
                            String b = parts[8].trim();
                            if (!b.equals("null")) g.baseUri = b;
                        }
                        if (parts.length >= 10) {
                            String p = parts[9].trim();
                            if (!p.equals("null")) g.password = p;
                        }
                        if (parts.length >= 11) {
                            try { g.popularityScore = Double.parseDouble(parts[10].trim()); } catch (Exception e) {}
                        }
                        if (parts.length >= 12) {
                            try { g.sizeBytes = Long.parseLong(parts[11].trim()); } catch (Exception e) {}
                        }
                        if (parts.length >= 13) {
                            g.isFavorite = "1".equals(parts[12].trim());
                        }
                        
                        // Stable ID
                        g.stableId = Objects.hash(g.releaseName, games.size());
                        
                        games.add(g);
                    }
                }
                s.close();
                this.allGames = games;
                loadRatingsForGames(this.allGames);
                runOnUiThread(() -> {
                    if (adapter == null) setupRecyclerView();
                    updateTabLabels();
                    filterGames(currentQuery);
                    View syncOverlay = (View) loader.getParent();
                    if (syncOverlay != null) syncOverlay.setVisibility(View.GONE);
                    loader.setVisibility(8);
                    finishMetadataSyncUi();
                });
                loadCloudRatings();
            }
        } catch (Exception e) {}
    }

    public void deleteRecursive(File file) {
        File[] children;
        if (file.isDirectory() && (children = file.listFiles()) != null) {
            for (File child : children) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }
    
    private String readNoteContent(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            br.close();
            return sb.toString().trim();
        } catch (Exception e) { return null; }
    }
    
    // Kept for compatibility but optimized logic is now inline in parseGameList
    private File findFolder(File root, String name) {
        if (root == null || !root.exists() || !root.isDirectory()) return null;
        Queue<File> queue = new LinkedList<>();
        queue.add(root);
        int foldersChecked = 0;
        while (!queue.isEmpty() && foldersChecked < 5000) {
            foldersChecked++;
            File current = queue.poll();
            if (current != null) {
                if (current.getName().equalsIgnoreCase(name)) return current;
                File[] files = current.listFiles();
                if (files != null) {
                    for (File f : files) if (f.isDirectory()) queue.add(f);
                }
            }
        }
        return null;
    }
    
    private String normalizeVersion(String v) {
        if (v == null) return "";
        String strTrim = v.trim();
        if (strTrim.startsWith("v") || strTrim.startsWith("V")) strTrim = strTrim.substring(1);
        return strTrim.trim();
    }
    
    private void loadConfig() {
        SharedPreferences prefs = getSharedPreferences("app_config", MODE_PRIVATE);
        Config.MIRROR_BASEURI_URL = prefs.getString("baseuri", "");
        Config.DEFAULT_PASSWORD = prefs.getString("password", "");
    }
    
    private void saveConfig(String baseuri, String password) {
        SharedPreferences prefs = getSharedPreferences("app_config", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("baseuri", baseuri);
        editor.putString("password", password);
        editor.apply();
        Config.MIRROR_BASEURI_URL = baseuri;
        Config.DEFAULT_PASSWORD = password;
    }
    
    private void showConfigDialog() {
        if (isConfigDialogShowing) return;
        isConfigDialogShowing = true;
        
        // Create custom dialog without AlertDialog wrapper for sleek look
        final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(false);
        
        // Main container with dark background
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        container.setBackgroundColor(Color.parseColor("#E6000000")); // 90% black
        
        // Card layout
        android.widget.LinearLayout card = new android.widget.LinearLayout(this);
        card.setOrientation(android.widget.LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.parseColor("#1A1A1A"));
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        card.setPadding(padding, padding, padding, padding);
        
        // Title
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("Config Setup");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);
        
        // Subtitle
        android.widget.TextView subtitle = new android.widget.TextView(this);
        subtitle.setText("Enter your VrSrc configuration");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.parseColor("#888888"));
        subtitle.setPadding(0, 8, 0, 0);
        card.addView(subtitle);
        
        // Base URI section
        android.widget.TextView baseuriLabel = new android.widget.TextView(this);
        baseuriLabel.setText("Base URI");
        baseuriLabel.setTextSize(14);
        baseuriLabel.setTextColor(Color.parseColor("#BBBBBB"));
        baseuriLabel.setPadding(0, 24, 0, 8);
        card.addView(baseuriLabel);
        
        android.widget.EditText baseuriInput = new android.widget.EditText(this);
        baseuriInput.setHint("https://example.com/");
        baseuriInput.setSingleLine(true);
        baseuriInput.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_NEXT);
        baseuriInput.setBackgroundColor(Color.parseColor("#2A2A2A"));
        baseuriInput.setTextColor(Color.parseColor("#FFFFFF"));
        baseuriInput.setHintTextColor(Color.parseColor("#666666"));
        baseuriInput.setPadding(16, 16, 16, 16);
        card.addView(baseuriInput);
        
        // Password section
        android.widget.TextView passLabel = new android.widget.TextView(this);
        passLabel.setText("Password");
        passLabel.setTextSize(14);
        passLabel.setTextColor(Color.parseColor("#BBBBBB"));
        passLabel.setPadding(0, 16, 0, 8);
        card.addView(passLabel);
        
        android.widget.EditText passInput = new android.widget.EditText(this);
        passInput.setHint("Enter password");
        passInput.setSingleLine(true);
        passInput.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        passInput.setBackgroundColor(Color.parseColor("#2A2A2A"));
        passInput.setTextColor(Color.parseColor("#FFFFFF"));
        passInput.setHintTextColor(Color.parseColor("#666666"));
        passInput.setPadding(16, 16, 16, 16);
        card.addView(passInput);
        
        // Link section
        android.widget.LinearLayout linkRow = new android.widget.LinearLayout(this);
        linkRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        linkRow.setPadding(0, 20, 0, 0);
        
        android.widget.TextView linkText = new android.widget.TextView(this);
        linkText.setText("Get config at ");
        linkText.setTextSize(13);
        linkText.setTextColor(Color.parseColor("#888888"));
        linkRow.addView(linkText);
        
        android.widget.TextView configLink = new android.widget.TextView(this);
        configLink.setText("Telegram");
        configLink.setTextSize(13);
        configLink.setTextColor(Color.parseColor("#0088CC"));
        configLink.setTypeface(null, android.graphics.Typeface.BOLD);
        configLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/the_vrSrc/2"));
                startActivity(intent);
            }
        });
        linkRow.addView(configLink);
        card.addView(linkRow);
        
        // Save button
        android.widget.Button saveBtn = new android.widget.Button(this);
        saveBtn.setText("Save & Continue");
        saveBtn.setTextColor(Color.parseColor("#FFFFFF"));
        saveBtn.setBackgroundColor(Color.parseColor("#007AFF"));
        saveBtn.setPadding(0, 16, 0, 16);
        android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.topMargin = 24;
        saveBtn.setLayoutParams(btnParams);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String baseuri = baseuriInput.getText().toString().trim();
                String encodedPass = passInput.getText().toString().trim();
                
                if (baseuri.isEmpty() || encodedPass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String decodedPass = decodeBase64(encodedPass);
                saveConfig(baseuri, decodedPass);
                dialog.dismiss();
                isConfigDialogShowing = false;
                fetchGames();
            }
        });
        card.addView(saveBtn);
        
        // Center card in container
        android.widget.FrameLayout.LayoutParams cardParams = new android.widget.FrameLayout.LayoutParams(
            (int) (320 * getResources().getDisplayMetrics().density),
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.gravity = android.view.Gravity.CENTER;
        card.setLayoutParams(cardParams);
        
        container.addView(card);
        dialog.setContentView(container);
        
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isConfigDialogShowing = false;
            }
        });
        
        dialog.show();
    }
    
    private String decodeBase64(String encoded) {
        try {
            byte[] decodedBytes = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT);
            return new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            Log.e("MainActivity", "Base64 decode failed", e);
            return encoded;
        }
    }
}

package com.squire.vr;

import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
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
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.app.NotificationCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.Key;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squire.vr.AutoInstaller;
import com.squire.vr.GameAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import com.squire.vr.updater.GithubUpdateChecker;
import com.squire.vr.updater.UpdateActivity;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* loaded from: classes3.dex */
public class MainActivity extends AppCompatActivity {
    private GameAdapter adapter;
    private List<Game> allGames;
    private SwitchCompat autoSideloadSwitch;
    private ProgressBar bgLoader;
    private View bgStatusBar;
    private TextView bgStatusDetail;
    private TextView bgStatusText;
    private Button bgStatusViewBtn;
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
    private ProgressBar loader;
    private SharedPreferences prefs;
    private TextView queueNotification;
    private RecyclerView recyclerView;
    private boolean showUpdatesOnly;
    private TextView statusText;
    private Button tabAll;
    private Button tabInstalled;
    private Button tabUpdates;
    private File thumbnailsDir;
    private int currentSortMode = 0;
    private Queue<Game> downloadQueue = new LinkedList();
    private Game currentDownloadingGame = null;
    private List<String> sessionCompletedGames = new ArrayList();
    private StringBuilder sessionSideloadResults = new StringBuilder();
    private boolean showInstalledOnly = false;
    private String currentQuery = HttpUrl.FRAGMENT_ENCODE_SET;
    private Map<String, String> installedPackagesCache = new HashMap();
    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).build();

    private Map buildVrpInstalledVersionsMap() {
        File[] fileArrListFiles;
        HashMap map = new HashMap();
        File file = new File("/sdcard/Download/Squire Vr Games/");
        if (file.exists() && (fileArrListFiles = file.listFiles()) != null) {
            for (File file2 : fileArrListFiles) {
                if (file2.isDirectory()) {
                    parseVrpGameListIntoMap(new File(file2, "VRP-GameList.txt"), map);
                    parseVrpGameListIntoMap(new File(file2, ".meta/VRP-GameList.txt"), map);
                }
            }
        }
        return map;
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

    private String extractVersionFromReleaseName(String releaseName) {
        String strSubstring = HttpUrl.FRAGMENT_ENCODE_SET;
        if (releaseName != null) {
            String strTrim = releaseName.trim();
            int iIndexOf = strTrim.indexOf("-VRP");
            if (iIndexOf >= 0) {
                strTrim = strTrim.substring(0, iIndexOf);
            }
            int iIndexOf2 = strTrim.indexOf("- VRP");
            if (iIndexOf2 >= 0) {
                strTrim = strTrim.substring(0, iIndexOf2);
            }
            String strTrim2 = strTrim.trim();
            int iLastIndexOf = strTrim2.lastIndexOf(" v");
            int iLastIndexOf2 = strTrim2.lastIndexOf(" V");
            if (iLastIndexOf2 > iLastIndexOf) {
                iLastIndexOf = iLastIndexOf2;
            }
            strSubstring = iLastIndexOf >= 0 ? strTrim2.substring(iLastIndexOf + 1) : strTrim2;
        }
        return strSubstring.trim();
    }

    private void fadeOutView(View view) {
        view.animate().alpha(0.0f).setDuration(1000L).withEndAction(new FadeOutRunnable(view)).start();
    }

    private String getInstalledGameDate(String pkgName) {
        if (pkgName != null) {
            if (this.prefs == null) {
                this.prefs = getSharedPreferences("squire_prefs", 0);
            }
            String string = this.prefs.getString("installed_date_" + pkgName, HttpUrl.FRAGMENT_ENCODE_SET);
            if (string != null && string.length() > 0) {
                return string;
            }
        }
        return null;
    }

    private String getInstalledGameVersion(String pkgName) {
        if (pkgName != null) {
            if (this.prefs == null) {
                this.prefs = getSharedPreferences("squire_prefs", 0);
            }
            String string = this.prefs.getString("installed_ver_" + pkgName, HttpUrl.FRAGMENT_ENCODE_SET);
            if (string != null && string.length() > 0) {
                return string;
            }
        }
        return null;
    }

    private long getInstalledVersionCode(String pkgName) throws PackageManager.NameNotFoundException {
        long longVersionCode = -1;
        if (pkgName != null) {
            try {
                longVersionCode = Build.VERSION.SDK_INT >= 28 ? getPackageManager().getPackageInfo(pkgName, 0).getLongVersionCode() : r2.versionCode;
            } catch (Exception e) {
            }
        }
        return longVersionCode;
    }

    private String getInstalledVersionFromVrpGameList(String releaseName, String pkgName) {
        String strTrim = null;
        if (releaseName != null && pkgName != null) {
            File file = new File("/sdcard/Download/Squire Vr Games/" + releaseName + "/VRP-GameList.txt");
            if (file.exists()) {
                try {
                    Scanner scanner = new Scanner(file);
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                        while (true) {
                            if (!scanner.hasNextLine()) {
                                break;
                            }
                            String[] strArrSplit = scanner.nextLine().split(";");
                            if (strArrSplit.length >= 4 && strArrSplit[2].trim().equalsIgnoreCase(pkgName)) {
                                strTrim = strArrSplit[3].trim();
                                break;
                            }
                        }
                    }
                    scanner.close();
                } catch (Exception e) {
                }
            }
        }
        return strTrim;
    }

    private boolean isVersionMismatch(String allVersion, String installedVersion) {
        String strNormalizeVersion = normalizeVersion(allVersion);
        String strNormalizeVersion2 = normalizeVersion(installedVersion);
        return strNormalizeVersion2.length() > 0 && !strNormalizeVersion.equalsIgnoreCase(strNormalizeVersion2);
    }

    private String normalizeVersion(String v) {
        if (v == null) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
        String strTrim = v.trim();
        if (strTrim.startsWith("v") || strTrim.startsWith(ExifInterface.GPS_MEASUREMENT_INTERRUPTED)) {
            strTrim = strTrim.substring(1);
        }
        return strTrim.trim();
    }

    private void parseVrpGameListIntoMap(File file, Map out) {
        if (file == null || out == null || !file.exists()) {
            return;
        }
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] strArrSplit = scanner.nextLine().split(";");
                if (strArrSplit.length >= 5) {
                    String strTrim = strArrSplit[2].trim();
                    if (strTrim.contains(".")) {
                        String strTrim2 = strArrSplit[4].trim();
                        if (strTrim.length() > 0 && strTrim2.length() > 0) {
                            out.put(strTrim, strTrim2);
                        }
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveInstalledGameDate(String pkgName, String date) {
        if (pkgName == null || date == null || date.length() <= 0) {
            return;
        }
        if (this.prefs == null) {
            this.prefs = getSharedPreferences("squire_prefs", 0);
        }
        this.prefs.edit().putString("installed_date_" + pkgName, date).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveInstalledGameVersion(String pkgName, String version) {
        if (pkgName == null || version == null || version.length() <= 0) {
            return;
        }
        if (this.prefs == null) {
            this.prefs = getSharedPreferences("squire_prefs", 0);
        }
        this.prefs.edit().putString("installed_ver_" + pkgName, version).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectUpdatesTab() throws PackageManager.NameNotFoundException {
        this.showInstalledOnly = true;
        this.showUpdatesOnly = true;
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

    private void updateTabLabels() throws PackageManager.NameNotFoundException {
        List<Game> list = this.allGames;
        int size = list != null ? list.size() : 0;
        int i = 0;
        int i2 = 0;
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
                        }
                    }
                    game2.needsUpdate = false;
                } else {
                    game2.needsUpdate = false;
                }
            }
        }
        this.tabAll.setText("All (" + size + ")");
        this.tabInstalled.setText("Installed (" + i + ")");
        setUpdatesTabText(i2);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws PackageManager.NameNotFoundException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.recyclerView = (RecyclerView) findViewById(R.id.game_list);
        this.loader = (ProgressBar) findViewById(R.id.loader);
        this.statusText = (TextView) findViewById(R.id.status_text);
        this.autoSideloadSwitch = (SwitchCompat) findViewById(R.id.auto_sideload_switch);
        this.tabAll = (Button) findViewById(R.id.tab_all);
        this.tabInstalled = (Button) findViewById(R.id.tab_installed);
        this.tabUpdates = (Button) findViewById(R.id.tab_updates);
        this.btnSort = (Button) findViewById(R.id.btn_sort);
        this.btnQueue = (Button) findViewById(R.id.btn_queue);
        this.tabAll.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) throws PackageManager.NameNotFoundException {
                this.f$0.lambda$onCreate$0(view);
            }
        });
        this.tabInstalled.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) throws PackageManager.NameNotFoundException {
                this.f$0.lambda$onCreate$1(view);
            }
        });
        this.tabUpdates.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws PackageManager.NameNotFoundException {
                MainActivity.this.selectUpdatesTab();
            }
        });
        this.btnSort.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$2(view);
            }
        });
        this.btnQueue.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$3(view);
            }
        });
        this.btnSort.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.lambda$onCreate$4(view);
            }
        });
        updateTabUI();
        updateTabLabels();
        this.prefs = getSharedPreferences("squire_prefs", 0);
        this.autoSideloadSwitch.setChecked(this.prefs.getBoolean("auto_sideload", true));
        this.autoSideloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda8
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.lambda$onCreate$5(compoundButton, z);
            }
        });
        setupRecyclerView();
        this.installResultReceiver = new AnonymousClass1();
        this.queueNotification = (TextView) findViewById(R.id.queue_notification);
        this.bgStatusBar = findViewById(R.id.bg_status_bar);
        this.bgLoader = (ProgressBar) findViewById(R.id.bg_loader);
        this.bgStatusText = (TextView) findViewById(R.id.bg_status_text);
        this.bgStatusDetail = (TextView) findViewById(R.id.bg_status_detail);
        this.bgStatusViewBtn = (Button) findViewById(R.id.bg_status_view_btn);
        this.dlBgStatusBar = findViewById(R.id.dl_bg_status_bar);
        this.dlBgStatusText = (TextView) findViewById(R.id.dl_bg_status_text);
        this.dlBgStatusDetail = (TextView) findViewById(R.id.dl_bg_status_detail);
        this.dlBgLoader = (ProgressBar) findViewById(R.id.dl_bg_loader);
        this.dlBgStatusViewBtn = (Button) findViewById(R.id.dl_bg_status_view_btn);
        this.bgStatusViewBtn.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$6(view);
            }
        });
        this.dlBgStatusViewBtn.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$6(view);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(InstallReceiver.ACTION_INSTALL_RESULT);
        filter.addAction(InstallReceiver.ACTION_INSTALL_STATUS);
        registerReceiver(this.installResultReceiver, filter, 2);
        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.squire.vr.MainActivity.2
                @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View v) throws PackageManager.NameNotFoundException {
        this.showInstalledOnly = false;
        this.showUpdatesOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View v) throws PackageManager.NameNotFoundException {
        this.showInstalledOnly = true;
        this.showUpdatesOnly = false;
        updateTabUI();
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View v) {
        showCustomSortMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(View v) {
        showQueueDialog();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$4(View v) {
        startActivity(new Intent(this, (Class<?>) SquireManagerActivity.class));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$5(CompoundButton btn, boolean checked) {
        this.prefs.edit().putBoolean("auto_sideload", checked).apply();
    }

    /* renamed from: com.squire.vr.MainActivity$1, reason: invalid class name */
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
                    new AlertDialog.Builder(MainActivity.this).setTitle("Games Done").setMessage("Organization: /sdcard/Download/Squire Vr Games/\n\n" + MainActivity.this.sessionSideloadResults.toString()).setPositiveButton("Awesome", new DialogInterface.OnClickListener() { // from class: com.squire.vr.MainActivity$1$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            this.f$0.lambda$onReceive$0(dialogInterface, i);
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

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(DialogInterface d, int w) {
            MainActivity.this.sessionSideloadResults.setLength(0);
            MainActivity.this.sessionCompletedGames.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$6(View v) {
        if (this.currentDownloadingGame != null) {
            if (this.customProgressDialog == null || !this.customProgressDialog.isShowing()) {
                showCustomProgressDialog(this.currentDownloadingGame.gameName);
            }
            this.bgStatusBar.setVisibility(8);
            this.bgStatusViewBtn.setVisibility(8);
        }
    }

    private void setupRecyclerView() {
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.squire.vr.MainActivity.3
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                return (MainActivity.this.adapter == null || MainActivity.this.adapter.getItemViewType(position) != 1) ? 1 : 3;
            }
        });
        this.recyclerView.setLayoutManager(glm);
        if (this.adapter == null) {
            this.adapter = new GameAdapter(new ArrayList(), new GameAdapter.OnGameClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda35
                @Override // com.squire.vr.GameAdapter.OnGameClickListener
                public final void onDownloadClick(Game game) throws NumberFormatException {
                    this.f$0.lambda$setupRecyclerView$7(game);
                }
            });
        }
        this.recyclerView.setAdapter(this.adapter);
    }

    private void startAppFlow() {
        if (checkAndRequestPermissions()) {
            new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startAppFlow$8();
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startAppFlow$8() {
        refreshPackageCache();
        loadCache();
        verifyBinaries();
        runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.fetchGames();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshPackageCache() {
        Map<String, String> newCache = new HashMap<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo pi : packages) {
            newCache.put(pi.packageName, pi.versionName);
        }
        this.installedPackagesCache = newCache;
        runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() throws PackageManager.NameNotFoundException {
                this.f$0.lambda$refreshPackageCache$9();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshPackageCache$9() throws PackageManager.NameNotFoundException {
        if (this.allGames != null) {
            updateTabLabels();
            filterGames(this.currentQuery);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.refreshPackageCache();
            }
        }).start();
        if (this.downloadReceiver == null) {
            this.downloadReceiver = new BroadcastReceiver() { // from class: com.squire.vr.MainActivity.4
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra("error")) {
                        MainActivity.this.dismissDialog();
                        String err = intent.getStringExtra("error");
                        MainActivity.this.showError("Download Failed: " + err);
                        return;
                    }
                    String statusMsg = intent.getStringExtra("statusMsg");
                    String phase = intent.getStringExtra(TypedValues.CycleType.S_WAVE_PHASE);
                    if (statusMsg == null) {
                        statusMsg = "Processing...";
                    }
                    if (phase == null) {
                        phase = "download";
                    }
                    if (MainActivity.this.dlBgStatusBar.getVisibility() == 0 && MainActivity.this.currentDownloadingGame != null) {
                        int pct = intent.getIntExtra("progress_percent", 0);
                        int queueSize = MainActivity.this.downloadQueue.size();
                        String queueSuffix = queueSize > 0 ? " (Queue: " + queueSize + ")" : HttpUrl.FRAGMENT_ENCODE_SET;
                        MainActivity.this.dlBgStatusText.setText("Downloading " + MainActivity.this.currentDownloadingGame.gameName);
                        MainActivity.this.dlBgStatusDetail.setText(pct + "% Completed" + queueSuffix);
                    }
                    if (intent.hasExtra("complete")) {
                        String name = intent.getStringExtra("name");
                        if (name != null) {
                            MainActivity.this.sessionCompletedGames.add(name);
                        }
                        if (MainActivity.this.customProgressDialog != null && MainActivity.this.customProgressDialog.isShowing() && !MainActivity.this.autoSideloadSwitch.isChecked()) {
                            MainActivity.this.customProgressDialog.dismiss();
                        }
                        if (MainActivity.this.autoSideloadSwitch.isChecked()) {
                            String finalSavePath = intent.getStringExtra("savePath");
                            if (finalSavePath == null) {
                                finalSavePath = "/sdcard/Download/Squire Vr Games/" + intent.getStringExtra("name");
                            }
                            if ((MainActivity.this.customProgressDialog == null || !MainActivity.this.customProgressDialog.isShowing()) && MainActivity.this.dlBgStatusBar.getVisibility() == 8) {
                                MainActivity.this.showCustomProgressDialog("Auto-Sideloading...");
                            } else {
                                MainActivity.this.updateDialogTitle("Auto-Sideloading...");
                            }
                            if (MainActivity.this.dlProgressBar != null) {
                                MainActivity.this.dlProgressBar.setIndeterminate(true);
                            }
                            AutoInstaller.processExtraction(MainActivity.this, new File(finalSavePath), new AnonymousClass1(name));
                            return;
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
                        if (total <= 0) {
                            total = 100;
                        }
                        long speed = intent.getLongExtra("speed", 0L);
                        int progress = (int) ((100 * current) / total);
                        if (MainActivity.this.dlProgressBar != null) {
                            MainActivity.this.dlProgressBar.setIndeterminate(false);
                            MainActivity.this.dlProgressBar.setProgress(progress);
                        }
                        String speedStr = HttpUrl.FRAGMENT_ENCODE_SET;
                        if ("download".equals(phase)) {
                            double speedMb = speed / 1048576.0d;
                            speedStr = String.format("%.2f MB/s", Double.valueOf(speedMb));
                        }
                        String etaStr = "--:--";
                        if (speed > 0) {
                            long remainingBytes = total - current;
                            long secondsLeft = remainingBytes / speed;
                            long mins = secondsLeft / 60;
                            long secs = secondsLeft % 60;
                            etaStr = String.format("%dm %ds", Long.valueOf(mins), Long.valueOf(secs));
                        }
                        if (!"download".equals(phase)) {
                            MainActivity.this.updateDialogMessage(statusMsg);
                            if (MainActivity.this.dlProgressText != null) {
                                MainActivity.this.dlProgressText.setText(HttpUrl.FRAGMENT_ENCODE_SET);
                            }
                            if (MainActivity.this.dlSpeedEta != null) {
                                MainActivity.this.dlSpeedEta.setText(etaStr);
                                return;
                            }
                            return;
                        }
                        double currentMb = current / 1048576.0d;
                        double totalMb = total / 1048576.0d;
                        MainActivity.this.updateDialogMessage(statusMsg);
                        if (MainActivity.this.dlProgressText != null) {
                            MainActivity.this.dlProgressText.setText(progress + "% (" + String.format("%.2f", Double.valueOf(currentMb)) + "MB / " + String.format("%.2f", Double.valueOf(totalMb)) + "MB)");
                        }
                        if (MainActivity.this.dlSpeedEta != null) {
                            MainActivity.this.dlSpeedEta.setText(speedStr + " • " + etaStr);
                        }
                    }
                }

                /* renamed from: com.squire.vr.MainActivity$4$1, reason: invalid class name */
                class AnonymousClass1 implements AutoInstaller.InstallCallback {
                    final /* synthetic */ String val$name;

                    AnonymousClass1(String str) {
                        this.val$name = str;
                    }

                    @Override // com.squire.vr.AutoInstaller.InstallCallback
                    public void onStatus(final String msg) {
                        MainActivity.this.runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$4$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() throws NumberFormatException {
                                this.f$0.lambda$onStatus$0(msg);
                            }
                        });
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public /* synthetic */ void lambda$onStatus$0(String msg) throws NumberFormatException {
                        MainActivity.this.updateDialogMessage(msg);
                        if (msg.startsWith("Preparing APK: ")) {
                            try {
                                String pctStr = msg.replace("Preparing APK: ", HttpUrl.FRAGMENT_ENCODE_SET).replace("%", HttpUrl.FRAGMENT_ENCODE_SET).trim();
                                int progress = Integer.parseInt(pctStr);
                                if (MainActivity.this.dlProgressBar != null) {
                                    MainActivity.this.dlProgressBar.setIndeterminate(false);
                                    MainActivity.this.dlProgressBar.setProgress(progress);
                                }
                                if (MainActivity.this.dlProgressText != null) {
                                    MainActivity.this.dlProgressText.setText(progress + "%");
                                }
                                if (MainActivity.this.dlSpeedEta != null) {
                                    MainActivity.this.dlSpeedEta.setText("Installing...");
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (msg.startsWith("System Installing: ")) {
                            try {
                                String pctStr2 = msg.replace("System Installing: ", HttpUrl.FRAGMENT_ENCODE_SET).replace("%", HttpUrl.FRAGMENT_ENCODE_SET).trim();
                                int progress2 = Integer.parseInt(pctStr2);
                                if (MainActivity.this.dlProgressBar != null) {
                                    MainActivity.this.dlProgressBar.setIndeterminate(false);
                                    MainActivity.this.dlProgressBar.setProgress(progress2);
                                }
                                if (MainActivity.this.dlProgressText != null) {
                                    MainActivity.this.dlProgressText.setText(progress2 + "%");
                                }
                                if (MainActivity.this.dlSpeedEta != null) {
                                    MainActivity.this.dlSpeedEta.setText("Finalizing...");
                                }
                            } catch (Exception e2) {
                            }
                        }
                        if (msg.contains("Check Quest Prompt")) {
                            MainActivity.this.updateDialogTitle("Squire: Awaiting Install...");
                            if (MainActivity.this.dlProgressBar != null) {
                                MainActivity.this.dlProgressBar.setIndeterminate(true);
                            }
                        }
                    }

                    @Override // com.squire.vr.AutoInstaller.InstallCallback
                    public void onDone(boolean obbMoved, String obbPath) {
                    }

                    @Override // com.squire.vr.AutoInstaller.InstallCallback
                    public void onError(String msg) {
                        MainActivity mainActivity = MainActivity.this;
                        final String str = this.val$name;
                        mainActivity.runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$4$1$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onError$2(str);
                            }
                        });
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public /* synthetic */ void lambda$onError$2(final String name) {
                        MainActivity.this.dismissDialog();
                        MainActivity.this.sessionSideloadResults.append("❌ ").append(name).append(" (Extraction Failed)\n");
                        new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$4$1$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onError$1(name);
                            }
                        }).start();
                        MainActivity.this.processNextInQueue();
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public /* synthetic */ void lambda$onError$1(String name) {
                        File gameDir = new File("/sdcard/Download/Squire Vr Games/" + name);
                        if (gameDir.exists()) {
                            MainActivity.this.deleteRecursive(gameDir);
                        }
                    }
                }
            };
            registerReceiver(this.downloadReceiver, new IntentFilter(Config.ACTION_PROGRESS), 2);
        }
        if (this.recyclerView.getAdapter() == null && this.loader.getVisibility() == 0 && Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
            startAppFlow();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        if (this.downloadReceiver != null) {
            unregisterReceiver(this.downloadReceiver);
        }
        if (this.installResultReceiver != null) {
            unregisterReceiver(this.installResultReceiver);
        }
        super.onDestroy();
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkAndRequestPermissions$10();
                }
            });
            Intent intent = new Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION");
            startActivity(intent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAndRequestPermissions$10() {
        this.statusText.setText("Permission required to install games.");
        Toast.makeText(this, "Please allow 'All Files Access' to continue", 1).show();
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
                for (String f : files) {
                    sb.append(f).append(", ");
                }
            }
            showError("Native binaries missing! Found: " + sb.toString());
            return;
        }
        updateStatus("Binaries ready...");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStatus$11(String status) {
        this.statusText.setText(status);
    }

    private void updateStatus(final String status) {
        runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateStatus$11(status);
            }
        });
    }

    private String getNativeLibPath(String libName) {
        return getApplicationInfo().nativeLibraryDir + "/" + libName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchGames() {
        updateStatus("Fetching VRP Config...");
        new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() throws IOException {
                this.f$0.lambda$fetchGames$14();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchGames$14() throws IOException {
        try {
            Request request = new Request.Builder().url(Config.VRP_CONFIG_URL).build();
            Response response = this.client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Config fetch failed: " + response.code());
            }
            String json = response.body().string();
            final VrpConfig config = (VrpConfig) new Gson().fromJson(json, VrpConfig.class);
            if (config.baseUri == null || config.baseUri.isEmpty()) {
                throw new IOException("Invalid config: missing baseUri");
            }
            if (config.password != null) {
                try {
                    byte[] decoded = Base64.decode(config.password, 0);
                    config.password = new String(decoded, Key.STRING_CHARSET_NAME);
                } catch (Exception e) {
                    config.password = Config.VRP_DEFAULT_PASSWORD;
                }
            } else {
                config.password = Config.VRP_DEFAULT_PASSWORD;
            }
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$fetchGames$12(config);
                }
            });
        } catch (Exception e2) {
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$fetchGames$13();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchGames$12(VrpConfig config) {
        updateStatus("Config loaded. Mirror: " + config.baseUri);
        downloadMeta(config);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchGames$13() {
        updateStatus("Config failed. Using fallback...");
        VrpConfig config = new VrpConfig();
        config.baseUri = Config.MIRROR_BASEURI_URL;
        downloadMeta(config);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showError(final String msg) {
        runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showError$15(msg);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showError$15(String msg) {
        this.loader.setVisibility(8);
        this.statusText.setText("Error occurred (see dialog)");
        new AlertDialog.Builder(this).setTitle("Error").setMessage(msg).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCustomProgressDialog(String title) {
        if (this.customProgressDialog == null) {
            this.customProgressDialog = new Dialog(this);
            this.customProgressDialog.requestWindowFeature(1);
            this.customProgressDialog.setContentView(R.layout.dialog_download);
            this.customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            this.customProgressDialog.setCancelable(false);
            this.dlTitle = (TextView) this.customProgressDialog.findViewById(R.id.dl_game_title);
            this.dlStatus = (TextView) this.customProgressDialog.findViewById(R.id.dl_status);
            this.dlProgressText = (TextView) this.customProgressDialog.findViewById(R.id.dl_progress_text);
            this.dlSpeedEta = (TextView) this.customProgressDialog.findViewById(R.id.dl_speed_eta);
            this.dlProgressBar = (ProgressBar) this.customProgressDialog.findViewById(R.id.dl_progress_bar);
            Button btnBg = (Button) this.customProgressDialog.findViewById(R.id.btn_bg);
            Button btnCancel = (Button) this.customProgressDialog.findViewById(R.id.btn_cancel);
            btnBg.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda20
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$showCustomProgressDialog$16(view);
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda21
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$showCustomProgressDialog$18(view);
                }
            });
        }
        if (this.dlTitle != null) {
            this.dlTitle.setText(title);
        }
        this.customProgressDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomProgressDialog$16(View v) {
        this.customProgressDialog.dismiss();
        this.dlBgStatusBar.setVisibility(0);
        this.bgStatusViewBtn.setVisibility(0);
        updateBgStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomProgressDialog$18(View v) {
        this.customProgressDialog.dismiss();
        stopService(new Intent(this, (Class<?>) StreamingService.class));
        if (this.currentDownloadingGame != null) {
            final String releaseName = this.currentDownloadingGame.releaseName;
            new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showCustomProgressDialog$17(releaseName);
                }
            }).start();
        }
        processNextInQueue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomProgressDialog$17(String releaseName) {
        File gameDir = new File("/sdcard/Download/Squire Vr Games/" + releaseName);
        if (gameDir.exists()) {
            Log.d("SquireCleanup", "Deleting canceled download folder: " + gameDir.getAbsolutePath());
            deleteRecursive(gameDir);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDialogMessage(String msg) {
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing() && this.dlStatus != null) {
            this.dlStatus.setText(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDialogTitle(String title) {
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing() && this.dlTitle != null) {
            this.dlTitle.setText(title);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissDialog() {
        if (this.customProgressDialog != null && this.customProgressDialog.isShowing()) {
            this.customProgressDialog.dismiss();
        }
    }

    private void updateBgStatus() {
        if (this.currentDownloadingGame != null) {
            this.dlBgStatusText.setText("Downloading " + this.currentDownloadingGame.gameName);
            this.dlBgStatusDetail.setText("Check details");
        }
    }

    private void showCustomSortMenu() {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_sort, (ViewGroup) null);
        popup.setContentView(layout);
        popup.setHeight(-2);
        popup.setWidth(-2);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(0));
        layout.findViewById(R.id.sort_default).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$showCustomSortMenu$19(popup, view);
            }
        });
        layout.findViewById(R.id.sort_date).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$showCustomSortMenu$20(popup, view);
            }
        });
        layout.findViewById(R.id.sort_size).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$showCustomSortMenu$21(popup, view);
            }
        });
        layout.findViewById(R.id.sort_manager).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$showCustomSortMenu$22(popup, view);
            }
        });
        popup.showAsDropDown(this.btnSort, 0, 10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomSortMenu$19(PopupWindow popup, View v) {
        this.currentSortMode = 0;
        filterGames(this.currentQuery);
        popup.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomSortMenu$20(PopupWindow popup, View v) {
        this.currentSortMode = 1;
        filterGames(this.currentQuery);
        popup.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomSortMenu$21(PopupWindow popup, View v) {
        this.currentSortMode = 2;
        filterGames(this.currentQuery);
        popup.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomSortMenu$22(PopupWindow popup, View v) {
        startActivity(new Intent(this, (Class<?>) SquireManagerActivity.class));
        popup.dismiss();
    }

    private void showQueueDialog() {
        final Dialog qDialog = new Dialog(this);
        qDialog.requestWindowFeature(1);
        qDialog.setContentView(R.layout.dialog_queue);
        qDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        qDialog.getWindow().setLayout(-1, -2);
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
        rv.setAdapter(new RecyclerView.Adapter<QueueViewHolder>() { // from class: com.squire.vr.MainActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return MainActivity.this.new QueueViewHolder(MainActivity.this.getLayoutInflater().inflate(R.layout.item_queue, parent, false));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(QueueViewHolder holder, int position) {
                Game g = (Game) qList.get(position);
                holder.name.setText(g.gameName);
                if (g == MainActivity.this.currentDownloadingGame) {
                    holder.status.setText("Downloading...");
                    holder.status.setTextColor(-16745729);
                } else {
                    holder.status.setText("Waiting");
                    holder.status.setTextColor(-5592406);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return qList.size();
            }
        });
        qDialog.findViewById(R.id.btn_close_queue).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                qDialog.dismiss();
            }
        });
        qDialog.show();
    }

    class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;

        public QueueViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.q_game_name);
            this.status = (TextView) itemView.findViewById(R.id.q_status);
        }
    }

    private void downloadMeta(final VrpConfig config) {
        updateStatus("Syncing Meta from VRP...");
        File metaFile = new File(getFilesDir(), "meta.7z");
        if (metaFile.exists()) {
            metaFile.delete();
        }
        final String rclonePath = getNativeLibPath("librclone.so");
        new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() throws InterruptedException, IOException {
                this.f$0.lambda$downloadMeta$24(config, rclonePath);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$downloadMeta$24(VrpConfig config, String rclonePath) throws InterruptedException, IOException {
        try {
            String metaUrl = (config.baseUri.endsWith("/") ? new StringBuilder().append(config.baseUri).append("meta.7z") : new StringBuilder().append(config.baseUri).append("/meta.7z")).toString();
            ProcessBuilder pb = new ProcessBuilder(rclonePath, "copyurl", metaUrl, new File(getFilesDir(), "meta.7z").getAbsolutePath(), "--config", "/dev/null", "--no-check-certificate");
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder log = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    log.append(line).append("\n");
                }
            }
            int exitCode = p.waitFor();
            if (exitCode == 0 && new File(getFilesDir(), "meta.7z").exists()) {
                extractMeta(config);
            } else {
                showError("Sync failed (" + exitCode + "):\n" + log.toString());
            }
        } catch (Exception e) {
            showError("Sync error: " + e.getMessage());
        }
    }

    private void extractMeta(final VrpConfig config) {
        updateStatus("Extracting game list...");
        final File metaFile = new File(getFilesDir(), "meta.7z");
        final File outDir = new File(getFilesDir(), ".meta");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        final String p7zPath = getNativeLibPath("lib7z.so");
        new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$extractMeta$25(p7zPath, metaFile, outDir, config);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$extractMeta$25(String p7zPath, File metaFile, File outDir, VrpConfig config) throws Throwable {
        try {
            ProcessBuilder pb = new ProcessBuilder(p7zPath, "x", metaFile.getAbsolutePath(), "-pgL59VfgPxoHR", "-o" + outDir.getAbsolutePath(), "-y");
            pb.environment().put("LD_LIBRARY_PATH", getApplicationInfo().nativeLibraryDir);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder log = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    log.append(line).append("\n");
                }
            }
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: startDownload, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void lambda$setupRecyclerView$7(final Game game) throws NumberFormatException {
        long totalBytes;
        if (this.currentDownloadingGame != null) {
            this.downloadQueue.add(game);
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startDownload$26(game);
                }
            });
            return;
        }
        this.currentDownloadingGame = game;
        showCustomProgressDialog(game.gameName);
        try {
            String cleanSize = game.sizeMb.trim().toUpperCase();
            double val = Double.parseDouble(cleanSize.replace(" GB", HttpUrl.FRAGMENT_ENCODE_SET).replace(" MB", HttpUrl.FRAGMENT_ENCODE_SET).trim());
            totalBytes = cleanSize.contains("GB") ? (long) (val * 1024.0d * 1024.0d * 1024.0d) : (long) (val * 1024.0d * 1024.0d);
        } catch (Exception e) {
            totalBytes = 2147483648L;
        }
        long requiredSpace = 2 * totalBytes;
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        if (availableSpace < requiredSpace) {
            dismissDialog();
            double availableGb = availableSpace / 1.073741824E9d;
            double requiredGb = requiredSpace / 1.073741824E9d;
            showError(String.format("Insufficient Storage!\nAvailable: %.2f GB\nRequired (2x Game Size): %.2f GB\n\nPlease free up space and try again.", Double.valueOf(availableGb), Double.valueOf(requiredGb)));
            processNextInQueue();
            return;
        }
        Intent intent = new Intent(this, (Class<?>) StreamingService.class);
        intent.putExtra("releaseName", game.releaseName);
        intent.putExtra("rcloneUrl", game.md5Hash);
        intent.putExtra("baseUri", game.baseUri);
        intent.putExtra("savePath", "/sdcard/Download/Squire Vr Games/" + game.releaseName);
        intent.putExtra("totalBytes", totalBytes);
        intent.putExtra("password", game.password);
        startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownload$26(Game game) {
        showQueueNotification(game.gameName);
        this.dlBgStatusBar.setVisibility(0);
        this.dlBgStatusBar.setAlpha(1.0f);
        this.bgStatusViewBtn.setVisibility(0);
        int queueSize = this.downloadQueue.size();
        this.dlBgStatusText.setText("Downloading " + this.currentDownloadingGame.gameName);
        this.dlBgStatusDetail.setText("0% Completed (Queue: " + queueSize + ")");
    }

    private void showQueueNotification(String gameName) {
        if (this.queueNotification == null) {
            return;
        }
        this.queueNotification.setText(gameName.toUpperCase() + " ADDED TO QUEUE");
        this.queueNotification.setVisibility(0);
        this.queueNotification.setAlpha(0.0f);
        this.queueNotification.animate().alpha(1.0f).setDuration(400L).withEndAction(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showQueueNotification$29();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showQueueNotification$29() {
        new Handler().postDelayed(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showQueueNotification$28();
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showQueueNotification$28() {
        if (this.queueNotification != null) {
            this.queueNotification.animate().alpha(0.0f).setDuration(600L).withEndAction(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showQueueNotification$27();
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showQueueNotification$27() {
        this.queueNotification.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processNextInQueue() {
        this.currentDownloadingGame = null;
        runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processNextInQueue$30();
            }
        });
        if (!this.downloadQueue.isEmpty()) {
            final Game next = this.downloadQueue.poll();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() throws NumberFormatException {
                    this.f$0.lambda$processNextInQueue$31(next);
                }
            }, 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processNextInQueue$30() {
        this.dlBgStatusBar.setVisibility(8);
        this.bgStatusViewBtn.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFinalSummary() {
        if (this.sessionCompletedGames.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("✅ All downloads finished!\n\n");
        sb.append("Location: /sdcard/Download/Squire Vr Games/\n\n");
        sb.append("Games:\n");
        for (String g : this.sessionCompletedGames) {
            sb.append("- ").append(g).append("\n");
        }
        new AlertDialog.Builder(this).setTitle("Games Done").setMessage(sb.toString()).setPositiveButton("Awesome", new DialogInterface.OnClickListener() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda18
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.lambda$showFinalSummary$32(dialogInterface, i);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFinalSummary$32(DialogInterface d, int w) {
        this.sessionCompletedGames.clear();
    }

    private void parseGameList(VrpConfig config) throws Throwable {
        Scanner scanner;
        Throwable th;
        File listFile;
        File listFile2 = new File(getFilesDir(), ".meta/VRP-GameList.txt");
        File listFile3 = !listFile2.exists() ? new File(getFilesDir(), "VRP-GameList.txt") : listFile2;
        try {
            scanner = new Scanner(listFile3);
        } catch (Exception e) {
            e = e;
        }
        try {
            try {
                List<Game> games = new ArrayList<>();
                if (scanner.hasNextLine()) {
                    try {
                        scanner.nextLine();
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            scanner.close();
                            throw th;
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                            throw th;
                        }
                    }
                }
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(";");
                    if (parts.length < 6) {
                        listFile = listFile3;
                    } else {
                        Game g = new Game();
                        int i = 0;
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
                            if (rawSize.toUpperCase().contains("GB")) {
                                g.sizeMb = String.format("%.2f GB", Double.valueOf(val));
                            } else if (val > 1000.0d) {
                                g.sizeMb = String.format("%.2f GB", Double.valueOf(val / 1024.0d));
                            } else {
                                g.sizeMb = String.format("%.2f MB", Double.valueOf(val));
                            }
                        } catch (Exception e2) {
                        }
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            byte[] digest = md.digest((g.releaseName + "\n").getBytes(Key.STRING_CHARSET_NAME));
                            StringBuilder hexString = new StringBuilder();
                            int length = digest.length;
                            while (i < length) {
                                byte b = digest[i];
                                String hex = Integer.toHexString(b & UByte.MAX_VALUE);
                                MessageDigest md2 = md;
                                listFile = listFile3;
                                if (hex.length() == 1) {
                                    try {
                                        try {
                                            hexString.append('0');
                                        } catch (Throwable th4) {
                                            th = th4;
                                            scanner.close();
                                            throw th;
                                        }
                                    } catch (Exception e3) {
                                        g.md5Hash = g.releaseName;
                                        g.baseUri = config.baseUri;
                                        g.password = config.password;
                                        games.add(g);
                                        listFile3 = listFile;
                                    }
                                }
                                hexString.append(hex);
                                i++;
                                md = md2;
                                listFile3 = listFile;
                            }
                            listFile = listFile3;
                            g.md5Hash = hexString.toString();
                        } catch (Exception e4) {
                            listFile = listFile3;
                        }
                        g.baseUri = config.baseUri;
                        g.password = config.password;
                        games.add(g);
                    }
                    listFile3 = listFile;
                }
                if (this.allGames == null) {
                    this.allGames = new ArrayList(games);
                } else {
                    Map<String, Game> gameMap = new HashMap<>();
                    for (Game existing : this.allGames) {
                        gameMap.put(existing.packageName, existing);
                    }
                    List<Game> merged = new ArrayList<>();
                    for (Game serverGame : games) {
                        Game existing2 = gameMap.get(serverGame.packageName);
                        if (existing2 != null) {
                            existing2.gameName = serverGame.gameName;
                            existing2.releaseName = serverGame.releaseName;
                            if (!serverGame.version.equals(existing2.version)) {
                                existing2.version = serverGame.version;
                                existing2.thumbnailPath = null;
                                existing2.noteContent = null;
                            }
                            existing2.sizeMb = serverGame.sizeMb;
                            existing2.date = serverGame.date;
                            merged.add(existing2);
                        } else {
                            merged.add(serverGame);
                        }
                    }
                    this.allGames = merged;
                }
                new Thread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda22
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$parseGameList$34();
                    }
                }).start();
                runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda33
                    @Override // java.lang.Runnable
                    public final void run() throws PackageManager.NameNotFoundException {
                        this.f$0.lambda$parseGameList$35();
                    }
                });
                scanner.close();
            } catch (Throwable th5) {
                th = th5;
            }
        } catch (Exception e5) {
            e = e5;
            showError("List error: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$parseGameList$34() {
        int size;
        try {
            File metaDir = new File(getFilesDir(), ".meta");
            File thumbnails = findFolder(metaDir, "Thumbnails");
            if (thumbnails == null) {
                thumbnails = findFolder(metaDir, "thumbnails");
            }
            File notes = findFolder(metaDir, "notes");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            this.cacheInProgress = true;
            this.cachePct = 0;
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$parseGameList$33();
                }
            });
            for (int i = 0; i < this.allGames.size(); i++) {
                Game g = this.allGames.get(i);
                boolean needsScan = g.thumbnailPath == null || g.noteContent == null;
                if (needsScan) {
                    if (thumbnails != null && thumbnails.exists()) {
                        File jpg = new File(thumbnails, g.packageName + ".jpg");
                        if (jpg.exists()) {
                            g.thumbnailPath = jpg.getAbsolutePath();
                        } else {
                            File png = new File(thumbnails, g.packageName + ".png");
                            if (png.exists()) {
                                g.thumbnailPath = png.getAbsolutePath();
                            }
                        }
                    }
                    if (notes != null && notes.exists()) {
                        File noteFile = new File(notes, g.releaseName + ".txt");
                        if (noteFile.exists()) {
                            g.noteContent = readNoteContent(noteFile);
                        }
                    }
                }
                File gameFolder = findFolder(metaDir, g.releaseName);
                if (gameFolder != null && gameFolder.exists()) {
                    long maxMtime = 0;
                    File[] metaFiles = gameFolder.listFiles();
                    if (metaFiles != null) {
                        for (File f : metaFiles) {
                            if (f.lastModified() > maxMtime) {
                                maxMtime = f.lastModified();
                            }
                        }
                    }
                    if (maxMtime > 0) {
                        g.date = sdf.format(new Date(maxMtime));
                    }
                }
                do {
                    size = this.allGames.size();
                } while (size <= 0);
                this.cachePct = (i * 100) / size;
                if (i % 50 == 0 || i == this.allGames.size() - 1) {
                    runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda40
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$parseGameList$33();
                        }
                    });
                }
            }
            this.cachePct = 100;
            this.cacheInProgress = false;
            runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$parseGameList$33();
                }
            });
            saveCache();
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$parseGameList$33() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
        if (this.prefs == null) {
            this.prefs = getSharedPreferences("squire_prefs", 0);
        }
        if (this.prefs.getBoolean("initial_cache_done", false)) {
            return;
        }
        if (this.cachePct == 100) {
            fadeOutView(this.bgStatusBar);
            return;
        }
        this.bgStatusBar.setVisibility(0);
        this.bgLoader.setIndeterminate(false);
        this.bgLoader.setMax(100);
        this.bgLoader.setProgress(this.cachePct);
        this.bgStatusText.setText("Caching library");
        this.bgStatusDetail.setText(this.cachePct + "%");
        this.bgStatusViewBtn.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$parseGameList$35() throws PackageManager.NameNotFoundException {
        this.loader.setVisibility(8);
        this.statusText.setVisibility(8);
        if (this.adapter == null) {
            setupRecyclerView();
        }
        updateTabLabels();
        filterGames(this.currentQuery);
    }

    private void updateTabUI() {
        this.tabAll.setBackgroundTintList(ColorStateList.valueOf(-13421773));
        this.tabInstalled.setBackgroundTintList(ColorStateList.valueOf(-13421773));
        this.tabUpdates.setBackgroundTintList(ColorStateList.valueOf(-13421773));
        if (!this.showInstalledOnly) {
            this.tabAll.setBackgroundTintList(ColorStateList.valueOf(-16745729));
        } else if (this.showUpdatesOnly) {
            this.tabUpdates.setBackgroundTintList(ColorStateList.valueOf(-16745729));
        } else {
            this.tabInstalled.setBackgroundTintList(ColorStateList.valueOf(-16745729));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void filterGames(String query) {
        this.currentQuery = query;
        if (this.allGames == null) {
            return;
        }
        List<Game> filtered = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Game g : this.allGames) {
            boolean matchesSearch = g.gameName.toLowerCase().contains(q);
            boolean matchesFilter = this.showInstalledOnly ? isPackageInstalled(g.packageName) : true;
            if (this.showUpdatesOnly && matchesFilter) {
                matchesFilter = g.needsUpdate;
            }
            if (matchesSearch && matchesFilter) {
                filtered.add(g);
            }
        }
        if (this.currentSortMode == 1) {
            Collections.sort(filtered, new Comparator() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda32
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return MainActivity.lambda$filterGames$36((Game) obj, (Game) obj2);
                }
            });
        } else if (this.currentSortMode == 2) {
            Collections.sort(filtered, new Comparator() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda34
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return this.f$0.lambda$filterGames$37((Game) obj, (Game) obj2);
                }
            });
        }
        if (this.adapter != null) {
            this.adapter.updateList(filtered, this.showInstalledOnly, this.showUpdatesOnly);
        }
    }

    static /* synthetic */ int lambda$filterGames$36(Game g1, Game g2) {
        String str = g1.date;
        String d2 = HttpUrl.FRAGMENT_ENCODE_SET;
        String d1 = str != null ? g1.date : HttpUrl.FRAGMENT_ENCODE_SET;
        if (g2.date != null) {
            d2 = g2.date;
        }
        return d2.compareTo(d1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$filterGames$37(Game g1, Game g2) {
        return Long.compare(parseSize(g2.sizeMb), parseSize(g1.sizeMb));
    }

    private long parseSize(String sizeStr) {
        if (sizeStr == null) {
            return 0L;
        }
        try {
            String clean = sizeStr.trim().toUpperCase();
            double multiplier = 1.0d;
            if (clean.contains("GB")) {
                multiplier = 1.073741824E9d;
            } else if (clean.contains("MB")) {
                multiplier = 1048576.0d;
            } else if (clean.contains("KB")) {
                multiplier = 1024.0d;
            }
            String numPart = clean.replace("GB", HttpUrl.FRAGMENT_ENCODE_SET).replace("MB", HttpUrl.FRAGMENT_ENCODE_SET).replace("KB", HttpUrl.FRAGMENT_ENCODE_SET).replace("BYTES", HttpUrl.FRAGMENT_ENCODE_SET).trim();
            return (long) (Double.parseDouble(numPart) * multiplier);
        } catch (Exception e) {
            return 0L;
        }
    }

    private String getInstalledVersion(String pkgName) {
        Map<String, String> map = this.installedPackagesCache;
        if (map != null) {
            return map.get(pkgName);
        }
        return null;
    }

    private String readNoteContent(File file) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        String strTrim = sb.toString().trim();
                        br.close();
                        return strTrim;
                    }
                    sb.append(line).append("\n");
                }
            } finally {
            }
        } catch (Exception e) {
            return null;
        }
    }

    private File findFolder(File root, String name) {
        if (root == null || !root.exists() || !root.isDirectory()) {
            return null;
        }
        Queue<File> queue = new LinkedList<>();
        queue.add(root);
        int foldersChecked = 0;
        while (!queue.isEmpty()) {
            int foldersChecked2 = foldersChecked + 1;
            if (foldersChecked >= 5000) {
                break;
            }
            File current = queue.poll();
            if (current != null) {
                if (current.getName().equalsIgnoreCase(name)) {
                    return current;
                }
                File[] files = current.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            queue.add(f);
                        }
                    }
                }
            }
            foldersChecked = foldersChecked2;
        }
        return null;
    }

    private boolean isPackageInstalled(String packageName) {
        Map<String, String> map = this.installedPackagesCache;
        if (map != null) {
            return map.containsKey(packageName);
        }
        return false;
    }

    private void saveCache() {
        try {
            File cacheFile = new File(getFilesDir(), "cached_games.txt");
            FileWriter writer = new FileWriter(cacheFile);
            try {
                List<Game> list = this.allGames;
                if (list != null) {
                    for (Game game : list) {
                        writer.write(game.gameName + ";" + game.releaseName + ";" + game.packageName + ";" + game.version + ";" + game.date + ";" + game.sizeMb + ";" + game.thumbnailPath + ";" + game.md5Hash + ";" + game.baseUri + ";" + game.password + "\n");
                    }
                }
                writer.close();
                if (this.prefs == null) {
                    this.prefs = getSharedPreferences("squire_prefs", 0);
                }
                this.prefs.edit().putBoolean("initial_cache_done", true).apply();
            } catch (Throwable th) {
                try {
                    writer.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Throwable th3) {
        }
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
                            String strTrim = parts[6].trim();
                            if (!strTrim.equals("null") && strTrim.length() > 0) {
                                g.thumbnailPath = strTrim;
                            }
                        }
                        if (parts.length >= 8) {
                            String strTrim2 = parts[7].trim();
                            if (!strTrim2.equals("null")) {
                                g.md5Hash = strTrim2;
                            }
                        }
                        if (parts.length >= 9) {
                            String strTrim3 = parts[8].trim();
                            if (!strTrim3.equals("null")) {
                                g.baseUri = strTrim3;
                            }
                        }
                        if (parts.length >= 10) {
                            String strTrim4 = parts[9].trim();
                            if (!strTrim4.equals("null")) {
                                g.password = strTrim4;
                            }
                        }
                        games.add(g);
                    }
                }
                s.close();
                this.allGames = games;
                runOnUiThread(new Runnable() { // from class: com.squire.vr.MainActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() throws PackageManager.NameNotFoundException {
                        this.f$0.lambda$loadCache$38();
                    }
                });
            }
        } catch (Throwable th) {
        }
    }

    /* renamed from: com.squire.vr.MainActivity$6, reason: invalid class name */
    class AnonymousClass6 extends TypeToken<ArrayList<Game>> {
        AnonymousClass6() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadCache$38() throws PackageManager.NameNotFoundException {
        if (this.adapter == null) {
            setupRecyclerView();
        }
        this.adapter.updateList(this.allGames, false, false);
        updateTabLabels();
        this.loader.setVisibility(8);
        this.statusText.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteRecursive(File file) {
        File[] children;
        if (file.isDirectory() && (children = file.listFiles()) != null) {
            for (File child : children) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }
}

package com.squire.vr.updater;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GithubUpdateChecker {

    private static final String GITHUB_OWNER = "kyy1-cyy"; // USER MUST REPLACE THIS
    private static final String GITHUB_REPO = "Squire-VR";   // USER MUST REPLACE THIS
    private static final String TAG = "SquireUpdate";

    public interface UpdateCallback {
        void onUpdateAvailable(String version, String body, String downloadUrl);
        void onNoUpdate();
        void onError(String error);
    }

    private final Context context;
    private final OkHttpClient client;

    public GithubUpdateChecker(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void check(final UpdateCallback callback) {
        // 1. Get Local Version
        final String localVersionName;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            localVersionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            callback.onError("Could not get local version");
            return;
        }

        // 2. Fetch GitHub Release
        String url = "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("GitHub API Error: " + response.code());
                    return;
                }

                try {
                    String json = response.body().string();
                    JsonObject release = new Gson().fromJson(json, JsonObject.class);

                    String tagName = release.get("tag_name").getAsString(); // e.g. v2.1.1
                    String body = release.get("body").getAsString();
                    
                    // Parse Versions
                    String remoteVer = normalizeVersion(tagName);
                    String localVer = normalizeVersion(localVersionName);

                    if (compareVersions(remoteVer, localVer) > 0) {
                        // Update Found
                        String apkUrl = null;
                        JsonArray assets = release.getAsJsonArray("assets");
                        for (int i = 0; i < assets.size(); i++) {
                            JsonObject asset = assets.get(i).getAsJsonObject();
                            String name = asset.get("name").getAsString();
                            if (name.toLowerCase().endsWith(".apk")) {
                                apkUrl = asset.get("browser_download_url").getAsString();
                                break;
                            }
                        }

                        if (apkUrl != null) {
                            callback.onUpdateAvailable(tagName, body, apkUrl);
                        } else {
                            callback.onError("No APK asset found in release");
                        }
                    } else {
                        callback.onNoUpdate();
                    }

                } catch (Exception e) {
                    callback.onError("Parse Error: " + e.getMessage());
                }
            }
        });
    }

    private String normalizeVersion(String v) {
        if (v == null) return "";
        v = v.trim();
        if (v.startsWith("v") || v.startsWith("V")) {
            v = v.substring(1);
        }
        return v;
    }

    // Returns > 0 if v1 > v2
    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? parse(parts1[i]) : 0;
            int num2 = i < parts2.length ? parse(parts2[i]) : 0;
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }

    private int parse(String s) {
        try {
            return Integer.parseInt(s.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
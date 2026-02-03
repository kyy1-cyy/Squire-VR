package com.squire.vr.updater;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.squire.vr.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    private String downloadUrl;
    private File apkFile;
    private LinearLayout downloadPanel;
    private ProgressBar progressBar;
    private TextView tvPercent, tvSpeed, tvEta, tvFinished;
    private Button btnDownload;
    private boolean isDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        String version = getIntent().getStringExtra("version");
        String body = getIntent().getStringExtra("body");
        downloadUrl = getIntent().getStringExtra("url");

        TextView titleVer = findViewById(R.id.update_version);
        titleVer.setText(version);

        TextView notes = findViewById(R.id.release_notes);
        notes.setText(parseMarkdown(body));

        btnDownload = findViewById(R.id.btn_download);
        downloadPanel = findViewById(R.id.download_panel);
        progressBar = findViewById(R.id.download_progress_bar);
        tvPercent = findViewById(R.id.tv_progress_percent);
        tvSpeed = findViewById(R.id.tv_speed);
        tvEta = findViewById(R.id.tv_eta);
        tvFinished = findViewById(R.id.tv_finished);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }

    private Spanned parseMarkdown(String md) {
        if (md == null) md = "";
        // Simple Markdown to HTML conversion
        String html = md
                // Headers
                .replaceAll("(?m)^### (.*)$", "<h3>$1</h3>")
                .replaceAll("(?m)^## (.*)$", "<h2>$1</h2>")
                .replaceAll("(?m)^# (.*)$", "<h1>$1</h1>")
                // Bold
                .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")
                // Newlines
                .replace("\n", "<br/>");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(html);
        }
    }

    private void startDownload() {
        if (isDownloading) return;
        isDownloading = true;

        // Animate Dropdown
        downloadPanel.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); 
        fadeIn.setDuration(500);
        downloadPanel.startAnimation(fadeIn);

        btnDownload.setEnabled(false);
        btnDownload.setText("Downloading...");

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        apkFile = new File(downloadsDir, "SquireVR_Update.apk");
        if (apkFile.exists()) apkFile.delete();

        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

        Request request = new Request.Builder().url(downloadUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(UpdateActivity.this, "Download Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    isDownloading = false;
                    btnDownload.setEnabled(true);
                    btnDownload.setText("Retry Download");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Server Error: " + response.code()));
                    return;
                }

                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(apkFile);
                    long total = response.body().contentLength();
                    byte[] buffer = new byte[4096];
                    long downloaded = 0;
                    int read;
                    long startTime = System.currentTimeMillis();

                    while ((read = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloaded += read;

                        long now = System.currentTimeMillis();
                        long elapsed = now - startTime;
                        
                        if (elapsed > 500) { // Update UI every 500ms
                            final long finalDownloaded = downloaded;
                            final long finalTotal = total;
                            final double speed = (downloaded / 1024.0 / 1024.0) / (elapsed / 1000.0); // MB/s
                            
                            runOnUiThread(() -> updateProgress(finalDownloaded, finalTotal, speed));
                        }
                    }

                    // Done
                    runOnUiThread(() -> onDownloadComplete());

                } catch (Exception e) {
                    onFailure(call, new IOException(e));
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }

    private void updateProgress(long current, long total, double speedMb) {
        if (total <= 0) return;
        int progress = (int) ((current * 100) / total);
        progressBar.setProgress(progress);
        tvPercent.setText(progress + "%");
        tvSpeed.setText(String.format("%.2f MB/s", speedMb));
        
        long remainingBytes = total - current;
        double speedBytesPerSec = speedMb * 1024 * 1024;
        if (speedBytesPerSec > 0) {
            long secondsLeft = (long) (remainingBytes / speedBytesPerSec);
            tvEta.setText(String.format("%ds left", secondsLeft));
        }
    }

    private void onDownloadComplete() {
        progressBar.setProgress(100);
        tvPercent.setText("100%");
        tvEta.setText("0s");
        tvFinished.setVisibility(View.VISIBLE);
        
        // Fade out dropdown after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setDuration(500);
            downloadPanel.startAnimation(fadeOut);
            downloadPanel.setVisibility(View.GONE);
            
            installApk();
        }, 2000);
    }

    private void installApk() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this, "com.squire.vr.provider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Install Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Blocking interaction
    }
}
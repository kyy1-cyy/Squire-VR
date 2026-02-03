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
    private LinearLayout instructionsPanel;
    private ProgressBar progressBar;
    private TextView tvPercent, tvSpeed, tvEta, instructionStep3;
    private Button btnDownload, btnClose;
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
        btnClose = findViewById(R.id.btn_close);
        
        downloadPanel = findViewById(R.id.download_panel);
        instructionsPanel = findViewById(R.id.instructions_panel);
        
        progressBar = findViewById(R.id.download_progress_bar);
        tvPercent = findViewById(R.id.tv_percent);
        tvSpeed = findViewById(R.id.tv_speed);
        tvEta = findViewById(R.id.tv_eta);
        instructionStep3 = findViewById(R.id.instruction_step_3);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity(); // Close app so they can uninstall
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

        // Hide button
        btnDownload.setVisibility(View.GONE);

        // Animate Dropdown
        downloadPanel.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); 
        fadeIn.setDuration(500);
        downloadPanel.startAnimation(fadeIn);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Use a consistent name or timestamped name? 
        // User asked to "click on [actual .apk name]" so let's try to preserve name or give it a clear one
        String fileName = "SquireVR_" + System.currentTimeMillis() + ".apk";
        try {
             if (downloadUrl != null && downloadUrl.contains("/")) {
                 String rawName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                 if (rawName.endsWith(".apk")) {
                     fileName = rawName;
                 }
             }
        } catch (Exception e) {}
        
        apkFile = new File(downloadsDir, fileName);
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
                    btnDownload.setVisibility(View.VISIBLE);
                    btnDownload.setText("Retry Download");
                    downloadPanel.setVisibility(View.GONE);
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
                        
                        if (elapsed > 200) { // Update UI frequently
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
        
        // Fade out download panel
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                downloadPanel.setVisibility(View.GONE);
                showInstructions();
            }
        });
        downloadPanel.startAnimation(fadeOut);
    }

    private void showInstructions() {
        if (apkFile != null) {
            instructionStep3.setText("3. Click on [" + apkFile.getName() + "] to install it.");
        }
        
        instructionsPanel.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        instructionsPanel.startAnimation(fadeIn);
    }

    @Override
    public void onBackPressed() {
        // Blocking interaction
    }
}

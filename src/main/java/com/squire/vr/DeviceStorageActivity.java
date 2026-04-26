package com.squire.vr;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.util.Locale;

public class DeviceStorageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_device_storage);

            // Bind Views
            TextView percentText = findViewById(R.id.storage_percent_text);
            ProgressBar progressBar = findViewById(R.id.storage_progress_bar);
            TextView detailText = findViewById(R.id.storage_detail_text);
            TextView freeText = findViewById(R.id.storage_free_text);
            Button btnClose = findViewById(R.id.btn_close);

            // Close Button Logic
            if (btnClose != null) {
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            // Storage Calculation Logic
            if (percentText != null && progressBar != null && detailText != null && freeText != null) {
                try {
                    File path = Environment.getExternalStorageDirectory();
                    StatFs stat = new StatFs(path.getPath());
                    
                    long blockSize = stat.getBlockSizeLong();
                    long totalBlocks = stat.getBlockCountLong();
                    long availableBlocks = stat.getAvailableBlocksLong();

                    long totalBytes = totalBlocks * blockSize;
                    long availableBytes = availableBlocks * blockSize;
                    long usedBytes = totalBytes - availableBytes;
                    
                    double totalGB = totalBytes / (1024.0 * 1024.0 * 1024.0);
                    double usedGB = usedBytes / (1024.0 * 1024.0 * 1024.0);
                    double freeGB = availableBytes / (1024.0 * 1024.0 * 1024.0);
                    
                    int usedPercent = (int) ((usedBytes * 100) / totalBytes);

                    // Update UI
                    percentText.setText(usedPercent + "%");
                    progressBar.setProgress(usedPercent);
                    detailText.setText(String.format(Locale.US, "Used: %.1f GB / %.1f GB", usedGB, totalGB));
                    freeText.setText(String.format(Locale.US, "%.1f GB Free Space", freeGB));
                    
                } catch (Exception e) {
                    detailText.setText("Storage info unavailable");
                    freeText.setText(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }
}
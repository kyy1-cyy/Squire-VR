package com.squire.vr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.lang.reflect.Method;
import rikka.shizuku.Shizuku;

/* loaded from: classes3.dex */
public class SquireManagerActivity extends AppCompatActivity {
    private ImageView statusIcon;
    private TextView statusSubtitle;
    private TextView statusTitle;
    private boolean isRunning = false;
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = new Shizuku.OnRequestPermissionResultListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda8
        @Override // rikka.shizuku.Shizuku.OnRequestPermissionResultListener
        public final void onRequestPermissionResult(int i, int i2) {
            this.f$0.lambda$new$0(i, i2);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int requestCode, int grantResult) {
        if (grantResult == 0) {
            checkShizukuStatus();
        } else {
            Toast.makeText(this, "Shizuku permission denied", 0).show();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squire_manager);
        Shizuku.addRequestPermissionResultListener(this.REQUEST_PERMISSION_RESULT_LISTENER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$1(view);
            }
        });
        this.statusTitle = (TextView) findViewById(R.id.status_title);
        this.statusSubtitle = (TextView) findViewById(R.id.status_subtitle);
        this.statusIcon = (ImageView) findViewById(R.id.status_icon);
        findViewById(R.id.btn_start_wireless).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$2(view);
            }
        });
        findViewById(R.id.btn_start_root).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$3(view);
            }
        });
        findViewById(R.id.btn_authorized_apps).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$4(view);
            }
        });
        findViewById(R.id.btn_terminal).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$5(view);
            }
        });
        findViewById(R.id.btn_pair).setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onCreate$6(view);
            }
        });
        checkShizukuStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View v) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View v) {
        checkShizukuPermission();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(View v) {
        checkShizukuPermission();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$4(View v) {
        Toast.makeText(this, "Managed via Shizuku App", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$5(View v) {
        testObbAccess();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$6(View v) {
        Toast.makeText(this, "Please use the official Shizuku app to pair first.", 1).show();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(this.REQUEST_PERMISSION_RESULT_LISTENER);
    }

    private void checkShizukuPermission() {
        if (Shizuku.isPreV11()) {
            Toast.makeText(this, "Shizuku version too old", 0).show();
            return;
        }
        if (Shizuku.checkSelfPermission() == 0) {
            checkShizukuStatus();
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            Toast.makeText(this, "We need Shizuku access to manage files.", 1).show();
            Shizuku.requestPermission(0);
        } else {
            Shizuku.requestPermission(0);
        }
    }

    private void checkShizukuStatus() {
        try {
            if (Shizuku.pingBinder()) {
                this.isRunning = true;
                updateStatusUI();
            } else {
                this.isRunning = false;
                updateStatusUI();
            }
        } catch (Exception e) {
            this.isRunning = false;
            updateStatusUI();
        }
    }

    private void testObbAccess() {
        if (!this.isRunning) {
            Toast.makeText(this, "Service not running!", 0).show();
        } else {
            new Thread(new Runnable() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() throws InterruptedException, NoSuchMethodException, SecurityException {
                    this.f$0.lambda$testObbAccess$9();
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$testObbAccess$9() throws InterruptedException, NoSuchMethodException, SecurityException {
        try {
            Method method = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
            method.setAccessible(true);
            Process process = (Process) method.invoke(null, new String[]{"sh", "-c", "ls -l /sdcard/Android/obb"}, null, null);
            process.waitFor();
            final int exitCode = process.exitValue();
            runOnUiThread(new Runnable() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$testObbAccess$7(exitCode);
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() { // from class: com.squire.vr.SquireManagerActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$testObbAccess$8(e);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$testObbAccess$7(int exitCode) {
        if (exitCode == 0) {
            Toast.makeText(this, "Success! Can access /Android/obb", 1).show();
        } else {
            Toast.makeText(this, "Failed to list /Android/obb (Code: " + exitCode + ")", 1).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$testObbAccess$8(Exception e) {
        Toast.makeText(this, "Error: " + e.getMessage(), 0).show();
    }

    private void showPairingDialog() {
        Toast.makeText(this, "Use Shizuku App to Pair", 0).show();
    }

    private void startService(String method) {
        checkShizukuPermission();
    }

    private void updateStatusUI() {
        if (this.isRunning) {
            this.statusTitle.setText("Shizuku Connected");
            this.statusSubtitle.setText("Squire has ADB access.");
            this.statusTitle.setTextColor(-11751600);
            this.statusIcon.setImageResource(android.R.drawable.presence_online);
            this.statusIcon.setColorFilter(-11751600);
            return;
        }
        this.statusTitle.setText("Service Disconnected");
        this.statusSubtitle.setText("Start Shizuku App first.");
        this.statusTitle.setTextColor(-1);
        this.statusIcon.setImageResource(android.R.drawable.ic_delete);
        this.statusIcon.setColorFilter(-48060);
    }
}

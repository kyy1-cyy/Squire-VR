package com.squire.vr;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.bumptech.glide.load.Key;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import kotlin.UByte;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class StreamingService extends Service {
    private static final String TAG = "TRDStreaming";
    private volatile boolean isStopped = false;
    private volatile Process p7zipProcess;
    private volatile Process rcloneProcess;

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String releaseName = intent.getStringExtra("releaseName");
        String rcloneUrl = intent.getStringExtra("rcloneUrl");
        if (rcloneUrl == null && releaseName != null) {
            try {
                byte[] bArrDigest = MessageDigest.getInstance("MD5").digest((releaseName + "\n").getBytes(Key.STRING_CHARSET_NAME));
                StringBuilder sb = new StringBuilder();
                for (byte b : bArrDigest) {
                    String hexString = Integer.toHexString(b & UByte.MAX_VALUE);
                    if (hexString.length() == 1) {
                        sb.append('0');
                    }
                    sb.append(hexString);
                }
                rcloneUrl = sb.toString();
            } catch (Exception unused) {
                rcloneUrl = releaseName;
            }
        }
        String baseUri = intent.getStringExtra("baseUri");
        if (baseUri == null) {
            baseUri = Config.MIRROR_BASEURI_URL;
        }
        final String savePath = intent.getStringExtra("savePath");
        final long totalBytes = intent.getLongExtra("totalBytes", 0L);
        String password = intent.getStringExtra("password");
        if (password == null) {
            password = Config.VRP_DEFAULT_PASSWORD;
        }
        final String password2 = password;
        final String finalRcloneUrl = rcloneUrl;
        final String finalBaseUri = baseUri;
        this.isStopped = false;
        new Thread(new Runnable() { // from class: com.squire.vr.StreamingService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$onStartCommand$0(releaseName, finalRcloneUrl, finalBaseUri, savePath, totalBytes, password2);
            }
        }).start();
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x05ab, code lost:
    
        r14 = r2;
        r40 = r4;
        r13 = r9;
        r9 = r3;
        r12 = r10;
        r3 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x05ba, code lost:
    
        if (r45.isStopped == false) goto L223;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x05c0, code lost:
    
        if (r3.exists() == false) goto L217;
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x05c2, code lost:
    
        deleteDir(r3);
        r14.append(r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x05ca, code lost:
    
        if (r45.isStopped == false) goto L356;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x05cc, code lost:
    
        r0 = new java.io.File(r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x05d5, code lost:
    
        if (r0.exists() == false) goto L357;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x05d7, code lost:
    
        android.util.Log.d(r9, r12 + r49);
        deleteDir(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x05ee, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x05ef, code lost:
    
        r14.append("[EXTRACT] Starting 7z extraction from disk\n");
        r10 = new java.io.File((java.io.File) r3, ((org.json.JSONObject) r0.get(0)).optString("Name"));
        r26 = java.lang.System.currentTimeMillis();
        r0 = new java.lang.String[7];
        r0[0] = r17;
        r0[1] = "x";
        r0[2] = r10.getAbsolutePath();
        r0[3] = "-y";
        r0[4] = "-o" + r49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x064d, code lost:
    
        r0[5] = "-p" + r52;
        r0[6] = "-bsp1";
        r2 = new java.lang.ProcessBuilder(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0665, code lost:
    
        r2.environment().put("LD_LIBRARY_PATH", r40);
        r45.p7zipProcess = r2.start();
        r1 = new java.lang.StringBuilder();
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x067f, code lost:
    
        new java.lang.Thread(new com.squire.vr.StreamingService$$ExternalSyntheticLambda2(r45, r46, r26)).start();
        new java.lang.Thread(new com.squire.vr.StreamingService$$ExternalSyntheticLambda3(r45, r1)).start();
        r2 = r45.p7zipProcess.waitFor();
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x069d, code lost:
    
        if (r45.isStopped == false) goto L240;
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x06a3, code lost:
    
        if (r3.exists() == false) goto L234;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x06a5, code lost:
    
        deleteDir(r3);
        r14.append(r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x06ad, code lost:
    
        if (r45.isStopped == false) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x06af, code lost:
    
        r6 = new java.io.File(r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x06b8, code lost:
    
        if (r6.exists() == false) goto L359;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x06ba, code lost:
    
        android.util.Log.d(r9, r12 + r49);
        deleteDir(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x06d1, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x06d2, code lost:
    
        if (r2 != 0) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x06d4, code lost:
    
        broadcastComplete(r46, r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x06db, code lost:
    
        if (r3.exists() == false) goto L245;
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x06dd, code lost:
    
        deleteDir(r3);
        r14.append(r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:246:0x06e5, code lost:
    
        if (r45.isStopped == false) goto L280;
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x06e7, code lost:
    
        r0 = new java.io.File(r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:248:0x06f0, code lost:
    
        if (r0.exists() == false) goto L362;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x06f2, code lost:
    
        r1 = new java.lang.StringBuilder();
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x0721, code lost:
    
        throw new java.lang.Exception("7z Exit " + r2 + "\n7z Err: " + r1.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x0722, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x0727, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x072b, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x074f, code lost:
    
        android.util.Log.e(r9, "Buffered Streaming failed", r0);
        broadcastError("Error: " + r0.getMessage());
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x0774, code lost:
    
        deleteDir(r3);
        r14.append(r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x077e, code lost:
    
        r0 = new java.io.File(r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0787, code lost:
    
        if (r0.exists() != false) goto L277;
     */
    /* JADX WARN: Code restructure failed: missing block: B:277:0x0789, code lost:
    
        r1 = new java.lang.StringBuilder();
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x07a1, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x07a9, code lost:
    
        deleteDir(r3);
        r14.append(r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:287:0x07b3, code lost:
    
        r1 = new java.io.File(r49);
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x07bc, code lost:
    
        if (r1.exists() != false) goto L289;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x07be, code lost:
    
        android.util.Log.d(r9, r12 + r49);
        deleteDir(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:356:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:357:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:358:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:359:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:360:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:362:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:363:?, code lost:
    
        return;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0487  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x074f A[Catch: all -> 0x07a2, TRY_LEAVE, TryCatch #1 {all -> 0x07a2, blocks: (B:267:0x074b, B:269:0x074f, B:225:0x064d, B:229:0x067f, B:241:0x06d4, B:250:0x06f9, B:251:0x0721), top: B:291:0x004f }] */
    /* JADX WARN: Removed duplicated region for block: B:272:0x0774  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x077e  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x07a9  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x07b3  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x0521 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:333:0x044d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:354:? A[Catch: all -> 0x072d, Exception -> 0x0732, SYNTHETIC, TryCatch #30 {Exception -> 0x0732, all -> 0x072d, blocks: (B:158:0x048b, B:161:0x04a4, B:162:0x04c0, B:187:0x052a, B:186:0x0527, B:212:0x05ab, B:223:0x05ef, B:182:0x0521), top: B:316:0x048b, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:360:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0104 A[Catch: all -> 0x0736, Exception -> 0x0741, TRY_ENTER, TRY_LEAVE, TryCatch #27 {Exception -> 0x0741, all -> 0x0736, blocks: (B:3:0x004f, B:14:0x0086, B:25:0x00c8, B:36:0x0104, B:48:0x0153, B:49:0x015e, B:73:0x01c5, B:86:0x020e, B:87:0x0223), top: B:291:0x004f }] */
    /* JADX WARN: Type inference failed for: r14v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r14v1 */
    /* JADX WARN: Type inference failed for: r14v10 */
    /* JADX WARN: Type inference failed for: r14v11 */
    /* JADX WARN: Type inference failed for: r14v12 */
    /* JADX WARN: Type inference failed for: r14v13 */
    /* JADX WARN: Type inference failed for: r14v14 */
    /* JADX WARN: Type inference failed for: r14v15 */
    /* JADX WARN: Type inference failed for: r14v16 */
    /* JADX WARN: Type inference failed for: r14v17 */
    /* JADX WARN: Type inference failed for: r14v18 */
    /* JADX WARN: Type inference failed for: r14v19 */
    /* JADX WARN: Type inference failed for: r14v2 */
    /* JADX WARN: Type inference failed for: r14v20 */
    /* JADX WARN: Type inference failed for: r14v21 */
    /* JADX WARN: Type inference failed for: r14v22 */
    /* JADX WARN: Type inference failed for: r14v23 */
    /* JADX WARN: Type inference failed for: r14v24 */
    /* JADX WARN: Type inference failed for: r14v25 */
    /* JADX WARN: Type inference failed for: r14v26 */
    /* JADX WARN: Type inference failed for: r14v29 */
    /* JADX WARN: Type inference failed for: r14v3, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r14v30, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r14v33 */
    /* JADX WARN: Type inference failed for: r14v4, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r14v41 */
    /* JADX WARN: Type inference failed for: r14v42 */
    /* JADX WARN: Type inference failed for: r14v5 */
    /* JADX WARN: Type inference failed for: r14v6 */
    /* JADX WARN: Type inference failed for: r14v8, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r14v9 */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v16 */
    /* JADX WARN: Type inference failed for: r3v18 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v20 */
    /* JADX WARN: Type inference failed for: r3v21 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v24 */
    /* JADX WARN: Type inference failed for: r3v25 */
    /* JADX WARN: Type inference failed for: r3v26 */
    /* JADX WARN: Type inference failed for: r3v27 */
    /* JADX WARN: Type inference failed for: r3v28 */
    /* JADX WARN: Type inference failed for: r3v29 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v34 */
    /* JADX WARN: Type inference failed for: r3v35 */
    /* JADX WARN: Type inference failed for: r3v36 */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v40 */
    /* JADX WARN: Type inference failed for: r3v41 */
    /* JADX WARN: Type inference failed for: r3v42 */
    /* JADX WARN: Type inference failed for: r3v43 */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r3v54 */
    /* JADX WARN: Type inference failed for: r3v55 */
    /* JADX WARN: Type inference failed for: r3v6, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r3v9, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r45v0, types: [com.squire.vr.StreamingService] */
    /* renamed from: startStreaming, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void lambda$onStartCommand$0(final String str, String str2, String str3, String str4, long j, String str5) throws Throwable {
        StringBuilder sb;
        File file;
        InputStream inputStream;
        Throwable th;
        long j2;
        StringBuilder sb2;
        File file2;
        FileOutputStream fileOutputStream;
        Throwable th2;
        String str6;
        String str7;
        File file3;
        byte[] bArr;
        String str8;
        FileOutputStream fileOutputStream2;
        String str9;
        File file4;
        StringBuilder sb3;
        File file5;
        byte[] bArr2;
        String str10;
        long j3;
        String str11 = str;
        String str12 = str2;
        ?? r14 = str3;
        String str13 = "Cleanup: Deleting partial game folder due to stop: ";
        String str14 = "[CLEANUP] Temp folder removed\n";
        ?? r3 = TAG;
        String str15 = getApplicationInfo().nativeLibraryDir;
        String str16 = str15 + "/librclone.so";
        String str17 = str15 + "/lib7z.so";
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Mode: Buffered Streaming (Disk-to-Disk)\n");
        File file6 = new File(str4, "temp_archives");
        try {
            try {
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e) {
            e = e;
            r14 = sb4;
            str12 = str14;
            str11 = str13;
            str14 = r3;
            r3 = file6;
        } catch (Throwable th4) {
            th = th4;
            r14 = sb4;
            str12 = str14;
            str11 = str13;
            str14 = r3;
            r3 = file6;
        }
        if (this.isStopped) {
            if (file6.exists()) {
                deleteDir(file6);
                sb4.append("[CLEANUP] Temp folder removed\n");
            }
            if (this.isStopped) {
                File file7 = new File(str4);
                if (file7.exists()) {
                    Log.d(TAG, "Cleanup: Deleting partial game folder due to stop: " + str4);
                    deleteDir(file7);
                    return;
                }
                return;
            }
            return;
        }
        if (file6.exists()) {
            JSONArray jSONArrayListFiles = listFiles(str12, r14);
            if (!this.isStopped) {
            }
        } else {
            try {
                if (!file6.mkdirs()) {
                    throw new Exception("Failed to create temp directory at " + file6.getAbsolutePath());
                }
                JSONArray jSONArrayListFiles2 = listFiles(str12, r14);
                if (!this.isStopped) {
                    if (file6.exists()) {
                        deleteDir(file6);
                        sb4.append("[CLEANUP] Temp folder removed\n");
                    }
                    if (this.isStopped) {
                        File file8 = new File(str4);
                        if (file8.exists()) {
                            Log.d(TAG, "Cleanup: Deleting partial game folder due to stop: " + str4);
                            deleteDir(file8);
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (jSONArrayListFiles2.length() != 0) {
                    ArrayList arrayList = new ArrayList();
                    int i = 0;
                    long jOptLong = 0;
                    while (true) {
                        boolean z = true;
                        if (i >= jSONArrayListFiles2.length()) {
                            break;
                        }
                        JSONObject jSONObject = jSONArrayListFiles2.getJSONObject(i);
                        String strOptString = jSONObject.optString("Name");
                        if (!strOptString.matches(".*\\.\\d{3}$") && !strOptString.endsWith(".7z") && !strOptString.endsWith(".zip") && !strOptString.endsWith(".rar") && !strOptString.endsWith(".obvr")) {
                            z = false;
                        }
                        boolean z2 = z;
                        if (!jSONObject.optBoolean("IsDir") && z2) {
                            arrayList.add(jSONObject);
                            jOptLong += jSONObject.optLong("Size", 0L);
                        }
                        i++;
                    }
                    Collections.sort(arrayList, new Comparator() { // from class: com.squire.vr.StreamingService$$ExternalSyntheticLambda1
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            return ((JSONObject) obj).optString("Name").compareTo(((JSONObject) obj2).optString("Name"));
                        }
                    });
                    if (!arrayList.isEmpty()) {
                        long j4 = jOptLong;
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        long j5 = 0;
                        byte[] bArr3 = new byte[524288];
                        Iterator it = arrayList.iterator();
                        r3 = r3;
                        String str18 = r14;
                        while (true) {
                            long j6 = j4;
                            if (!it.hasNext()) {
                                break;
                            }
                            try {
                                JSONObject jSONObject2 = (JSONObject) it.next();
                                if (this.isStopped) {
                                    if (file6.exists()) {
                                        deleteDir(file6);
                                        sb4.append(str14);
                                    }
                                    if (this.isStopped) {
                                        File file9 = new File(str4);
                                        if (file9.exists()) {
                                            Log.d(r3, str13 + str4);
                                            deleteDir(file9);
                                            return;
                                        }
                                        return;
                                    }
                                    return;
                                }
                                String str19 = r3;
                                try {
                                    Iterator it2 = it;
                                    String strOptString2 = jSONObject2.optString("Name");
                                    File file10 = new File(file6, strOptString2);
                                    File file11 = file6;
                                    try {
                                        StringBuilder sb5 = sb4;
                                        try {
                                            sb4.append("[DOWN] ").append(strOptString2).append("\n");
                                            String[] strArr = new String[9];
                                            strArr[0] = str16;
                                            strArr[1] = "cat";
                                            strArr[2] = "--config";
                                            strArr[3] = "/dev/null";
                                            strArr[4] = "--user-agent";
                                            strArr[5] = "rclone/v1.68.2";
                                            strArr[6] = "--http-url";
                                            strArr[7] = str18;
                                            String str20 = str14;
                                            try {
                                                strArr[8] = ":http:" + str2 + "/" + strOptString2;
                                                ProcessBuilder processBuilder = new ProcessBuilder(strArr);
                                                processBuilder.environment().put("LD_LIBRARY_PATH", str15);
                                                this.rcloneProcess = processBuilder.start();
                                                InputStream inputStream2 = this.rcloneProcess.getInputStream();
                                                try {
                                                    FileOutputStream fileOutputStream3 = new FileOutputStream(file10);
                                                    j2 = j5;
                                                    long j7 = 0;
                                                    while (true) {
                                                        try {
                                                            int i2 = inputStream2.read(bArr3);
                                                            File file12 = file10;
                                                            if (i2 == -1) {
                                                                str6 = str15;
                                                                str7 = str19;
                                                                file3 = file11;
                                                                r14 = sb5;
                                                                bArr = bArr3;
                                                                str8 = strOptString2;
                                                                str12 = str20;
                                                                str11 = str13;
                                                                long j8 = j6;
                                                                inputStream = inputStream2;
                                                                fileOutputStream2 = fileOutputStream3;
                                                                j4 = j8;
                                                                break;
                                                            }
                                                            try {
                                                                if (this.isStopped) {
                                                                    str6 = str15;
                                                                    str7 = str19;
                                                                    file3 = file11;
                                                                    r14 = sb5;
                                                                    bArr = bArr3;
                                                                    str8 = strOptString2;
                                                                    str12 = str20;
                                                                    str11 = str13;
                                                                    long j9 = j6;
                                                                    inputStream = inputStream2;
                                                                    fileOutputStream2 = fileOutputStream3;
                                                                    j4 = j9;
                                                                    break;
                                                                }
                                                                fileOutputStream3.write(bArr3, 0, i2);
                                                                String str21 = str15;
                                                                long j10 = j2 + i2;
                                                                try {
                                                                    long jCurrentTimeMillis2 = System.currentTimeMillis();
                                                                    if (jCurrentTimeMillis2 - j7 > 500) {
                                                                        File file13 = file11;
                                                                        sb3 = sb5;
                                                                        bArr2 = bArr3;
                                                                        String str22 = str19;
                                                                        file5 = file12;
                                                                        file4 = file13;
                                                                        str10 = strOptString2;
                                                                        str12 = str20;
                                                                        str9 = str22;
                                                                        str11 = str13;
                                                                        long j11 = j6;
                                                                        inputStream = inputStream2;
                                                                        fileOutputStream = fileOutputStream3;
                                                                        j3 = j11;
                                                                        try {
                                                                            broadcastProgress(str, j10, j3, jCurrentTimeMillis, "Downloading Archives...", "download");
                                                                            j7 = jCurrentTimeMillis2;
                                                                        } catch (Throwable th5) {
                                                                            th2 = th5;
                                                                            file2 = file4;
                                                                            str14 = str9;
                                                                            sb2 = sb3;
                                                                            try {
                                                                                try {
                                                                                    fileOutputStream.close();
                                                                                    throw th2;
                                                                                } catch (Throwable th6) {
                                                                                    th2.addSuppressed(th6);
                                                                                    throw th2;
                                                                                }
                                                                            } catch (Throwable th7) {
                                                                                th = th7;
                                                                                r3 = file2;
                                                                                r14 = sb2;
                                                                                if (inputStream != null) {
                                                                                    throw th;
                                                                                }
                                                                                try {
                                                                                    inputStream.close();
                                                                                    throw th;
                                                                                } catch (Throwable th8) {
                                                                                    th.addSuppressed(th8);
                                                                                    throw th;
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        str9 = str19;
                                                                        file4 = file11;
                                                                        sb3 = sb5;
                                                                        file5 = file12;
                                                                        bArr2 = bArr3;
                                                                        str10 = strOptString2;
                                                                        str12 = str20;
                                                                        str11 = str13;
                                                                        long j12 = j6;
                                                                        inputStream = inputStream2;
                                                                        fileOutputStream = fileOutputStream3;
                                                                        j3 = j12;
                                                                    }
                                                                    str13 = str11;
                                                                    str20 = str12;
                                                                    j2 = j10;
                                                                    bArr3 = bArr2;
                                                                    file10 = file5;
                                                                    strOptString2 = str10;
                                                                    str15 = str21;
                                                                    sb5 = sb3;
                                                                    file11 = file4;
                                                                    str19 = str9;
                                                                    long j13 = j3;
                                                                    inputStream2 = inputStream;
                                                                    fileOutputStream3 = fileOutputStream;
                                                                    j6 = j13;
                                                                } catch (Throwable th9) {
                                                                    sb2 = sb5;
                                                                    str12 = str20;
                                                                    str11 = str13;
                                                                    inputStream = inputStream2;
                                                                    fileOutputStream = fileOutputStream3;
                                                                    th2 = th9;
                                                                    file2 = file11;
                                                                    str14 = str19;
                                                                }
                                                            } catch (Throwable th10) {
                                                                sb2 = sb5;
                                                                str12 = str20;
                                                                str11 = str13;
                                                                inputStream = inputStream2;
                                                                fileOutputStream = fileOutputStream3;
                                                                file2 = file11;
                                                                str14 = str19;
                                                                th2 = th10;
                                                            }
                                                        } catch (Throwable th11) {
                                                            str14 = str19;
                                                            sb2 = sb5;
                                                            str12 = str20;
                                                            file2 = file11;
                                                            str11 = str13;
                                                            inputStream = inputStream2;
                                                            fileOutputStream = fileOutputStream3;
                                                            th2 = th11;
                                                        }
                                                    }
                                                } catch (Throwable th12) {
                                                    str14 = str19;
                                                    r14 = sb5;
                                                    str12 = str20;
                                                    r3 = file11;
                                                    str11 = str13;
                                                    inputStream = inputStream2;
                                                    th = th12;
                                                }
                                                try {
                                                    fileOutputStream2.close();
                                                    if (inputStream != null) {
                                                        try {
                                                            inputStream.close();
                                                        } catch (Exception e2) {
                                                            e = e2;
                                                            r3 = file3;
                                                            str14 = str7;
                                                            if (!this.isStopped) {
                                                            }
                                                            if (r3.exists()) {
                                                            }
                                                            if (this.isStopped) {
                                                            }
                                                        } catch (Throwable th13) {
                                                            th = th13;
                                                            r3 = file3;
                                                            str14 = str7;
                                                            if (r3.exists()) {
                                                            }
                                                            if (this.isStopped) {
                                                            }
                                                            throw th;
                                                        }
                                                        try {
                                                            if (!this.isStopped) {
                                                                if (file3.exists()) {
                                                                    deleteDir(file3);
                                                                    r14.append(str12);
                                                                }
                                                                if (this.isStopped) {
                                                                    File file14 = new File(str4);
                                                                    if (file14.exists()) {
                                                                        Log.d(str7, str11 + str4);
                                                                        deleteDir(file14);
                                                                        return;
                                                                    }
                                                                    return;
                                                                }
                                                                return;
                                                            }
                                                            r3 = file3;
                                                            str14 = str7;
                                                            try {
                                                                if (this.rcloneProcess.waitFor() != 0) {
                                                                    throw new Exception("Rclone download failed for " + str8);
                                                                }
                                                                j5 = j2;
                                                                file6 = r3;
                                                                r3 = str14;
                                                                str13 = str11;
                                                                str14 = str12;
                                                                sb4 = r14;
                                                                bArr3 = bArr;
                                                                it = it2;
                                                                str15 = str6;
                                                                str18 = str3;
                                                            } catch (Exception e3) {
                                                                e = e3;
                                                                if (!this.isStopped) {
                                                                }
                                                                if (r3.exists()) {
                                                                }
                                                                if (this.isStopped) {
                                                                }
                                                            } catch (Throwable th14) {
                                                                th = th14;
                                                                if (r3.exists()) {
                                                                }
                                                                if (this.isStopped) {
                                                                }
                                                                throw th;
                                                            }
                                                        } catch (Exception e4) {
                                                            e = e4;
                                                            r3 = file3;
                                                            str14 = str7;
                                                            if (!this.isStopped) {
                                                            }
                                                            if (r3.exists()) {
                                                            }
                                                            if (this.isStopped) {
                                                            }
                                                        } catch (Throwable th15) {
                                                            th = th15;
                                                            r3 = file3;
                                                            str14 = str7;
                                                            if (r3.exists()) {
                                                            }
                                                            if (this.isStopped) {
                                                            }
                                                            throw th;
                                                        }
                                                    } else if (!this.isStopped) {
                                                    }
                                                } catch (Throwable th16) {
                                                    r3 = file3;
                                                    str14 = str7;
                                                    th = th16;
                                                    r14 = r14;
                                                    if (inputStream != null) {
                                                    }
                                                }
                                            } catch (Exception e5) {
                                                e = e5;
                                                str11 = str13;
                                                str14 = str19;
                                                r3 = file11;
                                                r14 = sb5;
                                                str12 = str20;
                                                if (!this.isStopped) {
                                                }
                                                if (r3.exists()) {
                                                }
                                                if (this.isStopped) {
                                                }
                                            } catch (Throwable th17) {
                                                th = th17;
                                                str11 = str13;
                                                str14 = str19;
                                                r3 = file11;
                                                r14 = sb5;
                                                str12 = str20;
                                                if (r3.exists()) {
                                                }
                                                if (this.isStopped) {
                                                }
                                                throw th;
                                            }
                                        } catch (Exception e6) {
                                            e = e6;
                                            str12 = str14;
                                            str11 = str13;
                                            str14 = str19;
                                            r3 = file11;
                                            r14 = sb5;
                                        } catch (Throwable th18) {
                                            th = th18;
                                            str12 = str14;
                                            str11 = str13;
                                            str14 = str19;
                                            r3 = file11;
                                            r14 = sb5;
                                        }
                                    } catch (Exception e7) {
                                        e = e7;
                                        r14 = sb4;
                                        str12 = str14;
                                        str11 = str13;
                                        str14 = str19;
                                        r3 = file11;
                                    } catch (Throwable th19) {
                                        th = th19;
                                        r14 = sb4;
                                        str12 = str14;
                                        str11 = str13;
                                        str14 = str19;
                                        r3 = file11;
                                    }
                                } catch (Exception e8) {
                                    e = e8;
                                    r3 = file6;
                                    r14 = sb4;
                                    str12 = str14;
                                    str11 = str13;
                                    str14 = str19;
                                    if (!this.isStopped) {
                                    }
                                    if (r3.exists()) {
                                    }
                                    if (this.isStopped) {
                                    }
                                } catch (Throwable th20) {
                                    th = th20;
                                    r3 = file6;
                                    r14 = sb4;
                                    str12 = str14;
                                    str11 = str13;
                                    str14 = str19;
                                    if (r3.exists()) {
                                    }
                                    if (this.isStopped) {
                                    }
                                    throw th;
                                }
                            } catch (Exception e9) {
                                e = e9;
                                r14 = sb4;
                                str12 = str14;
                                str11 = str13;
                                str14 = r3;
                                r3 = file6;
                            } catch (Throwable th21) {
                                th = th21;
                                r14 = sb4;
                                str12 = str14;
                                str11 = str13;
                                str14 = r3;
                                r3 = file6;
                            }
                        }
                    } else {
                        broadcastError("No archive files detected in folder.");
                        if (file6.exists()) {
                            deleteDir(file6);
                            sb4.append("[CLEANUP] Temp folder removed\n");
                        }
                        if (this.isStopped) {
                            File file15 = new File(str4);
                            if (file15.exists()) {
                                Log.d(TAG, "Cleanup: Deleting partial game folder due to stop: " + str4);
                                deleteDir(file15);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                } else {
                    broadcastError("No files found for game: " + str11);
                    if (file6.exists()) {
                        deleteDir(file6);
                        sb4.append("[CLEANUP] Temp folder removed\n");
                    }
                    if (this.isStopped) {
                        File file16 = new File(str4);
                        if (file16.exists()) {
                            Log.d(TAG, "Cleanup: Deleting partial game folder due to stop: " + str4);
                            deleteDir(file16);
                            return;
                        }
                        return;
                    }
                    return;
                }
            } catch (Exception e10) {
                e = e10;
                r14 = sb4;
                str12 = "[CLEANUP] Temp folder removed\n";
                str11 = "Cleanup: Deleting partial game folder due to stop: ";
                str14 = TAG;
                r3 = file6;
                if (!this.isStopped) {
                }
                if (r3.exists()) {
                }
                if (this.isStopped) {
                }
            } catch (Throwable th22) {
                th = th22;
                r14 = sb4;
                str12 = "[CLEANUP] Temp folder removed\n";
                str11 = "Cleanup: Deleting partial game folder due to stop: ";
                str14 = TAG;
                r3 = file6;
                if (r3.exists()) {
                }
                if (this.isStopped) {
                }
                throw th;
            }
        }
        Log.d(str14, sb.append(str11).append(str4).toString());
        deleteDir(file);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startStreaming$2(String releaseName, long extractStartTime) throws IOException {
        try {
            InputStream stm = this.p7zipProcess.getInputStream();
            try {
                byte[] lineBuf = new byte[128];
                int pos = 0;
                while (true) {
                    int b = stm.read();
                    if (b == -1 || this.isStopped) {
                        break;
                    }
                    if (b == 13 || b == 10 || pos >= lineBuf.length - 1) {
                        String line = new String(lineBuf, 0, pos);
                        int pctIdx = line.indexOf(37);
                        if (pctIdx > 0) {
                            int start = pctIdx - 1;
                            while (start >= 0 && Character.isDigit(line.charAt(start))) {
                                start--;
                            }
                            String numStr = line.substring(start + 1, pctIdx).trim();
                            if (!numStr.isEmpty()) {
                                int p = Integer.parseInt(numStr);
                                broadcastProgress(releaseName, (long) (100000 * (p / 100.0d)), 100000L, extractStartTime, "Extracting (" + p + "%)", "extract");
                            }
                        }
                        pos = 0;
                    } else {
                        lineBuf[pos] = (byte) b;
                        pos++;
                    }
                }
                if (stm != null) {
                    stm.close();
                }
            } finally {
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startStreaming$3(StringBuilder p7zError) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.p7zipProcess.getErrorStream()));
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        reader.close();
                        return;
                    }
                    p7zError.append(line).append("\n");
                } finally {
                }
            }
        } catch (Exception e) {
        }
    }

    private void deleteDir(File file) {
        File[] children;
        if (file.isDirectory() && (children = file.listFiles()) != null) {
            for (File child : children) {
                deleteDir(child);
            }
        }
        file.delete();
    }

    private JSONArray listFiles(String hash, String baseUri) throws Exception {
        String libPath = getApplicationInfo().nativeLibraryDir;
        String rclonePath = libPath + "/librclone.so";
        ProcessBuilder pb = new ProcessBuilder(rclonePath, "lsjson", "--config", "/dev/null", "--user-agent", "rclone/v1.68.2", "--http-url", baseUri, ":http:" + hash + "/");
        pb.environment().put("LD_LIBRARY_PATH", libPath);
        Process p = pb.start();
        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
        boolean zHasNext = s.hasNext();
        String errStr = HttpUrl.FRAGMENT_ENCODE_SET;
        String output = zHasNext ? s.next() : HttpUrl.FRAGMENT_ENCODE_SET;
        p.waitFor();
        if (p.exitValue() != 0) {
            Scanner err = new Scanner(p.getErrorStream()).useDelimiter("\\A");
            if (err.hasNext()) {
                errStr = err.next();
            }
            Log.e(TAG, "lsjson failed: " + errStr);
            throw new Exception("Failed to list files: " + errStr);
        }
        return new JSONArray(output);
    }

    private void broadcastProgress(String name, long current, long total, long startTime, String statusMsg, String phase) {
        if (this.isStopped) {
            return;
        }
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("name", name);
        intent.putExtra("current", current);
        intent.putExtra("total", total);
        intent.putExtra("statusMsg", statusMsg);
        intent.putExtra(TypedValues.CycleType.S_WAVE_PHASE, phase);
        int percent = total > 0 ? (int) ((100 * current) / total) : 0;
        intent.putExtra("progress_percent", percent);
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            long speed = (1000 * current) / elapsed;
            intent.putExtra("speed", speed);
        }
        sendBroadcast(intent);
    }

    private void broadcastError(String msg) {
        if (this.isStopped) {
            return;
        }
        Log.e(TAG, "Broadcasting error: " + msg);
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("error", msg);
        sendBroadcast(intent);
    }

    private void broadcastComplete(String releaseName, String savePath) {
        if (this.isStopped) {
            return;
        }
        Log.i(TAG, "Broadcasting completion for: " + releaseName);
        Intent intent = new Intent(Config.ACTION_PROGRESS);
        intent.putExtra("complete", true);
        intent.putExtra("name", releaseName);
        intent.putExtra("savePath", savePath);
        sendBroadcast(intent);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.squire.vr.StreamingService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$broadcastComplete$4();
            }
        }, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastComplete$4() {
        stopSelf();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.isStopped = true;
        if (this.rcloneProcess != null) {
            this.rcloneProcess.destroy();
            this.rcloneProcess = null;
        }
        if (this.p7zipProcess != null) {
            this.p7zipProcess.destroy();
            this.p7zipProcess = null;
        }
        super.onDestroy();
    }
}

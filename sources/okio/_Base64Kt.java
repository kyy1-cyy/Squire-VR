package okio;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;

/* compiled from: -Base64.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0012\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u000e\u0010\t\u001a\u0004\u0018\u00010\u0001*\u00020\nH\u0000\u001a\u0016\u0010\u000b\u001a\u00020\n*\u00020\u00012\b\b\u0002\u0010\f\u001a\u00020\u0001H\u0000\"\u001c\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005\"\u001c\u0010\u0006\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0007\u0010\u0003\u001a\u0004\b\b\u0010\u0005¨\u0006\r"}, d2 = {"BASE64", HttpUrl.FRAGMENT_ENCODE_SET, "getBASE64$annotations", "()V", "getBASE64", "()[B", "BASE64_URL_SAFE", "getBASE64_URL_SAFE$annotations", "getBASE64_URL_SAFE", "decodeBase64ToArray", HttpUrl.FRAGMENT_ENCODE_SET, "encodeBase64", "map", "okio"}, k = 2, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class _Base64Kt {
    private static final byte[] BASE64 = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/").getData$okio();
    private static final byte[] BASE64_URL_SAFE = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_").getData$okio();

    public static /* synthetic */ void getBASE64$annotations() {
    }

    public static /* synthetic */ void getBASE64_URL_SAFE$annotations() {
    }

    public static final byte[] getBASE64() {
        return BASE64;
    }

    public static final byte[] getBASE64_URL_SAFE() {
        return BASE64_URL_SAFE;
    }

    /* JADX WARN: Removed duplicated region for block: B:64:0x00b6 A[LOOP:1: B:16:0x003c->B:64:0x00b6, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x00b9 A[EDGE_INSN: B:84:0x00b9->B:65:0x00b9 BREAK  A[LOOP:1: B:16:0x003c->B:64:0x00b6], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final byte[] decodeBase64ToArray(String $this$decodeBase64ToArray) {
        char c;
        int bits;
        char c2;
        Intrinsics.checkNotNullParameter($this$decodeBase64ToArray, "<this>");
        int limit = $this$decodeBase64ToArray.length();
        while (true) {
            c = '\t';
            if (limit <= 0 || !((c2 = $this$decodeBase64ToArray.charAt(limit - 1)) == '=' || c2 == '\n' || c2 == '\r' || c2 == ' ' || c2 == '\t')) {
                break;
            }
            limit--;
        }
        byte[] out = new byte[(int) ((limit * 6) / 8)];
        int outCount = 0;
        int inCount = 0;
        int word = 0;
        if (limit > 0) {
            int i = 0;
            while (true) {
                int pos = i;
                i++;
                char c3 = $this$decodeBase64ToArray.charAt(pos);
                if ('A' <= c3 && c3 <= 'Z') {
                    bits = c3 - 'A';
                } else {
                    if ('a' <= c3 && c3 <= 'z') {
                        bits = c3 - 'G';
                    } else {
                        if ('0' <= c3 && c3 <= '9') {
                            bits = c3 + 4;
                        } else if (c3 == '+' || c3 == '-') {
                            bits = 62;
                        } else if (c3 == '/' || c3 == '_') {
                            bits = 63;
                        } else {
                            if (c3 != '\n' && c3 != '\r' && c3 != ' ' && c3 != c) {
                                return null;
                            }
                            if (i < limit) {
                                break;
                            }
                            c = '\t';
                        }
                    }
                }
                word = (word << 6) | bits;
                inCount++;
                if (inCount % 4 == 0) {
                    int outCount2 = outCount + 1;
                    out[outCount] = (byte) (word >> 16);
                    int outCount3 = outCount2 + 1;
                    out[outCount2] = (byte) (word >> 8);
                    outCount = outCount3 + 1;
                    out[outCount3] = (byte) word;
                }
                if (i < limit) {
                }
            }
        }
        int lastWordChars = inCount % 4;
        switch (lastWordChars) {
            case 1:
                return null;
            case 2:
                out[outCount] = (byte) ((word << 12) >> 16);
                outCount++;
                break;
            case 3:
                int word2 = word << 6;
                int outCount4 = outCount + 1;
                out[outCount] = (byte) (word2 >> 16);
                outCount = outCount4 + 1;
                out[outCount4] = (byte) (word2 >> 8);
                break;
        }
        if (outCount == out.length) {
            return out;
        }
        byte[] bArrCopyOf = Arrays.copyOf(out, outCount);
        Intrinsics.checkNotNullExpressionValue(bArrCopyOf, "java.util.Arrays.copyOf(this, newSize)");
        return bArrCopyOf;
    }

    public static /* synthetic */ String encodeBase64$default(byte[] bArr, byte[] bArr2, int i, Object obj) {
        if ((i & 1) != 0) {
            bArr2 = BASE64;
        }
        return encodeBase64(bArr, bArr2);
    }

    public static final String encodeBase64(byte[] $this$encodeBase64, byte[] map) {
        Intrinsics.checkNotNullParameter($this$encodeBase64, "<this>");
        Intrinsics.checkNotNullParameter(map, "map");
        int length = (($this$encodeBase64.length + 2) / 3) * 4;
        byte[] out = new byte[length];
        int index = 0;
        int end = $this$encodeBase64.length - ($this$encodeBase64.length % 3);
        int b0 = 0;
        while (b0 < end) {
            int i = b0 + 1;
            int b02 = $this$encodeBase64[b0];
            int i2 = i + 1;
            int b1 = $this$encodeBase64[i];
            int i3 = i2 + 1;
            int b2 = $this$encodeBase64[i2];
            int index2 = index + 1;
            out[index] = map[(b02 & 255) >> 2];
            int index3 = index2 + 1;
            out[index2] = map[((b02 & 3) << 4) | ((b1 & 255) >> 4)];
            int index4 = index3 + 1;
            out[index3] = map[((b1 & 15) << 2) | ((b2 & 255) >> 6)];
            index = index4 + 1;
            out[index4] = map[b2 & 63];
            b0 = i3;
        }
        switch ($this$encodeBase64.length - end) {
            case 1:
                int i4 = $this$encodeBase64[b0];
                int index5 = index + 1;
                out[index] = map[(i4 & 255) >> 2];
                int index6 = index5 + 1;
                out[index5] = map[(i4 & 3) << 4];
                byte b = (byte) 61;
                out[index6] = b;
                out[index6 + 1] = b;
                break;
            case 2:
                int i5 = b0 + 1;
                int b03 = $this$encodeBase64[b0];
                int b12 = $this$encodeBase64[i5];
                int index7 = index + 1;
                out[index] = map[(b03 & 255) >> 2];
                int index8 = index7 + 1;
                out[index7] = map[((b03 & 3) << 4) | ((b12 & 255) >> 4)];
                out[index8] = map[(b12 & 15) << 2];
                out[index8 + 1] = (byte) 61;
                break;
        }
        return _JvmPlatformKt.toUtf8String(out);
    }
}

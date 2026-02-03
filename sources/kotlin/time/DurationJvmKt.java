package kotlin.time;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;

/* compiled from: DurationJvm.kt */
@Metadata(d1 = {"\u0000.\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\u001a\u0010\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000bH\u0002\u001a\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0000\u001a\u0018\u0010\u0010\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u001c\u0010\u0004\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\b¨\u0006\u0011"}, d2 = {"durationAssertionsEnabled", HttpUrl.FRAGMENT_ENCODE_SET, "getDurationAssertionsEnabled", "()Z", "precisionFormats", HttpUrl.FRAGMENT_ENCODE_SET, "Ljava/lang/ThreadLocal;", "Ljava/text/DecimalFormat;", "[Ljava/lang/ThreadLocal;", "createFormatForDecimals", "decimals", HttpUrl.FRAGMENT_ENCODE_SET, "formatToExactDecimals", HttpUrl.FRAGMENT_ENCODE_SET, "value", HttpUrl.FRAGMENT_ENCODE_SET, "formatUpToDecimals", "kotlin-stdlib"}, k = 2, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class DurationJvmKt {
    private static final boolean durationAssertionsEnabled = false;
    private static final ThreadLocal<DecimalFormat>[] precisionFormats;

    static {
        ThreadLocal<DecimalFormat>[] threadLocalArr = new ThreadLocal[4];
        for (int i = 0; i < 4; i++) {
            threadLocalArr[i] = new ThreadLocal<>();
        }
        precisionFormats = threadLocalArr;
    }

    public static final boolean getDurationAssertionsEnabled() {
        return durationAssertionsEnabled;
    }

    private static final DecimalFormat createFormatForDecimals(int decimals) {
        DecimalFormat $this$createFormatForDecimals_u24lambda_u2d0 = new DecimalFormat("0");
        if (decimals > 0) {
            $this$createFormatForDecimals_u24lambda_u2d0.setMinimumFractionDigits(decimals);
        }
        $this$createFormatForDecimals_u24lambda_u2d0.setRoundingMode(RoundingMode.HALF_UP);
        return $this$createFormatForDecimals_u24lambda_u2d0;
    }

    public static final String formatToExactDecimals(double value, int decimals) {
        DecimalFormat decimalFormatCreateFormatForDecimals;
        if (decimals < precisionFormats.length) {
            ThreadLocal<DecimalFormat> threadLocal = precisionFormats[decimals];
            DecimalFormat decimalFormatCreateFormatForDecimals2 = threadLocal.get();
            if (decimalFormatCreateFormatForDecimals2 == null) {
                decimalFormatCreateFormatForDecimals2 = createFormatForDecimals(decimals);
                threadLocal.set(decimalFormatCreateFormatForDecimals2);
            } else {
                Intrinsics.checkNotNullExpressionValue(decimalFormatCreateFormatForDecimals2, "get() ?: default().also(this::set)");
            }
            decimalFormatCreateFormatForDecimals = decimalFormatCreateFormatForDecimals2;
        } else {
            decimalFormatCreateFormatForDecimals = createFormatForDecimals(decimals);
        }
        DecimalFormat format = decimalFormatCreateFormatForDecimals;
        String str = format.format(value);
        Intrinsics.checkNotNullExpressionValue(str, "format.format(value)");
        return str;
    }

    public static final String formatUpToDecimals(double value, int decimals) {
        DecimalFormat $this$formatUpToDecimals_u24lambda_u2d2 = createFormatForDecimals(0);
        $this$formatUpToDecimals_u24lambda_u2d2.setMaximumFractionDigits(decimals);
        String str = $this$formatUpToDecimals_u24lambda_u2d2.format(value);
        Intrinsics.checkNotNullExpressionValue(str, "createFormatForDecimals(… }\n        .format(value)");
        return str;
    }
}

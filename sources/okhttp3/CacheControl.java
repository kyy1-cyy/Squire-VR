package okhttp3;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.internal.Util;

/* compiled from: CacheControl.kt */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0011\u0018\u0000 !2\u00020\u0001:\u0002 !Bq\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\u0002\u0010\u0012J\r\u0010\u000f\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0015J\r\u0010\u0005\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0016J\r\u0010\u000b\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0017J\r\u0010\f\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u0018J\r\u0010\n\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0019J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001aJ\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001bJ\r\u0010\u000e\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001cJ\r\u0010\r\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u001dJ\r\u0010\u0007\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\u001eJ\b\u0010\u001f\u001a\u00020\u0011H\u0016R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0013\u0010\u000f\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0013R\u0013\u0010\u0005\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0014R\u0013\u0010\u000b\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0014R\u0013\u0010\f\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0013\u0010\n\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0013R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0013R\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0013R\u0013\u0010\u000e\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0013R\u0013\u0010\r\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0013R\u0013\u0010\u0007\u001a\u00020\u00068\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0014¨\u0006\""}, d2 = {"Lokhttp3/CacheControl;", HttpUrl.FRAGMENT_ENCODE_SET, "noCache", HttpUrl.FRAGMENT_ENCODE_SET, "noStore", "maxAgeSeconds", HttpUrl.FRAGMENT_ENCODE_SET, "sMaxAgeSeconds", "isPrivate", "isPublic", "mustRevalidate", "maxStaleSeconds", "minFreshSeconds", "onlyIfCached", "noTransform", "immutable", "headerValue", HttpUrl.FRAGMENT_ENCODE_SET, "(ZZIIZZZIIZZZLjava/lang/String;)V", "()Z", "()I", "-deprecated_immutable", "-deprecated_maxAgeSeconds", "-deprecated_maxStaleSeconds", "-deprecated_minFreshSeconds", "-deprecated_mustRevalidate", "-deprecated_noCache", "-deprecated_noStore", "-deprecated_noTransform", "-deprecated_onlyIfCached", "-deprecated_sMaxAgeSeconds", "toString", "Builder", "Companion", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class CacheControl {
    private String headerValue;
    private final boolean immutable;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final CacheControl FORCE_NETWORK = new Builder().noCache().build();
    public static final CacheControl FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();

    public /* synthetic */ CacheControl(boolean z, boolean z2, int i, int i2, boolean z3, boolean z4, boolean z5, int i3, int i4, boolean z6, boolean z7, boolean z8, String str, DefaultConstructorMarker defaultConstructorMarker) {
        this(z, z2, i, i2, z3, z4, z5, i3, i4, z6, z7, z8, str);
    }

    @JvmStatic
    public static final CacheControl parse(Headers headers) {
        return INSTANCE.parse(headers);
    }

    private CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable, String headerValue) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.onlyIfCached = onlyIfCached;
        this.noTransform = noTransform;
        this.immutable = immutable;
        this.headerValue = headerValue;
    }

    public final boolean noCache() {
        return this.noCache;
    }

    public final boolean noStore() {
        return this.noStore;
    }

    public final int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public final int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    /* renamed from: isPrivate, reason: from getter */
    public final boolean getIsPrivate() {
        return this.isPrivate;
    }

    /* renamed from: isPublic, reason: from getter */
    public final boolean getIsPublic() {
        return this.isPublic;
    }

    public final boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public final int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public final int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public final boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public final boolean noTransform() {
        return this.noTransform;
    }

    public final boolean immutable() {
        return this.immutable;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noCache", imports = {}))
    /* renamed from: -deprecated_noCache, reason: not valid java name and from getter */
    public final boolean getNoCache() {
        return this.noCache;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noStore", imports = {}))
    /* renamed from: -deprecated_noStore, reason: not valid java name and from getter */
    public final boolean getNoStore() {
        return this.noStore;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "maxAgeSeconds", imports = {}))
    /* renamed from: -deprecated_maxAgeSeconds, reason: not valid java name and from getter */
    public final int getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "sMaxAgeSeconds", imports = {}))
    /* renamed from: -deprecated_sMaxAgeSeconds, reason: not valid java name and from getter */
    public final int getSMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "mustRevalidate", imports = {}))
    /* renamed from: -deprecated_mustRevalidate, reason: not valid java name and from getter */
    public final boolean getMustRevalidate() {
        return this.mustRevalidate;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "maxStaleSeconds", imports = {}))
    /* renamed from: -deprecated_maxStaleSeconds, reason: not valid java name and from getter */
    public final int getMaxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "minFreshSeconds", imports = {}))
    /* renamed from: -deprecated_minFreshSeconds, reason: not valid java name and from getter */
    public final int getMinFreshSeconds() {
        return this.minFreshSeconds;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "onlyIfCached", imports = {}))
    /* renamed from: -deprecated_onlyIfCached, reason: not valid java name and from getter */
    public final boolean getOnlyIfCached() {
        return this.onlyIfCached;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "noTransform", imports = {}))
    /* renamed from: -deprecated_noTransform, reason: not valid java name and from getter */
    public final boolean getNoTransform() {
        return this.noTransform;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "immutable", imports = {}))
    /* renamed from: -deprecated_immutable, reason: not valid java name and from getter */
    public final boolean getImmutable() {
        return this.immutable;
    }

    public String toString() {
        String result = this.headerValue;
        if (result == null) {
            StringBuilder $this$toString_u24lambda_u2d0 = new StringBuilder();
            if (noCache()) {
                $this$toString_u24lambda_u2d0.append("no-cache, ");
            }
            if (noStore()) {
                $this$toString_u24lambda_u2d0.append("no-store, ");
            }
            if (maxAgeSeconds() != -1) {
                $this$toString_u24lambda_u2d0.append("max-age=").append(maxAgeSeconds()).append(", ");
            }
            if (sMaxAgeSeconds() != -1) {
                $this$toString_u24lambda_u2d0.append("s-maxage=").append(sMaxAgeSeconds()).append(", ");
            }
            if (getIsPrivate()) {
                $this$toString_u24lambda_u2d0.append("private, ");
            }
            if (getIsPublic()) {
                $this$toString_u24lambda_u2d0.append("public, ");
            }
            if (mustRevalidate()) {
                $this$toString_u24lambda_u2d0.append("must-revalidate, ");
            }
            if (maxStaleSeconds() != -1) {
                $this$toString_u24lambda_u2d0.append("max-stale=").append(maxStaleSeconds()).append(", ");
            }
            if (minFreshSeconds() != -1) {
                $this$toString_u24lambda_u2d0.append("min-fresh=").append(minFreshSeconds()).append(", ");
            }
            if (onlyIfCached()) {
                $this$toString_u24lambda_u2d0.append("only-if-cached, ");
            }
            if (noTransform()) {
                $this$toString_u24lambda_u2d0.append("no-transform, ");
            }
            if (immutable()) {
                $this$toString_u24lambda_u2d0.append("immutable, ");
            }
            if ($this$toString_u24lambda_u2d0.length() == 0) {
                return HttpUrl.FRAGMENT_ENCODE_SET;
            }
            $this$toString_u24lambda_u2d0.delete($this$toString_u24lambda_u2d0.length() - 2, $this$toString_u24lambda_u2d0.length());
            String result2 = $this$toString_u24lambda_u2d0.toString();
            Intrinsics.checkNotNullExpressionValue(result2, "StringBuilder().apply(builderAction).toString()");
            this.headerValue = result2;
            return result2;
        }
        return result;
    }

    /* compiled from: CacheControl.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u0003\u001a\u00020\u0000J\u0016\u0010\u000f\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\t\u001a\u00020\u0000J\u0006\u0010\n\u001a\u00020\u0000J\u0006\u0010\u000b\u001a\u00020\u0000J\u0006\u0010\f\u001a\u00020\u0000J\f\u0010\u0014\u001a\u00020\u0006*\u00020\u0015H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lokhttp3/CacheControl$Builder;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "immutable", HttpUrl.FRAGMENT_ENCODE_SET, "maxAgeSeconds", HttpUrl.FRAGMENT_ENCODE_SET, "maxStaleSeconds", "minFreshSeconds", "noCache", "noStore", "noTransform", "onlyIfCached", "build", "Lokhttp3/CacheControl;", "maxAge", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "maxStale", "minFresh", "clampToInt", HttpUrl.FRAGMENT_ENCODE_SET, "okhttp"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Builder {
        private boolean immutable;
        private int maxAgeSeconds = -1;
        private int maxStaleSeconds = -1;
        private int minFreshSeconds = -1;
        private boolean noCache;
        private boolean noStore;
        private boolean noTransform;
        private boolean onlyIfCached;

        public final Builder noCache() {
            Builder $this$noCache_u24lambda_u2d0 = this;
            $this$noCache_u24lambda_u2d0.noCache = true;
            return this;
        }

        public final Builder noStore() {
            Builder $this$noStore_u24lambda_u2d1 = this;
            $this$noStore_u24lambda_u2d1.noStore = true;
            return this;
        }

        public final Builder maxAge(int maxAge, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$maxAge_u24lambda_u2d3 = this;
            if (!(maxAge >= 0)) {
                throw new IllegalArgumentException(Intrinsics.stringPlus("maxAge < 0: ", Integer.valueOf(maxAge)).toString());
            }
            long maxAgeSecondsLong = timeUnit.toSeconds(maxAge);
            $this$maxAge_u24lambda_u2d3.maxAgeSeconds = $this$maxAge_u24lambda_u2d3.clampToInt(maxAgeSecondsLong);
            return this;
        }

        public final Builder maxStale(int maxStale, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$maxStale_u24lambda_u2d5 = this;
            if (!(maxStale >= 0)) {
                throw new IllegalArgumentException(Intrinsics.stringPlus("maxStale < 0: ", Integer.valueOf(maxStale)).toString());
            }
            long maxStaleSecondsLong = timeUnit.toSeconds(maxStale);
            $this$maxStale_u24lambda_u2d5.maxStaleSeconds = $this$maxStale_u24lambda_u2d5.clampToInt(maxStaleSecondsLong);
            return this;
        }

        public final Builder minFresh(int minFresh, TimeUnit timeUnit) {
            Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
            Builder $this$minFresh_u24lambda_u2d7 = this;
            if (!(minFresh >= 0)) {
                throw new IllegalArgumentException(Intrinsics.stringPlus("minFresh < 0: ", Integer.valueOf(minFresh)).toString());
            }
            long minFreshSecondsLong = timeUnit.toSeconds(minFresh);
            $this$minFresh_u24lambda_u2d7.minFreshSeconds = $this$minFresh_u24lambda_u2d7.clampToInt(minFreshSecondsLong);
            return this;
        }

        public final Builder onlyIfCached() {
            Builder $this$onlyIfCached_u24lambda_u2d8 = this;
            $this$onlyIfCached_u24lambda_u2d8.onlyIfCached = true;
            return this;
        }

        public final Builder noTransform() {
            Builder $this$noTransform_u24lambda_u2d9 = this;
            $this$noTransform_u24lambda_u2d9.noTransform = true;
            return this;
        }

        public final Builder immutable() {
            Builder $this$immutable_u24lambda_u2d10 = this;
            $this$immutable_u24lambda_u2d10.immutable = true;
            return this;
        }

        private final int clampToInt(long $this$clampToInt) {
            if ($this$clampToInt > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) $this$clampToInt;
        }

        public final CacheControl build() {
            return new CacheControl(this.noCache, this.noStore, this.maxAgeSeconds, -1, false, false, false, this.maxStaleSeconds, this.minFreshSeconds, this.onlyIfCached, this.noTransform, this.immutable, null, null);
        }
    }

    /* compiled from: CacheControl.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u001e\u0010\t\u001a\u00020\n*\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\nH\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lokhttp3/CacheControl$Companion;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "FORCE_CACHE", "Lokhttp3/CacheControl;", "FORCE_NETWORK", "parse", "headers", "Lokhttp3/Headers;", "indexOfElement", HttpUrl.FRAGMENT_ENCODE_SET, HttpUrl.FRAGMENT_ENCODE_SET, "characters", "startIndex", "okhttp"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Removed duplicated region for block: B:82:0x0103 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:84:0x00f8 A[SYNTHETIC] */
        @JvmStatic
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final CacheControl parse(Headers headers) {
            int sMaxAgeSeconds;
            String parameter;
            Headers headers2 = headers;
            Intrinsics.checkNotNullParameter(headers2, "headers");
            boolean noStore = false;
            int maxAgeSeconds = -1;
            int sMaxAgeSeconds2 = -1;
            boolean isPrivate = false;
            boolean isPublic = false;
            boolean mustRevalidate = false;
            int maxStaleSeconds = -1;
            int minFreshSeconds = -1;
            boolean onlyIfCached = false;
            boolean noTransform = false;
            boolean canUseHeaderValue = true;
            String headerValue = null;
            boolean noCache = false;
            int size = headers.size();
            boolean immutable = false;
            int i = 0;
            while (i < size) {
                int i2 = i;
                int i3 = i + 1;
                int i4 = size;
                String name = headers2.name(i2);
                boolean noTransform2 = noTransform;
                String value = headers2.value(i2);
                if (StringsKt.equals(name, "Cache-Control", true)) {
                    if (headerValue != null) {
                        canUseHeaderValue = false;
                    } else {
                        headerValue = value;
                    }
                } else if (StringsKt.equals(name, "Pragma", true)) {
                    canUseHeaderValue = false;
                } else {
                    headers2 = headers;
                    i = i3;
                    size = i4;
                    noTransform = noTransform2;
                }
                int pos = 0;
                while (pos < value.length()) {
                    int tokenStart = pos;
                    boolean noStore2 = noStore;
                    int pos2 = indexOfElement(value, "=,;", pos);
                    String strSubstring = value.substring(tokenStart, pos2);
                    Intrinsics.checkNotNullExpressionValue(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
                    String directive = StringsKt.trim((CharSequence) strSubstring).toString();
                    int maxAgeSeconds2 = maxAgeSeconds;
                    int maxAgeSeconds3 = value.length();
                    if (pos2 == maxAgeSeconds3) {
                        sMaxAgeSeconds = sMaxAgeSeconds2;
                    } else {
                        sMaxAgeSeconds = sMaxAgeSeconds2;
                        if (value.charAt(pos2) != ',' && value.charAt(pos2) != ';') {
                            int pos3 = Util.indexOfNonWhitespace(value, pos2 + 1);
                            if (pos3 < value.length() && value.charAt(pos3) == '\"') {
                                int pos4 = pos3 + 1;
                                int pos5 = StringsKt.indexOf$default((CharSequence) value, Typography.quote, pos4, false, 4, (Object) null);
                                String strSubstring2 = value.substring(pos4, pos5);
                                Intrinsics.checkNotNullExpressionValue(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
                                parameter = strSubstring2;
                                pos = pos5 + 1;
                            } else {
                                pos = indexOfElement(value, ",;", pos3);
                                String strSubstring3 = value.substring(pos3, pos);
                                Intrinsics.checkNotNullExpressionValue(strSubstring3, "this as java.lang.String…ing(startIndex, endIndex)");
                                parameter = StringsKt.trim((CharSequence) strSubstring3).toString();
                            }
                        }
                        if (!StringsKt.equals("no-cache", directive, true)) {
                            noCache = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("no-store", directive, true)) {
                            noStore = true;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("max-age", directive, true)) {
                            maxAgeSeconds = Util.toNonNegativeInt(parameter, -1);
                            noStore = noStore2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("s-maxage", directive, true)) {
                            noStore = noStore2;
                            sMaxAgeSeconds2 = Util.toNonNegativeInt(parameter, -1);
                            maxAgeSeconds = maxAgeSeconds2;
                        } else if (StringsKt.equals("private", directive, true)) {
                            isPrivate = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("public", directive, true)) {
                            isPublic = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("must-revalidate", directive, true)) {
                            mustRevalidate = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("max-stale", directive, true)) {
                            maxStaleSeconds = Util.toNonNegativeInt(parameter, Integer.MAX_VALUE);
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("min-fresh", directive, true)) {
                            minFreshSeconds = Util.toNonNegativeInt(parameter, -1);
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("only-if-cached", directive, true)) {
                            onlyIfCached = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("no-transform", directive, true)) {
                            noTransform2 = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else if (StringsKt.equals("immutable", directive, true)) {
                            immutable = true;
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        } else {
                            noStore = noStore2;
                            maxAgeSeconds = maxAgeSeconds2;
                            sMaxAgeSeconds2 = sMaxAgeSeconds;
                        }
                    }
                    pos = pos2 + 1;
                    parameter = null;
                    if (!StringsKt.equals("no-cache", directive, true)) {
                    }
                }
                headers2 = headers;
                i = i3;
                size = i4;
                noTransform = noTransform2;
            }
            boolean noTransform3 = noTransform;
            if (!canUseHeaderValue) {
                headerValue = null;
            }
            return new CacheControl(noCache, noStore, maxAgeSeconds, sMaxAgeSeconds2, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform3, immutable, headerValue, null);
        }

        static /* synthetic */ int indexOfElement$default(Companion companion, String str, String str2, int i, int i2, Object obj) {
            if ((i2 & 2) != 0) {
                i = 0;
            }
            return companion.indexOfElement(str, str2, i);
        }

        private final int indexOfElement(String $this$indexOfElement, String characters, int startIndex) {
            int length = $this$indexOfElement.length();
            int i = startIndex;
            while (i < length) {
                int i2 = i;
                i++;
                if (StringsKt.contains$default((CharSequence) characters, $this$indexOfElement.charAt(i2), false, 2, (Object) null)) {
                    return i2;
                }
            }
            return $this$indexOfElement.length();
        }
    }
}

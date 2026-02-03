package okio;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;

/* compiled from: SegmentPool.kt */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u000eH\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0006H\u0007J\b\u0010\u0014\u001a\u00020\u0006H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u00048F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u001e\u0010\f\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u000e0\rX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000f¨\u0006\u0015"}, d2 = {"Lokio/SegmentPool;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "HASH_BUCKET_COUNT", HttpUrl.FRAGMENT_ENCODE_SET, "LOCK", "Lokio/Segment;", "MAX_SIZE", "getMAX_SIZE", "()I", "byteCount", "getByteCount", "hashBuckets", HttpUrl.FRAGMENT_ENCODE_SET, "Ljava/util/concurrent/atomic/AtomicReference;", "[Ljava/util/concurrent/atomic/AtomicReference;", "firstRef", "recycle", HttpUrl.FRAGMENT_ENCODE_SET, "segment", "take", "okio"}, k = 1, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class SegmentPool {
    private static final AtomicReference<Segment>[] hashBuckets;
    public static final SegmentPool INSTANCE = new SegmentPool();
    private static final int MAX_SIZE = 65536;
    private static final Segment LOCK = new Segment(new byte[0], 0, 0, false, false);
    private static final int HASH_BUCKET_COUNT = Integer.highestOneBit((Runtime.getRuntime().availableProcessors() * 2) - 1);

    private SegmentPool() {
    }

    static {
        int i = HASH_BUCKET_COUNT;
        AtomicReference<Segment>[] atomicReferenceArr = new AtomicReference[i];
        for (int i2 = 0; i2 < i; i2++) {
            atomicReferenceArr[i2] = new AtomicReference<>();
        }
        hashBuckets = atomicReferenceArr;
    }

    public final int getMAX_SIZE() {
        return MAX_SIZE;
    }

    public final int getByteCount() {
        Segment first = firstRef().get();
        if (first == null) {
            return 0;
        }
        return first.limit;
    }

    @JvmStatic
    public static final Segment take() {
        AtomicReference firstRef = INSTANCE.firstRef();
        Segment first = firstRef.getAndSet(LOCK);
        if (first == LOCK) {
            return new Segment();
        }
        if (first == null) {
            firstRef.set(null);
            return new Segment();
        }
        firstRef.set(first.next);
        first.next = null;
        first.limit = 0;
        return first;
    }

    @JvmStatic
    public static final void recycle(Segment segment) {
        AtomicReference firstRef;
        Segment first;
        Intrinsics.checkNotNullParameter(segment, "segment");
        if (!(segment.next == null && segment.prev == null)) {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
        if (segment.shared || (first = (firstRef = INSTANCE.firstRef()).get()) == LOCK) {
            return;
        }
        int firstLimit = first == null ? 0 : first.limit;
        if (firstLimit >= MAX_SIZE) {
            return;
        }
        segment.next = first;
        segment.pos = 0;
        segment.limit = firstLimit + 8192;
        if (!SegmentPool$$ExternalSyntheticBackportWithForwarding0.m(firstRef, first, segment)) {
            segment.next = null;
        }
    }

    private final AtomicReference<Segment> firstRef() {
        int hashBucket = (int) (Thread.currentThread().getId() & (HASH_BUCKET_COUNT - 1));
        return hashBuckets[hashBucket];
    }
}

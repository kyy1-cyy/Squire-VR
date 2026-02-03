package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancelHandlerBase;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.debug.internal.ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0;
import kotlinx.coroutines.internal.ConcurrentLinkedListKt;
import kotlinx.coroutines.internal.ConcurrentLinkedListNode;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.SegmentOrClosed;
import okhttp3.HttpUrl;
import rikka.shizuku.shared.BuildConfig;

/* compiled from: Semaphore.kt */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\b\u0002\u0018\u00002\u00020\u001eB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0001¢\u0006\u0004\b\u0004\u0010\u0005J\u0013\u0010\u0007\u001a\u00020\u0006H\u0096@ø\u0001\u0000¢\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\t\u001a\u00020\u0006H\u0082@ø\u0001\u0000¢\u0006\u0004\b\t\u0010\bJ\u001d\u0010\r\u001a\u00020\f2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0016¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0013\u0010\u0012J\u0019\u0010\u0014\u001a\u00020\f*\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0017\u001a\u00020\u00018VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R \u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00060\u00188\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0002\u001a\u00020\u00018\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0002\u0010\u001c\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001d"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", HttpUrl.FRAGMENT_ENCODE_SET, "permits", "acquiredPermits", "<init>", "(II)V", HttpUrl.FRAGMENT_ENCODE_SET, "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "acquireSlowPath", "Lkotlinx/coroutines/CancellableContinuation;", "cont", HttpUrl.FRAGMENT_ENCODE_SET, "addAcquireToQueue", "(Lkotlinx/coroutines/CancellableContinuation;)Z", BuildConfig.BUILD_TYPE, "()V", "tryAcquire", "()Z", "tryResumeNextFromQueue", "tryResumeAcquire", "getAvailablePermits", "()I", "availablePermits", "Lkotlin/Function1;", HttpUrl.FRAGMENT_ENCODE_SET, "onCancellationRelease", "Lkotlin/jvm/functions/Function1;", "I", "kotlinx-coroutines-core", "Lkotlinx/coroutines/sync/Semaphore;"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
final class SemaphoreImpl implements Semaphore {
    volatile /* synthetic */ int _availablePermits;
    private volatile /* synthetic */ long deqIdx = 0;
    private volatile /* synthetic */ long enqIdx = 0;
    private volatile /* synthetic */ Object head;
    private final Function1<Throwable, Unit> onCancellationRelease;
    private final int permits;
    private volatile /* synthetic */ Object tail;
    private static final /* synthetic */ AtomicReferenceFieldUpdater head$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head");
    private static final /* synthetic */ AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");
    private static final /* synthetic */ AtomicReferenceFieldUpdater tail$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail");
    private static final /* synthetic */ AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
    static final /* synthetic */ AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");

    public SemaphoreImpl(int permits, int acquiredPermits) {
        this.permits = permits;
        if (!(this.permits > 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("Semaphore should have at least 1 permit, but had ", Integer.valueOf(this.permits)).toString());
        }
        if (!(acquiredPermits >= 0 && acquiredPermits <= this.permits)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("The number of acquired permits should be in 0..", Integer.valueOf(this.permits)).toString());
        }
        SemaphoreSegment s = new SemaphoreSegment(0L, null, 2);
        this.head = s;
        this.tail = s;
        this._availablePermits = this.permits - acquiredPermits;
        this.onCancellationRelease = new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.SemaphoreImpl$onCancellationRelease$1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable $noName_0) {
                this.this$0.release();
            }
        };
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public int getAvailablePermits() {
        return Math.max(this._availablePermits, 0);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public boolean tryAcquire() {
        int p;
        do {
            p = this._availablePermits;
            if (p <= 0) {
                return false;
            }
        } while (!_availablePermits$FU.compareAndSet(this, p, p - 1));
        return true;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public Object acquire(Continuation<? super Unit> continuation) {
        Object objAcquireSlowPath;
        int p = _availablePermits$FU.getAndDecrement(this);
        return (p <= 0 && (objAcquireSlowPath = acquireSlowPath(continuation)) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) ? objAcquireSlowPath : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object acquireSlowPath(Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellable$iv = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        CancellableContinuationImpl cont = cancellable$iv;
        while (true) {
            if (addAcquireToQueue(cont)) {
                break;
            }
            int p = _availablePermits$FU.getAndDecrement(this);
            if (p > 0) {
                cont.resume(Unit.INSTANCE, this.onCancellationRelease);
                break;
            }
        }
        Object result = cancellable$iv.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public void release() {
        int cur$iv;
        int upd$iv;
        do {
            do {
                cur$iv = this._availablePermits;
                if (!(cur$iv < this.permits)) {
                    throw new IllegalStateException(Intrinsics.stringPlus("The number of released permits cannot be greater than ", Integer.valueOf(this.permits)).toString());
                }
                upd$iv = cur$iv + 1;
            } while (!_availablePermits$FU.compareAndSet(this, cur$iv, upd$iv));
            if (cur$iv >= 0) {
                return;
            }
        } while (!tryResumeNextFromQueue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean addAcquireToQueue(CancellableContinuation<? super Unit> cont) {
        SemaphoreSegment curTail;
        Object objM1688constructorimpl;
        Object s$iv;
        boolean z;
        SemaphoreSegment s$iv2 = (SemaphoreSegment) this.tail;
        long enqIdx = enqIdx$FU.getAndIncrement(this);
        long id$iv = enqIdx / SemaphoreKt.SEGMENT_SIZE;
        while (true) {
            SemaphoreSegment $this$findSegmentInternal$iv$iv = s$iv2;
            Segment cur$iv$iv = $this$findSegmentInternal$iv$iv;
            while (true) {
                if (cur$iv$iv.getId() < id$iv || cur$iv$iv.getRemoved()) {
                    ConcurrentLinkedListNode this_$iv$iv$iv = cur$iv$iv;
                    Object it$iv$iv$iv = this_$iv$iv$iv.get_next();
                    curTail = s$iv2;
                    if (it$iv$iv$iv == ConcurrentLinkedListKt.CLOSED) {
                        objM1688constructorimpl = SegmentOrClosed.m1688constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    Segment next$iv$iv = (Segment) ((ConcurrentLinkedListNode) it$iv$iv$iv);
                    if (next$iv$iv != null) {
                        cur$iv$iv = next$iv$iv;
                        s$iv2 = curTail;
                    } else {
                        long p0 = cur$iv$iv.getId() + 1;
                        SemaphoreSegment p1 = (SemaphoreSegment) cur$iv$iv;
                        Segment newTail$iv$iv = SemaphoreKt.createSegment(p0, p1);
                        if (!cur$iv$iv.trySetNext(newTail$iv$iv)) {
                            s$iv2 = curTail;
                        } else {
                            if (cur$iv$iv.getRemoved()) {
                                cur$iv$iv.remove();
                            }
                            cur$iv$iv = newTail$iv$iv;
                            s$iv2 = curTail;
                        }
                    }
                } else {
                    objM1688constructorimpl = SegmentOrClosed.m1688constructorimpl(cur$iv$iv);
                    curTail = s$iv2;
                    break;
                }
            }
            s$iv = objM1688constructorimpl;
            if (SegmentOrClosed.m1693isClosedimpl(s$iv)) {
                break;
            }
            Segment to$iv$iv = SegmentOrClosed.m1691getSegmentimpl(s$iv);
            while (true) {
                Segment cur$iv$iv2 = (Segment) this.tail;
                if (cur$iv$iv2.getId() >= to$iv$iv.getId()) {
                    z = true;
                    break;
                }
                if (!to$iv$iv.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    break;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(tail$FU, this, cur$iv$iv2, to$iv$iv)) {
                    if (cur$iv$iv2.decPointers$kotlinx_coroutines_core()) {
                        cur$iv$iv2.remove();
                    }
                    z = true;
                } else if (to$iv$iv.decPointers$kotlinx_coroutines_core()) {
                    to$iv$iv.remove();
                }
            }
            if (z) {
                break;
            }
            s$iv2 = curTail;
        }
        SemaphoreSegment segment = (SemaphoreSegment) SegmentOrClosed.m1691getSegmentimpl(s$iv);
        int i = (int) (enqIdx % SemaphoreKt.SEGMENT_SIZE);
        if (!ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i, null, cont)) {
            Object expected$iv = SemaphoreKt.PERMIT;
            Object value$iv = SemaphoreKt.TAKEN;
            if (ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i, expected$iv, value$iv)) {
                cont.resume(Unit.INSTANCE, this.onCancellationRelease);
                return true;
            }
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (segment.acquirers.get(i) == SemaphoreKt.BROKEN) {
                    return false;
                }
                throw new AssertionError();
            }
            return false;
        }
        CancelHandlerBase $this$asHandler$iv = new CancelSemaphoreAcquisitionHandler(segment, i);
        cont.invokeOnCancellation($this$asHandler$iv);
        return true;
    }

    private final boolean tryResumeNextFromQueue() {
        Object objM1688constructorimpl;
        Object s$iv;
        boolean z;
        Segment curHead = (SemaphoreSegment) this.head;
        long deqIdx = deqIdx$FU.getAndIncrement(this);
        long id = deqIdx / SemaphoreKt.SEGMENT_SIZE;
        do {
            Segment $this$findSegmentInternal$iv$iv = curHead;
            Segment cur$iv$iv = $this$findSegmentInternal$iv$iv;
            while (true) {
                if (cur$iv$iv.getId() < id || cur$iv$iv.getRemoved()) {
                    ConcurrentLinkedListNode this_$iv$iv$iv = cur$iv$iv;
                    Object it$iv$iv$iv = this_$iv$iv$iv.get_next();
                    if (it$iv$iv$iv == ConcurrentLinkedListKt.CLOSED) {
                        objM1688constructorimpl = SegmentOrClosed.m1688constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    Segment next$iv$iv = (Segment) ((ConcurrentLinkedListNode) it$iv$iv$iv);
                    if (next$iv$iv != null) {
                        cur$iv$iv = next$iv$iv;
                    } else {
                        long p0 = cur$iv$iv.getId() + 1;
                        SemaphoreSegment p1 = (SemaphoreSegment) cur$iv$iv;
                        Segment newTail$iv$iv = SemaphoreKt.createSegment(p0, p1);
                        if (cur$iv$iv.trySetNext(newTail$iv$iv)) {
                            if (cur$iv$iv.getRemoved()) {
                                cur$iv$iv.remove();
                            }
                            cur$iv$iv = newTail$iv$iv;
                        }
                    }
                } else {
                    objM1688constructorimpl = SegmentOrClosed.m1688constructorimpl(cur$iv$iv);
                    break;
                }
            }
            s$iv = objM1688constructorimpl;
            if (SegmentOrClosed.m1693isClosedimpl(s$iv)) {
                break;
            }
            Segment to$iv$iv = SegmentOrClosed.m1691getSegmentimpl(s$iv);
            while (true) {
                Segment cur$iv$iv2 = (Segment) this.head;
                if (cur$iv$iv2.getId() >= to$iv$iv.getId()) {
                    z = true;
                    break;
                }
                if (!to$iv$iv.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    break;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(head$FU, this, cur$iv$iv2, to$iv$iv)) {
                    if (cur$iv$iv2.decPointers$kotlinx_coroutines_core()) {
                        cur$iv$iv2.remove();
                    }
                    z = true;
                } else if (to$iv$iv.decPointers$kotlinx_coroutines_core()) {
                    to$iv$iv.remove();
                }
            }
        } while (!z);
        SemaphoreSegment segment = (SemaphoreSegment) SegmentOrClosed.m1691getSegmentimpl(s$iv);
        segment.cleanPrev();
        if (segment.getId() > id) {
            return false;
        }
        int i = (int) (deqIdx % SemaphoreKt.SEGMENT_SIZE);
        Object value$iv = segment.acquirers.getAndSet(i, SemaphoreKt.PERMIT);
        if (value$iv == null) {
            int i2 = SemaphoreKt.MAX_SPIN_CYCLES;
            int it = 0;
            while (it < i2) {
                int i3 = it + 1;
                if (segment.acquirers.get(i) == SemaphoreKt.TAKEN) {
                    return true;
                }
                it = i3;
            }
            Object expected$iv = SemaphoreKt.PERMIT;
            return !ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i, expected$iv, SemaphoreKt.BROKEN);
        }
        if (value$iv == SemaphoreKt.CANCELLED) {
            return false;
        }
        return tryResumeAcquire((CancellableContinuation) value$iv);
    }

    private final boolean tryResumeAcquire(CancellableContinuation<? super Unit> cancellableContinuation) {
        Object token = cancellableContinuation.tryResume(Unit.INSTANCE, null, this.onCancellationRelease);
        if (token == null) {
            return false;
        }
        cancellableContinuation.completeResume(token);
        return true;
    }
}

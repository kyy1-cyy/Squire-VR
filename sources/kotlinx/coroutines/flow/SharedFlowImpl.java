package kotlinx.coroutines.flow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.DisposableHandle;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.JobKt;
import kotlinx.coroutines.channels.BufferOverflow;
import kotlinx.coroutines.flow.internal.AbstractSharedFlow;
import kotlinx.coroutines.flow.internal.AbstractSharedFlowKt;
import kotlinx.coroutines.flow.internal.FusibleFlow;
import okhttp3.HttpUrl;

/* compiled from: SharedFlow.kt */
@Metadata(d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010 \n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0012\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u00020\u00030\u00022\b\u0012\u0004\u0012\u0002H\u00010\u00042\b\u0012\u0004\u0012\u0002H\u00010\u00052\b\u0012\u0004\u0012\u0002H\u00010\u0006:\u0001hB\u001d\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u0019\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0003H\u0082@ø\u0001\u0000¢\u0006\u0002\u0010.J\u0010\u0010/\u001a\u00020,2\u0006\u00100\u001a\u000201H\u0002J\b\u00102\u001a\u00020,H\u0002J\u001f\u00103\u001a\u0002042\f\u00105\u001a\b\u0012\u0004\u0012\u00028\u000006H\u0096@ø\u0001\u0000¢\u0006\u0002\u00107J\u0010\u00108\u001a\u00020,2\u0006\u00109\u001a\u00020\u0012H\u0002J\b\u0010:\u001a\u00020\u0003H\u0014J\u001d\u0010;\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u000e2\u0006\u0010<\u001a\u00020\bH\u0014¢\u0006\u0002\u0010=J\b\u0010>\u001a\u00020,H\u0002J\u0019\u0010?\u001a\u00020,2\u0006\u0010@\u001a\u00028\u0000H\u0096@ø\u0001\u0000¢\u0006\u0002\u0010AJ\u0019\u0010B\u001a\u00020,2\u0006\u0010@\u001a\u00028\u0000H\u0082@ø\u0001\u0000¢\u0006\u0002\u0010AJ\u0012\u0010C\u001a\u00020,2\b\u0010D\u001a\u0004\u0018\u00010\u000fH\u0002J1\u0010E\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000e2\u0014\u0010G\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000eH\u0002¢\u0006\u0002\u0010HJ&\u0010I\u001a\b\u0012\u0004\u0012\u00028\u00000J2\u0006\u0010K\u001a\u00020L2\u0006\u0010M\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0012\u0010N\u001a\u0004\u0018\u00010\u000f2\u0006\u0010O\u001a\u00020\u0012H\u0002J7\u0010P\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u000e2\u0010\u0010Q\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0018\u00010\u000e2\u0006\u0010R\u001a\u00020\b2\u0006\u0010S\u001a\u00020\bH\u0002¢\u0006\u0002\u0010TJ\b\u0010U\u001a\u00020,H\u0016J\u0015\u0010V\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010XJ\u0015\u0010Y\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010XJ\u0015\u0010Z\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010XJ\u0010\u0010[\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u0003H\u0002J\u0012\u0010\\\u001a\u0004\u0018\u00010\u000f2\u0006\u0010-\u001a\u00020\u0003H\u0002J(\u0010]\u001a\u00020,2\u0006\u0010^\u001a\u00020\u00122\u0006\u0010_\u001a\u00020\u00122\u0006\u0010`\u001a\u00020\u00122\u0006\u0010a\u001a\u00020\u0012H\u0002J%\u0010b\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000e2\u0006\u0010c\u001a\u00020\u0012H\u0000¢\u0006\u0004\bd\u0010eJ\r\u0010f\u001a\u00020\u0012H\u0000¢\u0006\u0002\bgR\u001a\u0010\r\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0010R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\u00020\u00128BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\u00020\u00128BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0014R\u001a\u0010\u0018\u001a\u00028\u00008DX\u0084\u0004¢\u0006\f\u0012\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\u00020\u00128BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u0014R\u000e\u0010 \u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010!\u001a\b\u0012\u0004\u0012\u00028\u00000\"8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b#\u0010$R\u000e\u0010%\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010&\u001a\u00020\b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b'\u0010(R\u0014\u0010)\u001a\u00020\b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b*\u0010(\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006i"}, d2 = {"Lkotlinx/coroutines/flow/SharedFlowImpl;", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/flow/internal/AbstractSharedFlow;", "Lkotlinx/coroutines/flow/SharedFlowSlot;", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lkotlinx/coroutines/flow/CancellableFlow;", "Lkotlinx/coroutines/flow/internal/FusibleFlow;", "replay", HttpUrl.FRAGMENT_ENCODE_SET, "bufferCapacity", "onBufferOverflow", "Lkotlinx/coroutines/channels/BufferOverflow;", "(IILkotlinx/coroutines/channels/BufferOverflow;)V", "buffer", HttpUrl.FRAGMENT_ENCODE_SET, HttpUrl.FRAGMENT_ENCODE_SET, "[Ljava/lang/Object;", "bufferEndIndex", HttpUrl.FRAGMENT_ENCODE_SET, "getBufferEndIndex", "()J", "bufferSize", "head", "getHead", "lastReplayedLocked", "getLastReplayedLocked$annotations", "()V", "getLastReplayedLocked", "()Ljava/lang/Object;", "minCollectorIndex", "queueEndIndex", "getQueueEndIndex", "queueSize", "replayCache", HttpUrl.FRAGMENT_ENCODE_SET, "getReplayCache", "()Ljava/util/List;", "replayIndex", "replaySize", "getReplaySize", "()I", "totalSize", "getTotalSize", "awaitValue", HttpUrl.FRAGMENT_ENCODE_SET, "slot", "(Lkotlinx/coroutines/flow/SharedFlowSlot;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cancelEmitter", "emitter", "Lkotlinx/coroutines/flow/SharedFlowImpl$Emitter;", "cleanupTailLocked", "collect", HttpUrl.FRAGMENT_ENCODE_SET, "collector", "Lkotlinx/coroutines/flow/FlowCollector;", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "correctCollectorIndexesOnDropOldest", "newHead", "createSlot", "createSlotArray", "size", "(I)[Lkotlinx/coroutines/flow/SharedFlowSlot;", "dropOldestLocked", "emit", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emitSuspend", "enqueueLocked", "item", "findSlotsToResumeLocked", "Lkotlin/coroutines/Continuation;", "resumesIn", "([Lkotlin/coroutines/Continuation;)[Lkotlin/coroutines/Continuation;", "fuse", "Lkotlinx/coroutines/flow/Flow;", "context", "Lkotlin/coroutines/CoroutineContext;", "capacity", "getPeekedValueLockedAt", "index", "growBuffer", "curBuffer", "curSize", "newSize", "([Ljava/lang/Object;II)[Ljava/lang/Object;", "resetReplayCache", "tryEmit", HttpUrl.FRAGMENT_ENCODE_SET, "(Ljava/lang/Object;)Z", "tryEmitLocked", "tryEmitNoCollectorsLocked", "tryPeekLocked", "tryTakeValue", "updateBufferLocked", "newReplayIndex", "newMinCollectorIndex", "newBufferEndIndex", "newQueueEndIndex", "updateCollectorIndexLocked", "oldIndex", "updateCollectorIndexLocked$kotlinx_coroutines_core", "(J)[Lkotlin/coroutines/Continuation;", "updateNewCollectorIndexLocked", "updateNewCollectorIndexLocked$kotlinx_coroutines_core", "Emitter", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public class SharedFlowImpl<T> extends AbstractSharedFlow<SharedFlowSlot> implements MutableSharedFlow<T>, CancellableFlow<T>, FusibleFlow<T> {
    private Object[] buffer;
    private final int bufferCapacity;
    private int bufferSize;
    private long minCollectorIndex;
    private final BufferOverflow onBufferOverflow;
    private int queueSize;
    private final int replay;
    private long replayIndex;

    /* compiled from: SharedFlow.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[BufferOverflow.values().length];
            iArr[BufferOverflow.SUSPEND.ordinal()] = 1;
            iArr[BufferOverflow.DROP_LATEST.ordinal()] = 2;
            iArr[BufferOverflow.DROP_OLDEST.ordinal()] = 3;
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* compiled from: SharedFlow.kt */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "kotlinx.coroutines.flow.SharedFlowImpl", f = "SharedFlow.kt", i = {0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2}, l = {373, 380, 383}, m = "collect$suspendImpl", n = {"this", "collector", "slot", "this", "collector", "slot", "collectorJob", "this", "collector", "slot", "collectorJob"}, s = {"L$0", "L$1", "L$2", "L$0", "L$1", "L$2", "L$3", "L$0", "L$1", "L$2", "L$3"})
    /* renamed from: kotlinx.coroutines.flow.SharedFlowImpl$collect$1, reason: invalid class name */
    static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        int label;
        /* synthetic */ Object result;
        final /* synthetic */ SharedFlowImpl<T> this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(SharedFlowImpl<T> sharedFlowImpl, Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
            this.this$0 = sharedFlowImpl;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return SharedFlowImpl.collect$suspendImpl(this.this$0, null, this);
        }
    }

    protected static /* synthetic */ void getLastReplayedLocked$annotations() {
    }

    @Override // kotlinx.coroutines.flow.SharedFlow, kotlinx.coroutines.flow.Flow
    public Object collect(FlowCollector<? super T> flowCollector, Continuation<?> continuation) {
        return collect$suspendImpl(this, flowCollector, continuation);
    }

    @Override // kotlinx.coroutines.flow.MutableSharedFlow, kotlinx.coroutines.flow.FlowCollector
    public Object emit(T t, Continuation<? super Unit> continuation) {
        return emit$suspendImpl(this, t, continuation);
    }

    public SharedFlowImpl(int replay, int bufferCapacity, BufferOverflow onBufferOverflow) {
        this.replay = replay;
        this.bufferCapacity = bufferCapacity;
        this.onBufferOverflow = onBufferOverflow;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long getHead() {
        return Math.min(this.minCollectorIndex, this.replayIndex);
    }

    private final int getReplaySize() {
        return (int) ((getHead() + this.bufferSize) - this.replayIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getTotalSize() {
        return this.bufferSize + this.queueSize;
    }

    private final long getBufferEndIndex() {
        return getHead() + this.bufferSize;
    }

    private final long getQueueEndIndex() {
        return getHead() + this.bufferSize + this.queueSize;
    }

    @Override // kotlinx.coroutines.flow.SharedFlow
    public List<T> getReplayCache() {
        synchronized (this) {
            int replaySize = getReplaySize();
            if (replaySize == 0) {
                return CollectionsKt.emptyList();
            }
            ArrayList result = new ArrayList(replaySize);
            Object[] buffer = this.buffer;
            Intrinsics.checkNotNull(buffer);
            int i = 0;
            while (i < replaySize) {
                int i2 = i;
                i++;
                result.add(SharedFlowKt.getBufferAt(buffer, this.replayIndex + i2));
            }
            return result;
        }
    }

    protected final T getLastReplayedLocked() {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        return (T) SharedFlowKt.getBufferAt(objArr, (this.replayIndex + getReplaySize()) - 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00c1 A[Catch: all -> 0x00d4, TRY_LEAVE, TryCatch #0 {all -> 0x00d4, blocks: (B:13:0x003d, B:30:0x009f, B:35:0x00ae, B:34:0x00ab, B:39:0x00c1, B:16:0x0052, B:19:0x0066, B:28:0x008f, B:22:0x0077, B:24:0x007b), top: B:46:0x0022 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00a8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v15 */
    /* JADX WARN: Type inference failed for: r2v7, types: [java.lang.Object, kotlinx.coroutines.flow.FlowCollector] */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v7, types: [java.lang.Object, kotlinx.coroutines.flow.SharedFlowImpl] */
    /* JADX WARN: Type inference failed for: r7v0, types: [kotlinx.coroutines.flow.FlowCollector] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v15 */
    /* JADX WARN: Type inference failed for: r7v16 */
    /* JADX WARN: Type inference failed for: r7v17 */
    /* JADX WARN: Type inference failed for: r7v9, types: [java.lang.Object, kotlinx.coroutines.flow.SharedFlowSlot] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:36:0x00bd -> B:38:0x00c0). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    static /* synthetic */ Object collect$suspendImpl(SharedFlowImpl sharedFlowImpl, FlowCollector flowCollector, Continuation continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        SharedFlowSlot sharedFlowSlot;
        SharedFlowImpl sharedFlowImpl2;
        FlowCollector flowCollector2;
        Job job;
        ?? r3;
        ?? r2;
        Object objTryTakeValue;
        SharedFlowSlot sharedFlowSlot2;
        SharedFlowImpl sharedFlowImpl3;
        FlowCollector flowCollector3;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(sharedFlowImpl, continuation);
            }
        }
        AnonymousClass1 anonymousClass12 = anonymousClass1;
        Object obj = anonymousClass12.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        try {
            switch (anonymousClass12.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    SharedFlowImpl sharedFlowImpl4 = sharedFlowImpl;
                    SubscribedFlowCollector subscribedFlowCollector = flowCollector;
                    SharedFlowSlot sharedFlowSlotAllocateSlot = sharedFlowImpl4.allocateSlot();
                    boolean z = subscribedFlowCollector instanceof SubscribedFlowCollector;
                    flowCollector2 = subscribedFlowCollector;
                    sharedFlowImpl2 = sharedFlowImpl4;
                    sharedFlowSlot = sharedFlowSlotAllocateSlot;
                    if (z) {
                        anonymousClass12.L$0 = sharedFlowImpl4;
                        anonymousClass12.L$1 = subscribedFlowCollector;
                        anonymousClass12.L$2 = sharedFlowSlotAllocateSlot;
                        anonymousClass12.label = 1;
                        Object objOnSubscription = subscribedFlowCollector.onSubscription(anonymousClass12);
                        flowCollector2 = subscribedFlowCollector;
                        sharedFlowImpl2 = sharedFlowImpl4;
                        sharedFlowSlot = sharedFlowSlotAllocateSlot;
                        if (objOnSubscription == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                    }
                    job = (Job) anonymousClass12.getContext().get(Job.INSTANCE);
                    r2 = flowCollector2;
                    r3 = sharedFlowImpl2;
                    flowCollector = sharedFlowSlot;
                    do {
                        objTryTakeValue = r3.tryTakeValue(flowCollector);
                        if (objTryTakeValue == SharedFlowKt.NO_VALUE) {
                            anonymousClass12.L$0 = r3;
                            anonymousClass12.L$1 = r2;
                            anonymousClass12.L$2 = flowCollector;
                            anonymousClass12.L$3 = job;
                            anonymousClass12.label = 2;
                        } else {
                            if (job != null) {
                                JobKt.ensureActive(job);
                            }
                            anonymousClass12.L$0 = r3;
                            anonymousClass12.L$1 = r2;
                            anonymousClass12.L$2 = flowCollector;
                            anonymousClass12.L$3 = job;
                            anonymousClass12.label = 3;
                            Object objEmit = r2.emit(objTryTakeValue, anonymousClass12);
                            flowCollector3 = r2;
                            sharedFlowImpl3 = r3;
                            sharedFlowSlot2 = flowCollector;
                            if (objEmit == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            r2 = flowCollector3;
                            r3 = sharedFlowImpl3;
                            flowCollector = sharedFlowSlot2;
                            objTryTakeValue = r3.tryTakeValue(flowCollector);
                            if (objTryTakeValue == SharedFlowKt.NO_VALUE) {
                            }
                        }
                    } while (r3.awaitValue(flowCollector, anonymousClass12) != coroutine_suspended);
                    return coroutine_suspended;
                case 1:
                    SharedFlowSlot sharedFlowSlot3 = (SharedFlowSlot) anonymousClass12.L$2;
                    FlowCollector flowCollector4 = (FlowCollector) anonymousClass12.L$1;
                    SharedFlowImpl sharedFlowImpl5 = (SharedFlowImpl) anonymousClass12.L$0;
                    ResultKt.throwOnFailure(obj);
                    flowCollector2 = flowCollector4;
                    sharedFlowImpl2 = sharedFlowImpl5;
                    sharedFlowSlot = sharedFlowSlot3;
                    job = (Job) anonymousClass12.getContext().get(Job.INSTANCE);
                    r2 = flowCollector2;
                    r3 = sharedFlowImpl2;
                    flowCollector = sharedFlowSlot;
                    do {
                        objTryTakeValue = r3.tryTakeValue(flowCollector);
                        if (objTryTakeValue == SharedFlowKt.NO_VALUE) {
                        }
                    } while (r3.awaitValue(flowCollector, anonymousClass12) != coroutine_suspended);
                    return coroutine_suspended;
                case 2:
                    job = (Job) anonymousClass12.L$3;
                    SharedFlowSlot sharedFlowSlot4 = (SharedFlowSlot) anonymousClass12.L$2;
                    FlowCollector flowCollector5 = (FlowCollector) anonymousClass12.L$1;
                    SharedFlowImpl sharedFlowImpl6 = (SharedFlowImpl) anonymousClass12.L$0;
                    ResultKt.throwOnFailure(obj);
                    r2 = flowCollector5;
                    r3 = sharedFlowImpl6;
                    flowCollector = sharedFlowSlot4;
                    do {
                        objTryTakeValue = r3.tryTakeValue(flowCollector);
                        if (objTryTakeValue == SharedFlowKt.NO_VALUE) {
                        }
                    } while (r3.awaitValue(flowCollector, anonymousClass12) != coroutine_suspended);
                    return coroutine_suspended;
                case 3:
                    job = (Job) anonymousClass12.L$3;
                    SharedFlowSlot sharedFlowSlot5 = (SharedFlowSlot) anonymousClass12.L$2;
                    FlowCollector flowCollector6 = (FlowCollector) anonymousClass12.L$1;
                    SharedFlowImpl sharedFlowImpl7 = (SharedFlowImpl) anonymousClass12.L$0;
                    ResultKt.throwOnFailure(obj);
                    flowCollector3 = flowCollector6;
                    sharedFlowImpl3 = sharedFlowImpl7;
                    sharedFlowSlot2 = sharedFlowSlot5;
                    r2 = flowCollector3;
                    r3 = sharedFlowImpl3;
                    flowCollector = sharedFlowSlot2;
                    do {
                        objTryTakeValue = r3.tryTakeValue(flowCollector);
                        if (objTryTakeValue == SharedFlowKt.NO_VALUE) {
                        }
                    } while (r3.awaitValue(flowCollector, anonymousClass12) != coroutine_suspended);
                    return coroutine_suspended;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        } catch (Throwable th) {
            r3.freeSlot(flowCollector);
            throw th;
        }
    }

    @Override // kotlinx.coroutines.flow.MutableSharedFlow
    public boolean tryEmit(T value) {
        int i;
        boolean z;
        Continuation[] continuationArrFindSlotsToResumeLocked = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            i = 0;
            if (tryEmitLocked(value)) {
                continuationArrFindSlotsToResumeLocked = findSlotsToResumeLocked(continuationArrFindSlotsToResumeLocked);
                z = true;
            } else {
                z = false;
            }
        }
        boolean emitted = z;
        int length = continuationArrFindSlotsToResumeLocked.length;
        while (i < length) {
            Continuation cont = continuationArrFindSlotsToResumeLocked[i];
            i++;
            if (cont != null) {
                Result.Companion companion = Result.INSTANCE;
                cont.resumeWith(Result.m149constructorimpl(Unit.INSTANCE));
            }
        }
        return emitted;
    }

    static /* synthetic */ Object emit$suspendImpl(SharedFlowImpl sharedFlowImpl, Object value, Continuation $completion) {
        Object objEmitSuspend;
        return (!sharedFlowImpl.tryEmit(value) && (objEmitSuspend = sharedFlowImpl.emitSuspend(value, $completion)) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) ? objEmitSuspend : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean tryEmitLocked(T value) {
        if (getNCollectors() == 0) {
            return tryEmitNoCollectorsLocked(value);
        }
        if (this.bufferSize >= this.bufferCapacity && this.minCollectorIndex <= this.replayIndex) {
            switch (WhenMappings.$EnumSwitchMapping$0[this.onBufferOverflow.ordinal()]) {
                case 1:
                    return false;
                case 2:
                    return true;
            }
        }
        enqueueLocked(value);
        this.bufferSize++;
        if (this.bufferSize > this.bufferCapacity) {
            dropOldestLocked();
        }
        if (getReplaySize() > this.replay) {
            updateBufferLocked(this.replayIndex + 1, this.minCollectorIndex, getBufferEndIndex(), getQueueEndIndex());
        }
        return true;
    }

    private final boolean tryEmitNoCollectorsLocked(T value) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(getNCollectors() == 0)) {
                throw new AssertionError();
            }
        }
        if (this.replay == 0) {
            return true;
        }
        enqueueLocked(value);
        this.bufferSize++;
        if (this.bufferSize > this.replay) {
            dropOldestLocked();
        }
        this.minCollectorIndex = getHead() + this.bufferSize;
        return true;
    }

    private final void dropOldestLocked() {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        SharedFlowKt.setBufferAt(objArr, getHead(), null);
        this.bufferSize--;
        long newHead = getHead() + 1;
        if (this.replayIndex < newHead) {
            this.replayIndex = newHead;
        }
        if (this.minCollectorIndex < newHead) {
            correctCollectorIndexesOnDropOldest(newHead);
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(getHead() == newHead)) {
                throw new AssertionError();
            }
        }
    }

    private final void correctCollectorIndexesOnDropOldest(long newHead) {
        Object[] $this$forEach$iv$iv;
        SharedFlowImpl<T> this_$iv = this;
        if (((AbstractSharedFlow) this_$iv).nCollectors != 0 && ($this$forEach$iv$iv = ((AbstractSharedFlow) this_$iv).slots) != null) {
            int length = $this$forEach$iv$iv.length;
            int i = 0;
            while (i < length) {
                Object element$iv$iv = $this$forEach$iv$iv[i];
                i++;
                if (element$iv$iv != null) {
                    SharedFlowSlot slot = (SharedFlowSlot) element$iv$iv;
                    if (slot.index >= 0 && slot.index < newHead) {
                        slot.index = newHead;
                    }
                }
            }
        }
        this.minCollectorIndex = newHead;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void enqueueLocked(Object item) {
        int curSize = getTotalSize();
        Object[] curBuffer = this.buffer;
        if (curBuffer == null) {
            curBuffer = growBuffer(null, 0, 2);
        } else if (curSize >= curBuffer.length) {
            curBuffer = growBuffer(curBuffer, curSize, curBuffer.length * 2);
        }
        SharedFlowKt.setBufferAt(curBuffer, getHead() + curSize, item);
    }

    private final Object[] growBuffer(Object[] curBuffer, int curSize, int newSize) {
        int i = 0;
        if (!(newSize > 0)) {
            throw new IllegalStateException("Buffer size overflow".toString());
        }
        Object[] newBuffer = new Object[newSize];
        this.buffer = newBuffer;
        if (curBuffer == null) {
            return newBuffer;
        }
        long head = getHead();
        while (i < curSize) {
            int i2 = i;
            i++;
            SharedFlowKt.setBufferAt(newBuffer, i2 + head, SharedFlowKt.getBufferAt(curBuffer, i2 + head));
        }
        return newBuffer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object emitSuspend(T t, Continuation<? super Unit> continuation) {
        Continuation[] continuationArrFindSlotsToResumeLocked;
        Emitter emitter;
        CancellableContinuationImpl cancellable$iv = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellable$iv.initCancellability();
        CancellableContinuationImpl cont = cancellable$iv;
        Continuation[] continuationArrFindSlotsToResumeLocked2 = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            if (tryEmitLocked(t)) {
                Result.Companion companion = Result.INSTANCE;
                cont.resumeWith(Result.m149constructorimpl(Unit.INSTANCE));
                continuationArrFindSlotsToResumeLocked = findSlotsToResumeLocked(continuationArrFindSlotsToResumeLocked2);
                emitter = null;
            } else {
                Emitter it = new Emitter(this, getTotalSize() + getHead(), t, cont);
                enqueueLocked(it);
                this.queueSize++;
                if (this.bufferCapacity == 0) {
                    continuationArrFindSlotsToResumeLocked2 = findSlotsToResumeLocked(continuationArrFindSlotsToResumeLocked2);
                }
                continuationArrFindSlotsToResumeLocked = continuationArrFindSlotsToResumeLocked2;
                emitter = it;
            }
        }
        Emitter emitter2 = emitter;
        if (emitter2 != null) {
            CancellableContinuationKt.disposeOnCancellation(cont, emitter2);
        }
        int length = continuationArrFindSlotsToResumeLocked.length;
        int i = 0;
        while (i < length) {
            Continuation r = continuationArrFindSlotsToResumeLocked[i];
            i++;
            if (r != null) {
                Result.Companion companion2 = Result.INSTANCE;
                r.resumeWith(Result.m149constructorimpl(Unit.INSTANCE));
            }
        }
        Object result = cancellable$iv.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void cancelEmitter(Emitter emitter) {
        synchronized (this) {
            if (emitter.index < getHead()) {
                return;
            }
            Object[] buffer = this.buffer;
            Intrinsics.checkNotNull(buffer);
            if (SharedFlowKt.getBufferAt(buffer, emitter.index) != emitter) {
                return;
            }
            SharedFlowKt.setBufferAt(buffer, emitter.index, SharedFlowKt.NO_VALUE);
            cleanupTailLocked();
            Unit unit = Unit.INSTANCE;
        }
    }

    public final long updateNewCollectorIndexLocked$kotlinx_coroutines_core() {
        long index = this.replayIndex;
        if (index < this.minCollectorIndex) {
            this.minCollectorIndex = index;
        }
        return index;
    }

    /* JADX WARN: Removed duplicated region for block: B:86:0x0155  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Continuation<Unit>[] updateCollectorIndexLocked$kotlinx_coroutines_core(long oldIndex) {
        Object[] $this$forEach$iv$iv;
        long head;
        long head2;
        int newBufferSize0;
        Continuation[] resumes;
        long newBufferEndIndex;
        long newReplayIndex;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((oldIndex >= this.minCollectorIndex ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (oldIndex > this.minCollectorIndex) {
            return AbstractSharedFlowKt.EMPTY_RESUMES;
        }
        long head3 = getHead();
        long newMinCollectorIndex = this.bufferSize + head3;
        long curEmitterIndex = 1;
        if (this.bufferCapacity == 0 && this.queueSize > 0) {
            newMinCollectorIndex++;
        }
        SharedFlowImpl<T> this_$iv = this;
        if (((AbstractSharedFlow) this_$iv).nCollectors == 0 || ($this$forEach$iv$iv = ((AbstractSharedFlow) this_$iv).slots) == null) {
            head = head3;
        } else {
            int length = $this$forEach$iv$iv.length;
            int i = 0;
            while (i < length) {
                Object element$iv$iv = $this$forEach$iv$iv[i];
                i++;
                if (element$iv$iv != null) {
                    SharedFlowSlot slot = (SharedFlowSlot) element$iv$iv;
                    head2 = head3;
                    if (slot.index >= 0 && slot.index < newMinCollectorIndex) {
                        newMinCollectorIndex = slot.index;
                    }
                } else {
                    head2 = head3;
                }
                head3 = head2;
            }
            head = head3;
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((newMinCollectorIndex >= this.minCollectorIndex ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (newMinCollectorIndex <= this.minCollectorIndex) {
            return AbstractSharedFlowKt.EMPTY_RESUMES;
        }
        long newBufferEndIndex2 = getBufferEndIndex();
        if (getNCollectors() > 0) {
            int newBufferSize02 = (int) (newBufferEndIndex2 - newMinCollectorIndex);
            newBufferSize0 = Math.min(this.queueSize, this.bufferCapacity - newBufferSize02);
        } else {
            newBufferSize0 = this.queueSize;
        }
        int maxResumeCount = newBufferSize0;
        Continuation[] resumes2 = AbstractSharedFlowKt.EMPTY_RESUMES;
        long newQueueEndIndex = newBufferEndIndex2 + this.queueSize;
        if (maxResumeCount <= 0) {
            resumes = resumes2;
        } else {
            Continuation[] resumes3 = new Continuation[maxResumeCount];
            int resumeCount = 0;
            Object[] buffer = this.buffer;
            Intrinsics.checkNotNull(buffer);
            long newBufferEndIndex3 = newBufferEndIndex2;
            while (true) {
                if (newBufferEndIndex2 < newQueueEndIndex) {
                    long curEmitterIndex2 = newBufferEndIndex2;
                    long j = newBufferEndIndex2 + curEmitterIndex;
                    Object emitter = SharedFlowKt.getBufferAt(buffer, curEmitterIndex2);
                    if (emitter == SharedFlowKt.NO_VALUE) {
                        newBufferEndIndex2 = j;
                        curEmitterIndex = 1;
                    } else {
                        if (emitter == null) {
                            throw new NullPointerException("null cannot be cast to non-null type kotlinx.coroutines.flow.SharedFlowImpl.Emitter");
                        }
                        int resumeCount2 = resumeCount + 1;
                        resumes3[resumeCount] = ((Emitter) emitter).cont;
                        SharedFlowKt.setBufferAt(buffer, curEmitterIndex2, SharedFlowKt.NO_VALUE);
                        SharedFlowKt.setBufferAt(buffer, newBufferEndIndex3, ((Emitter) emitter).value);
                        newBufferEndIndex3++;
                        if (resumeCount2 >= maxResumeCount) {
                            newBufferEndIndex2 = newBufferEndIndex3;
                            resumes = resumes3;
                            break;
                        }
                        resumeCount = resumeCount2;
                        newBufferEndIndex2 = j;
                        curEmitterIndex = 1;
                    }
                } else {
                    newBufferEndIndex2 = newBufferEndIndex3;
                    resumes = resumes3;
                    break;
                }
            }
        }
        int newBufferSize1 = (int) (newBufferEndIndex2 - head);
        if (getNCollectors() == 0) {
            newMinCollectorIndex = newBufferEndIndex2;
        }
        long newMinCollectorIndex2 = newMinCollectorIndex;
        long newReplayIndex2 = Math.max(this.replayIndex, newBufferEndIndex2 - Math.min(this.replay, newBufferSize1));
        if (this.bufferCapacity == 0 && newReplayIndex2 < newQueueEndIndex) {
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            if (Intrinsics.areEqual(SharedFlowKt.getBufferAt(objArr, newReplayIndex2), SharedFlowKt.NO_VALUE)) {
                newBufferEndIndex = newBufferEndIndex2 + 1;
                newReplayIndex = newReplayIndex2 + 1;
            }
        } else {
            newBufferEndIndex = newBufferEndIndex2;
            newReplayIndex = newReplayIndex2;
        }
        updateBufferLocked(newReplayIndex, newMinCollectorIndex2, newBufferEndIndex, newQueueEndIndex);
        cleanupTailLocked();
        if (!(resumes.length == 0)) {
            return findSlotsToResumeLocked(resumes);
        }
        return resumes;
    }

    private final void updateBufferLocked(long newReplayIndex, long newMinCollectorIndex, long newBufferEndIndex, long newQueueEndIndex) {
        long newHead = Math.min(newMinCollectorIndex, newReplayIndex);
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((newHead >= getHead() ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        long head = getHead();
        while (head < newHead) {
            long index = head;
            head++;
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            SharedFlowKt.setBufferAt(objArr, index, null);
        }
        this.replayIndex = newReplayIndex;
        this.minCollectorIndex = newMinCollectorIndex;
        this.bufferSize = (int) (newBufferEndIndex - newHead);
        this.queueSize = (int) (newQueueEndIndex - newBufferEndIndex);
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((this.bufferSize >= 0 ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((this.queueSize >= 0 ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.replayIndex <= getHead() + ((long) this.bufferSize))) {
                throw new AssertionError();
            }
        }
    }

    private final void cleanupTailLocked() {
        if (this.bufferCapacity != 0 || this.queueSize > 1) {
            Object[] buffer = this.buffer;
            Intrinsics.checkNotNull(buffer);
            while (this.queueSize > 0 && SharedFlowKt.getBufferAt(buffer, (getHead() + getTotalSize()) - 1) == SharedFlowKt.NO_VALUE) {
                this.queueSize--;
                SharedFlowKt.setBufferAt(buffer, getHead() + getTotalSize(), null);
            }
        }
    }

    private final Object tryTakeValue(SharedFlowSlot slot) {
        Object obj;
        Continuation[] continuationArrUpdateCollectorIndexLocked$kotlinx_coroutines_core = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            long index = tryPeekLocked(slot);
            if (index < 0) {
                obj = SharedFlowKt.NO_VALUE;
            } else {
                long oldIndex = slot.index;
                Object newValue = getPeekedValueLockedAt(index);
                slot.index = 1 + index;
                continuationArrUpdateCollectorIndexLocked$kotlinx_coroutines_core = updateCollectorIndexLocked$kotlinx_coroutines_core(oldIndex);
                obj = newValue;
            }
        }
        Object value = obj;
        int length = continuationArrUpdateCollectorIndexLocked$kotlinx_coroutines_core.length;
        int i = 0;
        while (i < length) {
            Continuation resume = continuationArrUpdateCollectorIndexLocked$kotlinx_coroutines_core[i];
            i++;
            if (resume != null) {
                Result.Companion companion = Result.INSTANCE;
                resume.resumeWith(Result.m149constructorimpl(Unit.INSTANCE));
            }
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long tryPeekLocked(SharedFlowSlot slot) {
        long index = slot.index;
        if (index < getBufferEndIndex()) {
            return index;
        }
        if (this.bufferCapacity <= 0 && index <= getHead() && this.queueSize != 0) {
            return index;
        }
        return -1L;
    }

    private final Object getPeekedValueLockedAt(long index) {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        Object item = SharedFlowKt.getBufferAt(objArr, index);
        return item instanceof Emitter ? ((Emitter) item).value : item;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object awaitValue(SharedFlowSlot slot, Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellable$iv = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellable$iv.initCancellability();
        CancellableContinuationImpl cont = cancellable$iv;
        synchronized (this) {
            long index = tryPeekLocked(slot);
            if (index < 0) {
                slot.cont = cont;
                slot.cont = cont;
            } else {
                Result.Companion companion = Result.INSTANCE;
                cont.resumeWith(Result.m149constructorimpl(Unit.INSTANCE));
            }
            Unit unit = Unit.INSTANCE;
        }
        Object result = cancellable$iv.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Continuation<Unit>[] findSlotsToResumeLocked(Continuation<Unit>[] resumesIn) {
        Object[] $this$forEach$iv$iv;
        SharedFlowSlot slot;
        Continuation cont;
        SharedFlowImpl<T> sharedFlowImpl = this;
        Object resumes = resumesIn;
        int resumeCount = resumesIn.length;
        SharedFlowImpl<T> this_$iv = sharedFlowImpl;
        if (((AbstractSharedFlow) this_$iv).nCollectors != 0 && ($this$forEach$iv$iv = ((AbstractSharedFlow) this_$iv).slots) != null) {
            int length = $this$forEach$iv$iv.length;
            int i = 0;
            while (i < length) {
                Object element$iv$iv = $this$forEach$iv$iv[i];
                i++;
                if (element$iv$iv != null && (cont = (slot = (SharedFlowSlot) element$iv$iv).cont) != null && sharedFlowImpl.tryPeekLocked(slot) >= 0) {
                    if (resumeCount >= ((Object[]) resumes).length) {
                        Object objCopyOf = Arrays.copyOf((Object[]) resumes, Math.max(2, ((Object[]) resumes).length * 2));
                        Intrinsics.checkNotNullExpressionValue(objCopyOf, "copyOf(this, newSize)");
                        resumes = objCopyOf;
                    }
                    ((Continuation[]) resumes)[resumeCount] = cont;
                    slot.cont = null;
                    resumeCount++;
                    sharedFlowImpl = this;
                } else {
                    Object resumes2 = resumes;
                    resumes = resumes2;
                    sharedFlowImpl = this;
                }
            }
        }
        return (Continuation[]) resumes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlinx.coroutines.flow.internal.AbstractSharedFlow
    public SharedFlowSlot createSlot() {
        return new SharedFlowSlot();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlinx.coroutines.flow.internal.AbstractSharedFlow
    public SharedFlowSlot[] createSlotArray(int size) {
        return new SharedFlowSlot[size];
    }

    @Override // kotlinx.coroutines.flow.MutableSharedFlow
    public void resetReplayCache() {
        synchronized (this) {
            updateBufferLocked(getBufferEndIndex(), this.minCollectorIndex, getBufferEndIndex(), getQueueEndIndex());
            Unit unit = Unit.INSTANCE;
        }
    }

    @Override // kotlinx.coroutines.flow.internal.FusibleFlow
    public Flow<T> fuse(CoroutineContext context, int capacity, BufferOverflow onBufferOverflow) {
        return SharedFlowKt.fuseSharedFlow(this, context, capacity, onBufferOverflow);
    }

    /* compiled from: SharedFlow.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B1\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0002\u0010\u000bJ\b\u0010\f\u001a\u00020\nH\u0016R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lkotlinx/coroutines/flow/SharedFlowImpl$Emitter;", "Lkotlinx/coroutines/DisposableHandle;", "flow", "Lkotlinx/coroutines/flow/SharedFlowImpl;", "index", HttpUrl.FRAGMENT_ENCODE_SET, "value", HttpUrl.FRAGMENT_ENCODE_SET, "cont", "Lkotlin/coroutines/Continuation;", HttpUrl.FRAGMENT_ENCODE_SET, "(Lkotlinx/coroutines/flow/SharedFlowImpl;JLjava/lang/Object;Lkotlin/coroutines/Continuation;)V", "dispose", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    private static final class Emitter implements DisposableHandle {
        public final Continuation<Unit> cont;
        public final SharedFlowImpl<?> flow;
        public long index;
        public final Object value;

        /* JADX WARN: Multi-variable type inference failed */
        public Emitter(SharedFlowImpl<?> sharedFlowImpl, long index, Object value, Continuation<? super Unit> continuation) {
            this.flow = sharedFlowImpl;
            this.index = index;
            this.value = value;
            this.cont = continuation;
        }

        @Override // kotlinx.coroutines.DisposableHandle
        public void dispose() {
            this.flow.cancelEmitter(this);
        }
    }
}

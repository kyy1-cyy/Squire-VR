package kotlinx.coroutines.flow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.BroadcastChannel;
import kotlinx.coroutines.channels.ChannelResult;
import kotlinx.coroutines.channels.ChannelsKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.ChannelFlowKt;
import okhttp3.HttpUrl;

/* compiled from: Channels.kt */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u0007\u001a\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0005\u001a/\u0010\u0006\u001a\u00020\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005H\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\n\u001a9\u0010\u000b\u001a\u00020\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00020\u00052\u0006\u0010\f\u001a\u00020\rH\u0082@ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\u000f\u001a&\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0012H\u0007\u001a\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0005\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0014"}, d2 = {"asFlow", "Lkotlinx/coroutines/flow/Flow;", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/channels/BroadcastChannel;", "consumeAsFlow", "Lkotlinx/coroutines/channels/ReceiveChannel;", "emitAll", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/flow/FlowCollector;", "channel", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emitAllImpl", "consume", HttpUrl.FRAGMENT_ENCODE_SET, "emitAllImpl$FlowKt__ChannelsKt", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlinx/coroutines/channels/ReceiveChannel;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "produceIn", "scope", "Lkotlinx/coroutines/CoroutineScope;", "receiveAsFlow", "kotlinx-coroutines-core"}, k = 5, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE, xs = "kotlinx/coroutines/flow/FlowKt")
/* loaded from: classes.dex */
final /* synthetic */ class FlowKt__ChannelsKt {
    public static final <T> Object emitAll(FlowCollector<? super T> flowCollector, ReceiveChannel<? extends T> receiveChannel, Continuation<? super Unit> continuation) throws Throwable {
        Object objEmitAllImpl$FlowKt__ChannelsKt = emitAllImpl$FlowKt__ChannelsKt(flowCollector, receiveChannel, true, continuation);
        return objEmitAllImpl$FlowKt__ChannelsKt == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objEmitAllImpl$FlowKt__ChannelsKt : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:26:0x007c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x008a A[Catch: all -> 0x00b5, TRY_LEAVE, TryCatch #0 {all -> 0x00b5, blocks: (B:29:0x0084, B:31:0x008a, B:38:0x009c, B:39:0x009d), top: B:52:0x0084 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x009d A[Catch: all -> 0x00b5, TRY_LEAVE, TryCatch #0 {all -> 0x00b5, blocks: (B:29:0x0084, B:31:0x008a, B:38:0x009c, B:39:0x009d), top: B:52:0x0084 }] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.lang.Object, kotlinx.coroutines.flow.FlowCollector] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:42:0x00b1 -> B:23:0x006a). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final <T> Object emitAllImpl$FlowKt__ChannelsKt(FlowCollector<? super T> flowCollector, ReceiveChannel<? extends T> receiveChannel, boolean z, Continuation<? super Unit> continuation) throws Throwable {
        FlowKt__ChannelsKt$emitAllImpl$1 flowKt__ChannelsKt$emitAllImpl$1;
        FlowCollector<? super T> flowCollector2;
        boolean z2;
        ReceiveChannel receiveChannel2;
        Throwable th;
        boolean z3;
        Throwable th2;
        Object holder;
        ?? r2;
        Throwable th3;
        Object obj;
        if (continuation instanceof FlowKt__ChannelsKt$emitAllImpl$1) {
            flowKt__ChannelsKt$emitAllImpl$1 = (FlowKt__ChannelsKt$emitAllImpl$1) continuation;
            if ((flowKt__ChannelsKt$emitAllImpl$1.label & Integer.MIN_VALUE) != 0) {
                flowKt__ChannelsKt$emitAllImpl$1.label -= Integer.MIN_VALUE;
            } else {
                flowKt__ChannelsKt$emitAllImpl$1 = new FlowKt__ChannelsKt$emitAllImpl$1(continuation);
            }
        }
        FlowKt__ChannelsKt$emitAllImpl$1 flowKt__ChannelsKt$emitAllImpl$12 = flowKt__ChannelsKt$emitAllImpl$1;
        Object obj2 = flowKt__ChannelsKt$emitAllImpl$12.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (flowKt__ChannelsKt$emitAllImpl$12.label) {
            case 0:
                ResultKt.throwOnFailure(obj2);
                FlowKt.ensureActive(flowCollector);
                flowCollector2 = flowCollector;
                z2 = z;
                receiveChannel2 = receiveChannel;
                th = null;
                flowKt__ChannelsKt$emitAllImpl$12.L$0 = flowCollector2;
                flowKt__ChannelsKt$emitAllImpl$12.L$1 = receiveChannel2;
                flowKt__ChannelsKt$emitAllImpl$12.Z$0 = z2;
                flowKt__ChannelsKt$emitAllImpl$12.label = 1;
                holder = receiveChannel2.mo1638receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$12);
                if (holder == coroutine_suspended) {
                    return coroutine_suspended;
                }
                try {
                    Throwable th4 = th;
                    z3 = z2;
                    th2 = th4;
                    r2 = flowCollector2;
                    if (!ChannelResult.m1653isClosedimpl(obj)) {
                        Throwable thM1649exceptionOrNullimpl = ChannelResult.m1649exceptionOrNullimpl(obj);
                        if (thM1649exceptionOrNullimpl == null) {
                            return Unit.INSTANCE;
                        }
                        throw thM1649exceptionOrNullimpl;
                    }
                    Object objM1651getOrThrowimpl = ChannelResult.m1651getOrThrowimpl(obj);
                    flowKt__ChannelsKt$emitAllImpl$12.L$0 = r2;
                    flowKt__ChannelsKt$emitAllImpl$12.L$1 = receiveChannel2;
                    flowKt__ChannelsKt$emitAllImpl$12.Z$0 = z3;
                    flowKt__ChannelsKt$emitAllImpl$12.label = 2;
                    if (r2.emit(objM1651getOrThrowimpl, flowKt__ChannelsKt$emitAllImpl$12) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    boolean z4 = z3;
                    th = th3;
                    z2 = z4;
                    flowCollector2 = r2;
                    flowKt__ChannelsKt$emitAllImpl$12.L$0 = flowCollector2;
                    flowKt__ChannelsKt$emitAllImpl$12.L$1 = receiveChannel2;
                    flowKt__ChannelsKt$emitAllImpl$12.Z$0 = z2;
                    flowKt__ChannelsKt$emitAllImpl$12.label = 1;
                    holder = receiveChannel2.mo1638receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$12);
                    if (holder == coroutine_suspended) {
                    }
                } catch (Throwable th5) {
                    th = th5;
                    z2 = z3;
                    Throwable th6 = th;
                    try {
                        throw th;
                    } finally {
                        if (z2) {
                            ChannelsKt.cancelConsumed(receiveChannel2, th6);
                        }
                    }
                }
                th3 = th2;
                obj = holder;
            case 1:
                z3 = flowKt__ChannelsKt$emitAllImpl$12.Z$0;
                receiveChannel2 = (ReceiveChannel) flowKt__ChannelsKt$emitAllImpl$12.L$1;
                FlowCollector flowCollector3 = (FlowCollector) flowKt__ChannelsKt$emitAllImpl$12.L$0;
                th2 = null;
                try {
                    ResultKt.throwOnFailure(obj2);
                    holder = ((ChannelResult) obj2).getHolder();
                    r2 = flowCollector3;
                    th3 = th2;
                    obj = holder;
                    if (!ChannelResult.m1653isClosedimpl(obj)) {
                    }
                } catch (Throwable th7) {
                    th = th7;
                    z2 = z3;
                    Throwable th62 = th;
                    throw th;
                }
                break;
            case 2:
                z2 = flowKt__ChannelsKt$emitAllImpl$12.Z$0;
                th = null;
                receiveChannel2 = (ReceiveChannel) flowKt__ChannelsKt$emitAllImpl$12.L$1;
                FlowCollector<? super T> flowCollector4 = (FlowCollector) flowKt__ChannelsKt$emitAllImpl$12.L$0;
                try {
                    ResultKt.throwOnFailure(obj2);
                    flowCollector2 = flowCollector4;
                    flowKt__ChannelsKt$emitAllImpl$12.L$0 = flowCollector2;
                    flowKt__ChannelsKt$emitAllImpl$12.L$1 = receiveChannel2;
                    flowKt__ChannelsKt$emitAllImpl$12.Z$0 = z2;
                    flowKt__ChannelsKt$emitAllImpl$12.label = 1;
                    holder = receiveChannel2.mo1638receiveCatchingJP2dKIU(flowKt__ChannelsKt$emitAllImpl$12);
                    if (holder == coroutine_suspended) {
                    }
                } catch (Throwable th8) {
                    th = th8;
                    Throwable th622 = th;
                    throw th;
                }
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    public static final <T> Flow<T> receiveAsFlow(ReceiveChannel<? extends T> receiveChannel) {
        return new ChannelAsFlow(receiveChannel, false, null, 0, null, 28, null);
    }

    public static final <T> Flow<T> consumeAsFlow(ReceiveChannel<? extends T> receiveChannel) {
        return new ChannelAsFlow(receiveChannel, true, null, 0, null, 28, null);
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "'BroadcastChannel' is obsolete and all corresponding operators are deprecated in the favour of StateFlow and SharedFlow")
    public static final <T> Flow<T> asFlow(final BroadcastChannel<T> broadcastChannel) {
        return new Flow<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ChannelsKt$asFlow$$inlined$unsafeFlow$1
            @Override // kotlinx.coroutines.flow.Flow
            public Object collect(FlowCollector<? super T> flowCollector, Continuation<? super Unit> continuation) {
                Object objEmitAll = FlowKt.emitAll(flowCollector, broadcastChannel.openSubscription(), continuation);
                return objEmitAll == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objEmitAll : Unit.INSTANCE;
            }
        };
    }

    public static final <T> ReceiveChannel<T> produceIn(Flow<? extends T> flow, CoroutineScope scope) {
        return ChannelFlowKt.asChannelFlow(flow).produceImpl(scope);
    }
}

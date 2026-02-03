package kotlinx.coroutines.flow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.selects.SelectInstance;
import okhttp3.HttpUrl;

/* JADX INFO: Add missing generic type declarations: [T] */
/* compiled from: Delay.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;", "downstream", "Lkotlinx/coroutines/flow/FlowCollector;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1", f = "Delay.kt", i = {0, 0, 0, 0, 1, 1, 1, 1}, l = {222, 355}, m = "invokeSuspend", n = {"downstream", "values", "lastValue", "timeoutMillis", "downstream", "values", "lastValue", "timeoutMillis"}, s = {"L$0", "L$1", "L$2", "L$3", "L$0", "L$1", "L$2", "L$3"})
/* loaded from: classes.dex */
final class FlowKt__DelayKt$debounceInternal$1<T> extends SuspendLambda implements Function3<CoroutineScope, FlowCollector<? super T>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow<T> $this_debounceInternal;
    final /* synthetic */ Function1<T, Long> $timeoutMillisSelector;
    private /* synthetic */ Object L$0;
    /* synthetic */ Object L$1;
    Object L$2;
    Object L$3;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    FlowKt__DelayKt$debounceInternal$1(Function1<? super T, Long> function1, Flow<? extends T> flow, Continuation<? super FlowKt__DelayKt$debounceInternal$1> continuation) {
        super(3, continuation);
        this.$timeoutMillisSelector = function1;
        this.$this_debounceInternal = flow;
    }

    @Override // kotlin.jvm.functions.Function3
    public final Object invoke(CoroutineScope coroutineScope, FlowCollector<? super T> flowCollector, Continuation<? super Unit> continuation) {
        FlowKt__DelayKt$debounceInternal$1 flowKt__DelayKt$debounceInternal$1 = new FlowKt__DelayKt$debounceInternal$1(this.$timeoutMillisSelector, this.$this_debounceInternal, continuation);
        flowKt__DelayKt$debounceInternal$1.L$0 = coroutineScope;
        flowKt__DelayKt$debounceInternal$1.L$1 = flowCollector;
        return flowKt__DelayKt$debounceInternal$1.invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:11|(5:13|(1:15)|16|(1:18)(1:19)|(2:30|31)(2:21|(15:23|(1:25)|26|(1:28)|29|33|(3:35|(1:41)(1:40)|(2:44|45))|46|63|47|(1:49)|50|54|(1:56)|(1:58)(4:59|60|9|(2:61|62)(0)))))|32|33|(0)|46|63|47|(0)|50|54|(0)|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0147, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0148, code lost:
    
        r0.handleBuilderException(r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:11:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00ee  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x012b A[Catch: all -> 0x0147, TryCatch #0 {all -> 0x0147, blocks: (B:47:0x0122, B:49:0x012b, B:50:0x0137), top: B:63:0x0122 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x015d A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x016a  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:59:0x015e -> B:60:0x0165). Please report as a decompilation issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object invokeSuspend(Object obj) throws Throwable {
        FlowKt__DelayKt$debounceInternal$1 flowKt__DelayKt$debounceInternal$1;
        Object $result;
        Ref.ObjectRef lastValue;
        ReceiveChannel values;
        FlowCollector downstream;
        Ref.LongRef timeoutMillis;
        FlowCollector downstream2;
        ReceiveChannel values2;
        Ref.ObjectRef lastValue2;
        Ref.LongRef timeoutMillis2;
        Object $result2;
        FlowKt__DelayKt$debounceInternal$1 flowKt__DelayKt$debounceInternal$12;
        Object obj2;
        Object result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        long j = 0;
        int i = 1;
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                flowKt__DelayKt$debounceInternal$1 = this;
                $result = obj;
                CoroutineScope $this$scopedFlow = (CoroutineScope) flowKt__DelayKt$debounceInternal$1.L$0;
                FlowCollector downstream3 = (FlowCollector) flowKt__DelayKt$debounceInternal$1.L$1;
                ReceiveChannel values3 = ProduceKt.produce$default($this$scopedFlow, null, 0, new FlowKt__DelayKt$debounceInternal$1$values$1(flowKt__DelayKt$debounceInternal$1.$this_debounceInternal, null), 3, null);
                lastValue = new Ref.ObjectRef();
                values = values3;
                downstream = downstream3;
                if (lastValue.element != NullSurrogateKt.DONE) {
                    timeoutMillis = new Ref.LongRef();
                    if (lastValue.element != null) {
                        Function1<T, Long> function1 = flowKt__DelayKt$debounceInternal$1.$timeoutMillisSelector;
                        Symbol this_$iv = NullSurrogateKt.NULL;
                        T t = lastValue.element;
                        if (t == this_$iv) {
                            t = null;
                        }
                        timeoutMillis.element = function1.invoke(t).longValue();
                        if ((timeoutMillis.element >= j ? i : 0) == 0) {
                            throw new IllegalArgumentException("Debounce timeout should not be negative".toString());
                        }
                        if (timeoutMillis.element == j) {
                            Symbol this_$iv2 = NullSurrogateKt.NULL;
                            T t2 = lastValue.element;
                            if (t2 == this_$iv2) {
                                t2 = null;
                            }
                            flowKt__DelayKt$debounceInternal$1.L$0 = downstream;
                            flowKt__DelayKt$debounceInternal$1.L$1 = values;
                            flowKt__DelayKt$debounceInternal$1.L$2 = lastValue;
                            flowKt__DelayKt$debounceInternal$1.L$3 = timeoutMillis;
                            flowKt__DelayKt$debounceInternal$1.label = i;
                            if (downstream.emit(t2, flowKt__DelayKt$debounceInternal$1) == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            lastValue.element = null;
                            downstream2 = downstream;
                            values2 = values;
                            lastValue2 = lastValue;
                            timeoutMillis2 = timeoutMillis;
                            $result2 = $result;
                            flowKt__DelayKt$debounceInternal$12 = flowKt__DelayKt$debounceInternal$1;
                            obj2 = coroutine_suspended;
                            if (DebugKt.getASSERTIONS_ENABLED()) {
                                if (((lastValue2.element == null || timeoutMillis2.element > j) ? i : 0) == 0) {
                                    throw new AssertionError();
                                }
                            }
                            flowKt__DelayKt$debounceInternal$12.L$0 = downstream2;
                            flowKt__DelayKt$debounceInternal$12.L$1 = values2;
                            flowKt__DelayKt$debounceInternal$12.L$2 = lastValue2;
                            flowKt__DelayKt$debounceInternal$12.L$3 = timeoutMillis2;
                            flowKt__DelayKt$debounceInternal$12.label = 2;
                            Continuation uCont$iv = flowKt__DelayKt$debounceInternal$12;
                            SelectInstance scope$iv = new SelectInstance(uCont$iv);
                            SelectInstance $this$invokeSuspend_u24lambda_u2d2 = scope$iv;
                            if (lastValue2.element != null) {
                                $this$invokeSuspend_u24lambda_u2d2.onTimeout(timeoutMillis2.element, new FlowKt__DelayKt$debounceInternal$1$3$1(downstream2, lastValue2, null));
                            }
                            $this$invokeSuspend_u24lambda_u2d2.invoke(values2.getOnReceiveCatching(), new FlowKt__DelayKt$debounceInternal$1$3$2(lastValue2, downstream2, null));
                            result = scope$iv.getResult();
                            if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                                DebugProbesKt.probeCoroutineSuspended(flowKt__DelayKt$debounceInternal$12);
                            }
                            if (result == obj2) {
                                return obj2;
                            }
                            coroutine_suspended = obj2;
                            flowKt__DelayKt$debounceInternal$1 = flowKt__DelayKt$debounceInternal$12;
                            $result = $result2;
                            lastValue = lastValue2;
                            values = values2;
                            downstream = downstream2;
                            j = 0;
                            i = 1;
                            if (lastValue.element != NullSurrogateKt.DONE) {
                                return Unit.INSTANCE;
                            }
                        }
                    }
                    downstream2 = downstream;
                    values2 = values;
                    lastValue2 = lastValue;
                    timeoutMillis2 = timeoutMillis;
                    $result2 = $result;
                    flowKt__DelayKt$debounceInternal$12 = flowKt__DelayKt$debounceInternal$1;
                    obj2 = coroutine_suspended;
                    if (DebugKt.getASSERTIONS_ENABLED()) {
                    }
                    flowKt__DelayKt$debounceInternal$12.L$0 = downstream2;
                    flowKt__DelayKt$debounceInternal$12.L$1 = values2;
                    flowKt__DelayKt$debounceInternal$12.L$2 = lastValue2;
                    flowKt__DelayKt$debounceInternal$12.L$3 = timeoutMillis2;
                    flowKt__DelayKt$debounceInternal$12.label = 2;
                    Continuation uCont$iv2 = flowKt__DelayKt$debounceInternal$12;
                    SelectInstance scope$iv2 = new SelectInstance(uCont$iv2);
                    SelectInstance $this$invokeSuspend_u24lambda_u2d22 = scope$iv2;
                    if (lastValue2.element != null) {
                    }
                    $this$invokeSuspend_u24lambda_u2d22.invoke(values2.getOnReceiveCatching(), new FlowKt__DelayKt$debounceInternal$1$3$2(lastValue2, downstream2, null));
                    result = scope$iv2.getResult();
                    if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                    }
                    if (result == obj2) {
                    }
                }
                break;
            case 1:
                flowKt__DelayKt$debounceInternal$1 = this;
                $result = obj;
                timeoutMillis = (Ref.LongRef) flowKt__DelayKt$debounceInternal$1.L$3;
                lastValue = (Ref.ObjectRef) flowKt__DelayKt$debounceInternal$1.L$2;
                values = (ReceiveChannel) flowKt__DelayKt$debounceInternal$1.L$1;
                downstream = (FlowCollector) flowKt__DelayKt$debounceInternal$1.L$0;
                ResultKt.throwOnFailure($result);
                lastValue.element = null;
                downstream2 = downstream;
                values2 = values;
                lastValue2 = lastValue;
                timeoutMillis2 = timeoutMillis;
                $result2 = $result;
                flowKt__DelayKt$debounceInternal$12 = flowKt__DelayKt$debounceInternal$1;
                obj2 = coroutine_suspended;
                if (DebugKt.getASSERTIONS_ENABLED()) {
                }
                flowKt__DelayKt$debounceInternal$12.L$0 = downstream2;
                flowKt__DelayKt$debounceInternal$12.L$1 = values2;
                flowKt__DelayKt$debounceInternal$12.L$2 = lastValue2;
                flowKt__DelayKt$debounceInternal$12.L$3 = timeoutMillis2;
                flowKt__DelayKt$debounceInternal$12.label = 2;
                Continuation uCont$iv22 = flowKt__DelayKt$debounceInternal$12;
                SelectInstance scope$iv22 = new SelectInstance(uCont$iv22);
                SelectInstance $this$invokeSuspend_u24lambda_u2d222 = scope$iv22;
                if (lastValue2.element != null) {
                }
                $this$invokeSuspend_u24lambda_u2d222.invoke(values2.getOnReceiveCatching(), new FlowKt__DelayKt$debounceInternal$1$3$2(lastValue2, downstream2, null));
                result = scope$iv22.getResult();
                if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                }
                if (result == obj2) {
                }
                break;
            case 2:
                flowKt__DelayKt$debounceInternal$1 = this;
                $result = obj;
                lastValue = (Ref.ObjectRef) flowKt__DelayKt$debounceInternal$1.L$2;
                values = (ReceiveChannel) flowKt__DelayKt$debounceInternal$1.L$1;
                downstream = (FlowCollector) flowKt__DelayKt$debounceInternal$1.L$0;
                ResultKt.throwOnFailure($result);
                j = 0;
                i = 1;
                if (lastValue.element != NullSurrogateKt.DONE) {
                }
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}

package kotlinx.coroutines.flow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;
import okhttp3.HttpUrl;

/* compiled from: Limit.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
final class FlowKt__LimitKt$dropWhile$1$1<T> implements FlowCollector, SuspendFunction {
    final /* synthetic */ Ref.BooleanRef $matched;
    final /* synthetic */ Function2<T, Continuation<? super Boolean>, Object> $predicate;
    final /* synthetic */ FlowCollector<T> $this_unsafeFlow;

    /* JADX WARN: Multi-variable type inference failed */
    FlowKt__LimitKt$dropWhile$1$1(Ref.BooleanRef booleanRef, FlowCollector<? super T> flowCollector, Function2<? super T, ? super Continuation<? super Boolean>, ? extends Object> function2) {
        this.$matched = booleanRef;
        this.$this_unsafeFlow = flowCollector;
        this.$predicate = function2;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
    @Override // kotlinx.coroutines.flow.FlowCollector
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object emit(T t, Continuation<? super Unit> continuation) throws Throwable {
        FlowKt__LimitKt$dropWhile$1$1$emit$1 flowKt__LimitKt$dropWhile$1$1$emit$1;
        FlowKt__LimitKt$dropWhile$1$1<T> flowKt__LimitKt$dropWhile$1$1;
        Object objInvoke;
        if (continuation instanceof FlowKt__LimitKt$dropWhile$1$1$emit$1) {
            flowKt__LimitKt$dropWhile$1$1$emit$1 = (FlowKt__LimitKt$dropWhile$1$1$emit$1) continuation;
            if ((flowKt__LimitKt$dropWhile$1$1$emit$1.label & Integer.MIN_VALUE) != 0) {
                flowKt__LimitKt$dropWhile$1$1$emit$1.label -= Integer.MIN_VALUE;
            } else {
                flowKt__LimitKt$dropWhile$1$1$emit$1 = new FlowKt__LimitKt$dropWhile$1$1$emit$1(this, continuation);
            }
        }
        FlowKt__LimitKt$dropWhile$1$1$emit$1 flowKt__LimitKt$dropWhile$1$1$emit$12 = flowKt__LimitKt$dropWhile$1$1$emit$1;
        Object obj = flowKt__LimitKt$dropWhile$1$1$emit$12.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (flowKt__LimitKt$dropWhile$1$1$emit$12.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                flowKt__LimitKt$dropWhile$1$1 = this;
                if (flowKt__LimitKt$dropWhile$1$1.$matched.element) {
                    FlowCollector<T> flowCollector = flowKt__LimitKt$dropWhile$1$1.$this_unsafeFlow;
                    flowKt__LimitKt$dropWhile$1$1$emit$12.label = 1;
                    if (flowCollector.emit(t, flowKt__LimitKt$dropWhile$1$1$emit$12) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    return Unit.INSTANCE;
                }
                Function2<T, Continuation<? super Boolean>, Object> function2 = flowKt__LimitKt$dropWhile$1$1.$predicate;
                flowKt__LimitKt$dropWhile$1$1$emit$12.L$0 = flowKt__LimitKt$dropWhile$1$1;
                flowKt__LimitKt$dropWhile$1$1$emit$12.L$1 = t;
                flowKt__LimitKt$dropWhile$1$1$emit$12.label = 2;
                objInvoke = function2.invoke(t, flowKt__LimitKt$dropWhile$1$1$emit$12);
                if (objInvoke == coroutine_suspended) {
                    return coroutine_suspended;
                }
                if (!((Boolean) objInvoke).booleanValue()) {
                    flowKt__LimitKt$dropWhile$1$1.$matched.element = true;
                    FlowCollector<T> flowCollector2 = flowKt__LimitKt$dropWhile$1$1.$this_unsafeFlow;
                    flowKt__LimitKt$dropWhile$1$1$emit$12.L$0 = null;
                    flowKt__LimitKt$dropWhile$1$1$emit$12.L$1 = null;
                    flowKt__LimitKt$dropWhile$1$1$emit$12.label = 3;
                    if (flowCollector2.emit(t, flowKt__LimitKt$dropWhile$1$1$emit$12) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                }
                return Unit.INSTANCE;
            case 1:
            case 3:
                ResultKt.throwOnFailure(obj);
                return Unit.INSTANCE;
            case 2:
                t = (T) flowKt__LimitKt$dropWhile$1$1$emit$12.L$1;
                flowKt__LimitKt$dropWhile$1$1 = (FlowKt__LimitKt$dropWhile$1$1) flowKt__LimitKt$dropWhile$1$1$emit$12.L$0;
                ResultKt.throwOnFailure(obj);
                objInvoke = obj;
                if (!((Boolean) objInvoke).booleanValue()) {
                }
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}

package kotlinx.coroutines.flow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;
import okhttp3.HttpUrl;

/* compiled from: Transform.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
final class FlowKt__TransformKt$runningReduce$1$1<T> implements FlowCollector, SuspendFunction {
    final /* synthetic */ Ref.ObjectRef<Object> $accumulator;
    final /* synthetic */ Function3<T, T, Continuation<? super T>, Object> $operation;
    final /* synthetic */ FlowCollector<T> $this_unsafeFlow;

    /* JADX WARN: Multi-variable type inference failed */
    FlowKt__TransformKt$runningReduce$1$1(Ref.ObjectRef<Object> objectRef, Function3<? super T, ? super T, ? super Continuation<? super T>, ? extends Object> function3, FlowCollector<? super T> flowCollector) {
        this.$accumulator = objectRef;
        this.$operation = function3;
        this.$this_unsafeFlow = flowCollector;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0080 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
    @Override // kotlinx.coroutines.flow.FlowCollector
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object emit(T t, Continuation<? super Unit> continuation) throws Throwable {
        FlowKt__TransformKt$runningReduce$1$1$emit$1 flowKt__TransformKt$runningReduce$1$1$emit$1;
        FlowKt__TransformKt$runningReduce$1$1 flowKt__TransformKt$runningReduce$1$1;
        Ref.ObjectRef<Object> objectRef;
        T t2;
        Ref.ObjectRef<Object> objectRef2;
        FlowKt__TransformKt$runningReduce$1$1 flowKt__TransformKt$runningReduce$1$12;
        FlowCollector<T> flowCollector;
        T t3;
        if (continuation instanceof FlowKt__TransformKt$runningReduce$1$1$emit$1) {
            flowKt__TransformKt$runningReduce$1$1$emit$1 = (FlowKt__TransformKt$runningReduce$1$1$emit$1) continuation;
            if ((flowKt__TransformKt$runningReduce$1$1$emit$1.label & Integer.MIN_VALUE) != 0) {
                flowKt__TransformKt$runningReduce$1$1$emit$1.label -= Integer.MIN_VALUE;
            } else {
                flowKt__TransformKt$runningReduce$1$1$emit$1 = new FlowKt__TransformKt$runningReduce$1$1$emit$1(this, continuation);
            }
        }
        FlowKt__TransformKt$runningReduce$1$1$emit$1 flowKt__TransformKt$runningReduce$1$1$emit$12 = flowKt__TransformKt$runningReduce$1$1$emit$1;
        Object obj = flowKt__TransformKt$runningReduce$1$1$emit$12.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (flowKt__TransformKt$runningReduce$1$1$emit$12.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                flowKt__TransformKt$runningReduce$1$1 = this;
                objectRef = flowKt__TransformKt$runningReduce$1$1.$accumulator;
                if (flowKt__TransformKt$runningReduce$1$1.$accumulator.element != NullSurrogateKt.NULL) {
                    Function3<T, T, Continuation<? super T>, Object> function3 = flowKt__TransformKt$runningReduce$1$1.$operation;
                    T t4 = flowKt__TransformKt$runningReduce$1$1.$accumulator.element;
                    flowKt__TransformKt$runningReduce$1$1$emit$12.L$0 = flowKt__TransformKt$runningReduce$1$1;
                    flowKt__TransformKt$runningReduce$1$1$emit$12.L$1 = objectRef;
                    flowKt__TransformKt$runningReduce$1$1$emit$12.label = 1;
                    Object objInvoke = function3.invoke(t4, t, flowKt__TransformKt$runningReduce$1$1$emit$12);
                    if (objInvoke == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    t2 = objInvoke;
                    objectRef2 = objectRef;
                    flowKt__TransformKt$runningReduce$1$12 = flowKt__TransformKt$runningReduce$1$1;
                    FlowKt__TransformKt$runningReduce$1$1 flowKt__TransformKt$runningReduce$1$13 = flowKt__TransformKt$runningReduce$1$12;
                    objectRef = objectRef2;
                    t = t2;
                    flowKt__TransformKt$runningReduce$1$1 = flowKt__TransformKt$runningReduce$1$13;
                }
                objectRef.element = t;
                flowCollector = flowKt__TransformKt$runningReduce$1$1.$this_unsafeFlow;
                t3 = flowKt__TransformKt$runningReduce$1$1.$accumulator.element;
                flowKt__TransformKt$runningReduce$1$1$emit$12.L$0 = null;
                flowKt__TransformKt$runningReduce$1$1$emit$12.L$1 = null;
                flowKt__TransformKt$runningReduce$1$1$emit$12.label = 2;
                if (flowCollector.emit(t3, flowKt__TransformKt$runningReduce$1$1$emit$12) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                return Unit.INSTANCE;
            case 1:
                objectRef2 = (Ref.ObjectRef) flowKt__TransformKt$runningReduce$1$1$emit$12.L$1;
                FlowKt__TransformKt$runningReduce$1$1 flowKt__TransformKt$runningReduce$1$14 = (FlowKt__TransformKt$runningReduce$1$1) flowKt__TransformKt$runningReduce$1$1$emit$12.L$0;
                ResultKt.throwOnFailure(obj);
                flowKt__TransformKt$runningReduce$1$12 = flowKt__TransformKt$runningReduce$1$14;
                t2 = obj;
                FlowKt__TransformKt$runningReduce$1$1 flowKt__TransformKt$runningReduce$1$132 = flowKt__TransformKt$runningReduce$1$12;
                objectRef = objectRef2;
                t = t2;
                flowKt__TransformKt$runningReduce$1$1 = flowKt__TransformKt$runningReduce$1$132;
                objectRef.element = t;
                flowCollector = flowKt__TransformKt$runningReduce$1$1.$this_unsafeFlow;
                t3 = flowKt__TransformKt$runningReduce$1$1.$accumulator.element;
                flowKt__TransformKt$runningReduce$1$1$emit$12.L$0 = null;
                flowKt__TransformKt$runningReduce$1$1$emit$12.L$1 = null;
                flowKt__TransformKt$runningReduce$1$1$emit$12.label = 2;
                if (flowCollector.emit(t3, flowKt__TransformKt$runningReduce$1$1$emit$12) == coroutine_suspended) {
                }
                return Unit.INSTANCE;
            case 2:
                ResultKt.throwOnFailure(obj);
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}

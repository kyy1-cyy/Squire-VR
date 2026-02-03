package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.SequenceScope;
import okhttp3.HttpUrl;

/* compiled from: View.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlin/sequences/SequenceScope;", "Landroid/view/View;"}, k = 3, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "androidx.core.view.ViewKt$allViews$1", f = "View.kt", i = {0}, l = {414, TypedValues.CycleType.TYPE_PATH_ROTATE}, m = "invokeSuspend", n = {"$this$sequence"}, s = {"L$0"})
/* loaded from: classes.dex */
final class ViewKt$allViews$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super View>, Continuation<? super Unit>, Object> {
    final /* synthetic */ View $this_allViews;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ViewKt$allViews$1(View view, Continuation<? super ViewKt$allViews$1> continuation) {
        super(2, continuation);
        this.$this_allViews = view;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        ViewKt$allViews$1 viewKt$allViews$1 = new ViewKt$allViews$1(this.$this_allViews, continuation);
        viewKt$allViews$1.L$0 = obj;
        return viewKt$allViews$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(SequenceScope<? super View> sequenceScope, Continuation<? super Unit> continuation) {
        return ((ViewKt$allViews$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x003e  */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object invokeSuspend(Object $result) throws Throwable {
        ViewKt$allViews$1 viewKt$allViews$1;
        SequenceScope $this$sequence;
        ViewKt$allViews$1 viewKt$allViews$12;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                viewKt$allViews$1 = this;
                $this$sequence = (SequenceScope) viewKt$allViews$1.L$0;
                viewKt$allViews$1.L$0 = $this$sequence;
                viewKt$allViews$1.label = 1;
                if ($this$sequence.yield(viewKt$allViews$1.$this_allViews, viewKt$allViews$1) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                if (viewKt$allViews$1.$this_allViews instanceof ViewGroup) {
                    viewKt$allViews$1.L$0 = null;
                    viewKt$allViews$1.label = 2;
                    if ($this$sequence.yieldAll(ViewGroupKt.getDescendants((ViewGroup) viewKt$allViews$1.$this_allViews), viewKt$allViews$1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    viewKt$allViews$12 = viewKt$allViews$1;
                }
                return Unit.INSTANCE;
            case 1:
                viewKt$allViews$1 = this;
                $this$sequence = (SequenceScope) viewKt$allViews$1.L$0;
                ResultKt.throwOnFailure($result);
                if (viewKt$allViews$1.$this_allViews instanceof ViewGroup) {
                }
                return Unit.INSTANCE;
            case 2:
                viewKt$allViews$12 = this;
                ResultKt.throwOnFailure($result);
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}

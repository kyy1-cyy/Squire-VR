package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import okhttp3.HttpUrl;

/* compiled from: ViewGroup.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlin/sequences/SequenceScope;", "Landroid/view/View;"}, k = 3, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
@DebugMetadata(c = "androidx.core.view.ViewGroupKt$descendants$1", f = "ViewGroup.kt", i = {0, 0, 0, 0, 1, 1, 1}, l = {119, 121}, m = "invokeSuspend", n = {"$this$sequence", "$this$forEach$iv", "child", "index$iv", "$this$sequence", "$this$forEach$iv", "index$iv"}, s = {"L$0", "L$1", "L$2", "I$0", "L$0", "L$1", "I$0"})
/* loaded from: classes.dex */
final class ViewGroupKt$descendants$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super View>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ViewGroup $this_descendants;
    int I$0;
    int I$1;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ViewGroupKt$descendants$1(ViewGroup viewGroup, Continuation<? super ViewGroupKt$descendants$1> continuation) {
        super(2, continuation);
        this.$this_descendants = viewGroup;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        ViewGroupKt$descendants$1 viewGroupKt$descendants$1 = new ViewGroupKt$descendants$1(this.$this_descendants, continuation);
        viewGroupKt$descendants$1.L$0 = obj;
        return viewGroupKt$descendants$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(SequenceScope<? super View> sequenceScope, Continuation<? super Unit> continuation) {
        return ((ViewGroupKt$descendants$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x004f  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0077  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x009a  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00a0  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x0093 -> B:20:0x0095). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x009a -> B:22:0x009e). Please report as a decompilation issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Object invokeSuspend(Object $result) throws Throwable {
        ViewGroupKt$descendants$1 viewGroupKt$descendants$1;
        SequenceScope $this$sequence;
        ViewGroup $this$forEach$iv;
        int $i$f$forEach;
        int index$iv;
        int childCount;
        int $i$f$forEach2;
        int $i$f$forEach3;
        View child;
        ViewGroup $this$forEach$iv2;
        SequenceScope $this$sequence2;
        ViewGroup $this$forEach$iv3;
        SequenceScope $this$sequence3;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                viewGroupKt$descendants$1 = this;
                $this$sequence = (SequenceScope) viewGroupKt$descendants$1.L$0;
                $this$forEach$iv = viewGroupKt$descendants$1.$this_descendants;
                $i$f$forEach = 0;
                index$iv = 0;
                childCount = $this$forEach$iv.getChildCount();
                if (index$iv >= childCount) {
                    View child2 = $this$forEach$iv.getChildAt(index$iv);
                    Intrinsics.checkNotNullExpressionValue(child2, "getChildAt(index)");
                    viewGroupKt$descendants$1.L$0 = $this$sequence;
                    viewGroupKt$descendants$1.L$1 = $this$forEach$iv;
                    viewGroupKt$descendants$1.L$2 = child2;
                    viewGroupKt$descendants$1.I$0 = index$iv;
                    viewGroupKt$descendants$1.I$1 = childCount;
                    viewGroupKt$descendants$1.label = 1;
                    if ($this$sequence.yield(child2, viewGroupKt$descendants$1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    $this$sequence2 = $this$sequence;
                    $i$f$forEach2 = $i$f$forEach;
                    $i$f$forEach3 = childCount;
                    child = child2;
                    $this$forEach$iv2 = $this$forEach$iv;
                    if (child instanceof ViewGroup) {
                        childCount = $i$f$forEach3;
                        $this$forEach$iv = $this$forEach$iv2;
                        $i$f$forEach = $i$f$forEach2;
                        $this$sequence = $this$sequence2;
                        index$iv++;
                        if (index$iv >= childCount) {
                        }
                    } else {
                        Sequence<View> descendants = ViewGroupKt.getDescendants((ViewGroup) child);
                        viewGroupKt$descendants$1.L$0 = $this$sequence2;
                        viewGroupKt$descendants$1.L$1 = $this$forEach$iv2;
                        viewGroupKt$descendants$1.L$2 = null;
                        viewGroupKt$descendants$1.I$0 = index$iv;
                        viewGroupKt$descendants$1.I$1 = $i$f$forEach3;
                        viewGroupKt$descendants$1.label = 2;
                        if ($this$sequence2.yieldAll(descendants, viewGroupKt$descendants$1) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        $this$forEach$iv3 = $this$forEach$iv2;
                        $this$sequence3 = $this$sequence2;
                        $this$forEach$iv = $this$forEach$iv3;
                        childCount = $i$f$forEach3;
                        $i$f$forEach = $i$f$forEach2;
                        $this$sequence = $this$sequence3;
                        index$iv++;
                        if (index$iv >= childCount) {
                            return Unit.INSTANCE;
                        }
                    }
                }
            case 1:
                viewGroupKt$descendants$1 = this;
                $i$f$forEach2 = 0;
                $i$f$forEach3 = viewGroupKt$descendants$1.I$1;
                index$iv = viewGroupKt$descendants$1.I$0;
                child = (View) viewGroupKt$descendants$1.L$2;
                $this$forEach$iv2 = (ViewGroup) viewGroupKt$descendants$1.L$1;
                $this$sequence2 = (SequenceScope) viewGroupKt$descendants$1.L$0;
                ResultKt.throwOnFailure($result);
                if (child instanceof ViewGroup) {
                }
                break;
            case 2:
                viewGroupKt$descendants$1 = this;
                $i$f$forEach2 = 0;
                $i$f$forEach3 = viewGroupKt$descendants$1.I$1;
                index$iv = viewGroupKt$descendants$1.I$0;
                $this$forEach$iv3 = (ViewGroup) viewGroupKt$descendants$1.L$1;
                $this$sequence3 = (SequenceScope) viewGroupKt$descendants$1.L$0;
                ResultKt.throwOnFailure($result);
                $this$forEach$iv = $this$forEach$iv3;
                childCount = $i$f$forEach3;
                $i$f$forEach = $i$f$forEach2;
                $this$sequence = $this$sequence3;
                index$iv++;
                if (index$iv >= childCount) {
                }
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}

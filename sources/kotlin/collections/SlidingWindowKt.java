package kotlin.collections;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import okhttp3.HttpUrl;

/* compiled from: SlidingWindow.kt */
@Metadata(d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0000\u001aH\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u0006\"\u0004\b\u0000\u0010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\b0\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000\u001aD\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u000e\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\u000e2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000¨\u0006\u000f"}, d2 = {"checkWindowSizeStep", HttpUrl.FRAGMENT_ENCODE_SET, "size", HttpUrl.FRAGMENT_ENCODE_SET, "step", "windowedIterator", HttpUrl.FRAGMENT_ENCODE_SET, HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "iterator", "partialWindows", HttpUrl.FRAGMENT_ENCODE_SET, "reuseBuffer", "windowedSequence", "Lkotlin/sequences/Sequence;", "kotlin-stdlib"}, k = 2, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class SlidingWindowKt {
    public static final void checkWindowSizeStep(int size, int step) {
        if (!(size > 0 && step > 0)) {
            throw new IllegalArgumentException((size != step ? "Both size " + size + " and step " + step + " must be greater than zero." : "size " + size + " must be greater than zero.").toString());
        }
    }

    public static final <T> Sequence<List<T>> windowedSequence(final Sequence<? extends T> sequence, final int i, final int i2, final boolean z, final boolean z2) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        checkWindowSizeStep(i, i2);
        return new Sequence<List<? extends T>>() { // from class: kotlin.collections.SlidingWindowKt$windowedSequence$$inlined$Sequence$1
            @Override // kotlin.sequences.Sequence
            public Iterator<List<? extends T>> iterator() {
                return SlidingWindowKt.windowedIterator(sequence.iterator(), i, i2, z, z2);
            }
        };
    }

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* compiled from: SlidingWindow.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "Lkotlin/sequences/SequenceScope;", HttpUrl.FRAGMENT_ENCODE_SET}, k = 3, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "kotlin.collections.SlidingWindowKt$windowedIterator$1", f = "SlidingWindow.kt", i = {0, 0, 0, 2, 2, 3, 3}, l = {34, 40, ConstraintLayout.LayoutParams.Table.LAYOUT_EDITOR_ABSOLUTEX, ConstraintLayout.LayoutParams.Table.LAYOUT_GONE_MARGIN_BASELINE, 58}, m = "invokeSuspend", n = {"$this$iterator", "buffer", "gap", "$this$iterator", "buffer", "$this$iterator", "buffer"}, s = {"L$0", "L$1", "I$0", "L$0", "L$1", "L$0", "L$1"})
    /* renamed from: kotlin.collections.SlidingWindowKt$windowedIterator$1, reason: invalid class name */
    static final class AnonymousClass1<T> extends RestrictedSuspendLambda implements Function2<SequenceScope<? super List<? extends T>>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Iterator<T> $iterator;
        final /* synthetic */ boolean $partialWindows;
        final /* synthetic */ boolean $reuseBuffer;
        final /* synthetic */ int $size;
        final /* synthetic */ int $step;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass1(int i, int i2, Iterator<? extends T> it, boolean z, boolean z2, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$size = i;
            this.$step = i2;
            this.$iterator = it;
            this.$reuseBuffer = z;
            this.$partialWindows = z2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.$size, this.$step, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
            anonymousClass1.L$0 = obj;
            return anonymousClass1;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super List<? extends T>> sequenceScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:16:0x0087  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x00b3  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x00b7  */
        /* JADX WARN: Removed duplicated region for block: B:37:0x00e8 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:42:0x00fc  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x0147  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x0154  */
        /* JADX WARN: Removed duplicated region for block: B:68:0x017f  */
        /* JADX WARN: Removed duplicated region for block: B:74:0x019d  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x00ac -> B:24:0x00af). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:52:0x013a -> B:54:0x013d). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:65:0x0176 -> B:67:0x0179). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Object invokeSuspend(Object $result) throws Throwable {
            AnonymousClass1 anonymousClass1;
            RingBuffer buffer;
            SequenceScope sequenceScope;
            Iterator<T> it;
            int skip;
            Object obj;
            AnonymousClass1 anonymousClass12;
            SequenceScope $this$iterator;
            int gap;
            ArrayList buffer2;
            Iterator<T> it2;
            RingBuffer buffer3;
            SequenceScope sequenceScope2;
            Object obj2;
            AnonymousClass1 anonymousClass13;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    anonymousClass1 = this;
                    SequenceScope $this$iterator2 = (SequenceScope) anonymousClass1.L$0;
                    int bufferInitialCapacity = RangesKt.coerceAtMost(anonymousClass1.$size, 1024);
                    int gap2 = anonymousClass1.$step - anonymousClass1.$size;
                    if (gap2 >= 0) {
                        ArrayList buffer4 = new ArrayList(bufferInitialCapacity);
                        skip = 0;
                        Iterator<T> it3 = anonymousClass1.$iterator;
                        obj = coroutine_suspended;
                        anonymousClass12 = anonymousClass1;
                        $this$iterator = $this$iterator2;
                        gap = gap2;
                        buffer2 = buffer4;
                        it2 = it3;
                        while (it2.hasNext()) {
                            T next = it2.next();
                            if (skip > 0) {
                                skip--;
                            } else {
                                buffer2.add(next);
                                if (buffer2.size() == anonymousClass12.$size) {
                                    anonymousClass12.L$0 = $this$iterator;
                                    anonymousClass12.L$1 = buffer2;
                                    anonymousClass12.L$2 = it2;
                                    anonymousClass12.I$0 = gap;
                                    anonymousClass12.label = 1;
                                    if ($this$iterator.yield(buffer2, anonymousClass12) == obj) {
                                        return obj;
                                    }
                                    if (anonymousClass12.$reuseBuffer) {
                                        buffer2 = new ArrayList(anonymousClass12.$size);
                                    } else {
                                        buffer2.clear();
                                    }
                                    skip = gap;
                                    while (it2.hasNext()) {
                                    }
                                }
                            }
                        }
                        if ((true ^ buffer2.isEmpty()) && (anonymousClass12.$partialWindows || buffer2.size() == anonymousClass12.$size)) {
                            anonymousClass12.L$0 = null;
                            anonymousClass12.L$1 = null;
                            anonymousClass12.L$2 = null;
                            anonymousClass12.label = 2;
                            if ($this$iterator.yield(buffer2, anonymousClass12) == obj) {
                                return obj;
                            }
                        }
                        return Unit.INSTANCE;
                    }
                    buffer = new RingBuffer(bufferInitialCapacity);
                    sequenceScope = $this$iterator2;
                    it = anonymousClass1.$iterator;
                    while (it.hasNext()) {
                        buffer.add((RingBuffer) it.next());
                        if (buffer.isFull()) {
                            if (buffer.size() >= anonymousClass1.$size) {
                                List arrayList = anonymousClass1.$reuseBuffer ? buffer : new ArrayList(buffer);
                                anonymousClass1.L$0 = sequenceScope;
                                anonymousClass1.L$1 = buffer;
                                anonymousClass1.L$2 = it;
                                anonymousClass1.label = 3;
                                if (sequenceScope.yield(arrayList, anonymousClass1) == coroutine_suspended) {
                                    return coroutine_suspended;
                                }
                                buffer.removeFirst(anonymousClass1.$step);
                                while (it.hasNext()) {
                                }
                            } else {
                                buffer = buffer.expanded(anonymousClass1.$size);
                            }
                        }
                    }
                    if (anonymousClass1.$partialWindows) {
                        return Unit.INSTANCE;
                    }
                    buffer3 = buffer;
                    sequenceScope2 = sequenceScope;
                    AnonymousClass1 anonymousClass14 = anonymousClass1;
                    obj2 = coroutine_suspended;
                    anonymousClass13 = anonymousClass14;
                    if (buffer3.size() <= anonymousClass13.$step) {
                        List arrayList2 = anonymousClass13.$reuseBuffer ? buffer3 : new ArrayList(buffer3);
                        anonymousClass13.L$0 = sequenceScope2;
                        anonymousClass13.L$1 = buffer3;
                        anonymousClass13.L$2 = null;
                        anonymousClass13.label = 4;
                        if (sequenceScope2.yield(arrayList2, anonymousClass13) == obj2) {
                            return obj2;
                        }
                        buffer3.removeFirst(anonymousClass13.$step);
                        if (buffer3.size() <= anonymousClass13.$step) {
                            if (true ^ buffer3.isEmpty()) {
                                anonymousClass13.L$0 = null;
                                anonymousClass13.L$1 = null;
                                anonymousClass13.L$2 = null;
                                anonymousClass13.label = 5;
                                if (sequenceScope2.yield(buffer3, anonymousClass13) == obj2) {
                                    return obj2;
                                }
                            }
                            return Unit.INSTANCE;
                        }
                    }
                    break;
                case 1:
                    gap = this.I$0;
                    it2 = (Iterator) this.L$2;
                    buffer2 = (ArrayList) this.L$1;
                    SequenceScope $this$iterator3 = (SequenceScope) this.L$0;
                    ResultKt.throwOnFailure($result);
                    $this$iterator = $this$iterator3;
                    obj = coroutine_suspended;
                    anonymousClass12 = this;
                    if (anonymousClass12.$reuseBuffer) {
                    }
                    skip = gap;
                    while (it2.hasNext()) {
                    }
                    if (true ^ buffer2.isEmpty()) {
                        anonymousClass12.L$0 = null;
                        anonymousClass12.L$1 = null;
                        anonymousClass12.L$2 = null;
                        anonymousClass12.label = 2;
                        if ($this$iterator.yield(buffer2, anonymousClass12) == obj) {
                        }
                        break;
                    }
                    return Unit.INSTANCE;
                case 2:
                    ResultKt.throwOnFailure($result);
                    return Unit.INSTANCE;
                case 3:
                    anonymousClass1 = this;
                    it = (Iterator) anonymousClass1.L$2;
                    buffer = (RingBuffer) anonymousClass1.L$1;
                    sequenceScope = (SequenceScope) anonymousClass1.L$0;
                    ResultKt.throwOnFailure($result);
                    buffer.removeFirst(anonymousClass1.$step);
                    while (it.hasNext()) {
                    }
                    if (anonymousClass1.$partialWindows) {
                    }
                    break;
                case 4:
                    buffer3 = (RingBuffer) this.L$1;
                    sequenceScope2 = (SequenceScope) this.L$0;
                    ResultKt.throwOnFailure($result);
                    obj2 = coroutine_suspended;
                    anonymousClass13 = this;
                    buffer3.removeFirst(anonymousClass13.$step);
                    if (buffer3.size() <= anonymousClass13.$step) {
                    }
                    break;
                case 5:
                    ResultKt.throwOnFailure($result);
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    public static final <T> Iterator<List<T>> windowedIterator(Iterator<? extends T> iterator, int size, int step, boolean partialWindows, boolean reuseBuffer) {
        Intrinsics.checkNotNullParameter(iterator, "iterator");
        return !iterator.hasNext() ? EmptyIterator.INSTANCE : SequencesKt.iterator(new AnonymousClass1(size, step, iterator, reuseBuffer, partialWindows, null));
    }
}

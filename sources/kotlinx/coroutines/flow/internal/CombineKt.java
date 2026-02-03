package kotlinx.coroutines.flow.internal;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.IndexedValue;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.YieldKt;
import kotlinx.coroutines.channels.Channel;
import kotlinx.coroutines.channels.ChannelKt;
import kotlinx.coroutines.channels.ChannelResult;
import kotlinx.coroutines.channels.SendChannel;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import okhttp3.HttpUrl;

/* compiled from: Combine.kt */
@Metadata(d1 = {"\u0000>\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001an\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0003\"\u0004\b\u0001\u0010\u0004\"\u0004\b\u0002\u0010\u00022\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u00012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00040\u00012(\u0010\u0007\u001a$\b\u0001\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\t\u0012\u0006\u0012\u0004\u0018\u00010\n0\bH\u0000ø\u0001\u0000¢\u0006\u0002\u0010\u000b\u001a\u0090\u0001\u0010\f\u001a\u00020\r\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u00020\u000f2\u0014\u0010\u0010\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u0002H\u000e0\u00010\u00112\u0016\u0010\u0012\u001a\u0012\u0012\u000e\u0012\f\u0012\u0006\u0012\u0004\u0018\u0001H\u000e\u0018\u00010\u00110\u001329\u0010\u0007\u001a5\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u000f\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u000e0\u0011\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\t\u0012\u0006\u0012\u0004\u0018\u00010\n0\b¢\u0006\u0002\b\u0014H\u0081@ø\u0001\u0000¢\u0006\u0002\u0010\u0015*\u001c\b\u0002\u0010\u0016\"\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u00172\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u0017\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0018"}, d2 = {"zipImpl", "Lkotlinx/coroutines/flow/Flow;", "R", "T1", "T2", "flow", "flow2", "transform", "Lkotlin/Function3;", "Lkotlin/coroutines/Continuation;", HttpUrl.FRAGMENT_ENCODE_SET, "(Lkotlinx/coroutines/flow/Flow;Lkotlinx/coroutines/flow/Flow;Lkotlin/jvm/functions/Function3;)Lkotlinx/coroutines/flow/Flow;", "combineInternal", HttpUrl.FRAGMENT_ENCODE_SET, ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/flow/FlowCollector;", "flows", HttpUrl.FRAGMENT_ENCODE_SET, "arrayFactory", "Lkotlin/Function0;", "Lkotlin/ExtensionFunctionType;", "(Lkotlinx/coroutines/flow/FlowCollector;[Lkotlinx/coroutines/flow/Flow;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Update", "Lkotlin/collections/IndexedValue;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class CombineKt {

    /* compiled from: Combine.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\u00020\u0004H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "R", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$combineInternal$2", f = "Combine.kt", i = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2}, l = {57, 79, 82}, m = "invokeSuspend", n = {"latestValues", "resultChannel", "lastReceivedEpoch", "remainingAbsentValues", "currentEpoch", "latestValues", "resultChannel", "lastReceivedEpoch", "remainingAbsentValues", "currentEpoch", "latestValues", "resultChannel", "lastReceivedEpoch", "remainingAbsentValues", "currentEpoch"}, s = {"L$0", "L$1", "L$2", "I$0", "I$1", "L$0", "L$1", "L$2", "I$0", "I$1", "L$0", "L$1", "L$2", "I$0", "I$1"})
    /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$combineInternal$2, reason: invalid class name */
    static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function0<T[]> $arrayFactory;
        final /* synthetic */ Flow<T>[] $flows;
        final /* synthetic */ FlowCollector<R> $this_combineInternal;
        final /* synthetic */ Function3<FlowCollector<? super R>, T[], Continuation<? super Unit>, Object> $transform;
        int I$0;
        int I$1;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass2(Flow<? extends T>[] flowArr, Function0<T[]> function0, Function3<? super FlowCollector<? super R>, ? super T[], ? super Continuation<? super Unit>, ? extends Object> function3, FlowCollector<? super R> flowCollector, Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
            this.$flows = flowArr;
            this.$arrayFactory = function0;
            this.$transform = function3;
            this.$this_combineInternal = flowCollector;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass2 anonymousClass2 = new AnonymousClass2(this.$flows, this.$arrayFactory, this.$transform, this.$this_combineInternal, continuation);
            anonymousClass2.L$0 = obj;
            return anonymousClass2;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass2) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:19:0x00e8 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:20:0x00e9  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x00f2  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x00f5 A[LOOP:0: B:25:0x00f5->B:50:?, LOOP_START, PHI: r6 r9
          0x00f5: PHI (r6v11 'remainingAbsentValues' int) = (r6v10 'remainingAbsentValues' int), (r6v12 'remainingAbsentValues' int) binds: [B:22:0x00f0, B:50:?] A[DONT_GENERATE, DONT_INLINE]
          0x00f5: PHI (r9v8 'element' kotlin.collections.IndexedValue) = (r9v7 'element' kotlin.collections.IndexedValue), (r9v20 'element' kotlin.collections.IndexedValue) binds: [B:22:0x00f0, B:50:?] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Removed duplicated region for block: B:34:0x011e  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x0169  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:39:0x0140 -> B:17:0x00ce). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:44:0x0166 -> B:17:0x00ce). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:46:0x0169 -> B:17:0x00ce). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Object invokeSuspend(Object $result) throws Throwable {
            AnonymousClass2 anonymousClass2;
            Object[] latestValues;
            byte[] lastReceivedEpoch;
            int remainingAbsentValues;
            Channel resultChannel;
            int remainingAbsentValues2;
            Object[] latestValues2;
            Object holder;
            IndexedValue element;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    anonymousClass2 = this;
                    CoroutineScope $this$flowScope = (CoroutineScope) anonymousClass2.L$0;
                    int size = anonymousClass2.$flows.length;
                    if (size == 0) {
                        return Unit.INSTANCE;
                    }
                    latestValues = new Object[size];
                    ArraysKt.fill$default(latestValues, NullSurrogateKt.UNINITIALIZED, 0, 0, 6, (Object) null);
                    Channel resultChannel2 = ChannelKt.Channel$default(size, null, null, 6, null);
                    AtomicInteger nonClosed = new AtomicInteger(size);
                    for (int i = 0; i < size; i++) {
                        int i2 = i;
                        BuildersKt__Builders_commonKt.launch$default($this$flowScope, null, null, new AnonymousClass1(anonymousClass2.$flows, i2, nonClosed, resultChannel2, null), 3, null);
                    }
                    lastReceivedEpoch = new byte[size];
                    remainingAbsentValues = 0;
                    resultChannel = resultChannel2;
                    remainingAbsentValues2 = size;
                    remainingAbsentValues = (byte) (remainingAbsentValues + 1);
                    anonymousClass2.L$0 = latestValues;
                    anonymousClass2.L$1 = resultChannel;
                    anonymousClass2.L$2 = lastReceivedEpoch;
                    anonymousClass2.I$0 = remainingAbsentValues2;
                    anonymousClass2.I$1 = remainingAbsentValues;
                    anonymousClass2.label = 1;
                    holder = resultChannel.mo1638receiveCatchingJP2dKIU(anonymousClass2);
                    if (holder == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    latestValues2 = latestValues;
                    element = (IndexedValue) ChannelResult.m1650getOrNullimpl(holder);
                    if (element != null) {
                        return Unit.INSTANCE;
                    }
                    do {
                        int index = element.getIndex();
                        Object previous = latestValues2[index];
                        latestValues2[index] = element.getValue();
                        if (previous == NullSurrogateKt.UNINITIALIZED) {
                            remainingAbsentValues2--;
                        }
                        if (lastReceivedEpoch[index] != remainingAbsentValues) {
                            lastReceivedEpoch[index] = (byte) remainingAbsentValues;
                            element = (IndexedValue) ChannelResult.m1650getOrNullimpl(resultChannel.mo1639tryReceivePtdJZtk());
                        }
                        if (remainingAbsentValues2 != 0) {
                            latestValues = latestValues2;
                        } else {
                            Object[] results = (Object[]) anonymousClass2.$arrayFactory.invoke();
                            if (results == null) {
                                Function3<FlowCollector<? super R>, T[], Continuation<? super Unit>, Object> function3 = anonymousClass2.$transform;
                                Object obj = anonymousClass2.$this_combineInternal;
                                anonymousClass2.L$0 = latestValues2;
                                anonymousClass2.L$1 = resultChannel;
                                anonymousClass2.L$2 = lastReceivedEpoch;
                                anonymousClass2.I$0 = remainingAbsentValues2;
                                anonymousClass2.I$1 = remainingAbsentValues;
                                anonymousClass2.label = 2;
                                if (function3.invoke(obj, latestValues2, anonymousClass2) == coroutine_suspended) {
                                    return coroutine_suspended;
                                }
                                latestValues = latestValues2;
                            } else {
                                ArraysKt.copyInto$default(latestValues2, results, 0, 0, 0, 14, (Object) null);
                                Function3<FlowCollector<? super R>, T[], Continuation<? super Unit>, Object> function32 = anonymousClass2.$transform;
                                Object obj2 = anonymousClass2.$this_combineInternal;
                                anonymousClass2.L$0 = latestValues2;
                                anonymousClass2.L$1 = resultChannel;
                                anonymousClass2.L$2 = lastReceivedEpoch;
                                anonymousClass2.I$0 = remainingAbsentValues2;
                                anonymousClass2.I$1 = remainingAbsentValues;
                                anonymousClass2.label = 3;
                                if (function32.invoke(obj2, results, anonymousClass2) == coroutine_suspended) {
                                    return coroutine_suspended;
                                }
                                latestValues = latestValues2;
                            }
                        }
                        remainingAbsentValues = (byte) (remainingAbsentValues + 1);
                        anonymousClass2.L$0 = latestValues;
                        anonymousClass2.L$1 = resultChannel;
                        anonymousClass2.L$2 = lastReceivedEpoch;
                        anonymousClass2.I$0 = remainingAbsentValues2;
                        anonymousClass2.I$1 = remainingAbsentValues;
                        anonymousClass2.label = 1;
                        holder = resultChannel.mo1638receiveCatchingJP2dKIU(anonymousClass2);
                        if (holder == coroutine_suspended) {
                        }
                    } while (element != null);
                    if (remainingAbsentValues2 != 0) {
                    }
                    remainingAbsentValues = (byte) (remainingAbsentValues + 1);
                    anonymousClass2.L$0 = latestValues;
                    anonymousClass2.L$1 = resultChannel;
                    anonymousClass2.L$2 = lastReceivedEpoch;
                    anonymousClass2.I$0 = remainingAbsentValues2;
                    anonymousClass2.I$1 = remainingAbsentValues;
                    anonymousClass2.label = 1;
                    holder = resultChannel.mo1638receiveCatchingJP2dKIU(anonymousClass2);
                    if (holder == coroutine_suspended) {
                    }
                case 1:
                    anonymousClass2 = this;
                    int i3 = anonymousClass2.I$1;
                    int remainingAbsentValues3 = anonymousClass2.I$0;
                    byte[] lastReceivedEpoch2 = (byte[]) anonymousClass2.L$2;
                    resultChannel = (Channel) anonymousClass2.L$1;
                    latestValues2 = (Object[]) anonymousClass2.L$0;
                    ResultKt.throwOnFailure($result);
                    holder = ((ChannelResult) $result).getHolder();
                    remainingAbsentValues = i3;
                    lastReceivedEpoch = lastReceivedEpoch2;
                    remainingAbsentValues2 = remainingAbsentValues3;
                    element = (IndexedValue) ChannelResult.m1650getOrNullimpl(holder);
                    if (element != null) {
                    }
                    break;
                case 2:
                    anonymousClass2 = this;
                    int i4 = anonymousClass2.I$1;
                    int remainingAbsentValues4 = anonymousClass2.I$0;
                    byte[] lastReceivedEpoch3 = (byte[]) anonymousClass2.L$2;
                    resultChannel = (Channel) anonymousClass2.L$1;
                    Object[] latestValues3 = (Object[]) anonymousClass2.L$0;
                    ResultKt.throwOnFailure($result);
                    latestValues = latestValues3;
                    remainingAbsentValues = i4;
                    lastReceivedEpoch = lastReceivedEpoch3;
                    remainingAbsentValues2 = remainingAbsentValues4;
                    remainingAbsentValues = (byte) (remainingAbsentValues + 1);
                    anonymousClass2.L$0 = latestValues;
                    anonymousClass2.L$1 = resultChannel;
                    anonymousClass2.L$2 = lastReceivedEpoch;
                    anonymousClass2.I$0 = remainingAbsentValues2;
                    anonymousClass2.I$1 = remainingAbsentValues;
                    anonymousClass2.label = 1;
                    holder = resultChannel.mo1638receiveCatchingJP2dKIU(anonymousClass2);
                    if (holder == coroutine_suspended) {
                    }
                    break;
                case 3:
                    anonymousClass2 = this;
                    int i5 = anonymousClass2.I$1;
                    int remainingAbsentValues5 = anonymousClass2.I$0;
                    byte[] lastReceivedEpoch4 = (byte[]) anonymousClass2.L$2;
                    resultChannel = (Channel) anonymousClass2.L$1;
                    Object[] latestValues4 = (Object[]) anonymousClass2.L$0;
                    ResultKt.throwOnFailure($result);
                    latestValues = latestValues4;
                    remainingAbsentValues = i5;
                    lastReceivedEpoch = lastReceivedEpoch4;
                    remainingAbsentValues2 = remainingAbsentValues5;
                    remainingAbsentValues = (byte) (remainingAbsentValues + 1);
                    anonymousClass2.L$0 = latestValues;
                    anonymousClass2.L$1 = resultChannel;
                    anonymousClass2.L$2 = lastReceivedEpoch;
                    anonymousClass2.I$0 = remainingAbsentValues2;
                    anonymousClass2.I$1 = remainingAbsentValues;
                    anonymousClass2.label = 1;
                    holder = resultChannel.mo1638receiveCatchingJP2dKIU(anonymousClass2);
                    if (holder == coroutine_suspended) {
                    }
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }

        /* compiled from: Combine.kt */
        @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\u00020\u0004H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "R", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
        @DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$combineInternal$2$1", f = "Combine.kt", i = {}, l = {34}, m = "invokeSuspend", n = {}, s = {})
        /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$combineInternal$2$1, reason: invalid class name */
        static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            final /* synthetic */ Flow<T>[] $flows;
            final /* synthetic */ int $i;
            final /* synthetic */ AtomicInteger $nonClosed;
            final /* synthetic */ Channel<IndexedValue<Object>> $resultChannel;
            int label;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            AnonymousClass1(Flow<? extends T>[] flowArr, int i, AtomicInteger atomicInteger, Channel<IndexedValue<Object>> channel, Continuation<? super AnonymousClass1> continuation) {
                super(2, continuation);
                this.$flows = flowArr;
                this.$i = i;
                this.$nonClosed = atomicInteger;
                this.$resultChannel = channel;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return new AnonymousClass1(this.$flows, this.$i, this.$nonClosed, this.$resultChannel, continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            /* JADX WARN: Removed duplicated region for block: B:18:0x0045  */
            /* JADX WARN: Removed duplicated region for block: B:25:0x005c  */
            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public final Object invokeSuspend(Object $result) throws Throwable {
                Throwable th;
                AnonymousClass1 anonymousClass1;
                Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0:
                        ResultKt.throwOnFailure($result);
                        try {
                            this.label = 1;
                            if (this.$flows[this.$i].collect(new C00101(this.$resultChannel, this.$i), this) == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            anonymousClass1 = this;
                            if (anonymousClass1.$nonClosed.decrementAndGet() == 0) {
                                SendChannel.DefaultImpls.close$default(anonymousClass1.$resultChannel, null, 1, null);
                            }
                            return Unit.INSTANCE;
                        } catch (Throwable th2) {
                            th = th2;
                            anonymousClass1 = this;
                            if (anonymousClass1.$nonClosed.decrementAndGet() == 0) {
                                SendChannel.DefaultImpls.close$default(anonymousClass1.$resultChannel, null, 1, null);
                            }
                            throw th;
                        }
                    case 1:
                        anonymousClass1 = this;
                        try {
                            ResultKt.throwOnFailure($result);
                            if (anonymousClass1.$nonClosed.decrementAndGet() == 0) {
                            }
                            return Unit.INSTANCE;
                        } catch (Throwable th3) {
                            th = th3;
                            if (anonymousClass1.$nonClosed.decrementAndGet() == 0) {
                            }
                            throw th;
                        }
                    default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
            }

            /* compiled from: Combine.kt */
            @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u00032\u0006\u0010\u0004\u001a\u0002H\u0003H\u008a@¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "R", ExifInterface.GPS_DIRECTION_TRUE, "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
            /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$combineInternal$2$1$1, reason: invalid class name and collision with other inner class name */
            static final class C00101<T> implements FlowCollector, SuspendFunction {
                final /* synthetic */ int $i;
                final /* synthetic */ Channel<IndexedValue<Object>> $resultChannel;

                C00101(Channel<IndexedValue<Object>> channel, int i) {
                    this.$resultChannel = channel;
                    this.$i = i;
                }

                /* JADX WARN: Removed duplicated region for block: B:19:0x0055 A[RETURN] */
                /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
                @Override // kotlinx.coroutines.flow.FlowCollector
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                */
                public final Object emit(T t, Continuation<? super Unit> continuation) throws Throwable {
                    CombineKt$combineInternal$2$1$1$emit$1 combineKt$combineInternal$2$1$1$emit$1;
                    if (continuation instanceof CombineKt$combineInternal$2$1$1$emit$1) {
                        combineKt$combineInternal$2$1$1$emit$1 = (CombineKt$combineInternal$2$1$1$emit$1) continuation;
                        if ((combineKt$combineInternal$2$1$1$emit$1.label & Integer.MIN_VALUE) != 0) {
                            combineKt$combineInternal$2$1$1$emit$1.label -= Integer.MIN_VALUE;
                        } else {
                            combineKt$combineInternal$2$1$1$emit$1 = new CombineKt$combineInternal$2$1$1$emit$1(this, continuation);
                        }
                    }
                    CombineKt$combineInternal$2$1$1$emit$1 combineKt$combineInternal$2$1$1$emit$12 = combineKt$combineInternal$2$1$1$emit$1;
                    Object $result = combineKt$combineInternal$2$1$1$emit$12.result;
                    Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                    switch (combineKt$combineInternal$2$1$1$emit$12.label) {
                        case 0:
                            ResultKt.throwOnFailure($result);
                            Channel<IndexedValue<Object>> channel = this.$resultChannel;
                            IndexedValue<Object> indexedValue = new IndexedValue<>(this.$i, t);
                            combineKt$combineInternal$2$1$1$emit$12.label = 1;
                            Object value = channel.send(indexedValue, combineKt$combineInternal$2$1$1$emit$12);
                            if (value == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            combineKt$combineInternal$2$1$1$emit$12.label = 2;
                            if (YieldKt.yield(combineKt$combineInternal$2$1$1$emit$12) == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            return Unit.INSTANCE;
                        case 1:
                            ResultKt.throwOnFailure($result);
                            combineKt$combineInternal$2$1$1$emit$12.label = 2;
                            if (YieldKt.yield(combineKt$combineInternal$2$1$1$emit$12) == coroutine_suspended) {
                            }
                            return Unit.INSTANCE;
                        case 2:
                            ResultKt.throwOnFailure($result);
                            return Unit.INSTANCE;
                        default:
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                }
            }
        }
    }

    public static final <R, T> Object combineInternal(FlowCollector<? super R> flowCollector, Flow<? extends T>[] flowArr, Function0<T[]> function0, Function3<? super FlowCollector<? super R>, ? super T[], ? super Continuation<? super Unit>, ? extends Object> function3, Continuation<? super Unit> continuation) {
        Object objFlowScope = FlowCoroutineKt.flowScope(new AnonymousClass2(flowArr, function0, function3, flowCollector, null), continuation);
        return objFlowScope == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objFlowScope : Unit.INSTANCE;
    }

    public static final <T1, T2, R> Flow<R> zipImpl(final Flow<? extends T1> flow, final Flow<? extends T2> flow2, final Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3) {
        return new Flow<R>() { // from class: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$$inlined$unsafeFlow$1
            @Override // kotlinx.coroutines.flow.Flow
            public Object collect(FlowCollector<? super R> flowCollector, Continuation<? super Unit> continuation) {
                Object objCoroutineScope = CoroutineScopeKt.coroutineScope(new CombineKt$zipImpl$1$1(flowCollector, flow2, flow, function3, null), continuation);
                return objCoroutineScope == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objCoroutineScope : Unit.INSTANCE;
            }
        };
    }
}

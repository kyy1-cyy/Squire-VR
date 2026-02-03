package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import okhttp3.HttpUrl;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Source;

/* compiled from: -FileSystem.kt */
@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0080@ø\u0001\u0000¢\u0006\u0002\u0010\f\u001a\u001c\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0000\u001a\u001c\u0010\u0010\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\nH\u0000\u001a\u001c\u0010\u0013\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0016\u001a\u00020\n*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0019\u001a\u00020\u001a*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0003*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001c"}, d2 = {"collectRecursively", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlin/sequences/SequenceScope;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "stack", "Lkotlin/collections/ArrayDeque;", "path", "followSymlinks", HttpUrl.FRAGMENT_ENCODE_SET, "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "commonCopy", "source", TypedValues.AttributesType.S_TARGET, "commonCreateDirectories", "dir", "mustCreate", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonExists", "commonListRecursively", "Lkotlin/sequences/Sequence;", "commonMetadata", "Lokio/FileMetadata;", "symlinkTarget", "okio"}, k = 2, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class _FileSystemKt {

    /* compiled from: -FileSystem.kt */
    @Metadata(k = 3, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "okio.internal._FileSystemKt", f = "-FileSystem.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1}, l = {113, 132, 142}, m = "collectRecursively", n = {"$this$collectRecursively", "fileSystem", "stack", "path", "followSymlinks", "postorder", "$this$collectRecursively", "fileSystem", "stack", "path", "followSymlinks", "postorder"}, s = {"L$0", "L$1", "L$2", "L$3", "Z$0", "Z$1", "L$0", "L$1", "L$2", "L$3", "Z$0", "Z$1"})
    /* renamed from: okio.internal._FileSystemKt$collectRecursively$1, reason: invalid class name */
    static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        boolean Z$0;
        boolean Z$1;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return _FileSystemKt.collectRecursively(null, null, null, null, false, false, this);
        }
    }

    public static final FileMetadata commonMetadata(FileSystem $this$commonMetadata, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonMetadata, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        FileMetadata fileMetadataMetadataOrNull = $this$commonMetadata.metadataOrNull(path);
        if (fileMetadataMetadataOrNull != null) {
            return fileMetadataMetadataOrNull;
        }
        throw new FileNotFoundException(Intrinsics.stringPlus("no such file: ", path));
    }

    public static final boolean commonExists(FileSystem $this$commonExists, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonExists, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        return $this$commonExists.metadataOrNull(path) != null;
    }

    public static final void commonCreateDirectories(FileSystem $this$commonCreateDirectories, Path dir, boolean mustCreate) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonCreateDirectories, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        ArrayDeque directories = new ArrayDeque();
        for (Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
            directories.addFirst(path);
        }
        if (mustCreate && directories.isEmpty()) {
            throw new IOException(dir + " already exist.");
        }
        Iterator it = directories.iterator();
        while (it.hasNext()) {
            Path toCreate = (Path) it.next();
            $this$commonCreateDirectories.createDirectory(toCreate);
        }
    }

    public static final void commonCopy(FileSystem $this$commonCopy, Path source, Path target) throws IOException {
        Object result$iv;
        Throwable thrown$iv;
        Intrinsics.checkNotNullParameter($this$commonCopy, "<this>");
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        Closeable $this$use$iv = $this$commonCopy.source(source);
        Object result$iv2 = null;
        Throwable thrown$iv2 = null;
        try {
            Source bytesIn = (Source) $this$use$iv;
            Closeable $this$use$iv2 = Okio.buffer($this$commonCopy.sink(target));
            result$iv = null;
            thrown$iv = null;
            try {
                BufferedSink bytesOut = (BufferedSink) $this$use$iv2;
                result$iv = Long.valueOf(bytesOut.writeAll(bytesIn));
            } catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            if ($this$use$iv2 != null) {
                try {
                    $this$use$iv2.close();
                } catch (Throwable t$iv2) {
                    if (thrown$iv == null) {
                        thrown$iv = t$iv2;
                    } else {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv2);
                    }
                }
            }
        } catch (Throwable t$iv3) {
            thrown$iv2 = t$iv3;
        }
        if (thrown$iv != null) {
            throw thrown$iv;
        }
        Intrinsics.checkNotNull(result$iv);
        result$iv2 = Long.valueOf(((Number) result$iv).longValue());
        if ($this$use$iv != null) {
            try {
                $this$use$iv.close();
            } catch (Throwable t$iv4) {
                if (thrown$iv2 == null) {
                    thrown$iv2 = t$iv4;
                } else {
                    ExceptionsKt.addSuppressed(thrown$iv2, t$iv4);
                }
            }
        }
        if (thrown$iv2 != null) {
            throw thrown$iv2;
        }
        Intrinsics.checkNotNull(result$iv2);
    }

    public static final void commonDeleteRecursively(FileSystem $this$commonDeleteRecursively, Path fileOrDirectory, boolean mustExist) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonDeleteRecursively, "<this>");
        Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
        Sequence sequence = SequencesKt.sequence(new _FileSystemKt$commonDeleteRecursively$sequence$1($this$commonDeleteRecursively, fileOrDirectory, null));
        Iterator iterator = sequence.iterator();
        while (iterator.hasNext()) {
            Path toDelete = (Path) iterator.next();
            $this$commonDeleteRecursively.delete(toDelete, mustExist && !iterator.hasNext());
        }
    }

    /* compiled from: -FileSystem.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", HttpUrl.FRAGMENT_ENCODE_SET, "Lkotlin/sequences/SequenceScope;", "Lokio/Path;"}, k = 3, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    @DebugMetadata(c = "okio.internal._FileSystemKt$commonListRecursively$1", f = "-FileSystem.kt", i = {0, 0}, l = {93}, m = "invokeSuspend", n = {"$this$sequence", "stack"}, s = {"L$0", "L$1"})
    /* renamed from: okio.internal._FileSystemKt$commonListRecursively$1, reason: invalid class name and case insensitive filesystem */
    static final class C01401 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Path $dir;
        final /* synthetic */ boolean $followSymlinks;
        final /* synthetic */ FileSystem $this_commonListRecursively;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C01401(Path path, FileSystem fileSystem, boolean z, Continuation<? super C01401> continuation) {
            super(2, continuation);
            this.$dir = path;
            this.$this_commonListRecursively = fileSystem;
            this.$followSymlinks = z;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01401 c01401 = new C01401(this.$dir, this.$this_commonListRecursively, this.$followSymlinks, continuation);
            c01401.L$0 = obj;
            return c01401;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
            return ((C01401) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            C01401 c01401;
            SequenceScope $this$sequence;
            ArrayDeque stack;
            Iterator<Path> it;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    c01401 = this;
                    SequenceScope $this$sequence2 = (SequenceScope) c01401.L$0;
                    ArrayDeque stack2 = new ArrayDeque();
                    stack2.addLast(c01401.$dir);
                    $this$sequence = $this$sequence2;
                    stack = stack2;
                    it = c01401.$this_commonListRecursively.list(c01401.$dir).iterator();
                    break;
                case 1:
                    c01401 = this;
                    it = (Iterator) c01401.L$2;
                    ArrayDeque stack3 = (ArrayDeque) c01401.L$1;
                    SequenceScope $this$sequence3 = (SequenceScope) c01401.L$0;
                    ResultKt.throwOnFailure($result);
                    stack = stack3;
                    $this$sequence = $this$sequence3;
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            while (it.hasNext()) {
                Path next = it.next();
                c01401.L$0 = $this$sequence;
                c01401.L$1 = stack;
                c01401.L$2 = it;
                c01401.label = 1;
                if (_FileSystemKt.collectRecursively($this$sequence, c01401.$this_commonListRecursively, stack, next, c01401.$followSymlinks, false, c01401) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            }
            return Unit.INSTANCE;
        }
    }

    public static final Sequence<Path> commonListRecursively(FileSystem $this$commonListRecursively, Path dir, boolean followSymlinks) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonListRecursively, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        return SequencesKt.sequence(new C01401(dir, $this$commonListRecursively, followSymlinks, null));
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x008f, code lost:
    
        if (r9.yield(r6, r1) == r0) goto L23;
     */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00a1  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0129  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0016  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Object collectRecursively(SequenceScope<? super Path> sequenceScope, FileSystem fileSystem, ArrayDeque<Path> arrayDeque, Path path, boolean z, boolean z2, Continuation<? super Unit> continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        SequenceScope $this$collectRecursively;
        ArrayDeque stack;
        boolean followSymlinks;
        FileSystem fileSystem2;
        Path path2;
        boolean postorder;
        SequenceScope $this$collectRecursively2;
        Path path3;
        ArrayDeque stack2;
        List children;
        ArrayDeque stack3;
        FileSystem fileSystem3;
        Iterator<Path> it;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(continuation);
            }
        }
        Object $result = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (anonymousClass1.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$collectRecursively = sequenceScope;
                stack = arrayDeque;
                followSymlinks = z;
                fileSystem2 = fileSystem;
                path2 = path;
                postorder = z2;
                if (!postorder) {
                    anonymousClass1.L$0 = $this$collectRecursively;
                    anonymousClass1.L$1 = fileSystem2;
                    anonymousClass1.L$2 = stack;
                    anonymousClass1.L$3 = path2;
                    anonymousClass1.Z$0 = followSymlinks;
                    anonymousClass1.Z$1 = postorder;
                    anonymousClass1.label = 1;
                    break;
                } else {
                    $this$collectRecursively2 = $this$collectRecursively;
                    path3 = path2;
                    stack2 = stack;
                    children = fileSystem2.listOrNull(path3);
                    if (children == null) {
                        children = CollectionsKt.emptyList();
                    }
                    if (!children.isEmpty()) {
                        Path symlinkPath = path3;
                        int symlinkCount = 0;
                        while (true) {
                            if (followSymlinks && stack2.contains(symlinkPath)) {
                                throw new IOException(Intrinsics.stringPlus("symlink cycle at ", path3));
                            }
                            Path pathSymlinkTarget = symlinkTarget(fileSystem2, symlinkPath);
                            if (pathSymlinkTarget != null) {
                                symlinkPath = pathSymlinkTarget;
                                symlinkCount++;
                            } else if (followSymlinks || symlinkCount == 0) {
                                stack2.addLast(symlinkPath);
                                try {
                                    Iterator<Path> it2 = children.iterator();
                                    fileSystem3 = fileSystem2;
                                    stack3 = stack2;
                                    it = it2;
                                    break;
                                } catch (Throwable th) {
                                    th = th;
                                    stack3 = stack2;
                                    stack3.removeLast();
                                    throw th;
                                }
                            }
                        }
                    }
                    if (postorder) {
                        anonymousClass1.L$0 = null;
                        anonymousClass1.L$1 = null;
                        anonymousClass1.L$2 = null;
                        anonymousClass1.L$3 = null;
                        anonymousClass1.L$4 = null;
                        anonymousClass1.label = 3;
                        if ($this$collectRecursively2.yield(path3, anonymousClass1) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                    }
                    return Unit.INSTANCE;
                }
            case 1:
                postorder = anonymousClass1.Z$1;
                followSymlinks = anonymousClass1.Z$0;
                path2 = (Path) anonymousClass1.L$3;
                stack = (ArrayDeque) anonymousClass1.L$2;
                fileSystem2 = (FileSystem) anonymousClass1.L$1;
                $this$collectRecursively = (SequenceScope) anonymousClass1.L$0;
                ResultKt.throwOnFailure($result);
                $this$collectRecursively2 = $this$collectRecursively;
                ArrayDeque arrayDeque2 = stack;
                path3 = path2;
                stack2 = arrayDeque2;
                children = fileSystem2.listOrNull(path3);
                if (children == null) {
                }
                if (!children.isEmpty()) {
                }
                if (postorder) {
                }
                return Unit.INSTANCE;
            case 2:
                postorder = anonymousClass1.Z$1;
                followSymlinks = anonymousClass1.Z$0;
                it = (Iterator) anonymousClass1.L$4;
                path3 = (Path) anonymousClass1.L$3;
                stack3 = (ArrayDeque) anonymousClass1.L$2;
                fileSystem3 = (FileSystem) anonymousClass1.L$1;
                $this$collectRecursively2 = (SequenceScope) anonymousClass1.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    while (it.hasNext()) {
                        Path next = it.next();
                        boolean z3 = followSymlinks;
                        boolean z4 = postorder;
                        anonymousClass1.L$0 = $this$collectRecursively2;
                        anonymousClass1.L$1 = fileSystem3;
                        anonymousClass1.L$2 = stack3;
                        anonymousClass1.L$3 = path3;
                        anonymousClass1.L$4 = it;
                        anonymousClass1.Z$0 = followSymlinks;
                        anonymousClass1.Z$1 = postorder;
                        anonymousClass1.label = 2;
                        if (collectRecursively($this$collectRecursively2, fileSystem3, stack3, next, z3, z4, anonymousClass1) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                    }
                    stack3.removeLast();
                    if (postorder) {
                    }
                    return Unit.INSTANCE;
                } catch (Throwable th2) {
                    th = th2;
                    stack3.removeLast();
                    throw th;
                }
            case 3:
                ResultKt.throwOnFailure($result);
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    public static final Path symlinkTarget(FileSystem $this$symlinkTarget, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$symlinkTarget, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        Path target = $this$symlinkTarget.metadata(path).getSymlinkTarget();
        if (target == null) {
            return null;
        }
        Path pathParent = path.parent();
        Intrinsics.checkNotNull(pathParent);
        return pathParent.resolve(target);
    }
}

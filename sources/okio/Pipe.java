package okio;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;

/* compiled from: Pipe.kt */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0006\u0010!\u001a\u00020\"J\u000e\u0010#\u001a\u00020\"2\u0006\u0010\u0017\u001a\u00020\u0010J\r\u0010\u0017\u001a\u00020\u0010H\u0007¢\u0006\u0002\b$J\r\u0010\u001b\u001a\u00020\u001cH\u0007¢\u0006\u0002\b%J&\u0010&\u001a\u00020\"*\u00020\u00102\u0017\u0010'\u001a\u0013\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\"0(¢\u0006\u0002\b)H\u0082\bR\u0014\u0010\u0005\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0017\u001a\u00020\u00108G¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u001a\u0010\u0018\u001a\u00020\nX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\f\"\u0004\b\u001a\u0010\u000eR\u0013\u0010\u001b\u001a\u00020\u001c8G¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001dR\u001a\u0010\u001e\u001a\u00020\nX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\f\"\u0004\b \u0010\u000e¨\u0006*"}, d2 = {"Lokio/Pipe;", HttpUrl.FRAGMENT_ENCODE_SET, "maxBufferSize", HttpUrl.FRAGMENT_ENCODE_SET, "(J)V", "buffer", "Lokio/Buffer;", "getBuffer$okio", "()Lokio/Buffer;", "canceled", HttpUrl.FRAGMENT_ENCODE_SET, "getCanceled$okio", "()Z", "setCanceled$okio", "(Z)V", "foldedSink", "Lokio/Sink;", "getFoldedSink$okio", "()Lokio/Sink;", "setFoldedSink$okio", "(Lokio/Sink;)V", "getMaxBufferSize$okio", "()J", "sink", "sinkClosed", "getSinkClosed$okio", "setSinkClosed$okio", "source", "Lokio/Source;", "()Lokio/Source;", "sourceClosed", "getSourceClosed$okio", "setSourceClosed$okio", "cancel", HttpUrl.FRAGMENT_ENCODE_SET, "fold", "-deprecated_sink", "-deprecated_source", "forward", "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "okio"}, k = 1, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class Pipe {
    private final Buffer buffer = new Buffer();
    private boolean canceled;
    private Sink foldedSink;
    private final long maxBufferSize;
    private final Sink sink;
    private boolean sinkClosed;
    private final Source source;
    private boolean sourceClosed;

    public Pipe(long maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        if (!(this.maxBufferSize >= 1)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("maxBufferSize < 1: ", Long.valueOf(getMaxBufferSize())).toString());
        }
        this.sink = new Sink() { // from class: okio.Pipe.sink.1
            private final Timeout timeout = new Timeout();

            /* JADX WARN: Code restructure failed: missing block: B:30:0x0087, code lost:
            
                r0 = kotlin.Unit.INSTANCE;
             */
            /* JADX WARN: Code restructure failed: missing block: B:32:0x008b, code lost:
            
                if (r3 != null) goto L34;
             */
            /* JADX WARN: Code restructure failed: missing block: B:34:0x0091, code lost:
            
                r4 = r18.this$0;
                r0 = r3;
                r9 = r0.getTimeout();
                r0 = r4.sink().getTimeout();
                r12 = r9.getTimeoutNanos();
                r9.timeout(okio.Timeout.INSTANCE.minTimeout(r0.getTimeoutNanos(), r9.getTimeoutNanos()), java.util.concurrent.TimeUnit.NANOSECONDS);
             */
            /* JADX WARN: Code restructure failed: missing block: B:35:0x00c3, code lost:
            
                if (r9.getHasDeadline() == false) goto L50;
             */
            /* JADX WARN: Code restructure failed: missing block: B:36:0x00c5, code lost:
            
                r3 = r9.deadlineNanoTime();
             */
            /* JADX WARN: Code restructure failed: missing block: B:37:0x00cd, code lost:
            
                if (r0.getHasDeadline() == false) goto L39;
             */
            /* JADX WARN: Code restructure failed: missing block: B:38:0x00cf, code lost:
            
                r9.deadlineNanoTime(java.lang.Math.min(r9.deadlineNanoTime(), r0.deadlineNanoTime()));
             */
            /* JADX WARN: Code restructure failed: missing block: B:40:0x00e2, code lost:
            
                r0.write(r19, r7);
                r0 = kotlin.Unit.INSTANCE;
             */
            /* JADX WARN: Code restructure failed: missing block: B:45:0x00f6, code lost:
            
                r0 = move-exception;
             */
            /* JADX WARN: Code restructure failed: missing block: B:46:0x00f7, code lost:
            
                r9.timeout(r12, java.util.concurrent.TimeUnit.NANOSECONDS);
             */
            /* JADX WARN: Code restructure failed: missing block: B:47:0x0100, code lost:
            
                if (r0.getHasDeadline() != false) goto L48;
             */
            /* JADX WARN: Code restructure failed: missing block: B:48:0x0102, code lost:
            
                r9.deadlineNanoTime(r3);
             */
            /* JADX WARN: Code restructure failed: missing block: B:49:0x0105, code lost:
            
                throw r0;
             */
            /* JADX WARN: Code restructure failed: missing block: B:51:0x010a, code lost:
            
                if (r0.getHasDeadline() == false) goto L53;
             */
            /* JADX WARN: Code restructure failed: missing block: B:52:0x010c, code lost:
            
                r9.deadlineNanoTime(r0.deadlineNanoTime());
             */
            /* JADX WARN: Code restructure failed: missing block: B:54:0x0117, code lost:
            
                r0.write(r19, r7);
                r0 = kotlin.Unit.INSTANCE;
             */
            /* JADX WARN: Code restructure failed: missing block: B:60:0x012c, code lost:
            
                return;
             */
            /* JADX WARN: Code restructure failed: missing block: B:61:0x012d, code lost:
            
                r0 = move-exception;
             */
            /* JADX WARN: Code restructure failed: missing block: B:62:0x012e, code lost:
            
                r9.timeout(r12, java.util.concurrent.TimeUnit.NANOSECONDS);
             */
            /* JADX WARN: Code restructure failed: missing block: B:63:0x0137, code lost:
            
                if (r0.getHasDeadline() != false) goto L64;
             */
            /* JADX WARN: Code restructure failed: missing block: B:64:0x0139, code lost:
            
                r9.clearDeadline();
             */
            /* JADX WARN: Code restructure failed: missing block: B:65:0x013c, code lost:
            
                throw r0;
             */
            /* JADX WARN: Code restructure failed: missing block: B:66:0x013d, code lost:
            
                r0 = th;
             */
            /* JADX WARN: Code restructure failed: missing block: B:92:?, code lost:
            
                return;
             */
            /* JADX WARN: Code restructure failed: missing block: B:93:?, code lost:
            
                return;
             */
            /* JADX WARN: Code restructure failed: missing block: B:94:?, code lost:
            
                return;
             */
            /* JADX WARN: Code restructure failed: missing block: B:95:?, code lost:
            
                return;
             */
            @Override // okio.Sink
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void write(Buffer source, long byteCount) throws Throwable {
                Intrinsics.checkNotNullParameter(source, "source");
                Sink sink = null;
                Object lock$iv = Pipe.this.getBuffer();
                Pipe pipe = Pipe.this;
                synchronized (lock$iv) {
                    try {
                    } catch (Throwable th) {
                        th = th;
                    }
                    if (!(!pipe.getSinkClosed())) {
                        throw new IllegalStateException("closed".toString());
                    }
                    if (pipe.getCanceled()) {
                        throw new IOException("canceled");
                    }
                    long byteCount2 = byteCount;
                    while (true) {
                        if (byteCount2 > 0) {
                            try {
                                Sink it = pipe.getFoldedSink();
                                if (it != null) {
                                    sink = it;
                                    break;
                                }
                                if (pipe.getSourceClosed()) {
                                    throw new IOException("source is closed");
                                }
                                long bufferSpaceAvailable = pipe.getMaxBufferSize() - pipe.getBuffer().size();
                                if (bufferSpaceAvailable == 0) {
                                    this.timeout.waitUntilNotified(pipe.getBuffer());
                                    if (pipe.getCanceled()) {
                                        throw new IOException("canceled");
                                    }
                                } else {
                                    long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount2);
                                    pipe.getBuffer().write(source, bytesToWrite);
                                    byteCount2 -= bytesToWrite;
                                    pipe.getBuffer().notifyAll();
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        throw th;
                    }
                }
            }

            @Override // okio.Sink, java.io.Flushable
            public void flush() {
                boolean hasDeadline;
                Sink sink = null;
                Object lock$iv = Pipe.this.getBuffer();
                Pipe pipe = Pipe.this;
                synchronized (lock$iv) {
                    if (!(!pipe.getSinkClosed())) {
                        throw new IllegalStateException("closed".toString());
                    }
                    if (pipe.getCanceled()) {
                        throw new IOException("canceled");
                    }
                    Sink it = pipe.getFoldedSink();
                    if (it != null) {
                        sink = it;
                    } else if (pipe.getSourceClosed() && pipe.getBuffer().size() > 0) {
                        throw new IOException("source is closed");
                    }
                    Unit unit = Unit.INSTANCE;
                }
                if (sink == null) {
                    return;
                }
                Pipe this_$iv = Pipe.this;
                Sink $this$forward$iv = sink;
                Timeout this_$iv$iv = $this$forward$iv.getTimeout();
                Timeout other$iv$iv = this_$iv.sink().getTimeout();
                long originalTimeout$iv$iv = this_$iv$iv.getTimeoutNanos();
                this_$iv$iv.timeout(Timeout.INSTANCE.minTimeout(other$iv$iv.getTimeoutNanos(), this_$iv$iv.getTimeoutNanos()), TimeUnit.NANOSECONDS);
                if (this_$iv$iv.getHasDeadline()) {
                    long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                    if (other$iv$iv.getHasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                    }
                    try {
                        $this$forward$iv.flush();
                        Unit unit2 = Unit.INSTANCE;
                        if (hasDeadline) {
                            return;
                        } else {
                            return;
                        }
                    } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.getHasDeadline()) {
                            this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                        }
                    }
                }
                if (other$iv$iv.getHasDeadline()) {
                    this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                }
                try {
                    $this$forward$iv.flush();
                    Unit unit3 = Unit.INSTANCE;
                } finally {
                    this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                    if (other$iv$iv.getHasDeadline()) {
                        this_$iv$iv.clearDeadline();
                    }
                }
            }

            @Override // okio.Sink, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
                boolean hasDeadline;
                Sink sink = null;
                Object lock$iv = Pipe.this.getBuffer();
                Pipe pipe = Pipe.this;
                synchronized (lock$iv) {
                    if (pipe.getSinkClosed()) {
                        return;
                    }
                    Sink it = pipe.getFoldedSink();
                    if (it != null) {
                        sink = it;
                    } else {
                        if (pipe.getSourceClosed() && pipe.getBuffer().size() > 0) {
                            throw new IOException("source is closed");
                        }
                        pipe.setSinkClosed$okio(true);
                        pipe.getBuffer().notifyAll();
                    }
                    Unit unit = Unit.INSTANCE;
                    if (sink == null) {
                        return;
                    }
                    Pipe this_$iv = Pipe.this;
                    Sink $this$forward$iv = sink;
                    Timeout this_$iv$iv = $this$forward$iv.getTimeout();
                    Timeout other$iv$iv = this_$iv.sink().getTimeout();
                    long originalTimeout$iv$iv = this_$iv$iv.getTimeoutNanos();
                    this_$iv$iv.timeout(Timeout.INSTANCE.minTimeout(other$iv$iv.getTimeoutNanos(), this_$iv$iv.getTimeoutNanos()), TimeUnit.NANOSECONDS);
                    if (this_$iv$iv.getHasDeadline()) {
                        long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                        if (other$iv$iv.getHasDeadline()) {
                            this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                        }
                        try {
                            $this$forward$iv.close();
                            Unit unit2 = Unit.INSTANCE;
                            if (hasDeadline) {
                                return;
                            } else {
                                return;
                            }
                        } finally {
                            this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                            if (other$iv$iv.getHasDeadline()) {
                                this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                            }
                        }
                    }
                    if (other$iv$iv.getHasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                    }
                    try {
                        $this$forward$iv.close();
                        Unit unit3 = Unit.INSTANCE;
                    } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.getHasDeadline()) {
                            this_$iv$iv.clearDeadline();
                        }
                    }
                }
            }

            @Override // okio.Sink
            /* renamed from: timeout, reason: from getter */
            public Timeout getTimeout() {
                return this.timeout;
            }
        };
        this.source = new Source() { // from class: okio.Pipe.source.1
            private final Timeout timeout = new Timeout();

            @Override // okio.Source
            public long read(Buffer sink, long byteCount) {
                Intrinsics.checkNotNullParameter(sink, "sink");
                Object lock$iv = Pipe.this.getBuffer();
                Pipe pipe = Pipe.this;
                synchronized (lock$iv) {
                    if (!(!pipe.getSourceClosed())) {
                        throw new IllegalStateException("closed".toString());
                    }
                    if (pipe.getCanceled()) {
                        throw new IOException("canceled");
                    }
                    while (pipe.getBuffer().size() == 0) {
                        if (pipe.getSinkClosed()) {
                            return -1L;
                        }
                        this.timeout.waitUntilNotified(pipe.getBuffer());
                        if (pipe.getCanceled()) {
                            throw new IOException("canceled");
                        }
                    }
                    long j = pipe.getBuffer().read(sink, byteCount);
                    pipe.getBuffer().notifyAll();
                    return j;
                }
            }

            @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
                Object lock$iv = Pipe.this.getBuffer();
                Pipe pipe = Pipe.this;
                synchronized (lock$iv) {
                    pipe.setSourceClosed$okio(true);
                    pipe.getBuffer().notifyAll();
                    Unit unit = Unit.INSTANCE;
                }
            }

            @Override // okio.Source
            /* renamed from: timeout, reason: from getter */
            public Timeout getTimeout() {
                return this.timeout;
            }
        };
    }

    /* renamed from: getMaxBufferSize$okio, reason: from getter */
    public final long getMaxBufferSize() {
        return this.maxBufferSize;
    }

    /* renamed from: getBuffer$okio, reason: from getter */
    public final Buffer getBuffer() {
        return this.buffer;
    }

    /* renamed from: getCanceled$okio, reason: from getter */
    public final boolean getCanceled() {
        return this.canceled;
    }

    public final void setCanceled$okio(boolean z) {
        this.canceled = z;
    }

    /* renamed from: getSinkClosed$okio, reason: from getter */
    public final boolean getSinkClosed() {
        return this.sinkClosed;
    }

    public final void setSinkClosed$okio(boolean z) {
        this.sinkClosed = z;
    }

    /* renamed from: getSourceClosed$okio, reason: from getter */
    public final boolean getSourceClosed() {
        return this.sourceClosed;
    }

    public final void setSourceClosed$okio(boolean z) {
        this.sourceClosed = z;
    }

    /* renamed from: getFoldedSink$okio, reason: from getter */
    public final Sink getFoldedSink() {
        return this.foldedSink;
    }

    public final void setFoldedSink$okio(Sink sink) {
        this.foldedSink = sink;
    }

    public final Sink sink() {
        return this.sink;
    }

    public final Source source() {
        return this.source;
    }

    public final void fold(Sink sink) throws IOException {
        boolean closed;
        Buffer buffer;
        Intrinsics.checkNotNullParameter(sink, "sink");
        while (true) {
            Object lock$iv = this.buffer;
            synchronized (lock$iv) {
                if (!(getFoldedSink() == null)) {
                    throw new IllegalStateException("sink already folded".toString());
                }
                if (getCanceled()) {
                    setFoldedSink$okio(sink);
                    throw new IOException("canceled");
                }
                if (getBuffer().exhausted()) {
                    setSourceClosed$okio(true);
                    setFoldedSink$okio(sink);
                    return;
                } else {
                    closed = getSinkClosed();
                    buffer = new Buffer();
                    buffer.write(getBuffer(), getBuffer().size());
                    getBuffer().notifyAll();
                    Unit unit = Unit.INSTANCE;
                }
            }
            try {
                sink.write(buffer, buffer.size());
                if (closed) {
                    sink.close();
                } else {
                    sink.flush();
                }
            } catch (Throwable th) {
                synchronized (this.buffer) {
                    setSourceClosed$okio(true);
                    getBuffer().notifyAll();
                    Unit unit2 = Unit.INSTANCE;
                    throw th;
                }
            }
        }
    }

    private final void forward(Sink $this$forward, Function1<? super Sink, Unit> function1) {
        Timeout this_$iv = $this$forward.getTimeout();
        Timeout other$iv = sink().getTimeout();
        long originalTimeout$iv = this_$iv.getTimeoutNanos();
        this_$iv.timeout(Timeout.INSTANCE.minTimeout(other$iv.getTimeoutNanos(), this_$iv.getTimeoutNanos()), TimeUnit.NANOSECONDS);
        if (this_$iv.getHasDeadline()) {
            long originalDeadline$iv = this_$iv.deadlineNanoTime();
            if (other$iv.getHasDeadline()) {
                this_$iv.deadlineNanoTime(Math.min(this_$iv.deadlineNanoTime(), other$iv.deadlineNanoTime()));
            }
            try {
                function1.invoke($this$forward);
                Unit unit = Unit.INSTANCE;
                return;
            } finally {
                InlineMarker.finallyStart(1);
                this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                if (other$iv.getHasDeadline()) {
                    this_$iv.deadlineNanoTime(originalDeadline$iv);
                }
                InlineMarker.finallyEnd(1);
            }
        }
        if (other$iv.getHasDeadline()) {
            this_$iv.deadlineNanoTime(other$iv.deadlineNanoTime());
        }
        try {
            function1.invoke($this$forward);
            Unit unit2 = Unit.INSTANCE;
        } finally {
            InlineMarker.finallyStart(1);
            this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
            if (other$iv.getHasDeadline()) {
                this_$iv.clearDeadline();
            }
            InlineMarker.finallyEnd(1);
        }
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "sink", imports = {}))
    /* renamed from: -deprecated_sink, reason: not valid java name and from getter */
    public final Sink getSink() {
        return this.sink;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to val", replaceWith = @ReplaceWith(expression = "source", imports = {}))
    /* renamed from: -deprecated_source, reason: not valid java name and from getter */
    public final Source getSource() {
        return this.source;
    }

    public final void cancel() {
        Object lock$iv = this.buffer;
        synchronized (lock$iv) {
            setCanceled$okio(true);
            getBuffer().clear();
            getBuffer().notifyAll();
            Unit unit = Unit.INSTANCE;
        }
    }
}

package com.bumptech.glide.load.data;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class BufferedOutputStream extends OutputStream {
    private ArrayPool arrayPool;
    private byte[] buffer;
    private int index;
    private final OutputStream out;

    public BufferedOutputStream(OutputStream out, ArrayPool arrayPool) {
        this(out, arrayPool, 65536);
    }

    BufferedOutputStream(OutputStream out, ArrayPool arrayPool, int bufferSize) {
        this.out = out;
        this.arrayPool = arrayPool;
        this.buffer = (byte[]) arrayPool.get(bufferSize, byte[].class);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        byte[] bArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        bArr[i] = (byte) b;
        maybeFlushBuffer();
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int initialOffset, int length) throws IOException {
        int writtenSoFar = 0;
        do {
            int remainingToWrite = length - writtenSoFar;
            int currentOffset = initialOffset + writtenSoFar;
            if (this.index == 0 && remainingToWrite >= this.buffer.length) {
                this.out.write(b, currentOffset, remainingToWrite);
                return;
            }
            int remainingSpaceInBuffer = this.buffer.length - this.index;
            int totalBytesToWriteToBuffer = Math.min(remainingToWrite, remainingSpaceInBuffer);
            System.arraycopy(b, currentOffset, this.buffer, this.index, totalBytesToWriteToBuffer);
            this.index += totalBytesToWriteToBuffer;
            writtenSoFar += totalBytesToWriteToBuffer;
            maybeFlushBuffer();
        } while (writtenSoFar < length);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        flushBuffer();
        this.out.flush();
    }

    private void flushBuffer() throws IOException {
        if (this.index > 0) {
            this.out.write(this.buffer, 0, this.index);
            this.index = 0;
        }
    }

    private void maybeFlushBuffer() throws IOException {
        if (this.index == this.buffer.length) {
            flushBuffer();
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            flush();
            this.out.close();
            release();
        } catch (Throwable th) {
            this.out.close();
            throw th;
        }
    }

    private void release() {
        if (this.buffer != null) {
            this.arrayPool.put(this.buffer);
            this.buffer = null;
        }
    }
}

package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

/* loaded from: classes.dex */
public class RecyclableBufferedInputStream extends FilterInputStream {
    private volatile byte[] buf;
    private final ArrayPool byteArrayPool;
    private int count;
    private int marklimit;
    private int markpos;
    private int pos;

    public RecyclableBufferedInputStream(InputStream in, ArrayPool byteArrayPool) {
        this(in, byteArrayPool, 65536);
    }

    RecyclableBufferedInputStream(InputStream in, ArrayPool byteArrayPool, int bufferSize) {
        super(in);
        this.markpos = -1;
        this.byteArrayPool = byteArrayPool;
        this.buf = (byte[]) byteArrayPool.get(bufferSize, byte[].class);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf == null || localIn == null) {
            throw streamClosed();
        }
        return (this.count - this.pos) + localIn.available();
    }

    private static IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public synchronized void fixMarkLimit() {
        this.marklimit = this.buf.length;
    }

    public synchronized void release() {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        if (this.markpos == -1 || this.pos - this.markpos >= this.marklimit) {
            int result = localIn.read(localBuf);
            if (result > 0) {
                this.markpos = -1;
                this.pos = 0;
                this.count = result;
            }
            return result;
        }
        if (this.markpos == 0 && this.marklimit > localBuf.length && this.count == localBuf.length) {
            int newLength = localBuf.length * 2;
            if (newLength > this.marklimit) {
                newLength = this.marklimit;
            }
            byte[] newbuf = (byte[]) this.byteArrayPool.get(newLength, byte[].class);
            System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
            this.buf = newbuf;
            localBuf = newbuf;
            this.byteArrayPool.put(localBuf);
        } else if (this.markpos > 0) {
            System.arraycopy(localBuf, this.markpos, localBuf, 0, localBuf.length - this.markpos);
        }
        this.pos -= this.markpos;
        this.markpos = 0;
        this.count = 0;
        int bytesread = localIn.read(localBuf, this.pos, localBuf.length - this.pos);
        int i = this.pos;
        if (bytesread > 0) {
            i += bytesread;
        }
        this.count = i;
        return bytesread;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void mark(int readlimit) {
        this.marklimit = Math.max(this.marklimit, readlimit);
        this.markpos = this.pos;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int read() throws IOException {
        byte[] localBuf = this.buf;
        InputStream localIn = this.in;
        if (localBuf == null || localIn == null) {
            throw streamClosed();
        }
        if (this.pos >= this.count && fillbuf(localIn, localBuf) == -1) {
            return -1;
        }
        if (localBuf != this.buf && (localBuf = this.buf) == null) {
            throw streamClosed();
        }
        if (this.count - this.pos <= 0) {
            return -1;
        }
        int i = this.pos;
        this.pos = i + 1;
        return localBuf[i] & UByte.MAX_VALUE;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int read(byte[] buffer, int offset, int byteCount) throws IOException {
        int copylength;
        int read;
        byte[] localBuf = this.buf;
        if (localBuf == null) {
            throw streamClosed();
        }
        if (byteCount == 0) {
            return 0;
        }
        InputStream localIn = this.in;
        if (localIn == null) {
            throw streamClosed();
        }
        if (this.pos < this.count) {
            int copylength2 = this.count - this.pos >= byteCount ? byteCount : this.count - this.pos;
            System.arraycopy(localBuf, this.pos, buffer, offset, copylength2);
            this.pos += copylength2;
            if (copylength2 == byteCount || localIn.available() == 0) {
                return copylength2;
            }
            offset += copylength2;
            copylength = byteCount - copylength2;
        } else {
            copylength = byteCount;
        }
        while (true) {
            if (this.markpos == -1 && copylength >= localBuf.length) {
                read = localIn.read(buffer, offset, copylength);
                if (read == -1) {
                    return copylength != byteCount ? byteCount - copylength : -1;
                }
            } else {
                if (fillbuf(localIn, localBuf) == -1) {
                    return copylength != byteCount ? byteCount - copylength : -1;
                }
                if (localBuf != this.buf && (localBuf = this.buf) == null) {
                    throw streamClosed();
                }
                read = this.count - this.pos >= copylength ? copylength : this.count - this.pos;
                System.arraycopy(localBuf, this.pos, buffer, offset, read);
                this.pos += read;
            }
            copylength -= read;
            if (copylength == 0) {
                return byteCount;
            }
            if (localIn.available() == 0) {
                return byteCount - copylength;
            }
            offset += read;
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        }
        if (-1 == this.markpos) {
            throw new InvalidMarkException("Mark has been invalidated, pos: " + this.pos + " markLimit: " + this.marklimit);
        }
        this.pos = this.markpos;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized long skip(long byteCount) throws IOException {
        if (byteCount < 1) {
            return 0L;
        }
        byte[] localBuf = this.buf;
        if (localBuf == null) {
            throw streamClosed();
        }
        InputStream localIn = this.in;
        if (localIn == null) {
            throw streamClosed();
        }
        if (this.count - this.pos >= byteCount) {
            this.pos = (int) (this.pos + byteCount);
            return byteCount;
        }
        long read = this.count - this.pos;
        this.pos = this.count;
        if (this.markpos != -1 && byteCount <= this.marklimit) {
            if (fillbuf(localIn, localBuf) == -1) {
                return read;
            }
            if (this.count - this.pos >= byteCount - read) {
                this.pos = (int) ((this.pos + byteCount) - read);
                return byteCount;
            }
            long read2 = (this.count + read) - this.pos;
            this.pos = this.count;
            return read2;
        }
        long skipped = localIn.skip(byteCount - read);
        if (skipped > 0) {
            this.markpos = -1;
        }
        return read + skipped;
    }

    static class InvalidMarkException extends IOException {
        private static final long serialVersionUID = -4338378848813561757L;

        InvalidMarkException(String detailMessage) {
            super(detailMessage);
        }
    }
}

package com.bumptech.glide.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.UByte;

/* loaded from: classes.dex */
public final class ByteBufferUtil {
    private static final AtomicReference<byte[]> BUFFER_REF = new AtomicReference<>();
    private static final int BUFFER_SIZE = 16384;

    private ByteBufferUtil() {
    }

    public static ByteBuffer fromFile(File file) throws IOException {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            long fileLength = file.length();
            if (fileLength > 2147483647L) {
                throw new IOException("File too large to map into memory");
            }
            if (fileLength == 0) {
                throw new IOException("File unsuitable for memory mapping");
            }
            RandomAccessFile raf2 = new RandomAccessFile(file, "r");
            FileChannel channel2 = raf2.getChannel();
            MappedByteBuffer mappedByteBufferLoad = channel2.map(FileChannel.MapMode.READ_ONLY, 0L, fileLength).load();
            if (channel2 != null) {
                try {
                    channel2.close();
                } catch (IOException e) {
                }
            }
            try {
                raf2.close();
            } catch (IOException e2) {
            }
            return mappedByteBufferLoad;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    channel.close();
                } catch (IOException e3) {
                }
            }
            if (0 != 0) {
                try {
                    raf.close();
                    throw th;
                } catch (IOException e4) {
                    throw th;
                }
            }
            throw th;
        }
    }

    public static void toFile(ByteBuffer buffer, File file) throws IOException {
        rewind(buffer);
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            channel.write(buffer);
            channel.force(false);
            channel.close();
            raf.close();
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                }
            }
            try {
                raf.close();
            } catch (IOException e2) {
            }
        } finally {
        }
    }

    public static void toStream(ByteBuffer byteBuffer, OutputStream os) throws IOException {
        SafeArray safeArray = getSafeArray(byteBuffer);
        if (safeArray != null) {
            os.write(safeArray.data, safeArray.offset, safeArray.offset + safeArray.limit);
            return;
        }
        byte[] buffer = BUFFER_REF.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[16384];
        }
        while (byteBuffer.remaining() > 0) {
            int toRead = Math.min(byteBuffer.remaining(), buffer.length);
            byteBuffer.get(buffer, 0, toRead);
            os.write(buffer, 0, toRead);
        }
        BUFFER_REF.set(buffer);
    }

    public static byte[] toBytes(ByteBuffer byteBuffer) {
        SafeArray safeArray = getSafeArray(byteBuffer);
        if (safeArray != null && safeArray.offset == 0 && safeArray.limit == safeArray.data.length) {
            return byteBuffer.array();
        }
        ByteBuffer toCopy = byteBuffer.asReadOnlyBuffer();
        byte[] result = new byte[toCopy.limit()];
        rewind(toCopy);
        toCopy.get(result);
        return result;
    }

    public static InputStream toStream(ByteBuffer buffer) {
        return new ByteBufferStream(buffer);
    }

    public static ByteBuffer fromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(16384);
        byte[] buffer = BUFFER_REF.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[16384];
        }
        while (true) {
            int n = stream.read(buffer);
            if (n >= 0) {
                outStream.write(buffer, 0, n);
            } else {
                BUFFER_REF.set(buffer);
                byte[] bytes = outStream.toByteArray();
                return rewind(ByteBuffer.allocateDirect(bytes.length).put(bytes));
            }
        }
    }

    public static ByteBuffer rewind(ByteBuffer buffer) {
        return (ByteBuffer) buffer.position(0);
    }

    private static SafeArray getSafeArray(ByteBuffer byteBuffer) {
        if (!byteBuffer.isReadOnly() && byteBuffer.hasArray()) {
            return new SafeArray(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit());
        }
        return null;
    }

    static final class SafeArray {
        final byte[] data;
        final int limit;
        final int offset;

        SafeArray(byte[] data, int offset, int limit) {
            this.data = data;
            this.offset = offset;
            this.limit = limit;
        }
    }

    private static class ByteBufferStream extends InputStream {
        private static final int UNSET = -1;
        private final ByteBuffer byteBuffer;
        private int markPos = -1;

        ByteBufferStream(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        @Override // java.io.InputStream
        public int available() {
            return this.byteBuffer.remaining();
        }

        @Override // java.io.InputStream
        public int read() {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            return this.byteBuffer.get() & UByte.MAX_VALUE;
        }

        @Override // java.io.InputStream
        public synchronized void mark(int readLimit) {
            this.markPos = this.byteBuffer.position();
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int byteOffset, int byteCount) {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            int toRead = Math.min(byteCount, available());
            this.byteBuffer.get(buffer, byteOffset, toRead);
            return toRead;
        }

        @Override // java.io.InputStream
        public synchronized void reset() throws IOException {
            if (this.markPos == -1) {
                throw new IOException("Cannot reset to unset mark position");
            }
            this.byteBuffer.position(this.markPos);
        }

        @Override // java.io.InputStream
        public long skip(long byteCount) {
            if (!this.byteBuffer.hasRemaining()) {
                return -1L;
            }
            long toSkip = Math.min(byteCount, available());
            this.byteBuffer.position((int) (this.byteBuffer.position() + toSkip));
            return toSkip;
        }
    }
}

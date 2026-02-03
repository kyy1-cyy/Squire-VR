package com.bumptech.glide.load.data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

/* loaded from: classes.dex */
public final class ExifOrientationStream extends FilterInputStream {
    private static final int SEGMENT_START_POSITION = 2;
    private final byte orientation;
    private int position;
    private static final byte[] EXIF_SEGMENT = {-1, -31, 0, 28, 69, 120, 105, 102, 0, 0, 77, 77, 0, 0, 0, 0, 0, 8, 0, 1, 1, 18, 0, 2, 0, 0, 0, 1, 0};
    private static final int SEGMENT_LENGTH = EXIF_SEGMENT.length;
    private static final int ORIENTATION_POSITION = SEGMENT_LENGTH + 2;

    public ExifOrientationStream(InputStream in, int orientation) {
        super(in);
        if (orientation < -1 || orientation > 8) {
            throw new IllegalArgumentException("Cannot add invalid orientation: " + orientation);
        }
        this.orientation = (byte) orientation;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int readLimit) {
        throw new UnsupportedOperationException();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int result;
        if (this.position < 2 || this.position > ORIENTATION_POSITION) {
            result = super.read();
        } else if (this.position == ORIENTATION_POSITION) {
            result = this.orientation;
        } else {
            result = EXIF_SEGMENT[this.position - 2] & UByte.MAX_VALUE;
        }
        if (result != -1) {
            this.position++;
        }
        return result;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int read;
        if (this.position > ORIENTATION_POSITION) {
            read = super.read(buffer, byteOffset, byteCount);
        } else {
            int read2 = this.position;
            if (read2 == ORIENTATION_POSITION) {
                buffer[byteOffset] = this.orientation;
                read = 1;
            } else {
                int read3 = this.position;
                if (read3 < 2) {
                    read = super.read(buffer, byteOffset, 2 - this.position);
                } else {
                    int read4 = ORIENTATION_POSITION;
                    read = Math.min(read4 - this.position, byteCount);
                    System.arraycopy(EXIF_SEGMENT, this.position - 2, buffer, byteOffset, read);
                }
            }
        }
        if (read > 0) {
            this.position += read;
        }
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        long skipped = super.skip(byteCount);
        if (skipped > 0) {
            this.position = (int) (this.position + skipped);
        }
        return skipped;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
}

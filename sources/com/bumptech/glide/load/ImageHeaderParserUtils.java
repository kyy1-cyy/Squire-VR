package com.bumptech.glide.load;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.data.ParcelFileDescriptorRewinder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: classes.dex */
public final class ImageHeaderParserUtils {
    private static final int MARK_READ_LIMIT = 5242880;

    private interface OrientationReader {
        int getOrientationAndRewind(ImageHeaderParser imageHeaderParser) throws IOException;
    }

    private interface TypeReader {
        ImageHeaderParser.ImageType getTypeAndRewind(ImageHeaderParser imageHeaderParser) throws IOException;
    }

    private ImageHeaderParserUtils() {
    }

    public static ImageHeaderParser.ImageType getType(List<ImageHeaderParser> parsers, InputStream is, ArrayPool byteArrayPool) throws IOException {
        if (is == null) {
            return ImageHeaderParser.ImageType.UNKNOWN;
        }
        if (!is.markSupported()) {
            is = new RecyclableBufferedInputStream(is, byteArrayPool);
        }
        is.mark(MARK_READ_LIMIT);
        final InputStream finalIs = is;
        return getTypeInternal(parsers, new TypeReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.1
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.TypeReader
            public ImageHeaderParser.ImageType getTypeAndRewind(ImageHeaderParser parser) throws IOException {
                try {
                    return parser.getType(finalIs);
                } finally {
                    finalIs.reset();
                }
            }
        });
    }

    public static ImageHeaderParser.ImageType getType(List<ImageHeaderParser> parsers, final ByteBuffer buffer) throws IOException {
        if (buffer == null) {
            return ImageHeaderParser.ImageType.UNKNOWN;
        }
        return getTypeInternal(parsers, new TypeReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.2
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.TypeReader
            public ImageHeaderParser.ImageType getTypeAndRewind(ImageHeaderParser parser) throws IOException {
                try {
                    return parser.getType(buffer);
                } finally {
                    ByteBufferUtil.rewind(buffer);
                }
            }
        });
    }

    public static ImageHeaderParser.ImageType getType(List<ImageHeaderParser> parsers, final ParcelFileDescriptorRewinder parcelFileDescriptorRewinder, final ArrayPool byteArrayPool) throws IOException {
        return getTypeInternal(parsers, new TypeReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.3
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.TypeReader
            public ImageHeaderParser.ImageType getTypeAndRewind(ImageHeaderParser parser) throws IOException {
                RecyclableBufferedInputStream is = null;
                try {
                    is = new RecyclableBufferedInputStream(new FileInputStream(parcelFileDescriptorRewinder.rewindAndGet().getFileDescriptor()), byteArrayPool);
                    ImageHeaderParser.ImageType type = parser.getType(is);
                    is.release();
                    parcelFileDescriptorRewinder.rewindAndGet();
                    return type;
                } catch (Throwable th) {
                    if (is != null) {
                        is.release();
                    }
                    parcelFileDescriptorRewinder.rewindAndGet();
                    throw th;
                }
            }
        });
    }

    private static ImageHeaderParser.ImageType getTypeInternal(List<ImageHeaderParser> parsers, TypeReader reader) throws IOException {
        int size = parsers.size();
        for (int i = 0; i < size; i++) {
            ImageHeaderParser parser = parsers.get(i);
            ImageHeaderParser.ImageType type = reader.getTypeAndRewind(parser);
            if (type != ImageHeaderParser.ImageType.UNKNOWN) {
                return type;
            }
        }
        return ImageHeaderParser.ImageType.UNKNOWN;
    }

    public static int getOrientation(List<ImageHeaderParser> parsers, final ByteBuffer buffer, final ArrayPool arrayPool) throws IOException {
        if (buffer == null) {
            return -1;
        }
        return getOrientationInternal(parsers, new OrientationReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.4
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.OrientationReader
            public int getOrientationAndRewind(ImageHeaderParser parser) throws IOException {
                try {
                    return parser.getOrientation(buffer, arrayPool);
                } finally {
                    ByteBufferUtil.rewind(buffer);
                }
            }
        });
    }

    public static int getOrientation(List<ImageHeaderParser> parsers, InputStream is, final ArrayPool byteArrayPool) throws IOException {
        if (is == null) {
            return -1;
        }
        if (!is.markSupported()) {
            is = new RecyclableBufferedInputStream(is, byteArrayPool);
        }
        is.mark(MARK_READ_LIMIT);
        final InputStream finalIs = is;
        return getOrientationInternal(parsers, new OrientationReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.5
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.OrientationReader
            public int getOrientationAndRewind(ImageHeaderParser parser) throws IOException {
                try {
                    return parser.getOrientation(finalIs, byteArrayPool);
                } finally {
                    finalIs.reset();
                }
            }
        });
    }

    public static int getOrientation(List<ImageHeaderParser> parsers, final ParcelFileDescriptorRewinder parcelFileDescriptorRewinder, final ArrayPool byteArrayPool) throws IOException {
        return getOrientationInternal(parsers, new OrientationReader() { // from class: com.bumptech.glide.load.ImageHeaderParserUtils.6
            @Override // com.bumptech.glide.load.ImageHeaderParserUtils.OrientationReader
            public int getOrientationAndRewind(ImageHeaderParser parser) throws IOException {
                RecyclableBufferedInputStream is = null;
                try {
                    is = new RecyclableBufferedInputStream(new FileInputStream(parcelFileDescriptorRewinder.rewindAndGet().getFileDescriptor()), byteArrayPool);
                    int orientation = parser.getOrientation(is, byteArrayPool);
                    is.release();
                    parcelFileDescriptorRewinder.rewindAndGet();
                    return orientation;
                } catch (Throwable th) {
                    if (is != null) {
                        is.release();
                    }
                    parcelFileDescriptorRewinder.rewindAndGet();
                    throw th;
                }
            }
        });
    }

    private static int getOrientationInternal(List<ImageHeaderParser> parsers, OrientationReader reader) throws IOException {
        int size = parsers.size();
        for (int i = 0; i < size; i++) {
            ImageHeaderParser parser = parsers.get(i);
            int orientation = reader.getOrientationAndRewind(parser);
            if (orientation != -1) {
                return orientation;
            }
        }
        return -1;
    }
}

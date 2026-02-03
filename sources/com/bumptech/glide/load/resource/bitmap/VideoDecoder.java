package com.bumptech.glide.load.resource.bitmap;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaDataSource;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class VideoDecoder<T> implements ResourceDecoder<T, Bitmap> {
    public static final long DEFAULT_FRAME = -1;
    static final int DEFAULT_FRAME_OPTION = 2;
    private static final String TAG = "VideoDecoder";
    private static final String WEBM_MIME_TYPE = "video/webm";
    private final BitmapPool bitmapPool;
    private final MediaMetadataRetrieverFactory factory;
    private final MediaInitializer<T> initializer;
    public static final Option<Long> TARGET_FRAME = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.TargetFrame", -1L, new Option.CacheKeyUpdater<Long>() { // from class: com.bumptech.glide.load.resource.bitmap.VideoDecoder.1
        private final ByteBuffer buffer = ByteBuffer.allocate(8);

        @Override // com.bumptech.glide.load.Option.CacheKeyUpdater
        public void update(byte[] keyBytes, Long value, MessageDigest messageDigest) {
            messageDigest.update(keyBytes);
            synchronized (this.buffer) {
                this.buffer.position(0);
                messageDigest.update(this.buffer.putLong(value.longValue()).array());
            }
        }
    });
    public static final Option<Integer> FRAME_OPTION = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.FrameOption", 2, new Option.CacheKeyUpdater<Integer>() { // from class: com.bumptech.glide.load.resource.bitmap.VideoDecoder.2
        private final ByteBuffer buffer = ByteBuffer.allocate(4);

        @Override // com.bumptech.glide.load.Option.CacheKeyUpdater
        public void update(byte[] keyBytes, Integer value, MessageDigest messageDigest) {
            if (value == null) {
                return;
            }
            messageDigest.update(keyBytes);
            synchronized (this.buffer) {
                this.buffer.position(0);
                messageDigest.update(this.buffer.putInt(value.intValue()).array());
            }
        }
    });
    private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
    private static final List<String> PIXEL_T_BUILD_ID_PREFIXES_REQUIRING_HDR_180_ROTATION_FIX = Collections.unmodifiableList(Arrays.asList("TP1A", "TD1A.220804.031"));

    interface MediaInitializer<T> {
        void initializeExtractor(MediaExtractor mediaExtractor, T t) throws IOException;

        void initializeRetriever(MediaMetadataRetriever mediaMetadataRetriever, T t);
    }

    public static ResourceDecoder<AssetFileDescriptor, Bitmap> asset(BitmapPool bitmapPool) {
        return new VideoDecoder(bitmapPool, new AssetFileDescriptorInitializer());
    }

    public static ResourceDecoder<ParcelFileDescriptor, Bitmap> parcel(BitmapPool bitmapPool) {
        return new VideoDecoder(bitmapPool, new ParcelFileDescriptorInitializer());
    }

    public static ResourceDecoder<ByteBuffer, Bitmap> byteBuffer(BitmapPool bitmapPool) {
        return new VideoDecoder(bitmapPool, new ByteBufferInitializer());
    }

    VideoDecoder(BitmapPool bitmapPool, MediaInitializer<T> initializer) {
        this(bitmapPool, initializer, DEFAULT_FACTORY);
    }

    VideoDecoder(BitmapPool bitmapPool, MediaInitializer<T> initializer, MediaMetadataRetrieverFactory factory) {
        this.bitmapPool = bitmapPool;
        this.initializer = initializer;
        this.factory = factory;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(T data, Options options) {
        return true;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Bitmap> decode(T resource, int outWidth, int outHeight, Options options) throws Throwable {
        Integer frameOption;
        DownsampleStrategy downsampleStrategy;
        int i;
        MediaMetadataRetriever mediaMetadataRetriever;
        long frameTimeMicros = ((Long) options.get(TARGET_FRAME)).longValue();
        if (frameTimeMicros < 0 && frameTimeMicros != -1) {
            throw new IllegalArgumentException("Requested frame must be non-negative, or DEFAULT_FRAME, given: " + frameTimeMicros);
        }
        Integer frameOption2 = (Integer) options.get(FRAME_OPTION);
        if (frameOption2 != null) {
            frameOption = frameOption2;
        } else {
            frameOption = 2;
        }
        DownsampleStrategy downsampleStrategy2 = (DownsampleStrategy) options.get(DownsampleStrategy.OPTION);
        if (downsampleStrategy2 != null) {
            downsampleStrategy = downsampleStrategy2;
        } else {
            downsampleStrategy = DownsampleStrategy.DEFAULT;
        }
        MediaMetadataRetriever mediaMetadataRetriever2 = this.factory.build();
        try {
            this.initializer.initializeRetriever(mediaMetadataRetriever2, resource);
            i = 29;
            mediaMetadataRetriever = mediaMetadataRetriever2;
            try {
                Bitmap result = decodeFrame(resource, mediaMetadataRetriever2, frameTimeMicros, frameOption.intValue(), outWidth, outHeight, downsampleStrategy);
                if (Build.VERSION.SDK_INT >= 29) {
                    mediaMetadataRetriever.release();
                } else {
                    mediaMetadataRetriever.release();
                }
                return BitmapResource.obtain(result, this.bitmapPool);
            } catch (Throwable th) {
                th = th;
                if (Build.VERSION.SDK_INT >= i) {
                    mediaMetadataRetriever.release();
                } else {
                    mediaMetadataRetriever.release();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            i = 29;
            mediaMetadataRetriever = mediaMetadataRetriever2;
        }
    }

    private Bitmap decodeFrame(T resource, MediaMetadataRetriever mediaMetadataRetriever, long frameTimeMicros, int frameOption, int outWidth, int outHeight, DownsampleStrategy strategy) throws NumberFormatException {
        if (isUnsupportedFormat(resource, mediaMetadataRetriever)) {
            throw new IllegalStateException("Cannot decode VP8 video on CrOS.");
        }
        Bitmap result = null;
        if (Build.VERSION.SDK_INT >= 27 && outWidth != Integer.MIN_VALUE && outHeight != Integer.MIN_VALUE && strategy != DownsampleStrategy.NONE) {
            result = decodeScaledFrame(mediaMetadataRetriever, frameTimeMicros, frameOption, outWidth, outHeight, strategy);
        }
        if (result == null) {
            result = decodeOriginalFrame(mediaMetadataRetriever, frameTimeMicros, frameOption);
        }
        Bitmap result2 = correctHdr180DegVideoFrameOrientation(mediaMetadataRetriever, result);
        if (result2 == null) {
            throw new VideoDecoderException();
        }
        return result2;
    }

    private static Bitmap correctHdr180DegVideoFrameOrientation(MediaMetadataRetriever mediaMetadataRetriever, Bitmap frame) throws NumberFormatException {
        if (!isHdr180RotationFixRequired()) {
            return frame;
        }
        boolean requiresHdr180RotationFix = false;
        try {
            if (isHDR(mediaMetadataRetriever)) {
                String rotationString = mediaMetadataRetriever.extractMetadata(24);
                int rotation = Integer.parseInt(rotationString);
                requiresHdr180RotationFix = Math.abs(rotation) == 180;
            }
        } catch (NumberFormatException e) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Exception trying to extract HDR transfer function or rotation");
            }
        }
        if (!requiresHdr180RotationFix) {
            return frame;
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Applying HDR 180 deg thumbnail correction");
        }
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(180.0f, frame.getWidth() / 2.0f, frame.getHeight() / 2.0f);
        return Bitmap.createBitmap(frame, 0, 0, frame.getWidth(), frame.getHeight(), rotationMatrix, true);
    }

    private static boolean isHDR(MediaMetadataRetriever mediaMetadataRetriever) throws NumberFormatException {
        String colorTransferString = mediaMetadataRetriever.extractMetadata(36);
        String colorStandardString = mediaMetadataRetriever.extractMetadata(35);
        int colorTransfer = Integer.parseInt(colorTransferString);
        int colorStandard = Integer.parseInt(colorStandardString);
        return (colorTransfer == 7 || colorTransfer == 6) && colorStandard == 6;
    }

    static boolean isHdr180RotationFixRequired() {
        if (Build.MODEL.startsWith("Pixel") && Build.VERSION.SDK_INT == 33) {
            return isTBuildRequiringRotationFix();
        }
        return Build.VERSION.SDK_INT >= 30 && Build.VERSION.SDK_INT < 33;
    }

    private static boolean isTBuildRequiringRotationFix() {
        for (String buildId : PIXEL_T_BUILD_ID_PREFIXES_REQUIRING_HDR_180_ROTATION_FIX) {
            if (Build.ID.startsWith(buildId)) {
                return true;
            }
        }
        return false;
    }

    private static Bitmap decodeScaledFrame(MediaMetadataRetriever mediaMetadataRetriever, long frameTimeMicros, int frameOption, int outWidth, int outHeight, DownsampleStrategy strategy) {
        int originalHeight;
        try {
            int originalWidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(18));
            int originalHeight2 = Integer.parseInt(mediaMetadataRetriever.extractMetadata(19));
            int orientation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(24));
            if (orientation == 90 || orientation == 270) {
                originalWidth = originalHeight2;
                originalHeight = originalWidth;
            } else {
                originalHeight = originalHeight2;
            }
            try {
                float scaleFactor = strategy.getScaleFactor(originalWidth, originalHeight, outWidth, outHeight);
                int decodeWidth = Math.round(originalWidth * scaleFactor);
                int decodeHeight = Math.round(originalHeight * scaleFactor);
                return mediaMetadataRetriever.getScaledFrameAtTime(frameTimeMicros, frameOption, decodeWidth, decodeHeight);
            } catch (Throwable th) {
                t = th;
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Exception trying to decode a scaled frame on oreo+, falling back to a fullsize frame", t);
                    return null;
                }
                return null;
            }
        } catch (Throwable th2) {
            t = th2;
        }
    }

    private static Bitmap decodeOriginalFrame(MediaMetadataRetriever mediaMetadataRetriever, long frameTimeMicros, int frameOption) {
        return mediaMetadataRetriever.getFrameAtTime(frameTimeMicros, frameOption);
    }

    private boolean isUnsupportedFormat(T resource, MediaMetadataRetriever mediaMetadataRetriever) {
        String mimeType;
        boolean isArc = Build.DEVICE != null && Build.DEVICE.matches(".+_cheets|cheets_.+");
        if (!isArc) {
            return false;
        }
        MediaExtractor mediaExtractor = null;
        try {
            mimeType = mediaMetadataRetriever.extractMetadata(12);
        } catch (Throwable t) {
            try {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Exception trying to extract track info for a webm video on CrOS.", t);
                }
                if (mediaExtractor != null) {
                }
            } finally {
                if (mediaExtractor != null) {
                    mediaExtractor.release();
                }
            }
        }
        if (!WEBM_MIME_TYPE.equals(mimeType)) {
            return false;
        }
        mediaExtractor = new MediaExtractor();
        this.initializer.initializeExtractor(mediaExtractor, resource);
        int numTracks = mediaExtractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat mediaformat = mediaExtractor.getTrackFormat(i);
            String trackMimeType = mediaformat.getString("mime");
            if ("video/x-vnd.on2.vp8".equals(trackMimeType)) {
                mediaExtractor.release();
                return true;
            }
        }
        mediaExtractor.release();
        return false;
    }

    static class MediaMetadataRetrieverFactory {
        MediaMetadataRetrieverFactory() {
        }

        public MediaMetadataRetriever build() {
            return new MediaMetadataRetriever();
        }
    }

    private static final class AssetFileDescriptorInitializer implements MediaInitializer<AssetFileDescriptor> {
        private AssetFileDescriptorInitializer() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeRetriever(MediaMetadataRetriever retriever, AssetFileDescriptor data) throws IllegalArgumentException {
            retriever.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeExtractor(MediaExtractor extractor, AssetFileDescriptor data) throws IOException {
            extractor.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
        }
    }

    static final class ParcelFileDescriptorInitializer implements MediaInitializer<ParcelFileDescriptor> {
        ParcelFileDescriptorInitializer() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeRetriever(MediaMetadataRetriever retriever, ParcelFileDescriptor data) throws IllegalArgumentException {
            retriever.setDataSource(data.getFileDescriptor());
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeExtractor(MediaExtractor extractor, ParcelFileDescriptor data) throws IOException {
            extractor.setDataSource(data.getFileDescriptor());
        }
    }

    static final class ByteBufferInitializer implements MediaInitializer<ByteBuffer> {
        ByteBufferInitializer() {
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeRetriever(MediaMetadataRetriever retriever, ByteBuffer data) throws IllegalArgumentException {
            retriever.setDataSource(getMediaDataSource(data));
        }

        @Override // com.bumptech.glide.load.resource.bitmap.VideoDecoder.MediaInitializer
        public void initializeExtractor(MediaExtractor extractor, ByteBuffer data) throws IOException {
            extractor.setDataSource(getMediaDataSource(data));
        }

        private MediaDataSource getMediaDataSource(final ByteBuffer data) {
            return new MediaDataSource() { // from class: com.bumptech.glide.load.resource.bitmap.VideoDecoder.ByteBufferInitializer.1
                @Override // android.media.MediaDataSource
                public int readAt(long position, byte[] buffer, int offset, int size) {
                    if (position >= data.limit()) {
                        return -1;
                    }
                    data.position((int) position);
                    int numBytesRead = Math.min(size, data.remaining());
                    data.get(buffer, offset, numBytesRead);
                    return numBytesRead;
                }

                @Override // android.media.MediaDataSource
                public long getSize() {
                    return data.limit();
                }

                @Override // java.io.Closeable, java.lang.AutoCloseable
                public void close() {
                }
            };
        }
    }

    private static final class VideoDecoderException extends RuntimeException {
        private static final long serialVersionUID = -2556382523004027815L;

        VideoDecoderException() {
            super("MediaMetadataRetriever failed to retrieve a frame without throwing, check the adb logs for .*MetadataRetriever.* prior to this exception for details");
        }
    }
}

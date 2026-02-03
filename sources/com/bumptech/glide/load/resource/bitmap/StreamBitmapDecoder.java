package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.util.ExceptionPassthroughInputStream;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class StreamBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private final ArrayPool byteArrayPool;
    private final Downsampler downsampler;

    public StreamBitmapDecoder(Downsampler downsampler, ArrayPool byteArrayPool) {
        this.downsampler = downsampler;
        this.byteArrayPool = byteArrayPool;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(InputStream source, Options options) {
        return this.downsampler.handles(source);
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Bitmap> decode(InputStream source, int width, int height, Options options) throws IOException {
        RecyclableBufferedInputStream bufferedStream;
        boolean ownsBufferedStream;
        if (source instanceof RecyclableBufferedInputStream) {
            bufferedStream = (RecyclableBufferedInputStream) source;
            ownsBufferedStream = false;
        } else {
            bufferedStream = new RecyclableBufferedInputStream(source, this.byteArrayPool);
            ownsBufferedStream = true;
        }
        ExceptionPassthroughInputStream exceptionStream = ExceptionPassthroughInputStream.obtain(bufferedStream);
        MarkEnforcingInputStream invalidatingStream = new MarkEnforcingInputStream(exceptionStream);
        UntrustedCallbacks callbacks = new UntrustedCallbacks(bufferedStream, exceptionStream);
        try {
            return this.downsampler.decode(invalidatingStream, width, height, options, callbacks);
        } finally {
            exceptionStream.release();
            if (ownsBufferedStream) {
                bufferedStream.release();
            }
        }
    }

    static class UntrustedCallbacks implements Downsampler.DecodeCallbacks {
        private final RecyclableBufferedInputStream bufferedStream;
        private final ExceptionPassthroughInputStream exceptionStream;

        UntrustedCallbacks(RecyclableBufferedInputStream bufferedStream, ExceptionPassthroughInputStream exceptionStream) {
            this.bufferedStream = bufferedStream;
            this.exceptionStream = exceptionStream;
        }

        @Override // com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeCallbacks
        public void onObtainBounds() {
            this.bufferedStream.fixMarkLimit();
        }

        @Override // com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeCallbacks
        public void onDecodeComplete(BitmapPool bitmapPool, Bitmap downsampled) throws IOException {
            IOException streamException = this.exceptionStream.getException();
            if (streamException != null) {
                if (downsampled != null) {
                    bitmapPool.put(downsampled);
                    throw streamException;
                }
                throw streamException;
            }
        }
    }
}

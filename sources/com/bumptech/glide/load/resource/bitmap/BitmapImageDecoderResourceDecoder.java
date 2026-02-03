package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.bumptech.glide.load.resource.DefaultOnHeaderDecodedListener;
import java.io.IOException;

/* loaded from: classes.dex */
public final class BitmapImageDecoderResourceDecoder implements ResourceDecoder<ImageDecoder.Source, Bitmap> {
    private static final String TAG = "BitmapImageDecoder";
    private final BitmapPool bitmapPool = new BitmapPoolAdapter();

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(ImageDecoder.Source source, Options options) throws IOException {
        return true;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Bitmap> decode(ImageDecoder.Source source, int width, int height, Options options) throws IOException {
        Bitmap result = ImageDecoder.decodeBitmap(source, new DefaultOnHeaderDecodedListener(width, height, options));
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Decoded [" + result.getWidth() + "x" + result.getHeight() + "] for [" + width + "x" + height + "]");
        }
        return new BitmapResource(result, this.bitmapPool);
    }
}

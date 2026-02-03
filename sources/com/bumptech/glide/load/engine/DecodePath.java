package com.bumptech.glide.load.engine;

import android.util.Log;
import androidx.core.util.Pools;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DecodePath<DataType, ResourceType, Transcode> {
    private static final String TAG = "DecodePath";
    private final Class<DataType> dataClass;
    private final List<? extends ResourceDecoder<DataType, ResourceType>> decoders;
    private final String failureMessage;
    private final Pools.Pool<List<Throwable>> listPool;
    private final ResourceTranscoder<ResourceType, Transcode> transcoder;

    interface DecodeCallback<ResourceType> {
        Resource<ResourceType> onResourceDecoded(Resource<ResourceType> resource);
    }

    public DecodePath(Class<DataType> dataClass, Class<ResourceType> resourceClass, Class<Transcode> transcodeClass, List<? extends ResourceDecoder<DataType, ResourceType>> decoders, ResourceTranscoder<ResourceType, Transcode> transcoder, Pools.Pool<List<Throwable>> listPool) {
        this.dataClass = dataClass;
        this.decoders = decoders;
        this.transcoder = transcoder;
        this.listPool = listPool;
        this.failureMessage = "Failed DecodePath{" + dataClass.getSimpleName() + "->" + resourceClass.getSimpleName() + "->" + transcodeClass.getSimpleName() + "}";
    }

    public Resource<Transcode> decode(DataRewinder<DataType> rewinder, int width, int height, Options options, DecodeCallback<ResourceType> callback) throws GlideException {
        Resource<ResourceType> decoded = decodeResource(rewinder, width, height, options);
        Resource<ResourceType> transformed = callback.onResourceDecoded(decoded);
        return this.transcoder.transcode(transformed, options);
    }

    private Resource<ResourceType> decodeResource(DataRewinder<DataType> rewinder, int width, int height, Options options) throws GlideException {
        List<Throwable> exceptions = (List) Preconditions.checkNotNull(this.listPool.acquire());
        try {
            return decodeResourceWithList(rewinder, width, height, options, exceptions);
        } finally {
            this.listPool.release(exceptions);
        }
    }

    private Resource<ResourceType> decodeResourceWithList(DataRewinder<DataType> rewinder, int width, int height, Options options, List<Throwable> exceptions) throws IOException, GlideException {
        Resource<ResourceType> result = null;
        int size = this.decoders.size();
        for (int i = 0; i < size; i++) {
            ResourceDecoder<DataType, ResourceType> decoder = this.decoders.get(i);
            try {
                DataType data = rewinder.rewindAndGet();
                if (decoder.handles(data, options)) {
                    DataType data2 = rewinder.rewindAndGet();
                    result = decoder.decode(data2, width, height, options);
                }
            } catch (IOException | OutOfMemoryError | RuntimeException e) {
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "Failed to decode data for " + decoder, e);
                }
                exceptions.add(e);
            }
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            throw new GlideException(this.failureMessage, new ArrayList(exceptions));
        }
        return result;
    }

    public String toString() {
        return "DecodePath{ dataClass=" + this.dataClass + ", decoders=" + this.decoders + ", transcoder=" + this.transcoder + '}';
    }
}

package com.bumptech.glide.load.engine;

import androidx.core.util.Pools;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class LoadPath<Data, ResourceType, Transcode> {
    private final Class<Data> dataClass;
    private final List<? extends DecodePath<Data, ResourceType, Transcode>> decodePaths;
    private final String failureMessage;
    private final Pools.Pool<List<Throwable>> listPool;

    public LoadPath(Class<Data> dataClass, Class<ResourceType> resourceClass, Class<Transcode> transcodeClass, List<DecodePath<Data, ResourceType, Transcode>> decodePaths, Pools.Pool<List<Throwable>> listPool) {
        this.dataClass = dataClass;
        this.listPool = listPool;
        this.decodePaths = (List) Preconditions.checkNotEmpty(decodePaths);
        this.failureMessage = "Failed LoadPath{" + dataClass.getSimpleName() + "->" + resourceClass.getSimpleName() + "->" + transcodeClass.getSimpleName() + "}";
    }

    public Resource<Transcode> load(DataRewinder<Data> rewinder, Options options, int width, int height, DecodePath.DecodeCallback<ResourceType> decodeCallback) throws GlideException {
        List<Throwable> throwables = (List) Preconditions.checkNotNull(this.listPool.acquire());
        try {
            return loadWithExceptionList(rewinder, options, width, height, decodeCallback, throwables);
        } finally {
            this.listPool.release(throwables);
        }
    }

    private Resource<Transcode> loadWithExceptionList(DataRewinder<Data> rewinder, Options options, int width, int height, DecodePath.DecodeCallback<ResourceType> decodeCallback, List<Throwable> exceptions) throws GlideException {
        int size = this.decodePaths.size();
        Resource<Transcode> result = null;
        for (int i = 0; i < size; i++) {
            DecodePath<Data, ResourceType, Transcode> path = this.decodePaths.get(i);
            try {
                Resource<Transcode> result2 = path.decode(rewinder, width, height, options, decodeCallback);
                result = result2;
            } catch (GlideException e) {
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

    public Class<Data> getDataClass() {
        return this.dataClass;
    }

    public String toString() {
        return "LoadPath{decodePaths=" + Arrays.toString(this.decodePaths.toArray()) + '}';
    }
}

package com.bumptech.glide.load.model.stream;

import android.text.TextUtils;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public abstract class BaseGlideUrlLoader<Model> implements ModelLoader<Model, InputStream> {
    private final ModelLoader<GlideUrl, InputStream> concreteLoader;
    private final ModelCache<Model, GlideUrl> modelCache;

    protected abstract String getUrl(Model model, int i, int i2, Options options);

    protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        this(concreteLoader, null);
    }

    protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, ModelCache<Model, GlideUrl> modelCache) {
        this.concreteLoader = concreteLoader;
        this.modelCache = modelCache;
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public ModelLoader.LoadData<InputStream> buildLoadData(Model model, int width, int height, Options options) {
        GlideUrl result = null;
        if (this.modelCache != null) {
            GlideUrl result2 = this.modelCache.get(model, width, height);
            result = result2;
        }
        if (result == null) {
            String stringURL = getUrl(model, width, height, options);
            if (TextUtils.isEmpty(stringURL)) {
                return null;
            }
            result = new GlideUrl(stringURL, getHeaders(model, width, height, options));
            if (this.modelCache != null) {
                this.modelCache.put(model, width, height, result);
            }
        }
        List<String> alternateUrls = getAlternateUrls(model, width, height, options);
        ModelLoader.LoadData<InputStream> concreteLoaderData = this.concreteLoader.buildLoadData(result, width, height, options);
        if (concreteLoaderData == null || alternateUrls.isEmpty()) {
            return concreteLoaderData;
        }
        return new ModelLoader.LoadData<>(concreteLoaderData.sourceKey, getAlternateKeys(alternateUrls), concreteLoaderData.fetcher);
    }

    private static List<Key> getAlternateKeys(Collection<String> alternateUrls) {
        List<Key> result = new ArrayList<>(alternateUrls.size());
        for (String alternate : alternateUrls) {
            result.add(new GlideUrl(alternate));
        }
        return result;
    }

    protected List<String> getAlternateUrls(Model model, int width, int height, Options options) {
        return Collections.emptyList();
    }

    protected Headers getHeaders(Model model, int width, int height, Options options) {
        return Headers.DEFAULT;
    }
}

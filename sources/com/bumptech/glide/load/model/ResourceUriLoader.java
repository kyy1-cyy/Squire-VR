package com.bumptech.glide.load.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import java.io.InputStream;
import java.util.List;

/* loaded from: classes.dex */
public final class ResourceUriLoader<DataT> implements ModelLoader<Uri, DataT> {
    private static final int INVALID_RESOURCE_ID = 0;
    private static final String TAG = "ResourceUriLoader";
    private final Context context;
    private final ModelLoader<Integer, DataT> delegate;

    public static ModelLoaderFactory<Uri, InputStream> newStreamFactory(Context context) {
        return new InputStreamFactory(context);
    }

    public static ModelLoaderFactory<Uri, AssetFileDescriptor> newAssetFileDescriptorFactory(Context context) {
        return new AssetFileDescriptorFactory(context);
    }

    ResourceUriLoader(Context context, ModelLoader<Integer, DataT> delegate) {
        this.context = context.getApplicationContext();
        this.delegate = delegate;
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public ModelLoader.LoadData<DataT> buildLoadData(Uri uri, int width, int height, Options options) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 1) {
            return parseResourceIdUri(uri, width, height, options);
        }
        if (pathSegments.size() == 2) {
            return parseResourceNameUri(uri, width, height, options);
        }
        if (Log.isLoggable(TAG, 5)) {
            Log.w(TAG, "Failed to parse resource uri: " + uri);
            return null;
        }
        return null;
    }

    private ModelLoader.LoadData<DataT> parseResourceNameUri(Uri uri, int width, int height, Options options) {
        List<String> pathSegments = uri.getPathSegments();
        String resourceType = pathSegments.get(0);
        String resourceName = pathSegments.get(1);
        int identifier = this.context.getResources().getIdentifier(resourceName, resourceType, this.context.getPackageName());
        if (identifier == 0) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Failed to find resource id for: " + uri);
                return null;
            }
            return null;
        }
        return this.delegate.buildLoadData(Integer.valueOf(identifier), width, height, options);
    }

    private ModelLoader.LoadData<DataT> parseResourceIdUri(Uri uri, int width, int height, Options options) throws NumberFormatException {
        try {
            int resourceId = Integer.parseInt(uri.getPathSegments().get(0));
            if (resourceId == 0) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Failed to parse a valid non-0 resource id from: " + uri);
                }
                return null;
            }
            return this.delegate.buildLoadData(Integer.valueOf(resourceId), width, height, options);
        } catch (NumberFormatException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Failed to parse resource id from: " + uri, e);
            }
            return null;
        }
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public boolean handles(Uri uri) {
        return "android.resource".equals(uri.getScheme()) && this.context.getPackageName().equals(uri.getAuthority());
    }

    private static final class InputStreamFactory implements ModelLoaderFactory<Uri, InputStream> {
        private final Context context;

        InputStreamFactory(Context context) {
            this.context = context;
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceUriLoader(this.context, multiFactory.build(Integer.class, InputStream.class));
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public void teardown() {
        }
    }

    private static final class AssetFileDescriptorFactory implements ModelLoaderFactory<Uri, AssetFileDescriptor> {
        private final Context context;

        AssetFileDescriptorFactory(Context context) {
            this.context = context;
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public ModelLoader<Uri, AssetFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceUriLoader(this.context, multiFactory.build(Integer.class, AssetFileDescriptor.class));
        }

        @Override // com.bumptech.glide.load.model.ModelLoaderFactory
        public void teardown() {
        }
    }
}

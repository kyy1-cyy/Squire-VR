package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.util.List;

/* loaded from: classes.dex */
public class ResourceDrawableDecoder implements ResourceDecoder<Uri, Drawable> {
    private static final String ANDROID_PACKAGE_NAME = "android";
    private static final int ID_PATH_SEGMENTS = 1;
    private static final int MISSING_RESOURCE_ID = 0;
    private static final int NAME_PATH_SEGMENT_INDEX = 1;
    private static final int NAME_URI_PATH_SEGMENTS = 2;
    private static final int RESOURCE_ID_SEGMENT_INDEX = 0;
    public static final Option<Resources.Theme> THEME = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.Theme");
    private static final int TYPE_PATH_SEGMENT_INDEX = 0;
    private final Context context;

    public ResourceDrawableDecoder(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(Uri source, Options options) {
        String scheme = source.getScheme();
        return scheme != null && scheme.equals("android.resource");
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Drawable> decode(Uri source, int width, int height, Options options) {
        Resources.Theme theme;
        Drawable drawable;
        String packageName = source.getAuthority();
        if (TextUtils.isEmpty(packageName)) {
            throw new IllegalStateException("Package name for " + source + " is null or empty");
        }
        Context targetContext = findContextForPackage(source, packageName);
        int resId = findResourceIdFromUri(targetContext, source);
        if (((String) Preconditions.checkNotNull(packageName)).equals(this.context.getPackageName())) {
            theme = (Resources.Theme) options.get(THEME);
        } else {
            theme = null;
        }
        if (theme == null) {
            drawable = DrawableDecoderCompat.getDrawable(this.context, targetContext, resId);
        } else {
            drawable = DrawableDecoderCompat.getDrawable(this.context, resId, theme);
        }
        return NonOwnedDrawableResource.newInstance(drawable);
    }

    private Context findContextForPackage(Uri source, String packageName) {
        if (packageName.equals(this.context.getPackageName())) {
            return this.context;
        }
        try {
            return this.context.createPackageContext(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            if (packageName.contains(this.context.getPackageName())) {
                return this.context;
            }
            throw new IllegalArgumentException("Failed to obtain context or unrecognized Uri format for: " + source, e);
        }
    }

    private int findResourceIdFromUri(Context context, Uri source) {
        List<String> segments = source.getPathSegments();
        if (segments.size() == 2) {
            return findResourceIdFromTypeAndNameResourceUri(context, source);
        }
        if (segments.size() == 1) {
            return findResourceIdFromResourceIdUri(source);
        }
        throw new IllegalArgumentException("Unrecognized Uri format: " + source);
    }

    private int findResourceIdFromTypeAndNameResourceUri(Context context, Uri source) {
        List<String> segments = source.getPathSegments();
        String packageName = source.getAuthority();
        String typeName = segments.get(0);
        String resourceName = segments.get(1);
        int result = context.getResources().getIdentifier(resourceName, typeName, packageName);
        if (result == 0) {
            result = Resources.getSystem().getIdentifier(resourceName, typeName, ANDROID_PACKAGE_NAME);
        }
        if (result == 0) {
            throw new IllegalArgumentException("Failed to find resource id for: " + source);
        }
        return result;
    }

    private int findResourceIdFromResourceIdUri(Uri source) {
        List<String> segments = source.getPathSegments();
        try {
            return Integer.parseInt(segments.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unrecognized Uri format: " + source, e);
        }
    }
}

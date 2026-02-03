package com.bumptech.glide.load.engine.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import com.bumptech.glide.util.Preconditions;

/* loaded from: classes.dex */
public final class MemorySizeCalculator {
    static final int BYTES_PER_ARGB_8888_PIXEL = 4;
    private static final int LOW_MEMORY_BYTE_ARRAY_POOL_DIVISOR = 2;
    private static final String TAG = "MemorySizeCalculator";
    private final int arrayPoolSize;
    private final int bitmapPoolSize;
    private final Context context;
    private final int memoryCacheSize;

    interface ScreenDimensions {
        int getHeightPixels();

        int getWidthPixels();
    }

    MemorySizeCalculator(Builder builder) {
        int i;
        this.context = builder.context;
        if (isLowMemoryDevice(builder.activityManager)) {
            i = builder.arrayPoolSizeBytes / 2;
        } else {
            i = builder.arrayPoolSizeBytes;
        }
        this.arrayPoolSize = i;
        int maxSize = getMaxSize(builder.activityManager, builder.maxSizeMultiplier, builder.lowMemoryMaxSizeMultiplier);
        int widthPixels = builder.screenDimensions.getWidthPixels();
        int heightPixels = builder.screenDimensions.getHeightPixels();
        int screenSize = widthPixels * heightPixels * 4;
        int targetBitmapPoolSize = Math.round(screenSize * builder.bitmapPoolScreens);
        int targetMemoryCacheSize = Math.round(screenSize * builder.memoryCacheScreens);
        int availableSize = maxSize - this.arrayPoolSize;
        if (targetMemoryCacheSize + targetBitmapPoolSize <= availableSize) {
            this.memoryCacheSize = targetMemoryCacheSize;
            this.bitmapPoolSize = targetBitmapPoolSize;
        } else {
            float part = availableSize / (builder.bitmapPoolScreens + builder.memoryCacheScreens);
            this.memoryCacheSize = Math.round(builder.memoryCacheScreens * part);
            this.bitmapPoolSize = Math.round(builder.bitmapPoolScreens * part);
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Calculation complete, Calculated memory cache size: " + toMb(this.memoryCacheSize) + ", pool size: " + toMb(this.bitmapPoolSize) + ", byte array size: " + toMb(this.arrayPoolSize) + ", memory class limited? " + (targetMemoryCacheSize + targetBitmapPoolSize > maxSize) + ", max size: " + toMb(maxSize) + ", memoryClass: " + builder.activityManager.getMemoryClass() + ", isLowMemoryDevice: " + isLowMemoryDevice(builder.activityManager));
        }
    }

    public int getMemoryCacheSize() {
        return this.memoryCacheSize;
    }

    public int getBitmapPoolSize() {
        return this.bitmapPoolSize;
    }

    public int getArrayPoolSizeInBytes() {
        return this.arrayPoolSize;
    }

    private static int getMaxSize(ActivityManager activityManager, float maxSizeMultiplier, float lowMemoryMaxSizeMultiplier) {
        int memoryClassBytes = activityManager.getMemoryClass() * 1024 * 1024;
        boolean isLowMemoryDevice = isLowMemoryDevice(activityManager);
        return Math.round(memoryClassBytes * (isLowMemoryDevice ? lowMemoryMaxSizeMultiplier : maxSizeMultiplier));
    }

    private String toMb(int bytes) {
        return Formatter.formatFileSize(this.context, bytes);
    }

    static boolean isLowMemoryDevice(ActivityManager activityManager) {
        return activityManager.isLowRamDevice();
    }

    public static final class Builder {
        static final int ARRAY_POOL_SIZE_BYTES = 4194304;
        static final int BITMAP_POOL_TARGET_SCREENS = 1;
        static final float LOW_MEMORY_MAX_SIZE_MULTIPLIER = 0.33f;
        static final float MAX_SIZE_MULTIPLIER = 0.4f;
        static final int MEMORY_CACHE_TARGET_SCREENS = 2;
        ActivityManager activityManager;
        float bitmapPoolScreens;
        final Context context;
        ScreenDimensions screenDimensions;
        float memoryCacheScreens = 2.0f;
        float maxSizeMultiplier = MAX_SIZE_MULTIPLIER;
        float lowMemoryMaxSizeMultiplier = LOW_MEMORY_MAX_SIZE_MULTIPLIER;
        int arrayPoolSizeBytes = 4194304;

        public Builder(Context context) {
            this.bitmapPoolScreens = BITMAP_POOL_TARGET_SCREENS;
            this.context = context;
            this.activityManager = (ActivityManager) context.getSystemService("activity");
            this.screenDimensions = new DisplayMetricsScreenDimensions(context.getResources().getDisplayMetrics());
            if (MemorySizeCalculator.isLowMemoryDevice(this.activityManager)) {
                this.bitmapPoolScreens = 0.0f;
            }
        }

        public Builder setMemoryCacheScreens(float memoryCacheScreens) {
            Preconditions.checkArgument(memoryCacheScreens >= 0.0f, "Memory cache screens must be greater than or equal to 0");
            this.memoryCacheScreens = memoryCacheScreens;
            return this;
        }

        public Builder setBitmapPoolScreens(float bitmapPoolScreens) {
            Preconditions.checkArgument(bitmapPoolScreens >= 0.0f, "Bitmap pool screens must be greater than or equal to 0");
            this.bitmapPoolScreens = bitmapPoolScreens;
            return this;
        }

        public Builder setMaxSizeMultiplier(float maxSizeMultiplier) {
            Preconditions.checkArgument(maxSizeMultiplier >= 0.0f && maxSizeMultiplier <= 1.0f, "Size multiplier must be between 0 and 1");
            this.maxSizeMultiplier = maxSizeMultiplier;
            return this;
        }

        public Builder setLowMemoryMaxSizeMultiplier(float lowMemoryMaxSizeMultiplier) {
            Preconditions.checkArgument(lowMemoryMaxSizeMultiplier >= 0.0f && lowMemoryMaxSizeMultiplier <= 1.0f, "Low memory max size multiplier must be between 0 and 1");
            this.lowMemoryMaxSizeMultiplier = lowMemoryMaxSizeMultiplier;
            return this;
        }

        public Builder setArrayPoolSize(int arrayPoolSizeBytes) {
            this.arrayPoolSizeBytes = arrayPoolSizeBytes;
            return this;
        }

        Builder setActivityManager(ActivityManager activityManager) {
            this.activityManager = activityManager;
            return this;
        }

        Builder setScreenDimensions(ScreenDimensions screenDimensions) {
            this.screenDimensions = screenDimensions;
            return this;
        }

        public MemorySizeCalculator build() {
            return new MemorySizeCalculator(this);
        }
    }

    private static final class DisplayMetricsScreenDimensions implements ScreenDimensions {
        private final DisplayMetrics displayMetrics;

        DisplayMetricsScreenDimensions(DisplayMetrics displayMetrics) {
            this.displayMetrics = displayMetrics;
        }

        @Override // com.bumptech.glide.load.engine.cache.MemorySizeCalculator.ScreenDimensions
        public int getWidthPixels() {
            return this.displayMetrics.widthPixels;
        }

        @Override // com.bumptech.glide.load.engine.cache.MemorySizeCalculator.ScreenDimensions
        public int getHeightPixels() {
            return this.displayMetrics.heightPixels;
        }
    }
}

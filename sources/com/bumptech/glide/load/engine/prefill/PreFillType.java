package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap;
import com.bumptech.glide.util.Preconditions;

/* loaded from: classes.dex */
public final class PreFillType {
    static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.RGB_565;
    private final Bitmap.Config config;
    private final int height;
    private final int weight;
    private final int width;

    PreFillType(int width, int height, Bitmap.Config config, int weight) {
        this.config = (Bitmap.Config) Preconditions.checkNotNull(config, "Config must not be null");
        this.width = width;
        this.height = height;
        this.weight = weight;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    Bitmap.Config getConfig() {
        return this.config;
    }

    int getWeight() {
        return this.weight;
    }

    public boolean equals(Object o) {
        if (!(o instanceof PreFillType)) {
            return false;
        }
        PreFillType other = (PreFillType) o;
        return this.height == other.height && this.width == other.width && this.weight == other.weight && this.config == other.config;
    }

    public int hashCode() {
        int result = this.width;
        return (((((result * 31) + this.height) * 31) + this.config.hashCode()) * 31) + this.weight;
    }

    public String toString() {
        return "PreFillSize{width=" + this.width + ", height=" + this.height + ", config=" + this.config + ", weight=" + this.weight + '}';
    }

    public static class Builder {
        private Bitmap.Config config;
        private final int height;
        private int weight;
        private final int width;

        public Builder(int size) {
            this(size, size);
        }

        public Builder(int width, int height) {
            this.weight = 1;
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be > 0");
            }
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be > 0");
            }
            this.width = width;
            this.height = height;
        }

        public Builder setConfig(Bitmap.Config config) {
            this.config = config;
            return this;
        }

        Bitmap.Config getConfig() {
            return this.config;
        }

        public Builder setWeight(int weight) {
            if (weight <= 0) {
                throw new IllegalArgumentException("Weight must be > 0");
            }
            this.weight = weight;
            return this;
        }

        PreFillType build() {
            return new PreFillType(this.width, this.height, this.config, this.weight);
        }
    }
}

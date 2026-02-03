package com.google.android.material.color;

import com.google.android.material.R;

/* loaded from: classes.dex */
public class HarmonizedColorsOptions {
    private final int colorAttributeToHarmonizeWith;
    private final HarmonizedColorAttributes colorAttributes;
    private final int[] colorResourceIds;

    public static HarmonizedColorsOptions createMaterialDefaults() {
        return new Builder().setColorAttributes(HarmonizedColorAttributes.createMaterialDefaults()).build();
    }

    private HarmonizedColorsOptions(Builder builder) {
        this.colorResourceIds = builder.colorResourceIds;
        this.colorAttributes = builder.colorAttributes;
        this.colorAttributeToHarmonizeWith = builder.colorAttributeToHarmonizeWith;
    }

    public int[] getColorResourceIds() {
        return this.colorResourceIds;
    }

    public HarmonizedColorAttributes getColorAttributes() {
        return this.colorAttributes;
    }

    public int getColorAttributeToHarmonizeWith() {
        return this.colorAttributeToHarmonizeWith;
    }

    public static class Builder {
        private HarmonizedColorAttributes colorAttributes;
        private int[] colorResourceIds = new int[0];
        private int colorAttributeToHarmonizeWith = R.attr.colorPrimary;

        public Builder setColorResourceIds(int[] colorResourceIds) {
            this.colorResourceIds = colorResourceIds;
            return this;
        }

        public Builder setColorAttributes(HarmonizedColorAttributes colorAttributes) {
            this.colorAttributes = colorAttributes;
            return this;
        }

        public Builder setColorAttributeToHarmonizeWith(int colorAttributeToHarmonizeWith) {
            this.colorAttributeToHarmonizeWith = colorAttributeToHarmonizeWith;
            return this;
        }

        public HarmonizedColorsOptions build() {
            return new HarmonizedColorsOptions(this);
        }
    }

    int getThemeOverlayResourceId(int defaultThemeOverlay) {
        if (this.colorAttributes != null && this.colorAttributes.getThemeOverlay() != 0) {
            return this.colorAttributes.getThemeOverlay();
        }
        return defaultThemeOverlay;
    }
}

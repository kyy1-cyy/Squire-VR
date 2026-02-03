package com.google.android.material.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.R;
import com.google.android.material.color.utilities.Blend;
import com.google.android.material.color.utilities.Hct;
import com.google.android.material.resources.MaterialAttributes;

/* loaded from: classes.dex */
public class MaterialColors {
    public static final float ALPHA_DISABLED = 0.38f;
    public static final float ALPHA_DISABLED_LOW = 0.12f;
    public static final float ALPHA_FULL = 1.0f;
    public static final float ALPHA_LOW = 0.32f;
    public static final float ALPHA_MEDIUM = 0.54f;
    private static final int TONE_ACCENT_CONTAINER_DARK = 30;
    private static final int TONE_ACCENT_CONTAINER_LIGHT = 90;
    private static final int TONE_ACCENT_DARK = 80;
    private static final int TONE_ACCENT_LIGHT = 40;
    private static final int TONE_ON_ACCENT_CONTAINER_DARK = 90;
    private static final int TONE_ON_ACCENT_CONTAINER_LIGHT = 10;
    private static final int TONE_ON_ACCENT_DARK = 20;
    private static final int TONE_ON_ACCENT_LIGHT = 100;

    private MaterialColors() {
    }

    public static int getColor(View view, int colorAttributeResId) {
        return resolveColor(view.getContext(), MaterialAttributes.resolveTypedValueOrThrow(view, colorAttributeResId));
    }

    public static int getColor(Context context, int colorAttributeResId, String errorMessageComponent) {
        return resolveColor(context, MaterialAttributes.resolveTypedValueOrThrow(context, colorAttributeResId, errorMessageComponent));
    }

    public static int getColor(View view, int colorAttributeResId, int defaultValue) {
        return getColor(view.getContext(), colorAttributeResId, defaultValue);
    }

    public static int getColor(Context context, int colorAttributeResId, int defaultValue) {
        TypedValue typedValue = MaterialAttributes.resolve(context, colorAttributeResId);
        if (typedValue != null) {
            return resolveColor(context, typedValue);
        }
        return defaultValue;
    }

    public static ColorStateList getColorStateList(Context context, int colorAttributeResId, ColorStateList defaultValue) {
        ColorStateList resolvedColor = null;
        TypedValue typedValue = MaterialAttributes.resolve(context, colorAttributeResId);
        if (typedValue != null) {
            resolvedColor = resolveColorStateList(context, typedValue);
        }
        return resolvedColor == null ? defaultValue : resolvedColor;
    }

    public static ColorStateList getColorStateListOrNull(Context context, int colorAttributeResId) {
        TypedValue typedValue = MaterialAttributes.resolve(context, colorAttributeResId);
        if (typedValue == null) {
            return null;
        }
        if (typedValue.resourceId != 0) {
            return ContextCompat.getColorStateList(context, typedValue.resourceId);
        }
        if (typedValue.data == 0) {
            return null;
        }
        return ColorStateList.valueOf(typedValue.data);
    }

    private static int resolveColor(Context context, TypedValue typedValue) {
        if (typedValue.resourceId != 0) {
            return ContextCompat.getColor(context, typedValue.resourceId);
        }
        return typedValue.data;
    }

    private static ColorStateList resolveColorStateList(Context context, TypedValue typedValue) {
        if (typedValue.resourceId != 0) {
            return ContextCompat.getColorStateList(context, typedValue.resourceId);
        }
        return ColorStateList.valueOf(typedValue.data);
    }

    public static int layer(View view, int backgroundColorAttributeResId, int overlayColorAttributeResId) {
        return layer(view, backgroundColorAttributeResId, overlayColorAttributeResId, 1.0f);
    }

    public static int layer(View view, int backgroundColorAttributeResId, int overlayColorAttributeResId, float overlayAlpha) {
        int backgroundColor = getColor(view, backgroundColorAttributeResId);
        int overlayColor = getColor(view, overlayColorAttributeResId);
        return layer(backgroundColor, overlayColor, overlayAlpha);
    }

    public static int layer(int backgroundColor, int overlayColor, float overlayAlpha) {
        int computedAlpha = Math.round(Color.alpha(overlayColor) * overlayAlpha);
        int computedOverlayColor = ColorUtils.setAlphaComponent(overlayColor, computedAlpha);
        return layer(backgroundColor, computedOverlayColor);
    }

    public static int layer(int backgroundColor, int overlayColor) {
        return ColorUtils.compositeColors(overlayColor, backgroundColor);
    }

    public static int compositeARGBWithAlpha(int originalARGB, int alpha) {
        return ColorUtils.setAlphaComponent(originalARGB, (Color.alpha(originalARGB) * alpha) / 255);
    }

    public static boolean isColorLight(int color) {
        return color != 0 && ColorUtils.calculateLuminance(color) > 0.5d;
    }

    public static int harmonizeWithPrimary(Context context, int colorToHarmonize) {
        return harmonize(colorToHarmonize, getColor(context, R.attr.colorPrimary, MaterialColors.class.getCanonicalName()));
    }

    public static int harmonize(int colorToHarmonize, int colorToHarmonizeWith) {
        return Blend.harmonize(colorToHarmonize, colorToHarmonizeWith);
    }

    public static ColorRoles getColorRoles(Context context, int color) {
        return getColorRoles(color, MaterialAttributes.resolveBoolean(context, R.attr.isLightTheme, true));
    }

    public static ColorRoles getColorRoles(int color, boolean isLightTheme) {
        if (isLightTheme) {
            return new ColorRoles(getColorRole(color, 40), getColorRole(color, 100), getColorRole(color, 90), getColorRole(color, 10));
        }
        return new ColorRoles(getColorRole(color, TONE_ACCENT_DARK), getColorRole(color, 20), getColorRole(color, 30), getColorRole(color, 90));
    }

    private static int getColorRole(int color, int tone) {
        Hct hctColor = Hct.fromInt(color);
        hctColor.setTone(tone);
        return hctColor.toInt();
    }
}

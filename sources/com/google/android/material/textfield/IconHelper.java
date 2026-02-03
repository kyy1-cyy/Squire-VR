package com.google.android.material.textfield;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.internal.CheckableImageButton;
import java.util.Arrays;

/* loaded from: classes.dex */
class IconHelper {
    private IconHelper() {
    }

    static void setIconOnClickListener(CheckableImageButton iconView, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        iconView.setOnClickListener(onClickListener);
        setIconClickable(iconView, onLongClickListener);
    }

    static void setIconOnLongClickListener(CheckableImageButton iconView, View.OnLongClickListener onLongClickListener) {
        iconView.setOnLongClickListener(onLongClickListener);
        setIconClickable(iconView, onLongClickListener);
    }

    private static void setIconClickable(CheckableImageButton iconView, View.OnLongClickListener onLongClickListener) {
        boolean iconClickable = ViewCompat.hasOnClickListeners(iconView);
        boolean iconLongClickable = onLongClickListener != null;
        boolean iconFocusable = iconClickable || iconLongClickable;
        iconView.setFocusable(iconFocusable);
        iconView.setClickable(iconClickable);
        iconView.setPressable(iconClickable);
        iconView.setLongClickable(iconLongClickable);
        ViewCompat.setImportantForAccessibility(iconView, iconFocusable ? 1 : 2);
    }

    static void applyIconTint(TextInputLayout textInputLayout, CheckableImageButton iconView, ColorStateList iconTintList, PorterDuff.Mode iconTintMode) {
        Drawable icon = iconView.getDrawable();
        if (icon != null) {
            icon = DrawableCompat.wrap(icon).mutate();
            if (iconTintList != null && iconTintList.isStateful()) {
                int color = iconTintList.getColorForState(mergeIconState(textInputLayout, iconView), iconTintList.getDefaultColor());
                DrawableCompat.setTintList(icon, ColorStateList.valueOf(color));
            } else {
                DrawableCompat.setTintList(icon, iconTintList);
            }
            if (iconTintMode != null) {
                DrawableCompat.setTintMode(icon, iconTintMode);
            }
        }
        if (iconView.getDrawable() != icon) {
            iconView.setImageDrawable(icon);
        }
    }

    static void refreshIconDrawableState(TextInputLayout textInputLayout, CheckableImageButton iconView, ColorStateList colorStateList) {
        Drawable icon = iconView.getDrawable();
        if (iconView.getDrawable() == null || colorStateList == null || !colorStateList.isStateful()) {
            return;
        }
        int color = colorStateList.getColorForState(mergeIconState(textInputLayout, iconView), colorStateList.getDefaultColor());
        Drawable icon2 = DrawableCompat.wrap(icon).mutate();
        DrawableCompat.setTintList(icon2, ColorStateList.valueOf(color));
        iconView.setImageDrawable(icon2);
    }

    private static int[] mergeIconState(TextInputLayout textInputLayout, CheckableImageButton iconView) {
        int[] textInputStates = textInputLayout.getDrawableState();
        int[] iconStates = iconView.getDrawableState();
        int index = textInputStates.length;
        int[] states = Arrays.copyOf(textInputStates, textInputStates.length + iconStates.length);
        System.arraycopy(iconStates, 0, states, index, iconStates.length);
        return states;
    }

    static void setCompatRippleBackgroundIfNeeded(CheckableImageButton iconView) {
    }

    static void setIconMinSize(CheckableImageButton iconView, int iconSize) {
        iconView.setMinimumWidth(iconSize);
        iconView.setMinimumHeight(iconSize);
    }

    static void setIconScaleType(CheckableImageButton iconView, ImageView.ScaleType scaleType) {
        iconView.setScaleType(scaleType);
    }

    static ImageView.ScaleType convertScaleType(int scaleType) {
        switch (scaleType) {
            case 0:
                return ImageView.ScaleType.FIT_XY;
            case 1:
                return ImageView.ScaleType.FIT_START;
            case 2:
                return ImageView.ScaleType.FIT_CENTER;
            case 3:
                return ImageView.ScaleType.FIT_END;
            case 4:
            default:
                return ImageView.ScaleType.CENTER;
            case 5:
                return ImageView.ScaleType.CENTER_CROP;
            case 6:
                return ImageView.ScaleType.CENTER_INSIDE;
        }
    }
}

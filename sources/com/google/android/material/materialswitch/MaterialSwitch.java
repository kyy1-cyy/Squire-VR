package com.google.android.material.materialswitch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import com.google.android.material.R;
import com.google.android.material.drawable.DrawableUtils;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

/* loaded from: classes.dex */
public class MaterialSwitch extends SwitchCompat {
    private static final int DEF_STYLE_RES = R.style.Widget_Material3_CompoundButton_MaterialSwitch;
    private static final int[] STATE_SET_WITH_ICON = {R.attr.state_with_icon};
    private int[] currentStateChecked;
    private int[] currentStateUnchecked;
    private Drawable thumbDrawable;
    private Drawable thumbIconDrawable;
    private ColorStateList thumbIconTintList;
    private PorterDuff.Mode thumbIconTintMode;
    private ColorStateList thumbTintList;
    private Drawable trackDecorationDrawable;
    private ColorStateList trackDecorationTintList;
    private PorterDuff.Mode trackDecorationTintMode;
    private Drawable trackDrawable;
    private ColorStateList trackTintList;

    public MaterialSwitch(Context context) {
        this(context, null);
    }

    public MaterialSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialSwitchStyle);
    }

    public MaterialSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
        Context context2 = getContext();
        this.thumbDrawable = super.getThumbDrawable();
        this.thumbTintList = super.getThumbTintList();
        super.setThumbTintList(null);
        this.trackDrawable = super.getTrackDrawable();
        this.trackTintList = super.getTrackTintList();
        super.setTrackTintList(null);
        TintTypedArray attributes = ThemeEnforcement.obtainTintedStyledAttributes(context2, attrs, R.styleable.MaterialSwitch, defStyleAttr, DEF_STYLE_RES, new int[0]);
        this.thumbIconDrawable = attributes.getDrawable(R.styleable.MaterialSwitch_thumbIcon);
        this.thumbIconTintList = attributes.getColorStateList(R.styleable.MaterialSwitch_thumbIconTint);
        this.thumbIconTintMode = ViewUtils.parseTintMode(attributes.getInt(R.styleable.MaterialSwitch_thumbIconTintMode, -1), PorterDuff.Mode.SRC_IN);
        this.trackDecorationDrawable = attributes.getDrawable(R.styleable.MaterialSwitch_trackDecoration);
        this.trackDecorationTintList = attributes.getColorStateList(R.styleable.MaterialSwitch_trackDecorationTint);
        this.trackDecorationTintMode = ViewUtils.parseTintMode(attributes.getInt(R.styleable.MaterialSwitch_trackDecorationTintMode, -1), PorterDuff.Mode.SRC_IN);
        attributes.recycle();
        setEnforceSwitchWidth(false);
        refreshThumbDrawable();
        refreshTrackDrawable();
    }

    @Override // android.view.View
    public void invalidate() {
        updateDrawableTints();
        super.invalidate();
    }

    @Override // androidx.appcompat.widget.SwitchCompat, android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.thumbIconDrawable != null) {
            mergeDrawableStates(drawableState, STATE_SET_WITH_ICON);
        }
        this.currentStateUnchecked = DrawableUtils.getUncheckedState(drawableState);
        this.currentStateChecked = DrawableUtils.getCheckedState(drawableState);
        return drawableState;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setThumbDrawable(Drawable drawable) {
        this.thumbDrawable = drawable;
        refreshThumbDrawable();
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public Drawable getThumbDrawable() {
        return this.thumbDrawable;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setThumbTintList(ColorStateList tintList) {
        this.thumbTintList = tintList;
        refreshThumbDrawable();
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public ColorStateList getThumbTintList() {
        return this.thumbTintList;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setThumbTintMode(PorterDuff.Mode tintMode) {
        super.setThumbTintMode(tintMode);
        refreshThumbDrawable();
    }

    public void setThumbIconResource(int resId) {
        setThumbIconDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public void setThumbIconDrawable(Drawable icon) {
        this.thumbIconDrawable = icon;
        refreshThumbDrawable();
    }

    public Drawable getThumbIconDrawable() {
        return this.thumbIconDrawable;
    }

    public void setThumbIconTintList(ColorStateList tintList) {
        this.thumbIconTintList = tintList;
        refreshThumbDrawable();
    }

    public ColorStateList getThumbIconTintList() {
        return this.thumbIconTintList;
    }

    public void setThumbIconTintMode(PorterDuff.Mode tintMode) {
        this.thumbIconTintMode = tintMode;
        refreshThumbDrawable();
    }

    public PorterDuff.Mode getThumbIconTintMode() {
        return this.thumbIconTintMode;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setTrackDrawable(Drawable track) {
        this.trackDrawable = track;
        refreshTrackDrawable();
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public Drawable getTrackDrawable() {
        return this.trackDrawable;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setTrackTintList(ColorStateList tintList) {
        this.trackTintList = tintList;
        refreshTrackDrawable();
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public ColorStateList getTrackTintList() {
        return this.trackTintList;
    }

    @Override // androidx.appcompat.widget.SwitchCompat
    public void setTrackTintMode(PorterDuff.Mode tintMode) {
        super.setTrackTintMode(tintMode);
        refreshTrackDrawable();
    }

    public void setTrackDecorationResource(int resId) {
        setTrackDecorationDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public void setTrackDecorationDrawable(Drawable trackDecoration) {
        this.trackDecorationDrawable = trackDecoration;
        refreshTrackDrawable();
    }

    public Drawable getTrackDecorationDrawable() {
        return this.trackDecorationDrawable;
    }

    public void setTrackDecorationTintList(ColorStateList tintList) {
        this.trackDecorationTintList = tintList;
        refreshTrackDrawable();
    }

    public ColorStateList getTrackDecorationTintList() {
        return this.trackDecorationTintList;
    }

    public void setTrackDecorationTintMode(PorterDuff.Mode tintMode) {
        this.trackDecorationTintMode = tintMode;
        refreshTrackDrawable();
    }

    public PorterDuff.Mode getTrackDecorationTintMode() {
        return this.trackDecorationTintMode;
    }

    private void refreshThumbDrawable() {
        this.thumbDrawable = DrawableUtils.createTintableDrawableIfNeeded(this.thumbDrawable, this.thumbTintList, getThumbTintMode());
        this.thumbIconDrawable = DrawableUtils.createTintableDrawableIfNeeded(this.thumbIconDrawable, this.thumbIconTintList, this.thumbIconTintMode);
        updateDrawableTints();
        super.setThumbDrawable(DrawableUtils.compositeTwoLayeredDrawable(this.thumbDrawable, this.thumbIconDrawable));
        refreshDrawableState();
    }

    private void refreshTrackDrawable() {
        Drawable finalTrackDrawable;
        this.trackDrawable = DrawableUtils.createTintableDrawableIfNeeded(this.trackDrawable, this.trackTintList, getTrackTintMode());
        this.trackDecorationDrawable = DrawableUtils.createTintableDrawableIfNeeded(this.trackDecorationDrawable, this.trackDecorationTintList, this.trackDecorationTintMode);
        updateDrawableTints();
        if (this.trackDrawable != null && this.trackDecorationDrawable != null) {
            finalTrackDrawable = new LayerDrawable(new Drawable[]{this.trackDrawable, this.trackDecorationDrawable});
        } else {
            Drawable finalTrackDrawable2 = this.trackDrawable;
            if (finalTrackDrawable2 != null) {
                finalTrackDrawable = this.trackDrawable;
            } else {
                finalTrackDrawable = this.trackDecorationDrawable;
            }
        }
        if (finalTrackDrawable != null) {
            setSwitchMinWidth(finalTrackDrawable.getIntrinsicWidth());
        }
        super.setTrackDrawable(finalTrackDrawable);
    }

    private void updateDrawableTints() {
        if (this.thumbTintList == null && this.thumbIconTintList == null && this.trackTintList == null && this.trackDecorationTintList == null) {
            return;
        }
        float thumbPosition = getThumbPosition();
        if (this.thumbTintList != null) {
            setInterpolatedDrawableTintIfPossible(this.thumbDrawable, this.thumbTintList, this.currentStateUnchecked, this.currentStateChecked, thumbPosition);
        }
        if (this.thumbIconTintList != null) {
            setInterpolatedDrawableTintIfPossible(this.thumbIconDrawable, this.thumbIconTintList, this.currentStateUnchecked, this.currentStateChecked, thumbPosition);
        }
        if (this.trackTintList != null) {
            setInterpolatedDrawableTintIfPossible(this.trackDrawable, this.trackTintList, this.currentStateUnchecked, this.currentStateChecked, thumbPosition);
        }
        if (this.trackDecorationTintList != null) {
            setInterpolatedDrawableTintIfPossible(this.trackDecorationDrawable, this.trackDecorationTintList, this.currentStateUnchecked, this.currentStateChecked, thumbPosition);
        }
    }

    private static void setInterpolatedDrawableTintIfPossible(Drawable drawable, ColorStateList tint, int[] stateUnchecked, int[] stateChecked, float thumbPosition) {
        if (drawable == null || tint == null) {
            return;
        }
        DrawableCompat.setTint(drawable, ColorUtils.blendARGB(tint.getColorForState(stateUnchecked, 0), tint.getColorForState(stateChecked, 0), thumbPosition));
    }
}

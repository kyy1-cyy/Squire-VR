package com.google.android.material.textfield;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.view.GravityCompat;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import com.google.android.material.R;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.resources.MaterialResources;

/* loaded from: classes.dex */
class StartCompoundLayout extends LinearLayout {
    private boolean hintExpanded;
    private CharSequence prefixText;
    private final TextView prefixTextView;
    private int startIconMinSize;
    private View.OnLongClickListener startIconOnLongClickListener;
    private ImageView.ScaleType startIconScaleType;
    private ColorStateList startIconTintList;
    private PorterDuff.Mode startIconTintMode;
    private final CheckableImageButton startIconView;
    private final TextInputLayout textInputLayout;

    StartCompoundLayout(TextInputLayout textInputLayout, TintTypedArray a) {
        super(textInputLayout.getContext());
        this.textInputLayout = textInputLayout;
        setVisibility(8);
        setOrientation(0);
        setLayoutParams(new FrameLayout.LayoutParams(-2, -1, GravityCompat.START));
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        this.startIconView = (CheckableImageButton) layoutInflater.inflate(R.layout.design_text_input_start_icon, (ViewGroup) this, false);
        IconHelper.setCompatRippleBackgroundIfNeeded(this.startIconView);
        this.prefixTextView = new AppCompatTextView(getContext());
        initStartIconView(a);
        initPrefixTextView(a);
        addView(this.startIconView);
        addView(this.prefixTextView);
    }

    private void initStartIconView(TintTypedArray a) {
        if (MaterialResources.isFontScaleAtLeast1_3(getContext())) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.startIconView.getLayoutParams();
            MarginLayoutParamsCompat.setMarginEnd(lp, 0);
        }
        setStartIconOnClickListener(null);
        setStartIconOnLongClickListener(null);
        if (a.hasValue(R.styleable.TextInputLayout_startIconTint)) {
            this.startIconTintList = MaterialResources.getColorStateList(getContext(), a, R.styleable.TextInputLayout_startIconTint);
        }
        if (a.hasValue(R.styleable.TextInputLayout_startIconTintMode)) {
            this.startIconTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.TextInputLayout_startIconTintMode, -1), null);
        }
        if (a.hasValue(R.styleable.TextInputLayout_startIconDrawable)) {
            setStartIconDrawable(a.getDrawable(R.styleable.TextInputLayout_startIconDrawable));
            if (a.hasValue(R.styleable.TextInputLayout_startIconContentDescription)) {
                setStartIconContentDescription(a.getText(R.styleable.TextInputLayout_startIconContentDescription));
            }
            setStartIconCheckable(a.getBoolean(R.styleable.TextInputLayout_startIconCheckable, true));
        }
        setStartIconMinSize(a.getDimensionPixelSize(R.styleable.TextInputLayout_startIconMinSize, getResources().getDimensionPixelSize(R.dimen.mtrl_min_touch_target_size)));
        if (a.hasValue(R.styleable.TextInputLayout_startIconScaleType)) {
            setStartIconScaleType(IconHelper.convertScaleType(a.getInt(R.styleable.TextInputLayout_startIconScaleType, -1)));
        }
    }

    private void initPrefixTextView(TintTypedArray a) {
        this.prefixTextView.setVisibility(8);
        this.prefixTextView.setId(R.id.textinput_prefix_text);
        this.prefixTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        ViewCompat.setAccessibilityLiveRegion(this.prefixTextView, 1);
        setPrefixTextAppearance(a.getResourceId(R.styleable.TextInputLayout_prefixTextAppearance, 0));
        if (a.hasValue(R.styleable.TextInputLayout_prefixTextColor)) {
            setPrefixTextColor(a.getColorStateList(R.styleable.TextInputLayout_prefixTextColor));
        }
        setPrefixText(a.getText(R.styleable.TextInputLayout_prefixText));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updatePrefixTextViewPadding();
    }

    TextView getPrefixTextView() {
        return this.prefixTextView;
    }

    void setPrefixText(CharSequence prefixText) {
        this.prefixText = TextUtils.isEmpty(prefixText) ? null : prefixText;
        this.prefixTextView.setText(prefixText);
        updateVisibility();
    }

    CharSequence getPrefixText() {
        return this.prefixText;
    }

    void setPrefixTextColor(ColorStateList prefixTextColor) {
        this.prefixTextView.setTextColor(prefixTextColor);
    }

    ColorStateList getPrefixTextColor() {
        return this.prefixTextView.getTextColors();
    }

    void setPrefixTextAppearance(int prefixTextAppearance) {
        TextViewCompat.setTextAppearance(this.prefixTextView, prefixTextAppearance);
    }

    void setStartIconDrawable(Drawable startIconDrawable) {
        this.startIconView.setImageDrawable(startIconDrawable);
        if (startIconDrawable != null) {
            IconHelper.applyIconTint(this.textInputLayout, this.startIconView, this.startIconTintList, this.startIconTintMode);
            setStartIconVisible(true);
            refreshStartIconDrawableState();
        } else {
            setStartIconVisible(false);
            setStartIconOnClickListener(null);
            setStartIconOnLongClickListener(null);
            setStartIconContentDescription(null);
        }
    }

    Drawable getStartIconDrawable() {
        return this.startIconView.getDrawable();
    }

    void setStartIconOnClickListener(View.OnClickListener startIconOnClickListener) {
        IconHelper.setIconOnClickListener(this.startIconView, startIconOnClickListener, this.startIconOnLongClickListener);
    }

    void setStartIconOnLongClickListener(View.OnLongClickListener startIconOnLongClickListener) {
        this.startIconOnLongClickListener = startIconOnLongClickListener;
        IconHelper.setIconOnLongClickListener(this.startIconView, startIconOnLongClickListener);
    }

    void setStartIconVisible(boolean visible) {
        if (isStartIconVisible() != visible) {
            this.startIconView.setVisibility(visible ? 0 : 8);
            updatePrefixTextViewPadding();
            updateVisibility();
        }
    }

    boolean isStartIconVisible() {
        return this.startIconView.getVisibility() == 0;
    }

    void refreshStartIconDrawableState() {
        IconHelper.refreshIconDrawableState(this.textInputLayout, this.startIconView, this.startIconTintList);
    }

    void setStartIconCheckable(boolean startIconCheckable) {
        this.startIconView.setCheckable(startIconCheckable);
    }

    boolean isStartIconCheckable() {
        return this.startIconView.isCheckable();
    }

    void setStartIconContentDescription(CharSequence startIconContentDescription) {
        if (getStartIconContentDescription() != startIconContentDescription) {
            this.startIconView.setContentDescription(startIconContentDescription);
        }
    }

    CharSequence getStartIconContentDescription() {
        return this.startIconView.getContentDescription();
    }

    void setStartIconTintList(ColorStateList startIconTintList) {
        if (this.startIconTintList != startIconTintList) {
            this.startIconTintList = startIconTintList;
            IconHelper.applyIconTint(this.textInputLayout, this.startIconView, startIconTintList, this.startIconTintMode);
        }
    }

    void setStartIconTintMode(PorterDuff.Mode startIconTintMode) {
        if (this.startIconTintMode != startIconTintMode) {
            this.startIconTintMode = startIconTintMode;
            IconHelper.applyIconTint(this.textInputLayout, this.startIconView, this.startIconTintList, this.startIconTintMode);
        }
    }

    void setStartIconMinSize(int iconSize) {
        if (iconSize < 0) {
            throw new IllegalArgumentException("startIconSize cannot be less than 0");
        }
        if (iconSize != this.startIconMinSize) {
            this.startIconMinSize = iconSize;
            IconHelper.setIconMinSize(this.startIconView, iconSize);
        }
    }

    int getStartIconMinSize() {
        return this.startIconMinSize;
    }

    void setStartIconScaleType(ImageView.ScaleType startIconScaleType) {
        this.startIconScaleType = startIconScaleType;
        IconHelper.setIconScaleType(this.startIconView, startIconScaleType);
    }

    ImageView.ScaleType getStartIconScaleType() {
        return this.startIconScaleType;
    }

    void setupAccessibilityNodeInfo(AccessibilityNodeInfoCompat info) {
        if (this.prefixTextView.getVisibility() == 0) {
            info.setLabelFor(this.prefixTextView);
            info.setTraversalAfter(this.prefixTextView);
        } else {
            info.setTraversalAfter(this.startIconView);
        }
    }

    void updatePrefixTextViewPadding() {
        EditText editText = this.textInputLayout.editText;
        if (editText == null) {
            return;
        }
        int startPadding = isStartIconVisible() ? 0 : ViewCompat.getPaddingStart(editText);
        ViewCompat.setPaddingRelative(this.prefixTextView, startPadding, editText.getCompoundPaddingTop(), getContext().getResources().getDimensionPixelSize(R.dimen.material_input_text_to_prefix_suffix_padding), editText.getCompoundPaddingBottom());
    }

    void onHintStateChanged(boolean hintExpanded) {
        this.hintExpanded = hintExpanded;
        updateVisibility();
    }

    private void updateVisibility() {
        int prefixTextVisibility = (this.prefixText == null || this.hintExpanded) ? 8 : 0;
        boolean shouldBeVisible = this.startIconView.getVisibility() == 0 || prefixTextVisibility == 0;
        setVisibility(shouldBeVisible ? 0 : 8);
        this.prefixTextView.setVisibility(prefixTextVisibility);
        this.textInputLayout.updateDummyDrawables();
    }
}

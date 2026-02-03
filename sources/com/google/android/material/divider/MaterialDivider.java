package com.google.android.material.divider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

/* loaded from: classes.dex */
public class MaterialDivider extends View {
    private static final int DEF_STYLE_RES = R.style.Widget_MaterialComponents_MaterialDivider;
    private int color;
    private final MaterialShapeDrawable dividerDrawable;
    private int insetEnd;
    private int insetStart;
    private int thickness;

    public MaterialDivider(Context context) {
        this(context, null);
    }

    public MaterialDivider(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialDividerStyle);
    }

    public MaterialDivider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
        Context context2 = getContext();
        this.dividerDrawable = new MaterialShapeDrawable();
        TypedArray attributes = ThemeEnforcement.obtainStyledAttributes(context2, attrs, R.styleable.MaterialDivider, defStyleAttr, DEF_STYLE_RES, new int[0]);
        this.thickness = attributes.getDimensionPixelSize(R.styleable.MaterialDivider_dividerThickness, getResources().getDimensionPixelSize(R.dimen.material_divider_thickness));
        this.insetStart = attributes.getDimensionPixelOffset(R.styleable.MaterialDivider_dividerInsetStart, 0);
        this.insetEnd = attributes.getDimensionPixelOffset(R.styleable.MaterialDivider_dividerInsetEnd, 0);
        setDividerColor(MaterialResources.getColorStateList(context2, attributes, R.styleable.MaterialDivider_dividerColor).getDefaultColor());
        attributes.recycle();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int newThickness = getMeasuredHeight();
        if (heightMode == Integer.MIN_VALUE || heightMode == 0) {
            if (this.thickness > 0 && newThickness != this.thickness) {
                newThickness = this.thickness;
            }
            setMeasuredDimension(getMeasuredWidth(), newThickness);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
        int left = isRtl ? this.insetEnd : this.insetStart;
        int right = getWidth() - (isRtl ? this.insetStart : this.insetEnd);
        this.dividerDrawable.setBounds(left, 0, right, getBottom() - getTop());
        this.dividerDrawable.draw(canvas);
    }

    public void setDividerThickness(int thickness) {
        if (this.thickness != thickness) {
            this.thickness = thickness;
            requestLayout();
        }
    }

    public void setDividerThicknessResource(int thicknessId) {
        setDividerThickness(getContext().getResources().getDimensionPixelSize(thicknessId));
    }

    public int getDividerThickness() {
        return this.thickness;
    }

    public void setDividerInsetStart(int insetStart) {
        this.insetStart = insetStart;
    }

    public void setDividerInsetStartResource(int insetStartId) {
        setDividerInsetStart(getContext().getResources().getDimensionPixelOffset(insetStartId));
    }

    public int getDividerInsetStart() {
        return this.insetStart;
    }

    public void setDividerInsetEnd(int insetEnd) {
        this.insetEnd = insetEnd;
    }

    public void setDividerInsetEndResource(int insetEndId) {
        setDividerInsetEnd(getContext().getResources().getDimensionPixelOffset(insetEndId));
    }

    public int getDividerInsetEnd() {
        return this.insetEnd;
    }

    public void setDividerColor(int color) {
        if (this.color != color) {
            this.color = color;
            this.dividerDrawable.setFillColor(ColorStateList.valueOf(color));
            invalidate();
        }
    }

    public void setDividerColorResource(int colorId) {
        setDividerColor(ContextCompat.getColor(getContext(), colorId));
    }

    public int getDividerColor() {
        return this.color;
    }
}

package com.google.android.material.timepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RelativeCornerSize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class RadialViewGroup extends ConstraintLayout {
    static final int LEVEL_1 = 1;
    static final int LEVEL_2 = 2;
    static final float LEVEL_RADIUS_RATIO = 0.66f;
    private static final String SKIP_TAG = "skip";
    private MaterialShapeDrawable background;
    private int radius;
    private final Runnable updateLayoutParametersRunnable;

    public RadialViewGroup(Context context) {
        this(context, null);
    }

    public RadialViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.material_radial_view_group, this);
        ViewCompat.setBackground(this, createBackground());
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadialViewGroup, defStyleAttr, 0);
        this.radius = a.getDimensionPixelSize(R.styleable.RadialViewGroup_materialCircleRadius, 0);
        this.updateLayoutParametersRunnable = new Runnable() { // from class: com.google.android.material.timepicker.RadialViewGroup$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateLayoutParams();
            }
        };
        a.recycle();
    }

    private Drawable createBackground() {
        this.background = new MaterialShapeDrawable();
        this.background.setCornerSize(new RelativeCornerSize(0.5f));
        this.background.setFillColor(ColorStateList.valueOf(-1));
        return this.background;
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.background.setFillColor(ColorStateList.valueOf(color));
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child.getId() == -1) {
            child.setId(ViewCompat.generateViewId());
        }
        updateLayoutParamsAsync();
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.ViewGroup
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        updateLayoutParamsAsync();
    }

    private void updateLayoutParamsAsync() {
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeCallbacks(this.updateLayoutParametersRunnable);
            handler.post(this.updateLayoutParametersRunnable);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateLayoutParams();
    }

    protected void updateLayoutParams() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        Map<Integer, List<View>> levels = new HashMap<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getId() != R.id.circle_center && !shouldSkipView(childAt)) {
                int level = (Integer) childAt.getTag(R.id.material_clock_level);
                if (level == null) {
                    level = 1;
                }
                if (!levels.containsKey(level)) {
                    levels.put(level, new ArrayList<>());
                }
                levels.get(level).add(childAt);
            }
        }
        for (Map.Entry<Integer, List<View>> entry : levels.entrySet()) {
            addConstraints(entry.getValue(), constraintSet, getLeveledRadius(entry.getKey().intValue()));
        }
        constraintSet.applyTo(this);
    }

    private void addConstraints(List<View> views, ConstraintSet constraintSet, int leveledRadius) {
        float currentAngle = 0.0f;
        for (View view : views) {
            constraintSet.constrainCircle(view.getId(), R.id.circle_center, leveledRadius, currentAngle);
            currentAngle += 360.0f / views.size();
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updateLayoutParams();
    }

    public int getRadius() {
        return this.radius;
    }

    int getLeveledRadius(int level) {
        return level == 2 ? Math.round(this.radius * LEVEL_RADIUS_RATIO) : this.radius;
    }

    private static boolean shouldSkipView(View child) {
        return SKIP_TAG.equals(child.getTag());
    }
}

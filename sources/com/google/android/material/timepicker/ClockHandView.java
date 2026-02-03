package com.google.android.material.timepicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.math.MathUtils;
import com.google.android.material.motion.MotionUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class ClockHandView extends View {
    private static final int DEFAULT_ANIMATION_DURATION = 200;
    private boolean animatingOnTouchUp;
    private final int animationDuration;
    private final TimeInterpolator animationInterpolator;
    private final float centerDotRadius;
    private boolean changedDuringTouch;
    private int circleRadius;
    private int currentLevel;
    private double degRad;
    private float downX;
    private float downY;
    private boolean isInTapRegion;
    private boolean isMultiLevel;
    private final List<OnRotateListener> listeners;
    private OnActionUpListener onActionUpListener;
    private float originalDeg;
    private final Paint paint;
    private final ValueAnimator rotationAnimator;
    private final int scaledTouchSlop;
    private final RectF selectorBox;
    private final int selectorRadius;
    private final int selectorStrokeWidth;

    public interface OnActionUpListener {
        void onActionUp(float f, boolean z);
    }

    public interface OnRotateListener {
        void onRotate(float f, boolean z);
    }

    public ClockHandView(Context context) {
        this(context, null);
    }

    public ClockHandView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialClockStyle);
    }

    public ClockHandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.rotationAnimator = new ValueAnimator();
        this.listeners = new ArrayList();
        this.paint = new Paint();
        this.selectorBox = new RectF();
        this.currentLevel = 1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockHandView, defStyleAttr, R.style.Widget_MaterialComponents_TimePicker_Clock);
        this.animationDuration = MotionUtils.resolveThemeDuration(context, R.attr.motionDurationLong2, 200);
        this.animationInterpolator = MotionUtils.resolveThemeInterpolator(context, R.attr.motionEasingEmphasizedInterpolator, AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.circleRadius = a.getDimensionPixelSize(R.styleable.ClockHandView_materialCircleRadius, 0);
        this.selectorRadius = a.getDimensionPixelSize(R.styleable.ClockHandView_selectorSize, 0);
        Resources res = getResources();
        this.selectorStrokeWidth = res.getDimensionPixelSize(R.dimen.material_clock_hand_stroke_width);
        this.centerDotRadius = res.getDimensionPixelSize(R.dimen.material_clock_hand_center_dot_radius);
        int selectorColor = a.getColor(R.styleable.ClockHandView_clockHandColor, 0);
        this.paint.setAntiAlias(true);
        this.paint.setColor(selectorColor);
        setHandRotation(0.0f);
        this.scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        ViewCompat.setImportantForAccessibility(this, 2);
        a.recycle();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!this.rotationAnimator.isRunning()) {
            setHandRotation(getHandRotation());
        }
    }

    public void setHandRotation(float degrees) {
        setHandRotation(degrees, false);
    }

    public void setHandRotation(float degrees, boolean animate) {
        if (this.rotationAnimator != null) {
            this.rotationAnimator.cancel();
        }
        if (!animate) {
            setHandRotationInternal(degrees, false);
            return;
        }
        Pair<Float, Float> animationValues = getValuesForAnimation(degrees);
        this.rotationAnimator.setFloatValues(((Float) animationValues.first).floatValue(), ((Float) animationValues.second).floatValue());
        this.rotationAnimator.setDuration(this.animationDuration);
        this.rotationAnimator.setInterpolator(this.animationInterpolator);
        this.rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.timepicker.ClockHandView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.m92xb17f7076(valueAnimator);
            }
        });
        this.rotationAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.timepicker.ClockHandView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                animation.end();
            }
        });
        this.rotationAnimator.start();
    }

    /* renamed from: lambda$setHandRotation$0$com-google-android-material-timepicker-ClockHandView, reason: not valid java name */
    /* synthetic */ void m92xb17f7076(ValueAnimator animation) {
        float animatedValue = ((Float) animation.getAnimatedValue()).floatValue();
        setHandRotationInternal(animatedValue, true);
    }

    private Pair<Float, Float> getValuesForAnimation(float degrees) {
        float currentDegrees = getHandRotation();
        if (Math.abs(currentDegrees - degrees) > 180.0f) {
            if (currentDegrees > 180.0f && degrees < 180.0f) {
                degrees += 360.0f;
            }
            if (currentDegrees < 180.0f && degrees > 180.0f) {
                currentDegrees += 360.0f;
            }
        }
        return new Pair<>(Float.valueOf(currentDegrees), Float.valueOf(degrees));
    }

    private void setHandRotationInternal(float degrees, boolean animate) {
        float degrees2 = degrees % 360.0f;
        this.originalDeg = degrees2;
        float angDeg = this.originalDeg - 90.0f;
        this.degRad = Math.toRadians(angDeg);
        int yCenter = getHeight() / 2;
        int xCenter = getWidth() / 2;
        int leveledCircleRadius = getLeveledCircleRadius(this.currentLevel);
        float selCenterX = xCenter + (leveledCircleRadius * ((float) Math.cos(this.degRad)));
        float selCenterY = yCenter + (leveledCircleRadius * ((float) Math.sin(this.degRad)));
        this.selectorBox.set(selCenterX - this.selectorRadius, selCenterY - this.selectorRadius, this.selectorRadius + selCenterX, this.selectorRadius + selCenterY);
        for (OnRotateListener listener : this.listeners) {
            listener.onRotate(degrees2, animate);
        }
        invalidate();
    }

    public void setAnimateOnTouchUp(boolean animating) {
        this.animatingOnTouchUp = animating;
    }

    public void addOnRotateListener(OnRotateListener listener) {
        this.listeners.add(listener);
    }

    public void setOnActionUpListener(OnActionUpListener listener) {
        this.onActionUpListener = listener;
    }

    public float getHandRotation() {
        return this.originalDeg;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSelector(canvas);
    }

    private void drawSelector(Canvas canvas) {
        int yCenter = getHeight() / 2;
        int xCenter = getWidth() / 2;
        int leveledCircleRadius = getLeveledCircleRadius(this.currentLevel);
        float selCenterX = xCenter + (leveledCircleRadius * ((float) Math.cos(this.degRad)));
        float selCenterY = yCenter + (leveledCircleRadius * ((float) Math.sin(this.degRad)));
        this.paint.setStrokeWidth(0.0f);
        canvas.drawCircle(selCenterX, selCenterY, this.selectorRadius, this.paint);
        double sin = Math.sin(this.degRad);
        double cos = Math.cos(this.degRad);
        float lineLength = leveledCircleRadius - this.selectorRadius;
        float linePointX = ((int) (lineLength * cos)) + xCenter;
        float linePointY = ((int) (lineLength * sin)) + yCenter;
        this.paint.setStrokeWidth(this.selectorStrokeWidth);
        canvas.drawLine(xCenter, yCenter, linePointX, linePointY, this.paint);
        canvas.drawCircle(xCenter, yCenter, this.centerDotRadius, this.paint);
    }

    public RectF getCurrentSelectorBox() {
        return this.selectorBox;
    }

    public int getSelectorRadius() {
        return this.selectorRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        boolean forceSelection = false;
        boolean actionDown = false;
        boolean actionUp = false;
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case 0:
                this.downX = x;
                this.downY = y;
                this.isInTapRegion = true;
                this.changedDuringTouch = false;
                actionDown = true;
                break;
            case 1:
            case 2:
                int deltaX = (int) (x - this.downX);
                int deltaY = (int) (y - this.downY);
                int distance = (deltaX * deltaX) + (deltaY * deltaY);
                this.isInTapRegion = distance > this.scaledTouchSlop;
                if (this.changedDuringTouch) {
                    forceSelection = true;
                }
                actionUp = action == 1;
                if (this.isMultiLevel) {
                    adjustLevel(x, y);
                    break;
                }
                break;
        }
        this.changedDuringTouch = handleTouchInput(x, y, forceSelection, actionDown, actionUp) | this.changedDuringTouch;
        if (this.changedDuringTouch && actionUp && this.onActionUpListener != null) {
            this.onActionUpListener.onActionUp(getDegreesFromXY(x, y), this.isInTapRegion);
        }
        return true;
    }

    private void adjustLevel(float x, float y) {
        int xCenter = getWidth() / 2;
        int yCenter = getHeight() / 2;
        float selectionRadius = MathUtils.dist(xCenter, yCenter, x, y);
        int level2CircleRadius = getLeveledCircleRadius(2);
        float buffer = ViewUtils.dpToPx(getContext(), 12);
        this.currentLevel = selectionRadius > ((float) level2CircleRadius) + buffer ? 1 : 2;
    }

    private boolean handleTouchInput(float x, float y, boolean forceSelection, boolean touchDown, boolean actionUp) {
        int degrees = getDegreesFromXY(x, y);
        boolean z = false;
        boolean valueChanged = getHandRotation() != ((float) degrees);
        if (touchDown && valueChanged) {
            return true;
        }
        if (!valueChanged && !forceSelection) {
            return false;
        }
        float f = degrees;
        if (actionUp && this.animatingOnTouchUp) {
            z = true;
        }
        setHandRotation(f, z);
        return true;
    }

    private int getDegreesFromXY(float x, float y) {
        int xCenter = getWidth() / 2;
        int yCenter = getHeight() / 2;
        double dX = x - xCenter;
        double dY = y - yCenter;
        int degrees = ((int) Math.toDegrees(Math.atan2(dY, dX))) + 90;
        if (degrees < 0) {
            return degrees + 360;
        }
        return degrees;
    }

    int getCurrentLevel() {
        return this.currentLevel;
    }

    void setCurrentLevel(int level) {
        this.currentLevel = level;
        invalidate();
    }

    void setMultiLevel(boolean isMultiLevel) {
        if (this.isMultiLevel && !isMultiLevel) {
            this.currentLevel = 1;
        }
        this.isMultiLevel = isMultiLevel;
        invalidate();
    }

    private int getLeveledCircleRadius(int level) {
        return level == 2 ? Math.round(this.circleRadius * 0.66f) : this.circleRadius;
    }
}

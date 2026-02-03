package androidx.appcompat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import androidx.appcompat.R;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class LinearLayoutCompat extends ViewGroup {
    private static final String ACCESSIBILITY_CLASS_NAME = "androidx.appcompat.widget.LinearLayoutCompat";
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.LinearLayoutCompat, defStyleAttr, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R.styleable.LinearLayoutCompat, attrs, a.getWrappedTypeArray(), defStyleAttr, 0);
        int index = a.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        int index2 = a.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        boolean baselineAligned = a.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        this.mWeightSum = a.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = a.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = a.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(a.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = a.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = a.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        a.recycle();
    }

    public void setShowDividers(int showDividers) {
        if (showDividers != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = showDividers;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider == this.mDivider) {
            return;
        }
        this.mDivider = divider;
        if (divider != null) {
            this.mDividerWidth = divider.getIntrinsicWidth();
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        setWillNotDraw(divider == null);
        requestLayout();
    }

    public void setDividerPadding(int padding) {
        this.mDividerPadding = padding;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int bottom;
        int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int top = (child.getTop() - lp.topMargin) - this.mDividerHeight;
                drawHorizontalDivider(canvas, top);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 == null) {
                bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                int bottom2 = child2.getBottom() + lp2.bottomMargin;
                bottom = bottom2;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        int position;
        int position2;
        int count = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position2 = child.getRight() + lp.rightMargin;
                } else {
                    int position3 = child.getLeft();
                    position2 = (position3 - lp.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, position2);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    int position4 = getWidth();
                    position = (position4 - getPaddingRight()) - this.mDividerWidth;
                }
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                if (isLayoutRtl) {
                    position = (child2.getLeft() - lp2.leftMargin) - this.mDividerWidth;
                } else {
                    int position5 = child2.getRight();
                    position = position5 + lp2.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, top, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + top);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + left, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public void setBaselineAligned(boolean baselineAligned) {
        this.mBaselineAligned = baselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        this.mUseLargestChild = enabled;
    }

    @Override // android.view.View
    public int getBaseline() {
        int majorGravity;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() <= this.mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View child = getChildAt(this.mBaselineAlignedChildIndex);
        int childBaseline = child.getBaseline();
        if (childBaseline == -1) {
            if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            }
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        int childTop = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (majorGravity = this.mGravity & 112) != 48) {
            switch (majorGravity) {
                case 16:
                    childTop += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                    break;
                case 80:
                    childTop = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                    break;
            }
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.topMargin + childTop + childBaseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
        }
        this.mBaselineAlignedChildIndex = i;
    }

    View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float weightSum) {
        this.mWeightSum = Math.max(0.0f, weightSum);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOrientation == 1) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (this.mShowDividers & 1) != 0;
        }
        if (childIndex == getChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        if ((this.mShowDividers & 2) == 0) {
            return false;
        }
        for (int i = childIndex - 1; i >= 0; i--) {
            if (getChildAt(i).getVisibility() != 8) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:163:0x03dc  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03de  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x0467  */
    /* JADX WARN: Removed duplicated region for block: B:199:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0186  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0199  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int childState;
        float totalWeight;
        int delta;
        int i;
        int weightedMaxWidth;
        int delta2;
        int heightMode;
        int baselineChildIndex;
        int delta3;
        int alternativeMaxWidth;
        int delta4;
        int alternativeMaxWidth2;
        int heightSize;
        int delta5;
        int childState2;
        int i2;
        int i3;
        int oldHeight;
        int i4;
        int weightedMaxWidth2;
        int alternativeMaxWidth3;
        int childState3;
        LayoutParams lp;
        View child;
        int largestChildHeight;
        int i5;
        int largestChildHeight2;
        int childState4;
        int weightedMaxWidth3;
        int alternativeMaxWidth4;
        this.mTotalLength = 0;
        int count = getVirtualChildCount();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int baselineChildIndex2 = this.mBaselineAlignedChildIndex;
        boolean useLargestChild = this.mUseLargestChild;
        boolean skippedMeasure = false;
        int maxWidth = 0;
        float totalWeight2 = 0.0f;
        int alternativeMaxWidth5 = 0;
        int alternativeMaxWidth6 = 0;
        boolean matchWidth = false;
        int weightedMaxWidth4 = 0;
        int margin = 0;
        int weightedMaxWidth5 = 0;
        int largestChildHeight3 = 1;
        while (true) {
            int weightedMaxWidth6 = margin;
            if (alternativeMaxWidth6 < count) {
                View child2 = getVirtualChildAt(alternativeMaxWidth6);
                if (child2 == null) {
                    this.mTotalLength += measureNullChild(alternativeMaxWidth6);
                    margin = weightedMaxWidth6;
                } else {
                    int largestChildHeight4 = weightedMaxWidth5;
                    int largestChildHeight5 = child2.getVisibility();
                    if (largestChildHeight5 == 8) {
                        alternativeMaxWidth6 += getChildrenSkipCount(child2, alternativeMaxWidth6);
                        margin = weightedMaxWidth6;
                        weightedMaxWidth5 = largestChildHeight4;
                    } else {
                        if (hasDividerBeforeChildAt(alternativeMaxWidth6)) {
                            this.mTotalLength += this.mDividerHeight;
                        }
                        LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                        float totalWeight3 = totalWeight2 + lp2.weight;
                        if (heightMode2 != 1073741824 || lp2.height != 0 || lp2.weight <= 0.0f) {
                            int i6 = alternativeMaxWidth6;
                            if (lp2.height == 0 && lp2.weight > 0.0f) {
                                lp2.height = -2;
                                oldHeight = 0;
                            } else {
                                oldHeight = Integer.MIN_VALUE;
                            }
                            int oldHeight2 = oldHeight;
                            i4 = i6;
                            weightedMaxWidth2 = weightedMaxWidth6;
                            alternativeMaxWidth3 = alternativeMaxWidth5;
                            childState3 = weightedMaxWidth4;
                            measureChildBeforeLayout(child2, i4, widthMeasureSpec, 0, heightMeasureSpec, totalWeight3 == 0.0f ? this.mTotalLength : 0);
                            if (oldHeight2 == Integer.MIN_VALUE) {
                                lp = lp2;
                            } else {
                                lp = lp2;
                                lp.height = oldHeight2;
                            }
                            int childHeight = child2.getMeasuredHeight();
                            int totalLength = this.mTotalLength;
                            child = child2;
                            this.mTotalLength = Math.max(totalLength, totalLength + childHeight + lp.topMargin + lp.bottomMargin + getNextLocationOffset(child));
                            if (!useLargestChild) {
                                largestChildHeight = largestChildHeight4;
                            } else {
                                largestChildHeight = Math.max(childHeight, largestChildHeight4);
                            }
                        } else {
                            int totalLength2 = this.mTotalLength;
                            int i7 = lp2.topMargin + totalLength2;
                            int i8 = alternativeMaxWidth6;
                            int i9 = lp2.bottomMargin;
                            this.mTotalLength = Math.max(totalLength2, i7 + i9);
                            skippedMeasure = true;
                            lp = lp2;
                            alternativeMaxWidth3 = alternativeMaxWidth5;
                            childState3 = weightedMaxWidth4;
                            weightedMaxWidth2 = weightedMaxWidth6;
                            largestChildHeight = largestChildHeight4;
                            i4 = i8;
                            child = child2;
                        }
                        if (baselineChildIndex2 >= 0) {
                            i5 = i4;
                            if (baselineChildIndex2 == i5 + 1) {
                                this.mBaselineChildTop = this.mTotalLength;
                            }
                        } else {
                            i5 = i4;
                        }
                        if (i5 < baselineChildIndex2 && lp.weight > 0.0f) {
                            throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                        }
                        boolean matchWidthLocally = false;
                        if (widthMode != 1073741824 && lp.width == -1) {
                            matchWidth = true;
                            matchWidthLocally = true;
                        }
                        int margin2 = lp.leftMargin + lp.rightMargin;
                        int measuredWidth = child.getMeasuredWidth() + margin2;
                        maxWidth = Math.max(maxWidth, measuredWidth);
                        int childState5 = View.combineMeasuredStates(childState3, child.getMeasuredState());
                        if (largestChildHeight3 != 0) {
                            largestChildHeight2 = largestChildHeight;
                            int largestChildHeight6 = lp.width == -1 ? 1 : 0;
                            if (lp.weight <= 0.0f) {
                                childState4 = childState5;
                                weightedMaxWidth3 = Math.max(weightedMaxWidth2, matchWidthLocally ? margin2 : measuredWidth);
                                alternativeMaxWidth4 = alternativeMaxWidth3;
                            } else {
                                childState4 = childState5;
                                weightedMaxWidth3 = weightedMaxWidth2;
                                alternativeMaxWidth4 = Math.max(alternativeMaxWidth3, matchWidthLocally ? margin2 : measuredWidth);
                            }
                            alternativeMaxWidth6 = i5 + getChildrenSkipCount(child, i5);
                            alternativeMaxWidth5 = alternativeMaxWidth4;
                            largestChildHeight3 = largestChildHeight6;
                            margin = weightedMaxWidth3;
                            weightedMaxWidth5 = largestChildHeight2;
                            weightedMaxWidth4 = childState4;
                            totalWeight2 = totalWeight3;
                        } else {
                            largestChildHeight2 = largestChildHeight;
                        }
                        if (lp.weight <= 0.0f) {
                        }
                        alternativeMaxWidth6 = i5 + getChildrenSkipCount(child, i5);
                        alternativeMaxWidth5 = alternativeMaxWidth4;
                        largestChildHeight3 = largestChildHeight6;
                        margin = weightedMaxWidth3;
                        weightedMaxWidth5 = largestChildHeight2;
                        weightedMaxWidth4 = childState4;
                        totalWeight2 = totalWeight3;
                    }
                }
                alternativeMaxWidth6++;
            } else {
                int largestChildHeight7 = weightedMaxWidth5;
                int childState6 = weightedMaxWidth4;
                int weightedMaxWidth7 = weightedMaxWidth6;
                int alternativeMaxWidth7 = alternativeMaxWidth5;
                int i10 = this.mTotalLength;
                if (i10 > 0 && hasDividerBeforeChildAt(count)) {
                    this.mTotalLength += this.mDividerHeight;
                }
                if (!useLargestChild) {
                    childState = childState6;
                } else if (heightMode2 == Integer.MIN_VALUE || heightMode2 == 0) {
                    this.mTotalLength = 0;
                    int i11 = 0;
                    while (i11 < count) {
                        View child3 = getVirtualChildAt(i11);
                        if (child3 == null) {
                            this.mTotalLength += measureNullChild(i11);
                            i2 = i11;
                            childState2 = childState6;
                        } else {
                            childState2 = childState6;
                            if (child3.getVisibility() == 8) {
                                i3 = i11 + getChildrenSkipCount(child3, i11);
                                i11 = i3 + 1;
                                childState6 = childState2;
                            } else {
                                LayoutParams lp3 = (LayoutParams) child3.getLayoutParams();
                                int totalLength3 = this.mTotalLength;
                                i2 = i11;
                                int i12 = lp3.topMargin;
                                this.mTotalLength = Math.max(totalLength3, totalLength3 + largestChildHeight7 + i12 + lp3.bottomMargin + getNextLocationOffset(child3));
                            }
                        }
                        i3 = i2;
                        i11 = i3 + 1;
                        childState6 = childState2;
                    }
                    childState = childState6;
                } else {
                    childState = childState6;
                }
                this.mTotalLength += getPaddingTop() + getPaddingBottom();
                int heightSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), heightMeasureSpec, 0);
                int heightSize2 = heightSizeAndState & ViewCompat.MEASURED_SIZE_MASK;
                int delta6 = heightSize2 - this.mTotalLength;
                if (skippedMeasure) {
                    totalWeight = totalWeight2;
                    delta = delta6;
                } else {
                    if (delta6 == 0 || totalWeight2 <= 0.0f) {
                        int alternativeMaxWidth8 = Math.max(alternativeMaxWidth7, weightedMaxWidth7);
                        if (!useLargestChild || heightMode2 == 1073741824) {
                            alternativeMaxWidth = alternativeMaxWidth8;
                            delta4 = delta6;
                        } else {
                            int i13 = 0;
                            while (i13 < count) {
                                float totalWeight4 = totalWeight2;
                                View child4 = getVirtualChildAt(i13);
                                if (child4 != null) {
                                    alternativeMaxWidth2 = alternativeMaxWidth8;
                                    int alternativeMaxWidth9 = child4.getVisibility();
                                    heightSize = heightSize2;
                                    if (alternativeMaxWidth9 == 8) {
                                        delta5 = delta6;
                                    } else if (((LayoutParams) child4.getLayoutParams()).weight > 0.0f) {
                                        delta5 = delta6;
                                        child4.measure(View.MeasureSpec.makeMeasureSpec(child4.getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(largestChildHeight7, BasicMeasure.EXACTLY));
                                    } else {
                                        delta5 = delta6;
                                    }
                                } else {
                                    alternativeMaxWidth2 = alternativeMaxWidth8;
                                    heightSize = heightSize2;
                                    delta5 = delta6;
                                }
                                i13++;
                                alternativeMaxWidth8 = alternativeMaxWidth2;
                                totalWeight2 = totalWeight4;
                                heightSize2 = heightSize;
                                delta6 = delta5;
                            }
                            alternativeMaxWidth = alternativeMaxWidth8;
                            delta4 = delta6;
                        }
                        delta2 = alternativeMaxWidth;
                        weightedMaxWidth = childState;
                        i = widthMeasureSpec;
                        if (largestChildHeight3 == 0 && widthMode != 1073741824) {
                            maxWidth = delta2;
                        }
                        setMeasuredDimension(View.resolveSizeAndState(Math.max(maxWidth + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, weightedMaxWidth), heightSizeAndState);
                        if (!matchWidth) {
                            forceUniformWidth(count, heightMeasureSpec);
                            return;
                        }
                        return;
                    }
                    totalWeight = totalWeight2;
                    delta = delta6;
                }
                float totalWeight5 = this.mWeightSum;
                float weightSum = totalWeight5 > 0.0f ? this.mWeightSum : totalWeight;
                this.mTotalLength = 0;
                int i14 = 0;
                int alternativeMaxWidth10 = alternativeMaxWidth7;
                int maxWidth2 = maxWidth;
                int alternativeMaxWidth11 = delta;
                int maxWidth3 = childState;
                while (i14 < count) {
                    int largestChildHeight8 = largestChildHeight7;
                    View child5 = getVirtualChildAt(i14);
                    int weightedMaxWidth8 = weightedMaxWidth7;
                    int weightedMaxWidth9 = child5.getVisibility();
                    boolean useLargestChild2 = useLargestChild;
                    if (weightedMaxWidth9 == 8) {
                        heightMode = heightMode2;
                        baselineChildIndex = baselineChildIndex2;
                    } else {
                        LayoutParams lp4 = (LayoutParams) child5.getLayoutParams();
                        float childExtra = lp4.weight;
                        if (childExtra > 0.0f) {
                            baselineChildIndex = baselineChildIndex2;
                            int share = (int) ((alternativeMaxWidth11 * childExtra) / weightSum);
                            float weightSum2 = weightSum - childExtra;
                            int delta7 = alternativeMaxWidth11 - share;
                            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp4.leftMargin + lp4.rightMargin, lp4.width);
                            if (lp4.height != 0 || heightMode2 != 1073741824) {
                                heightMode = heightMode2;
                                int childHeight2 = child5.getMeasuredHeight() + share;
                                if (childHeight2 < 0) {
                                    childHeight2 = 0;
                                }
                                child5.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(childHeight2, BasicMeasure.EXACTLY));
                                maxWidth3 = View.combineMeasuredStates(maxWidth3, child5.getMeasuredState() & InputDeviceCompat.SOURCE_ANY);
                                alternativeMaxWidth11 = delta7;
                                weightSum = weightSum2;
                            } else {
                                heightMode = heightMode2;
                                child5.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(share > 0 ? share : 0, BasicMeasure.EXACTLY));
                                maxWidth3 = View.combineMeasuredStates(maxWidth3, child5.getMeasuredState() & InputDeviceCompat.SOURCE_ANY);
                                alternativeMaxWidth11 = delta7;
                                weightSum = weightSum2;
                            }
                        } else {
                            heightMode = heightMode2;
                            baselineChildIndex = baselineChildIndex2;
                        }
                        int margin3 = lp4.leftMargin + lp4.rightMargin;
                        int measuredWidth2 = child5.getMeasuredWidth() + margin3;
                        maxWidth2 = Math.max(maxWidth2, measuredWidth2);
                        float weightSum3 = weightSum;
                        if (widthMode != 1073741824) {
                            delta3 = alternativeMaxWidth11;
                            boolean matchWidthLocally2 = lp4.width == -1;
                            int alternativeMaxWidth12 = Math.max(alternativeMaxWidth10, !matchWidthLocally2 ? margin3 : measuredWidth2);
                            int i15 = (largestChildHeight3 == 0 && lp4.width == -1) ? 1 : 0;
                            int totalLength4 = this.mTotalLength;
                            int measuredHeight = totalLength4 + child5.getMeasuredHeight();
                            int alternativeMaxWidth13 = lp4.topMargin;
                            this.mTotalLength = Math.max(totalLength4, measuredHeight + alternativeMaxWidth13 + lp4.bottomMargin + getNextLocationOffset(child5));
                            largestChildHeight3 = i15;
                            weightSum = weightSum3;
                            alternativeMaxWidth11 = delta3;
                            alternativeMaxWidth10 = alternativeMaxWidth12;
                        } else {
                            delta3 = alternativeMaxWidth11;
                        }
                        int alternativeMaxWidth122 = Math.max(alternativeMaxWidth10, !matchWidthLocally2 ? margin3 : measuredWidth2);
                        if (largestChildHeight3 == 0) {
                        }
                        int totalLength42 = this.mTotalLength;
                        int measuredHeight2 = totalLength42 + child5.getMeasuredHeight();
                        int alternativeMaxWidth132 = lp4.topMargin;
                        this.mTotalLength = Math.max(totalLength42, measuredHeight2 + alternativeMaxWidth132 + lp4.bottomMargin + getNextLocationOffset(child5));
                        largestChildHeight3 = i15;
                        weightSum = weightSum3;
                        alternativeMaxWidth11 = delta3;
                        alternativeMaxWidth10 = alternativeMaxWidth122;
                    }
                    i14++;
                    useLargestChild = useLargestChild2;
                    baselineChildIndex2 = baselineChildIndex;
                    heightMode2 = heightMode;
                    largestChildHeight7 = largestChildHeight8;
                    weightedMaxWidth7 = weightedMaxWidth8;
                }
                i = widthMeasureSpec;
                int i16 = this.mTotalLength;
                this.mTotalLength = i16 + getPaddingTop() + getPaddingBottom();
                delta2 = alternativeMaxWidth10;
                weightedMaxWidth = maxWidth3;
                maxWidth = maxWidth2;
                if (largestChildHeight3 == 0) {
                    maxWidth = delta2;
                }
                setMeasuredDimension(View.resolveSizeAndState(Math.max(maxWidth + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, weightedMaxWidth), heightSizeAndState);
                if (!matchWidth) {
                }
            }
        }
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), BasicMeasure.EXACTLY);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:207:0x0559  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x0591  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x0648  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x0650  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int childState;
        int descent;
        int maxHeight;
        int widthMode;
        float totalWeight;
        int largestChildWidth;
        int count2;
        int widthSizeAndState;
        int delta;
        int childState2;
        int widthMode2;
        int alternativeMaxHeight;
        int maxHeight2;
        int count3;
        int widthMode3;
        int widthSizeAndState2;
        int count4;
        boolean useLargestChild;
        int delta2;
        int alternativeMaxHeight2;
        int delta3;
        int alternativeMaxHeight3;
        int alternativeMaxHeight4;
        int largestChildWidth2;
        int widthSize;
        int maxHeight3;
        int i;
        int i2;
        int oldWidth;
        int weightedMaxHeight;
        int alternativeMaxHeight5;
        int childState3;
        int largestChildWidth3;
        int widthMode4;
        boolean baselineAligned;
        int count5;
        int count6;
        LayoutParams lp;
        int largestChildWidth4;
        int margin;
        int largestChildWidth5;
        int weightedMaxHeight2;
        int childBaseline;
        int alternativeMaxHeight6;
        this.mTotalLength = 0;
        int count7 = getVirtualChildCount();
        int widthMode5 = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] maxAscent = this.mMaxAscent;
        int[] maxDescent = this.mMaxDescent;
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        boolean baselineAligned2 = this.mBaselineAligned;
        boolean useLargestChild2 = this.mUseLargestChild;
        boolean isExactly = widthMode5 == 1073741824;
        int i3 = 0;
        int childState4 = 0;
        float totalWeight2 = 0.0f;
        int childHeight = 0;
        int childState5 = 0;
        int largestChildWidth6 = 0;
        boolean skippedMeasure = false;
        boolean matchHeight = true;
        int weightedMaxHeight3 = 0;
        int alternativeMaxHeight7 = 0;
        while (i3 < count7) {
            View child = getVirtualChildAt(i3);
            if (child == null) {
                int largestChildWidth7 = childState5;
                int largestChildWidth8 = this.mTotalLength;
                this.mTotalLength = largestChildWidth8 + measureNullChild(i3);
                baselineAligned = baselineAligned2;
                count5 = count7;
                childState5 = largestChildWidth7;
                largestChildWidth3 = widthMode5;
            } else {
                int largestChildWidth9 = childState5;
                int largestChildWidth10 = child.getVisibility();
                int weightedMaxHeight4 = alternativeMaxHeight7;
                if (largestChildWidth10 == 8) {
                    i3 += getChildrenSkipCount(child, i3);
                    baselineAligned = baselineAligned2;
                    childState5 = largestChildWidth9;
                    alternativeMaxHeight7 = weightedMaxHeight4;
                    count5 = count7;
                    largestChildWidth3 = widthMode5;
                } else {
                    if (hasDividerBeforeChildAt(i3)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams lp2 = (LayoutParams) child.getLayoutParams();
                    float totalWeight3 = totalWeight2 + lp2.weight;
                    if (widthMode5 != 1073741824 || lp2.width != 0 || lp2.weight <= 0.0f) {
                        int alternativeMaxHeight8 = weightedMaxHeight3;
                        if (lp2.width == 0 && lp2.weight > 0.0f) {
                            lp2.width = -2;
                            oldWidth = 0;
                        } else {
                            oldWidth = Integer.MIN_VALUE;
                        }
                        weightedMaxHeight = weightedMaxHeight4;
                        int oldWidth2 = oldWidth;
                        alternativeMaxHeight5 = alternativeMaxHeight8;
                        childState3 = childHeight;
                        int childState6 = totalWeight3 == 0.0f ? this.mTotalLength : 0;
                        largestChildWidth3 = widthMode5;
                        widthMode4 = childState4;
                        baselineAligned = baselineAligned2;
                        count5 = count7;
                        count6 = -1;
                        measureChildBeforeLayout(child, i3, widthMeasureSpec, childState6, heightMeasureSpec, 0);
                        if (oldWidth2 == Integer.MIN_VALUE) {
                            lp = lp2;
                        } else {
                            lp = lp2;
                            lp.width = oldWidth2;
                        }
                        int childWidth = child.getMeasuredWidth();
                        if (isExactly) {
                            this.mTotalLength += lp.leftMargin + childWidth + lp.rightMargin + getNextLocationOffset(child);
                        } else {
                            int totalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength, totalLength + childWidth + lp.leftMargin + lp.rightMargin + getNextLocationOffset(child));
                        }
                        if (!useLargestChild2) {
                            largestChildWidth4 = largestChildWidth9;
                        } else {
                            largestChildWidth4 = Math.max(childWidth, largestChildWidth9);
                        }
                    } else {
                        if (isExactly) {
                            int i4 = this.mTotalLength;
                            int i5 = lp2.leftMargin;
                            alternativeMaxHeight6 = weightedMaxHeight3;
                            int alternativeMaxHeight9 = lp2.rightMargin;
                            this.mTotalLength = i4 + i5 + alternativeMaxHeight9;
                        } else {
                            alternativeMaxHeight6 = weightedMaxHeight3;
                            int totalLength2 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength2, lp2.leftMargin + totalLength2 + lp2.rightMargin);
                        }
                        if (baselineAligned2) {
                            int freeSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                            child.measure(freeSpec, freeSpec);
                            lp = lp2;
                            childState3 = childHeight;
                            baselineAligned = baselineAligned2;
                            largestChildWidth4 = largestChildWidth9;
                            weightedMaxHeight = weightedMaxHeight4;
                            alternativeMaxHeight5 = alternativeMaxHeight6;
                            count5 = count7;
                            largestChildWidth3 = widthMode5;
                            count6 = -1;
                            widthMode4 = childState4;
                        } else {
                            largestChildWidth6 = 1;
                            lp = lp2;
                            childState3 = childHeight;
                            baselineAligned = baselineAligned2;
                            largestChildWidth4 = largestChildWidth9;
                            weightedMaxHeight = weightedMaxHeight4;
                            alternativeMaxHeight5 = alternativeMaxHeight6;
                            count5 = count7;
                            largestChildWidth3 = widthMode5;
                            count6 = -1;
                            widthMode4 = childState4;
                        }
                    }
                    int oldWidth3 = 0;
                    if (heightMode != 1073741824 && lp.height == count6) {
                        skippedMeasure = true;
                        oldWidth3 = 1;
                    }
                    int margin2 = lp.topMargin + lp.bottomMargin;
                    int childHeight2 = child.getMeasuredHeight() + margin2;
                    int childState7 = View.combineMeasuredStates(childState3, child.getMeasuredState());
                    if (!baselineAligned || (childBaseline = child.getBaseline()) == count6) {
                        margin = margin2;
                        largestChildWidth5 = largestChildWidth4;
                    } else {
                        int gravity = (lp.gravity < 0 ? this.mGravity : lp.gravity) & 112;
                        int index = ((gravity >> 4) & (-2)) >> 1;
                        margin = margin2;
                        maxAscent[index] = Math.max(maxAscent[index], childBaseline);
                        largestChildWidth5 = largestChildWidth4;
                        int largestChildWidth11 = childHeight2 - childBaseline;
                        maxDescent[index] = Math.max(maxDescent[index], largestChildWidth11);
                    }
                    int maxHeight4 = Math.max(widthMode4, childHeight2);
                    boolean allFillParent = matchHeight && lp.height == -1;
                    if (lp.weight > 0.0f) {
                        weightedMaxHeight2 = Math.max(weightedMaxHeight, oldWidth3 != 0 ? margin : childHeight2);
                    } else {
                        int weightedMaxHeight5 = weightedMaxHeight;
                        alternativeMaxHeight5 = Math.max(alternativeMaxHeight5, oldWidth3 != 0 ? margin : childHeight2);
                        weightedMaxHeight2 = weightedMaxHeight5;
                    }
                    int weightedMaxHeight6 = getChildrenSkipCount(child, i3);
                    i3 += weightedMaxHeight6;
                    matchHeight = allFillParent;
                    childHeight = childState7;
                    totalWeight2 = totalWeight3;
                    childState5 = largestChildWidth5;
                    weightedMaxHeight3 = alternativeMaxHeight5;
                    childState4 = maxHeight4;
                    alternativeMaxHeight7 = weightedMaxHeight2;
                }
            }
            i3++;
            baselineAligned2 = baselineAligned;
            widthMode5 = largestChildWidth3;
            count7 = count5;
        }
        boolean baselineAligned3 = baselineAligned2;
        int count8 = count7;
        int widthMode6 = widthMode5;
        int weightedMaxHeight7 = alternativeMaxHeight7;
        int weightedMaxHeight8 = weightedMaxHeight3;
        int childState8 = childHeight;
        int widthMode7 = childState4;
        int largestChildWidth12 = childState5;
        int largestChildWidth13 = this.mTotalLength;
        if (largestChildWidth13 > 0) {
            count = count8;
            if (hasDividerBeforeChildAt(count)) {
                this.mTotalLength += this.mDividerWidth;
            }
        } else {
            count = count8;
        }
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            childState = childState8;
            descent = widthMode7;
        } else {
            int ascent = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
            int i6 = maxDescent[3];
            int i7 = maxDescent[0];
            int i8 = maxDescent[1];
            childState = childState8;
            int childState9 = maxDescent[2];
            int descent2 = Math.max(i6, Math.max(i7, Math.max(i8, childState9)));
            descent = Math.max(widthMode7, ascent + descent2);
        }
        if (useLargestChild2) {
            widthMode = widthMode6;
            if (widthMode == Integer.MIN_VALUE || widthMode == 0) {
                this.mTotalLength = 0;
                int i9 = 0;
                while (i9 < count) {
                    View child2 = getVirtualChildAt(i9);
                    if (child2 == null) {
                        this.mTotalLength += measureNullChild(i9);
                        maxHeight3 = descent;
                        i = i9;
                    } else if (child2.getVisibility() == 8) {
                        i2 = i9 + getChildrenSkipCount(child2, i9);
                        maxHeight3 = descent;
                        i9 = i2 + 1;
                        descent = maxHeight3;
                    } else {
                        LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                        if (isExactly) {
                            int i10 = this.mTotalLength;
                            maxHeight3 = descent;
                            int maxHeight5 = lp3.leftMargin;
                            i = i9;
                            int i11 = lp3.rightMargin;
                            this.mTotalLength = i10 + maxHeight5 + largestChildWidth12 + i11 + getNextLocationOffset(child2);
                        } else {
                            maxHeight3 = descent;
                            i = i9;
                            int maxHeight6 = this.mTotalLength;
                            this.mTotalLength = Math.max(maxHeight6, maxHeight6 + largestChildWidth12 + lp3.leftMargin + lp3.rightMargin + getNextLocationOffset(child2));
                        }
                    }
                    i2 = i;
                    i9 = i2 + 1;
                    descent = maxHeight3;
                }
                maxHeight = descent;
            } else {
                maxHeight = descent;
            }
        } else {
            maxHeight = descent;
            widthMode = widthMode6;
        }
        int maxHeight7 = this.mTotalLength;
        this.mTotalLength = maxHeight7 + getPaddingLeft() + getPaddingRight();
        int widthSizeAndState3 = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), widthMeasureSpec, 0);
        int widthSize2 = widthSizeAndState3 & ViewCompat.MEASURED_SIZE_MASK;
        int delta4 = widthSize2 - this.mTotalLength;
        if (largestChildWidth6 != 0) {
            totalWeight = totalWeight2;
            largestChildWidth = weightedMaxHeight8;
        } else {
            if (delta4 == 0 || totalWeight2 <= 0.0f) {
                int alternativeMaxHeight10 = Math.max(weightedMaxHeight8, weightedMaxHeight7);
                if (!useLargestChild2 || widthMode == 1073741824) {
                    alternativeMaxHeight3 = alternativeMaxHeight10;
                } else {
                    int i12 = 0;
                    while (i12 < count) {
                        float totalWeight4 = totalWeight2;
                        View child3 = getVirtualChildAt(i12);
                        if (child3 != null) {
                            alternativeMaxHeight4 = alternativeMaxHeight10;
                            int alternativeMaxHeight11 = child3.getVisibility();
                            widthSize = widthSize2;
                            if (alternativeMaxHeight11 == 8) {
                                largestChildWidth2 = largestChildWidth12;
                            } else if (((LayoutParams) child3.getLayoutParams()).weight > 0.0f) {
                                int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(largestChildWidth12, BasicMeasure.EXACTLY);
                                largestChildWidth2 = largestChildWidth12;
                                int largestChildWidth14 = child3.getMeasuredHeight();
                                child3.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(largestChildWidth14, BasicMeasure.EXACTLY));
                            } else {
                                largestChildWidth2 = largestChildWidth12;
                            }
                        } else {
                            alternativeMaxHeight4 = alternativeMaxHeight10;
                            largestChildWidth2 = largestChildWidth12;
                            widthSize = widthSize2;
                        }
                        i12++;
                        alternativeMaxHeight10 = alternativeMaxHeight4;
                        totalWeight2 = totalWeight4;
                        widthSize2 = widthSize;
                        largestChildWidth12 = largestChildWidth2;
                    }
                    alternativeMaxHeight3 = alternativeMaxHeight10;
                }
                delta = heightMeasureSpec;
                count2 = count;
                widthSizeAndState = widthSizeAndState3;
                alternativeMaxHeight = alternativeMaxHeight3;
                childState2 = maxHeight;
                widthMode2 = childState;
                if (!matchHeight && heightMode != 1073741824) {
                    childState2 = alternativeMaxHeight;
                }
                int maxHeight8 = childState2 + getPaddingTop() + getPaddingBottom();
                setMeasuredDimension(widthSizeAndState | ((-16777216) & widthMode2), View.resolveSizeAndState(Math.max(maxHeight8, getSuggestedMinimumHeight()), delta, widthMode2 << 16));
                if (!skippedMeasure) {
                    forceUniformHeight(count2, widthMeasureSpec);
                    return;
                }
                return;
            }
            totalWeight = totalWeight2;
            largestChildWidth = weightedMaxHeight8;
        }
        float totalWeight5 = this.mWeightSum;
        float weightSum = totalWeight5 > 0.0f ? this.mWeightSum : totalWeight;
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        this.mTotalLength = 0;
        int i13 = 0;
        int delta5 = delta4;
        int maxHeight9 = -1;
        int childState10 = childState;
        while (i13 < count) {
            int weightedMaxHeight9 = weightedMaxHeight7;
            View child4 = getVirtualChildAt(i13);
            if (child4 != null) {
                useLargestChild = useLargestChild2;
                count3 = count;
                if (child4.getVisibility() == 8) {
                    widthMode3 = widthMode;
                    widthSizeAndState2 = widthSizeAndState3;
                    count4 = delta5;
                } else {
                    LayoutParams lp4 = (LayoutParams) child4.getLayoutParams();
                    float childExtra = lp4.weight;
                    if (childExtra > 0.0f) {
                        int share = (int) ((delta5 * childExtra) / weightSum);
                        float weightSum2 = weightSum - childExtra;
                        int delta6 = delta5 - share;
                        widthSizeAndState2 = widthSizeAndState3;
                        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + lp4.topMargin + lp4.bottomMargin, lp4.height);
                        if (lp4.width != 0 || widthMode != 1073741824) {
                            int childWidth2 = child4.getMeasuredWidth() + share;
                            if (childWidth2 < 0) {
                                childWidth2 = 0;
                            }
                            widthMode3 = widthMode;
                            child4.measure(View.MeasureSpec.makeMeasureSpec(childWidth2, BasicMeasure.EXACTLY), childHeightMeasureSpec);
                        } else {
                            child4.measure(View.MeasureSpec.makeMeasureSpec(share > 0 ? share : 0, BasicMeasure.EXACTLY), childHeightMeasureSpec);
                            widthMode3 = widthMode;
                        }
                        childState10 = View.combineMeasuredStates(childState10, child4.getMeasuredState() & ViewCompat.MEASURED_STATE_MASK);
                        weightSum = weightSum2;
                        delta2 = delta6;
                    } else {
                        widthMode3 = widthMode;
                        widthSizeAndState2 = widthSizeAndState3;
                        delta2 = delta5;
                    }
                    if (isExactly) {
                        this.mTotalLength += child4.getMeasuredWidth() + lp4.leftMargin + lp4.rightMargin + getNextLocationOffset(child4);
                    } else {
                        int totalLength3 = this.mTotalLength;
                        this.mTotalLength = Math.max(totalLength3, child4.getMeasuredWidth() + totalLength3 + lp4.leftMargin + lp4.rightMargin + getNextLocationOffset(child4));
                    }
                    boolean matchHeightLocally = heightMode != 1073741824 && lp4.height == -1;
                    int margin3 = lp4.topMargin + lp4.bottomMargin;
                    int childHeight3 = child4.getMeasuredHeight() + margin3;
                    maxHeight9 = Math.max(maxHeight9, childHeight3);
                    float weightSum3 = weightSum;
                    int alternativeMaxHeight12 = Math.max(largestChildWidth, matchHeightLocally ? margin3 : childHeight3);
                    if (matchHeight) {
                        alternativeMaxHeight2 = alternativeMaxHeight12;
                        boolean allFillParent2 = lp4.height == -1;
                        if (baselineAligned3) {
                            matchHeight = allFillParent2;
                            delta3 = delta2;
                        } else {
                            int childBaseline2 = child4.getBaseline();
                            matchHeight = allFillParent2;
                            if (childBaseline2 == -1) {
                                delta3 = delta2;
                            } else {
                                int gravity2 = (lp4.gravity < 0 ? this.mGravity : lp4.gravity) & 112;
                                int index2 = ((gravity2 >> 4) & (-2)) >> 1;
                                int gravity3 = maxAscent[index2];
                                maxAscent[index2] = Math.max(gravity3, childBaseline2);
                                delta3 = delta2;
                                maxDescent[index2] = Math.max(maxDescent[index2], childHeight3 - childBaseline2);
                            }
                        }
                        weightSum = weightSum3;
                        largestChildWidth = alternativeMaxHeight2;
                        count4 = delta3;
                    } else {
                        alternativeMaxHeight2 = alternativeMaxHeight12;
                    }
                    if (baselineAligned3) {
                    }
                    weightSum = weightSum3;
                    largestChildWidth = alternativeMaxHeight2;
                    count4 = delta3;
                }
            } else {
                count3 = count;
                widthMode3 = widthMode;
                widthSizeAndState2 = widthSizeAndState3;
                count4 = delta5;
                useLargestChild = useLargestChild2;
            }
            i13++;
            delta5 = count4;
            widthSizeAndState3 = widthSizeAndState2;
            useLargestChild2 = useLargestChild;
            count = count3;
            weightedMaxHeight7 = weightedMaxHeight9;
            widthMode = widthMode3;
        }
        count2 = count;
        widthSizeAndState = widthSizeAndState3;
        delta = heightMeasureSpec;
        int i14 = this.mTotalLength;
        this.mTotalLength = i14 + getPaddingLeft() + getPaddingRight();
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            maxHeight2 = maxHeight9;
        } else {
            int ascent2 = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
            int descent3 = Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2])));
            maxHeight2 = Math.max(maxHeight9, ascent2 + descent3);
        }
        alternativeMaxHeight = largestChildWidth;
        widthMode2 = childState10;
        childState2 = maxHeight2;
        if (!matchHeight) {
            childState2 = alternativeMaxHeight;
        }
        int maxHeight82 = childState2 + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthSizeAndState | ((-16777216) & widthMode2), View.resolveSizeAndState(Math.max(maxHeight82, getSuggestedMinimumHeight()), delta, widthMode2 << 16));
        if (!skippedMeasure) {
        }
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.height == -1) {
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    int getChildrenSkipCount(View child, int index) {
        return 0;
    }

    int measureNullChild(int childIndex) {
        return 0;
    }

    void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    int getLocationOffset(View child) {
        return 0;
    }

    int getNextLocationOffset(View child) {
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.mOrientation == 1) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }

    void layoutVertical(int left, int top, int right, int bottom) {
        int childTop;
        int paddingLeft;
        int gravity;
        int childLeft;
        int paddingLeft2 = getPaddingLeft();
        int width = right - left;
        int childRight = width - getPaddingRight();
        int childSpace = (width - paddingLeft2) - getPaddingRight();
        int count = getVirtualChildCount();
        int majorGravity = this.mGravity & 112;
        int minorGravity = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        switch (majorGravity) {
            case 16:
                int childTop2 = getPaddingTop();
                childTop = childTop2 + (((bottom - top) - this.mTotalLength) / 2);
                break;
            case 80:
                int childTop3 = getPaddingTop();
                childTop = ((childTop3 + bottom) - top) - this.mTotalLength;
                break;
            default:
                childTop = getPaddingTop();
                break;
        }
        int i = 0;
        while (i < count) {
            View child = getVirtualChildAt(i);
            if (child == null) {
                childTop += measureNullChild(i);
                paddingLeft = paddingLeft2;
            } else if (child.getVisibility() == 8) {
                paddingLeft = paddingLeft2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int gravity2 = lp.gravity;
                if (gravity2 >= 0) {
                    gravity = gravity2;
                } else {
                    gravity = minorGravity;
                }
                int layoutDirection = ViewCompat.getLayoutDirection(this);
                int absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & 7) {
                    case 1:
                        int childLeft2 = childSpace - childWidth;
                        childLeft = (((childLeft2 / 2) + paddingLeft2) + lp.leftMargin) - lp.rightMargin;
                        break;
                    case 5:
                        int childLeft3 = childRight - childWidth;
                        childLeft = childLeft3 - lp.rightMargin;
                        break;
                    default:
                        childLeft = lp.leftMargin + paddingLeft2;
                        break;
                }
                if (hasDividerBeforeChildAt(i)) {
                    childTop += this.mDividerHeight;
                }
                int childTop4 = childTop + lp.topMargin;
                int childTop5 = getLocationOffset(child);
                paddingLeft = paddingLeft2;
                setChildFrame(child, childLeft, childTop4 + childTop5, childWidth, childHeight);
                int childTop6 = childTop4 + childHeight + lp.bottomMargin + getNextLocationOffset(child);
                i += getChildrenSkipCount(child, i);
                childTop = childTop6;
            }
            i++;
            paddingLeft2 = paddingLeft;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00fd  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0111  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    void layoutHorizontal(int left, int top, int right, int bottom) {
        int childLeft;
        int start;
        int dir;
        int layoutDirection;
        int[] maxDescent;
        int[] maxAscent;
        int paddingTop;
        int height;
        int childBottom;
        int childBaseline;
        int gravity;
        int gravity2;
        int childTop;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop2 = getPaddingTop();
        int height2 = bottom - top;
        int childBottom2 = height2 - getPaddingBottom();
        int childSpace = (height2 - paddingTop2) - getPaddingBottom();
        int count = getVirtualChildCount();
        int majorGravity = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int minorGravity = this.mGravity & 112;
        boolean baselineAligned = this.mBaselineAligned;
        int[] maxAscent2 = this.mMaxAscent;
        int[] maxDescent2 = this.mMaxDescent;
        int layoutDirection2 = ViewCompat.getLayoutDirection(this);
        switch (GravityCompat.getAbsoluteGravity(majorGravity, layoutDirection2)) {
            case 1:
                int childLeft2 = getPaddingLeft();
                childLeft = childLeft2 + (((right - left) - this.mTotalLength) / 2);
                break;
            case 5:
                int childLeft3 = getPaddingLeft();
                childLeft = ((childLeft3 + right) - left) - this.mTotalLength;
                break;
            default:
                childLeft = getPaddingLeft();
                break;
        }
        if (!isLayoutRtl) {
            start = 0;
            dir = 1;
        } else {
            int start2 = count - 1;
            start = start2;
            dir = -1;
        }
        int i = 0;
        while (i < count) {
            int childIndex = start + (dir * i);
            boolean isLayoutRtl2 = isLayoutRtl;
            View child = getVirtualChildAt(childIndex);
            if (child == null) {
                childLeft += measureNullChild(childIndex);
                layoutDirection = layoutDirection2;
                maxDescent = maxDescent2;
                maxAscent = maxAscent2;
                paddingTop = paddingTop2;
                height = height2;
                childBottom = childBottom2;
            } else {
                int i2 = i;
                int i3 = child.getVisibility();
                layoutDirection = layoutDirection2;
                if (i3 == 8) {
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    paddingTop = paddingTop2;
                    height = height2;
                    childBottom = childBottom2;
                    i = i2;
                } else {
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (!baselineAligned) {
                        height = height2;
                    } else {
                        height = height2;
                        if (lp.height != -1) {
                            childBaseline = child.getBaseline();
                        }
                        gravity = lp.gravity;
                        if (gravity < 0) {
                            gravity2 = gravity;
                        } else {
                            gravity2 = minorGravity;
                        }
                        switch (gravity2 & 112) {
                            case 16:
                                childBottom = childBottom2;
                                int childTop2 = ((((childSpace - childHeight) / 2) + paddingTop2) + lp.topMargin) - lp.bottomMargin;
                                childTop = childTop2;
                                break;
                            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE /* 48 */:
                                childBottom = childBottom2;
                                int childTop3 = lp.topMargin + paddingTop2;
                                if (childBaseline == -1) {
                                    childTop = childTop3;
                                    break;
                                } else {
                                    childTop = childTop3 + (maxAscent2[1] - childBaseline);
                                    break;
                                }
                            case 80:
                                int childTop4 = childBottom2 - childHeight;
                                childBottom = childBottom2;
                                int childBottom3 = lp.bottomMargin;
                                int childTop5 = childTop4 - childBottom3;
                                if (childBaseline == -1) {
                                    childTop = childTop5;
                                    break;
                                } else {
                                    int descent = child.getMeasuredHeight() - childBaseline;
                                    childTop = childTop5 - (maxDescent2[2] - descent);
                                    break;
                                }
                            default:
                                childBottom = childBottom2;
                                childTop = paddingTop2;
                                break;
                        }
                        if (hasDividerBeforeChildAt(childIndex)) {
                            childLeft += this.mDividerWidth;
                        }
                        int childLeft4 = childLeft + lp.leftMargin;
                        int childLeft5 = getLocationOffset(child);
                        paddingTop = paddingTop2;
                        maxDescent = maxDescent2;
                        maxAscent = maxAscent2;
                        setChildFrame(child, childLeft4 + childLeft5, childTop, childWidth, childHeight);
                        int childLeft6 = childLeft4 + childWidth + lp.rightMargin + getNextLocationOffset(child);
                        i = i2 + getChildrenSkipCount(child, childIndex);
                        childLeft = childLeft6;
                    }
                    childBaseline = -1;
                    gravity = lp.gravity;
                    if (gravity < 0) {
                    }
                    switch (gravity2 & 112) {
                        case 16:
                            break;
                        case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE /* 48 */:
                            break;
                        case 80:
                            break;
                    }
                    if (hasDividerBeforeChildAt(childIndex)) {
                    }
                    int childLeft42 = childLeft + lp.leftMargin;
                    int childLeft52 = getLocationOffset(child);
                    paddingTop = paddingTop2;
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    setChildFrame(child, childLeft42 + childLeft52, childTop, childWidth, childHeight);
                    int childLeft62 = childLeft42 + childWidth + lp.rightMargin + getNextLocationOffset(child);
                    i = i2 + getChildrenSkipCount(child, childIndex);
                    childLeft = childLeft62;
                }
            }
            i++;
            isLayoutRtl = isLayoutRtl2;
            layoutDirection2 = layoutDirection;
            height2 = height;
            childBottom2 = childBottom;
            paddingTop2 = paddingTop;
            maxDescent2 = maxDescent;
            maxAscent2 = maxAscent;
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= GravityCompat.START;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((8388615 & this.mGravity) != gravity) {
            this.mGravity = (this.mGravity & (-8388616)) | gravity;
            requestLayout();
        }
    }

    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        if ((this.mGravity & 112) != gravity) {
            this.mGravity = (this.mGravity & (-113)) | gravity;
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ACCESSIBILITY_CLASS_NAME);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ACCESSIBILITY_CLASS_NAME);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}

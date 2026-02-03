package com.google.android.material.carousel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.carousel.KeylineState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class CarouselLayoutManager extends RecyclerView.LayoutManager implements Carousel {
    private static final String TAG = "CarouselLayoutManager";
    private CarouselStrategy carouselStrategy;
    private KeylineState currentKeylineState;
    private int horizontalScrollOffset;
    private KeylineStateList keylineStateList;
    private int maxHorizontalScroll;
    private int minHorizontalScroll;
    private boolean isDebuggingEnabled = false;
    private final DebugItemDecoration debugItemDecoration = new DebugItemDecoration();
    private int currentFillStartPosition = 0;

    private static final class ChildCalculations {
        View child;
        float locOffset;
        KeylineRange range;

        ChildCalculations(View child, float locOffset, KeylineRange range) {
            this.child = child;
            this.locOffset = locOffset;
            this.range = range;
        }
    }

    public CarouselLayoutManager() {
        setCarouselStrategy(new MultiBrowseCarouselStrategy());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    public void setCarouselStrategy(CarouselStrategy carouselStrategy) {
        this.carouselStrategy = carouselStrategy;
        this.keylineStateList = null;
        requestLayout();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() <= 0) {
            removeAndRecycleAllViews(recycler);
            this.currentFillStartPosition = 0;
            return;
        }
        boolean isRtl = isLayoutRtl();
        boolean isInitialLoad = this.keylineStateList == null;
        if (isInitialLoad) {
            View firstChild = recycler.getViewForPosition(0);
            measureChildWithMargins(firstChild, 0, 0);
            KeylineState keylineState = this.carouselStrategy.onFirstChildMeasuredWithMargins(this, firstChild);
            this.keylineStateList = KeylineStateList.from(this, isRtl ? KeylineState.reverse(keylineState) : keylineState);
        }
        int startHorizontalScroll = calculateStartHorizontalScroll(this.keylineStateList);
        int endHorizontalScroll = calculateEndHorizontalScroll(state, this.keylineStateList);
        this.minHorizontalScroll = isRtl ? endHorizontalScroll : startHorizontalScroll;
        this.maxHorizontalScroll = isRtl ? startHorizontalScroll : endHorizontalScroll;
        if (isInitialLoad) {
            this.horizontalScrollOffset = startHorizontalScroll;
        } else {
            this.horizontalScrollOffset += calculateShouldHorizontallyScrollBy(0, this.horizontalScrollOffset, this.minHorizontalScroll, this.maxHorizontalScroll);
        }
        this.currentFillStartPosition = MathUtils.clamp(this.currentFillStartPosition, 0, state.getItemCount());
        updateCurrentKeylineStateForScrollOffset();
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        removeAndRecycleOutOfBoundsViews(recycler);
        if (getChildCount() == 0) {
            addViewsStart(recycler, this.currentFillStartPosition - 1);
            addViewsEnd(recycler, state, this.currentFillStartPosition);
        } else {
            int firstPosition = getPosition(getChildAt(0));
            int lastPosition = getPosition(getChildAt(getChildCount() - 1));
            addViewsStart(recycler, firstPosition - 1);
            addViewsEnd(recycler, state, lastPosition + 1);
        }
        validateChildOrderIfDebugging();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (getChildCount() == 0) {
            this.currentFillStartPosition = 0;
        } else {
            this.currentFillStartPosition = getPosition(getChildAt(0));
        }
        validateChildOrderIfDebugging();
    }

    private void addViewsStart(RecyclerView.Recycler recycler, int startPosition) {
        int start = calculateChildStartForFill(startPosition);
        for (int i = startPosition; i >= 0; i--) {
            ChildCalculations calculations = makeChildCalculations(recycler, start, i);
            if (!isLocOffsetOutOfFillBoundsStart(calculations.locOffset, calculations.range)) {
                start = addStart(start, (int) this.currentKeylineState.getItemSize());
                if (!isLocOffsetOutOfFillBoundsEnd(calculations.locOffset, calculations.range)) {
                    addAndLayoutView(calculations.child, 0, calculations.locOffset);
                }
            } else {
                return;
            }
        }
    }

    private void addViewsEnd(RecyclerView.Recycler recycler, RecyclerView.State state, int startPosition) {
        int start = calculateChildStartForFill(startPosition);
        for (int i = startPosition; i < state.getItemCount(); i++) {
            ChildCalculations calculations = makeChildCalculations(recycler, start, i);
            if (!isLocOffsetOutOfFillBoundsEnd(calculations.locOffset, calculations.range)) {
                start = addEnd(start, (int) this.currentKeylineState.getItemSize());
                if (!isLocOffsetOutOfFillBoundsStart(calculations.locOffset, calculations.range)) {
                    addAndLayoutView(calculations.child, -1, calculations.locOffset);
                }
            } else {
                return;
            }
        }
    }

    private void logChildrenIfDebugging() {
        if (this.isDebuggingEnabled && Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "internal representation of views on the screen");
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float centerX = getDecoratedCenterXWithMargins(child);
                Log.d(TAG, "item position " + getPosition(child) + ", center:" + centerX + ", child index:" + i);
            }
            Log.d(TAG, "==============");
        }
    }

    private void validateChildOrderIfDebugging() {
        if (!this.isDebuggingEnabled || getChildCount() < 1) {
            return;
        }
        for (int i = 0; i < getChildCount() - 1; i++) {
            int currPos = getPosition(getChildAt(i));
            int nextPos = getPosition(getChildAt(i + 1));
            if (currPos > nextPos) {
                logChildrenIfDebugging();
                throw new IllegalStateException("Detected invalid child order. Child at index [" + i + "] had adapter position [" + currPos + "] and child at index [" + (i + 1) + "] had adapter position [" + nextPos + "].");
            }
        }
    }

    private ChildCalculations makeChildCalculations(RecyclerView.Recycler recycler, float start, int position) {
        float halfItemSize = this.currentKeylineState.getItemSize() / 2.0f;
        View child = recycler.getViewForPosition(position);
        measureChildWithMargins(child, 0, 0);
        int centerX = addEnd((int) start, (int) halfItemSize);
        KeylineRange range = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, false);
        float offsetCx = calculateChildOffsetCenterForLocation(child, centerX, range);
        updateChildMaskForLocation(child, centerX, range);
        return new ChildCalculations(child, offsetCx, range);
    }

    private void addAndLayoutView(View child, int index, float offsetCx) {
        float halfItemSize = this.currentKeylineState.getItemSize() / 2.0f;
        addView(child, index);
        layoutDecoratedWithMargins(child, (int) (offsetCx - halfItemSize), getParentTop(), (int) (offsetCx + halfItemSize), getParentBottom());
    }

    private boolean isLocOffsetOutOfFillBoundsStart(float locOffset, KeylineRange range) {
        float maskedSize = getMaskedItemSizeForLocOffset(locOffset, range);
        int maskedEnd = addEnd((int) locOffset, (int) (maskedSize / 2.0f));
        if (isLayoutRtl()) {
            if (maskedEnd > getContainerWidth()) {
                return true;
            }
        } else if (maskedEnd < 0) {
            return true;
        }
        return false;
    }

    private boolean isLocOffsetOutOfFillBoundsEnd(float locOffset, KeylineRange range) {
        float maskedSize = getMaskedItemSizeForLocOffset(locOffset, range);
        int maskedStart = addStart((int) locOffset, (int) (maskedSize / 2.0f));
        if (isLayoutRtl()) {
            if (maskedStart < 0) {
                return true;
            }
        } else if (maskedStart > getContainerWidth()) {
            return true;
        }
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
        super.getDecoratedBoundsWithMargins(view, outBounds);
        float centerX = outBounds.centerX();
        float maskedSize = getMaskedItemSizeForLocOffset(centerX, getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, true));
        float delta = (outBounds.width() - maskedSize) / 2.0f;
        outBounds.set((int) (outBounds.left + delta), outBounds.top, (int) (outBounds.right - delta), outBounds.bottom);
    }

    private float getDecoratedCenterXWithMargins(View child) {
        Rect bounds = new Rect();
        super.getDecoratedBoundsWithMargins(child, bounds);
        return bounds.centerX();
    }

    private void removeAndRecycleOutOfBoundsViews(RecyclerView.Recycler recycler) {
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            float centerX = getDecoratedCenterXWithMargins(child);
            KeylineRange range = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, true);
            if (!isLocOffsetOutOfFillBoundsStart(centerX, range)) {
                break;
            } else {
                removeAndRecycleView(child, recycler);
            }
        }
        while (getChildCount() - 1 >= 0) {
            View child2 = getChildAt(getChildCount() - 1);
            float centerX2 = getDecoratedCenterXWithMargins(child2);
            KeylineRange range2 = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX2, true);
            if (isLocOffsetOutOfFillBoundsEnd(centerX2, range2)) {
                removeAndRecycleView(child2, recycler);
            } else {
                return;
            }
        }
    }

    private static KeylineRange getSurroundingKeylineRange(List<KeylineState.Keyline> keylines, float location, boolean isOffset) {
        int leftMinDistanceIndex = -1;
        float leftMinDistance = Float.MAX_VALUE;
        int leftMostIndex = -1;
        float leftMostX = Float.MAX_VALUE;
        int rightMinDistanceIndex = -1;
        float rightMinDistance = Float.MAX_VALUE;
        int rightMostIndex = -1;
        float rightMostX = -3.4028235E38f;
        for (int i = 0; i < keylines.size(); i++) {
            KeylineState.Keyline keyline = keylines.get(i);
            float currentLoc = isOffset ? keyline.locOffset : keyline.loc;
            float delta = Math.abs(currentLoc - location);
            if (currentLoc <= location && delta <= leftMinDistance) {
                leftMinDistance = delta;
                leftMinDistanceIndex = i;
            }
            if (currentLoc > location && delta <= rightMinDistance) {
                rightMinDistance = delta;
                rightMinDistanceIndex = i;
            }
            if (currentLoc <= leftMostX) {
                leftMostIndex = i;
                leftMostX = currentLoc;
            }
            if (currentLoc > rightMostX) {
                rightMostIndex = i;
                rightMostX = currentLoc;
            }
        }
        if (leftMinDistanceIndex == -1) {
            leftMinDistanceIndex = leftMostIndex;
        }
        if (rightMinDistanceIndex == -1) {
            rightMinDistanceIndex = rightMostIndex;
        }
        return new KeylineRange(keylines.get(leftMinDistanceIndex), keylines.get(rightMinDistanceIndex));
    }

    private void updateCurrentKeylineStateForScrollOffset() {
        if (this.maxHorizontalScroll <= this.minHorizontalScroll) {
            this.currentKeylineState = isLayoutRtl() ? this.keylineStateList.getRightState() : this.keylineStateList.getLeftState();
        } else {
            this.currentKeylineState = this.keylineStateList.getShiftedState(this.horizontalScrollOffset, this.minHorizontalScroll, this.maxHorizontalScroll);
        }
        this.debugItemDecoration.setKeylines(this.currentKeylineState.getKeylines());
    }

    private static int calculateShouldHorizontallyScrollBy(int dx, int currentHorizontalScroll, int minHorizontalScroll, int maxHorizontalScroll) {
        int targetHorizontalScroll = currentHorizontalScroll + dx;
        if (targetHorizontalScroll < minHorizontalScroll) {
            return minHorizontalScroll - currentHorizontalScroll;
        }
        if (targetHorizontalScroll > maxHorizontalScroll) {
            return maxHorizontalScroll - currentHorizontalScroll;
        }
        return dx;
    }

    private int calculateStartHorizontalScroll(KeylineStateList stateList) {
        boolean isRtl = isLayoutRtl();
        KeylineState startState = isRtl ? stateList.getRightState() : stateList.getLeftState();
        KeylineState.Keyline startFocalKeyline = isRtl ? startState.getLastFocalKeyline() : startState.getFirstFocalKeyline();
        float firstItemDistanceFromStart = getPaddingStart() * (isRtl ? 1 : -1);
        int firstItemStart = addStart((int) startFocalKeyline.loc, (int) (startState.getItemSize() / 2.0f));
        return (int) ((getParentStart() + firstItemDistanceFromStart) - firstItemStart);
    }

    private int calculateEndHorizontalScroll(RecyclerView.State state, KeylineStateList stateList) {
        boolean isRtl = isLayoutRtl();
        KeylineState endState = isRtl ? stateList.getLeftState() : stateList.getRightState();
        KeylineState.Keyline endFocalKeyline = isRtl ? endState.getFirstFocalKeyline() : endState.getLastFocalKeyline();
        float lastItemDistanceFromFirstItem = (((state.getItemCount() - 1) * endState.getItemSize()) + getPaddingEnd()) * (isRtl ? -1.0f : 1.0f);
        float endFocalLocDistanceFromStart = endFocalKeyline.loc - getParentStart();
        float endFocalLocDistanceFromEnd = getParentEnd() - endFocalKeyline.loc;
        if (Math.abs(endFocalLocDistanceFromStart) > Math.abs(lastItemDistanceFromFirstItem)) {
            return 0;
        }
        return (int) ((lastItemDistanceFromFirstItem - endFocalLocDistanceFromStart) + endFocalLocDistanceFromEnd);
    }

    private int calculateChildStartForFill(int startPosition) {
        float scrollOffset = getParentStart() - this.horizontalScrollOffset;
        float positionOffset = this.currentKeylineState.getItemSize() * startPosition;
        return addEnd((int) scrollOffset, (int) positionOffset);
    }

    private float calculateChildOffsetCenterForLocation(View child, float childCenterLocation, KeylineRange range) {
        float offsetCx = AnimationUtils.lerp(range.left.locOffset, range.right.locOffset, range.left.loc, range.right.loc, childCenterLocation);
        if (range.right == this.currentKeylineState.getFirstKeyline() || range.left == this.currentKeylineState.getLastKeyline()) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            float horizontalMarginMask = (lp.rightMargin + lp.leftMargin) / this.currentKeylineState.getItemSize();
            float outOfBoundOffset = (childCenterLocation - range.right.loc) * ((1.0f - range.right.mask) + horizontalMarginMask);
            return offsetCx + outOfBoundOffset;
        }
        return offsetCx;
    }

    private float getMaskedItemSizeForLocOffset(float locOffset, KeylineRange range) {
        return AnimationUtils.lerp(range.left.maskedItemSize, range.right.maskedItemSize, range.left.locOffset, range.right.locOffset, locOffset);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateChildMaskForLocation(View view, float childCenterLocation, KeylineRange range) {
        if (view instanceof Maskable) {
            float maskProgress = AnimationUtils.lerp(range.left.mask, range.right.mask, range.left.loc, range.right.loc, childCenterLocation);
            ((Maskable) view).setMaskXPercentage(maskProgress);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        if (!(child instanceof Maskable)) {
            throw new IllegalStateException("All children of a RecyclerView using CarouselLayoutManager must use MaskableFrameLayout as their root ViewGroup.");
        }
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        Rect insets = new Rect();
        calculateItemDecorationsForChild(child, insets);
        int widthUsed2 = widthUsed + insets.left + insets.right;
        int heightUsed2 = heightUsed + insets.top + insets.bottom;
        float childWidthDimension = this.keylineStateList != null ? this.keylineStateList.getDefaultState().getItemSize() : lp.width;
        int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed2, (int) childWidthDimension, canScrollHorizontally());
        int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed2, lp.height, canScrollVertically());
        child.measure(widthSpec, heightSpec);
    }

    private int getParentStart() {
        if (isLayoutRtl()) {
            return getWidth();
        }
        return 0;
    }

    private int getParentEnd() {
        if (isLayoutRtl()) {
            return 0;
        }
        return getWidth();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getParentTop() {
        return getPaddingTop();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getParentBottom() {
        return getHeight() - getPaddingBottom();
    }

    @Override // com.google.android.material.carousel.Carousel
    public int getContainerWidth() {
        return getWidth();
    }

    private boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    private int addStart(int value, int amount) {
        return isLayoutRtl() ? value + amount : value - amount;
    }

    private int addEnd(int value, int amount) {
        return isLayoutRtl() ? value - amount : value + amount;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            event.setFromIndex(getPosition(getChildAt(0)));
            event.setToIndex(getPosition(getChildAt(getChildCount() - 1)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getScrollOffsetForPosition(KeylineState keylineState, int position) {
        if (isLayoutRtl()) {
            return (int) (((getContainerWidth() - keylineState.getLastFocalKeyline().loc) - (position * keylineState.getItemSize())) - (keylineState.getItemSize() / 2.0f));
        }
        return (int) (((position * keylineState.getItemSize()) - keylineState.getFirstFocalKeyline().loc) + (keylineState.getItemSize() / 2.0f));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void scrollToPosition(int position) {
        if (this.keylineStateList == null) {
            return;
        }
        this.horizontalScrollOffset = getScrollOffsetForPosition(this.keylineStateList.getDefaultState(), position);
        this.currentFillStartPosition = MathUtils.clamp(position, 0, Math.max(0, getItemCount() - 1));
        updateCurrentKeylineStateForScrollOffset();
        requestLayout();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: com.google.android.material.carousel.CarouselLayoutManager.1
            @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
            public PointF computeScrollVectorForPosition(int targetPosition) {
                if (CarouselLayoutManager.this.keylineStateList != null) {
                    float targetScrollOffset = CarouselLayoutManager.this.getScrollOffsetForPosition(CarouselLayoutManager.this.keylineStateList.getDefaultState(), targetPosition);
                    return new PointF(targetScrollOffset - CarouselLayoutManager.this.horizontalScrollOffset, 0.0f);
                }
                return null;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                float targetScrollOffset = CarouselLayoutManager.this.getScrollOffsetForPosition(CarouselLayoutManager.this.keylineStateList.getDefaultState(), CarouselLayoutManager.this.getPosition(view));
                return (int) (CarouselLayoutManager.this.horizontalScrollOffset - targetScrollOffset);
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (canScrollHorizontally()) {
            return scrollBy(dx, recycler, state);
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if (this.keylineStateList == null) {
            return false;
        }
        int offsetForChild = getScrollOffsetForPosition(this.keylineStateList.getDefaultState(), getPosition(child));
        int dx = offsetForChild - this.horizontalScrollOffset;
        if (focusedChildVisible || dx == 0) {
            return false;
        }
        parent.scrollBy(dx, 0);
        return true;
    }

    private int scrollBy(int distance, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || distance == 0) {
            return 0;
        }
        int scrolledBy = calculateShouldHorizontallyScrollBy(distance, this.horizontalScrollOffset, this.minHorizontalScroll, this.maxHorizontalScroll);
        this.horizontalScrollOffset += scrolledBy;
        updateCurrentKeylineStateForScrollOffset();
        float halfItemSize = this.currentKeylineState.getItemSize() / 2.0f;
        int startPosition = getPosition(getChildAt(0));
        int start = calculateChildStartForFill(startPosition);
        Rect boundsRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            offsetChildLeftAndRight(child, start, halfItemSize, boundsRect);
            start = addEnd(start, (int) this.currentKeylineState.getItemSize());
        }
        fill(recycler, state);
        return scrolledBy;
    }

    private void offsetChildLeftAndRight(View child, float startOffset, float halfItemSize, Rect boundsRect) {
        int centerX = addEnd((int) startOffset, (int) halfItemSize);
        KeylineRange range = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, false);
        float offsetCx = calculateChildOffsetCenterForLocation(child, centerX, range);
        updateChildMaskForLocation(child, centerX, range);
        super.getDecoratedBoundsWithMargins(child, boundsRect);
        float actualCx = boundsRect.left + halfItemSize;
        child.offsetLeftAndRight((int) (offsetCx - actualCx));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return this.horizontalScrollOffset;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return (int) this.keylineStateList.getDefaultState().getItemSize();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return this.maxHorizontalScroll - this.minHorizontalScroll;
    }

    public void setDebuggingEnabled(RecyclerView recyclerView, boolean enabled) {
        this.isDebuggingEnabled = enabled;
        recyclerView.removeItemDecoration(this.debugItemDecoration);
        if (enabled) {
            recyclerView.addItemDecoration(this.debugItemDecoration);
        }
        recyclerView.invalidateItemDecorations();
    }

    private static class KeylineRange {
        final KeylineState.Keyline left;
        final KeylineState.Keyline right;

        KeylineRange(KeylineState.Keyline left, KeylineState.Keyline right) {
            Preconditions.checkArgument(left.loc <= right.loc);
            this.left = left;
            this.right = right;
        }
    }

    private static class DebugItemDecoration extends RecyclerView.ItemDecoration {
        private final Paint linePaint = new Paint();
        private List<KeylineState.Keyline> keylines = Collections.unmodifiableList(new ArrayList());

        DebugItemDecoration() {
            this.linePaint.setStrokeWidth(5.0f);
            this.linePaint.setColor(-65281);
        }

        void setKeylines(List<KeylineState.Keyline> keylines) {
            this.keylines = Collections.unmodifiableList(keylines);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            this.linePaint.setStrokeWidth(parent.getResources().getDimension(R.dimen.m3_carousel_debug_keyline_width));
            for (KeylineState.Keyline keyline : this.keylines) {
                this.linePaint.setColor(ColorUtils.blendARGB(-65281, -16776961, keyline.mask));
                c.drawLine(keyline.locOffset, ((CarouselLayoutManager) parent.getLayoutManager()).getParentTop(), keyline.locOffset, ((CarouselLayoutManager) parent.getLayoutManager()).getParentBottom(), this.linePaint);
            }
        }
    }
}

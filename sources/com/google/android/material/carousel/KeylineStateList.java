package com.google.android.material.carousel;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.carousel.KeylineState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
class KeylineStateList {
    private static final int NO_INDEX = -1;
    private final KeylineState defaultState;
    private final float leftShiftRange;
    private final List<KeylineState> leftStateSteps;
    private final float[] leftStateStepsInterpolationPoints;
    private final float rightShiftRange;
    private final List<KeylineState> rightStateSteps;
    private final float[] rightStateStepsInterpolationPoints;

    private KeylineStateList(KeylineState defaultState, List<KeylineState> leftStateSteps, List<KeylineState> rightStateSteps) {
        this.defaultState = defaultState;
        this.leftStateSteps = Collections.unmodifiableList(leftStateSteps);
        this.rightStateSteps = Collections.unmodifiableList(rightStateSteps);
        this.leftShiftRange = leftStateSteps.get(leftStateSteps.size() - 1).getFirstKeyline().loc - defaultState.getFirstKeyline().loc;
        this.rightShiftRange = defaultState.getLastKeyline().loc - rightStateSteps.get(rightStateSteps.size() - 1).getLastKeyline().loc;
        this.leftStateStepsInterpolationPoints = getStateStepInterpolationPoints(this.leftShiftRange, leftStateSteps, true);
        this.rightStateStepsInterpolationPoints = getStateStepInterpolationPoints(this.rightShiftRange, rightStateSteps, false);
    }

    static KeylineStateList from(Carousel carousel, KeylineState state) {
        return new KeylineStateList(state, getStateStepsLeft(state), getStateStepsRight(carousel, state));
    }

    KeylineState getDefaultState() {
        return this.defaultState;
    }

    KeylineState getLeftState() {
        return this.leftStateSteps.get(this.leftStateSteps.size() - 1);
    }

    KeylineState getRightState() {
        return this.rightStateSteps.get(this.rightStateSteps.size() - 1);
    }

    public KeylineState getShiftedState(float scrollOffset, float minScrollOffset, float maxScrollOffset) {
        float leftShiftOffset = this.leftShiftRange + minScrollOffset;
        float rightShiftOffset = maxScrollOffset - this.rightShiftRange;
        if (scrollOffset < leftShiftOffset) {
            float interpolation = AnimationUtils.lerp(1.0f, 0.0f, minScrollOffset, leftShiftOffset, scrollOffset);
            return lerp(this.leftStateSteps, interpolation, this.leftStateStepsInterpolationPoints);
        }
        if (scrollOffset > rightShiftOffset) {
            float interpolation2 = AnimationUtils.lerp(0.0f, 1.0f, rightShiftOffset, maxScrollOffset, scrollOffset);
            return lerp(this.rightStateSteps, interpolation2, this.rightStateStepsInterpolationPoints);
        }
        return this.defaultState;
    }

    private static KeylineState lerp(List<KeylineState> stateSteps, float interpolation, float[] stateStepsInterpolationPoints) {
        int numberOfSteps = stateSteps.size();
        float lowerBounds = stateStepsInterpolationPoints[0];
        for (int i = 1; i < numberOfSteps; i++) {
            float upperBounds = stateStepsInterpolationPoints[i];
            if (interpolation <= upperBounds) {
                int fromIndex = i - 1;
                int toIndex = i;
                float steppedProgress = AnimationUtils.lerp(0.0f, 1.0f, lowerBounds, upperBounds, interpolation);
                return KeylineState.lerp(stateSteps.get(fromIndex), stateSteps.get(toIndex), steppedProgress);
            }
            lowerBounds = upperBounds;
        }
        return stateSteps.get(0);
    }

    private static float[] getStateStepInterpolationPoints(float shiftRange, List<KeylineState> stateSteps, boolean isShiftingLeft) {
        float distanceShifted;
        int numberOfSteps = stateSteps.size();
        float[] stateStepsInterpolationPoints = new float[numberOfSteps];
        int i = 1;
        while (i < numberOfSteps) {
            KeylineState prevState = stateSteps.get(i - 1);
            KeylineState currState = stateSteps.get(i);
            if (isShiftingLeft) {
                distanceShifted = currState.getFirstKeyline().loc - prevState.getFirstKeyline().loc;
            } else {
                distanceShifted = prevState.getLastKeyline().loc - currState.getLastKeyline().loc;
            }
            float stepProgress = distanceShifted / shiftRange;
            stateStepsInterpolationPoints[i] = i == numberOfSteps + (-1) ? 1.0f : stateStepsInterpolationPoints[i - 1] + stepProgress;
            i++;
        }
        return stateStepsInterpolationPoints;
    }

    private static boolean isFirstFocalItemAtLeftOfContainer(KeylineState state) {
        float firstFocalItemLeft = state.getFirstFocalKeyline().locOffset - (state.getFirstFocalKeyline().maskedItemSize / 2.0f);
        return firstFocalItemLeft <= 0.0f || state.getFirstFocalKeyline() == state.getFirstKeyline();
    }

    private static List<KeylineState> getStateStepsLeft(KeylineState defaultState) {
        List<KeylineState> steps = new ArrayList<>();
        steps.add(defaultState);
        int firstInBoundsKeylineIndex = findFirstInBoundsKeylineIndex(defaultState);
        if (isFirstFocalItemAtLeftOfContainer(defaultState) || firstInBoundsKeylineIndex == -1) {
            return steps;
        }
        int end = defaultState.getFirstFocalKeylineIndex() - 1;
        int numberOfSteps = end - firstInBoundsKeylineIndex;
        float originalStart = defaultState.getFirstKeyline().locOffset - (defaultState.getFirstKeyline().maskedItemSize / 2.0f);
        for (int i = 0; i <= numberOfSteps; i++) {
            KeylineState prevStepState = steps.get(steps.size() - 1);
            int itemOrigIndex = firstInBoundsKeylineIndex + i;
            int dstIndex = defaultState.getKeylines().size() - 1;
            if (itemOrigIndex - 1 >= 0) {
                float originalAdjacentMaskLeft = defaultState.getKeylines().get(itemOrigIndex - 1).mask;
                dstIndex = findFirstIndexAfterLastFocalKeylineWithMask(prevStepState, originalAdjacentMaskLeft) - 1;
            }
            int dstIndex2 = dstIndex;
            int dstIndex3 = defaultState.getFirstFocalKeylineIndex();
            int newFirstFocalIndex = (dstIndex3 - i) - 1;
            int newLastFocalIndex = (defaultState.getLastFocalKeylineIndex() - i) - 1;
            KeylineState shifted = moveKeylineAndCreateKeylineState(prevStepState, firstInBoundsKeylineIndex, dstIndex2, originalStart, newFirstFocalIndex, newLastFocalIndex);
            steps.add(shifted);
        }
        return steps;
    }

    private static boolean isLastFocalItemAtRightOfContainer(Carousel carousel, KeylineState state) {
        float firstFocalItemRight = state.getLastFocalKeyline().locOffset + (state.getLastFocalKeyline().maskedItemSize / 2.0f);
        return firstFocalItemRight >= ((float) carousel.getContainerWidth()) || state.getLastFocalKeyline() == state.getLastKeyline();
    }

    private static List<KeylineState> getStateStepsRight(Carousel carousel, KeylineState defaultState) {
        List<KeylineState> steps = new ArrayList<>();
        steps.add(defaultState);
        int lastInBoundsKeylineIndex = findLastInBoundsKeylineIndex(carousel, defaultState);
        if (isLastFocalItemAtRightOfContainer(carousel, defaultState) || lastInBoundsKeylineIndex == -1) {
            return steps;
        }
        int start = defaultState.getLastFocalKeylineIndex();
        int numberOfSteps = lastInBoundsKeylineIndex - start;
        float originalStart = defaultState.getFirstKeyline().locOffset - (defaultState.getFirstKeyline().maskedItemSize / 2.0f);
        for (int i = 0; i < numberOfSteps; i++) {
            KeylineState prevStepState = steps.get(steps.size() - 1);
            int itemOrigIndex = lastInBoundsKeylineIndex - i;
            int dstIndex = 0;
            if (itemOrigIndex + 1 < defaultState.getKeylines().size()) {
                float originalAdjacentMaskRight = defaultState.getKeylines().get(itemOrigIndex + 1).mask;
                dstIndex = findLastIndexBeforeFirstFocalKeylineWithMask(prevStepState, originalAdjacentMaskRight) + 1;
            }
            int dstIndex2 = dstIndex;
            int dstIndex3 = defaultState.getFirstFocalKeylineIndex();
            int newFirstFocalIndex = dstIndex3 + i + 1;
            int newLastFocalIndex = defaultState.getLastFocalKeylineIndex() + i + 1;
            KeylineState shifted = moveKeylineAndCreateKeylineState(prevStepState, lastInBoundsKeylineIndex, dstIndex2, originalStart, newFirstFocalIndex, newLastFocalIndex);
            steps.add(shifted);
        }
        return steps;
    }

    private static KeylineState moveKeylineAndCreateKeylineState(KeylineState state, int keylineSrcIndex, int keylineDstIndex, float startOffset, int newFirstFocalIndex, int newLastFocalIndex) {
        List<KeylineState.Keyline> tmpKeylines = new ArrayList<>(state.getKeylines());
        KeylineState.Keyline item = tmpKeylines.remove(keylineSrcIndex);
        tmpKeylines.add(keylineDstIndex, item);
        KeylineState.Builder builder = new KeylineState.Builder(state.getItemSize());
        int j = 0;
        while (j < tmpKeylines.size()) {
            KeylineState.Keyline k = tmpKeylines.get(j);
            float offset = (k.maskedItemSize / 2.0f) + startOffset;
            boolean isFocal = j >= newFirstFocalIndex && j <= newLastFocalIndex;
            builder.addKeyline(offset, k.mask, k.maskedItemSize, isFocal);
            startOffset += k.maskedItemSize;
            j++;
        }
        return builder.build();
    }

    private static int findFirstIndexAfterLastFocalKeylineWithMask(KeylineState state, float mask) {
        int focalEndIndex = state.getLastFocalKeylineIndex();
        for (int i = focalEndIndex; i < state.getKeylines().size(); i++) {
            if (mask == state.getKeylines().get(i).mask) {
                return i;
            }
        }
        return state.getKeylines().size() - 1;
    }

    private static int findLastIndexBeforeFirstFocalKeylineWithMask(KeylineState state, float mask) {
        int focalStartIndex = state.getFirstFocalKeylineIndex() - 1;
        for (int i = focalStartIndex; i >= 0; i--) {
            if (mask == state.getKeylines().get(i).mask) {
                return i;
            }
        }
        return 0;
    }

    private static int findFirstInBoundsKeylineIndex(KeylineState state) {
        for (int i = 0; i < state.getKeylines().size(); i++) {
            if (state.getKeylines().get(i).locOffset >= 0.0f) {
                return i;
            }
        }
        return -1;
    }

    private static int findLastInBoundsKeylineIndex(Carousel carousel, KeylineState state) {
        for (int i = state.getKeylines().size() - 1; i >= 0; i--) {
            if (state.getKeylines().get(i).locOffset <= carousel.getContainerWidth()) {
                return i;
            }
        }
        return -1;
    }
}

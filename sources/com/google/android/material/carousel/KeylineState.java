package com.google.android.material.carousel;

import com.google.android.material.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class KeylineState {
    private final int firstFocalKeylineIndex;
    private final float itemSize;
    private final List<Keyline> keylines;
    private final int lastFocalKeylineIndex;

    private KeylineState(float itemSize, List<Keyline> keylines, int firstFocalKeylineIndex, int lastFocalKeylineIndex) {
        this.itemSize = itemSize;
        this.keylines = Collections.unmodifiableList(keylines);
        this.firstFocalKeylineIndex = firstFocalKeylineIndex;
        this.lastFocalKeylineIndex = lastFocalKeylineIndex;
    }

    float getItemSize() {
        return this.itemSize;
    }

    List<Keyline> getKeylines() {
        return this.keylines;
    }

    Keyline getFirstFocalKeyline() {
        return this.keylines.get(this.firstFocalKeylineIndex);
    }

    int getFirstFocalKeylineIndex() {
        return this.firstFocalKeylineIndex;
    }

    Keyline getLastFocalKeyline() {
        return this.keylines.get(this.lastFocalKeylineIndex);
    }

    int getLastFocalKeylineIndex() {
        return this.lastFocalKeylineIndex;
    }

    Keyline getFirstKeyline() {
        return this.keylines.get(0);
    }

    Keyline getLastKeyline() {
        return this.keylines.get(this.keylines.size() - 1);
    }

    static KeylineState lerp(KeylineState from, KeylineState to, float progress) {
        if (from.getItemSize() != to.getItemSize()) {
            throw new IllegalArgumentException("Keylines being linearly interpolated must have the same item size.");
        }
        List<Keyline> fromKeylines = from.getKeylines();
        List<Keyline> toKeylines = to.getKeylines();
        if (fromKeylines.size() != toKeylines.size()) {
            throw new IllegalArgumentException("Keylines being linearly interpolated must have the same number of keylines.");
        }
        List<Keyline> keylines = new ArrayList<>();
        for (int i = 0; i < from.getKeylines().size(); i++) {
            keylines.add(Keyline.lerp(fromKeylines.get(i), toKeylines.get(i), progress));
        }
        int focalKeylineFirstIndex = AnimationUtils.lerp(from.getFirstFocalKeylineIndex(), to.getFirstFocalKeylineIndex(), progress);
        int focalKeylineLastIndex = AnimationUtils.lerp(from.getLastFocalKeylineIndex(), to.getLastFocalKeylineIndex(), progress);
        return new KeylineState(from.getItemSize(), keylines, focalKeylineFirstIndex, focalKeylineLastIndex);
    }

    static KeylineState reverse(KeylineState keylineState) {
        Builder builder = new Builder(keylineState.getItemSize());
        float start = keylineState.getFirstKeyline().locOffset - (keylineState.getFirstKeyline().maskedItemSize / 2.0f);
        int i = keylineState.getKeylines().size() - 1;
        while (i >= 0) {
            Keyline k = keylineState.getKeylines().get(i);
            float offset = (k.maskedItemSize / 2.0f) + start;
            boolean isFocal = i >= keylineState.getFirstFocalKeylineIndex() && i <= keylineState.getLastFocalKeylineIndex();
            builder.addKeyline(offset, k.mask, k.maskedItemSize, isFocal);
            start += k.maskedItemSize;
            i--;
        }
        return builder.build();
    }

    static final class Builder {
        private static final int NO_INDEX = -1;
        private static final float UNKNOWN_LOC = Float.MIN_VALUE;
        private final float itemSize;
        private Keyline tmpFirstFocalKeyline;
        private Keyline tmpLastFocalKeyline;
        private final List<Keyline> tmpKeylines = new ArrayList();
        private int firstFocalKeylineIndex = -1;
        private int lastFocalKeylineIndex = -1;
        private float lastKeylineMaskedSize = 0.0f;

        Builder(float itemSize) {
            this.itemSize = itemSize;
        }

        Builder addKeyline(float offsetLoc, float mask, float maskedItemSize) {
            return addKeyline(offsetLoc, mask, maskedItemSize, false);
        }

        Builder addKeyline(float offsetLoc, float mask, float maskedItemSize, boolean isFocal) {
            if (maskedItemSize <= 0.0f) {
                return this;
            }
            Keyline tmpKeyline = new Keyline(Float.MIN_VALUE, offsetLoc, mask, maskedItemSize);
            if (isFocal) {
                if (this.tmpFirstFocalKeyline == null) {
                    this.tmpFirstFocalKeyline = tmpKeyline;
                    this.firstFocalKeylineIndex = this.tmpKeylines.size();
                }
                if (this.lastFocalKeylineIndex != -1 && this.tmpKeylines.size() - this.lastFocalKeylineIndex > 1) {
                    throw new IllegalArgumentException("Keylines marked as focal must be placed next to each other. There cannot be non-focal keylines between focal keylines.");
                }
                if (maskedItemSize != this.tmpFirstFocalKeyline.maskedItemSize) {
                    throw new IllegalArgumentException("Keylines that are marked as focal must all have the same masked item size.");
                }
                this.tmpLastFocalKeyline = tmpKeyline;
                this.lastFocalKeylineIndex = this.tmpKeylines.size();
            } else {
                if (this.tmpFirstFocalKeyline == null && tmpKeyline.maskedItemSize < this.lastKeylineMaskedSize) {
                    throw new IllegalArgumentException("Keylines before the first focal keyline must be ordered by incrementing masked item size.");
                }
                if (this.tmpLastFocalKeyline != null && tmpKeyline.maskedItemSize > this.lastKeylineMaskedSize) {
                    throw new IllegalArgumentException("Keylines after the last focal keyline must be ordered by decreasing masked item size.");
                }
            }
            this.lastKeylineMaskedSize = tmpKeyline.maskedItemSize;
            this.tmpKeylines.add(tmpKeyline);
            return this;
        }

        Builder addKeylineRange(float offsetLoc, float mask, float maskedItemSize, int count) {
            return addKeylineRange(offsetLoc, mask, maskedItemSize, count, false);
        }

        Builder addKeylineRange(float offsetLoc, float mask, float maskedItemSize, int count, boolean isFocal) {
            if (count <= 0 || maskedItemSize <= 0.0f) {
                return this;
            }
            for (int i = 0; i < count; i++) {
                float loc = (i * maskedItemSize) + offsetLoc;
                addKeyline(loc, mask, maskedItemSize, isFocal);
            }
            return this;
        }

        KeylineState build() {
            if (this.tmpFirstFocalKeyline == null) {
                throw new IllegalStateException("There must be a keyline marked as focal.");
            }
            List<Keyline> keylines = new ArrayList<>();
            for (int i = 0; i < this.tmpKeylines.size(); i++) {
                Keyline tmpKeyline = this.tmpKeylines.get(i);
                Keyline keyline = new Keyline(calculateKeylineLocationForItemPosition(this.tmpFirstFocalKeyline.locOffset, this.itemSize, this.firstFocalKeylineIndex, i), tmpKeyline.locOffset, tmpKeyline.mask, tmpKeyline.maskedItemSize);
                keylines.add(keyline);
            }
            return new KeylineState(this.itemSize, keylines, this.firstFocalKeylineIndex, this.lastFocalKeylineIndex);
        }

        private static float calculateKeylineLocationForItemPosition(float firstFocalLoc, float itemSize, int firstFocalPosition, int itemPosition) {
            return (firstFocalLoc - (firstFocalPosition * itemSize)) + (itemPosition * itemSize);
        }
    }

    static final class Keyline {
        final float loc;
        final float locOffset;
        final float mask;
        final float maskedItemSize;

        Keyline(float loc, float locOffset, float mask, float maskedItemSize) {
            this.loc = loc;
            this.locOffset = locOffset;
            this.mask = mask;
            this.maskedItemSize = maskedItemSize;
        }

        static Keyline lerp(Keyline from, Keyline to, float progress) {
            return new Keyline(AnimationUtils.lerp(from.loc, to.loc, progress), AnimationUtils.lerp(from.locOffset, to.locOffset, progress), AnimationUtils.lerp(from.mask, to.mask, progress), AnimationUtils.lerp(from.maskedItemSize, to.maskedItemSize, progress));
        }
    }
}

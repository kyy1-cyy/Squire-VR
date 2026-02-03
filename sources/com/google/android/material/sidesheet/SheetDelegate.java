package com.google.android.material.sidesheet;

import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
abstract class SheetDelegate {
    abstract int calculateInnerMargin(ViewGroup.MarginLayoutParams marginLayoutParams);

    abstract float calculateSlideOffset(int i);

    abstract int calculateTargetStateOnViewReleased(View view, float f, float f2);

    abstract int getExpandedOffset();

    abstract int getHiddenOffset();

    abstract <V extends View> int getOuterEdge(V v);

    abstract int getSheetEdge();

    abstract boolean isSettling(View view, int i, boolean z);

    abstract boolean shouldHide(View view, float f);

    abstract void updateCoplanarSiblingLayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams, int i, int i2);

    SheetDelegate() {
    }
}

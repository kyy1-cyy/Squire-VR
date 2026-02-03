package com.google.android.material.textfield;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import androidx.core.view.accessibility.AccessibilityManagerCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.material.internal.CheckableImageButton;

/* loaded from: classes.dex */
abstract class EndIconDelegate {
    final Context context;
    final CheckableImageButton endIconView;
    final EndCompoundLayout endLayout;
    final TextInputLayout textInputLayout;

    EndIconDelegate(EndCompoundLayout endLayout) {
        this.textInputLayout = endLayout.textInputLayout;
        this.endLayout = endLayout;
        this.context = endLayout.getContext();
        this.endIconView = endLayout.getEndIconView();
    }

    void setUp() {
    }

    void tearDown() {
    }

    int getIconDrawableResId() {
        return 0;
    }

    int getIconContentDescriptionResId() {
        return 0;
    }

    boolean isIconCheckable() {
        return false;
    }

    boolean isIconChecked() {
        return false;
    }

    boolean isIconActivable() {
        return false;
    }

    boolean isIconActivated() {
        return false;
    }

    boolean shouldTintIconOnError() {
        return false;
    }

    boolean isBoxBackgroundModeSupported(int boxBackgroundMode) {
        return true;
    }

    void onSuffixVisibilityChanged(boolean visible) {
    }

    View.OnClickListener getOnIconClickListener() {
        return null;
    }

    View.OnFocusChangeListener getOnEditTextFocusChangeListener() {
        return null;
    }

    View.OnFocusChangeListener getOnIconViewFocusChangeListener() {
        return null;
    }

    AccessibilityManagerCompat.TouchExplorationStateChangeListener getTouchExplorationStateChangeListener() {
        return null;
    }

    void onEditTextAttached(EditText editText) {
    }

    void beforeEditTextChanged(CharSequence s, int start, int count, int after) {
    }

    void afterEditTextChanged(Editable s) {
    }

    void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
    }

    void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
    }

    final void refreshIconState() {
        this.endLayout.refreshIconState(false);
    }
}

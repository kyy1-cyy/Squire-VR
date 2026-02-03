package com.google.android.material.datepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

/* loaded from: classes.dex */
public abstract class DayViewDecorator implements Parcelable {
    public void initialize(Context context) {
    }

    public Drawable getCompoundDrawableLeft(Context context, int year, int month, int day, boolean valid, boolean selected) {
        return null;
    }

    public Drawable getCompoundDrawableTop(Context context, int year, int month, int day, boolean valid, boolean selected) {
        return null;
    }

    public Drawable getCompoundDrawableRight(Context context, int year, int month, int day, boolean valid, boolean selected) {
        return null;
    }

    public Drawable getCompoundDrawableBottom(Context context, int year, int month, int day, boolean valid, boolean selected) {
        return null;
    }

    public ColorStateList getBackgroundColor(Context context, int year, int month, int day, boolean valid, boolean selected) {
        return null;
    }

    public CharSequence getContentDescription(Context context, int year, int month, int day, boolean valid, boolean selected, CharSequence originalContentDescription) {
        return originalContentDescription;
    }
}

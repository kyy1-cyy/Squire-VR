package com.google.android.material.timepicker;

import android.text.InputFilter;
import android.text.Spanned;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
class MaxInputValidator implements InputFilter {
    private int max;

    public MaxInputValidator(int max) {
        this.max = max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return this.max;
    }

    @Override // android.text.InputFilter
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) throws NumberFormatException {
        try {
            StringBuilder builder = new StringBuilder(dest);
            builder.replace(dstart, dend, source.subSequence(start, end).toString());
            String newVal = builder.toString();
            int input = Integer.parseInt(newVal);
            if (input <= this.max) {
                return null;
            }
            return HttpUrl.FRAGMENT_ENCODE_SET;
        } catch (NumberFormatException e) {
            return HttpUrl.FRAGMENT_ENCODE_SET;
        }
    }
}

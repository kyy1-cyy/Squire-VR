package com.squire.vr;

import android.view.View;

/* loaded from: classes3.dex */
public class FadeOutRunnable implements Runnable {
    private final View view;

    public FadeOutRunnable(View view) {
        this.view = view;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.view.setVisibility(8);
        this.view.setAlpha(1.0f);
    }
}

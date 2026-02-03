package com.google.android.material.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public class ClippableRoundedCornerLayout extends FrameLayout {
    private Path path;

    public ClippableRoundedCornerLayout(Context context) {
        super(context);
    }

    public ClippableRoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClippableRoundedCornerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.path == null) {
            super.dispatchDraw(canvas);
            return;
        }
        int save = canvas.save();
        canvas.clipPath(this.path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    public void resetClipBoundsAndCornerRadius() {
        this.path = null;
        invalidate();
    }

    public void updateClipBoundsAndCornerRadius(Rect rect, float cornerRadius) {
        updateClipBoundsAndCornerRadius(rect.left, rect.top, rect.right, rect.bottom, cornerRadius);
    }

    public void updateClipBoundsAndCornerRadius(float left, float top, float right, float bottom, float cornerRadius) {
        updateClipBoundsAndCornerRadius(new RectF(left, top, right, bottom), cornerRadius);
    }

    public void updateClipBoundsAndCornerRadius(RectF rectF, float cornerRadius) {
        if (this.path == null) {
            this.path = new Path();
        }
        this.path.reset();
        this.path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
        this.path.close();
        invalidate();
    }
}

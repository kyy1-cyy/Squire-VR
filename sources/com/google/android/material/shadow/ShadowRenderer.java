package com.google.android.material.shadow;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
public class ShadowRenderer {
    private static final int COLOR_ALPHA_END = 0;
    private static final int COLOR_ALPHA_MIDDLE = 20;
    private static final int COLOR_ALPHA_START = 68;
    private final Paint cornerShadowPaint;
    private final Paint edgeShadowPaint;
    private final Path scratch;
    private int shadowEndColor;
    private int shadowMiddleColor;
    private final Paint shadowPaint;
    private int shadowStartColor;
    private final Paint transparentPaint;
    private static final int[] edgeColors = new int[3];
    private static final float[] edgePositions = {0.0f, 0.5f, 1.0f};
    private static final int[] cornerColors = new int[4];
    private static final float[] cornerPositions = {0.0f, 0.0f, 0.5f, 1.0f};

    public ShadowRenderer() {
        this(ViewCompat.MEASURED_STATE_MASK);
    }

    public ShadowRenderer(int color) {
        this.scratch = new Path();
        this.transparentPaint = new Paint();
        this.shadowPaint = new Paint();
        setShadowColor(color);
        this.transparentPaint.setColor(0);
        this.cornerShadowPaint = new Paint(4);
        this.cornerShadowPaint.setStyle(Paint.Style.FILL);
        this.edgeShadowPaint = new Paint(this.cornerShadowPaint);
    }

    public void setShadowColor(int color) {
        this.shadowStartColor = ColorUtils.setAlphaComponent(color, COLOR_ALPHA_START);
        this.shadowMiddleColor = ColorUtils.setAlphaComponent(color, 20);
        this.shadowEndColor = ColorUtils.setAlphaComponent(color, 0);
        this.shadowPaint.setColor(this.shadowStartColor);
    }

    public void drawEdgeShadow(Canvas canvas, Matrix transform, RectF bounds, int elevation) {
        bounds.bottom += elevation;
        bounds.offset(0.0f, -elevation);
        edgeColors[0] = this.shadowEndColor;
        edgeColors[1] = this.shadowMiddleColor;
        edgeColors[2] = this.shadowStartColor;
        this.edgeShadowPaint.setShader(new LinearGradient(bounds.left, bounds.top, bounds.left, bounds.bottom, edgeColors, edgePositions, Shader.TileMode.CLAMP));
        canvas.save();
        canvas.concat(transform);
        canvas.drawRect(bounds, this.edgeShadowPaint);
        canvas.restore();
    }

    public void drawCornerShadow(Canvas canvas, Matrix matrix, RectF bounds, int elevation, float startAngle, float sweepAngle) {
        boolean drawShadowInsideBounds = sweepAngle < 0.0f;
        Path arcBounds = this.scratch;
        if (drawShadowInsideBounds) {
            cornerColors[0] = 0;
            cornerColors[1] = this.shadowEndColor;
            cornerColors[2] = this.shadowMiddleColor;
            cornerColors[3] = this.shadowStartColor;
        } else {
            arcBounds.rewind();
            arcBounds.moveTo(bounds.centerX(), bounds.centerY());
            arcBounds.arcTo(bounds, startAngle, sweepAngle);
            arcBounds.close();
            bounds.inset(-elevation, -elevation);
            cornerColors[0] = 0;
            cornerColors[1] = this.shadowStartColor;
            cornerColors[2] = this.shadowMiddleColor;
            cornerColors[3] = this.shadowEndColor;
        }
        float radius = bounds.width() / 2.0f;
        if (radius <= 0.0f) {
            return;
        }
        float startRatio = 1.0f - (elevation / radius);
        float midRatio = startRatio + ((1.0f - startRatio) / 2.0f);
        cornerPositions[1] = startRatio;
        cornerPositions[2] = midRatio;
        RadialGradient shader = new RadialGradient(bounds.centerX(), bounds.centerY(), radius, cornerColors, cornerPositions, Shader.TileMode.CLAMP);
        this.cornerShadowPaint.setShader(shader);
        canvas.save();
        canvas.concat(matrix);
        canvas.scale(1.0f, bounds.height() / bounds.width());
        if (!drawShadowInsideBounds) {
            canvas.clipPath(arcBounds, Region.Op.DIFFERENCE);
            canvas.drawPath(arcBounds, this.transparentPaint);
        }
        canvas.drawArc(bounds, startAngle, sweepAngle, true, this.cornerShadowPaint);
        canvas.restore();
    }

    public void drawInnerCornerShadow(Canvas canvas, Matrix matrix, RectF bounds, int elevation, float startAngle, float sweepAngle, float[] cornerPosition) {
        if (sweepAngle > 0.0f) {
            startAngle += sweepAngle;
            sweepAngle = -sweepAngle;
        }
        drawCornerShadow(canvas, matrix, bounds, elevation, startAngle, sweepAngle);
        Path shapeBounds = this.scratch;
        shapeBounds.rewind();
        shapeBounds.moveTo(cornerPosition[0], cornerPosition[1]);
        shapeBounds.arcTo(bounds, startAngle, sweepAngle);
        shapeBounds.close();
        canvas.save();
        canvas.concat(matrix);
        canvas.scale(1.0f, bounds.height() / bounds.width());
        canvas.drawPath(shapeBounds, this.transparentPaint);
        canvas.drawPath(shapeBounds, this.shadowPaint);
        canvas.restore();
    }

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }
}

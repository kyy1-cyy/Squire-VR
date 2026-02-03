package com.google.android.material.bottomappbar;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.ShapePath;

/* loaded from: classes.dex */
public class BottomAppBarTopEdgeTreatment extends EdgeTreatment implements Cloneable {
    private static final int ANGLE_LEFT = 180;
    private static final int ANGLE_UP = 270;
    private static final int ARC_HALF = 180;
    private static final int ARC_QUARTER = 90;
    private static final float ROUNDED_CORNER_FAB_OFFSET = 1.75f;
    private float cradleVerticalOffset;
    private float fabCornerSize = -1.0f;
    private float fabDiameter;
    private float fabMargin;
    private float horizontalOffset;
    private float roundedCornerRadius;

    public BottomAppBarTopEdgeTreatment(float fabMargin, float roundedCornerRadius, float cradleVerticalOffset) {
        this.fabMargin = fabMargin;
        this.roundedCornerRadius = roundedCornerRadius;
        setCradleVerticalOffset(cradleVerticalOffset);
        this.horizontalOffset = 0.0f;
    }

    @Override // com.google.android.material.shape.EdgeTreatment
    public void getEdgePath(float length, float center, float interpolation, ShapePath shapePath) {
        float verticalOffset;
        float arcOffset;
        if (this.fabDiameter != 0.0f) {
            float cradleDiameter = (this.fabMargin * 2.0f) + this.fabDiameter;
            float cradleRadius = cradleDiameter / 2.0f;
            float roundedCornerOffset = interpolation * this.roundedCornerRadius;
            float middle = center + this.horizontalOffset;
            float verticalOffset2 = (this.cradleVerticalOffset * interpolation) + ((1.0f - interpolation) * cradleRadius);
            float verticalOffsetRatio = verticalOffset2 / cradleRadius;
            if (verticalOffsetRatio >= 1.0f) {
                shapePath.lineTo(length, 0.0f);
                return;
            }
            float cornerSize = this.fabCornerSize * interpolation;
            boolean useCircleCutout = this.fabCornerSize == -1.0f || Math.abs((this.fabCornerSize * 2.0f) - this.fabDiameter) < 0.1f;
            if (useCircleCutout) {
                verticalOffset = verticalOffset2;
                arcOffset = 0.0f;
            } else {
                verticalOffset = 0.0f;
                arcOffset = 1.75f;
            }
            float distanceBetweenCenters = cradleRadius + roundedCornerOffset;
            float distanceBetweenCentersSquared = distanceBetweenCenters * distanceBetweenCenters;
            float distanceY = verticalOffset + roundedCornerOffset;
            float distanceX = (float) Math.sqrt(distanceBetweenCentersSquared - (distanceY * distanceY));
            float leftRoundedCornerCircleX = middle - distanceX;
            float rightRoundedCornerCircleX = middle + distanceX;
            float cornerRadiusArcLength = (float) Math.toDegrees(Math.atan(distanceX / distanceY));
            float cutoutArcOffset = (90.0f - cornerRadiusArcLength) + arcOffset;
            shapePath.lineTo(leftRoundedCornerCircleX, 0.0f);
            shapePath.addArc(leftRoundedCornerCircleX - roundedCornerOffset, 0.0f, leftRoundedCornerCircleX + roundedCornerOffset, roundedCornerOffset * 2.0f, 270.0f, cornerRadiusArcLength);
            if (useCircleCutout) {
                shapePath.addArc(middle - cradleRadius, (-cradleRadius) - verticalOffset, middle + cradleRadius, cradleRadius - verticalOffset, 180.0f - cutoutArcOffset, (cutoutArcOffset * 2.0f) - 180.0f);
            } else {
                float cutoutDiameter = this.fabMargin + (cornerSize * 2.0f);
                shapePath.addArc(middle - cradleRadius, -(cornerSize + this.fabMargin), (middle - cradleRadius) + cutoutDiameter, this.fabMargin + cornerSize, 180.0f - cutoutArcOffset, ((cutoutArcOffset * 2.0f) - 180.0f) / 2.0f);
                shapePath.lineTo((middle + cradleRadius) - (cornerSize + (this.fabMargin / 2.0f)), cornerSize + this.fabMargin);
                shapePath.addArc((middle + cradleRadius) - ((cornerSize * 2.0f) + this.fabMargin), -(cornerSize + this.fabMargin), middle + cradleRadius, this.fabMargin + cornerSize, 90.0f, cutoutArcOffset - 90.0f);
            }
            shapePath.addArc(rightRoundedCornerCircleX - roundedCornerOffset, 0.0f, rightRoundedCornerCircleX + roundedCornerOffset, roundedCornerOffset * 2.0f, 270.0f - cornerRadiusArcLength, cornerRadiusArcLength);
            shapePath.lineTo(length, 0.0f);
            return;
        }
        shapePath.lineTo(length, 0.0f);
    }

    public float getFabDiameter() {
        return this.fabDiameter;
    }

    public void setFabDiameter(float fabDiameter) {
        this.fabDiameter = fabDiameter;
    }

    void setHorizontalOffset(float horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public float getHorizontalOffset() {
        return this.horizontalOffset;
    }

    float getCradleVerticalOffset() {
        return this.cradleVerticalOffset;
    }

    void setCradleVerticalOffset(float cradleVerticalOffset) {
        if (cradleVerticalOffset < 0.0f) {
            throw new IllegalArgumentException("cradleVerticalOffset must be positive.");
        }
        this.cradleVerticalOffset = cradleVerticalOffset;
    }

    float getFabCradleMargin() {
        return this.fabMargin;
    }

    void setFabCradleMargin(float fabMargin) {
        this.fabMargin = fabMargin;
    }

    float getFabCradleRoundedCornerRadius() {
        return this.roundedCornerRadius;
    }

    void setFabCradleRoundedCornerRadius(float roundedCornerRadius) {
        this.roundedCornerRadius = roundedCornerRadius;
    }

    public float getFabCornerRadius() {
        return this.fabCornerSize;
    }

    public void setFabCornerSize(float size) {
        this.fabCornerSize = size;
    }
}

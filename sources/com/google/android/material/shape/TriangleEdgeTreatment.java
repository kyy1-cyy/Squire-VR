package com.google.android.material.shape;

/* loaded from: classes.dex */
public class TriangleEdgeTreatment extends EdgeTreatment {
    private final boolean inside;
    private final float size;

    public TriangleEdgeTreatment(float size, boolean inside) {
        this.size = size;
        this.inside = inside;
    }

    @Override // com.google.android.material.shape.EdgeTreatment
    public void getEdgePath(float length, float center, float interpolation, ShapePath shapePath) {
        if (this.inside) {
            shapePath.lineTo(center - (this.size * interpolation), 0.0f);
            shapePath.lineTo(center, this.size * interpolation, (this.size * interpolation) + center, 0.0f);
            shapePath.lineTo(length, 0.0f);
        } else {
            shapePath.lineTo(center - (this.size * interpolation), 0.0f, center, (-this.size) * interpolation);
            shapePath.lineTo((this.size * interpolation) + center, 0.0f, length, 0.0f);
        }
    }
}

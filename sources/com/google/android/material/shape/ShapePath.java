package com.google.android.material.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import com.google.android.material.shadow.ShadowRenderer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ShapePath {
    protected static final float ANGLE_LEFT = 180.0f;
    private static final float ANGLE_UP = 270.0f;
    private boolean containsIncompatibleShadowOp;

    @Deprecated
    public float currentShadowAngle;

    @Deprecated
    public float endShadowAngle;

    @Deprecated
    public float endX;

    @Deprecated
    public float endY;
    private final List<PathOperation> operations = new ArrayList();
    private final List<ShadowCompatOperation> shadowCompatOperations = new ArrayList();

    @Deprecated
    public float startX;

    @Deprecated
    public float startY;

    public static abstract class PathOperation {
        protected final Matrix matrix = new Matrix();

        public abstract void applyToPath(Matrix matrix, Path path);
    }

    public ShapePath() {
        reset(0.0f, 0.0f);
    }

    public ShapePath(float startX, float startY) {
        reset(startX, startY);
    }

    public void reset(float startX, float startY) {
        reset(startX, startY, ANGLE_UP, 0.0f);
    }

    public void reset(float startX, float startY, float shadowStartAngle, float shadowSweepAngle) {
        setStartX(startX);
        setStartY(startY);
        setEndX(startX);
        setEndY(startY);
        setCurrentShadowAngle(shadowStartAngle);
        setEndShadowAngle((shadowStartAngle + shadowSweepAngle) % 360.0f);
        this.operations.clear();
        this.shadowCompatOperations.clear();
        this.containsIncompatibleShadowOp = false;
    }

    public void lineTo(float x, float y) {
        PathLineOperation operation = new PathLineOperation();
        operation.x = x;
        operation.y = y;
        this.operations.add(operation);
        LineShadowOperation shadowOperation = new LineShadowOperation(operation, getEndX(), getEndY());
        addShadowCompatOperation(shadowOperation, shadowOperation.getAngle() + ANGLE_UP, shadowOperation.getAngle() + ANGLE_UP);
        setEndX(x);
        setEndY(y);
    }

    public void lineTo(float x1, float y1, float x2, float y2) {
        if ((Math.abs(x1 - getEndX()) < 0.001f && Math.abs(y1 - getEndY()) < 0.001f) || (Math.abs(x1 - x2) < 0.001f && Math.abs(y1 - y2) < 0.001f)) {
            lineTo(x2, y2);
            return;
        }
        PathLineOperation operation1 = new PathLineOperation();
        operation1.x = x1;
        operation1.y = y1;
        this.operations.add(operation1);
        PathLineOperation operation2 = new PathLineOperation();
        operation2.x = x2;
        operation2.y = y2;
        this.operations.add(operation2);
        InnerCornerShadowOperation shadowOperation = new InnerCornerShadowOperation(operation1, operation2, getEndX(), getEndY());
        if (shadowOperation.getSweepAngle() > 0.0f) {
            lineTo(x1, y1);
            lineTo(x2, y2);
        } else {
            addShadowCompatOperation(shadowOperation, shadowOperation.getStartAngle() + ANGLE_UP, shadowOperation.getEndAngle() + ANGLE_UP);
            setEndX(x2);
            setEndY(y2);
        }
    }

    public void quadToPoint(float controlX, float controlY, float toX, float toY) {
        PathQuadOperation operation = new PathQuadOperation();
        operation.setControlX(controlX);
        operation.setControlY(controlY);
        operation.setEndX(toX);
        operation.setEndY(toY);
        this.operations.add(operation);
        this.containsIncompatibleShadowOp = true;
        setEndX(toX);
        setEndY(toY);
    }

    public void cubicToPoint(float controlX1, float controlY1, float controlX2, float controlY2, float toX, float toY) {
        PathCubicOperation operation = new PathCubicOperation(controlX1, controlY1, controlX2, controlY2, toX, toY);
        this.operations.add(operation);
        this.containsIncompatibleShadowOp = true;
        setEndX(toX);
        setEndY(toY);
    }

    public void addArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle) {
        PathArcOperation operation = new PathArcOperation(left, top, right, bottom);
        operation.setStartAngle(startAngle);
        operation.setSweepAngle(sweepAngle);
        this.operations.add(operation);
        ArcShadowOperation arcShadowOperation = new ArcShadowOperation(operation);
        float endAngle = startAngle + sweepAngle;
        boolean drawShadowInsideBounds = sweepAngle < 0.0f;
        addShadowCompatOperation(arcShadowOperation, drawShadowInsideBounds ? (startAngle + ANGLE_LEFT) % 360.0f : startAngle, drawShadowInsideBounds ? (ANGLE_LEFT + endAngle) % 360.0f : endAngle);
        setEndX(((left + right) * 0.5f) + (((right - left) / 2.0f) * ((float) Math.cos(Math.toRadians(startAngle + sweepAngle)))));
        setEndY(((top + bottom) * 0.5f) + (((bottom - top) / 2.0f) * ((float) Math.sin(Math.toRadians(startAngle + sweepAngle)))));
    }

    public void applyToPath(Matrix transform, Path path) {
        int size = this.operations.size();
        for (int i = 0; i < size; i++) {
            PathOperation operation = this.operations.get(i);
            operation.applyToPath(transform, path);
        }
    }

    ShadowCompatOperation createShadowCompatOperation(Matrix transform) {
        addConnectingShadowIfNecessary(getEndShadowAngle());
        final Matrix transformCopy = new Matrix(transform);
        final List<ShadowCompatOperation> operations = new ArrayList<>(this.shadowCompatOperations);
        return new ShadowCompatOperation() { // from class: com.google.android.material.shape.ShapePath.1
            @Override // com.google.android.material.shape.ShapePath.ShadowCompatOperation
            public void draw(Matrix matrix, ShadowRenderer shadowRenderer, int shadowElevation, Canvas canvas) {
                for (ShadowCompatOperation op : operations) {
                    op.draw(transformCopy, shadowRenderer, shadowElevation, canvas);
                }
            }
        };
    }

    private void addShadowCompatOperation(ShadowCompatOperation shadowOperation, float startShadowAngle, float endShadowAngle) {
        addConnectingShadowIfNecessary(startShadowAngle);
        this.shadowCompatOperations.add(shadowOperation);
        setCurrentShadowAngle(endShadowAngle);
    }

    boolean containsIncompatibleShadowOp() {
        return this.containsIncompatibleShadowOp;
    }

    private void addConnectingShadowIfNecessary(float nextShadowAngle) {
        if (getCurrentShadowAngle() == nextShadowAngle) {
            return;
        }
        float shadowSweep = ((nextShadowAngle - getCurrentShadowAngle()) + 360.0f) % 360.0f;
        if (shadowSweep > ANGLE_LEFT) {
            return;
        }
        PathArcOperation pathArcOperation = new PathArcOperation(getEndX(), getEndY(), getEndX(), getEndY());
        pathArcOperation.setStartAngle(getCurrentShadowAngle());
        pathArcOperation.setSweepAngle(shadowSweep);
        this.shadowCompatOperations.add(new ArcShadowOperation(pathArcOperation));
        setCurrentShadowAngle(nextShadowAngle);
    }

    float getStartX() {
        return this.startX;
    }

    float getStartY() {
        return this.startY;
    }

    float getEndX() {
        return this.endX;
    }

    float getEndY() {
        return this.endY;
    }

    private float getCurrentShadowAngle() {
        return this.currentShadowAngle;
    }

    private float getEndShadowAngle() {
        return this.endShadowAngle;
    }

    private void setStartX(float startX) {
        this.startX = startX;
    }

    private void setStartY(float startY) {
        this.startY = startY;
    }

    private void setEndX(float endX) {
        this.endX = endX;
    }

    private void setEndY(float endY) {
        this.endY = endY;
    }

    private void setCurrentShadowAngle(float currentShadowAngle) {
        this.currentShadowAngle = currentShadowAngle;
    }

    private void setEndShadowAngle(float endShadowAngle) {
        this.endShadowAngle = endShadowAngle;
    }

    static abstract class ShadowCompatOperation {
        static final Matrix IDENTITY_MATRIX = new Matrix();
        final Matrix renderMatrix = new Matrix();

        public abstract void draw(Matrix matrix, ShadowRenderer shadowRenderer, int i, Canvas canvas);

        ShadowCompatOperation() {
        }

        public final void draw(ShadowRenderer shadowRenderer, int shadowElevation, Canvas canvas) {
            draw(IDENTITY_MATRIX, shadowRenderer, shadowElevation, canvas);
        }
    }

    static class LineShadowOperation extends ShadowCompatOperation {
        private final PathLineOperation operation;
        private final float startX;
        private final float startY;

        public LineShadowOperation(PathLineOperation operation, float startX, float startY) {
            this.operation = operation;
            this.startX = startX;
            this.startY = startY;
        }

        @Override // com.google.android.material.shape.ShapePath.ShadowCompatOperation
        public void draw(Matrix transform, ShadowRenderer shadowRenderer, int shadowElevation, Canvas canvas) {
            float height = this.operation.y - this.startY;
            float width = this.operation.x - this.startX;
            RectF rect = new RectF(0.0f, 0.0f, (float) Math.hypot(height, width), 0.0f);
            this.renderMatrix.set(transform);
            this.renderMatrix.preTranslate(this.startX, this.startY);
            this.renderMatrix.preRotate(getAngle());
            shadowRenderer.drawEdgeShadow(canvas, this.renderMatrix, rect, shadowElevation);
        }

        float getAngle() {
            return (float) Math.toDegrees(Math.atan((this.operation.y - this.startY) / (this.operation.x - this.startX)));
        }
    }

    static class InnerCornerShadowOperation extends ShadowCompatOperation {
        private final PathLineOperation operation1;
        private final PathLineOperation operation2;
        private final float startX;
        private final float startY;

        public InnerCornerShadowOperation(PathLineOperation operation1, PathLineOperation operation2, float startX, float startY) {
            this.operation1 = operation1;
            this.operation2 = operation2;
            this.startX = startX;
            this.startY = startY;
        }

        @Override // com.google.android.material.shape.ShapePath.ShadowCompatOperation
        public void draw(Matrix transform, ShadowRenderer shadowRenderer, int shadowElevation, Canvas canvas) {
            double length2;
            float sweepAngle = getSweepAngle();
            if (sweepAngle <= 0.0f) {
                double length1 = Math.hypot(this.operation1.x - this.startX, this.operation1.y - this.startY);
                double length22 = Math.hypot(this.operation2.x - this.operation1.x, this.operation2.y - this.operation1.y);
                float arcRadius = (float) Math.min(shadowElevation, Math.min(length1, length22));
                double retractLength = arcRadius * Math.tan(Math.toRadians((-sweepAngle) / 2.0f));
                if (length1 <= retractLength) {
                    length2 = length22;
                } else {
                    length2 = length22;
                    RectF rect1 = new RectF(0.0f, 0.0f, (float) (length1 - retractLength), 0.0f);
                    this.renderMatrix.set(transform);
                    this.renderMatrix.preTranslate(this.startX, this.startY);
                    this.renderMatrix.preRotate(getStartAngle());
                    shadowRenderer.drawEdgeShadow(canvas, this.renderMatrix, rect1, shadowElevation);
                }
                RectF rect = new RectF(0.0f, 0.0f, arcRadius * 2.0f, arcRadius * 2.0f);
                this.renderMatrix.set(transform);
                this.renderMatrix.preTranslate(this.operation1.x, this.operation1.y);
                this.renderMatrix.preRotate(getStartAngle());
                this.renderMatrix.preTranslate((float) ((-retractLength) - arcRadius), (-2.0f) * arcRadius);
                double length23 = length2;
                shadowRenderer.drawInnerCornerShadow(canvas, this.renderMatrix, rect, (int) arcRadius, 450.0f, sweepAngle, new float[]{(float) (arcRadius + retractLength), 2.0f * arcRadius});
                if (length23 > retractLength) {
                    RectF rect2 = new RectF(0.0f, 0.0f, (float) (length23 - retractLength), 0.0f);
                    this.renderMatrix.set(transform);
                    this.renderMatrix.preTranslate(this.operation1.x, this.operation1.y);
                    this.renderMatrix.preRotate(getEndAngle());
                    this.renderMatrix.preTranslate((float) retractLength, 0.0f);
                    shadowRenderer.drawEdgeShadow(canvas, this.renderMatrix, rect2, shadowElevation);
                }
            }
        }

        float getStartAngle() {
            return (float) Math.toDegrees(Math.atan((this.operation1.y - this.startY) / (this.operation1.x - this.startX)));
        }

        float getEndAngle() {
            return (float) Math.toDegrees(Math.atan((this.operation2.y - this.operation1.y) / (this.operation2.x - this.operation1.x)));
        }

        float getSweepAngle() {
            float shadowAngle = ((getEndAngle() - getStartAngle()) + 360.0f) % 360.0f;
            if (shadowAngle <= ShapePath.ANGLE_LEFT) {
                return shadowAngle;
            }
            return shadowAngle - 360.0f;
        }
    }

    static class ArcShadowOperation extends ShadowCompatOperation {
        private final PathArcOperation operation;

        public ArcShadowOperation(PathArcOperation operation) {
            this.operation = operation;
        }

        @Override // com.google.android.material.shape.ShapePath.ShadowCompatOperation
        public void draw(Matrix transform, ShadowRenderer shadowRenderer, int shadowElevation, Canvas canvas) {
            float startAngle = this.operation.getStartAngle();
            float sweepAngle = this.operation.getSweepAngle();
            RectF rect = new RectF(this.operation.getLeft(), this.operation.getTop(), this.operation.getRight(), this.operation.getBottom());
            shadowRenderer.drawCornerShadow(canvas, transform, rect, shadowElevation, startAngle, sweepAngle);
        }
    }

    public static class PathLineOperation extends PathOperation {
        private float x;
        private float y;

        @Override // com.google.android.material.shape.ShapePath.PathOperation
        public void applyToPath(Matrix transform, Path path) {
            Matrix inverse = this.matrix;
            transform.invert(inverse);
            path.transform(inverse);
            path.lineTo(this.x, this.y);
            path.transform(transform);
        }
    }

    public static class PathQuadOperation extends PathOperation {

        @Deprecated
        public float controlX;

        @Deprecated
        public float controlY;

        @Deprecated
        public float endX;

        @Deprecated
        public float endY;

        @Override // com.google.android.material.shape.ShapePath.PathOperation
        public void applyToPath(Matrix transform, Path path) {
            Matrix inverse = this.matrix;
            transform.invert(inverse);
            path.transform(inverse);
            path.quadTo(getControlX(), getControlY(), getEndX(), getEndY());
            path.transform(transform);
        }

        private float getEndX() {
            return this.endX;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEndX(float endX) {
            this.endX = endX;
        }

        private float getControlY() {
            return this.controlY;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setControlY(float controlY) {
            this.controlY = controlY;
        }

        private float getEndY() {
            return this.endY;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEndY(float endY) {
            this.endY = endY;
        }

        private float getControlX() {
            return this.controlX;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setControlX(float controlX) {
            this.controlX = controlX;
        }
    }

    public static class PathArcOperation extends PathOperation {
        private static final RectF rectF = new RectF();

        @Deprecated
        public float bottom;

        @Deprecated
        public float left;

        @Deprecated
        public float right;

        @Deprecated
        public float startAngle;

        @Deprecated
        public float sweepAngle;

        @Deprecated
        public float top;

        public PathArcOperation(float left, float top, float right, float bottom) {
            setLeft(left);
            setTop(top);
            setRight(right);
            setBottom(bottom);
        }

        @Override // com.google.android.material.shape.ShapePath.PathOperation
        public void applyToPath(Matrix transform, Path path) {
            Matrix inverse = this.matrix;
            transform.invert(inverse);
            path.transform(inverse);
            rectF.set(getLeft(), getTop(), getRight(), getBottom());
            path.arcTo(rectF, getStartAngle(), getSweepAngle(), false);
            path.transform(transform);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getLeft() {
            return this.left;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getTop() {
            return this.top;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getRight() {
            return this.right;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getBottom() {
            return this.bottom;
        }

        private void setLeft(float left) {
            this.left = left;
        }

        private void setTop(float top) {
            this.top = top;
        }

        private void setRight(float right) {
            this.right = right;
        }

        private void setBottom(float bottom) {
            this.bottom = bottom;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getStartAngle() {
            return this.startAngle;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public float getSweepAngle() {
            return this.sweepAngle;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSweepAngle(float sweepAngle) {
            this.sweepAngle = sweepAngle;
        }
    }

    public static class PathCubicOperation extends PathOperation {
        private float controlX1;
        private float controlX2;
        private float controlY1;
        private float controlY2;
        private float endX;
        private float endY;

        public PathCubicOperation(float controlX1, float controlY1, float controlX2, float controlY2, float endX, float endY) {
            setControlX1(controlX1);
            setControlY1(controlY1);
            setControlX2(controlX2);
            setControlY2(controlY2);
            setEndX(endX);
            setEndY(endY);
        }

        @Override // com.google.android.material.shape.ShapePath.PathOperation
        public void applyToPath(Matrix transform, Path path) {
            Matrix inverse = this.matrix;
            transform.invert(inverse);
            path.transform(inverse);
            path.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX, this.endY);
            path.transform(transform);
        }

        private float getControlX1() {
            return this.controlX1;
        }

        private void setControlX1(float controlX1) {
            this.controlX1 = controlX1;
        }

        private float getControlY1() {
            return this.controlY1;
        }

        private void setControlY1(float controlY1) {
            this.controlY1 = controlY1;
        }

        private float getControlX2() {
            return this.controlX2;
        }

        private void setControlX2(float controlX2) {
            this.controlX2 = controlX2;
        }

        private float getControlY2() {
            return this.controlY1;
        }

        private void setControlY2(float controlY2) {
            this.controlY2 = controlY2;
        }

        private float getEndX() {
            return this.endX;
        }

        private void setEndX(float endX) {
            this.endX = endX;
        }

        private float getEndY() {
            return this.endY;
        }

        private void setEndY(float endY) {
            this.endY = endY;
        }
    }
}

package androidx.core.graphics;

import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;

/* loaded from: classes.dex */
public final class BitmapCompat {
    public static boolean hasMipMap(Bitmap bitmap) {
        return Api17Impl.hasMipMap(bitmap);
    }

    public static void setHasMipMap(Bitmap bitmap, boolean hasMipMap) {
        Api17Impl.setHasMipMap(bitmap, hasMipMap);
    }

    public static int getAllocationByteCount(Bitmap bitmap) {
        return Api19Impl.getAllocationByteCount(bitmap);
    }

    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v15, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r3v17 */
    public static Bitmap createScaledBitmap(Bitmap srcBm, int dstW, int dstH, Rect srcRect, boolean scaleInLinearSpace) {
        Paint paint;
        int stepsX;
        int srcH;
        int stepsY;
        int totalStepsY;
        int totalStepsY2;
        int stepsY2;
        ?? r3;
        int iSizeAtStep;
        int totalStepsY3;
        int i = dstW;
        int i2 = dstH;
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("dstW and dstH must be > 0!");
        }
        if (srcRect != null && (srcRect.isEmpty() || srcRect.left < 0 || srcRect.right > srcBm.getWidth() || srcRect.top < 0 || srcRect.bottom > srcBm.getHeight())) {
            throw new IllegalArgumentException("srcRect must be contained by srcBm!");
        }
        Bitmap src = srcBm;
        if (Build.VERSION.SDK_INT >= 27) {
            src = Api27Impl.copyBitmapIfHardware(srcBm);
        }
        int srcW = srcRect != null ? srcRect.width() : srcBm.getWidth();
        int srcH2 = srcRect != null ? srcRect.height() : srcBm.getHeight();
        float sx = i / srcW;
        float sy = i2 / srcH2;
        int srcX = srcRect != null ? srcRect.left : 0;
        int srcY = srcRect != null ? srcRect.top : 0;
        if (srcX == 0 && srcY == 0 && i == srcBm.getWidth() && i2 == srcBm.getHeight()) {
            if (srcBm.isMutable() && srcBm == src) {
                return srcBm.copy(srcBm.getConfig(), true);
            }
            return src;
        }
        Paint paint2 = new Paint(1);
        paint2.setFilterBitmap(true);
        if (Build.VERSION.SDK_INT >= 29) {
            Api29Impl.setPaintBlendMode(paint2);
        } else {
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        if (srcW == i && srcH2 == i2) {
            Bitmap out = Bitmap.createBitmap(i, i2, src.getConfig());
            Canvas canvasForCopy = new Canvas(out);
            canvasForCopy.drawBitmap(src, -srcX, -srcY, paint2);
            return out;
        }
        double log2 = Math.log(2.0d);
        if (sx > 1.0f) {
            paint = paint2;
            stepsX = (int) Math.ceil(Math.log(sx) / log2);
        } else {
            paint = paint2;
            stepsX = (int) Math.floor(Math.log(sx) / log2);
        }
        if (sy > 1.0f) {
            srcH = srcH2;
            stepsY = (int) Math.ceil(Math.log(sy) / log2);
        } else {
            srcH = srcH2;
            stepsY = (int) Math.floor(Math.log(sy) / log2);
        }
        int totalStepsX = stepsX;
        int totalStepsY4 = stepsY;
        Bitmap dst = null;
        int i3 = 0;
        if (!scaleInLinearSpace || Build.VERSION.SDK_INT < 27 || Api27Impl.isAlreadyF16AndLinear(srcBm)) {
            totalStepsY = totalStepsY4;
            totalStepsY2 = stepsY;
        } else {
            if (stepsX > 0) {
                r3 = 1;
                iSizeAtStep = sizeAtStep(srcW, i, 1, totalStepsX);
            } else {
                r3 = 1;
                iSizeAtStep = srcW;
            }
            int allocW = iSizeAtStep;
            if (stepsY > 0) {
                totalStepsY = totalStepsY4;
                totalStepsY3 = sizeAtStep(srcH, i2, r3, totalStepsY);
            } else {
                totalStepsY = totalStepsY4;
                totalStepsY3 = srcH;
            }
            int allocH = totalStepsY3;
            totalStepsY2 = stepsY;
            Bitmap dst2 = Api27Impl.createBitmapWithSourceColorspace(allocW, allocH, srcBm, r3);
            Canvas canvasForCopy2 = new Canvas(dst2);
            canvasForCopy2.drawBitmap(src, -srcX, -srcY, paint);
            srcX = 0;
            srcY = 0;
            dst = src;
            src = dst2;
            i3 = 1;
        }
        Rect currRect = new Rect(srcX, srcY, srcW, srcH);
        Rect nextRect = new Rect();
        Bitmap dst3 = dst;
        while (true) {
            if (stepsX == 0 && totalStepsY2 == 0) {
                break;
            }
            if (stepsX < 0) {
                stepsX++;
            } else if (stepsX > 0) {
                stepsX--;
            }
            if (totalStepsY2 < 0) {
                int stepsY3 = totalStepsY2 + 1;
                stepsY2 = stepsY3;
            } else if (totalStepsY2 <= 0) {
                stepsY2 = totalStepsY2;
            } else {
                int stepsY4 = totalStepsY2 - 1;
                stepsY2 = stepsY4;
            }
            int stepsY5 = srcX;
            int nextW = sizeAtStep(srcW, i, stepsX, totalStepsX);
            int srcY2 = srcY;
            int nextH = sizeAtStep(srcH, i2, stepsY2, totalStepsY);
            Rect currRect2 = currRect;
            nextRect.set(0, 0, nextW, nextH);
            boolean lastStep = stepsX == 0 && stepsY2 == 0;
            boolean dstSizeIsFinal = dst3 != null && dst3.getWidth() == i && dst3.getHeight() == i2;
            if (dst3 != null && dst3 != srcBm) {
                if (scaleInLinearSpace) {
                    int nextW2 = Build.VERSION.SDK_INT;
                    if (nextW2 < 27 || Api27Impl.isAlreadyF16AndLinear(dst3)) {
                    }
                    Canvas canvas = new Canvas(dst3);
                    canvas.drawBitmap(src, currRect2, nextRect, paint);
                    Bitmap swap = src;
                    src = dst3;
                    dst3 = swap;
                    currRect2.set(nextRect);
                    i = dstW;
                    currRect = currRect2;
                    srcX = stepsY5;
                    srcY = srcY2;
                    i2 = dstH;
                    totalStepsY2 = stepsY2;
                }
                if (!lastStep || (dstSizeIsFinal && i3 == 0)) {
                }
                Canvas canvas2 = new Canvas(dst3);
                canvas2.drawBitmap(src, currRect2, nextRect, paint);
                Bitmap swap2 = src;
                src = dst3;
                dst3 = swap2;
                currRect2.set(nextRect);
                i = dstW;
                currRect = currRect2;
                srcX = stepsY5;
                srcY = srcY2;
                i2 = dstH;
                totalStepsY2 = stepsY2;
            }
            if (dst3 != srcBm && dst3 != null) {
                dst3.recycle();
            }
            int lastScratchStep = i3;
            int allocW2 = sizeAtStep(srcW, i, stepsX > 0 ? lastScratchStep : stepsX, totalStepsX);
            int allocH2 = sizeAtStep(srcH, i2, stepsY2 > 0 ? lastScratchStep : stepsY2, totalStepsY);
            if (Build.VERSION.SDK_INT >= 27) {
                boolean linear = scaleInLinearSpace && !lastStep;
                dst3 = Api27Impl.createBitmapWithSourceColorspace(allocW2, allocH2, srcBm, linear);
            } else {
                dst3 = Bitmap.createBitmap(allocW2, allocH2, src.getConfig());
            }
            Canvas canvas22 = new Canvas(dst3);
            canvas22.drawBitmap(src, currRect2, nextRect, paint);
            Bitmap swap22 = src;
            src = dst3;
            dst3 = swap22;
            currRect2.set(nextRect);
            i = dstW;
            currRect = currRect2;
            srcX = stepsY5;
            srcY = srcY2;
            i2 = dstH;
            totalStepsY2 = stepsY2;
        }
        if (dst3 != srcBm && dst3 != null) {
            dst3.recycle();
        }
        return src;
    }

    public static int sizeAtStep(int srcSize, int dstSize, int step, int totalSteps) {
        if (step == 0) {
            return dstSize;
        }
        return step > 0 ? (1 << (totalSteps - step)) * srcSize : dstSize << ((-step) - 1);
    }

    private BitmapCompat() {
    }

    static class Api17Impl {
        private Api17Impl() {
        }

        static boolean hasMipMap(Bitmap bitmap) {
            return bitmap.hasMipMap();
        }

        static void setHasMipMap(Bitmap bitmap, boolean hasMipMap) {
            bitmap.setHasMipMap(hasMipMap);
        }
    }

    static class Api19Impl {
        private Api19Impl() {
        }

        static int getAllocationByteCount(Bitmap bitmap) {
            return bitmap.getAllocationByteCount();
        }
    }

    static class Api27Impl {
        private Api27Impl() {
        }

        static Bitmap createBitmapWithSourceColorspace(int w, int h, Bitmap src, boolean linear) {
            Bitmap.Config config = src.getConfig();
            ColorSpace colorSpace = src.getColorSpace();
            ColorSpace linearCs = ColorSpace.get(ColorSpace.Named.LINEAR_EXTENDED_SRGB);
            if (linear && !src.getColorSpace().equals(linearCs)) {
                config = Bitmap.Config.RGBA_F16;
                colorSpace = linearCs;
            } else if (src.getConfig() == Bitmap.Config.HARDWARE) {
                config = Bitmap.Config.ARGB_8888;
                if (Build.VERSION.SDK_INT >= 31) {
                    config = Api31Impl.getHardwareBitmapConfig(src);
                }
            }
            return Bitmap.createBitmap(w, h, config, src.hasAlpha(), colorSpace);
        }

        static boolean isAlreadyF16AndLinear(Bitmap b) {
            ColorSpace linearCs = ColorSpace.get(ColorSpace.Named.LINEAR_EXTENDED_SRGB);
            return b.getConfig() == Bitmap.Config.RGBA_F16 && b.getColorSpace().equals(linearCs);
        }

        static Bitmap copyBitmapIfHardware(Bitmap bm) {
            if (bm.getConfig() == Bitmap.Config.HARDWARE) {
                Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
                if (Build.VERSION.SDK_INT >= 31) {
                    newConfig = Api31Impl.getHardwareBitmapConfig(bm);
                }
                return bm.copy(newConfig, true);
            }
            return bm;
        }
    }

    static class Api29Impl {
        private Api29Impl() {
        }

        static void setPaintBlendMode(Paint paint) {
            paint.setBlendMode(BlendMode.SRC);
        }
    }

    static class Api31Impl {
        private Api31Impl() {
        }

        static Bitmap.Config getHardwareBitmapConfig(Bitmap bm) {
            if (bm.getHardwareBuffer().getFormat() == 22) {
                return Bitmap.Config.RGBA_F16;
            }
            return Bitmap.Config.ARGB_8888;
        }
    }
}

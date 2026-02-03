package com.google.android.material.internal;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class WindowUtils {
    private static final String TAG = WindowUtils.class.getSimpleName();

    private WindowUtils() {
    }

    public static Rect getCurrentWindowBounds(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (Build.VERSION.SDK_INT >= 30) {
            return Api30Impl.getCurrentWindowBounds(windowManager);
        }
        return Api17Impl.getCurrentWindowBounds(windowManager);
    }

    private static class Api30Impl {
        private Api30Impl() {
        }

        static Rect getCurrentWindowBounds(WindowManager windowManager) {
            return windowManager.getCurrentWindowMetrics().getBounds();
        }
    }

    private static class Api17Impl {
        private Api17Impl() {
        }

        static Rect getCurrentWindowBounds(WindowManager windowManager) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point defaultDisplaySize = new Point();
            defaultDisplay.getRealSize(defaultDisplaySize);
            Rect bounds = new Rect();
            bounds.right = defaultDisplaySize.x;
            bounds.bottom = defaultDisplaySize.y;
            return bounds;
        }
    }

    private static class Api14Impl {
        private Api14Impl() {
        }

        static Rect getCurrentWindowBounds(WindowManager windowManager) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point defaultDisplaySize = getRealSizeForDisplay(defaultDisplay);
            Rect bounds = new Rect();
            if (defaultDisplaySize.x == 0 || defaultDisplaySize.y == 0) {
                defaultDisplay.getRectSize(bounds);
            } else {
                bounds.right = defaultDisplaySize.x;
                bounds.bottom = defaultDisplaySize.y;
            }
            return bounds;
        }

        private static Point getRealSizeForDisplay(Display display) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
            Point size = new Point();
            try {
                Method getRealSizeMethod = Display.class.getDeclaredMethod("getRealSize", Point.class);
                getRealSizeMethod.setAccessible(true);
                getRealSizeMethod.invoke(display, size);
            } catch (IllegalAccessException e) {
                Log.w(WindowUtils.TAG, e);
            } catch (NoSuchMethodException e2) {
                Log.w(WindowUtils.TAG, e2);
            } catch (InvocationTargetException e3) {
                Log.w(WindowUtils.TAG, e3);
            }
            return size;
        }
    }
}

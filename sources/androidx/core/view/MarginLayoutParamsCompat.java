package androidx.core.view;

import android.view.ViewGroup;

/* loaded from: classes.dex */
public final class MarginLayoutParamsCompat {
    public static int getMarginStart(ViewGroup.MarginLayoutParams lp) {
        return Api17Impl.getMarginStart(lp);
    }

    public static int getMarginEnd(ViewGroup.MarginLayoutParams lp) {
        return Api17Impl.getMarginEnd(lp);
    }

    public static void setMarginStart(ViewGroup.MarginLayoutParams lp, int marginStart) {
        Api17Impl.setMarginStart(lp, marginStart);
    }

    public static void setMarginEnd(ViewGroup.MarginLayoutParams lp, int marginEnd) {
        Api17Impl.setMarginEnd(lp, marginEnd);
    }

    public static boolean isMarginRelative(ViewGroup.MarginLayoutParams lp) {
        return Api17Impl.isMarginRelative(lp);
    }

    public static int getLayoutDirection(ViewGroup.MarginLayoutParams lp) {
        int result = Api17Impl.getLayoutDirection(lp);
        if (result != 0 && result != 1) {
            return 0;
        }
        return result;
    }

    public static void setLayoutDirection(ViewGroup.MarginLayoutParams lp, int layoutDirection) {
        Api17Impl.setLayoutDirection(lp, layoutDirection);
    }

    public static void resolveLayoutDirection(ViewGroup.MarginLayoutParams lp, int layoutDirection) {
        Api17Impl.resolveLayoutDirection(lp, layoutDirection);
    }

    private MarginLayoutParamsCompat() {
    }

    static class Api17Impl {
        private Api17Impl() {
        }

        static int getMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getMarginStart();
        }

        static int getMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getMarginEnd();
        }

        static void setMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams, int start) {
            marginLayoutParams.setMarginStart(start);
        }

        static void setMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams, int end) {
            marginLayoutParams.setMarginEnd(end);
        }

        static boolean isMarginRelative(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.isMarginRelative();
        }

        static int getLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getLayoutDirection();
        }

        static void setLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int layoutDirection) {
            marginLayoutParams.setLayoutDirection(layoutDirection);
        }

        static void resolveLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int layoutDirection) {
            marginLayoutParams.resolveLayoutDirection(layoutDirection);
        }
    }
}

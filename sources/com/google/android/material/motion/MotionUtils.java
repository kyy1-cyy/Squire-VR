package com.google.android.material.motion;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.TypedValue;
import android.view.animation.AnimationUtils;
import androidx.core.graphics.PathParser;
import androidx.core.view.animation.PathInterpolatorCompat;
import com.google.android.material.resources.MaterialAttributes;

/* loaded from: classes.dex */
public class MotionUtils {
    private static final String EASING_TYPE_CUBIC_BEZIER = "cubic-bezier";
    private static final String EASING_TYPE_FORMAT_END = ")";
    private static final String EASING_TYPE_FORMAT_START = "(";
    private static final String EASING_TYPE_PATH = "path";

    private MotionUtils() {
    }

    public static int resolveThemeDuration(Context context, int attrResId, int defaultDuration) {
        return MaterialAttributes.resolveInteger(context, attrResId, defaultDuration);
    }

    public static TimeInterpolator resolveThemeInterpolator(Context context, int attrResId, TimeInterpolator defaultInterpolator) {
        TypedValue easingValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(attrResId, easingValue, true)) {
            return defaultInterpolator;
        }
        if (easingValue.type != 3) {
            throw new IllegalArgumentException("Motion easing theme attribute must be an @interpolator resource for ?attr/motionEasing*Interpolator attributes or a string for ?attr/motionEasing* attributes.");
        }
        String easingString = String.valueOf(easingValue.string);
        if (isLegacyEasingAttribute(easingString)) {
            return getLegacyThemeInterpolator(easingString);
        }
        return AnimationUtils.loadInterpolator(context, easingValue.resourceId);
    }

    private static TimeInterpolator getLegacyThemeInterpolator(String easingString) throws NumberFormatException {
        if (isLegacyEasingType(easingString, EASING_TYPE_CUBIC_BEZIER)) {
            String controlPointsString = getLegacyEasingContent(easingString, EASING_TYPE_CUBIC_BEZIER);
            String[] controlPoints = controlPointsString.split(",");
            if (controlPoints.length != 4) {
                throw new IllegalArgumentException("Motion easing theme attribute must have 4 control points if using bezier curve format; instead got: " + controlPoints.length);
            }
            float controlX1 = getLegacyControlPoint(controlPoints, 0);
            float controlY1 = getLegacyControlPoint(controlPoints, 1);
            float controlX2 = getLegacyControlPoint(controlPoints, 2);
            float controlY2 = getLegacyControlPoint(controlPoints, 3);
            return PathInterpolatorCompat.create(controlX1, controlY1, controlX2, controlY2);
        }
        if (isLegacyEasingType(easingString, EASING_TYPE_PATH)) {
            String path = getLegacyEasingContent(easingString, EASING_TYPE_PATH);
            return PathInterpolatorCompat.create(PathParser.createPathFromPathData(path));
        }
        throw new IllegalArgumentException("Invalid motion easing type: " + easingString);
    }

    private static boolean isLegacyEasingAttribute(String easingString) {
        return isLegacyEasingType(easingString, EASING_TYPE_CUBIC_BEZIER) || isLegacyEasingType(easingString, EASING_TYPE_PATH);
    }

    private static boolean isLegacyEasingType(String easingString, String easingType) {
        return easingString.startsWith(new StringBuilder().append(easingType).append(EASING_TYPE_FORMAT_START).toString()) && easingString.endsWith(EASING_TYPE_FORMAT_END);
    }

    private static String getLegacyEasingContent(String easingString, String easingType) {
        return easingString.substring(easingType.length() + EASING_TYPE_FORMAT_START.length(), easingString.length() - EASING_TYPE_FORMAT_END.length());
    }

    private static float getLegacyControlPoint(String[] controlPoints, int index) throws NumberFormatException {
        float controlPoint = Float.parseFloat(controlPoints[index]);
        if (controlPoint < 0.0f || controlPoint > 1.0f) {
            throw new IllegalArgumentException("Motion easing control point value must be between 0 and 1; instead got: " + controlPoint);
        }
        return controlPoint;
    }
}

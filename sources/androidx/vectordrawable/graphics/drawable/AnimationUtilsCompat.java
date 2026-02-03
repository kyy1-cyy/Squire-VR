package androidx.vectordrawable.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class AnimationUtilsCompat {
    public static Interpolator loadInterpolator(Context context, int id) throws Resources.NotFoundException {
        return AnimationUtils.loadInterpolator(context, id);
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x00d9, code lost:
    
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static Interpolator createInterpolatorFromXml(Context context, Resources res, Resources.Theme theme, XmlPullParser parser) throws XmlPullParserException, IOException {
        Interpolator interpolator = null;
        int depth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if ((type == 3 && parser.getDepth() <= depth) || type == 1) {
                break;
            }
            if (type == 2) {
                AttributeSet attrs = Xml.asAttributeSet(parser);
                String name = parser.getName();
                if (name.equals("linearInterpolator")) {
                    interpolator = new LinearInterpolator();
                } else if (name.equals("accelerateInterpolator")) {
                    interpolator = new AccelerateInterpolator(context, attrs);
                } else if (name.equals("decelerateInterpolator")) {
                    interpolator = new DecelerateInterpolator(context, attrs);
                } else if (name.equals("accelerateDecelerateInterpolator")) {
                    interpolator = new AccelerateDecelerateInterpolator();
                } else if (name.equals("cycleInterpolator")) {
                    interpolator = new CycleInterpolator(context, attrs);
                } else if (name.equals("anticipateInterpolator")) {
                    interpolator = new AnticipateInterpolator(context, attrs);
                } else if (name.equals("overshootInterpolator")) {
                    interpolator = new OvershootInterpolator(context, attrs);
                } else if (name.equals("anticipateOvershootInterpolator")) {
                    interpolator = new AnticipateOvershootInterpolator(context, attrs);
                } else if (name.equals("bounceInterpolator")) {
                    interpolator = new BounceInterpolator();
                } else if (name.equals("pathInterpolator")) {
                    interpolator = new PathInterpolatorCompat(context, attrs, parser);
                } else {
                    throw new RuntimeException("Unknown interpolator name: " + parser.getName());
                }
            }
        }
    }

    private AnimationUtilsCompat() {
    }
}

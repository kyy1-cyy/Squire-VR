package androidx.constraintlayout.core.motion.utils;

/* loaded from: classes.dex */
public interface TypedValues {
    public static final int BOOLEAN_MASK = 1;
    public static final int FLOAT_MASK = 4;
    public static final int INT_MASK = 2;
    public static final int STRING_MASK = 8;
    public static final String S_CUSTOM = "CUSTOM";
    public static final int TYPE_FRAME_POSITION = 100;
    public static final int TYPE_TARGET = 101;

    public interface OnSwipe {
        public static final String AUTOCOMPLETE_MODE = "autocompletemode";
        public static final String DRAG_DIRECTION = "dragdirection";
        public static final String DRAG_SCALE = "dragscale";
        public static final String DRAG_THRESHOLD = "dragthreshold";
        public static final String LIMIT_BOUNDS_TO = "limitboundsto";
        public static final String MAX_ACCELERATION = "maxacceleration";
        public static final String MAX_VELOCITY = "maxvelocity";
        public static final String MOVE_WHEN_SCROLLAT_TOP = "movewhenscrollattop";
        public static final String NESTED_SCROLL_FLAGS = "nestedscrollflags";
        public static final String ON_TOUCH_UP = "ontouchup";
        public static final String ROTATION_CENTER_ID = "rotationcenterid";
        public static final String SPRINGS_TOP_THRESHOLD = "springstopthreshold";
        public static final String SPRING_BOUNDARY = "springboundary";
        public static final String SPRING_DAMPING = "springdamping";
        public static final String SPRING_MASS = "springmass";
        public static final String SPRING_STIFFNESS = "springstiffness";
        public static final String TOUCH_ANCHOR_ID = "touchanchorid";
        public static final String TOUCH_ANCHOR_SIDE = "touchanchorside";
        public static final String TOUCH_REGION_ID = "touchregionid";
        public static final String[] ON_TOUCH_UP_ENUM = {"autoComplete", "autoCompleteToStart", "autoCompleteToEnd", "stop", "decelerate", "decelerateAndComplete", "neverCompleteToStart", "neverCompleteToEnd"};
        public static final String[] SPRING_BOUNDARY_ENUM = {"overshoot", "bounceStart", "bounceEnd", "bounceBoth"};
        public static final String[] AUTOCOMPLETE_MODE_ENUM = {"continuousVelocity", "spring"};
        public static final String[] NESTED_SCROLL_FLAGS_ENUM = {"none", "disablePostScroll", "disableScroll", "supportScrollUp"};
    }

    int getId(String str);

    boolean setValue(int i, float f);

    boolean setValue(int i, int i2);

    boolean setValue(int i, String str);

    boolean setValue(int i, boolean z);

    public interface AttributesType {
        public static final String NAME = "KeyAttributes";
        public static final String S_ALPHA = "alpha";
        public static final String S_CURVE_FIT = "curveFit";
        public static final String S_CUSTOM = "CUSTOM";
        public static final String S_EASING = "easing";
        public static final String S_ELEVATION = "elevation";
        public static final String S_PATH_ROTATE = "pathRotate";
        public static final String S_PIVOT_X = "pivotX";
        public static final String S_PIVOT_Y = "pivotY";
        public static final String S_PROGRESS = "progress";
        public static final String S_ROTATION_X = "rotationX";
        public static final String S_ROTATION_Y = "rotationY";
        public static final String S_ROTATION_Z = "rotationZ";
        public static final String S_SCALE_X = "scaleX";
        public static final String S_SCALE_Y = "scaleY";
        public static final String S_TRANSLATION_X = "translationX";
        public static final String S_TRANSLATION_Y = "translationY";
        public static final String S_TRANSLATION_Z = "translationZ";
        public static final String S_VISIBILITY = "visibility";
        public static final int TYPE_ALPHA = 303;
        public static final int TYPE_CURVE_FIT = 301;
        public static final int TYPE_EASING = 317;
        public static final int TYPE_ELEVATION = 307;
        public static final int TYPE_PATH_ROTATE = 316;
        public static final int TYPE_PIVOT_TARGET = 318;
        public static final int TYPE_PIVOT_X = 313;
        public static final int TYPE_PIVOT_Y = 314;
        public static final int TYPE_PROGRESS = 315;
        public static final int TYPE_ROTATION_X = 308;
        public static final int TYPE_ROTATION_Y = 309;
        public static final int TYPE_ROTATION_Z = 310;
        public static final int TYPE_SCALE_X = 311;
        public static final int TYPE_SCALE_Y = 312;
        public static final int TYPE_TRANSLATION_X = 304;
        public static final int TYPE_TRANSLATION_Y = 305;
        public static final int TYPE_TRANSLATION_Z = 306;
        public static final int TYPE_VISIBILITY = 302;
        public static final String S_FRAME = "frame";
        public static final String S_TARGET = "target";
        public static final String S_PIVOT_TARGET = "pivotTarget";
        public static final String[] KEY_WORDS = {"curveFit", "visibility", "alpha", "translationX", "translationY", "translationZ", "elevation", "rotationX", "rotationY", "rotationZ", "scaleX", "scaleY", "pivotX", "pivotY", "progress", "pathRotate", "easing", "CUSTOM", S_FRAME, S_TARGET, S_PIVOT_TARGET};

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:65:0x00f7  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "curveFit":
                    return 301;
                case "visibility":
                    return 302;
                case "alpha":
                    return 303;
                case "translationX":
                    return 304;
                case "translationY":
                    return 305;
                case "translationZ":
                    return 306;
                case "elevation":
                    return 307;
                case "rotationX":
                    return 308;
                case "rotationY":
                    return 309;
                case "rotationZ":
                    return 310;
                case "scaleX":
                    return 311;
                case "scaleY":
                    return 312;
                case "pivotX":
                    return 313;
                case "pivotY":
                    return 314;
                case "progress":
                    return 315;
                case "pathRotate":
                    return TYPE_PATH_ROTATE;
                case "easing":
                    return TYPE_EASING;
                case "frame":
                    return 100;
                case "target":
                    return TypedValues.TYPE_TARGET;
                case "pivotTarget":
                    return TYPE_PIVOT_TARGET;
                default:
                    return -1;
            }
        }

        static int getType(int name) {
            switch (name) {
                case 100:
                case 301:
                case 302:
                    return 2;
                case TypedValues.TYPE_TARGET /* 101 */:
                case TYPE_EASING /* 317 */:
                case TYPE_PIVOT_TARGET /* 318 */:
                    return 8;
                case 303:
                case 304:
                case 305:
                case 306:
                case 307:
                case 308:
                case 309:
                case 310:
                case 311:
                case 312:
                case 313:
                case 314:
                case 315:
                case TYPE_PATH_ROTATE /* 316 */:
                    return 4;
                default:
                    return -1;
            }
        }
    }

    public interface CycleType {
        public static final String NAME = "KeyCycle";
        public static final String S_ALPHA = "alpha";
        public static final String S_CURVE_FIT = "curveFit";
        public static final String S_EASING = "easing";
        public static final String S_ELEVATION = "elevation";
        public static final String S_PATH_ROTATE = "pathRotate";
        public static final String S_PIVOT_X = "pivotX";
        public static final String S_PIVOT_Y = "pivotY";
        public static final String S_PROGRESS = "progress";
        public static final String S_ROTATION_X = "rotationX";
        public static final String S_ROTATION_Y = "rotationY";
        public static final String S_ROTATION_Z = "rotationZ";
        public static final String S_SCALE_X = "scaleX";
        public static final String S_SCALE_Y = "scaleY";
        public static final String S_TRANSLATION_X = "translationX";
        public static final String S_TRANSLATION_Y = "translationY";
        public static final String S_TRANSLATION_Z = "translationZ";
        public static final String S_VISIBILITY = "visibility";
        public static final String S_WAVE_SHAPE = "waveShape";
        public static final int TYPE_ALPHA = 403;
        public static final int TYPE_CURVE_FIT = 401;
        public static final int TYPE_CUSTOM_WAVE_SHAPE = 422;
        public static final int TYPE_EASING = 420;
        public static final int TYPE_ELEVATION = 307;
        public static final int TYPE_PATH_ROTATE = 416;
        public static final int TYPE_PIVOT_X = 313;
        public static final int TYPE_PIVOT_Y = 314;
        public static final int TYPE_PROGRESS = 315;
        public static final int TYPE_ROTATION_X = 308;
        public static final int TYPE_ROTATION_Y = 309;
        public static final int TYPE_ROTATION_Z = 310;
        public static final int TYPE_SCALE_X = 311;
        public static final int TYPE_SCALE_Y = 312;
        public static final int TYPE_TRANSLATION_X = 304;
        public static final int TYPE_TRANSLATION_Y = 305;
        public static final int TYPE_TRANSLATION_Z = 306;
        public static final int TYPE_VISIBILITY = 402;
        public static final int TYPE_WAVE_OFFSET = 424;
        public static final int TYPE_WAVE_PERIOD = 423;
        public static final int TYPE_WAVE_PHASE = 425;
        public static final int TYPE_WAVE_SHAPE = 421;
        public static final String S_CUSTOM_WAVE_SHAPE = "customWave";
        public static final String S_WAVE_PERIOD = "period";
        public static final String S_WAVE_OFFSET = "offset";
        public static final String S_WAVE_PHASE = "phase";
        public static final String[] KEY_WORDS = {"curveFit", "visibility", "alpha", "translationX", "translationY", "translationZ", "elevation", "rotationX", "rotationY", "rotationZ", "scaleX", "scaleY", "pivotX", "pivotY", "progress", "pathRotate", "easing", "waveShape", S_CUSTOM_WAVE_SHAPE, S_WAVE_PERIOD, S_WAVE_OFFSET, S_WAVE_PHASE};

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:53:0x00c4  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "curveFit":
                    return TYPE_CURVE_FIT;
                case "visibility":
                    return TYPE_VISIBILITY;
                case "alpha":
                    return TYPE_ALPHA;
                case "translationX":
                    return 304;
                case "translationY":
                    return 305;
                case "translationZ":
                    return 306;
                case "rotationX":
                    return 308;
                case "rotationY":
                    return 309;
                case "rotationZ":
                    return 310;
                case "scaleX":
                    return 311;
                case "scaleY":
                    return 312;
                case "pivotX":
                    return 313;
                case "pivotY":
                    return 314;
                case "progress":
                    return 315;
                case "pathRotate":
                    return TYPE_PATH_ROTATE;
                case "easing":
                    return TYPE_EASING;
                default:
                    return -1;
            }
        }

        static int getType(int name) {
            switch (name) {
                case 100:
                case TYPE_CURVE_FIT /* 401 */:
                case TYPE_VISIBILITY /* 402 */:
                    return 2;
                case TypedValues.TYPE_TARGET /* 101 */:
                case TYPE_EASING /* 420 */:
                case 421:
                    return 8;
                case 304:
                case 305:
                case 306:
                case 307:
                case 308:
                case 309:
                case 310:
                case 311:
                case 312:
                case 313:
                case 314:
                case 315:
                case TYPE_ALPHA /* 403 */:
                case TYPE_PATH_ROTATE /* 416 */:
                case TYPE_WAVE_PERIOD /* 423 */:
                case TYPE_WAVE_OFFSET /* 424 */:
                case TYPE_WAVE_PHASE /* 425 */:
                    return 4;
                default:
                    return -1;
            }
        }
    }

    public interface TriggerType {
        public static final String CROSS = "CROSS";
        public static final String[] KEY_WORDS = {"viewTransitionOnCross", "viewTransitionOnPositiveCross", "viewTransitionOnNegativeCross", "postLayout", "triggerSlack", "triggerCollisionView", "triggerCollisionId", "triggerID", "positiveCross", "negativeCross", "triggerReceiver", "CROSS"};
        public static final String NAME = "KeyTrigger";
        public static final String NEGATIVE_CROSS = "negativeCross";
        public static final String POSITIVE_CROSS = "positiveCross";
        public static final String POST_LAYOUT = "postLayout";
        public static final String TRIGGER_COLLISION_ID = "triggerCollisionId";
        public static final String TRIGGER_COLLISION_VIEW = "triggerCollisionView";
        public static final String TRIGGER_ID = "triggerID";
        public static final String TRIGGER_RECEIVER = "triggerReceiver";
        public static final String TRIGGER_SLACK = "triggerSlack";
        public static final int TYPE_CROSS = 312;
        public static final int TYPE_NEGATIVE_CROSS = 310;
        public static final int TYPE_POSITIVE_CROSS = 309;
        public static final int TYPE_POST_LAYOUT = 304;
        public static final int TYPE_TRIGGER_COLLISION_ID = 307;
        public static final int TYPE_TRIGGER_COLLISION_VIEW = 306;
        public static final int TYPE_TRIGGER_ID = 308;
        public static final int TYPE_TRIGGER_RECEIVER = 311;
        public static final int TYPE_TRIGGER_SLACK = 305;
        public static final int TYPE_VIEW_TRANSITION_ON_CROSS = 301;
        public static final int TYPE_VIEW_TRANSITION_ON_NEGATIVE_CROSS = 303;
        public static final int TYPE_VIEW_TRANSITION_ON_POSITIVE_CROSS = 302;
        public static final String VIEW_TRANSITION_ON_CROSS = "viewTransitionOnCross";
        public static final String VIEW_TRANSITION_ON_NEGATIVE_CROSS = "viewTransitionOnNegativeCross";
        public static final String VIEW_TRANSITION_ON_POSITIVE_CROSS = "viewTransitionOnPositiveCross";

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0092  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "viewTransitionOnCross":
                    return 301;
                case "viewTransitionOnPositiveCross":
                    return 302;
                case "viewTransitionOnNegativeCross":
                    return 303;
                case "postLayout":
                    return 304;
                case "triggerSlack":
                    return 305;
                case "triggerCollisionView":
                    return 306;
                case "triggerCollisionId":
                    return 307;
                case "triggerID":
                    return 308;
                case "positiveCross":
                    return 309;
                case "negativeCross":
                    return 310;
                case "triggerReceiver":
                    return 311;
                case "CROSS":
                    return 312;
                default:
                    return -1;
            }
        }
    }

    public interface PositionType {
        public static final String[] KEY_WORDS = {"transitionEasing", "drawPath", "percentWidth", "percentHeight", "sizePercent", "percentX", "percentY"};
        public static final String NAME = "KeyPosition";
        public static final String S_DRAWPATH = "drawPath";
        public static final String S_PERCENT_HEIGHT = "percentHeight";
        public static final String S_PERCENT_WIDTH = "percentWidth";
        public static final String S_PERCENT_X = "percentX";
        public static final String S_PERCENT_Y = "percentY";
        public static final String S_SIZE_PERCENT = "sizePercent";
        public static final String S_TRANSITION_EASING = "transitionEasing";
        public static final int TYPE_CURVE_FIT = 508;
        public static final int TYPE_DRAWPATH = 502;
        public static final int TYPE_PATH_MOTION_ARC = 509;
        public static final int TYPE_PERCENT_HEIGHT = 504;
        public static final int TYPE_PERCENT_WIDTH = 503;
        public static final int TYPE_PERCENT_X = 506;
        public static final int TYPE_PERCENT_Y = 507;
        public static final int TYPE_POSITION_TYPE = 510;
        public static final int TYPE_SIZE_PERCENT = 505;
        public static final int TYPE_TRANSITION_EASING = 501;

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:26:0x0055  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "transitionEasing":
                    return TYPE_TRANSITION_EASING;
                case "drawPath":
                    return TYPE_DRAWPATH;
                case "percentWidth":
                    return TYPE_PERCENT_WIDTH;
                case "percentHeight":
                    return TYPE_PERCENT_HEIGHT;
                case "sizePercent":
                    return TYPE_SIZE_PERCENT;
                case "percentX":
                    return TYPE_PERCENT_X;
                case "percentY":
                    return TYPE_PERCENT_Y;
                default:
                    return -1;
            }
        }

        static int getType(int name) {
            switch (name) {
                case 100:
                case TYPE_CURVE_FIT /* 508 */:
                    return 2;
                case TypedValues.TYPE_TARGET /* 101 */:
                case TYPE_TRANSITION_EASING /* 501 */:
                case TYPE_DRAWPATH /* 502 */:
                    return 8;
                case TYPE_PERCENT_WIDTH /* 503 */:
                case TYPE_PERCENT_HEIGHT /* 504 */:
                case TYPE_SIZE_PERCENT /* 505 */:
                case TYPE_PERCENT_X /* 506 */:
                case TYPE_PERCENT_Y /* 507 */:
                    return 4;
                default:
                    return -1;
            }
        }
    }

    public interface MotionType {
        public static final String NAME = "Motion";
        public static final int TYPE_ANIMATE_CIRCLEANGLE_TO = 606;
        public static final int TYPE_ANIMATE_RELATIVE_TO = 605;
        public static final int TYPE_DRAW_PATH = 608;
        public static final int TYPE_EASING = 603;
        public static final int TYPE_PATHMOTION_ARC = 607;
        public static final int TYPE_PATH_ROTATE = 601;
        public static final int TYPE_POLAR_RELATIVETO = 609;
        public static final int TYPE_QUANTIZE_INTERPOLATOR = 604;
        public static final int TYPE_QUANTIZE_INTERPOLATOR_ID = 612;
        public static final int TYPE_QUANTIZE_INTERPOLATOR_TYPE = 611;
        public static final int TYPE_QUANTIZE_MOTIONSTEPS = 610;
        public static final int TYPE_QUANTIZE_MOTION_PHASE = 602;
        public static final int TYPE_STAGGER = 600;
        public static final String S_STAGGER = "Stagger";
        public static final String S_PATH_ROTATE = "PathRotate";
        public static final String S_QUANTIZE_MOTION_PHASE = "QuantizeMotionPhase";
        public static final String S_EASING = "TransitionEasing";
        public static final String S_QUANTIZE_INTERPOLATOR = "QuantizeInterpolator";
        public static final String S_ANIMATE_RELATIVE_TO = "AnimateRelativeTo";
        public static final String S_ANIMATE_CIRCLEANGLE_TO = "AnimateCircleAngleTo";
        public static final String S_PATHMOTION_ARC = "PathMotionArc";
        public static final String S_DRAW_PATH = "DrawPath";
        public static final String S_POLAR_RELATIVETO = "PolarRelativeTo";
        public static final String S_QUANTIZE_MOTIONSTEPS = "QuantizeMotionSteps";
        public static final String S_QUANTIZE_INTERPOLATOR_TYPE = "QuantizeInterpolatorType";
        public static final String S_QUANTIZE_INTERPOLATOR_ID = "QuantizeInterpolatorID";
        public static final String[] KEY_WORDS = {S_STAGGER, S_PATH_ROTATE, S_QUANTIZE_MOTION_PHASE, S_EASING, S_QUANTIZE_INTERPOLATOR, S_ANIMATE_RELATIVE_TO, S_ANIMATE_CIRCLEANGLE_TO, S_PATHMOTION_ARC, S_DRAW_PATH, S_POLAR_RELATIVETO, S_QUANTIZE_MOTIONSTEPS, S_QUANTIZE_INTERPOLATOR_TYPE, S_QUANTIZE_INTERPOLATOR_ID};

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:44:0x0094  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "Stagger":
                    return 600;
                case "PathRotate":
                    return 601;
                case "QuantizeMotionPhase":
                    return TYPE_QUANTIZE_MOTION_PHASE;
                case "TransitionEasing":
                    return TYPE_EASING;
                case "QuantizeInterpolator":
                    return TYPE_QUANTIZE_INTERPOLATOR;
                case "AnimateRelativeTo":
                    return TYPE_ANIMATE_RELATIVE_TO;
                case "AnimateCircleAngleTo":
                    return TYPE_ANIMATE_CIRCLEANGLE_TO;
                case "PathMotionArc":
                    return TYPE_PATHMOTION_ARC;
                case "DrawPath":
                    return TYPE_DRAW_PATH;
                case "PolarRelativeTo":
                    return TYPE_POLAR_RELATIVETO;
                case "QuantizeMotionSteps":
                    return TYPE_QUANTIZE_MOTIONSTEPS;
                case "QuantizeInterpolatorType":
                    return TYPE_QUANTIZE_INTERPOLATOR_TYPE;
                case "QuantizeInterpolatorID":
                    return TYPE_QUANTIZE_INTERPOLATOR_ID;
                default:
                    return -1;
            }
        }
    }

    public interface Custom {
        public static final String NAME = "Custom";
        public static final String S_INT = "integer";
        public static final int TYPE_BOOLEAN = 904;
        public static final int TYPE_COLOR = 902;
        public static final int TYPE_DIMENSION = 905;
        public static final int TYPE_FLOAT = 901;
        public static final int TYPE_INT = 900;
        public static final int TYPE_REFERENCE = 906;
        public static final int TYPE_STRING = 903;
        public static final String S_FLOAT = "float";
        public static final String S_COLOR = "color";
        public static final String S_STRING = "string";
        public static final String S_BOOLEAN = "boolean";
        public static final String S_DIMENSION = "dimension";
        public static final String S_REFERENCE = "refrence";
        public static final String[] KEY_WORDS = {S_FLOAT, S_COLOR, S_STRING, S_BOOLEAN, S_DIMENSION, S_REFERENCE};

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:26:0x0051  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "integer":
                    return TYPE_INT;
                case "float":
                    return TYPE_FLOAT;
                case "color":
                    return TYPE_COLOR;
                case "string":
                    return TYPE_STRING;
                case "boolean":
                    return TYPE_BOOLEAN;
                case "dimension":
                    return TYPE_DIMENSION;
                case "refrence":
                    return TYPE_REFERENCE;
                default:
                    return -1;
            }
        }
    }

    public interface MotionScene {
        public static final String NAME = "MotionScene";
        public static final int TYPE_DEFAULT_DURATION = 600;
        public static final int TYPE_LAYOUT_DURING_TRANSITION = 601;
        public static final String S_DEFAULT_DURATION = "defaultDuration";
        public static final String S_LAYOUT_DURING_TRANSITION = "layoutDuringTransition";
        public static final String[] KEY_WORDS = {S_DEFAULT_DURATION, S_LAYOUT_DURING_TRANSITION};

        static int getType(int name) {
            switch (name) {
                case 600:
                    return 2;
                case 601:
                    return 1;
                default:
                    return -1;
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:11:0x001d  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "defaultDuration":
                    return 600;
                case "layoutDuringTransition":
                    return 601;
                default:
                    return -1;
            }
        }
    }

    public interface TransitionType {
        public static final String NAME = "Transitions";
        public static final int TYPE_AUTO_TRANSITION = 704;
        public static final int TYPE_DURATION = 700;
        public static final int TYPE_FROM = 701;
        public static final int TYPE_INTERPOLATOR = 705;
        public static final int TYPE_PATH_MOTION_ARC = 509;
        public static final int TYPE_STAGGERED = 706;
        public static final int TYPE_TO = 702;
        public static final int TYPE_TRANSITION_FLAGS = 707;
        public static final String S_DURATION = "duration";
        public static final String S_FROM = "from";
        public static final String S_TO = "to";
        public static final String S_PATH_MOTION_ARC = "pathMotionArc";
        public static final String S_AUTO_TRANSITION = "autoTransition";
        public static final String S_INTERPOLATOR = "motionInterpolator";
        public static final String S_STAGGERED = "staggered";
        public static final String S_TRANSITION_FLAGS = "transitionFlags";
        public static final String[] KEY_WORDS = {S_DURATION, S_FROM, S_TO, S_PATH_MOTION_ARC, S_AUTO_TRANSITION, S_INTERPOLATOR, S_STAGGERED, S_FROM, S_TRANSITION_FLAGS};

        static int getType(int name) {
            switch (name) {
                case 509:
                case TYPE_DURATION /* 700 */:
                    return 2;
                case TYPE_FROM /* 701 */:
                case TYPE_TO /* 702 */:
                case TYPE_INTERPOLATOR /* 705 */:
                case TYPE_TRANSITION_FLAGS /* 707 */:
                    return 8;
                case TYPE_STAGGERED /* 706 */:
                    return 4;
                default:
                    return -1;
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:29:0x005c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        static int getId(String name) {
            switch (name) {
                case "duration":
                    return TYPE_DURATION;
                case "from":
                    return TYPE_FROM;
                case "to":
                    return TYPE_TO;
                case "pathMotionArc":
                    return 509;
                case "autoTransition":
                    return TYPE_AUTO_TRANSITION;
                case "motionInterpolator":
                    return TYPE_INTERPOLATOR;
                case "staggered":
                    return TYPE_STAGGERED;
                case "transitionFlags":
                    return TYPE_TRANSITION_FLAGS;
                default:
                    return -1;
            }
        }
    }
}

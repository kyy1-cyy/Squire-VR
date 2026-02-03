package androidx.dynamicanimation.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;

/* loaded from: classes.dex */
public final class SpringForce implements Force {
    public static final float DAMPING_RATIO_HIGH_BOUNCY = 0.2f;
    public static final float DAMPING_RATIO_LOW_BOUNCY = 0.75f;
    public static final float DAMPING_RATIO_MEDIUM_BOUNCY = 0.5f;
    public static final float DAMPING_RATIO_NO_BOUNCY = 1.0f;
    public static final float STIFFNESS_HIGH = 10000.0f;
    public static final float STIFFNESS_LOW = 200.0f;
    public static final float STIFFNESS_MEDIUM = 1500.0f;
    public static final float STIFFNESS_VERY_LOW = 50.0f;
    private static final double UNSET = Double.MAX_VALUE;
    private static final double VELOCITY_THRESHOLD_MULTIPLIER = 62.5d;
    private double mDampedFreq;
    double mDampingRatio;
    private double mFinalPosition;
    private double mGammaMinus;
    private double mGammaPlus;
    private boolean mInitialized;
    private final DynamicAnimation.MassState mMassState;
    double mNaturalFreq;
    private double mValueThreshold;
    private double mVelocityThreshold;

    public SpringForce() {
        this.mNaturalFreq = Math.sqrt(1500.0d);
        this.mDampingRatio = 0.5d;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
    }

    public SpringForce(float finalPosition) {
        this.mNaturalFreq = Math.sqrt(1500.0d);
        this.mDampingRatio = 0.5d;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
        this.mFinalPosition = finalPosition;
    }

    public SpringForce setStiffness(float stiffness) {
        if (stiffness <= 0.0f) {
            throw new IllegalArgumentException("Spring stiffness constant must be positive.");
        }
        this.mNaturalFreq = Math.sqrt(stiffness);
        this.mInitialized = false;
        return this;
    }

    public float getStiffness() {
        return (float) (this.mNaturalFreq * this.mNaturalFreq);
    }

    public SpringForce setDampingRatio(float dampingRatio) {
        if (dampingRatio < 0.0f) {
            throw new IllegalArgumentException("Damping ratio must be non-negative");
        }
        this.mDampingRatio = dampingRatio;
        this.mInitialized = false;
        return this;
    }

    public float getDampingRatio() {
        return (float) this.mDampingRatio;
    }

    public SpringForce setFinalPosition(float finalPosition) {
        this.mFinalPosition = finalPosition;
        return this;
    }

    public float getFinalPosition() {
        return (float) this.mFinalPosition;
    }

    @Override // androidx.dynamicanimation.animation.Force
    public float getAcceleration(float lastDisplacement, float lastVelocity) {
        float lastDisplacement2 = lastDisplacement - getFinalPosition();
        double k = this.mNaturalFreq * this.mNaturalFreq;
        double c = this.mNaturalFreq * 2.0d * this.mDampingRatio;
        return (float) (((-k) * lastDisplacement2) - (lastVelocity * c));
    }

    @Override // androidx.dynamicanimation.animation.Force
    public boolean isAtEquilibrium(float value, float velocity) {
        if (Math.abs(velocity) < this.mVelocityThreshold && Math.abs(value - getFinalPosition()) < this.mValueThreshold) {
            return true;
        }
        return false;
    }

    private void init() {
        if (this.mInitialized) {
            return;
        }
        if (this.mFinalPosition == Double.MAX_VALUE) {
            throw new IllegalStateException("Error: Final position of the spring must be set before the animation starts");
        }
        if (this.mDampingRatio > 1.0d) {
            this.mGammaPlus = ((-this.mDampingRatio) * this.mNaturalFreq) + (this.mNaturalFreq * Math.sqrt((this.mDampingRatio * this.mDampingRatio) - 1.0d));
            this.mGammaMinus = ((-this.mDampingRatio) * this.mNaturalFreq) - (this.mNaturalFreq * Math.sqrt((this.mDampingRatio * this.mDampingRatio) - 1.0d));
        } else if (this.mDampingRatio >= 0.0d && this.mDampingRatio < 1.0d) {
            this.mDampedFreq = this.mNaturalFreq * Math.sqrt(1.0d - (this.mDampingRatio * this.mDampingRatio));
        }
        this.mInitialized = true;
    }

    DynamicAnimation.MassState updateValues(double lastDisplacement, double lastVelocity, long timeElapsed) {
        double displacement;
        double currentVelocity;
        init();
        double deltaT = timeElapsed / 1000.0d;
        double lastDisplacement2 = lastDisplacement - this.mFinalPosition;
        if (this.mDampingRatio > 1.0d) {
            double coeffA = lastDisplacement2 - (((this.mGammaMinus * lastDisplacement2) - lastVelocity) / (this.mGammaMinus - this.mGammaPlus));
            double coeffB = ((this.mGammaMinus * lastDisplacement2) - lastVelocity) / (this.mGammaMinus - this.mGammaPlus);
            displacement = (Math.pow(2.718281828459045d, this.mGammaMinus * deltaT) * coeffA) + (Math.pow(2.718281828459045d, this.mGammaPlus * deltaT) * coeffB);
            currentVelocity = (this.mGammaMinus * coeffA * Math.pow(2.718281828459045d, this.mGammaMinus * deltaT)) + (this.mGammaPlus * coeffB * Math.pow(2.718281828459045d, this.mGammaPlus * deltaT));
        } else if (this.mDampingRatio != 1.0d) {
            double sinCoeff = (1.0d / this.mDampedFreq) * ((this.mDampingRatio * this.mNaturalFreq * lastDisplacement2) + lastVelocity);
            displacement = ((Math.cos(this.mDampedFreq * deltaT) * lastDisplacement2) + (Math.sin(this.mDampedFreq * deltaT) * sinCoeff)) * Math.pow(2.718281828459045d, (-this.mDampingRatio) * this.mNaturalFreq * deltaT);
            double d = (-this.mNaturalFreq) * displacement * this.mDampingRatio;
            double d2 = -this.mDampingRatio;
            double lastDisplacement3 = this.mNaturalFreq;
            double dPow = Math.pow(2.718281828459045d, d2 * lastDisplacement3 * deltaT);
            double d3 = (-this.mDampedFreq) * lastDisplacement2;
            double cosCoeff = this.mDampedFreq;
            currentVelocity = d + (dPow * ((d3 * Math.sin(cosCoeff * deltaT)) + (this.mDampedFreq * sinCoeff * Math.cos(this.mDampedFreq * deltaT))));
        } else {
            double coeffB2 = lastVelocity + (this.mNaturalFreq * lastDisplacement2);
            displacement = Math.pow(2.718281828459045d, (-this.mNaturalFreq) * deltaT) * ((coeffB2 * deltaT) + lastDisplacement2);
            currentVelocity = (((coeffB2 * deltaT) + lastDisplacement2) * Math.pow(2.718281828459045d, (-this.mNaturalFreq) * deltaT) * (-this.mNaturalFreq)) + (Math.pow(2.718281828459045d, (-this.mNaturalFreq) * deltaT) * coeffB2);
        }
        this.mMassState.mValue = (float) (this.mFinalPosition + displacement);
        this.mMassState.mVelocity = (float) currentVelocity;
        return this.mMassState;
    }

    void setValueThreshold(double threshold) {
        this.mValueThreshold = Math.abs(threshold);
        this.mVelocityThreshold = this.mValueThreshold * VELOCITY_THRESHOLD_MULTIPLIER;
    }
}

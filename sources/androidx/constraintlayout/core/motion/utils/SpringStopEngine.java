package androidx.constraintlayout.core.motion.utils;

/* loaded from: classes.dex */
public class SpringStopEngine implements StopEngine {
    private static final double UNSET = Double.MAX_VALUE;
    private float mLastTime;
    private double mLastVelocity;
    private float mMass;
    private float mPos;
    private double mStiffness;
    private float mStopThreshold;
    private double mTargetPos;
    private float mV;
    double mDamping = 0.5d;
    private boolean mInitialized = false;
    private int mBoundaryMode = 0;

    @Override // androidx.constraintlayout.core.motion.utils.StopEngine
    public String debug(String desc, float time) {
        return null;
    }

    void log(String str) {
        StackTraceElement s = new Throwable().getStackTrace()[1];
        String line = ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName() + "() ";
        System.out.println(line + str);
    }

    public void springConfig(float currentPos, float target, float currentVelocity, float mass, float stiffness, float damping, float stopThreshold, int boundaryMode) {
        this.mTargetPos = target;
        this.mDamping = damping;
        this.mInitialized = false;
        this.mPos = currentPos;
        this.mLastVelocity = currentVelocity;
        this.mStiffness = stiffness;
        this.mMass = mass;
        this.mStopThreshold = stopThreshold;
        this.mBoundaryMode = boundaryMode;
        this.mLastTime = 0.0f;
    }

    @Override // androidx.constraintlayout.core.motion.utils.StopEngine
    public float getVelocity(float t) {
        return this.mV;
    }

    @Override // androidx.constraintlayout.core.motion.utils.StopEngine
    public float getInterpolation(float time) {
        compute(time - this.mLastTime);
        this.mLastTime = time;
        return this.mPos;
    }

    public float getAcceleration() {
        double k = this.mStiffness;
        double c = this.mDamping;
        double x = this.mPos - this.mTargetPos;
        return ((float) (((-k) * x) - (this.mV * c))) / this.mMass;
    }

    @Override // androidx.constraintlayout.core.motion.utils.StopEngine
    public float getVelocity() {
        return 0.0f;
    }

    @Override // androidx.constraintlayout.core.motion.utils.StopEngine
    public boolean isStopped() {
        double x = this.mPos - this.mTargetPos;
        double k = this.mStiffness;
        double v = this.mV;
        double m = this.mMass;
        double energy = (v * v * m) + (k * x * x);
        double max_def = Math.sqrt(energy / k);
        return max_def <= ((double) this.mStopThreshold);
    }

    private void compute(double dt) {
        double k = this.mStiffness;
        double c = this.mDamping;
        int overSample = (int) ((9.0d / ((Math.sqrt(this.mStiffness / this.mMass) * dt) * 4.0d)) + 1.0d);
        double dt2 = dt / overSample;
        int i = 0;
        while (i < overSample) {
            double x = this.mPos - this.mTargetPos;
            double a = (((-k) * x) - (this.mV * c)) / this.mMass;
            double avgV = this.mV + ((a * dt2) / 2.0d);
            double x2 = this.mPos;
            double a2 = this.mTargetPos;
            double avgX = (x2 + ((dt2 * avgV) / 2.0d)) - a2;
            double k2 = k;
            double a3 = (((-avgX) * k) - (avgV * c)) / this.mMass;
            double dv = a3 * dt2;
            double c2 = c;
            double c3 = this.mV;
            this.mV = (float) (this.mV + dv);
            this.mPos = (float) (this.mPos + ((c3 + (dv / 2.0d)) * dt2));
            if (this.mBoundaryMode > 0) {
                if (this.mPos < 0.0f && (this.mBoundaryMode & 1) == 1) {
                    this.mPos = -this.mPos;
                    this.mV = -this.mV;
                }
                if (this.mPos > 1.0f && (this.mBoundaryMode & 2) == 2) {
                    this.mPos = 2.0f - this.mPos;
                    this.mV = -this.mV;
                }
            }
            i++;
            k = k2;
            c = c2;
        }
    }
}

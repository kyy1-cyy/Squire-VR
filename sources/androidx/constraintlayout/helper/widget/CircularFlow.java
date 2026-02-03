package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.constraintlayout.widget.VirtualLayout;
import java.util.Arrays;

/* loaded from: classes.dex */
public class CircularFlow extends VirtualLayout {
    private static final String TAG = "CircularFlow";
    private float[] mAngles;
    ConstraintLayout mContainer;
    private int mCountAngle;
    private int mCountRadius;
    private int[] mRadius;
    private String mReferenceAngles;
    private Float mReferenceDefaultAngle;
    private Integer mReferenceDefaultRadius;
    private String mReferenceRadius;
    int mViewCenter;
    private static int DEFAULT_RADIUS = 0;
    private static float DEFAULT_ANGLE = 0.0f;

    public CircularFlow(Context context) {
        super(context);
    }

    public CircularFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularFlow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int[] getRadius() {
        return Arrays.copyOf(this.mRadius, this.mCountRadius);
    }

    public float[] getAngles() {
        return Arrays.copyOf(this.mAngles, this.mCountAngle);
    }

    @Override // androidx.constraintlayout.widget.VirtualLayout, androidx.constraintlayout.widget.ConstraintHelper
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_circularflow_viewCenter) {
                    this.mViewCenter = a.getResourceId(attr, 0);
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_angles) {
                    this.mReferenceAngles = a.getString(attr);
                    setAngles(this.mReferenceAngles);
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_radiusInDP) {
                    this.mReferenceRadius = a.getString(attr);
                    setRadius(this.mReferenceRadius);
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_defaultAngle) {
                    this.mReferenceDefaultAngle = Float.valueOf(a.getFloat(attr, DEFAULT_ANGLE));
                    setDefaultAngle(this.mReferenceDefaultAngle.floatValue());
                } else if (attr == R.styleable.ConstraintLayout_Layout_circularflow_defaultRadius) {
                    this.mReferenceDefaultRadius = Integer.valueOf(a.getDimensionPixelSize(attr, DEFAULT_RADIUS));
                    setDefaultRadius(this.mReferenceDefaultRadius.intValue());
                }
            }
            a.recycle();
        }
    }

    @Override // androidx.constraintlayout.widget.VirtualLayout, androidx.constraintlayout.widget.ConstraintHelper, android.view.View
    public void onAttachedToWindow() throws IllegalAccessException, NoSuchFieldException, Resources.NotFoundException, IllegalArgumentException {
        super.onAttachedToWindow();
        if (this.mReferenceAngles != null) {
            this.mAngles = new float[1];
            setAngles(this.mReferenceAngles);
        }
        if (this.mReferenceRadius != null) {
            this.mRadius = new int[1];
            setRadius(this.mReferenceRadius);
        }
        if (this.mReferenceDefaultAngle != null) {
            setDefaultAngle(this.mReferenceDefaultAngle.floatValue());
        }
        if (this.mReferenceDefaultRadius != null) {
            setDefaultRadius(this.mReferenceDefaultRadius.intValue());
        }
        anchorReferences();
    }

    private void anchorReferences() {
        this.mContainer = (ConstraintLayout) getParent();
        for (int i = 0; i < this.mCount; i++) {
            View view = this.mContainer.getViewById(this.mIds[i]);
            if (view != null) {
                int radius = DEFAULT_RADIUS;
                float angle = DEFAULT_ANGLE;
                if (this.mRadius != null && i < this.mRadius.length) {
                    radius = this.mRadius[i];
                } else if (this.mReferenceDefaultRadius != null && this.mReferenceDefaultRadius.intValue() != -1) {
                    this.mCountRadius++;
                    if (this.mRadius == null) {
                        this.mRadius = new int[1];
                    }
                    this.mRadius = getRadius();
                    this.mRadius[this.mCountRadius - 1] = radius;
                } else {
                    Log.e(TAG, "Added radius to view with id: " + this.mMap.get(Integer.valueOf(view.getId())));
                }
                if (this.mAngles != null && i < this.mAngles.length) {
                    angle = this.mAngles[i];
                } else if (this.mReferenceDefaultAngle != null && this.mReferenceDefaultAngle.floatValue() != -1.0f) {
                    this.mCountAngle++;
                    if (this.mAngles == null) {
                        this.mAngles = new float[1];
                    }
                    this.mAngles = getAngles();
                    this.mAngles[this.mCountAngle - 1] = angle;
                } else {
                    Log.e(TAG, "Added angle to view with id: " + this.mMap.get(Integer.valueOf(view.getId())));
                }
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                params.circleAngle = angle;
                params.circleConstraint = this.mViewCenter;
                params.circleRadius = radius;
                view.setLayoutParams(params);
            }
        }
        applyLayoutFeatures();
    }

    public void addViewToCircularFlow(View view, int radius, float angle) {
        if (containsId(view.getId())) {
            return;
        }
        addView(view);
        this.mCountAngle++;
        this.mAngles = getAngles();
        this.mAngles[this.mCountAngle - 1] = angle;
        this.mCountRadius++;
        this.mRadius = getRadius();
        this.mRadius[this.mCountRadius - 1] = (int) (radius * this.myContext.getResources().getDisplayMetrics().density);
        anchorReferences();
    }

    public void updateRadius(View view, int radius) {
        if (!isUpdatable(view)) {
            Log.e(TAG, "It was not possible to update radius to view with id: " + view.getId());
            return;
        }
        int indexView = indexFromId(view.getId());
        if (indexView > this.mRadius.length) {
            return;
        }
        this.mRadius = getRadius();
        this.mRadius[indexView] = (int) (radius * this.myContext.getResources().getDisplayMetrics().density);
        anchorReferences();
    }

    public void updateAngle(View view, float angle) {
        if (!isUpdatable(view)) {
            Log.e(TAG, "It was not possible to update angle to view with id: " + view.getId());
            return;
        }
        int indexView = indexFromId(view.getId());
        if (indexView > this.mAngles.length) {
            return;
        }
        this.mAngles = getAngles();
        this.mAngles[indexView] = angle;
        anchorReferences();
    }

    public void updateReference(View view, int radius, float angle) {
        if (!isUpdatable(view)) {
            Log.e(TAG, "It was not possible to update radius and angle to view with id: " + view.getId());
            return;
        }
        int indexView = indexFromId(view.getId());
        if (getAngles().length > indexView) {
            this.mAngles = getAngles();
            this.mAngles[indexView] = angle;
        }
        if (getRadius().length > indexView) {
            this.mRadius = getRadius();
            this.mRadius[indexView] = (int) (radius * this.myContext.getResources().getDisplayMetrics().density);
        }
        anchorReferences();
    }

    public void setDefaultAngle(float angle) {
        DEFAULT_ANGLE = angle;
    }

    public void setDefaultRadius(int radius) {
        DEFAULT_RADIUS = radius;
    }

    @Override // androidx.constraintlayout.widget.ConstraintHelper
    public int removeView(View view) {
        int index = super.removeView(view);
        if (index == -1) {
            return index;
        }
        ConstraintSet c = new ConstraintSet();
        c.clone(this.mContainer);
        c.clear(view.getId(), 8);
        c.applyTo(this.mContainer);
        if (index < this.mAngles.length) {
            this.mAngles = removeAngle(this.mAngles, index);
            this.mCountAngle--;
        }
        if (index < this.mRadius.length) {
            this.mRadius = removeRadius(this.mRadius, index);
            this.mCountRadius--;
        }
        anchorReferences();
        return index;
    }

    private float[] removeAngle(float[] angles, int index) {
        if (angles == null || index < 0 || index >= this.mCountAngle) {
            return angles;
        }
        return removeElementFromArray(angles, index);
    }

    private int[] removeRadius(int[] radius, int index) {
        if (radius == null || index < 0 || index >= this.mCountRadius) {
            return radius;
        }
        return removeElementFromArray(radius, index);
    }

    private void setAngles(String idList) {
        if (idList == null) {
            return;
        }
        int begin = 0;
        this.mCountAngle = 0;
        while (true) {
            int end = idList.indexOf(44, begin);
            if (end == -1) {
                addAngle(idList.substring(begin).trim());
                return;
            } else {
                addAngle(idList.substring(begin, end).trim());
                begin = end + 1;
            }
        }
    }

    private void setRadius(String idList) {
        if (idList == null) {
            return;
        }
        int begin = 0;
        this.mCountRadius = 0;
        while (true) {
            int end = idList.indexOf(44, begin);
            if (end == -1) {
                addRadius(idList.substring(begin).trim());
                return;
            } else {
                addRadius(idList.substring(begin, end).trim());
                begin = end + 1;
            }
        }
    }

    private void addAngle(String angleString) {
        if (angleString == null || angleString.length() == 0 || this.myContext == null || this.mAngles == null) {
            return;
        }
        if (this.mCountAngle + 1 > this.mAngles.length) {
            this.mAngles = Arrays.copyOf(this.mAngles, this.mAngles.length + 1);
        }
        this.mAngles[this.mCountAngle] = Integer.parseInt(angleString);
        this.mCountAngle++;
    }

    private void addRadius(String radiusString) {
        if (radiusString == null || radiusString.length() == 0 || this.myContext == null || this.mRadius == null) {
            return;
        }
        if (this.mCountRadius + 1 > this.mRadius.length) {
            this.mRadius = Arrays.copyOf(this.mRadius, this.mRadius.length + 1);
        }
        this.mRadius[this.mCountRadius] = (int) (Integer.parseInt(radiusString) * this.myContext.getResources().getDisplayMetrics().density);
        this.mCountRadius++;
    }

    public static int[] removeElementFromArray(int[] array, int index) {
        int[] newArray = new int[array.length - 1];
        int k = 0;
        for (int i = 0; i < array.length; i++) {
            if (i != index) {
                newArray[k] = array[i];
                k++;
            }
        }
        return newArray;
    }

    public static float[] removeElementFromArray(float[] array, int index) {
        float[] newArray = new float[array.length - 1];
        int k = 0;
        for (int i = 0; i < array.length; i++) {
            if (i != index) {
                newArray[k] = array[i];
                k++;
            }
        }
        return newArray;
    }

    public boolean isUpdatable(View view) {
        if (!containsId(view.getId())) {
            return false;
        }
        int indexView = indexFromId(view.getId());
        return indexView != -1;
    }
}

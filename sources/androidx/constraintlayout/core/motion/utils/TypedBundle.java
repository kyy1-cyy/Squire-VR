package androidx.constraintlayout.core.motion.utils;

import java.util.Arrays;

/* loaded from: classes.dex */
public class TypedBundle {
    private static final int INITIAL_BOOLEAN = 4;
    private static final int INITIAL_FLOAT = 10;
    private static final int INITIAL_INT = 10;
    private static final int INITIAL_STRING = 5;
    int[] mTypeInt = new int[10];
    int[] mValueInt = new int[10];
    int mCountInt = 0;
    int[] mTypeFloat = new int[10];
    float[] mValueFloat = new float[10];
    int mCountFloat = 0;
    int[] mTypeString = new int[5];
    String[] mValueString = new String[5];
    int mCountString = 0;
    int[] mTypeBoolean = new int[4];
    boolean[] mValueBoolean = new boolean[4];
    int mCountBoolean = 0;

    public int getInteger(int type) {
        for (int i = 0; i < this.mCountInt; i++) {
            if (this.mTypeInt[i] == type) {
                return this.mValueInt[i];
            }
        }
        return -1;
    }

    public void add(int type, int value) {
        if (this.mCountInt >= this.mTypeInt.length) {
            this.mTypeInt = Arrays.copyOf(this.mTypeInt, this.mTypeInt.length * 2);
            this.mValueInt = Arrays.copyOf(this.mValueInt, this.mValueInt.length * 2);
        }
        this.mTypeInt[this.mCountInt] = type;
        int[] iArr = this.mValueInt;
        int i = this.mCountInt;
        this.mCountInt = i + 1;
        iArr[i] = value;
    }

    public void add(int type, float value) {
        if (this.mCountFloat >= this.mTypeFloat.length) {
            this.mTypeFloat = Arrays.copyOf(this.mTypeFloat, this.mTypeFloat.length * 2);
            this.mValueFloat = Arrays.copyOf(this.mValueFloat, this.mValueFloat.length * 2);
        }
        this.mTypeFloat[this.mCountFloat] = type;
        float[] fArr = this.mValueFloat;
        int i = this.mCountFloat;
        this.mCountFloat = i + 1;
        fArr[i] = value;
    }

    public void addIfNotNull(int type, String value) {
        if (value != null) {
            add(type, value);
        }
    }

    public void add(int type, String value) {
        if (this.mCountString >= this.mTypeString.length) {
            this.mTypeString = Arrays.copyOf(this.mTypeString, this.mTypeString.length * 2);
            this.mValueString = (String[]) Arrays.copyOf(this.mValueString, this.mValueString.length * 2);
        }
        this.mTypeString[this.mCountString] = type;
        String[] strArr = this.mValueString;
        int i = this.mCountString;
        this.mCountString = i + 1;
        strArr[i] = value;
    }

    public void add(int type, boolean value) {
        if (this.mCountBoolean >= this.mTypeBoolean.length) {
            this.mTypeBoolean = Arrays.copyOf(this.mTypeBoolean, this.mTypeBoolean.length * 2);
            this.mValueBoolean = Arrays.copyOf(this.mValueBoolean, this.mValueBoolean.length * 2);
        }
        this.mTypeBoolean[this.mCountBoolean] = type;
        boolean[] zArr = this.mValueBoolean;
        int i = this.mCountBoolean;
        this.mCountBoolean = i + 1;
        zArr[i] = value;
    }

    public void applyDelta(TypedValues values) {
        for (int i = 0; i < this.mCountInt; i++) {
            values.setValue(this.mTypeInt[i], this.mValueInt[i]);
        }
        for (int i2 = 0; i2 < this.mCountFloat; i2++) {
            values.setValue(this.mTypeFloat[i2], this.mValueFloat[i2]);
        }
        for (int i3 = 0; i3 < this.mCountString; i3++) {
            values.setValue(this.mTypeString[i3], this.mValueString[i3]);
        }
        for (int i4 = 0; i4 < this.mCountBoolean; i4++) {
            values.setValue(this.mTypeBoolean[i4], this.mValueBoolean[i4]);
        }
    }

    public void applyDelta(TypedBundle values) {
        for (int i = 0; i < this.mCountInt; i++) {
            values.add(this.mTypeInt[i], this.mValueInt[i]);
        }
        for (int i2 = 0; i2 < this.mCountFloat; i2++) {
            values.add(this.mTypeFloat[i2], this.mValueFloat[i2]);
        }
        for (int i3 = 0; i3 < this.mCountString; i3++) {
            values.add(this.mTypeString[i3], this.mValueString[i3]);
        }
        for (int i4 = 0; i4 < this.mCountBoolean; i4++) {
            values.add(this.mTypeBoolean[i4], this.mValueBoolean[i4]);
        }
    }

    public void clear() {
        this.mCountBoolean = 0;
        this.mCountString = 0;
        this.mCountFloat = 0;
        this.mCountInt = 0;
    }
}

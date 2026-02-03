package androidx.constraintlayout.core.motion.utils;

import androidx.constraintlayout.core.motion.CustomAttribute;
import androidx.constraintlayout.core.motion.CustomVariable;
import java.util.Arrays;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class KeyFrameArray {

    public static class CustomArray {
        private static final int EMPTY = 999;
        int count;
        int[] keys = new int[TypedValues.TYPE_TARGET];
        CustomAttribute[] values = new CustomAttribute[TypedValues.TYPE_TARGET];

        public CustomArray() {
            clear();
        }

        public void clear() {
            Arrays.fill(this.keys, EMPTY);
            Arrays.fill(this.values, (Object) null);
            this.count = 0;
        }

        public void dump() {
            System.out.println("V: " + Arrays.toString(Arrays.copyOf(this.keys, this.count)));
            System.out.print("K: [");
            int i = 0;
            while (i < this.count) {
                System.out.print((i == 0 ? HttpUrl.FRAGMENT_ENCODE_SET : ", ") + valueAt(i));
                i++;
            }
            System.out.println("]");
        }

        public int size() {
            return this.count;
        }

        public CustomAttribute valueAt(int i) {
            return this.values[this.keys[i]];
        }

        public int keyAt(int i) {
            return this.keys[i];
        }

        public void append(int position, CustomAttribute value) {
            if (this.values[position] != null) {
                remove(position);
            }
            this.values[position] = value;
            int[] iArr = this.keys;
            int i = this.count;
            this.count = i + 1;
            iArr[i] = position;
            Arrays.sort(this.keys);
        }

        public void remove(int position) {
            this.values[position] = null;
            int j = 0;
            for (int i = 0; i < this.count; i++) {
                if (position == this.keys[i]) {
                    this.keys[i] = EMPTY;
                    j++;
                }
                if (i != j) {
                    this.keys[i] = this.keys[j];
                }
                j++;
            }
            int j2 = this.count;
            this.count = j2 - 1;
        }
    }

    public static class CustomVar {
        private static final int EMPTY = 999;
        int count;
        int[] keys = new int[TypedValues.TYPE_TARGET];
        CustomVariable[] values = new CustomVariable[TypedValues.TYPE_TARGET];

        public CustomVar() {
            clear();
        }

        public void clear() {
            Arrays.fill(this.keys, EMPTY);
            Arrays.fill(this.values, (Object) null);
            this.count = 0;
        }

        public void dump() {
            System.out.println("V: " + Arrays.toString(Arrays.copyOf(this.keys, this.count)));
            System.out.print("K: [");
            int i = 0;
            while (i < this.count) {
                System.out.print((i == 0 ? HttpUrl.FRAGMENT_ENCODE_SET : ", ") + valueAt(i));
                i++;
            }
            System.out.println("]");
        }

        public int size() {
            return this.count;
        }

        public CustomVariable valueAt(int i) {
            return this.values[this.keys[i]];
        }

        public int keyAt(int i) {
            return this.keys[i];
        }

        public void append(int position, CustomVariable value) {
            if (this.values[position] != null) {
                remove(position);
            }
            this.values[position] = value;
            int[] iArr = this.keys;
            int i = this.count;
            this.count = i + 1;
            iArr[i] = position;
            Arrays.sort(this.keys);
        }

        public void remove(int position) {
            this.values[position] = null;
            int j = 0;
            for (int i = 0; i < this.count; i++) {
                if (position == this.keys[i]) {
                    this.keys[i] = EMPTY;
                    j++;
                }
                if (i != j) {
                    this.keys[i] = this.keys[j];
                }
                j++;
            }
            int j2 = this.count;
            this.count = j2 - 1;
        }
    }

    static class FloatArray {
        private static final int EMPTY = 999;
        int count;
        int[] keys = new int[TypedValues.TYPE_TARGET];
        float[][] values = new float[TypedValues.TYPE_TARGET][];

        public FloatArray() {
            clear();
        }

        public void clear() {
            Arrays.fill(this.keys, EMPTY);
            Arrays.fill(this.values, (Object) null);
            this.count = 0;
        }

        public void dump() {
            System.out.println("V: " + Arrays.toString(Arrays.copyOf(this.keys, this.count)));
            System.out.print("K: [");
            int i = 0;
            while (i < this.count) {
                System.out.print((i == 0 ? HttpUrl.FRAGMENT_ENCODE_SET : ", ") + Arrays.toString(valueAt(i)));
                i++;
            }
            System.out.println("]");
        }

        public int size() {
            return this.count;
        }

        public float[] valueAt(int i) {
            return this.values[this.keys[i]];
        }

        public int keyAt(int i) {
            return this.keys[i];
        }

        public void append(int position, float[] value) {
            if (this.values[position] != null) {
                remove(position);
            }
            this.values[position] = value;
            int[] iArr = this.keys;
            int i = this.count;
            this.count = i + 1;
            iArr[i] = position;
            Arrays.sort(this.keys);
        }

        public void remove(int position) {
            this.values[position] = null;
            int j = 0;
            for (int i = 0; i < this.count; i++) {
                if (position == this.keys[i]) {
                    this.keys[i] = EMPTY;
                    j++;
                }
                if (i != j) {
                    this.keys[i] = this.keys[j];
                }
                j++;
            }
            int j2 = this.count;
            this.count = j2 - 1;
        }
    }
}

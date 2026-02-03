package androidx.constraintlayout.core;

import androidx.constraintlayout.core.ArrayRow;
import java.util.Arrays;

/* loaded from: classes.dex */
public class SolverVariableValues implements ArrayRow.ArrayRowVariables {
    private static final boolean DEBUG = false;
    private static final boolean HASH = true;
    private static float epsilon = 0.001f;
    protected final Cache mCache;
    private final ArrayRow mRow;
    private final int NONE = -1;
    private int SIZE = 16;
    private int HASH_SIZE = 16;
    int[] keys = new int[this.SIZE];
    int[] nextKeys = new int[this.SIZE];
    int[] variables = new int[this.SIZE];
    float[] values = new float[this.SIZE];
    int[] previous = new int[this.SIZE];
    int[] next = new int[this.SIZE];
    int mCount = 0;
    int head = -1;

    SolverVariableValues(ArrayRow row, Cache cache) {
        this.mRow = row;
        this.mCache = cache;
        clear();
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public int getCurrentSize() {
        return this.mCount;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public SolverVariable getVariable(int index) {
        int count = this.mCount;
        if (count == 0) {
            return null;
        }
        int j = this.head;
        for (int i = 0; i < count; i++) {
            if (i == index && j != -1) {
                return this.mCache.mIndexedVariables[this.variables[j]];
            }
            j = this.next[j];
            if (j == -1) {
                break;
            }
        }
        return null;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public float getVariableValue(int index) {
        int count = this.mCount;
        int j = this.head;
        for (int i = 0; i < count; i++) {
            if (i == index) {
                return this.values[j];
            }
            j = this.next[j];
            if (j == -1) {
                return 0.0f;
            }
        }
        return 0.0f;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public boolean contains(SolverVariable variable) {
        if (indexOf(variable) != -1) {
            return HASH;
        }
        return false;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public int indexOf(SolverVariable variable) {
        if (this.mCount == 0 || variable == null) {
            return -1;
        }
        int id = variable.id;
        int key = this.keys[id % this.HASH_SIZE];
        if (key == -1) {
            return -1;
        }
        if (this.variables[key] == id) {
            return key;
        }
        while (this.nextKeys[key] != -1 && this.variables[this.nextKeys[key]] != id) {
            key = this.nextKeys[key];
        }
        if (this.nextKeys[key] != -1 && this.variables[this.nextKeys[key]] == id) {
            return this.nextKeys[key];
        }
        return -1;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public float get(SolverVariable variable) {
        int index = indexOf(variable);
        if (index != -1) {
            return this.values[index];
        }
        return 0.0f;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void display() {
        int count = this.mCount;
        System.out.print("{ ");
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v != null) {
                System.out.print(v + " = " + getVariableValue(i) + " ");
            }
        }
        System.out.println(" }");
    }

    public String toString() {
        String str;
        String str2;
        String str3 = hashCode() + " { ";
        int count = this.mCount;
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v != null) {
                String str4 = str3 + v + " = " + getVariableValue(i) + " ";
                int index = indexOf(v);
                String str5 = str4 + "[p: ";
                if (this.previous[index] != -1) {
                    str = str5 + this.mCache.mIndexedVariables[this.variables[this.previous[index]]];
                } else {
                    str = str5 + "none";
                }
                String str6 = str + ", n: ";
                if (this.next[index] != -1) {
                    str2 = str6 + this.mCache.mIndexedVariables[this.variables[this.next[index]]];
                } else {
                    str2 = str6 + "none";
                }
                str3 = str2 + "]";
            }
        }
        return str3 + " }";
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void clear() {
        int count = this.mCount;
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v != null) {
                v.removeFromRow(this.mRow);
            }
        }
        for (int i2 = 0; i2 < this.SIZE; i2++) {
            this.variables[i2] = -1;
            this.nextKeys[i2] = -1;
        }
        for (int i3 = 0; i3 < this.HASH_SIZE; i3++) {
            this.keys[i3] = -1;
        }
        this.mCount = 0;
        this.head = -1;
    }

    private void increaseSize() {
        int size = this.SIZE * 2;
        this.variables = Arrays.copyOf(this.variables, size);
        this.values = Arrays.copyOf(this.values, size);
        this.previous = Arrays.copyOf(this.previous, size);
        this.next = Arrays.copyOf(this.next, size);
        this.nextKeys = Arrays.copyOf(this.nextKeys, size);
        for (int i = this.SIZE; i < size; i++) {
            this.variables[i] = -1;
            this.nextKeys[i] = -1;
        }
        this.SIZE = size;
    }

    private void addToHashMap(SolverVariable variable, int index) {
        int hash = variable.id % this.HASH_SIZE;
        int key = this.keys[hash];
        if (key != -1) {
            while (this.nextKeys[key] != -1) {
                key = this.nextKeys[key];
            }
            this.nextKeys[key] = index;
        } else {
            this.keys[hash] = index;
        }
        this.nextKeys[index] = -1;
    }

    private void displayHash() {
        for (int i = 0; i < this.HASH_SIZE; i++) {
            if (this.keys[i] != -1) {
                String str = hashCode() + " hash [" + i + "] => ";
                int key = this.keys[i];
                boolean done = false;
                while (!done) {
                    str = str + " " + this.variables[key];
                    if (this.nextKeys[key] != -1) {
                        key = this.nextKeys[key];
                    } else {
                        done = HASH;
                    }
                }
                System.out.println(str);
            }
        }
    }

    private void removeFromHashMap(SolverVariable variable) {
        int hash = variable.id % this.HASH_SIZE;
        int key = this.keys[hash];
        if (key == -1) {
            return;
        }
        int id = variable.id;
        if (this.variables[key] != id) {
            while (this.nextKeys[key] != -1 && this.variables[this.nextKeys[key]] != id) {
                key = this.nextKeys[key];
            }
            int currentKey = this.nextKeys[key];
            if (currentKey != -1 && this.variables[currentKey] == id) {
                this.nextKeys[key] = this.nextKeys[currentKey];
                this.nextKeys[currentKey] = -1;
                return;
            }
            return;
        }
        this.keys[hash] = this.nextKeys[key];
        this.nextKeys[key] = -1;
    }

    private void addVariable(int index, SolverVariable variable, float value) {
        this.variables[index] = variable.id;
        this.values[index] = value;
        this.previous[index] = -1;
        this.next[index] = -1;
        variable.addToRow(this.mRow);
        variable.usageInRowCount++;
        this.mCount++;
    }

    private int findEmptySlot() {
        for (int i = 0; i < this.SIZE; i++) {
            if (this.variables[i] == -1) {
                return i;
            }
        }
        return -1;
    }

    private void insertVariable(int index, SolverVariable variable, float value) {
        int availableSlot = findEmptySlot();
        addVariable(availableSlot, variable, value);
        if (index == -1) {
            this.previous[availableSlot] = -1;
            if (this.mCount <= 0) {
                this.next[availableSlot] = -1;
            } else {
                this.next[availableSlot] = this.head;
                this.head = availableSlot;
            }
        } else {
            this.previous[availableSlot] = index;
            this.next[availableSlot] = this.next[index];
            this.next[index] = availableSlot;
        }
        if (this.next[availableSlot] != -1) {
            this.previous[this.next[availableSlot]] = availableSlot;
        }
        addToHashMap(variable, availableSlot);
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void put(SolverVariable variable, float value) {
        if (value > (-epsilon) && value < epsilon) {
            remove(variable, HASH);
            return;
        }
        if (this.mCount == 0) {
            addVariable(0, variable, value);
            addToHashMap(variable, 0);
            this.head = 0;
            return;
        }
        int index = indexOf(variable);
        if (index != -1) {
            this.values[index] = value;
            return;
        }
        if (this.mCount + 1 >= this.SIZE) {
            increaseSize();
        }
        int count = this.mCount;
        int previousItem = -1;
        int j = this.head;
        for (int i = 0; i < count; i++) {
            if (this.variables[j] == variable.id) {
                this.values[j] = value;
                return;
            }
            if (this.variables[j] < variable.id) {
                previousItem = j;
            }
            j = this.next[j];
            if (j == -1) {
                break;
            }
        }
        insertVariable(previousItem, variable, value);
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public int sizeInBytes() {
        return 0;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public float remove(SolverVariable v, boolean removeFromDefinition) {
        int index = indexOf(v);
        if (index == -1) {
            return 0.0f;
        }
        removeFromHashMap(v);
        float value = this.values[index];
        if (this.head == index) {
            this.head = this.next[index];
        }
        this.variables[index] = -1;
        if (this.previous[index] != -1) {
            this.next[this.previous[index]] = this.next[index];
        }
        if (this.next[index] != -1) {
            this.previous[this.next[index]] = this.previous[index];
        }
        this.mCount--;
        v.usageInRowCount--;
        if (removeFromDefinition) {
            v.removeFromRow(this.mRow);
        }
        return value;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void add(SolverVariable v, float value, boolean removeFromDefinition) {
        if (value > (-epsilon) && value < epsilon) {
            return;
        }
        int index = indexOf(v);
        if (index == -1) {
            put(v, value);
            return;
        }
        float[] fArr = this.values;
        fArr[index] = fArr[index] + value;
        if (this.values[index] > (-epsilon) && this.values[index] < epsilon) {
            this.values[index] = 0.0f;
            remove(v, removeFromDefinition);
        }
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public float use(ArrayRow def, boolean removeFromDefinition) {
        float value = get(def.variable);
        remove(def.variable, removeFromDefinition);
        SolverVariableValues definition = (SolverVariableValues) def.variables;
        int definitionSize = definition.getCurrentSize();
        int i = definition.head;
        int j = 0;
        int i2 = 0;
        while (j < definitionSize) {
            if (definition.variables[i2] != -1) {
                float definitionValue = definition.values[i2];
                SolverVariable definitionVariable = this.mCache.mIndexedVariables[definition.variables[i2]];
                add(definitionVariable, definitionValue * value, removeFromDefinition);
                j++;
            }
            i2++;
        }
        return value;
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void invert() {
        int count = this.mCount;
        int j = this.head;
        for (int i = 0; i < count; i++) {
            float[] fArr = this.values;
            fArr[j] = fArr[j] * (-1.0f);
            j = this.next[j];
            if (j == -1) {
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.core.ArrayRow.ArrayRowVariables
    public void divideByAmount(float amount) {
        int count = this.mCount;
        int j = this.head;
        for (int i = 0; i < count; i++) {
            float[] fArr = this.values;
            fArr[j] = fArr[j] / amount;
            j = this.next[j];
            if (j == -1) {
                return;
            }
        }
    }
}

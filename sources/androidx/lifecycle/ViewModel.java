package androidx.lifecycle;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class ViewModel {
    private final Map<String, Object> mBagOfTags = new HashMap();
    private final Set<Closeable> mCloseables = new LinkedHashSet();
    private volatile boolean mCleared = false;

    public ViewModel() {
    }

    public ViewModel(Closeable... closeables) {
        this.mCloseables.addAll(Arrays.asList(closeables));
    }

    public void addCloseable(Closeable closeable) {
        if (this.mCloseables != null) {
            synchronized (this.mCloseables) {
                this.mCloseables.add(closeable);
            }
        }
    }

    protected void onCleared() {
    }

    final void clear() {
        this.mCleared = true;
        if (this.mBagOfTags != null) {
            synchronized (this.mBagOfTags) {
                for (Object value : this.mBagOfTags.values()) {
                    closeWithRuntimeException(value);
                }
            }
        }
        if (this.mCloseables != null) {
            synchronized (this.mCloseables) {
                for (Closeable closeable : this.mCloseables) {
                    closeWithRuntimeException(closeable);
                }
            }
        }
        onCleared();
    }

    /* JADX WARN: Multi-variable type inference failed */
    <T> T setTagIfAbsent(String key, T newValue) throws IOException {
        Object obj;
        synchronized (this.mBagOfTags) {
            obj = this.mBagOfTags.get(key);
            if (obj == 0) {
                this.mBagOfTags.put(key, newValue);
            }
        }
        T result = obj == 0 ? newValue : obj;
        if (this.mCleared) {
            closeWithRuntimeException(result);
        }
        return result;
    }

    <T> T getTag(String str) {
        T t;
        if (this.mBagOfTags == null) {
            return null;
        }
        synchronized (this.mBagOfTags) {
            t = (T) this.mBagOfTags.get(str);
        }
        return t;
    }

    private static void closeWithRuntimeException(Object obj) throws IOException {
        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

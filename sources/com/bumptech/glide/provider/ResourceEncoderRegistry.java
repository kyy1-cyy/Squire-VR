package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceEncoder;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ResourceEncoderRegistry {
    private final List<Entry<?>> encoders = new ArrayList();

    public synchronized <Z> void append(Class<Z> resourceClass, ResourceEncoder<Z> encoder) {
        this.encoders.add(new Entry<>(resourceClass, encoder));
    }

    public synchronized <Z> void prepend(Class<Z> resourceClass, ResourceEncoder<Z> encoder) {
        this.encoders.add(0, new Entry<>(resourceClass, encoder));
    }

    public synchronized <Z> ResourceEncoder<Z> get(Class<Z> cls) {
        int size = this.encoders.size();
        for (int i = 0; i < size; i++) {
            Entry<?> entry = this.encoders.get(i);
            if (entry.handles(cls)) {
                return (ResourceEncoder<Z>) entry.encoder;
            }
        }
        return null;
    }

    private static final class Entry<T> {
        final ResourceEncoder<T> encoder;
        private final Class<T> resourceClass;

        Entry(Class<T> resourceClass, ResourceEncoder<T> encoder) {
            this.resourceClass = resourceClass;
            this.encoder = encoder;
        }

        boolean handles(Class<?> resourceClass) {
            return this.resourceClass.isAssignableFrom(resourceClass);
        }
    }
}

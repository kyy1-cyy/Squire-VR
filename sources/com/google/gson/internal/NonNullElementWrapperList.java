package com.google.gson.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

/* loaded from: classes.dex */
public class NonNullElementWrapperList<E> extends AbstractList<E> implements RandomAccess {
    private final ArrayList<E> delegate;

    public NonNullElementWrapperList(ArrayList<E> delegate) {
        this.delegate = (ArrayList) Objects.requireNonNull(delegate);
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        return this.delegate.get(index);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.delegate.size();
    }

    private E nonNull(E element) {
        if (element == null) {
            throw new NullPointerException("Element must be non-null");
        }
        return element;
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int index, E element) {
        return this.delegate.set(index, nonNull(element));
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        this.delegate.add(index, nonNull(element));
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int index) {
        return this.delegate.remove(index);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.delegate.clear();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.delegate.toArray(tArr);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        return this.delegate.hashCode();
    }
}

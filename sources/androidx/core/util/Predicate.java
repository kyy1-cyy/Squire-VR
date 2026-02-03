package androidx.core.util;

import java.util.Objects;

/* loaded from: classes.dex */
public interface Predicate<T> {
    boolean test(T t);

    default Predicate<T> and(final Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate() { // from class: androidx.core.util.Predicate$$ExternalSyntheticLambda3
            @Override // androidx.core.util.Predicate
            public final boolean test(Object obj) {
                return Predicate.lambda$and$0(this.f$0, other, obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$and$0(Predicate _this, Predicate other, Object t) {
        return _this.test(t) && other.test(t);
    }

    static /* synthetic */ boolean lambda$negate$1(Predicate _this, Object t) {
        return !_this.test(t);
    }

    default Predicate<T> negate() {
        return new Predicate() { // from class: androidx.core.util.Predicate$$ExternalSyntheticLambda4
            @Override // androidx.core.util.Predicate
            public final boolean test(Object obj) {
                return Predicate.lambda$negate$1(this.f$0, obj);
            }
        };
    }

    default Predicate<T> or(final Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate() { // from class: androidx.core.util.Predicate$$ExternalSyntheticLambda0
            @Override // androidx.core.util.Predicate
            public final boolean test(Object obj) {
                return Predicate.lambda$or$2(this.f$0, other, obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$or$2(Predicate _this, Predicate other, Object t) {
        return _this.test(t) || other.test(t);
    }

    static <T> Predicate<T> isEqual(final Object targetRef) {
        if (targetRef == null) {
            return new Predicate() { // from class: androidx.core.util.Predicate$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Predicate
                public final boolean test(Object obj) {
                    return Objects.isNull(obj);
                }
            };
        }
        return new Predicate() { // from class: androidx.core.util.Predicate$$ExternalSyntheticLambda2
            @Override // androidx.core.util.Predicate
            public final boolean test(Object obj) {
                return targetRef.equals(obj);
            }
        };
    }

    static <T> Predicate<T> not(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.negate();
    }
}

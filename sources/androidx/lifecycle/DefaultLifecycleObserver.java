package androidx.lifecycle;

/* loaded from: classes.dex */
public interface DefaultLifecycleObserver extends FullLifecycleObserver {
    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onCreate(LifecycleOwner owner) {
    }

    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onStart(LifecycleOwner owner) {
    }

    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onResume(LifecycleOwner owner) {
    }

    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onPause(LifecycleOwner owner) {
    }

    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onStop(LifecycleOwner owner) {
    }

    @Override // androidx.lifecycle.FullLifecycleObserver
    default void onDestroy(LifecycleOwner owner) {
    }
}

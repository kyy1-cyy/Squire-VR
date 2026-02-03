package androidx.core.view;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class MenuHostHelper {
    private final Runnable mOnInvalidateMenuCallback;
    private final CopyOnWriteArrayList<MenuProvider> mMenuProviders = new CopyOnWriteArrayList<>();
    private final Map<MenuProvider, LifecycleContainer> mProviderToLifecycleContainers = new HashMap();

    public MenuHostHelper(Runnable onInvalidateMenuCallback) {
        this.mOnInvalidateMenuCallback = onInvalidateMenuCallback;
    }

    public void onPrepareMenu(Menu menu) {
        Iterator<MenuProvider> it = this.mMenuProviders.iterator();
        while (it.hasNext()) {
            MenuProvider menuProvider = it.next();
            menuProvider.onPrepareMenu(menu);
        }
    }

    public void onCreateMenu(Menu menu, MenuInflater menuInflater) {
        Iterator<MenuProvider> it = this.mMenuProviders.iterator();
        while (it.hasNext()) {
            MenuProvider menuProvider = it.next();
            menuProvider.onCreateMenu(menu, menuInflater);
        }
    }

    public boolean onMenuItemSelected(MenuItem item) {
        Iterator<MenuProvider> it = this.mMenuProviders.iterator();
        while (it.hasNext()) {
            MenuProvider menuProvider = it.next();
            if (menuProvider.onMenuItemSelected(item)) {
                return true;
            }
        }
        return false;
    }

    public void onMenuClosed(Menu menu) {
        Iterator<MenuProvider> it = this.mMenuProviders.iterator();
        while (it.hasNext()) {
            MenuProvider menuProvider = it.next();
            menuProvider.onMenuClosed(menu);
        }
    }

    public void addMenuProvider(MenuProvider provider) {
        this.mMenuProviders.add(provider);
        this.mOnInvalidateMenuCallback.run();
    }

    public void addMenuProvider(final MenuProvider provider, LifecycleOwner owner) {
        addMenuProvider(provider);
        Lifecycle lifecycle = owner.getLifecycle();
        LifecycleContainer lifecycleContainer = this.mProviderToLifecycleContainers.remove(provider);
        if (lifecycleContainer != null) {
            lifecycleContainer.clearObservers();
        }
        LifecycleEventObserver observer = new LifecycleEventObserver() { // from class: androidx.core.view.MenuHostHelper$$ExternalSyntheticLambda0
            @Override // androidx.lifecycle.LifecycleEventObserver
            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                this.f$0.m33lambda$addMenuProvider$0$androidxcoreviewMenuHostHelper(provider, lifecycleOwner, event);
            }
        };
        this.mProviderToLifecycleContainers.put(provider, new LifecycleContainer(lifecycle, observer));
    }

    /* renamed from: lambda$addMenuProvider$0$androidx-core-view-MenuHostHelper, reason: not valid java name */
    /* synthetic */ void m33lambda$addMenuProvider$0$androidxcoreviewMenuHostHelper(MenuProvider provider, LifecycleOwner source, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            removeMenuProvider(provider);
        }
    }

    public void addMenuProvider(final MenuProvider provider, LifecycleOwner owner, final Lifecycle.State state) {
        Lifecycle lifecycle = owner.getLifecycle();
        LifecycleContainer lifecycleContainer = this.mProviderToLifecycleContainers.remove(provider);
        if (lifecycleContainer != null) {
            lifecycleContainer.clearObservers();
        }
        LifecycleEventObserver observer = new LifecycleEventObserver() { // from class: androidx.core.view.MenuHostHelper$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.LifecycleEventObserver
            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                this.f$0.m34lambda$addMenuProvider$1$androidxcoreviewMenuHostHelper(state, provider, lifecycleOwner, event);
            }
        };
        this.mProviderToLifecycleContainers.put(provider, new LifecycleContainer(lifecycle, observer));
    }

    /* renamed from: lambda$addMenuProvider$1$androidx-core-view-MenuHostHelper, reason: not valid java name */
    /* synthetic */ void m34lambda$addMenuProvider$1$androidxcoreviewMenuHostHelper(Lifecycle.State state, MenuProvider provider, LifecycleOwner source, Lifecycle.Event event) {
        if (event == Lifecycle.Event.upTo(state)) {
            addMenuProvider(provider);
            return;
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            removeMenuProvider(provider);
        } else if (event == Lifecycle.Event.downFrom(state)) {
            this.mMenuProviders.remove(provider);
            this.mOnInvalidateMenuCallback.run();
        }
    }

    public void removeMenuProvider(MenuProvider provider) {
        this.mMenuProviders.remove(provider);
        LifecycleContainer lifecycleContainer = this.mProviderToLifecycleContainers.remove(provider);
        if (lifecycleContainer != null) {
            lifecycleContainer.clearObservers();
        }
        this.mOnInvalidateMenuCallback.run();
    }

    private static class LifecycleContainer {
        final Lifecycle mLifecycle;
        private LifecycleEventObserver mObserver;

        LifecycleContainer(Lifecycle lifecycle, LifecycleEventObserver observer) {
            this.mLifecycle = lifecycle;
            this.mObserver = observer;
            this.mLifecycle.addObserver(observer);
        }

        void clearObservers() {
            this.mLifecycle.removeObserver(this.mObserver);
            this.mObserver = null;
        }
    }
}

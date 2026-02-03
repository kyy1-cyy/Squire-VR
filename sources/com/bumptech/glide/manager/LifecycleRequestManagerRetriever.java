package com.bumptech.glide.manager;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
final class LifecycleRequestManagerRetriever {
    private final RequestManagerRetriever.RequestManagerFactory factory;
    final Map<androidx.lifecycle.Lifecycle, RequestManager> lifecycleToRequestManager = new HashMap();

    LifecycleRequestManagerRetriever(RequestManagerRetriever.RequestManagerFactory factory) {
        this.factory = factory;
    }

    RequestManager getOnly(androidx.lifecycle.Lifecycle lifecycle) {
        Util.assertMainThread();
        return this.lifecycleToRequestManager.get(lifecycle);
    }

    RequestManager getOrCreate(Context context, Glide glide, final androidx.lifecycle.Lifecycle lifecycle, FragmentManager childFragmentManager, boolean isParentVisible) {
        Util.assertMainThread();
        RequestManager result = getOnly(lifecycle);
        if (result == null) {
            LifecycleLifecycle glideLifecycle = new LifecycleLifecycle(lifecycle);
            result = this.factory.build(glide, glideLifecycle, new SupportRequestManagerTreeNode(childFragmentManager), context);
            this.lifecycleToRequestManager.put(lifecycle, result);
            glideLifecycle.addListener(new LifecycleListener() { // from class: com.bumptech.glide.manager.LifecycleRequestManagerRetriever.1
                @Override // com.bumptech.glide.manager.LifecycleListener
                public void onStart() {
                }

                @Override // com.bumptech.glide.manager.LifecycleListener
                public void onStop() {
                }

                @Override // com.bumptech.glide.manager.LifecycleListener
                public void onDestroy() {
                    LifecycleRequestManagerRetriever.this.lifecycleToRequestManager.remove(lifecycle);
                }
            });
            if (isParentVisible) {
                result.onStart();
            }
        }
        return result;
    }

    private final class SupportRequestManagerTreeNode implements RequestManagerTreeNode {
        private final FragmentManager childFragmentManager;

        SupportRequestManagerTreeNode(FragmentManager childFragmentManager) {
            this.childFragmentManager = childFragmentManager;
        }

        @Override // com.bumptech.glide.manager.RequestManagerTreeNode
        public Set<RequestManager> getDescendants() {
            Set<RequestManager> result = new HashSet<>();
            getChildFragmentsRecursive(this.childFragmentManager, result);
            return result;
        }

        private void getChildFragmentsRecursive(FragmentManager fragmentManager, Set<RequestManager> requestManagers) {
            List<Fragment> children = fragmentManager.getFragments();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                Fragment child = children.get(i);
                getChildFragmentsRecursive(child.getChildFragmentManager(), requestManagers);
                RequestManager fromChild = LifecycleRequestManagerRetriever.this.getOnly(child.getLifecycle());
                if (fromChild != null) {
                    requestManagers.add(fromChild);
                }
            }
        }
    }
}

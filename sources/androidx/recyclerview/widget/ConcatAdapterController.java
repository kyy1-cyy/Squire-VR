package androidx.recyclerview.widget;

import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.NestedAdapterWrapper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StableIdStorage;
import androidx.recyclerview.widget.ViewTypeStorage;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
class ConcatAdapterController implements NestedAdapterWrapper.Callback {
    private final ConcatAdapter mConcatAdapter;
    private final ConcatAdapter.Config.StableIdMode mStableIdMode;
    private final StableIdStorage mStableIdStorage;
    private final ViewTypeStorage mViewTypeStorage;
    private List<WeakReference<RecyclerView>> mAttachedRecyclerViews = new ArrayList();
    private final IdentityHashMap<RecyclerView.ViewHolder, NestedAdapterWrapper> mBinderLookup = new IdentityHashMap<>();
    private List<NestedAdapterWrapper> mWrappers = new ArrayList();
    private WrapperAndLocalPosition mReusableHolder = new WrapperAndLocalPosition();

    ConcatAdapterController(ConcatAdapter concatAdapter, ConcatAdapter.Config config) {
        this.mConcatAdapter = concatAdapter;
        if (config.isolateViewTypes) {
            this.mViewTypeStorage = new ViewTypeStorage.IsolatedViewTypeStorage();
        } else {
            this.mViewTypeStorage = new ViewTypeStorage.SharedIdRangeViewTypeStorage();
        }
        this.mStableIdMode = config.stableIdMode;
        if (config.stableIdMode == ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS) {
            this.mStableIdStorage = new StableIdStorage.NoStableIdStorage();
        } else if (config.stableIdMode == ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS) {
            this.mStableIdStorage = new StableIdStorage.IsolatedStableIdStorage();
        } else {
            if (config.stableIdMode == ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS) {
                this.mStableIdStorage = new StableIdStorage.SharedPoolStableIdStorage();
                return;
            }
            throw new IllegalArgumentException("unknown stable id mode");
        }
    }

    private NestedAdapterWrapper findWrapperFor(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        int index = indexOfWrapper(adapter);
        if (index == -1) {
            return null;
        }
        return this.mWrappers.get(index);
    }

    private int indexOfWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        int limit = this.mWrappers.size();
        for (int i = 0; i < limit; i++) {
            if (this.mWrappers.get(i).adapter == adapter) {
                return i;
            }
        }
        return -1;
    }

    boolean addAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        return addAdapter(this.mWrappers.size(), adapter);
    }

    boolean addAdapter(int index, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        if (index < 0 || index > this.mWrappers.size()) {
            throw new IndexOutOfBoundsException("Index must be between 0 and " + this.mWrappers.size() + ". Given:" + index);
        }
        if (hasStableIds()) {
            Preconditions.checkArgument(adapter.hasStableIds(), "All sub adapters must have stable ids when stable id mode is ISOLATED_STABLE_IDS or SHARED_STABLE_IDS");
        } else if (adapter.hasStableIds()) {
            Log.w("ConcatAdapter", "Stable ids in the adapter will be ignored as the ConcatAdapter is configured not to have stable ids");
        }
        NestedAdapterWrapper existing = findWrapperFor(adapter);
        if (existing != null) {
            return false;
        }
        NestedAdapterWrapper wrapper = new NestedAdapterWrapper(adapter, this, this.mViewTypeStorage, this.mStableIdStorage.createStableIdLookup());
        this.mWrappers.add(index, wrapper);
        for (WeakReference<RecyclerView> reference : this.mAttachedRecyclerViews) {
            RecyclerView recyclerView = reference.get();
            if (recyclerView != null) {
                adapter.onAttachedToRecyclerView(recyclerView);
            }
        }
        if (wrapper.getCachedItemCount() > 0) {
            this.mConcatAdapter.notifyItemRangeInserted(countItemsBefore(wrapper), wrapper.getCachedItemCount());
        }
        calculateAndUpdateStateRestorationPolicy();
        return true;
    }

    boolean removeAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        int index = indexOfWrapper(adapter);
        if (index == -1) {
            return false;
        }
        NestedAdapterWrapper wrapper = this.mWrappers.get(index);
        int offset = countItemsBefore(wrapper);
        this.mWrappers.remove(index);
        this.mConcatAdapter.notifyItemRangeRemoved(offset, wrapper.getCachedItemCount());
        for (WeakReference<RecyclerView> reference : this.mAttachedRecyclerViews) {
            RecyclerView recyclerView = reference.get();
            if (recyclerView != null) {
                adapter.onDetachedFromRecyclerView(recyclerView);
            }
        }
        wrapper.dispose();
        calculateAndUpdateStateRestorationPolicy();
        return true;
    }

    private int countItemsBefore(NestedAdapterWrapper wrapper) {
        NestedAdapterWrapper item;
        int count = 0;
        Iterator<NestedAdapterWrapper> it = this.mWrappers.iterator();
        while (it.hasNext() && (item = it.next()) != wrapper) {
            count += item.getCachedItemCount();
        }
        return count;
    }

    public long getItemId(int globalPosition) {
        WrapperAndLocalPosition wrapperAndPos = findWrapperAndLocalPosition(globalPosition);
        long globalItemId = wrapperAndPos.mWrapper.getItemId(wrapperAndPos.mLocalPosition);
        releaseWrapperAndLocalPosition(wrapperAndPos);
        return globalItemId;
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onChanged(NestedAdapterWrapper wrapper) {
        this.mConcatAdapter.notifyDataSetChanged();
        calculateAndUpdateStateRestorationPolicy();
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onItemRangeChanged(NestedAdapterWrapper nestedAdapterWrapper, int positionStart, int itemCount) {
        int offset = countItemsBefore(nestedAdapterWrapper);
        this.mConcatAdapter.notifyItemRangeChanged(positionStart + offset, itemCount);
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onItemRangeChanged(NestedAdapterWrapper nestedAdapterWrapper, int positionStart, int itemCount, Object payload) {
        int offset = countItemsBefore(nestedAdapterWrapper);
        this.mConcatAdapter.notifyItemRangeChanged(positionStart + offset, itemCount, payload);
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onItemRangeInserted(NestedAdapterWrapper nestedAdapterWrapper, int positionStart, int itemCount) {
        int offset = countItemsBefore(nestedAdapterWrapper);
        this.mConcatAdapter.notifyItemRangeInserted(positionStart + offset, itemCount);
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onItemRangeRemoved(NestedAdapterWrapper nestedAdapterWrapper, int positionStart, int itemCount) {
        int offset = countItemsBefore(nestedAdapterWrapper);
        this.mConcatAdapter.notifyItemRangeRemoved(positionStart + offset, itemCount);
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onItemRangeMoved(NestedAdapterWrapper nestedAdapterWrapper, int fromPosition, int toPosition) {
        int offset = countItemsBefore(nestedAdapterWrapper);
        this.mConcatAdapter.notifyItemMoved(fromPosition + offset, toPosition + offset);
    }

    @Override // androidx.recyclerview.widget.NestedAdapterWrapper.Callback
    public void onStateRestorationPolicyChanged(NestedAdapterWrapper nestedAdapterWrapper) {
        calculateAndUpdateStateRestorationPolicy();
    }

    private void calculateAndUpdateStateRestorationPolicy() {
        RecyclerView.Adapter.StateRestorationPolicy newPolicy = computeStateRestorationPolicy();
        if (newPolicy != this.mConcatAdapter.getStateRestorationPolicy()) {
            this.mConcatAdapter.internalSetStateRestorationPolicy(newPolicy);
        }
    }

    private RecyclerView.Adapter.StateRestorationPolicy computeStateRestorationPolicy() {
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            RecyclerView.Adapter.StateRestorationPolicy strategy = wrapper.adapter.getStateRestorationPolicy();
            if (strategy == RecyclerView.Adapter.StateRestorationPolicy.PREVENT) {
                return RecyclerView.Adapter.StateRestorationPolicy.PREVENT;
            }
            if (strategy == RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY && wrapper.getCachedItemCount() == 0) {
                return RecyclerView.Adapter.StateRestorationPolicy.PREVENT;
            }
        }
        return RecyclerView.Adapter.StateRestorationPolicy.ALLOW;
    }

    public int getTotalCount() {
        int total = 0;
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            total += wrapper.getCachedItemCount();
        }
        return total;
    }

    public int getItemViewType(int globalPosition) {
        WrapperAndLocalPosition wrapperAndPos = findWrapperAndLocalPosition(globalPosition);
        int itemViewType = wrapperAndPos.mWrapper.getItemViewType(wrapperAndPos.mLocalPosition);
        releaseWrapperAndLocalPosition(wrapperAndPos);
        return itemViewType;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int globalViewType) {
        NestedAdapterWrapper wrapper = this.mViewTypeStorage.getWrapperForGlobalType(globalViewType);
        return wrapper.onCreateViewHolder(parent, globalViewType);
    }

    public Pair<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>, Integer> getWrappedAdapterAndPosition(int globalPosition) {
        WrapperAndLocalPosition wrapper = findWrapperAndLocalPosition(globalPosition);
        Pair<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>, Integer> pair = new Pair<>(wrapper.mWrapper.adapter, Integer.valueOf(wrapper.mLocalPosition));
        releaseWrapperAndLocalPosition(wrapper);
        return pair;
    }

    private WrapperAndLocalPosition findWrapperAndLocalPosition(int globalPosition) {
        WrapperAndLocalPosition result;
        if (this.mReusableHolder.mInUse) {
            result = new WrapperAndLocalPosition();
        } else {
            WrapperAndLocalPosition result2 = this.mReusableHolder;
            result2.mInUse = true;
            result = this.mReusableHolder;
        }
        int localPosition = globalPosition;
        Iterator<NestedAdapterWrapper> it = this.mWrappers.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            NestedAdapterWrapper wrapper = it.next();
            if (wrapper.getCachedItemCount() > localPosition) {
                result.mWrapper = wrapper;
                result.mLocalPosition = localPosition;
                break;
            }
            localPosition -= wrapper.getCachedItemCount();
        }
        if (result.mWrapper == null) {
            throw new IllegalArgumentException("Cannot find wrapper for " + globalPosition);
        }
        return result;
    }

    private void releaseWrapperAndLocalPosition(WrapperAndLocalPosition wrapperAndLocalPosition) {
        wrapperAndLocalPosition.mInUse = false;
        wrapperAndLocalPosition.mWrapper = null;
        wrapperAndLocalPosition.mLocalPosition = -1;
        this.mReusableHolder = wrapperAndLocalPosition;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int globalPosition) {
        WrapperAndLocalPosition wrapperAndPos = findWrapperAndLocalPosition(globalPosition);
        this.mBinderLookup.put(holder, wrapperAndPos.mWrapper);
        wrapperAndPos.mWrapper.onBindViewHolder(holder, wrapperAndPos.mLocalPosition);
        releaseWrapperAndLocalPosition(wrapperAndPos);
    }

    public boolean canRestoreState() {
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            if (!wrapper.adapter.canRestoreState()) {
                return false;
            }
        }
        return true;
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        NestedAdapterWrapper wrapper = getWrapper(holder);
        wrapper.adapter.onViewAttachedToWindow(holder);
    }

    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        NestedAdapterWrapper wrapper = getWrapper(holder);
        wrapper.adapter.onViewDetachedFromWindow(holder);
    }

    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        NestedAdapterWrapper wrapper = this.mBinderLookup.get(holder);
        if (wrapper == null) {
            throw new IllegalStateException("Cannot find wrapper for " + holder + ", seems like it is not bound by this adapter: " + this);
        }
        wrapper.adapter.onViewRecycled(holder);
        this.mBinderLookup.remove(holder);
    }

    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        NestedAdapterWrapper wrapper = this.mBinderLookup.get(holder);
        if (wrapper == null) {
            throw new IllegalStateException("Cannot find wrapper for " + holder + ", seems like it is not bound by this adapter: " + this);
        }
        boolean result = wrapper.adapter.onFailedToRecycleView(holder);
        this.mBinderLookup.remove(holder);
        return result;
    }

    private NestedAdapterWrapper getWrapper(RecyclerView.ViewHolder holder) {
        NestedAdapterWrapper wrapper = this.mBinderLookup.get(holder);
        if (wrapper == null) {
            throw new IllegalStateException("Cannot find wrapper for " + holder + ", seems like it is not bound by this adapter: " + this);
        }
        return wrapper;
    }

    private boolean isAttachedTo(RecyclerView recyclerView) {
        for (WeakReference<RecyclerView> reference : this.mAttachedRecyclerViews) {
            if (reference.get() == recyclerView) {
                return true;
            }
        }
        return false;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (isAttachedTo(recyclerView)) {
            return;
        }
        this.mAttachedRecyclerViews.add(new WeakReference<>(recyclerView));
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            wrapper.adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        int i = this.mAttachedRecyclerViews.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            WeakReference<RecyclerView> reference = this.mAttachedRecyclerViews.get(i);
            if (reference.get() == null) {
                this.mAttachedRecyclerViews.remove(i);
            } else if (reference.get() == recyclerView) {
                this.mAttachedRecyclerViews.remove(i);
                break;
            }
            i--;
        }
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            wrapper.adapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    public int getLocalAdapterPosition(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder viewHolder, int globalPosition) {
        NestedAdapterWrapper wrapper = this.mBinderLookup.get(viewHolder);
        if (wrapper == null) {
            return -1;
        }
        int itemsBefore = countItemsBefore(wrapper);
        int localPosition = globalPosition - itemsBefore;
        int itemCount = wrapper.adapter.getItemCount();
        if (localPosition < 0 || localPosition >= itemCount) {
            throw new IllegalStateException("Detected inconsistent adapter updates. The local position of the view holder maps to " + localPosition + " which is out of bounds for the adapter with size " + itemCount + ".Make sure to immediately call notify methods in your adapter when you change the backing dataviewHolder:" + viewHolder + "adapter:" + adapter);
        }
        return wrapper.adapter.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition);
    }

    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getBoundAdapter(RecyclerView.ViewHolder viewHolder) {
        NestedAdapterWrapper wrapper = this.mBinderLookup.get(viewHolder);
        if (wrapper == null) {
            return null;
        }
        return wrapper.adapter;
    }

    public List<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> getCopyOfAdapters() {
        if (this.mWrappers.isEmpty()) {
            return Collections.emptyList();
        }
        List<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> adapters = new ArrayList<>(this.mWrappers.size());
        for (NestedAdapterWrapper wrapper : this.mWrappers) {
            adapters.add(wrapper.adapter);
        }
        return adapters;
    }

    public boolean hasStableIds() {
        return this.mStableIdMode != ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS;
    }

    static class WrapperAndLocalPosition {
        boolean mInUse;
        int mLocalPosition;
        NestedAdapterWrapper mWrapper;

        WrapperAndLocalPosition() {
        }
    }
}

package androidx.recyclerview.widget;

/* loaded from: classes.dex */
public class BatchingListUpdateCallback implements ListUpdateCallback {
    private static final int TYPE_ADD = 1;
    private static final int TYPE_CHANGE = 3;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_REMOVE = 2;
    final ListUpdateCallback mWrapped;
    int mLastEventType = 0;
    int mLastEventPosition = -1;
    int mLastEventCount = -1;
    Object mLastEventPayload = null;

    public BatchingListUpdateCallback(ListUpdateCallback callback) {
        this.mWrapped = callback;
    }

    public void dispatchLastEvent() {
        if (this.mLastEventType == 0) {
            return;
        }
        switch (this.mLastEventType) {
            case 1:
                this.mWrapped.onInserted(this.mLastEventPosition, this.mLastEventCount);
                break;
            case 2:
                this.mWrapped.onRemoved(this.mLastEventPosition, this.mLastEventCount);
                break;
            case 3:
                this.mWrapped.onChanged(this.mLastEventPosition, this.mLastEventCount, this.mLastEventPayload);
                break;
        }
        this.mLastEventPayload = null;
        this.mLastEventType = 0;
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onInserted(int position, int count) {
        if (this.mLastEventType == 1 && position >= this.mLastEventPosition && position <= this.mLastEventPosition + this.mLastEventCount) {
            this.mLastEventCount += count;
            this.mLastEventPosition = Math.min(position, this.mLastEventPosition);
        } else {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventType = 1;
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onRemoved(int position, int count) {
        if (this.mLastEventType == 2 && this.mLastEventPosition >= position && this.mLastEventPosition <= position + count) {
            this.mLastEventCount += count;
            this.mLastEventPosition = position;
        } else {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventType = 2;
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onMoved(int fromPosition, int toPosition) {
        dispatchLastEvent();
        this.mWrapped.onMoved(fromPosition, toPosition);
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onChanged(int position, int count, Object payload) {
        if (this.mLastEventType == 3 && position <= this.mLastEventPosition + this.mLastEventCount && position + count >= this.mLastEventPosition && this.mLastEventPayload == payload) {
            int previousEnd = this.mLastEventPosition + this.mLastEventCount;
            this.mLastEventPosition = Math.min(position, this.mLastEventPosition);
            this.mLastEventCount = Math.max(previousEnd, position + count) - this.mLastEventPosition;
        } else {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventPayload = payload;
            this.mLastEventType = 3;
        }
    }
}

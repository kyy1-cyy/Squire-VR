package androidx.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class DiffUtil {
    private static final Comparator<Diagonal> DIAGONAL_COMPARATOR = new Comparator<Diagonal>() { // from class: androidx.recyclerview.widget.DiffUtil.1
        @Override // java.util.Comparator
        public int compare(Diagonal o1, Diagonal o2) {
            return o1.x - o2.x;
        }
    };

    private DiffUtil() {
    }

    public static DiffResult calculateDiff(Callback cb) {
        return calculateDiff(cb, true);
    }

    public static DiffResult calculateDiff(Callback cb, boolean detectMoves) {
        int oldSize = cb.getOldListSize();
        int newSize = cb.getNewListSize();
        List<Diagonal> diagonals = new ArrayList<>();
        List<Range> stack = new ArrayList<>();
        stack.add(new Range(0, oldSize, 0, newSize));
        int max = ((oldSize + newSize) + 1) / 2;
        CenteredArray forward = new CenteredArray((max * 2) + 1);
        CenteredArray backward = new CenteredArray((max * 2) + 1);
        List<Range> rangePool = new ArrayList<>();
        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);
            Snake snake = midPoint(range, cb, forward, backward);
            if (snake != null) {
                if (snake.diagonalSize() > 0) {
                    diagonals.add(snake.toDiagonal());
                }
                Range left = rangePool.isEmpty() ? new Range() : rangePool.remove(rangePool.size() - 1);
                left.oldListStart = range.oldListStart;
                left.newListStart = range.newListStart;
                left.oldListEnd = snake.startX;
                left.newListEnd = snake.startY;
                stack.add(left);
                range.oldListEnd = range.oldListEnd;
                range.newListEnd = range.newListEnd;
                range.oldListStart = snake.endX;
                range.newListStart = snake.endY;
                stack.add(range);
            } else {
                rangePool.add(range);
            }
        }
        Collections.sort(diagonals, DIAGONAL_COMPARATOR);
        return new DiffResult(cb, diagonals, forward.backingData(), backward.backingData(), detectMoves);
    }

    private static Snake midPoint(Range range, Callback cb, CenteredArray forward, CenteredArray backward) {
        if (range.oldSize() < 1 || range.newSize() < 1) {
            return null;
        }
        int max = ((range.oldSize() + range.newSize()) + 1) / 2;
        forward.set(1, range.oldListStart);
        backward.set(1, range.oldListEnd);
        for (int d = 0; d < max; d++) {
            Snake snake = forward(range, cb, forward, backward, d);
            if (snake != null) {
                return snake;
            }
            Snake snake2 = backward(range, cb, forward, backward, d);
            if (snake2 != null) {
                return snake2;
            }
        }
        return null;
    }

    private static Snake forward(Range range, Callback cb, CenteredArray forward, CenteredArray backward, int d) {
        int startX;
        int startX2;
        boolean checkForSnake = Math.abs(range.oldSize() - range.newSize()) % 2 == 1;
        int delta = range.oldSize() - range.newSize();
        for (int k = -d; k <= d; k += 2) {
            if (k == (-d) || (k != d && forward.get(k + 1) > forward.get(k - 1))) {
                startX = forward.get(k + 1);
                startX2 = startX;
            } else {
                startX2 = forward.get(k - 1);
                startX = startX2 + 1;
            }
            int y = (range.newListStart + (startX - range.oldListStart)) - k;
            int startY = (d == 0 || startX != startX2) ? y : y - 1;
            while (startX < range.oldListEnd && y < range.newListEnd) {
                if (!cb.areItemsTheSame(startX, y)) {
                    break;
                }
                startX++;
                y++;
            }
            forward.set(k, startX);
            if (checkForSnake) {
                int backwardsK = delta - k;
                if (backwardsK >= (-d) + 1 && backwardsK <= d - 1) {
                    if (backward.get(backwardsK) <= startX) {
                        Snake snake = new Snake();
                        snake.startX = startX2;
                        snake.startY = startY;
                        snake.endX = startX;
                        snake.endY = y;
                        snake.reverse = false;
                        return snake;
                    }
                }
            }
        }
        return null;
    }

    private static Snake backward(Range range, Callback cb, CenteredArray forward, CenteredArray backward, int d) {
        int startX;
        int startX2;
        int forwardsK;
        boolean checkForSnake = (range.oldSize() - range.newSize()) % 2 == 0;
        int delta = range.oldSize() - range.newSize();
        for (int k = -d; k <= d; k += 2) {
            if (k == (-d) || (k != d && backward.get(k + 1) < backward.get(k - 1))) {
                startX = backward.get(k + 1);
                startX2 = startX;
            } else {
                startX2 = backward.get(k - 1);
                startX = startX2 - 1;
            }
            int y = range.newListEnd - ((range.oldListEnd - startX) - k);
            int startY = (d == 0 || startX != startX2) ? y : y + 1;
            while (startX > range.oldListStart && y > range.newListStart && cb.areItemsTheSame(startX - 1, y - 1)) {
                startX--;
                y--;
            }
            backward.set(k, startX);
            if (checkForSnake && (forwardsK = delta - k) >= (-d) && forwardsK <= d && forward.get(forwardsK) >= startX) {
                Snake snake = new Snake();
                snake.startX = startX;
                snake.startY = y;
                snake.endX = startX2;
                snake.endY = startY;
                snake.reverse = true;
                return snake;
            }
        }
        return null;
    }

    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public abstract int getNewListSize();

        public abstract int getOldListSize();

        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }
    }

    public static abstract class ItemCallback<T> {
        public abstract boolean areContentsTheSame(T t, T t2);

        public abstract boolean areItemsTheSame(T t, T t2);

        public Object getChangePayload(T oldItem, T newItem) {
            return null;
        }
    }

    static class Diagonal {
        public final int size;
        public final int x;
        public final int y;

        Diagonal(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        int endX() {
            return this.x + this.size;
        }

        int endY() {
            return this.y + this.size;
        }
    }

    static class Snake {
        public int endX;
        public int endY;
        public boolean reverse;
        public int startX;
        public int startY;

        Snake() {
        }

        boolean hasAdditionOrRemoval() {
            return this.endY - this.startY != this.endX - this.startX;
        }

        boolean isAddition() {
            return this.endY - this.startY > this.endX - this.startX;
        }

        int diagonalSize() {
            return Math.min(this.endX - this.startX, this.endY - this.startY);
        }

        Diagonal toDiagonal() {
            if (hasAdditionOrRemoval()) {
                if (this.reverse) {
                    return new Diagonal(this.startX, this.startY, diagonalSize());
                }
                if (isAddition()) {
                    return new Diagonal(this.startX, this.startY + 1, diagonalSize());
                }
                return new Diagonal(this.startX + 1, this.startY, diagonalSize());
            }
            return new Diagonal(this.startX, this.startY, this.endX - this.startX);
        }
    }

    static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range() {
        }

        public Range(int oldListStart, int oldListEnd, int newListStart, int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }

        int oldSize() {
            return this.oldListEnd - this.oldListStart;
        }

        int newSize() {
            return this.newListEnd - this.newListStart;
        }
    }

    public static class DiffResult {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_MASK = 15;
        private static final int FLAG_MOVED = 12;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 4;
        public static final int NO_POSITION = -1;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final List<Diagonal> mDiagonals;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;

        DiffResult(Callback callback, List<Diagonal> diagonals, int[] oldItemStatuses, int[] newItemStatuses, boolean detectMoves) {
            this.mDiagonals = diagonals;
            this.mOldItemStatuses = oldItemStatuses;
            this.mNewItemStatuses = newItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = detectMoves;
            addEdgeDiagonals();
            findMatchingItems();
        }

        private void addEdgeDiagonals() {
            Diagonal first = this.mDiagonals.isEmpty() ? null : this.mDiagonals.get(0);
            if (first == null || first.x != 0 || first.y != 0) {
                this.mDiagonals.add(0, new Diagonal(0, 0, 0));
            }
            this.mDiagonals.add(new Diagonal(this.mOldListSize, this.mNewListSize, 0));
        }

        private void findMatchingItems() {
            for (Diagonal diagonal : this.mDiagonals) {
                for (int offset = 0; offset < diagonal.size; offset++) {
                    int posX = diagonal.x + offset;
                    int posY = diagonal.y + offset;
                    boolean theSame = this.mCallback.areContentsTheSame(posX, posY);
                    int changeFlag = theSame ? 1 : 2;
                    this.mOldItemStatuses[posX] = (posY << 4) | changeFlag;
                    this.mNewItemStatuses[posY] = (posX << 4) | changeFlag;
                }
            }
            if (this.mDetectMoves) {
                findMoveMatches();
            }
        }

        private void findMoveMatches() {
            int posX = 0;
            for (Diagonal diagonal : this.mDiagonals) {
                while (posX < diagonal.x) {
                    if (this.mOldItemStatuses[posX] == 0) {
                        findMatchingAddition(posX);
                    }
                    posX++;
                }
                posX = diagonal.endX();
            }
        }

        private void findMatchingAddition(int posX) {
            int posY = 0;
            int diagonalsSize = this.mDiagonals.size();
            for (int i = 0; i < diagonalsSize; i++) {
                Diagonal diagonal = this.mDiagonals.get(i);
                while (posY < diagonal.y) {
                    if (this.mNewItemStatuses[posY] == 0) {
                        boolean matching = this.mCallback.areItemsTheSame(posX, posY);
                        if (matching) {
                            boolean contentsMatching = this.mCallback.areContentsTheSame(posX, posY);
                            int changeFlag = contentsMatching ? 8 : 4;
                            this.mOldItemStatuses[posX] = (posY << 4) | changeFlag;
                            this.mNewItemStatuses[posY] = (posX << 4) | changeFlag;
                            return;
                        }
                    }
                    posY++;
                }
                posY = diagonal.endY();
            }
        }

        public int convertOldPositionToNew(int oldListPosition) {
            if (oldListPosition < 0 || oldListPosition >= this.mOldListSize) {
                throw new IndexOutOfBoundsException("Index out of bounds - passed position = " + oldListPosition + ", old list size = " + this.mOldListSize);
            }
            int status = this.mOldItemStatuses[oldListPosition];
            if ((status & 15) == 0) {
                return -1;
            }
            return status >> 4;
        }

        public int convertNewPositionToOld(int newListPosition) {
            if (newListPosition < 0 || newListPosition >= this.mNewListSize) {
                throw new IndexOutOfBoundsException("Index out of bounds - passed position = " + newListPosition + ", new list size = " + this.mNewListSize);
            }
            int status = this.mNewItemStatuses[newListPosition];
            if ((status & 15) == 0) {
                return -1;
            }
            return status >> 4;
        }

        public void dispatchUpdatesTo(RecyclerView.Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void dispatchUpdatesTo(ListUpdateCallback listUpdateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            boolean z;
            ListUpdateCallback listUpdateCallback2;
            int i;
            ListUpdateCallback listUpdateCallback3 = listUpdateCallback;
            if (listUpdateCallback3 instanceof BatchingListUpdateCallback) {
                batchingListUpdateCallback = (BatchingListUpdateCallback) listUpdateCallback3;
            } else {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback3);
                listUpdateCallback3 = batchingListUpdateCallback;
            }
            int i2 = this.mOldListSize;
            ArrayDeque arrayDeque = new ArrayDeque();
            int i3 = this.mOldListSize;
            int i4 = this.mNewListSize;
            int i5 = 1;
            int size = this.mDiagonals.size() - 1;
            while (size >= 0) {
                Diagonal diagonal = this.mDiagonals.get(size);
                int iEndX = diagonal.endX();
                int iEndY = diagonal.endY();
                while (true) {
                    z = false;
                    if (i3 <= iEndX) {
                        break;
                    }
                    i3--;
                    int i6 = this.mOldItemStatuses[i3];
                    if ((i6 & 12) != 0) {
                        int i7 = i6 >> 4;
                        PostponedUpdate postponedUpdate = getPostponedUpdate(arrayDeque, i7, false);
                        if (postponedUpdate != null) {
                            int i8 = i2 - postponedUpdate.currentPos;
                            batchingListUpdateCallback.onMoved(i3, i8 - 1);
                            if ((i6 & 4) == 0) {
                                listUpdateCallback2 = listUpdateCallback3;
                                i = i4;
                            } else {
                                listUpdateCallback2 = listUpdateCallback3;
                                i = i4;
                                batchingListUpdateCallback.onChanged(i8 - 1, 1, this.mCallback.getChangePayload(i3, i7));
                            }
                        } else {
                            listUpdateCallback2 = listUpdateCallback3;
                            i = i4;
                            boolean z2 = i5;
                            arrayDeque.add(new PostponedUpdate(i3, (i2 - i3) - (z2 ? 1 : 0), z2));
                        }
                    } else {
                        listUpdateCallback2 = listUpdateCallback3;
                        i = i4;
                        batchingListUpdateCallback.onRemoved(i3, i5);
                        i2--;
                    }
                    listUpdateCallback3 = listUpdateCallback2;
                    i4 = i;
                    i5 = 1;
                }
                ListUpdateCallback listUpdateCallback4 = listUpdateCallback3;
                while (i4 > iEndY) {
                    i4--;
                    int i9 = this.mNewItemStatuses[i4];
                    if ((i9 & 12) != 0) {
                        int i10 = i9 >> 4;
                        PostponedUpdate postponedUpdate2 = getPostponedUpdate(arrayDeque, i10, true);
                        if (postponedUpdate2 == null) {
                            arrayDeque.add(new PostponedUpdate(i4, i2 - i3, z));
                        } else {
                            batchingListUpdateCallback.onMoved((i2 - postponedUpdate2.currentPos) - 1, i3);
                            if ((i9 & 4) != 0) {
                                batchingListUpdateCallback.onChanged(i3, 1, this.mCallback.getChangePayload(i10, i4));
                            }
                        }
                    } else {
                        batchingListUpdateCallback.onInserted(i3, 1);
                        i2++;
                    }
                    z = false;
                }
                int i11 = diagonal.x;
                int i12 = diagonal.y;
                for (int i13 = 0; i13 < diagonal.size; i13++) {
                    if ((this.mOldItemStatuses[i11] & 15) == 2) {
                        batchingListUpdateCallback.onChanged(i11, 1, this.mCallback.getChangePayload(i11, i12));
                    }
                    i11++;
                    i12++;
                }
                int i14 = diagonal.x;
                i4 = diagonal.y;
                size--;
                i3 = i14;
                i5 = 1;
                listUpdateCallback3 = listUpdateCallback4;
            }
            batchingListUpdateCallback.dispatchLastEvent();
        }

        private static PostponedUpdate getPostponedUpdate(Collection<PostponedUpdate> postponedUpdates, int posInList, boolean removal) {
            PostponedUpdate postponedUpdate = null;
            Iterator<PostponedUpdate> itr = postponedUpdates.iterator();
            while (true) {
                if (!itr.hasNext()) {
                    break;
                }
                PostponedUpdate update = itr.next();
                if (update.posInOwnerList == posInList && update.removal == removal) {
                    postponedUpdate = update;
                    itr.remove();
                    break;
                }
            }
            while (itr.hasNext()) {
                PostponedUpdate update2 = itr.next();
                if (removal) {
                    update2.currentPos--;
                } else {
                    update2.currentPos++;
                }
            }
            return postponedUpdate;
        }
    }

    private static class PostponedUpdate {
        int currentPos;
        int posInOwnerList;
        boolean removal;

        PostponedUpdate(int posInOwnerList, int currentPos, boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }

    static class CenteredArray {
        private final int[] mData;
        private final int mMid;

        CenteredArray(int size) {
            this.mData = new int[size];
            this.mMid = this.mData.length / 2;
        }

        int get(int index) {
            return this.mData[this.mMid + index];
        }

        int[] backingData() {
            return this.mData;
        }

        void set(int index, int value) {
            this.mData[this.mMid + index] = value;
        }

        public void fill(int value) {
            Arrays.fill(this.mData, value);
        }
    }
}

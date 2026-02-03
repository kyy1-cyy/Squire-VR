package okio;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;

/* compiled from: Options.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\b\u0018\u0000 \u00152\b\u0012\u0004\u0012\u00020\u00020\u00012\u00060\u0003j\u0002`\u0004:\u0001\u0015B\u001f\b\u0002\u0012\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0011\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u000eH\u0096\u0002R\u001e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006X\u0080\u0004¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0007\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u0016"}, d2 = {"Lokio/Options;", "Lkotlin/collections/AbstractList;", "Lokio/ByteString;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "byteStrings", HttpUrl.FRAGMENT_ENCODE_SET, "trie", HttpUrl.FRAGMENT_ENCODE_SET, "([Lokio/ByteString;[I)V", "getByteStrings$okio", "()[Lokio/ByteString;", "[Lokio/ByteString;", "size", HttpUrl.FRAGMENT_ENCODE_SET, "getSize", "()I", "getTrie$okio", "()[I", "get", "index", "Companion", "okio"}, k = 1, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class Options extends AbstractList<ByteString> implements RandomAccess {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final ByteString[] byteStrings;
    private final int[] trie;

    public /* synthetic */ Options(ByteString[] byteStringArr, int[] iArr, DefaultConstructorMarker defaultConstructorMarker) {
        this(byteStringArr, iArr);
    }

    @JvmStatic
    public static final Options of(ByteString... byteStringArr) {
        return INSTANCE.of(byteStringArr);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public final /* bridge */ boolean contains(Object element) {
        if (element instanceof ByteString) {
            return contains((ByteString) element);
        }
        return false;
    }

    public /* bridge */ boolean contains(ByteString element) {
        return super.contains((Options) element);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object element) {
        if (element instanceof ByteString) {
            return indexOf((ByteString) element);
        }
        return -1;
    }

    public /* bridge */ int indexOf(ByteString element) {
        return super.indexOf((Options) element);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object element) {
        if (element instanceof ByteString) {
            return lastIndexOf((ByteString) element);
        }
        return -1;
    }

    public /* bridge */ int lastIndexOf(ByteString element) {
        return super.lastIndexOf((Options) element);
    }

    /* renamed from: getByteStrings$okio, reason: from getter */
    public final ByteString[] getByteStrings() {
        return this.byteStrings;
    }

    /* renamed from: getTrie$okio, reason: from getter */
    public final int[] getTrie() {
        return this.trie;
    }

    private Options(ByteString[] byteStrings, int[] trie) {
        this.byteStrings = byteStrings;
        this.trie = trie;
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection
    public int getSize() {
        return this.byteStrings.length;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public ByteString get(int index) {
        return this.byteStrings[index];
    }

    /* compiled from: Options.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002JT\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u000fH\u0002J!\u0010\u0014\u001a\u00020\u00152\u0012\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100\u0016\"\u00020\u0010H\u0007¢\u0006\u0002\u0010\u0017R\u0018\u0010\u0003\u001a\u00020\u0004*\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0018"}, d2 = {"Lokio/Options$Companion;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "intCount", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/Buffer;", "getIntCount", "(Lokio/Buffer;)J", "buildTrieRecursive", HttpUrl.FRAGMENT_ENCODE_SET, "nodeOffset", "node", "byteStringOffset", HttpUrl.FRAGMENT_ENCODE_SET, "byteStrings", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/ByteString;", "fromIndex", "toIndex", "indexes", "of", "Lokio/Options;", HttpUrl.FRAGMENT_ENCODE_SET, "([Lokio/ByteString;)Lokio/Options;", "okio"}, k = 1, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:59:0x0107, code lost:
        
            continue;
         */
        @JvmStatic
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Options of(ByteString... byteStrings) throws IOException {
            Intrinsics.checkNotNullParameter(byteStrings, "byteStrings");
            DefaultConstructorMarker defaultConstructorMarker = null;
            if (byteStrings.length == 0) {
                return new Options(new ByteString[0], new int[]{0, -1}, defaultConstructorMarker);
            }
            List list = ArraysKt.toMutableList(byteStrings);
            CollectionsKt.sort(list);
            Collection destination$iv$iv = new ArrayList(byteStrings.length);
            for (ByteString byteString : byteStrings) {
                destination$iv$iv.add(-1);
            }
            Collection $this$toTypedArray$iv = (List) destination$iv$iv;
            Object[] array = $this$toTypedArray$iv.toArray(new Integer[0]);
            if (array == null) {
                throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
            }
            Integer[] numArr = (Integer[]) array;
            List indexes = CollectionsKt.mutableListOf(Arrays.copyOf(numArr, numArr.length));
            int index$iv = 0;
            int length = byteStrings.length;
            int i = 0;
            while (i < length) {
                int index$iv2 = index$iv + 1;
                int callerIndex = index$iv;
                int sortedIndex = CollectionsKt.binarySearch$default(list, byteStrings[i], 0, 0, 6, (Object) null);
                indexes.set(sortedIndex, Integer.valueOf(callerIndex));
                i++;
                index$iv = index$iv2;
            }
            if (!(((ByteString) list.get(0)).size() > 0)) {
                throw new IllegalArgumentException("the empty byte string is not a supported option".toString());
            }
            for (int a = 0; a < list.size(); a++) {
                ByteString prefix = (ByteString) list.get(a);
                int b = a + 1;
                while (b < list.size()) {
                    ByteString byteString2 = (ByteString) list.get(b);
                    if (!byteString2.startsWith(prefix)) {
                        break;
                    }
                    if (!(byteString2.size() != prefix.size())) {
                        throw new IllegalArgumentException(Intrinsics.stringPlus("duplicate option: ", byteString2).toString());
                    }
                    if (((Number) indexes.get(b)).intValue() > ((Number) indexes.get(a)).intValue()) {
                        list.remove(b);
                        indexes.remove(b);
                    } else {
                        b++;
                    }
                }
            }
            Buffer trieBytes = new Buffer();
            buildTrieRecursive$default(this, 0L, trieBytes, 0, list, 0, 0, indexes, 53, null);
            int[] trie = new int[(int) getIntCount(trieBytes)];
            int i2 = 0;
            while (!trieBytes.exhausted()) {
                trie[i2] = trieBytes.readInt();
                i2++;
            }
            Object[] objArrCopyOf = Arrays.copyOf(byteStrings, byteStrings.length);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "java.util.Arrays.copyOf(this, size)");
            return new Options((ByteString[]) objArrCopyOf, trie, defaultConstructorMarker);
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long j, Buffer buffer, int i, List list, int i2, int i3, List list2, int i4, Object obj) throws IOException {
            companion.buildTrieRecursive((i4 & 1) != 0 ? 0L : j, buffer, (i4 & 4) != 0 ? 0 : i, list, (i4 & 16) != 0 ? 0 : i2, (i4 & 32) != 0 ? list.size() : i3, list2);
        }

        /* JADX WARN: Removed duplicated region for block: B:59:0x015c  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private final void buildTrieRecursive(long nodeOffset, Buffer node, int byteStringOffset, List<? extends ByteString> byteStrings, int fromIndex, int toIndex, List<Integer> indexes) throws IOException {
            int fromIndex2;
            ByteString from;
            int prefixIndex;
            ByteString from2;
            ByteString to;
            int scanByteCount;
            int selectChoiceCount;
            int selectChoiceCount2;
            int prefixIndex2;
            int rangeEnd;
            Buffer childNodes;
            int rangeEnd2;
            int prefixIndex3;
            ByteString from3;
            int fromIndex3;
            ByteString to2;
            List<Integer> list = indexes;
            if (!(fromIndex < toIndex)) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            if (fromIndex < toIndex) {
                int i = fromIndex;
                do {
                    int i2 = i;
                    i++;
                    if (!(byteStrings.get(i2).size() >= byteStringOffset)) {
                        throw new IllegalArgumentException("Failed requirement.".toString());
                    }
                } while (i < toIndex);
            }
            ByteString from4 = byteStrings.get(fromIndex);
            ByteString to3 = byteStrings.get(toIndex - 1);
            if (byteStringOffset != from4.size()) {
                fromIndex2 = fromIndex;
                from = from4;
                prefixIndex = -1;
            } else {
                int prefixIndex4 = list.get(fromIndex).intValue();
                int fromIndex4 = fromIndex + 1;
                fromIndex2 = fromIndex4;
                from = byteStrings.get(fromIndex4);
                prefixIndex = prefixIndex4;
            }
            if (from.getByte(byteStringOffset) != to3.getByte(byteStringOffset)) {
                int selectChoiceCount3 = 1;
                int i3 = fromIndex2 + 1;
                if (i3 >= toIndex) {
                    selectChoiceCount = 1;
                } else {
                    do {
                        int i4 = i3;
                        i3++;
                        if (byteStrings.get(i4 - 1).getByte(byteStringOffset) != byteStrings.get(i4).getByte(byteStringOffset)) {
                            selectChoiceCount3++;
                        }
                    } while (i3 < toIndex);
                    selectChoiceCount = selectChoiceCount3;
                }
                long childNodesOffset = nodeOffset + getIntCount(node) + 2 + (selectChoiceCount * 2);
                node.writeInt(selectChoiceCount);
                node.writeInt(prefixIndex);
                if (fromIndex2 < toIndex) {
                    int i5 = fromIndex2;
                    do {
                        int i6 = i5;
                        i5++;
                        byte rangeByte = byteStrings.get(i6).getByte(byteStringOffset);
                        if (i6 == fromIndex2 || rangeByte != byteStrings.get(i6 - 1).getByte(byteStringOffset)) {
                            int other$iv = rangeByte & UByte.MAX_VALUE;
                            node.writeInt(other$iv);
                        }
                    } while (i5 < toIndex);
                }
                Buffer childNodes2 = new Buffer();
                int rangeStart = fromIndex2;
                while (rangeStart < toIndex) {
                    byte rangeByte2 = byteStrings.get(rangeStart).getByte(byteStringOffset);
                    int rangeEnd3 = rangeStart + 1;
                    if (rangeEnd3 < toIndex) {
                        while (true) {
                            int i7 = rangeEnd3;
                            rangeEnd3++;
                            selectChoiceCount2 = selectChoiceCount;
                            prefixIndex2 = prefixIndex;
                            if (rangeByte2 == byteStrings.get(i7).getByte(byteStringOffset)) {
                                if (rangeEnd3 >= toIndex) {
                                    break;
                                }
                                selectChoiceCount = selectChoiceCount2;
                                prefixIndex = prefixIndex2;
                            } else {
                                rangeEnd = i7;
                                break;
                            }
                        }
                        if (rangeStart + 1 != rangeEnd && byteStringOffset + 1 == byteStrings.get(rangeStart).size()) {
                            node.writeInt(list.get(rangeStart).intValue());
                            childNodes = childNodes2;
                            rangeEnd2 = rangeEnd;
                            fromIndex3 = fromIndex2;
                            to2 = to3;
                            prefixIndex3 = prefixIndex2;
                            from3 = from;
                        } else {
                            node.writeInt(((int) (childNodesOffset + getIntCount(childNodes2))) * (-1));
                            childNodes = childNodes2;
                            rangeEnd2 = rangeEnd;
                            prefixIndex3 = prefixIndex2;
                            from3 = from;
                            fromIndex3 = fromIndex2;
                            to2 = to3;
                            buildTrieRecursive(childNodesOffset, childNodes2, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd2, indexes);
                        }
                        rangeStart = rangeEnd2;
                        prefixIndex = prefixIndex3;
                        fromIndex2 = fromIndex3;
                        selectChoiceCount = selectChoiceCount2;
                        from = from3;
                        childNodes2 = childNodes;
                        to3 = to2;
                        list = indexes;
                    } else {
                        selectChoiceCount2 = selectChoiceCount;
                        prefixIndex2 = prefixIndex;
                    }
                    rangeEnd = toIndex;
                    if (rangeStart + 1 != rangeEnd) {
                        node.writeInt(((int) (childNodesOffset + getIntCount(childNodes2))) * (-1));
                        childNodes = childNodes2;
                        rangeEnd2 = rangeEnd;
                        prefixIndex3 = prefixIndex2;
                        from3 = from;
                        fromIndex3 = fromIndex2;
                        to2 = to3;
                        buildTrieRecursive(childNodesOffset, childNodes2, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd2, indexes);
                    }
                    rangeStart = rangeEnd2;
                    prefixIndex = prefixIndex3;
                    fromIndex2 = fromIndex3;
                    selectChoiceCount = selectChoiceCount2;
                    from = from3;
                    childNodes2 = childNodes;
                    to3 = to2;
                    list = indexes;
                }
                node.writeAll(childNodes2);
                return;
            }
            int prefixIndex5 = prefixIndex;
            ByteString from5 = from;
            int fromIndex5 = fromIndex2;
            ByteString to4 = to3;
            int scanByteCount2 = 0;
            int iMin = Math.min(from5.size(), to4.size());
            if (byteStringOffset < iMin) {
                int i8 = byteStringOffset;
                while (true) {
                    int i9 = i8;
                    i8++;
                    from2 = from5;
                    to = to4;
                    if (from2.getByte(i9) == to.getByte(i9)) {
                        scanByteCount2++;
                        if (i8 >= iMin) {
                            scanByteCount = scanByteCount2;
                            break;
                        } else {
                            to4 = to;
                            from5 = from2;
                        }
                    } else {
                        scanByteCount = scanByteCount2;
                        break;
                    }
                }
            } else {
                from2 = from5;
                to = to4;
                scanByteCount = 0;
            }
            long childNodesOffset2 = nodeOffset + getIntCount(node) + 2 + scanByteCount + 1;
            node.writeInt(-scanByteCount);
            node.writeInt(prefixIndex5);
            int i10 = byteStringOffset + scanByteCount;
            if (byteStringOffset < i10) {
                int i11 = byteStringOffset;
                do {
                    int i12 = i11;
                    i11++;
                    byte $this$and$iv = from2.getByte(i12);
                    node.writeInt($this$and$iv & UByte.MAX_VALUE);
                } while (i11 < i10);
            }
            if (fromIndex5 + 1 == toIndex) {
                if (!(byteStringOffset + scanByteCount == byteStrings.get(fromIndex5).size())) {
                    throw new IllegalStateException("Check failed.".toString());
                }
                node.writeInt(indexes.get(fromIndex5).intValue());
            } else {
                Buffer childNodes3 = new Buffer();
                node.writeInt(((int) (childNodesOffset2 + getIntCount(childNodes3))) * (-1));
                buildTrieRecursive(childNodesOffset2, childNodes3, byteStringOffset + scanByteCount, byteStrings, fromIndex5, toIndex, indexes);
                node.writeAll(childNodes3);
            }
        }

        private final long getIntCount(Buffer $this$intCount) {
            return $this$intCount.size() / 4;
        }
    }
}

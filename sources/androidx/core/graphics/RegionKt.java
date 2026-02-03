package androidx.core.graphics;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;
import okhttp3.HttpUrl;

/* compiled from: Region.kt */
@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010(\n\u0002\b\u0007\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0086\f\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0086\f\u001a\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\n\u001a3\u0010\b\u001a\u00020\t*\u00020\u00012!\u0010\n\u001a\u001d\u0012\u0013\u0012\u00110\u0003¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\t0\u000bH\u0086\bø\u0001\u0000\u001a\u0013\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0010*\u00020\u0001H\u0086\u0002\u001a\u0015\u0010\u0011\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0086\n\u001a\u0015\u0010\u0011\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0086\n\u001a\r\u0010\u0012\u001a\u00020\u0001*\u00020\u0001H\u0086\n\u001a\u0015\u0010\u0013\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0086\f\u001a\u0015\u0010\u0013\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0086\f\u001a\u0015\u0010\u0014\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0086\n\u001a\u0015\u0010\u0014\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0086\n\u001a\r\u0010\u0015\u001a\u00020\u0001*\u00020\u0001H\u0086\n\u001a\u0015\u0010\u0016\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0086\f\u001a\u0015\u0010\u0016\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0086\f\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0017"}, d2 = {"and", "Landroid/graphics/Region;", "r", "Landroid/graphics/Rect;", "contains", HttpUrl.FRAGMENT_ENCODE_SET, "p", "Landroid/graphics/Point;", "forEach", HttpUrl.FRAGMENT_ENCODE_SET, "action", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "rect", "iterator", HttpUrl.FRAGMENT_ENCODE_SET, "minus", "not", "or", "plus", "unaryMinus", "xor", "core-ktx_release"}, k = 2, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class RegionKt {
    public static final boolean contains(Region $this$contains, Point p) {
        Intrinsics.checkNotNullParameter($this$contains, "<this>");
        Intrinsics.checkNotNullParameter(p, "p");
        return $this$contains.contains(p.x, p.y);
    }

    public static final Region plus(Region $this$plus, Rect r) {
        Intrinsics.checkNotNullParameter($this$plus, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$plus_u24lambda_u2d0 = new Region($this$plus);
        $this$plus_u24lambda_u2d0.union(r);
        return $this$plus_u24lambda_u2d0;
    }

    public static final Region plus(Region $this$plus, Region r) {
        Intrinsics.checkNotNullParameter($this$plus, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$plus_u24lambda_u2d1 = new Region($this$plus);
        $this$plus_u24lambda_u2d1.op(r, Region.Op.UNION);
        return $this$plus_u24lambda_u2d1;
    }

    public static final Region minus(Region $this$minus, Rect r) {
        Intrinsics.checkNotNullParameter($this$minus, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$minus_u24lambda_u2d2 = new Region($this$minus);
        $this$minus_u24lambda_u2d2.op(r, Region.Op.DIFFERENCE);
        return $this$minus_u24lambda_u2d2;
    }

    public static final Region minus(Region $this$minus, Region r) {
        Intrinsics.checkNotNullParameter($this$minus, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$minus_u24lambda_u2d3 = new Region($this$minus);
        $this$minus_u24lambda_u2d3.op(r, Region.Op.DIFFERENCE);
        return $this$minus_u24lambda_u2d3;
    }

    public static final Region unaryMinus(Region $this$unaryMinus) {
        Intrinsics.checkNotNullParameter($this$unaryMinus, "<this>");
        Region $this$unaryMinus_u24lambda_u2d4 = new Region($this$unaryMinus.getBounds());
        $this$unaryMinus_u24lambda_u2d4.op($this$unaryMinus, Region.Op.DIFFERENCE);
        return $this$unaryMinus_u24lambda_u2d4;
    }

    public static final Region not(Region $this$not) {
        Intrinsics.checkNotNullParameter($this$not, "<this>");
        Region $this$unaryMinus_u24lambda_u2d4$iv = new Region($this$not.getBounds());
        $this$unaryMinus_u24lambda_u2d4$iv.op($this$not, Region.Op.DIFFERENCE);
        return $this$unaryMinus_u24lambda_u2d4$iv;
    }

    public static final Region or(Region $this$or, Rect r) {
        Intrinsics.checkNotNullParameter($this$or, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$plus_u24lambda_u2d0$iv = new Region($this$or);
        $this$plus_u24lambda_u2d0$iv.union(r);
        return $this$plus_u24lambda_u2d0$iv;
    }

    public static final Region or(Region $this$or, Region r) {
        Intrinsics.checkNotNullParameter($this$or, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$plus_u24lambda_u2d1$iv = new Region($this$or);
        $this$plus_u24lambda_u2d1$iv.op(r, Region.Op.UNION);
        return $this$plus_u24lambda_u2d1$iv;
    }

    public static final Region and(Region $this$and, Rect r) {
        Intrinsics.checkNotNullParameter($this$and, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$and_u24lambda_u2d5 = new Region($this$and);
        $this$and_u24lambda_u2d5.op(r, Region.Op.INTERSECT);
        return $this$and_u24lambda_u2d5;
    }

    public static final Region and(Region $this$and, Region r) {
        Intrinsics.checkNotNullParameter($this$and, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$and_u24lambda_u2d6 = new Region($this$and);
        $this$and_u24lambda_u2d6.op(r, Region.Op.INTERSECT);
        return $this$and_u24lambda_u2d6;
    }

    public static final Region xor(Region $this$xor, Rect r) {
        Intrinsics.checkNotNullParameter($this$xor, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$xor_u24lambda_u2d7 = new Region($this$xor);
        $this$xor_u24lambda_u2d7.op(r, Region.Op.XOR);
        return $this$xor_u24lambda_u2d7;
    }

    public static final Region xor(Region $this$xor, Region r) {
        Intrinsics.checkNotNullParameter($this$xor, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        Region $this$xor_u24lambda_u2d8 = new Region($this$xor);
        $this$xor_u24lambda_u2d8.op(r, Region.Op.XOR);
        return $this$xor_u24lambda_u2d8;
    }

    public static final void forEach(Region $this$forEach, Function1<? super Rect, Unit> action) {
        Intrinsics.checkNotNullParameter($this$forEach, "<this>");
        Intrinsics.checkNotNullParameter(action, "action");
        RegionIterator iterator = new RegionIterator($this$forEach);
        while (true) {
            Rect r = new Rect();
            if (iterator.next(r)) {
                action.invoke(r);
            } else {
                return;
            }
        }
    }

    /* compiled from: Region.kt */
    @Metadata(d1 = {"\u0000\u001d\n\u0000\n\u0002\u0010(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\t\u0010\b\u001a\u00020\u0004H\u0096\u0002J\t\u0010\t\u001a\u00020\u0002H\u0096\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"androidx/core/graphics/RegionKt$iterator$1", HttpUrl.FRAGMENT_ENCODE_SET, "Landroid/graphics/Rect;", "hasMore", HttpUrl.FRAGMENT_ENCODE_SET, "iterator", "Landroid/graphics/RegionIterator;", "rect", "hasNext", "next", "core-ktx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
    /* renamed from: androidx.core.graphics.RegionKt$iterator$1, reason: invalid class name */
    public static final class AnonymousClass1 implements Iterator<Rect>, KMappedMarker {
        private boolean hasMore;
        private final RegionIterator iterator;
        private final Rect rect = new Rect();

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }

        AnonymousClass1(Region $receiver) {
            this.iterator = new RegionIterator($receiver);
            this.hasMore = this.iterator.next(this.rect);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.hasMore;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Rect next() {
            if (this.hasMore) {
                Rect r = new Rect(this.rect);
                this.hasMore = this.iterator.next(this.rect);
                return r;
            }
            throw new IndexOutOfBoundsException();
        }
    }

    public static final Iterator<Rect> iterator(Region $this$iterator) {
        Intrinsics.checkNotNullParameter($this$iterator, "<this>");
        return new AnonymousClass1($this$iterator);
    }
}

package androidx.core.widget;

import android.widget.ListView;

/* loaded from: classes.dex */
public final class ListViewCompat {
    public static void scrollListBy(ListView listView, int y) {
        Api19Impl.scrollListBy(listView, y);
    }

    public static boolean canScrollList(ListView listView, int direction) {
        return Api19Impl.canScrollList(listView, direction);
    }

    private ListViewCompat() {
    }

    static class Api19Impl {
        private Api19Impl() {
        }

        static void scrollListBy(ListView absListView, int y) {
            absListView.scrollListBy(y);
        }

        static boolean canScrollList(ListView absListView, int direction) {
            return absListView.canScrollList(direction);
        }
    }
}

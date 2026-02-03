package com.bumptech.glide.request.target;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;

/* loaded from: classes.dex */
public class NotificationTarget extends CustomTarget<Bitmap> {
    private final Context context;
    private final Notification notification;
    private final int notificationId;
    private final String notificationTag;
    private final RemoteViews remoteViews;
    private final int viewId;

    @Override // com.bumptech.glide.request.target.Target
    public /* bridge */ /* synthetic */ void onResourceReady(Object obj, Transition transition) {
        onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
    }

    public NotificationTarget(Context context, int viewId, RemoteViews remoteViews, Notification notification, int notificationId) {
        this(context, viewId, remoteViews, notification, notificationId, null);
    }

    public NotificationTarget(Context context, int viewId, RemoteViews remoteViews, Notification notification, int notificationId, String notificationTag) {
        this(context, Integer.MIN_VALUE, Integer.MIN_VALUE, viewId, remoteViews, notification, notificationId, notificationTag);
    }

    public NotificationTarget(Context context, int width, int height, int viewId, RemoteViews remoteViews, Notification notification, int notificationId, String notificationTag) {
        super(width, height);
        this.context = (Context) Preconditions.checkNotNull(context, "Context must not be null!");
        this.notification = (Notification) Preconditions.checkNotNull(notification, "Notification object can not be null!");
        this.remoteViews = (RemoteViews) Preconditions.checkNotNull(remoteViews, "RemoteViews object can not be null!");
        this.viewId = viewId;
        this.notificationId = notificationId;
        this.notificationTag = notificationTag;
    }

    private void update() {
        NotificationManager manager = (NotificationManager) this.context.getSystemService("notification");
        ((NotificationManager) Preconditions.checkNotNull(manager)).notify(this.notificationTag, this.notificationId, this.notification);
    }

    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
        setBitmap(resource);
    }

    @Override // com.bumptech.glide.request.target.Target
    public void onLoadCleared(Drawable placeholder) {
        setBitmap(null);
    }

    private void setBitmap(Bitmap bitmap) {
        this.remoteViews.setImageViewBitmap(this.viewId, bitmap);
        update();
    }
}

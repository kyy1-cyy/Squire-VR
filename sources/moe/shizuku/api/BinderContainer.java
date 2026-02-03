package moe.shizuku.api;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class BinderContainer implements Parcelable {
    public static final Parcelable.Creator<BinderContainer> CREATOR = new Parcelable.Creator<BinderContainer>() { // from class: moe.shizuku.api.BinderContainer.1
        @Override // android.os.Parcelable.Creator
        public BinderContainer createFromParcel(Parcel source) {
            return new BinderContainer(source);
        }

        @Override // android.os.Parcelable.Creator
        public BinderContainer[] newArray(int size) {
            return new BinderContainer[size];
        }
    };
    public IBinder binder;

    public BinderContainer(IBinder binder) {
        this.binder = binder;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.binder);
    }

    protected BinderContainer(Parcel in) {
        this.binder = in.readStrongBinder();
    }
}

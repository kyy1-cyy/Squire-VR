package com.google.android.material.color;

import android.content.Context;
import android.content.res.loader.ResourcesLoader;
import android.content.res.loader.ResourcesProvider;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/* loaded from: classes.dex */
final class ColorResourcesLoaderCreator {
    private static final String TAG = ColorResourcesLoaderCreator.class.getSimpleName();

    private ColorResourcesLoaderCreator() {
    }

    static ResourcesLoader create(Context context, Map<Integer, Integer> colorMapping) throws ErrnoException {
        try {
            byte[] contentBytes = ColorResourcesTableCreator.create(context, colorMapping);
            Log.i(TAG, "Table created, length: " + contentBytes.length);
            if (contentBytes.length == 0) {
                return null;
            }
            FileDescriptor arscFile = null;
            try {
                arscFile = Os.memfd_create("temp.arsc", 0);
                OutputStream pipeWriter = new FileOutputStream(arscFile);
                try {
                    pipeWriter.write(contentBytes);
                    ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(arscFile);
                    try {
                        ResourcesLoader colorsLoader = new ResourcesLoader();
                        colorsLoader.addProvider(ResourcesProvider.loadFromTable(pfd, null));
                        if (pfd != null) {
                            pfd.close();
                        }
                        pipeWriter.close();
                        return colorsLoader;
                    } finally {
                    }
                } finally {
                }
            } finally {
                if (arscFile != null) {
                    Os.close(arscFile);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to create the ColorResourcesTableCreator.", e);
            return null;
        }
    }
}

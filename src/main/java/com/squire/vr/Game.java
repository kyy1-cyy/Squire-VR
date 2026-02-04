package com.squire.vr;

/* compiled from: MainActivity.java */
/* loaded from: classes3.dex */
class Game {
    String baseUri;
    String date;
    String gameName;
    String installedDate;
    String installedVersion;
    boolean isExpanded;
    String md5Hash;
    boolean needsUpdate;
    String noteContent;
    String packageName;
    String password;
    String releaseName;
    String sizeMb;
    String thumbnailPath;
    String version;
    long sizeBytes; // Added for sorting
    double popularityScore; // Tracks download count/popularity (double for precision)
    boolean isFavorite; // Tracks if game is favorited
    long stableId; // Unique ID for RecyclerView

    Game() {
    }
}

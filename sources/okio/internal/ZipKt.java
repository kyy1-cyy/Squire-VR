package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.UShort;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okhttp3.HttpUrl;
import okhttp3.internal.ws.WebSocketProtocol;
import okio.BufferedSource;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.ZipFileSystem;

/* compiled from: zip.kt */
@Metadata(d1 = {"\u0000j\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\"\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0002\u001a\u001f\u0010\u0018\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0001H\u0002¢\u0006\u0002\u0010\u001b\u001a.\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020 2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020#0\"H\u0000\u001a\f\u0010$\u001a\u00020\u0015*\u00020%H\u0000\u001a\f\u0010&\u001a\u00020'*\u00020%H\u0002\u001a.\u0010(\u001a\u00020)*\u00020%2\u0006\u0010*\u001a\u00020\u00012\u0018\u0010+\u001a\u0014\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020)0,H\u0002\u001a\u0014\u0010-\u001a\u00020.*\u00020%2\u0006\u0010/\u001a\u00020.H\u0000\u001a\u0018\u00100\u001a\u0004\u0018\u00010.*\u00020%2\b\u0010/\u001a\u0004\u0018\u00010.H\u0002\u001a\u0014\u00101\u001a\u00020'*\u00020%2\u0006\u00102\u001a\u00020'H\u0002\u001a\f\u00103\u001a\u00020)*\u00020%H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u0018\u0010\u000e\u001a\u00020\u000f*\u00020\u00018BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u00064"}, d2 = {"BIT_FLAG_ENCRYPTED", HttpUrl.FRAGMENT_ENCODE_SET, "BIT_FLAG_UNSUPPORTED_MASK", "CENTRAL_FILE_HEADER_SIGNATURE", "COMPRESSION_METHOD_DEFLATED", "COMPRESSION_METHOD_STORED", "END_OF_CENTRAL_DIRECTORY_SIGNATURE", "HEADER_ID_EXTENDED_TIMESTAMP", "HEADER_ID_ZIP64_EXTENDED_INFO", "LOCAL_FILE_HEADER_SIGNATURE", "MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE", HttpUrl.FRAGMENT_ENCODE_SET, "ZIP64_EOCD_RECORD_SIGNATURE", "ZIP64_LOCATOR_SIGNATURE", "hex", HttpUrl.FRAGMENT_ENCODE_SET, "getHex", "(I)Ljava/lang/String;", "buildIndex", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/Path;", "Lokio/internal/ZipEntry;", "entries", HttpUrl.FRAGMENT_ENCODE_SET, "dosDateTimeToEpochMillis", "date", "time", "(II)Ljava/lang/Long;", "openZip", "Lokio/ZipFileSystem;", "zipPath", "fileSystem", "Lokio/FileSystem;", "predicate", "Lkotlin/Function1;", HttpUrl.FRAGMENT_ENCODE_SET, "readEntry", "Lokio/BufferedSource;", "readEocdRecord", "Lokio/internal/EocdRecord;", "readExtra", HttpUrl.FRAGMENT_ENCODE_SET, "extraSize", "block", "Lkotlin/Function2;", "readLocalHeader", "Lokio/FileMetadata;", "basicMetadata", "readOrSkipLocalHeader", "readZip64EocdRecord", "regularRecord", "skipLocalHeader", "okio"}, k = 2, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class ZipKt {
    private static final int BIT_FLAG_ENCRYPTED = 1;
    private static final int BIT_FLAG_UNSUPPORTED_MASK = 1;
    private static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
    public static final int COMPRESSION_METHOD_DEFLATED = 8;
    public static final int COMPRESSION_METHOD_STORED = 0;
    private static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 101010256;
    private static final int HEADER_ID_EXTENDED_TIMESTAMP = 21589;
    private static final int HEADER_ID_ZIP64_EXTENDED_INFO = 1;
    private static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
    private static final long MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE = 4294967295L;
    private static final int ZIP64_EOCD_RECORD_SIGNATURE = 101075792;
    private static final int ZIP64_LOCATOR_SIGNATURE = 117853008;

    public static /* synthetic */ ZipFileSystem openZip$default(Path path, FileSystem fileSystem, Function1 function1, int i, Object obj) throws IOException {
        if ((i & 4) != 0) {
            function1 = new Function1<ZipEntry, Boolean>() { // from class: okio.internal.ZipKt.openZip.1
                @Override // kotlin.jvm.functions.Function1
                public final Boolean invoke(ZipEntry it) {
                    Intrinsics.checkNotNullParameter(it, "it");
                    return true;
                }
            };
        }
        return openZip(path, fileSystem, function1);
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x022f, code lost:
    
        if (r22 < r13) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0232, code lost:
    
        r3 = r36;
        r0 = r19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x0242, code lost:
    
        throw new java.io.IOException("bad zip: local file header offset >= central directory offset");
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0245, code lost:
    
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0249, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r9, null);
        r0 = buildIndex(r0);
        r3 = new okio.ZipFileSystem(r34, r35, r0, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0255, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r4, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x0259, code lost:
    
        return r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x025a, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x0263, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x0264, code lost:
    
        r3 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x026c, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x026d, code lost:
    
        r3 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x0277, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x0278, code lost:
    
        r3 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x02af, code lost:
    
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:132:0x02b2, code lost:
    
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00b1, code lost:
    
        r17 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00b7, code lost:
    
        r7 = readEocdRecord(r20);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00bf, code lost:
    
        r0 = r20.readUtf8(r7.getCommentByteCount());
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00c5, code lost:
    
        r20.close();
        r12 = 20;
        r12 = r17 - r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00d3, code lost:
    
        if (r12 <= 0) goto L90;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00d5, code lost:
    
        r9 = okio.Okio.buffer(r5.source(r12));
        r15 = (java.lang.Throwable) null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00e4, code lost:
    
        r0 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00f3, code lost:
    
        if (r0.readIntLe() != okio.internal.ZipKt.ZIP64_LOCATOR_SIGNATURE) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00f5, code lost:
    
        r0 = r0.readIntLe();
        r26 = r0.readLongLe();
        r0 = r0.readIntLe();
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x010b, code lost:
    
        if (r0 != 1) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x010d, code lost:
    
        if (r0 != 0) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0113, code lost:
    
        r6 = okio.Okio.buffer(r5.source(r26));
        r20 = (java.lang.Throwable) null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0125, code lost:
    
        r0 = r6;
        r28 = r0.readIntLe();
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0137, code lost:
    
        if (r28 != okio.internal.ZipKt.ZIP64_EOCD_RECORD_SIGNATURE) goto L186;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0139, code lost:
    
        r7 = readZip64EocdRecord(r0, r7);
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0142, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r6, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0147, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0148, code lost:
    
        r12 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0179, code lost:
    
        throw new java.io.IOException("bad zip: expected " + getHex(okio.internal.ZipKt.ZIP64_EOCD_RECORD_SIGNATURE) + " but was " + getHex(r28));
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x017a, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x017b, code lost:
    
        r12 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x017f, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0180, code lost:
    
        r12 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0184, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0185, code lost:
    
        r12 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x018a, code lost:
    
        throw r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x018b, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x018d, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r6, r12);
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0190, code lost:
    
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0191, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0192, code lost:
    
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01a5, code lost:
    
        throw new java.io.IOException("unsupported zip: spanned");
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x01a6, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01a7, code lost:
    
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01ab, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01ac, code lost:
    
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01b7, code lost:
    
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x01ba, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r9, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x01be, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01bf, code lost:
    
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01c1, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01c2, code lost:
    
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x01c9, code lost:
    
        throw r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01ca, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01cc, code lost:
    
        kotlin.io.CloseableKt.closeFinally(r9, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01cf, code lost:
    
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01d6, code lost:
    
        r0 = new java.util.ArrayList();
        r9 = okio.Okio.buffer(r5.source(r7.getCentralDirectoryOffset()));
        r12 = (java.lang.Throwable) null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01f1, code lost:
    
        r0 = r9;
        r13 = r7.getEntryCount();
        r22 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x01fd, code lost:
    
        if (0 >= r13) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x01ff, code lost:
    
        r22 = r22 + 1;
        r19 = readEntry(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0215, code lost:
    
        if (r19.getOffset() >= r7.getCentralDirectoryOffset()) goto L191;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0217, code lost:
    
        r19 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0225, code lost:
    
        if (r3.invoke(r19).booleanValue() == false) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0227, code lost:
    
        r0.add(r19);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final ZipFileSystem openZip(Path zipPath, FileSystem fileSystem, Function1<? super ZipEntry, Boolean> function1) throws IOException {
        Throwable th;
        Function1<? super ZipEntry, Boolean> predicate = function1;
        Intrinsics.checkNotNullParameter(zipPath, "zipPath");
        Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        BufferedSource bufferedSourceOpenReadOnly = fileSystem.openReadOnly(zipPath);
        try {
            FileHandle fileHandle = bufferedSourceOpenReadOnly;
            int i = 0;
            long j = 0;
            BufferedSource source = Okio.buffer(FileHandle.source$default(fileHandle, 0L, 1, null));
            try {
                int firstFileSignature = source.readIntLe();
                if (firstFileSignature != LOCAL_FILE_HEADER_SIGNATURE) {
                    try {
                        if (firstFileSignature == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
                            throw new IOException("unsupported zip: empty");
                        }
                        throw new IOException("not a zip: expected " + getHex(LOCAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(firstFileSignature));
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            throw th;
                        } catch (Throwable th3) {
                            CloseableKt.closeFinally(source, th);
                            throw th3;
                        }
                    }
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(source, null);
                long scanOffset = fileHandle.size() - 22;
                if (scanOffset < 0) {
                    throw new IOException(Intrinsics.stringPlus("not a zip: size=", Long.valueOf(fileHandle.size())));
                }
                long stopOffset = Math.max(scanOffset - 65536, 0L);
                while (true) {
                    BufferedSource source2 = Okio.buffer(fileHandle.source(scanOffset));
                    try {
                        if (source2.readIntLe() == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
                            break;
                        }
                        long j2 = j;
                        long stopOffset2 = stopOffset;
                        int i2 = i;
                        source2.close();
                        scanOffset--;
                        if (scanOffset < stopOffset2) {
                            throw new IOException("not a zip: end of central directory signature not found");
                        }
                        predicate = function1;
                        i = i2;
                        j = j2;
                        stopOffset = stopOffset2;
                    } catch (Throwable th4) {
                        th = th4;
                        BufferedSource source3 = source2;
                    }
                }
            } catch (Throwable th5) {
                th = th5;
            }
        } finally {
        }
    }

    private static final Map<Path, ZipEntry> buildIndex(List<ZipEntry> list) {
        Map result = new LinkedHashMap();
        List<ZipEntry> $this$sortedBy$iv = list;
        for (ZipEntry entry : CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: okio.internal.ZipKt$buildIndex$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                ZipEntry it = (ZipEntry) t;
                ZipEntry it2 = (ZipEntry) t2;
                return ComparisonsKt.compareValues(it.getCanonicalPath(), it2.getCanonicalPath());
            }
        })) {
            ZipEntry replaced = (ZipEntry) result.put(entry.getCanonicalPath(), entry);
            if (replaced == null) {
                ZipEntry child = entry;
                while (true) {
                    Path parentPath = child.getCanonicalPath().parent();
                    if (parentPath != null) {
                        ZipEntry parentEntry = (ZipEntry) result.get(parentPath);
                        if (parentEntry != null) {
                            parentEntry.getChildren().add(child.getCanonicalPath());
                            break;
                        }
                        ZipEntry parentEntry2 = new ZipEntry(parentPath, true, null, 0L, 0L, 0L, 0, null, 0L, TypedValues.PositionType.TYPE_CURVE_FIT, null);
                        result.put(parentPath, parentEntry2);
                        parentEntry2.getChildren().add(child.getCanonicalPath());
                        child = parentEntry2;
                    }
                }
            }
        }
        return result;
    }

    public static final ZipEntry readEntry(final BufferedSource $this$readEntry) throws IOException {
        Ref.LongRef offset;
        Intrinsics.checkNotNullParameter($this$readEntry, "<this>");
        int signature = $this$readEntry.readIntLe();
        if (signature != CENTRAL_FILE_HEADER_SIGNATURE) {
            throw new IOException("bad zip: expected " + getHex(CENTRAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(signature));
        }
        $this$readEntry.skip(4L);
        int bitFlag = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        if ((bitFlag & 1) != 0) {
            throw new IOException(Intrinsics.stringPlus("unsupported zip: general purpose bit flag=", getHex(bitFlag)));
        }
        int compressionMethod = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int time = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int date = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        Long lastModifiedAtMillis = dosDateTimeToEpochMillis(date, time);
        long crc = $this$readEntry.readIntLe() & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        final Ref.LongRef compressedSize = new Ref.LongRef();
        compressedSize.element = $this$readEntry.readIntLe() & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        final Ref.LongRef size = new Ref.LongRef();
        size.element = $this$readEntry.readIntLe() & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        int nameSize = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int extraSize = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int commentByteCount = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        $this$readEntry.skip(8L);
        Ref.LongRef offset2 = new Ref.LongRef();
        offset2.element = $this$readEntry.readIntLe() & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        String name = $this$readEntry.readUtf8(nameSize);
        if (StringsKt.contains$default((CharSequence) name, (char) 0, false, 2, (Object) null)) {
            throw new IOException("bad zip: filename contains 0x00");
        }
        long result = 0;
        if (size.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            offset = offset2;
            result = 0 + 8;
        } else {
            offset = offset2;
        }
        if (compressedSize.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            result += 8;
        }
        if (offset.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            result += 8;
        }
        final long requiredZip64ExtraSize = result;
        final Ref.BooleanRef hasZip64Extra = new Ref.BooleanRef();
        final Ref.LongRef offset3 = offset;
        readExtra($this$readEntry, extraSize, new Function2<Integer, Long, Unit>() { // from class: okio.internal.ZipKt.readEntry.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(2);
            }

            @Override // kotlin.jvm.functions.Function2
            public /* bridge */ /* synthetic */ Unit invoke(Integer num, Long l) throws IOException {
                invoke(num.intValue(), l.longValue());
                return Unit.INSTANCE;
            }

            public final void invoke(int headerId, long dataSize) throws IOException {
                if (headerId == 1) {
                    if (!hasZip64Extra.element) {
                        hasZip64Extra.element = true;
                        if (dataSize < requiredZip64ExtraSize) {
                            throw new IOException("bad zip: zip64 extra too short");
                        }
                        size.element = size.element == ZipKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : size.element;
                        compressedSize.element = compressedSize.element == ZipKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : 0L;
                        offset3.element = offset3.element == ZipKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : 0L;
                        return;
                    }
                    throw new IOException("bad zip: zip64 extra repeated");
                }
            }
        });
        if (requiredZip64ExtraSize > 0 && !hasZip64Extra.element) {
            throw new IOException("bad zip: zip64 extra required but absent");
        }
        String comment = $this$readEntry.readUtf8(commentByteCount);
        Path canonicalPath = Path.Companion.get$default(Path.INSTANCE, "/", false, 1, (Object) null).resolve(name);
        boolean isDirectory = StringsKt.endsWith$default(name, "/", false, 2, (Object) null);
        return new ZipEntry(canonicalPath, isDirectory, comment, crc, compressedSize.element, size.element, compressionMethod, lastModifiedAtMillis, offset3.element);
    }

    private static final EocdRecord readEocdRecord(BufferedSource $this$readEocdRecord) throws IOException {
        int diskNumber = $this$readEocdRecord.readShortLe() & 65535;
        int diskWithCentralDir = $this$readEocdRecord.readShortLe() & 65535;
        long entryCount = $this$readEocdRecord.readShortLe() & UShort.MAX_VALUE;
        long totalEntryCount = $this$readEocdRecord.readShortLe() & UShort.MAX_VALUE;
        if (entryCount != totalEntryCount || diskNumber != 0 || diskWithCentralDir != 0) {
            throw new IOException("unsupported zip: spanned");
        }
        $this$readEocdRecord.skip(4L);
        long centralDirectoryOffset = $this$readEocdRecord.readIntLe() & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        int commentByteCount = 65535 & $this$readEocdRecord.readShortLe();
        return new EocdRecord(entryCount, centralDirectoryOffset, commentByteCount);
    }

    private static final EocdRecord readZip64EocdRecord(BufferedSource $this$readZip64EocdRecord, EocdRecord regularRecord) throws IOException {
        $this$readZip64EocdRecord.skip(12L);
        int diskNumber = $this$readZip64EocdRecord.readIntLe();
        int diskWithCentralDirStart = $this$readZip64EocdRecord.readIntLe();
        long entryCount = $this$readZip64EocdRecord.readLongLe();
        long totalEntryCount = $this$readZip64EocdRecord.readLongLe();
        if (entryCount == totalEntryCount && diskNumber == 0 && diskWithCentralDirStart == 0) {
            $this$readZip64EocdRecord.skip(8L);
            long centralDirectoryOffset = $this$readZip64EocdRecord.readLongLe();
            return new EocdRecord(entryCount, centralDirectoryOffset, regularRecord.getCommentByteCount());
        }
        throw new IOException("unsupported zip: spanned");
    }

    private static final void readExtra(BufferedSource $this$readExtra, int extraSize, Function2<? super Integer, ? super Long, Unit> function2) throws IOException {
        long remaining = extraSize;
        while (remaining != 0) {
            if (remaining < 4) {
                throw new IOException("bad zip: truncated header in extra field");
            }
            int headerId = $this$readExtra.readShortLe() & UShort.MAX_VALUE;
            long dataSize = $this$readExtra.readShortLe() & WebSocketProtocol.PAYLOAD_SHORT_MAX;
            long remaining2 = remaining - 4;
            if (remaining2 < dataSize) {
                throw new IOException("bad zip: truncated value in extra field");
            }
            $this$readExtra.require(dataSize);
            long sizeBefore = $this$readExtra.getBuffer().size();
            function2.invoke(Integer.valueOf(headerId), Long.valueOf(dataSize));
            long fieldRemaining = ($this$readExtra.getBuffer().size() + dataSize) - sizeBefore;
            if (fieldRemaining < 0) {
                throw new IOException(Intrinsics.stringPlus("unsupported zip: too many bytes processed for ", Integer.valueOf(headerId)));
            }
            if (fieldRemaining > 0) {
                $this$readExtra.getBuffer().skip(fieldRemaining);
            }
            remaining = remaining2 - dataSize;
        }
    }

    public static final void skipLocalHeader(BufferedSource $this$skipLocalHeader) throws IOException {
        Intrinsics.checkNotNullParameter($this$skipLocalHeader, "<this>");
        readOrSkipLocalHeader($this$skipLocalHeader, null);
    }

    public static final FileMetadata readLocalHeader(BufferedSource $this$readLocalHeader, FileMetadata basicMetadata) throws IOException {
        Intrinsics.checkNotNullParameter($this$readLocalHeader, "<this>");
        Intrinsics.checkNotNullParameter(basicMetadata, "basicMetadata");
        FileMetadata orSkipLocalHeader = readOrSkipLocalHeader($this$readLocalHeader, basicMetadata);
        Intrinsics.checkNotNull(orSkipLocalHeader);
        return orSkipLocalHeader;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final FileMetadata readOrSkipLocalHeader(final BufferedSource bufferedSource, FileMetadata fileMetadata) throws IOException {
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        objectRef.element = fileMetadata == null ? 0 : fileMetadata.getLastModifiedAtMillis();
        final Ref.ObjectRef objectRef2 = new Ref.ObjectRef();
        final Ref.ObjectRef objectRef3 = new Ref.ObjectRef();
        int intLe = bufferedSource.readIntLe();
        if (intLe == LOCAL_FILE_HEADER_SIGNATURE) {
            bufferedSource.skip(2L);
            int shortLe = bufferedSource.readShortLe() & UShort.MAX_VALUE;
            if ((shortLe & 1) == 0) {
                bufferedSource.skip(18L);
                long shortLe2 = bufferedSource.readShortLe() & WebSocketProtocol.PAYLOAD_SHORT_MAX;
                int shortLe3 = 65535 & bufferedSource.readShortLe();
                bufferedSource.skip(shortLe2);
                if (fileMetadata == null) {
                    bufferedSource.skip(shortLe3);
                    return null;
                }
                readExtra(bufferedSource, shortLe3, new Function2<Integer, Long, Unit>() { // from class: okio.internal.ZipKt.readOrSkipLocalHeader.1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(2);
                    }

                    @Override // kotlin.jvm.functions.Function2
                    public /* bridge */ /* synthetic */ Unit invoke(Integer num, Long l) throws IOException {
                        invoke(num.intValue(), l.longValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Type inference failed for: r4v12, types: [T, java.lang.Long] */
                    /* JADX WARN: Type inference failed for: r4v6, types: [T, java.lang.Long] */
                    /* JADX WARN: Type inference failed for: r4v9, types: [T, java.lang.Long] */
                    public final void invoke(int headerId, long dataSize) throws IOException {
                        if (headerId == ZipKt.HEADER_ID_EXTENDED_TIMESTAMP) {
                            if (dataSize < 1) {
                                throw new IOException("bad zip: extended timestamp extra too short");
                            }
                            int flags = bufferedSource.readByte() & 255;
                            boolean hasLastModifiedAtMillis = (flags & 1) == 1;
                            boolean hasLastAccessedAtMillis = (flags & 2) == 2;
                            boolean hasCreatedAtMillis = (flags & 4) == 4;
                            BufferedSource bufferedSource2 = bufferedSource;
                            long result = hasLastModifiedAtMillis ? 1 + 4 : 1L;
                            if (hasLastAccessedAtMillis) {
                                result += 4;
                            }
                            if (hasCreatedAtMillis) {
                                result += 4;
                            }
                            long requiredSize = result;
                            if (dataSize < requiredSize) {
                                throw new IOException("bad zip: extended timestamp extra too short");
                            }
                            if (hasLastModifiedAtMillis) {
                                objectRef.element = Long.valueOf(bufferedSource.readIntLe() * 1000);
                            }
                            if (hasLastAccessedAtMillis) {
                                objectRef2.element = Long.valueOf(bufferedSource.readIntLe() * 1000);
                            }
                            if (hasCreatedAtMillis) {
                                objectRef3.element = Long.valueOf(bufferedSource.readIntLe() * 1000);
                            }
                        }
                    }
                });
                return new FileMetadata(fileMetadata.getIsRegularFile(), fileMetadata.getIsDirectory(), null, fileMetadata.getSize(), (Long) objectRef3.element, (Long) objectRef.element, (Long) objectRef2.element, null, 128, null);
            }
            throw new IOException(Intrinsics.stringPlus("unsupported zip: general purpose bit flag=", getHex(shortLe)));
        }
        throw new IOException("bad zip: expected " + getHex(LOCAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(intLe));
    }

    private static final Long dosDateTimeToEpochMillis(int date, int time) {
        if (time == -1) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(14, 0);
        int year = ((date >> 9) & 127) + 1980;
        int month = (date >> 5) & 15;
        int day = date & 31;
        int hour = (time >> 11) & 31;
        int minute = (time >> 5) & 63;
        int second = (time & 31) << 1;
        cal.set(year, month - 1, day, hour, minute, second);
        return Long.valueOf(cal.getTime().getTime());
    }

    private static final String getHex(int $this$hex) {
        String string = Integer.toString($this$hex, CharsKt.checkRadix(16));
        Intrinsics.checkNotNullExpressionValue(string, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return Intrinsics.stringPlus("0x", string);
    }
}

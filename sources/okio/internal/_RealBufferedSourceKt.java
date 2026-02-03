package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.location.LocationRequestCompat;
import java.io.EOFException;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import kotlin.text.Typography;
import okhttp3.HttpUrl;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.PeekSource;
import okio.Sink;
import okio.Timeout;
import okio._UtilKt;
import okio.buffer;

/* compiled from: -RealBufferedSource.kt */
@Metadata(d1 = {"\u0000j\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0003\u001a\u00020\u0004*\u00020\u0002H\u0080\b\u001a%\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u0006H\u0080\b\u001a\u001d\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\u0006H\u0080\b\u001a\u001d\u0010\r\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u000f\u001a\u00020\u0010*\u00020\u0002H\u0080\b\u001a-\u0010\u0011\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0080\b\u001a%\u0010\u0016\u001a\u00020\u0014*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0080\b\u001a\u001d\u0010\u0016\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010\u001a\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u001bH\u0080\b\u001a\r\u0010\u001c\u001a\u00020\b*\u00020\u0002H\u0080\b\u001a\r\u0010\u001d\u001a\u00020\u0018*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u001d\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u001e\u001a\u00020\f*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u001e\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010\u001f\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\u0015\u0010 \u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u0018H\u0080\b\u001a\u001d\u0010 \u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010!\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010\"\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\r\u0010#\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\r\u0010$\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010%\u001a\u00020\u0006*\u00020\u0002H\u0080\b\u001a\r\u0010&\u001a\u00020'*\u00020\u0002H\u0080\b\u001a\r\u0010(\u001a\u00020'*\u00020\u0002H\u0080\b\u001a\r\u0010)\u001a\u00020**\u00020\u0002H\u0080\b\u001a\u0015\u0010)\u001a\u00020**\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u0010+\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\u000f\u0010,\u001a\u0004\u0018\u00010**\u00020\u0002H\u0080\b\u001a\u0015\u0010-\u001a\u00020**\u00020\u00022\u0006\u0010.\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010/\u001a\u00020\u0004*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u00100\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\u0015\u00101\u001a\u00020\u0014*\u00020\u00022\u0006\u00102\u001a\u000203H\u0080\b\u001a\u0015\u00104\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0006H\u0080\b\u001a\r\u00105\u001a\u000206*\u00020\u0002H\u0080\b\u001a\r\u00107\u001a\u00020**\u00020\u0002H\u0080\b¨\u00068"}, d2 = {"commonClose", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/RealBufferedSource;", "commonExhausted", HttpUrl.FRAGMENT_ENCODE_SET, "commonIndexOf", HttpUrl.FRAGMENT_ENCODE_SET, "b", HttpUrl.FRAGMENT_ENCODE_SET, "fromIndex", "toIndex", "bytes", "Lokio/ByteString;", "commonIndexOfElement", "targetBytes", "commonPeek", "Lokio/BufferedSource;", "commonRangeEquals", TypedValues.CycleType.S_WAVE_OFFSET, "bytesOffset", HttpUrl.FRAGMENT_ENCODE_SET, "byteCount", "commonRead", "sink", HttpUrl.FRAGMENT_ENCODE_SET, "Lokio/Buffer;", "commonReadAll", "Lokio/Sink;", "commonReadByte", "commonReadByteArray", "commonReadByteString", "commonReadDecimalLong", "commonReadFully", "commonReadHexadecimalUnsignedLong", "commonReadInt", "commonReadIntLe", "commonReadLong", "commonReadLongLe", "commonReadShort", HttpUrl.FRAGMENT_ENCODE_SET, "commonReadShortLe", "commonReadUtf8", HttpUrl.FRAGMENT_ENCODE_SET, "commonReadUtf8CodePoint", "commonReadUtf8Line", "commonReadUtf8LineStrict", "limit", "commonRequest", "commonRequire", "commonSelect", "options", "Lokio/Options;", "commonSkip", "commonTimeout", "Lokio/Timeout;", "commonToString", "okio"}, k = 2, mv = {1, 5, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes4.dex */
public final class _RealBufferedSourceKt {
    public static final long commonRead(buffer $this$commonRead, Buffer sink, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (!(byteCount >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
        }
        if (!(!$this$commonRead.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        if ($this$commonRead.bufferField.size() == 0) {
            long read = $this$commonRead.source.read($this$commonRead.bufferField, 8192L);
            if (read == -1) {
                return -1L;
            }
        }
        long toRead = Math.min(byteCount, $this$commonRead.bufferField.size());
        return $this$commonRead.bufferField.read(sink, toRead);
    }

    public static final boolean commonExhausted(buffer $this$commonExhausted) {
        Intrinsics.checkNotNullParameter($this$commonExhausted, "<this>");
        if (!$this$commonExhausted.closed) {
            return $this$commonExhausted.bufferField.exhausted() && $this$commonExhausted.source.read($this$commonExhausted.bufferField, 8192L) == -1;
        }
        throw new IllegalStateException("closed".toString());
    }

    public static final void commonRequire(buffer $this$commonRequire, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonRequire, "<this>");
        if (!$this$commonRequire.request(byteCount)) {
            throw new EOFException();
        }
    }

    public static final boolean commonRequest(buffer $this$commonRequest, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRequest, "<this>");
        if (!(byteCount >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(byteCount)).toString());
        }
        if (!(!$this$commonRequest.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        while ($this$commonRequest.bufferField.size() < byteCount) {
            if ($this$commonRequest.source.read($this$commonRequest.bufferField, 8192L) == -1) {
                return false;
            }
        }
        return true;
    }

    public static final byte commonReadByte(buffer $this$commonReadByte) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadByte, "<this>");
        $this$commonReadByte.require(1L);
        return $this$commonReadByte.bufferField.readByte();
    }

    public static final ByteString commonReadByteString(buffer $this$commonReadByteString) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
        $this$commonReadByteString.bufferField.writeAll($this$commonReadByteString.source);
        return $this$commonReadByteString.bufferField.readByteString();
    }

    public static final ByteString commonReadByteString(buffer $this$commonReadByteString, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
        $this$commonReadByteString.require(byteCount);
        return $this$commonReadByteString.bufferField.readByteString(byteCount);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:200)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:61)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:103)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:95)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public static final int commonSelect(okio.buffer r8, okio.Options r9) {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "options"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            r0 = 0
            boolean r1 = r8.closed
            r2 = 1
            r1 = r1 ^ r2
            if (r1 == 0) goto L45
        L11:
        L12:
            r1 = r8
            r3 = 0
            okio.Buffer r1 = r1.bufferField
            int r1 = okio.internal._BufferKt.selectPrefix(r1, r9, r2)
            r3 = -1
            switch(r1) {
                case -2: goto L32;
                case -1: goto L31;
                default: goto L1e;
            }
        L1e:
            okio.ByteString[] r2 = r9.getByteStrings()
            r2 = r2[r1]
            int r2 = r2.size()
            r3 = r8
            r4 = 0
            okio.Buffer r3 = r3.bufferField
            long r4 = (long) r2
            r3.skip(r4)
            return r1
        L31:
            return r3
        L32:
            okio.Source r4 = r8.source
            r5 = r8
            r6 = 0
            okio.Buffer r5 = r5.bufferField
            r6 = 8192(0x2000, double:4.0474E-320)
            long r4 = r4.read(r5, r6)
            r6 = -1
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L11
            return r3
        L45:
            r1 = 0
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "closed"
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal._RealBufferedSourceKt.commonSelect(okio.RealBufferedSource, okio.Options):int");
    }

    public static final byte[] commonReadByteArray(buffer $this$commonReadByteArray) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
        $this$commonReadByteArray.bufferField.writeAll($this$commonReadByteArray.source);
        return $this$commonReadByteArray.bufferField.readByteArray();
    }

    public static final byte[] commonReadByteArray(buffer $this$commonReadByteArray, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
        $this$commonReadByteArray.require(byteCount);
        return $this$commonReadByteArray.bufferField.readByteArray(byteCount);
    }

    public static final void commonReadFully(buffer $this$commonReadFully, byte[] sink) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
        Intrinsics.checkNotNullParameter(sink, "sink");
        try {
            $this$commonReadFully.require(sink.length);
            $this$commonReadFully.bufferField.readFully(sink);
        } catch (EOFException e) {
            int offset = 0;
            while ($this$commonReadFully.bufferField.size() > 0) {
                int read = $this$commonReadFully.bufferField.read(sink, offset, (int) $this$commonReadFully.bufferField.size());
                if (read == -1) {
                    throw new AssertionError();
                }
                offset += read;
            }
            throw e;
        }
    }

    public static final int commonRead(buffer $this$commonRead, byte[] sink, int offset, int byteCount) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
        Intrinsics.checkNotNullParameter(sink, "sink");
        _UtilKt.checkOffsetAndCount(sink.length, offset, byteCount);
        if ($this$commonRead.bufferField.size() == 0) {
            long read = $this$commonRead.source.read($this$commonRead.bufferField, 8192L);
            if (read == -1) {
                return -1;
            }
        }
        long b$iv = $this$commonRead.bufferField.size();
        int toRead = (int) Math.min(byteCount, b$iv);
        return $this$commonRead.bufferField.read(sink, offset, toRead);
    }

    public static final void commonReadFully(buffer $this$commonReadFully, Buffer sink, long byteCount) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
        Intrinsics.checkNotNullParameter(sink, "sink");
        try {
            $this$commonReadFully.require(byteCount);
            $this$commonReadFully.bufferField.readFully(sink, byteCount);
        } catch (EOFException e) {
            sink.writeAll($this$commonReadFully.bufferField);
            throw e;
        }
    }

    public static final long commonReadAll(buffer $this$commonReadAll, Sink sink) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonReadAll, "<this>");
        Intrinsics.checkNotNullParameter(sink, "sink");
        long totalBytesWritten = 0;
        while ($this$commonReadAll.source.read($this$commonReadAll.bufferField, 8192L) != -1) {
            long emitByteCount = $this$commonReadAll.bufferField.completeSegmentByteCount();
            if (emitByteCount > 0) {
                totalBytesWritten += emitByteCount;
                sink.write($this$commonReadAll.bufferField, emitByteCount);
            }
        }
        if ($this$commonReadAll.bufferField.size() > 0) {
            long totalBytesWritten2 = totalBytesWritten + $this$commonReadAll.bufferField.size();
            sink.write($this$commonReadAll.bufferField, $this$commonReadAll.bufferField.size());
            return totalBytesWritten2;
        }
        return totalBytesWritten;
    }

    public static final String commonReadUtf8(buffer $this$commonReadUtf8) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
        $this$commonReadUtf8.bufferField.writeAll($this$commonReadUtf8.source);
        return $this$commonReadUtf8.bufferField.readUtf8();
    }

    public static final String commonReadUtf8(buffer $this$commonReadUtf8, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
        $this$commonReadUtf8.require(byteCount);
        return $this$commonReadUtf8.bufferField.readUtf8(byteCount);
    }

    public static final String commonReadUtf8Line(buffer $this$commonReadUtf8Line) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8Line, "<this>");
        long newline = $this$commonReadUtf8Line.indexOf((byte) 10);
        if (newline != -1) {
            return _BufferKt.readUtf8Line($this$commonReadUtf8Line.bufferField, newline);
        }
        if ($this$commonReadUtf8Line.bufferField.size() != 0) {
            return $this$commonReadUtf8Line.readUtf8($this$commonReadUtf8Line.bufferField.size());
        }
        return null;
    }

    public static final String commonReadUtf8LineStrict(buffer $this$commonReadUtf8LineStrict, long limit) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8LineStrict, "<this>");
        if (!(limit >= 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("limit < 0: ", Long.valueOf(limit)).toString());
        }
        long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1;
        byte b = (byte) 10;
        long newline = $this$commonReadUtf8LineStrict.indexOf(b, 0L, scanLength);
        if (newline != -1) {
            return _BufferKt.readUtf8Line($this$commonReadUtf8LineStrict.bufferField, newline);
        }
        if (scanLength < Long.MAX_VALUE && $this$commonReadUtf8LineStrict.request(scanLength) && $this$commonReadUtf8LineStrict.bufferField.getByte(scanLength - 1) == ((byte) 13) && $this$commonReadUtf8LineStrict.request(1 + scanLength) && $this$commonReadUtf8LineStrict.bufferField.getByte(scanLength) == b) {
            return _BufferKt.readUtf8Line($this$commonReadUtf8LineStrict.bufferField, scanLength);
        }
        Buffer data = new Buffer();
        Buffer buffer = $this$commonReadUtf8LineStrict.bufferField;
        long b$iv = $this$commonReadUtf8LineStrict.bufferField.size();
        buffer.copyTo(data, 0L, Math.min(32, b$iv));
        throw new EOFException("\\n not found: limit=" + Math.min($this$commonReadUtf8LineStrict.bufferField.size(), limit) + " content=" + data.readByteString().hex() + Typography.ellipsis);
    }

    public static final int commonReadUtf8CodePoint(buffer $this$commonReadUtf8CodePoint) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8CodePoint, "<this>");
        $this$commonReadUtf8CodePoint.require(1L);
        int b0 = $this$commonReadUtf8CodePoint.bufferField.getByte(0L);
        if ((b0 & 224) == 192) {
            $this$commonReadUtf8CodePoint.require(2L);
        } else if ((b0 & 240) == 224) {
            $this$commonReadUtf8CodePoint.require(3L);
        } else if ((b0 & 248) == 240) {
            $this$commonReadUtf8CodePoint.require(4L);
        }
        return $this$commonReadUtf8CodePoint.bufferField.readUtf8CodePoint();
    }

    public static final short commonReadShort(buffer $this$commonReadShort) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadShort, "<this>");
        $this$commonReadShort.require(2L);
        return $this$commonReadShort.bufferField.readShort();
    }

    public static final short commonReadShortLe(buffer $this$commonReadShortLe) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadShortLe, "<this>");
        $this$commonReadShortLe.require(2L);
        return $this$commonReadShortLe.bufferField.readShortLe();
    }

    public static final int commonReadInt(buffer $this$commonReadInt) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadInt, "<this>");
        $this$commonReadInt.require(4L);
        return $this$commonReadInt.bufferField.readInt();
    }

    public static final int commonReadIntLe(buffer $this$commonReadIntLe) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadIntLe, "<this>");
        $this$commonReadIntLe.require(4L);
        return $this$commonReadIntLe.bufferField.readIntLe();
    }

    public static final long commonReadLong(buffer $this$commonReadLong) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadLong, "<this>");
        $this$commonReadLong.require(8L);
        return $this$commonReadLong.bufferField.readLong();
    }

    public static final long commonReadLongLe(buffer $this$commonReadLongLe) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadLongLe, "<this>");
        $this$commonReadLongLe.require(8L);
        return $this$commonReadLongLe.bufferField.readLongLe();
    }

    public static final long commonReadDecimalLong(buffer $this$commonReadDecimalLong) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadDecimalLong, "<this>");
        $this$commonReadDecimalLong.require(1L);
        for (long pos = 0; $this$commonReadDecimalLong.request(pos + 1); pos++) {
            byte b = $this$commonReadDecimalLong.bufferField.getByte(pos);
            if ((b < ((byte) 48) || b > ((byte) 57)) && (pos != 0 || b != ((byte) 45))) {
                if (pos == 0) {
                    String string = Integer.toString(b, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                    Intrinsics.checkNotNullExpressionValue(string, "java.lang.Integer.toStri…(this, checkRadix(radix))");
                    throw new NumberFormatException(Intrinsics.stringPlus("Expected a digit or '-' but was 0x", string));
                }
                return $this$commonReadDecimalLong.bufferField.readDecimalLong();
            }
        }
        return $this$commonReadDecimalLong.bufferField.readDecimalLong();
    }

    public static final long commonReadHexadecimalUnsignedLong(buffer $this$commonReadHexadecimalUnsignedLong) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonReadHexadecimalUnsignedLong, "<this>");
        $this$commonReadHexadecimalUnsignedLong.require(1L);
        for (int pos = 0; $this$commonReadHexadecimalUnsignedLong.request(pos + 1); pos++) {
            byte b = $this$commonReadHexadecimalUnsignedLong.bufferField.getByte(pos);
            if ((b < ((byte) 48) || b > ((byte) 57)) && ((b < ((byte) 97) || b > ((byte) LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY)) && (b < ((byte) 65) || b > ((byte) 70)))) {
                if (pos == 0) {
                    String string = Integer.toString(b, CharsKt.checkRadix(CharsKt.checkRadix(16)));
                    Intrinsics.checkNotNullExpressionValue(string, "java.lang.Integer.toStri…(this, checkRadix(radix))");
                    throw new NumberFormatException(Intrinsics.stringPlus("Expected leading [0-9a-fA-F] character but was 0x", string));
                }
                return $this$commonReadHexadecimalUnsignedLong.bufferField.readHexadecimalUnsignedLong();
            }
        }
        return $this$commonReadHexadecimalUnsignedLong.bufferField.readHexadecimalUnsignedLong();
    }

    public static final void commonSkip(buffer $this$commonSkip, long byteCount) throws EOFException {
        Intrinsics.checkNotNullParameter($this$commonSkip, "<this>");
        long byteCount2 = byteCount;
        if (!(!$this$commonSkip.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        while (byteCount2 > 0) {
            if ($this$commonSkip.bufferField.size() == 0 && $this$commonSkip.source.read($this$commonSkip.bufferField, 8192L) == -1) {
                throw new EOFException();
            }
            long toSkip = Math.min(byteCount2, $this$commonSkip.bufferField.size());
            $this$commonSkip.bufferField.skip(toSkip);
            byteCount2 -= toSkip;
        }
    }

    public static final long commonIndexOf(buffer $this$commonIndexOf, byte b, long fromIndex, long toIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        if (!(!$this$commonIndexOf.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        if (!(0 <= fromIndex && fromIndex <= toIndex)) {
            throw new IllegalArgumentException(("fromIndex=" + fromIndex + " toIndex=" + toIndex).toString());
        }
        long fromIndex2 = fromIndex;
        while (fromIndex2 < toIndex) {
            long result = $this$commonIndexOf.bufferField.indexOf(b, fromIndex2, toIndex);
            if (result != -1) {
                return result;
            }
            long lastBufferSize = $this$commonIndexOf.bufferField.size();
            if (lastBufferSize < toIndex && $this$commonIndexOf.source.read($this$commonIndexOf.bufferField, 8192L) != -1) {
                fromIndex2 = Math.max(fromIndex2, lastBufferSize);
            }
            return -1L;
        }
        return -1L;
    }

    public static final long commonIndexOf(buffer $this$commonIndexOf, ByteString bytes, long fromIndex) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        long fromIndex2 = fromIndex;
        if (!(!$this$commonIndexOf.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        while (true) {
            long result = $this$commonIndexOf.bufferField.indexOf(bytes, fromIndex2);
            if (result != -1) {
                return result;
            }
            long lastBufferSize = $this$commonIndexOf.bufferField.size();
            if ($this$commonIndexOf.source.read($this$commonIndexOf.bufferField, 8192L) == -1) {
                return -1L;
            }
            fromIndex2 = Math.max(fromIndex2, (lastBufferSize - bytes.size()) + 1);
        }
    }

    public static final long commonIndexOfElement(buffer $this$commonIndexOfElement, ByteString targetBytes, long fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOfElement, "<this>");
        Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
        long fromIndex2 = fromIndex;
        if (!(!$this$commonIndexOfElement.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        while (true) {
            long result = $this$commonIndexOfElement.bufferField.indexOfElement(targetBytes, fromIndex2);
            if (result != -1) {
                return result;
            }
            long lastBufferSize = $this$commonIndexOfElement.bufferField.size();
            if ($this$commonIndexOfElement.source.read($this$commonIndexOfElement.bufferField, 8192L) == -1) {
                return -1L;
            }
            fromIndex2 = Math.max(fromIndex2, lastBufferSize);
        }
    }

    public static final boolean commonRangeEquals(buffer $this$commonRangeEquals, long offset, ByteString bytes, int bytesOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (!(!$this$commonRangeEquals.closed)) {
            throw new IllegalStateException("closed".toString());
        }
        if (offset < 0 || bytesOffset < 0 || byteCount < 0 || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        if (byteCount > 0) {
            int i = 0;
            do {
                int i2 = i;
                i++;
                long bufferOffset = i2 + offset;
                if (!$this$commonRangeEquals.request(1 + bufferOffset) || $this$commonRangeEquals.bufferField.getByte(bufferOffset) != bytes.getByte(bytesOffset + i2)) {
                    return false;
                }
            } while (i < byteCount);
        }
        return true;
    }

    public static final BufferedSource commonPeek(buffer $this$commonPeek) {
        Intrinsics.checkNotNullParameter($this$commonPeek, "<this>");
        return Okio.buffer(new PeekSource($this$commonPeek));
    }

    public static final void commonClose(buffer $this$commonClose) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
        if ($this$commonClose.closed) {
            return;
        }
        $this$commonClose.closed = true;
        $this$commonClose.source.close();
        $this$commonClose.bufferField.clear();
    }

    public static final Timeout commonTimeout(buffer $this$commonTimeout) {
        Intrinsics.checkNotNullParameter($this$commonTimeout, "<this>");
        return $this$commonTimeout.source.getTimeout();
    }

    public static final String commonToString(buffer $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
        return "buffer(" + $this$commonToString.source + ')';
    }
}

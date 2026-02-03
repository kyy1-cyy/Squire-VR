package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import kotlin.text.Typography;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class ISO8601Utils {
    private static final String UTC_ID = "UTC";
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone(UTC_ID);

    public static String format(Date date) {
        return format(date, false, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis) {
        return format(date, millis, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis, TimeZone tz) {
        Calendar calendar = new GregorianCalendar(tz, Locale.US);
        calendar.setTime(date);
        int capacity = "yyyy-MM-ddThh:mm:ss".length();
        StringBuilder formatted = new StringBuilder(capacity + (millis ? ".sss".length() : 0) + (tz.getRawOffset() == 0 ? "Z" : "+hh:mm").length());
        padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        padInt(formatted, calendar.get(5), "dd".length());
        formatted.append('T');
        padInt(formatted, calendar.get(11), "hh".length());
        formatted.append(':');
        padInt(formatted, calendar.get(12), "mm".length());
        formatted.append(':');
        padInt(formatted, calendar.get(13), "ss".length());
        if (millis) {
            formatted.append('.');
            padInt(formatted, calendar.get(14), "sss".length());
        }
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / 60000) / 60);
            int minutes = Math.abs((offset / 60000) % 60);
            formatted.append(offset >= 0 ? '+' : '-');
            padInt(formatted, hours, "hh".length());
            formatted.append(':');
            padInt(formatted, minutes, "mm".length());
        } else {
            formatted.append('Z');
        }
        return formatted.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:112:0x022a  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x022c  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0067 A[Catch: IllegalArgumentException -> 0x0052, NumberFormatException -> 0x0057, IndexOutOfBoundsException -> 0x005c, TryCatch #7 {NumberFormatException -> 0x0057, IllegalArgumentException -> 0x0052, IndexOutOfBoundsException -> 0x005c, blocks: (B:12:0x003a, B:14:0x0040, B:24:0x0067, B:26:0x0078, B:27:0x007a, B:29:0x0087, B:31:0x008c, B:33:0x0092, B:38:0x009e, B:44:0x00b1, B:46:0x00b9, B:59:0x00ea), top: B:121:0x003a }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00e2 A[Catch: IllegalArgumentException -> 0x0219, NumberFormatException -> 0x021e, IndexOutOfBoundsException -> 0x0223, TRY_LEAVE, TryCatch #4 {IllegalArgumentException -> 0x0219, IndexOutOfBoundsException -> 0x0223, NumberFormatException -> 0x021e, blocks: (B:3:0x0005, B:5:0x0017, B:6:0x0019, B:8:0x0025, B:9:0x0027, B:54:0x00dc, B:56:0x00e2, B:66:0x00ff), top: B:123:0x0005 }] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0207 A[Catch: IllegalArgumentException -> 0x0213, NumberFormatException -> 0x0215, IndexOutOfBoundsException -> 0x0217, TryCatch #5 {IndexOutOfBoundsException -> 0x0217, NumberFormatException -> 0x0215, IllegalArgumentException -> 0x0213, blocks: (B:91:0x01d4, B:71:0x0122, B:75:0x0142, B:77:0x0150, B:89:0x01cf, B:80:0x0160, B:82:0x0182, B:85:0x0197, B:86:0x01c1, B:74:0x012f, B:68:0x0108, B:69:0x011f, B:93:0x0207, B:94:0x0212), top: B:122:0x00e0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Date parse(String date, ParsePosition pos) throws ParseException {
        Exception fail;
        String msg;
        int offset;
        TimeZone timezone;
        int offset2;
        char c;
        try {
            int offset3 = pos.getIndex();
            int offset4 = offset3 + 4;
            int year = parseInt(date, offset3, offset4);
            if (checkOffset(date, offset4, '-')) {
                offset4++;
            }
            int offset5 = offset4 + 2;
            int month = parseInt(date, offset4, offset5);
            if (checkOffset(date, offset5, '-')) {
                offset5++;
            }
            int offset6 = offset5 + 2;
            int day = parseInt(date, offset5, offset6);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(date, offset6, 'T');
            if (hasT) {
                if (hasT) {
                }
                if (date.length() > offset6) {
                }
            } else {
                try {
                    if (date.length() <= offset6) {
                        Calendar calendar = new GregorianCalendar(year, month - 1, day);
                        calendar.setLenient(false);
                        pos.setIndex(offset6);
                        return calendar.getTime();
                    }
                    if (hasT) {
                        int offset7 = offset6 + 1;
                        int offset8 = offset7 + 2;
                        hour = parseInt(date, offset7, offset8);
                        if (checkOffset(date, offset8, ':')) {
                            offset8++;
                        }
                        int offset9 = offset8 + 2;
                        minutes = parseInt(date, offset8, offset9);
                        if (!checkOffset(date, offset9, ':')) {
                            offset6 = offset9;
                        } else {
                            offset6 = offset9 + 1;
                        }
                        if (date.length() > offset6 && (c = date.charAt(offset6)) != 'Z' && c != '+' && c != '-') {
                            int offset10 = offset6 + 2;
                            int seconds2 = parseInt(date, offset6, offset10);
                            seconds = (seconds2 <= 59 || seconds2 >= 63) ? seconds2 : 59;
                            if (!checkOffset(date, offset10, '.')) {
                                offset6 = offset10;
                            } else {
                                int offset11 = offset10 + 1;
                                offset6 = indexOfNonDigit(date, offset11 + 1);
                                int parseEndOffset = Math.min(offset6, offset11 + 3);
                                int fraction = parseInt(date, offset11, parseEndOffset);
                                switch (parseEndOffset - offset11) {
                                    case 1:
                                        milliseconds = fraction * 100;
                                        break;
                                    case 2:
                                        milliseconds = fraction * 10;
                                        break;
                                    default:
                                        milliseconds = fraction;
                                        break;
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e = e;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                        msg = "(" + fail.getClass().getName() + ")";
                    }
                    ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex.initCause(fail);
                    throw ex;
                } catch (IllegalArgumentException e2) {
                    e = e2;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                    }
                    ParseException ex2 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex2.initCause(fail);
                    throw ex2;
                } catch (IndexOutOfBoundsException e3) {
                    e = e3;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                    }
                    ParseException ex22 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex22.initCause(fail);
                    throw ex22;
                }
                try {
                    if (date.length() > offset6) {
                        throw new IllegalArgumentException("No time zone indicator");
                    }
                    char timezoneIndicator = date.charAt(offset6);
                    if (timezoneIndicator == 'Z') {
                        timezone = TIMEZONE_UTC;
                        offset2 = offset6 + 1;
                    } else if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                        String timezoneOffset = date.substring(offset6);
                        String timezoneOffset2 = timezoneOffset.length() >= 5 ? timezoneOffset : timezoneOffset + "00";
                        int offset12 = offset6 + timezoneOffset2.length();
                        if ("+0000".equals(timezoneOffset2) || "+00:00".equals(timezoneOffset2)) {
                            offset = offset12;
                            timezone = TIMEZONE_UTC;
                            offset2 = offset;
                        } else {
                            String timezoneId = "GMT" + timezoneOffset2;
                            timezone = TimeZone.getTimeZone(timezoneId);
                            String act = timezone.getID();
                            if (act.equals(timezoneId)) {
                                offset = offset12;
                            } else {
                                offset = offset12;
                                String cleaned = act.replace(":", HttpUrl.FRAGMENT_ENCODE_SET);
                                if (!cleaned.equals(timezoneId)) {
                                    throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                                }
                            }
                            offset2 = offset;
                        }
                    } else {
                        throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                    }
                    Calendar calendar2 = new GregorianCalendar(timezone);
                    calendar2.setLenient(false);
                    calendar2.set(1, year);
                    calendar2.set(2, month - 1);
                    calendar2.set(5, day);
                    calendar2.set(11, hour);
                    calendar2.set(12, minutes);
                    calendar2.set(13, seconds);
                    calendar2.set(14, milliseconds);
                    pos.setIndex(offset2);
                    return calendar2.getTime();
                } catch (IndexOutOfBoundsException e4) {
                    e = e4;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                    }
                    ParseException ex222 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex222.initCause(fail);
                    throw ex222;
                } catch (NumberFormatException e5) {
                    e = e5;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                    }
                    ParseException ex2222 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex2222.initCause(fail);
                    throw ex2222;
                } catch (IllegalArgumentException e6) {
                    e = e6;
                    fail = e;
                    if (date == null) {
                    }
                    msg = fail.getMessage();
                    if (msg == null) {
                    }
                    ParseException ex22222 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex22222.initCause(fail);
                    throw ex22222;
                }
            }
        } catch (IllegalArgumentException e7) {
            e = e7;
        } catch (IndexOutOfBoundsException e8) {
            e = e8;
        } catch (NumberFormatException e9) {
            e = e9;
        }
        String input = date == null ? null : Typography.quote + date + Typography.quote;
        msg = fail.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "(" + fail.getClass().getName() + ")";
        }
        ParseException ex222222 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
        ex222222.initCause(fail);
        throw ex222222;
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int digit = beginIndex;
        int result = 0;
        if (digit < endIndex) {
            int i = digit + 1;
            int digit2 = Character.digit(value.charAt(digit), 10);
            if (digit2 < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = -digit2;
            digit = i;
        }
        while (digit < endIndex) {
            int i2 = digit + 1;
            int digit3 = Character.digit(value.charAt(digit), 10);
            if (digit3 < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = (result * 10) - digit3;
            digit = i2;
        }
        return -result;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }

    private static int indexOfNonDigit(String string, int offset) {
        for (int i = offset; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') {
                return i;
            }
        }
        int i2 = string.length();
        return i2;
    }
}

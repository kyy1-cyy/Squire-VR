package com.google.android.material.datepicker;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.util.ObjectsCompat;
import java.util.Arrays;
import java.util.Objects;

/* loaded from: classes.dex */
public final class CalendarConstraints implements Parcelable {
    public static final Parcelable.Creator<CalendarConstraints> CREATOR = new Parcelable.Creator<CalendarConstraints>() { // from class: com.google.android.material.datepicker.CalendarConstraints.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CalendarConstraints createFromParcel(Parcel source) {
            Month start = (Month) source.readParcelable(Month.class.getClassLoader());
            Month end = (Month) source.readParcelable(Month.class.getClassLoader());
            Month openAt = (Month) source.readParcelable(Month.class.getClassLoader());
            DateValidator validator = (DateValidator) source.readParcelable(DateValidator.class.getClassLoader());
            int firstDayOfWeek = source.readInt();
            return new CalendarConstraints(start, end, validator, openAt, firstDayOfWeek);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CalendarConstraints[] newArray(int size) {
            return new CalendarConstraints[size];
        }
    };
    private final Month end;
    private final int firstDayOfWeek;
    private final int monthSpan;
    private Month openAt;
    private final Month start;
    private final DateValidator validator;
    private final int yearSpan;

    public interface DateValidator extends Parcelable {
        boolean isValid(long j);
    }

    private CalendarConstraints(Month start, Month end, DateValidator validator, Month openAt, int firstDayOfWeek) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        Objects.requireNonNull(validator, "validator cannot be null");
        this.start = start;
        this.end = end;
        this.openAt = openAt;
        this.firstDayOfWeek = firstDayOfWeek;
        this.validator = validator;
        if (openAt != null && start.compareTo(openAt) > 0) {
            throw new IllegalArgumentException("start Month cannot be after current Month");
        }
        if (openAt != null && openAt.compareTo(end) > 0) {
            throw new IllegalArgumentException("current Month cannot be after end Month");
        }
        if (firstDayOfWeek < 0 || firstDayOfWeek > UtcDates.getUtcCalendar().getMaximum(7)) {
            throw new IllegalArgumentException("firstDayOfWeek is not valid");
        }
        this.monthSpan = start.monthsUntil(end) + 1;
        this.yearSpan = (end.year - start.year) + 1;
    }

    boolean isWithinBounds(long date) {
        return this.start.getDay(1) <= date && date <= this.end.getDay(this.end.daysInMonth);
    }

    public DateValidator getDateValidator() {
        return this.validator;
    }

    Month getStart() {
        return this.start;
    }

    Month getEnd() {
        return this.end;
    }

    Month getOpenAt() {
        return this.openAt;
    }

    void setOpenAt(Month openAt) {
        this.openAt = openAt;
    }

    int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    int getMonthSpan() {
        return this.monthSpan;
    }

    int getYearSpan() {
        return this.yearSpan;
    }

    public long getStartMs() {
        return this.start.timeInMillis;
    }

    public long getEndMs() {
        return this.end.timeInMillis;
    }

    public Long getOpenAtMs() {
        if (this.openAt == null) {
            return null;
        }
        return Long.valueOf(this.openAt.timeInMillis);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CalendarConstraints)) {
            return false;
        }
        CalendarConstraints that = (CalendarConstraints) o;
        return this.start.equals(that.start) && this.end.equals(that.end) && ObjectsCompat.equals(this.openAt, that.openAt) && this.firstDayOfWeek == that.firstDayOfWeek && this.validator.equals(that.validator);
    }

    public int hashCode() {
        Object[] hashedFields = {this.start, this.end, this.openAt, Integer.valueOf(this.firstDayOfWeek), this.validator};
        return Arrays.hashCode(hashedFields);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.start, 0);
        dest.writeParcelable(this.end, 0);
        dest.writeParcelable(this.openAt, 0);
        dest.writeParcelable(this.validator, 0);
        dest.writeInt(this.firstDayOfWeek);
    }

    Month clamp(Month month) {
        if (month.compareTo(this.start) < 0) {
            return this.start;
        }
        if (month.compareTo(this.end) > 0) {
            return this.end;
        }
        return month;
    }

    public static final class Builder {
        private static final String DEEP_COPY_VALIDATOR_KEY = "DEEP_COPY_VALIDATOR_KEY";
        private long end;
        private int firstDayOfWeek;
        private Long openAt;
        private long start;
        private DateValidator validator;
        static final long DEFAULT_START = UtcDates.canonicalYearMonthDay(Month.create(1900, 0).timeInMillis);
        static final long DEFAULT_END = UtcDates.canonicalYearMonthDay(Month.create(2100, 11).timeInMillis);

        public Builder() {
            this.start = DEFAULT_START;
            this.end = DEFAULT_END;
            this.validator = DateValidatorPointForward.from(Long.MIN_VALUE);
        }

        Builder(CalendarConstraints clone) {
            this.start = DEFAULT_START;
            this.end = DEFAULT_END;
            this.validator = DateValidatorPointForward.from(Long.MIN_VALUE);
            this.start = clone.start.timeInMillis;
            this.end = clone.end.timeInMillis;
            this.openAt = Long.valueOf(clone.openAt.timeInMillis);
            this.firstDayOfWeek = clone.firstDayOfWeek;
            this.validator = clone.validator;
        }

        public Builder setStart(long month) {
            this.start = month;
            return this;
        }

        public Builder setEnd(long month) {
            this.end = month;
            return this;
        }

        public Builder setOpenAt(long month) {
            this.openAt = Long.valueOf(month);
            return this;
        }

        public Builder setFirstDayOfWeek(int firstDayOfWeek) {
            this.firstDayOfWeek = firstDayOfWeek;
            return this;
        }

        public Builder setValidator(DateValidator validator) {
            Objects.requireNonNull(validator, "validator cannot be null");
            this.validator = validator;
            return this;
        }

        public CalendarConstraints build() {
            Bundle deepCopyBundle = new Bundle();
            deepCopyBundle.putParcelable(DEEP_COPY_VALIDATOR_KEY, this.validator);
            return new CalendarConstraints(Month.create(this.start), Month.create(this.end), (DateValidator) deepCopyBundle.getParcelable(DEEP_COPY_VALIDATOR_KEY), this.openAt == null ? null : Month.create(this.openAt.longValue()), this.firstDayOfWeek);
        }
    }
}

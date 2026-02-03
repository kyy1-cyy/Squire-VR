package com.google.android.material.datepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.util.Pair;
import com.google.android.material.R;
import com.google.android.material.timepicker.TimeModel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes.dex */
class MonthAdapter extends BaseAdapter {
    private static final int NO_DAY_NUMBER = -1;
    final CalendarConstraints calendarConstraints;
    CalendarStyle calendarStyle;
    final DateSelector<?> dateSelector;
    final DayViewDecorator dayViewDecorator;
    final Month month;
    private Collection<Long> previouslySelectedDates;
    static final int MAXIMUM_WEEKS = UtcDates.getUtcCalendar().getMaximum(4);
    private static final int MAXIMUM_GRID_CELLS = (UtcDates.getUtcCalendar().getMaximum(5) + UtcDates.getUtcCalendar().getMaximum(7)) - 1;

    MonthAdapter(Month month, DateSelector<?> dateSelector, CalendarConstraints calendarConstraints, DayViewDecorator dayViewDecorator) {
        this.month = month;
        this.dateSelector = dateSelector;
        this.calendarConstraints = calendarConstraints;
        this.dayViewDecorator = dayViewDecorator;
        this.previouslySelectedDates = dateSelector.getSelectedDays();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.Adapter
    public Long getItem(int position) {
        if (position < firstPositionInMonth() || position > lastPositionInMonth()) {
            return null;
        }
        return Long.valueOf(this.month.getDay(positionToDay(position)));
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position / this.month.daysInWeek;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return MAXIMUM_GRID_CELLS;
    }

    @Override // android.widget.Adapter
    public TextView getView(int position, View convertView, ViewGroup parent) {
        initializeStyles(parent.getContext());
        TextView dayTextView = (TextView) convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            dayTextView = (TextView) layoutInflater.inflate(R.layout.mtrl_calendar_day, parent, false);
        }
        int offsetPosition = position - firstPositionInMonth();
        int dayNumber = -1;
        if (offsetPosition < 0 || offsetPosition >= this.month.daysInMonth) {
            dayTextView.setVisibility(8);
            dayTextView.setEnabled(false);
        } else {
            dayNumber = offsetPosition + 1;
            dayTextView.setTag(this.month);
            Locale locale = dayTextView.getResources().getConfiguration().locale;
            dayTextView.setText(String.format(locale, TimeModel.NUMBER_FORMAT, Integer.valueOf(dayNumber)));
            dayTextView.setVisibility(0);
            dayTextView.setEnabled(true);
        }
        Long date = getItem(position);
        if (date == null) {
            return dayTextView;
        }
        updateSelectedState(dayTextView, date.longValue(), dayNumber);
        return dayTextView;
    }

    public void updateSelectedStates(MaterialCalendarGridView monthGrid) {
        for (Long date : this.previouslySelectedDates) {
            updateSelectedStateForDate(monthGrid, date.longValue());
        }
        if (this.dateSelector != null) {
            for (Long date2 : this.dateSelector.getSelectedDays()) {
                updateSelectedStateForDate(monthGrid, date2.longValue());
            }
            this.previouslySelectedDates = this.dateSelector.getSelectedDays();
        }
    }

    private void updateSelectedStateForDate(MaterialCalendarGridView monthGrid, long date) {
        if (Month.create(date).equals(this.month)) {
            int day = this.month.getDayOfMonth(date);
            updateSelectedState((TextView) monthGrid.getChildAt(monthGrid.getAdapter().dayToPosition(day) - monthGrid.getFirstVisiblePosition()), date, day);
        }
    }

    private void updateSelectedState(TextView dayTextView, long date, int dayNumber) {
        boolean selected;
        CalendarItemStyle style;
        if (dayTextView == null) {
            return;
        }
        Context context = dayTextView.getContext();
        String contentDescription = getDayContentDescription(context, date);
        dayTextView.setContentDescription(contentDescription);
        boolean valid = this.calendarConstraints.getDateValidator().isValid(date);
        if (valid) {
            dayTextView.setEnabled(true);
            boolean selected2 = isSelected(date);
            dayTextView.setSelected(selected2);
            if (selected2) {
                selected = selected2;
                style = this.calendarStyle.selectedDay;
            } else if (isToday(date)) {
                selected = selected2;
                style = this.calendarStyle.todayDay;
            } else {
                selected = selected2;
                style = this.calendarStyle.day;
            }
        } else {
            dayTextView.setEnabled(false);
            selected = false;
            style = this.calendarStyle.invalidDay;
        }
        if (this.dayViewDecorator != null && dayNumber != -1) {
            int year = this.month.year;
            int month = this.month.month;
            ColorStateList backgroundColorOverride = this.dayViewDecorator.getBackgroundColor(context, year, month, dayNumber, valid, selected);
            style.styleItem(dayTextView, backgroundColorOverride);
            Drawable drawableLeft = this.dayViewDecorator.getCompoundDrawableLeft(context, year, month, dayNumber, valid, selected);
            boolean z = selected;
            Drawable drawableTop = this.dayViewDecorator.getCompoundDrawableTop(context, year, month, dayNumber, valid, z);
            Drawable drawableRight = this.dayViewDecorator.getCompoundDrawableRight(context, year, month, dayNumber, valid, z);
            Drawable drawableBottom = this.dayViewDecorator.getCompoundDrawableBottom(context, year, month, dayNumber, valid, selected);
            dayTextView.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
            CharSequence decoratorContentDescription = this.dayViewDecorator.getContentDescription(context, year, month, dayNumber, valid, selected, contentDescription);
            dayTextView.setContentDescription(decoratorContentDescription);
            return;
        }
        style.styleItem(dayTextView);
    }

    private String getDayContentDescription(Context context, long date) {
        return DateStrings.getDayContentDescription(context, date, isToday(date), isStartOfRange(date), isEndOfRange(date));
    }

    private boolean isToday(long date) {
        return UtcDates.getTodayCalendar().getTimeInMillis() == date;
    }

    boolean isStartOfRange(long date) {
        for (Pair<Long, Long> range : this.dateSelector.getSelectedRanges()) {
            if (range.first != null && range.first.longValue() == date) {
                return true;
            }
        }
        return false;
    }

    boolean isEndOfRange(long date) {
        for (Pair<Long, Long> range : this.dateSelector.getSelectedRanges()) {
            if (range.second != null && range.second.longValue() == date) {
                return true;
            }
        }
        return false;
    }

    private boolean isSelected(long date) {
        Iterator<Long> it = this.dateSelector.getSelectedDays().iterator();
        while (it.hasNext()) {
            long selectedDay = it.next().longValue();
            if (UtcDates.canonicalYearMonthDay(date) == UtcDates.canonicalYearMonthDay(selectedDay)) {
                return true;
            }
        }
        return false;
    }

    private void initializeStyles(Context context) {
        if (this.calendarStyle == null) {
            this.calendarStyle = new CalendarStyle(context);
        }
    }

    int firstPositionInMonth() {
        return this.month.daysFromStartOfWeekToFirstOfMonth(this.calendarConstraints.getFirstDayOfWeek());
    }

    int lastPositionInMonth() {
        return (firstPositionInMonth() + this.month.daysInMonth) - 1;
    }

    int positionToDay(int position) {
        return (position - firstPositionInMonth()) + 1;
    }

    int dayToPosition(int day) {
        int offsetFromFirst = day - 1;
        return firstPositionInMonth() + offsetFromFirst;
    }

    boolean withinMonth(int position) {
        return position >= firstPositionInMonth() && position <= lastPositionInMonth();
    }

    boolean isFirstInRow(int position) {
        return position % this.month.daysInWeek == 0;
    }

    boolean isLastInRow(int position) {
        return (position + 1) % this.month.daysInWeek == 0;
    }
}

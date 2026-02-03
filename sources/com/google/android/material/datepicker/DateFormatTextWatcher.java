package com.google.android.material.datepicker;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.google.android.material.R;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import kotlin.text.Typography;

/* loaded from: classes.dex */
abstract class DateFormatTextWatcher extends TextWatcherAdapter {
    private static final int VALIDATION_DELAY = 1000;
    private final CalendarConstraints constraints;
    private final DateFormat dateFormat;
    private final String outOfRange;
    private final Runnable setErrorCallback;
    private Runnable setRangeErrorCallback;
    private final TextInputLayout textInputLayout;

    abstract void onValidDate(Long l);

    DateFormatTextWatcher(final String formatHint, DateFormat dateFormat, TextInputLayout textInputLayout, CalendarConstraints constraints) {
        this.dateFormat = dateFormat;
        this.textInputLayout = textInputLayout;
        this.constraints = constraints;
        this.outOfRange = textInputLayout.getContext().getString(R.string.mtrl_picker_out_of_range);
        this.setErrorCallback = new Runnable() { // from class: com.google.android.material.datepicker.DateFormatTextWatcher$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m57x5657fb8e(formatHint);
            }
        };
    }

    /* renamed from: lambda$new$0$com-google-android-material-datepicker-DateFormatTextWatcher, reason: not valid java name */
    /* synthetic */ void m57x5657fb8e(String formatHint) {
        TextInputLayout textLayout = this.textInputLayout;
        DateFormat df = this.dateFormat;
        Context context = textLayout.getContext();
        String invalidFormat = context.getString(R.string.mtrl_picker_invalid_format);
        String useLine = String.format(context.getString(R.string.mtrl_picker_invalid_format_use), sanitizeDateString(formatHint));
        String exampleLine = String.format(context.getString(R.string.mtrl_picker_invalid_format_example), sanitizeDateString(df.format(new Date(UtcDates.getTodayCalendar().getTimeInMillis()))));
        textLayout.setError(invalidFormat + "\n" + useLine + "\n" + exampleLine);
        onInvalidDate();
    }

    void onInvalidDate() {
    }

    @Override // com.google.android.material.internal.TextWatcherAdapter, android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) throws ParseException {
        this.textInputLayout.removeCallbacks(this.setErrorCallback);
        this.textInputLayout.removeCallbacks(this.setRangeErrorCallback);
        this.textInputLayout.setError(null);
        onValidDate(null);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        try {
            Date date = this.dateFormat.parse(s.toString());
            this.textInputLayout.setError(null);
            long milliseconds = date.getTime();
            if (this.constraints.getDateValidator().isValid(milliseconds) && this.constraints.isWithinBounds(milliseconds)) {
                onValidDate(Long.valueOf(date.getTime()));
            } else {
                this.setRangeErrorCallback = createRangeErrorCallback(milliseconds);
                runValidation(this.textInputLayout, this.setRangeErrorCallback);
            }
        } catch (ParseException e) {
            runValidation(this.textInputLayout, this.setErrorCallback);
        }
    }

    private Runnable createRangeErrorCallback(final long milliseconds) {
        return new Runnable() { // from class: com.google.android.material.datepicker.DateFormatTextWatcher$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m56x14d77527(milliseconds);
            }
        };
    }

    /* renamed from: lambda$createRangeErrorCallback$1$com-google-android-material-datepicker-DateFormatTextWatcher, reason: not valid java name */
    /* synthetic */ void m56x14d77527(long milliseconds) {
        String dateString = DateStrings.getDateString(milliseconds);
        this.textInputLayout.setError(String.format(this.outOfRange, sanitizeDateString(dateString)));
        onInvalidDate();
    }

    private String sanitizeDateString(String dateString) {
        return dateString.replace(' ', Typography.nbsp);
    }

    public void runValidation(View view, Runnable validation) {
        view.postDelayed(validation, 1000L);
    }
}

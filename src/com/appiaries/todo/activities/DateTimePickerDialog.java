//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.appiaries.todo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerDialog extends Dialog {

    public interface DateTimePickerListener {
        public void onSet(Date date);
        public void onCancel();
    }

    private DateTimePickerListener mDateTimePickerListener;
	private TimePicker mTimePicker;
	private DatePicker mDatePicker;

	public DateTimePickerDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

    public void setDateTimePickerListener(DateTimePickerListener listener) {
        this.mDateTimePickerListener = listener;
    }

    private void setupView() {
        setContentView(R.layout.datepicker_view);

        mTimePicker = (TimePicker) findViewById(R.id.picker_time);
        mDatePicker = (DatePicker) findViewById(R.id.picker_date);

        Button selectButton = (Button) findViewById(R.id.button_select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimePickerListener.onSet(getSelectedDate());
                dismiss();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimePickerListener.onCancel();
                dismiss();
            }
        });

        setTitle(R.string.datetime_picker__title);

        Calendar cal = Calendar.getInstance();
        mDatePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

	private Date getSelectedDate() {
		String dateString = String.format("%s/%02d/%02d %02d:%02d",
                mDatePicker.getYear(),
                mDatePicker.getMonth() + 1,
                mDatePicker.getDayOfMonth(),
                mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
		
		Date selectedDate = null;
		try {
			selectedDate = sdf.parse(dateString);
		} catch (ParseException ignored) {}
		
		return selectedDate;
	}

}

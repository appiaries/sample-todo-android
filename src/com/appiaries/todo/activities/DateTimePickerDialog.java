package com.appiaries.todo.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appiaries.todo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerDialog extends Dialog {

	private DateTimePickerListener dateTimePickerListener;
	private TimePicker timePicker;
	private DatePicker datePicker;
	private Date defaultDate;
	
	public DateTimePickerDialog(Context context, Date defaultDate) {
		super(context);
		this.defaultDate = defaultDate;
	}
	
	public DateTimePickerDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datepicker_view);		

		timePicker = (TimePicker) findViewById(R.id.timePicker);
		datePicker = (DatePicker) findViewById(R.id.datePicker);

		Button btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {								
				dateTimePickerListener.onSet(getSelectedDate());
				dismiss();
			}
		});

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateTimePickerListener.onCancel();
				dismiss();
			}
		});
		
		setTitle("Select Date & Time");
		
		Calendar cal = Calendar.getInstance();
		
		datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
	}
	
	private Date getSelectedDate()
	{
		String dateString = String.format("%s/%02d/%02d %02d:%02d",
				datePicker.getYear(), 
				datePicker.getMonth() + 1,
				datePicker.getDayOfMonth(), 
				timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		
		Date selectedDate = null;
		
		try {
			selectedDate = sdf.parse(dateString);
		} catch (ParseException e) {					
		}
		
		return selectedDate;
	}
		
	// Used to convert 24hr format to 12hr format with AM/PM values
	/*private String getTimeString(int hours, int mins) {

		Log.d("24hour Time", hours + ":" + mins);

		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";

		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		String aTime = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();

		return aTime;
	}*/

	public void setDateTimePickerListener(DateTimePickerListener listener) {
		this.dateTimePickerListener = listener;
	}

	public interface DateTimePickerListener {
		public void onSet(Date date);

		public void onCancel();
	}
}

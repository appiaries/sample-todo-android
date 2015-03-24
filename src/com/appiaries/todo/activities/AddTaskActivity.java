package com.appiaries.todo.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.appiaries.todo.R;
import com.appiaries.todo.activities.DateTimePickerDialog.DateTimePickerListener;
import com.appiaries.todo.common.APIHelper;
import com.appiaries.todo.common.TextHelper;
import com.appiaries.todo.managers.TaskManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AddTaskActivity extends BaseActivity {

	String screenTag = "AddTaskActivity";
	private Date selectedDate;
	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);

		// Get Category Date from DailyList
		Intent myIntent = getIntent();
		long categoryDateInMillis = myIntent.getExtras()
				.getLong("categoryDate");

		// Set category Date as selectedDate
		Date categoryDate = TextHelper.getDate(categoryDateInMillis);
		Calendar cal = Calendar.getInstance();
		cal.setTime(categoryDate);

		Calendar calNew = Calendar.getInstance();
		calNew.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

		selectedDate = calNew.getTime();

		final TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		final TextView txtTitle = (TextView) findViewById(R.id.txtTaskTitle);
		final TextView txtMemo = (TextView) findViewById(R.id.txtMemo);

		Button btnChooseDate = (Button) findViewById(R.id.btnChooseDate);
		Button btnSave = (Button) findViewById(R.id.btnSave);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);

		tvDateTime.setText(TextHelper.getFormatedDateString(selectedDate));

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, Object> data = new HashMap<String, Object>();

				if (selectedDate != null) {
					data.put("scheduled_at", selectedDate.getTime());
				} else {
					Calendar c = Calendar.getInstance();
					data.put("scheduled_at", c.getTimeInMillis());
				}

				if (txtTitle.getText() != null
						&& !TextUtils.isEmpty(txtTitle.getText())) {
					data.put("title", txtTitle.getText().toString());
				}

				if (txtMemo != null && !TextUtils.isEmpty(txtMemo.getText())) {
					data.put("body", txtMemo.getText().toString());
				}

				data.put("category_id", "");

				if (APIHelper.getUserId(getApplicationContext()) != null) {
					data.put("user_Id",
							APIHelper.getUserId(getApplicationContext()));
					data.put("type", 0);
					data.put("status", 0);
				}

				new createTaskAsyncTask().execute(data);
			}
		});

		btnChooseDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Show DateTime Picker dialog
				final DateTimePickerDialog dialog = new DateTimePickerDialog(
						AddTaskActivity.this);
				dialog.setDateTimePickerListener(new DateTimePickerListener() {

					@Override
					public void onSet(Date date) {
						selectedDate = date;
						tvDateTime.setText(TextHelper
								.getFormatedDateString(date));
					}

					@Override
					public void onCancel() {

					}
				});

				dialog.show();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private class createTaskAsyncTask extends
			AsyncTask<HashMap<String, Object>, Void, Integer> {

		@Override
		protected Integer doInBackground(HashMap<String, Object>... params) {
			try {
				return TaskManager.getInstance().createTask(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(AddTaskActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressBar.dismiss();
			super.onPostExecute(result);
			if (result == 201) {
				Log.d(screenTag, "create successful");
				Intent resultIntent = new Intent();
				setResult(1, resultIntent);
				finish();
			} else {
				Log.d(screenTag, "create fail");
			}
		}

	}
}

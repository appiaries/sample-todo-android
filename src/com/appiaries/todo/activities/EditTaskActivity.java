package com.appiaries.todo.activities;

import java.util.Date;
import java.util.HashMap;

import com.appiaries.todo.R;
import com.appiaries.todo.activities.DateTimePickerDialog.DateTimePickerListener;
import com.appiaries.todo.common.TextHelper;
import com.appiaries.todo.jsonmodels.Task;
import com.appiaries.todo.managers.TaskManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditTaskActivity extends BaseActivity {

	String screenTag = "EditTaskActivity";
	private Date selectedDate;
	Task task;
	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);

		Bundle bundle = getIntent().getExtras();
		task = (Task) bundle.getSerializable("task");

		final TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		final EditText edtTaskTitle = (EditText) findViewById(R.id.txtTaskTitle);
		final EditText edtMemo = (EditText) findViewById(R.id.txtMemo);
		final Button btnDateTime = (Button) findViewById(R.id.btnChooseDate);
		final Button btnSave = (Button) findViewById(R.id.btnSave);
		final Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Date date = task.getScheduledAt();
		String strScheduledAt = date.toString();

		tvDateTime.setText(strScheduledAt);
		edtTaskTitle.setText(task.getTitle());
		edtMemo.setText(task.getText());

		edtTaskTitle.setClickable(false);
		edtTaskTitle.setFocusable(false);
		edtTaskTitle.setFocusableInTouchMode(false);

		edtTaskTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(screenTag, "task title on clicked");
				edtTaskTitle.setClickable(true);
				edtTaskTitle.setFocusable(true);
				edtTaskTitle.setFocusableInTouchMode(true);
			}
		});

		edtMemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edtMemo.setCursorVisible(true);
				edtMemo.setFocusable(true);
				edtMemo.setFocusableInTouchMode(true);
			}
		});

		btnDateTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final DateTimePickerDialog dialog = new DateTimePickerDialog(
						EditTaskActivity.this);
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

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(screenTag, "btn save clicked");
				String title = "";
				String memo = "";

				long date = 0;
				if (selectedDate != null) {
					date = selectedDate.getTime();
				}

				if (edtTaskTitle.getText() != null) {
					title = edtTaskTitle.getText().toString();
				}

				if (edtMemo.getText() != null) {
					memo = edtMemo.getText().toString();
				}

				HashMap<String, Object> data = new HashMap<String, Object>();
				if (!title.equals("")) {
					data.put("title", title);
				}

				if (!memo.equals("")) {
					data.put("body", memo);
				}

				if (date != 0) {
					data.put("scheduled_at", date);
				}

				if (data != null) {
					new UpdateTaskAsynTask().execute(data);
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private class UpdateTaskAsynTask extends
			AsyncTask<HashMap<String, Object>, Void, Integer> {

		@Override
		protected Integer doInBackground(HashMap<String, Object>... params) {

			HashMap<String, Object> data = params[0];

			try {
				return TaskManager.getInstance().updateTask(task.getId(), data);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(EditTaskActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.dismiss();
			if (result == 204) {
				Log.d(screenTag, "update successful");
				Intent resultIntent = new Intent();
				setResult(1, resultIntent);
				finish();
			} else {
				Log.d(screenTag, "update fail");
			}

		}

	}
}

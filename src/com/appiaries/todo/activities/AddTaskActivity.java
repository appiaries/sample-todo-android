//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.todo.R;
import com.appiaries.todo.activities.DateTimePickerDialog.DateTimePickerListener;
import com.appiaries.todo.common.Constants;
import com.appiaries.todo.common.PreferenceHelper;
import com.appiaries.todo.models.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddTaskActivity extends BaseActivity {

	private Date mSelectedDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

    private void setupView() {
        setContentView(R.layout.activity_add_task);

        // Set date as mSelectedDate
        Calendar calSectionDate = Calendar.getInstance(Locale.ENGLISH); //FIXME:
        long msec = getIntent().getExtras().getLong(Constants.EXTRA_KEY_DATE);
        calSectionDate.setTimeInMillis(msec);
        Date date = calSectionDate.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar calNew = Calendar.getInstance();
        calNew.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        mSelectedDate = calNew.getTime();

        final TextView textDatetime = (TextView) findViewById(R.id.text_datetime);
        final EditText editTitle    = (EditText) findViewById(R.id.edit_task_title);
        final EditText editMemo     = (EditText) findViewById(R.id.edit_memo);
        Button datetimeButton       = (Button)   findViewById(R.id.button_datetime);
        Button saveButton           = (Button)   findViewById(R.id.button_save);
        Button cancelButton         = (Button)   findViewById(R.id.button_cancel);

        textDatetime.setText(new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT, Locale.getDefault()).format(mSelectedDate));

        // Save Button
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide software keyboard if needed
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (editTitle.hasFocus()) {
                    imm.hideSoftInputFromWindow(editTitle.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                } else if (editMemo.hasFocus()) {
                    imm.hideSoftInputFromWindow(editMemo.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }

                // move focus
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                layout.setFocusableInTouchMode(true);
                layout.requestFocus();
                layout.setFocusableInTouchMode(false);


                String title = editTitle.getText() != null ? editTitle.getText().toString() : null;
                String memo  = editMemo.getText()  != null ? editMemo.getText().toString()  : null;
                Date scheduleAt = mSelectedDate != null ? mSelectedDate : new Date();

                if (TextUtils.isEmpty(title)) {
                    createAndShowConfirmationDialog(
                            R.string.add_task__save_confirm_title,
                            R.string.add_task__save_confirm_message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* NOP */
                                }
                            });
                    return;
                }

                Task task = new Task();
                task.setTitle(title);
                task.setBody(memo);
                task.setScheduleAt(scheduleAt);
                task.setStatus(Task.Status.NEW.ordinal());
                task.setType(Task.Type.NORMAL.ordinal());
                task.setUserId(PreferenceHelper.loadUserId(getApplicationContext()));

                final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__creating);
                task.save(new ResultCallback() {
                    @Override
                    public void done(ABResult result, ABException e) {
                        progress.dismiss();
                        if (e == null) {
                            int code = result.getCode();
                            if (code == ABStatus.CREATED && result.getData() != null) {
                                showMessage(AddTaskActivity.this, R.string.message_success__created);
                                Intent intent = new Intent();
                                setResult(TaskListActivity.RESULT_CODE_SUCCESS, intent);
                                finish();
                            } else {
                                showUnexpectedStatusCodeError(AddTaskActivity.this, code);
                            }
                        } else {
                            showError(AddTaskActivity.this, e);
                        }
                    }
                });
            }
        });

        // Select Datetime Button
        datetimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show DateTime Picker dialog
                final DateTimePickerDialog dialog = new DateTimePickerDialog(AddTaskActivity.this);
                dialog.setDateTimePickerListener(new DateTimePickerListener() {
                    @Override
                    public void onSet(Date date) {
                        mSelectedDate = date;
                        textDatetime.setText(new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT, Locale.getDefault()).format(date));
                    }
                    @Override
                    public void onCancel() {
                        /* NOP */
                    }
                });
                dialog.show();
            }
        });

        // Cancel Button
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

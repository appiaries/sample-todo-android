//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.todo.R;
import com.appiaries.todo.activities.DateTimePickerDialog.DateTimePickerListener;
import com.appiaries.todo.common.Constants;
import com.appiaries.todo.models.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditTaskActivity extends BaseActivity {

	private Date mSelectedDate;
	Task mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

    private void setupView() {
        setContentView(R.layout.activity_edit_task);

        Bundle bundle = getIntent().getExtras();
        mTask = (Task) bundle.getSerializable(Constants.EXTRA_KEY_TASK);

        final TextView textDatetime = (TextView) findViewById(R.id.text_datetime);
        final EditText editTitle    = (EditText) findViewById(R.id.edit_task_title);
        final EditText editMemo     = (EditText) findViewById(R.id.edit_memo);
        final Button dtPickerButton = (Button)   findViewById(R.id.button_datetime);
        final Button saveButton     = (Button)   findViewById(R.id.button_save);
        final Button cancelButton   = (Button)   findViewById(R.id.button_cancel);

        Date date = mTask.getScheduledAt();
        String scheduledAt = date.toString();
        textDatetime.setText(scheduledAt);

        editTitle.setText(mTask.getTitle());
        editMemo.setText(mTask.getBody());
        editMemo.setSelection(editMemo.getText().length());

        // Date & Time Picker Button
        dtPickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final DateTimePickerDialog dialog = new DateTimePickerDialog(EditTaskActivity.this);
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


                Task task;
                try {
                    task = (Task) mTask.clone();
                } catch (CloneNotSupportedException e) {
                    showError(EditTaskActivity.this, new ABException(e));
                    finish();
                    return;
                }

                String title = editTitle.getText() != null ? editTitle.getText().toString() : null;
                String memo  = editMemo.getText()  != null ? editMemo.getText().toString()  : null;
                Date scheduleAt = mSelectedDate != null ? mSelectedDate : null;

                if (scheduleAt != null && scheduleAt.getTime() != task.getScheduledAt().getTime()) {
                    task.setScheduleAt(scheduleAt);
                }
                if (title != null && !title.equals(task.getTitle())) {
                    task.setTitle(title);
                }
                if (memo != null && !memo.equals(task.getBody())) {
                    task.setBody(memo);
                }

                if (!task.isDirty()) {
                    finish();
                    return;
                }

                final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__updating);
                task.save(new ResultCallback<Task>() {
                    @Override
                    public void done(ABResult<Task> result, ABException e) {
                        progress.dismiss();
                        if (e == null) {
                            int code = result.getCode();
                            if (code == ABStatus.OK) {
                                showMessage(EditTaskActivity.this, R.string.message_success__updated);
                                Intent intent = new Intent();
                                setResult(TaskListActivity.RESULT_CODE_SUCCESS, intent);
                                finish();
                            } else {
                                showUnexpectedStatusCodeError(EditTaskActivity.this, code);
                            }
                        } else {
                            showError(EditTaskActivity.this, e);
                        }
                    }
                });
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

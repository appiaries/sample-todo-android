//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.appiaries.baas.sdk.AB;
import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABQuery;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.todo.R;
import com.appiaries.todo.common.Constants;
import com.appiaries.todo.common.PreferenceHelper;
import com.appiaries.todo.models.TaskListItem;
import com.appiaries.todo.models.Task;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TaskListActivity extends BaseActivity {

    class TaskListAdapter extends ArrayAdapter<TaskListItem> {

        private final List<TaskListItem> mDataSource;
        private final LayoutInflater mInflater;

        public TaskListAdapter(Context context, List<TaskListItem> data) {
            super(context, R.layout.list_item, data);
            mDataSource = data;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Get rowView from mInflater
            View rowView;

            final TaskListItem item = mDataSource.get(position);

            if (!item.isGroupHeader()) {
                rowView = mInflater.inflate(R.layout.list_item, parent, false);

                // Set task title
                ((TextView) rowView.findViewById(R.id.text_task_title)).setText(mDataSource.get(position).getTask().getTitle());

                // Mark row as important (only for New Task)
                if (item.getTask().getType() == Task.Type.IMPORTANT.ordinal()
                        && item.getTask().getStatus() == Task.Status.NEW.ordinal()) {
                    LinearLayout itemLayout = (LinearLayout) rowView.findViewById(R.id.item_layout);
                    itemLayout.setPadding(6, 0, 0, 0);
                }

                // Mark task title as completed (STRIKE THRU)
                if (item.getTask().getStatus() == Task.Status.COMPLETED.ordinal()) {
                    TextView textTitle = (TextView) rowView.findViewById(R.id.text_task_title);
                    textTitle.setPaintFlags(textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    Button deleteTaskButton = (Button) rowView.findViewById(R.id.button_delete_task);
                    deleteTaskButton.setVisibility(View.VISIBLE);

                    // Handle button click event for btnDeleteTask
                    deleteTaskButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteTask(position);
                        }
                    });
                }

                // Resets the toolbar to be closed
                View toolbar = rowView.findViewById(R.id.toolbar);

                // Done Button
                Button btnFinished = (Button) toolbar.findViewById(R.id.button_done);
                btnFinished.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItems.get(position).getTask().getStatus() == Task.Status.NEW.ordinal()) {
                            updateTask(position, Task.Status.COMPLETED, null);
                        } else {
                            updateTask(position, Task.Status.NEW, null);
                        }
                    }
                });

                // Important Button
                Button importantButton = (Button) toolbar.findViewById(R.id.button_important);
                importantButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItems.get(position).getTask().getType() == Task.Type.NORMAL.ordinal()) {
                            updateTask(position, null, Task.Type.IMPORTANT);
                        } else {
                            updateTask(position, null, Task.Type.NORMAL);
                        }
                    }
                });

                // Delete Button
                Button deleteButton = (Button) toolbar.findViewById(R.id.button_delete);
                deleteButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTask(position);
                    }
                });

                // Edit Button
                Button editButton = (Button) toolbar.findViewById(R.id.button_edit);
                editButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TaskListActivity.this, EditTaskActivity.class);
                        intent.putExtra(Constants.EXTRA_KEY_TASK, mItems.get(position).getTask());
                        startActivityForResult(intent, RESULT_CODE_SUCCESS);
                    }
                });

                toolbar.setVisibility(View.GONE);

            } else {

                rowView = mInflater.inflate(R.layout.group_header_item, parent, false);

                ((TextView) rowView.findViewById(R.id.text_group_title)).setText(mDataSource.get(position).getGroupTitle());

                Button addTaskButton = (Button) rowView.findViewById(R.id.button_add_task);

                if (item.getGroupDate() == null) {
                    addTaskButton.setVisibility(View.GONE);
                } else {
                    addTaskButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(item.getGroupDate());
                            long msec = cal.getTimeInMillis();

                            Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                            intent.putExtra(Constants.EXTRA_KEY_DATE, msec);
                            startActivityForResult(intent, RESULT_CODE_SUCCESS);
                        }
                    });
                }
            }

            return rowView;
        }
    }

    static final int RESULT_CODE_SUCCESS = 1;

	List<Task> mTasks;
	List<TaskListItem> mItems;
	EditText mTaskTitleOnActionBar;
	PullToRefreshListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();

        refreshListView();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_SUCCESS) {
            refreshListView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.createTask: // Quick Task Creation
                // Hide keyboard and clear focus
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTaskTitleOnActionBar.getWindowToken(), 0);
                mTaskTitleOnActionBar.clearFocus();
                // Create new task
                String taskTitle = mTaskTitleOnActionBar.getText().toString();
                if (taskTitle.isEmpty()) {
                    showMessage(TaskListActivity.this, R.string.message_error__empty_task_title);
                    return true;
                }
                createTask(taskTitle);
                return true;

            case R.id.settings:
                Intent myIntent = new Intent(TaskListActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupView() {
        setContentView(R.layout.activity_task_list);

        mListView = (PullToRefreshListView) findViewById(R.id.list);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshListView(500); //NOTE: delay 500 msec
            }
        });
        mListView.setOverScrollMode(PullToRefreshListView.OVER_SCROLL_ALWAYS);
        mListView.setEmptyView(findViewById(R.id.empty));

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            /* actionBar.setIcon(android.R.color.transparent); */
            // add the custom view to the action bar
            actionBar.setCustomView(R.layout.action_view);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

            mTaskTitleOnActionBar = (EditText) actionBar.getCustomView().findViewById(R.id.edit_task_title);
            mTaskTitleOnActionBar.setHint(R.string.task_list__search_bar_placeholder);
            mTaskTitleOnActionBar.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });
            mTaskTitleOnActionBar.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        // Hide keyboard and clear focus
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mTaskTitleOnActionBar.getWindowToken(), 0);
                        mTaskTitleOnActionBar.clearFocus();
                        // Create new task
                        String taskTitle = mTaskTitleOnActionBar.getText().toString();
                        if (TextUtils.isEmpty(taskTitle)) {
                            showMessage(TaskListActivity.this, R.string.message_error__empty_task_title);
                            return true;
                        }
                        createTask(taskTitle);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void refreshListView() {
        refreshListView(createAndShowProgressDialog(R.string.progress__loading), 0);
    }
    private void refreshListView(int delay) {
        refreshListView(null, delay);
    }
    private void refreshListView(final ProgressDialog progressDialog) {
        refreshListView(progressDialog, 0);
    }
    private void refreshListView(final ProgressDialog progress, int delay) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String userId = PreferenceHelper.loadUserId(TaskListActivity.this);
                ABQuery query = Task.query()
                        .where(Task.Field.USER_ID).equalsTo(userId)
                        .orderBy(Task.Field.CREATED, ABQuery.SortDirection.DESC);

                AB.DBService.findWithQuery(query, new ResultCallback<List<Task>>() {
                    @Override
                    public void done(ABResult<List<Task>> result, ABException e) {
                        if (progress != null) {
                            progress.dismiss();
                        }
                        if (e == null) {
                            mTasks = result.getData();

                            // Creating the list adapter and populating the list
                            mItems = buildDataForListView(mTasks);

                            TaskListAdapter listAdapter = new TaskListAdapter(TaskListActivity.this, mItems);

                            // Set Adapter for the listView
                            mListView.setAdapter(listAdapter);

                            // Creating an item click listener, to open/close our toolbar for each item
                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                                    TaskListItem item = mItems.get(position - 1);

                                    if (!item.isGroupHeader() && item.getTask().getStatus() == Task.Status.NEW.ordinal()) {
                                        View toolbar = view.findViewById(R.id.toolbar);
                                        if (toolbar.getVisibility() == View.GONE) {
                                            toolbar.setVisibility(View.VISIBLE);
                                        } else {
                                            toolbar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        } else {
                            showError(TaskListActivity.this, e);
                        }
                        if (mListView.isRefreshing()) {
                            mListView.onRefreshComplete();
                        }
                    }
                });
            }
        }, delay);
    }

    private void createTask(String title) {
        Task task = new Task();
        task.setScheduleAt(new Date());
        task.setTitle(title);
        task.setBody("");
        task.setUserId(PreferenceHelper.loadUserId(getApplicationContext()));
        task.setType(Task.Type.NORMAL.ordinal());
        task.setStatus(Task.Status.NEW.ordinal());

        final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__creating);
        task.save(new ResultCallback<Task>() {
            @Override
            public void done(ABResult<Task> result, ABException e) {
                if (e == null) {
                    int code = result.getCode();
                    if (code == ABStatus.OK || code == ABStatus.CREATED || code == ABStatus.NO_CONTENT) {
                        refreshListView(progress);
                        mTaskTitleOnActionBar.setText("");
                    }
                } else {
                    showError(TaskListActivity.this, e);
                }
                progress.dismiss();
            }
        });
    }

    private void updateTask(final int position, Task.Status status, Task.Type type) {
        // Position for getting Task object from DataSource)
        try {
            Task task = (Task) mItems.get(position).getTask().clone();
            task.setPosition(position);
            if (status != null) {
                task.setStatus(status.ordinal());
            }
            if (type != null) {
                task.setType(type.ordinal());
            }

            final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__updating);
            task.save(new ResultCallback<Task>() {
                @Override
                public void done(ABResult<Task> result, ABException e) {
                    if (e == null) {
                        int code = result.getCode();
                        if (code == ABStatus.OK || code == ABStatus.CREATED || code == ABStatus.NO_CONTENT) {
                            refreshListView(progress);
                        } else {
                            showUnexpectedStatusCodeError(TaskListActivity.this, code);
                        }
                    } else {
                        showError(TaskListActivity.this, e);
                    }
                    progress.dismiss();
                }
            });
        } catch (CloneNotSupportedException e) {
            showError(TaskListActivity.this, new ABException(e));
        }
    }

    private void deleteTask(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);
        //>> custom Title
        TextView textTitle = new TextView(TaskListActivity.this);
        textTitle.setText(R.string.task_list__delete_confirm_title);
        textTitle.setPadding(10, 10, 10, 10);
        textTitle.setGravity(Gravity.CENTER);
        textTitle.setTextSize(20);
        builder.setCustomTitle(textTitle);
        //>> custom message
        TextView textMessage = new TextView(TaskListActivity.this);
        textMessage.setPadding(10, 40, 10, 40);
        textMessage.setText(R.string.task_list__delete_confirm_message);
        textMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        textMessage.setTextSize(20);
        builder.setView(textMessage);
        //>> Delete Button
        builder.setPositiveButton(
                R.string.task_list__delete_confirm_negative_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__deleting);
                        Task task = mItems.get(position).getTask();
                        task.delete(new ResultCallback<Void>() {
                            @Override
                            public void done(ABResult<Void> result, ABException e) {
                                if (e == null) {
                                    int code = result.getCode();
                                    if (code == ABStatus.CREATED || code == ABStatus.NO_CONTENT) {
                                        refreshListView(progress);
                                    }
                                } else {
                                    showError(TaskListActivity.this, e);
                                }
                                progress.dismiss();
                            }
                        });
                    }
                });
        //>> Cancel Button
        builder.setNegativeButton(
                R.string.task_list__delete_confirm_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /* NOP */
                    }
                }
        );

        builder.show();
    }

    private List<TaskListItem> buildDataForListView(List<Task> tasks) {
		List<TaskListItem> items = new ArrayList<>();

        if (tasks == null || tasks.size() == 0) {
            return items;
        }

		Calendar cal = Calendar.getInstance();

		// TODAY
		items.add(new TaskListItem(true, getString(R.string.task_list__group_today), cal.getTime()));
		items.addAll(getTaskListFromTasks(getTasksByDate(cal.getTime())));

		// TOMORROW
		cal.add(Calendar.DATE, 1); // Increase by one
		items.add(new TaskListItem(true, getString(R.string.task_list__group_tomorrow), cal.getTime()));
		items.addAll(getTaskListFromTasks(getTasksByDate(cal.getTime())));

		// THE DAY AFTER TOMORROW
		cal.add(Calendar.DATE, 1); // Increase by one
		items.add(new TaskListItem(true, getString(R.string.task_list__group_day_after_tomorrow), cal.getTime()));
		items.addAll(getTaskListFromTasks(getTasksByDate(cal.getTime())));

		// 4 WEEKDAY
		String weekdayLabel;

		for (int i = 0; i < 4; i++) {
			cal.add(Calendar.DATE, 1); // Increase by one
			weekdayLabel = getWeekDayName(cal.get(Calendar.DAY_OF_WEEK));
			items.add(new TaskListItem(true, weekdayLabel, cal.getTime()));
			items.addAll(getTaskListFromTasks(getTasksByDate(cal.getTime())));
		}

		// NEAR FUTURE between TODAY and next 30 days
		items.add(new TaskListItem(true, getString(R.string.task_list__group_near_future)));
		items.addAll(getTaskListFromTasks(getTasksWithinRange(30)));

		// SOME DAY IN THE FUTURE over next 30 days
		items.add(new TaskListItem(true, getString(R.string.task_list__group_someday)));
		items.addAll(getTaskListFromTasks(getTasksOverRange(30)));

		// PAST
		items.add(new TaskListItem(true, getString(R.string.task_list__group_past)));
		items.addAll(getTaskListFromTasks(getTasksInPast()));

		return items;
	}

	private List<TaskListItem> getTaskListFromTasks(List<Task> tasks) {
		List<TaskListItem> items = new ArrayList<>();
		for (Task task : tasks) {
			items.add(new TaskListItem(task));
		}
		return items;
	}

	private List<Task> getTasksByDate(Date dateToCompare) {
		List<Task> tasks = new ArrayList<>();
        for (Iterator<Task> iterator = mTasks.iterator(); iterator.hasNext(); ) {
            Task task = iterator.next();
            if (DateUtils.isSameDay(task.getScheduledAt(), dateToCompare)) {
                tasks.add(task);
                iterator.remove();
            }
        }
		return tasks;
	}

	private List<Task> getTasksWithinRange(int numberOfDays) {
		Calendar calLimit = Calendar.getInstance();
		calLimit.add(Calendar.DATE, numberOfDays);

		Calendar calToday = Calendar.getInstance();

		List<Task> tasks = new ArrayList<>();
		for (Iterator<Task> iterator = mTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			Calendar taskCal = Calendar.getInstance();
			taskCal.setTime(task.getScheduledAt());

			// Today <= Scheduled At <= Next N days
			if ((taskCal.before(calLimit) || taskCal.equals(calLimit))
					&& (taskCal.after(calToday) || taskCal.equals(calToday))) {
				tasks.add(task);
				iterator.remove();
			}
		}
		return tasks;
	}

	private List<Task> getTasksOverRange(int numberOfDays) {
		Calendar calLimit = Calendar.getInstance();
		calLimit.add(Calendar.DATE, numberOfDays);

		List<Task> tasks = new ArrayList<>();
		for (Iterator<Task> iterator = mTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			Calendar taskCal = Calendar.getInstance();
			taskCal.setTime(task.getScheduledAt());

			// Scheduled At > Next N days
			if (taskCal.after(calLimit)) {
				tasks.add(task);
				iterator.remove();
			}
		}
		return tasks;
	}

	private List<Task> getTasksInPast() {
		Calendar cal = Calendar.getInstance();

		List<Task> tasks = new ArrayList<>();
		for (Iterator<Task> iterator = mTasks.iterator(); iterator.hasNext();) {
			Task task = iterator.next();
			Calendar taskCal = Calendar.getInstance();
			taskCal.setTime(task.getScheduledAt());

			if (taskCal.before(cal)) {
				tasks.add(task);
				iterator.remove();
			}
		}
		return tasks;
	}

	private String getWeekDayName(int dayOfWeek) {
		switch (dayOfWeek) {
            case Calendar.SUNDAY    : return getString(R.string.task_list__weekday_sunday);
            case Calendar.MONDAY    : return getString(R.string.task_list__weekday_monday);
            case Calendar.TUESDAY   : return getString(R.string.task_list__weekday_tuesday);
            case Calendar.WEDNESDAY : return getString(R.string.task_list__weekday_wednesday);
            case Calendar.THURSDAY  : return getString(R.string.task_list__weekday_thursday);
            case Calendar.FRIDAY    : return getString(R.string.task_list__weekday_friday);
            case Calendar.SATURDAY  : return getString(R.string.task_list__weekday_saturday);
            default                 : return getString(R.string.task_list__weekday_unknown);
		}
	}

}

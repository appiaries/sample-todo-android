package com.appiaries.todo.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.appiaries.todo.R;
import com.appiaries.todo.common.APIHelper;
import com.appiaries.todo.jsonmodels.DailyModel;
import com.appiaries.todo.jsonmodels.Task;
import com.appiaries.todo.managers.TaskManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class DailyListActivity extends BaseActivity {
	String screenTag = "DailyListActivity";
	List<Task> taskList;
	ArrayList<DailyModel> models;
	EditText search;
	private static final int RESPONSE_CODE = 1;

	ProgressDialog progressBar;
	PullToRefreshListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_list);
		list = (PullToRefreshListView) findViewById(R.id.dailyListView);
		
		// binding reload new data when pull list view
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// execute HttpRequest to get Shop List data
				new GetTaskListAsyncTask().execute();
			}
		});
		
		ActionBar actionBar = getActionBar();

		// actionBar.setIcon(android.R.color.transparent);

		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.action_view);
		search = (EditText) actionBar.getCustomView().findViewById(
				R.id.searchField);
		search.setHint("タスクを入力");

		search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				return false;
			}
		});

		search.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		new GetTaskListAsyncTask().execute();

	}

	private ArrayList<DailyModel> buildDataForListView() {
		ArrayList<DailyModel> modelList = new ArrayList<DailyModel>();

		Calendar cal = Calendar.getInstance();

		List<Task> tasks = null;

		// TODAY (今日)
		modelList.add(new DailyModel(true, "今日", cal.getTime()));
		modelList.addAll(getDailyListFromTasks(getTasksByDate(cal.getTime())));

		// TOMORROW (明日)
		cal.add(Calendar.DATE, 1); // Increase by one
		modelList.add(new DailyModel(true, "明日", cal.getTime()));
		modelList.addAll(getDailyListFromTasks(getTasksByDate(cal.getTime())));

		// THE DAY AFTER TOMORROW (明後日)
		cal.add(Calendar.DATE, 1); // Increase by one
		modelList.add(new DailyModel(true, "明後日", cal.getTime()));
		modelList.addAll(getDailyListFromTasks(getTasksByDate(cal.getTime())));

		// 4 WEEKDAY
		String weekdayLabel = "";

		for (int i = 0; i < 4; i++) {
			cal.add(Calendar.DATE, 1); // Increase by one
			weekdayLabel = getWeekDayName(cal.get(Calendar.DAY_OF_WEEK));
			modelList.add(new DailyModel(true, weekdayLabel, cal.getTime()));
			modelList
					.addAll(getDailyListFromTasks(getTasksByDate(cal.getTime())));
		}

		// NEAR FUTURE (近日中) between TODAY and next 30 days
		modelList.add(new DailyModel(true, "近日中"));
		modelList.addAll(getDailyListFromTasks(getTasksWithinRange(30)));

		// SOME DAY IN THE FUTURE (いつか) over next 30 days
		modelList.add(new DailyModel(true, "いつか"));
		modelList.addAll(getDailyListFromTasks(getTasksOverRange(30)));

		// PAST (過去)
		modelList.add(new DailyModel(true, "過去"));
		modelList.addAll(getDailyListFromTasks(getTasksInPast()));

		return modelList;
	}

	private List<DailyModel> getDailyListFromTasks(List<Task> tasks) {
		List<DailyModel> modelList = new ArrayList<DailyModel>();

		for (Task task : tasks) {
			modelList.add(new DailyModel(task));
		}

		return modelList;
	}

	private List<Task> getTasksByDate(Date dateToCompare) {
		List<Task> tasks = new ArrayList<Task>();

		for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();) {
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

		List<Task> tasks = new ArrayList<Task>();

		for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();) {
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

		List<Task> tasks = new ArrayList<Task>();

		for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();) {
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

		List<Task> tasks = new ArrayList<Task>();

		for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();) {
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
		String weekDayName = "";

		switch (dayOfWeek) {
		case Calendar.SUNDAY:
			weekDayName = "日曜日";
			break;
		case Calendar.MONDAY:
			weekDayName = "月曜日";
			break;
		case Calendar.TUESDAY:
			weekDayName = "火曜日";
			break;
		case Calendar.WEDNESDAY:
			weekDayName = "水曜日";
			break;
		case Calendar.THURSDAY:
			weekDayName = "木曜日";
			break;
		case Calendar.FRIDAY:
			weekDayName = "金曜日";
			break;
		case Calendar.SATURDAY:
			weekDayName = "土曜日";
			break;
		}

		return weekDayName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.daily_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.createTask:
			Log.d(screenTag, "btn createTask clicked");
			String strTaskName = search.getText().toString();
			if (!strTaskName.isEmpty() && !strTaskName.equals("")) {
				new QuickCreateTaskAsyncTask().execute(strTaskName);
			}
			return true;

		case R.id.settings:

			Log.d(screenTag, "btn settings clicked");
			Intent myIntent = new Intent(DailyListActivity.this,
					Menu1Activity.class);
			startActivity(myIntent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A simple implementation of list adapter.
	 */
	class DailyListAdapter extends ArrayAdapter<DailyModel> {

		private final Context context;
		private final ArrayList<DailyModel> dataSource;

		public DailyListAdapter(Context context, ArrayList<DailyModel> data) {

			super(context, R.layout.list_item, data);

			this.context = context;
			this.dataSource = data;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// Create inflater
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Get rowView from inflater
			View rowView = null;

			DailyModel model = dataSource.get(position);

			if (!model.isGroupHeader()) {
				rowView = inflater.inflate(R.layout.list_item, parent, false);

				// Set task title
				((TextView) rowView.findViewById(R.id.taskTitle))
						.setText(dataSource.get(position).getTask().getTitle());

				// Mark row as important (only for New Task)
				if (model.getTask().getType() == Task.TaskType.IMPORTANT
						.ordinal()
						&& model.getTask().getStatus() == Task.Status.NEW
								.ordinal()) {
					LinearLayout itemLayout = (LinearLayout) rowView
							.findViewById(R.id.item_layout);
					itemLayout.setPadding(6, 0, 0, 0);
				}

				// Mark task title as completed (STRIKE THRU)
				if (model.getTask().getStatus() == Task.Status.COMPLETED
						.ordinal()) {
					((TextView) rowView.findViewById(R.id.taskTitle))
							.setPaintFlags(((TextView) rowView
									.findViewById(R.id.taskTitle))
									.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);

					Button btnDeleteTask = (Button) rowView
							.findViewById(R.id.btnDeleteTask);
					btnDeleteTask.setVisibility(View.VISIBLE);

					// Handle button click event for btnDeleteTask
					btnDeleteTask.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteTask(position);
						}
					});
				}

				// Resets the toolbar to be closed
				View toolbar = rowView.findViewById(R.id.toolbar);

				// Handle ListView item button click event
				// Button finished
				Button btnFinished = (Button) toolbar
						.findViewById(R.id.btnFinished);
				btnFinished.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Position for getting Task object from DataSource)
						int updatedStatus = models.get(position).getTask()
								.getType();
						if (models.get(position).getTask().getStatus() == Task.Status.NEW
								.ordinal()) {
							updatedStatus = Task.Status.COMPLETED.ordinal();
						} else {
							updatedStatus = Task.Status.NEW.ordinal();
						}

						Map<String, Object> updateData = new HashMap<String, Object>();
						updateData.put("status", updatedStatus);

						Map<String, Object> data = new HashMap<String, Object>();
						data.put("position", position);
						data.put("data", updateData);

						new UpdateTaskAsyncTask().execute(data);
					}
				});

				// Button important
				Button btnImportant = (Button) toolbar
						.findViewById(R.id.btnImportant);
				btnImportant.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int updateType = models.get(position).getTask()
								.getType();
						if (models.get(position).getTask().getType() == Task.TaskType.NORMAL
								.ordinal()) {
							updateType = Task.TaskType.IMPORTANT.ordinal();
						} else {
							updateType = Task.TaskType.NORMAL.ordinal();
						}

						Map<String, Object> updateData = new HashMap<String, Object>();
						updateData.put("type", updateType);

						Map<String, Object> data = new HashMap<String, Object>();
						data.put("position", position);
						data.put("data", updateData);

						new UpdateTaskAsyncTask().execute(data);

					}
				});

				// Button delete
				Button btnDelete = (Button) toolbar
						.findViewById(R.id.btnDelete);
				btnDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteTask(position);
					}
				});

				// Button edit
				Button btnEdit = (Button) toolbar.findViewById(R.id.btnEdit);
				btnEdit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(DailyListActivity.this,
								EditTaskActivity.class);
						myIntent.putExtra("task", models.get(position)
								.getTask());
						startActivityForResult(myIntent, RESPONSE_CODE);

					}
				});

				toolbar.setVisibility(View.GONE);
			} else {

				final DailyModel headerModel = dataSource.get(position);

				rowView = inflater.inflate(R.layout.group_header_item, parent,
						false);							
				
				((TextView) rowView.findViewById(R.id.groupTitle))
						.setText(dataSource.get(position).getCategoryName());

				Button btnAddTask = (Button) rowView
						.findViewById(R.id.btnCreateNewTask);

				if (headerModel.getCategoryDate() == null) {
					btnAddTask.setVisibility(View.GONE);
				} else {
					btnAddTask.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent myIntent = new Intent(
									DailyListActivity.this,
									AddTaskActivity.class);

							Calendar cal = Calendar.getInstance();
							cal.setTime(headerModel.getCategoryDate());

							long categoryDateInMillis = cal.getTimeInMillis();

							myIntent.putExtra("categoryDate",
									categoryDateInMillis);

							startActivityForResult(myIntent, RESPONSE_CODE);
						}
					});
				}
			}

			// return rowView
			return rowView;
		}
	}
	
	private void deleteTask(final int position)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(
				DailyListActivity.this);

		// customize and set title and message content
		TextView customTitle = new TextView(DailyListActivity.this);

		customTitle.setText("ご確認");
		customTitle.setPadding(10, 10, 10, 10);
		customTitle.setGravity(Gravity.CENTER);
		customTitle.setTextSize(20);

		builder.setCustomTitle(customTitle);

		TextView customMessage = new TextView(DailyListActivity.this);

		customMessage.setPadding(10, 40, 10, 40);
		customMessage.setText("削除しますか？");
		customMessage.setGravity(Gravity.CENTER_HORIZONTAL);
		customMessage.setTextSize(20);

		builder.setView(customMessage);

		// handle cancel button click
		builder.setNegativeButton("削除",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						new DeleteTaskAsyncTask().execute(position);
					}
				});
		
		builder.setPositiveButton("キャンセル",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				});
		builder.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESPONSE_CODE) {
			// Reload ListView
			new GetTaskListAsyncTask().execute();
		}
	}

	private class GetTaskListAsyncTask extends
			AsyncTask<Void, Void, List<Task>> {

		@Override
		protected List<Task> doInBackground(Void... params) {
			String strUserId = APIHelper.getUserId(DailyListActivity.this);
			try {
				taskList = TaskManager.getInstance().getTaskList(strUserId);
				return taskList;
			} catch (Exception ex) {

				ex.printStackTrace();

			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(DailyListActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(List<Task> result) {
			super.onPostExecute(result);

			progressBar.dismiss();
			
			if (result.size() > 0) {
				// Creating the list adapter and populating the list
				models = buildDataForListView();

				DailyListAdapter listAdapter = new DailyListAdapter(
						DailyListActivity.this, models);

				// Set Adapter for the listview
				list.setAdapter(listAdapter);

				// Creating an item click listener, to open/close our toolbar
				// for each item
				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long id) {

						DailyModel model = models.get(position - 1);

						if (!model.isGroupHeader()
								&& model.getTask().getStatus() == Task.Status.NEW
										.ordinal()) {
							View toolbar = view.findViewById(R.id.toolbar);

							if (toolbar.getVisibility() == View.GONE) {
								toolbar.setVisibility(View.VISIBLE);
							} else {
								toolbar.setVisibility(View.GONE);
							}
						}

					}
				});
			}
			if (list.isRefreshing()) {
				list.onRefreshComplete();
			}
		}

	}

	private class DeleteTaskAsyncTask extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			int position = params[0];
			try {
				return TaskManager.getInstance().deleteTask(
						models.get(position).getTask().getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(DailyListActivity.this);
			progressBar.setMessage("Deleting....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.dismiss();

			if (result == 201 || result == 204) {
				new GetTaskListAsyncTask().execute();
			}
		}

	}

	private class UpdateTaskAsyncTask extends
			AsyncTask<Map<String, Object>, Void, Integer> {

		@Override
		protected Integer doInBackground(Map<String, Object>... params) {
			try {
				int position = Integer.parseInt(params[0].get("position")
						.toString());
				Map<String, Object> data = (HashMap<String, Object>) params[0]
						.get("data");

				return TaskManager.getInstance().updateTask(
						models.get(position).getTask().getId(), data);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(DailyListActivity.this);
			progressBar.setMessage("Updating....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.dismiss();
			if (result == 201 || result == 204 || result == 200) {
				new GetTaskListAsyncTask().execute();
			}
		}

	}

	private class QuickCreateTaskAsyncTask extends
			AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			try {
				HashMap<String, Object> data = new HashMap<String, Object>();

				Calendar c = Calendar.getInstance();
				data.put("scheduled_at", c.getTimeInMillis());
				data.put("title", params[0]);
				data.put("body", "");
				data.put("category_id", "");
				data.put("user_Id",
						APIHelper.getUserId(getApplicationContext()));
				data.put("type", 0);
				data.put("status", 0);

				return TaskManager.getInstance().createTask(data);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(DailyListActivity.this);
			progressBar.setMessage("Creating....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.dismiss();
			if (result == 201 || result == 204 || result == 200) {
				new GetTaskListAsyncTask().execute();
				search.setText("");
			}

		}

	}
}

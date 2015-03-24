package com.appiaries.todo.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.appiaries.APISException;
import com.appiaries.APISJsonData;
import com.appiaries.APISQueryCondition;
import com.appiaries.APISResult;
import com.appiaries.todo.common.Constants;
import com.appiaries.todo.common.TextHelper;
import com.appiaries.todo.jsonmodels.Task;

/**
 * 
 * @author ntduc
 * 
 */
public class TaskManager {

	private final static TaskManager instance = new TaskManager();

	/**
	 * init
	 */
	private TaskManager() {

	}

	public static TaskManager getInstance() {
		return instance;
	}

	public int updateTask(String objectId, Map<String, Object> data) {
		APISResult responseObj = null;
		try {

			responseObj = APISJsonData.updateJsonData(Constants.COLLECTION_TASK,
					objectId, data);

			if (responseObj != null && responseObj.getResponseCode() == 204) {
				return responseObj.getResponseCode();
			}
		} catch (APISException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public List<Task> getTaskList(String userId) throws JSONException {

		APISQueryCondition appQueryCondition = new APISQueryCondition();
		appQueryCondition.equal("user_Id", userId);
		APISResult responseObject;
		String jsonString = Constants.BLANK_STRING;

		try {

			responseObject = APISJsonData.selectJsonData(
					Constants.COLLECTION_TASK, appQueryCondition);
			if (responseObject.getResponseData() != null) {
				jsonString = TextHelper.convertToJSON(responseObject
						.getResponseData());
			}
			Log.d("AppInfoManager json return string: ", jsonString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d("JSON Response: ", jsonString);

		JSONObject jsonObj = new JSONObject(jsonString);

		String objsString = jsonObj.getString("_objs");

		JSONArray jsonObjs = new JSONArray(objsString);

		List<Task> taskList = new ArrayList<Task>();

		for (int i = 0; i < jsonObjs.length(); i++) {

			JSONObject obj = jsonObjs.getJSONObject(i);

			taskList.add(new Task(obj.getString("_id"), 
					obj.getString("user_Id"), 
					obj.getString("category_id"), 
					obj.getInt("type"), 
					obj.getString("title"), 
					obj.getString("body"), 
					obj.getInt("status"),
					obj.getLong("scheduled_at"),
					obj.getLong("_cts"),
					obj.getLong("_uts"),
					obj.getString("_cby"),
					obj.getString("_uby")));
		}
		Collections.sort(taskList);
		return taskList;
	}


	/**
	 * Delete all
	 */
	public void deleteAll() {
		APISQueryCondition appQueryCondition = new APISQueryCondition();

		APISResult responseObject;

		try {

			responseObject = APISJsonData.deleteJsonData(
					Constants.COLLECTION_TASK, appQueryCondition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int createTask(Map<String, Object> data){
		APISResult responseObject = null;

		try {
			responseObject = APISJsonData.registJsonData(
					Constants.COLLECTION_TASK, null, data);
			if(responseObject != null && responseObject.getResponseCode() == 201){
				return responseObject.getResponseCode();
			}
		} catch (APISException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int deleteTask(String objectName) {
		APISResult responseObject;
		try {

			responseObject = APISJsonData.deleteJsonData(
					Constants.COLLECTION_TASK, objectName);
			if(responseObject != null && (responseObject.getResponseCode() == 204 || responseObject.getResponseCode() == 201)){
				return responseObject.getResponseCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}

package com.appiaries.todo.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import com.appiaries.APISException;
import com.appiaries.APISResult;
import com.appiaries.APISUser;
import com.appiaries.todo.common.APIHelper;
import com.appiaries.todo.common.TextHelper;
import com.appiaries.todo.jsonmodels.User;

/**
 * 
 * @author ntduc
 * 
 */
public class UserManager {

	private final static UserManager instance = new UserManager();

	/**
	 * init
	 */
	private UserManager() {

	}

	public static UserManager getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param ctx
	 *            is Context
	 * @return List<Player>
	 * @throws JSONException
	 */
	public APISResult getUserInfo(Context ctx) throws JSONException {

		APISResult responseObject = null;
		String jsonString = "";

		try {

			responseObject = APISUser.getOwnInformation();
			if (responseObject.getResponseData() != null) {
				jsonString = TextHelper.convertToJSON(responseObject
						.getResponseData());
			}
			Log.d("PlayerManager json return string: ", jsonString);

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

		Log.d("JSON Response: ", jsonString);

		JSONObject obj = new JSONObject(jsonString);

		APIHelper.setStringToLocalStorage(ctx, "userId",
				obj.getString("user_id"));

		return responseObject;

	}

	public APISResult doLogin(String strLoginId, String strPassword,
			boolean bAutoLogin, Context ctx) throws JSONException {

		APISResult responseObject = null;
		String jsonString = "";

		try {

			responseObject = APISUser.login(strLoginId, strPassword, true);
			if (responseObject.getResponseCode() == 201)
				jsonString = TextHelper.convertToJSON(responseObject
						.getResponseData());
				Log.d("JSON Response: ", jsonString);
				
				JSONObject jsonObj = new JSONObject(jsonString);
				APIHelper.setStringToLocalStorage(ctx,"userId", jsonObj.getString("user_id"));
				
		} catch (APISException e) {

			e.printStackTrace();

		}


		return responseObject;
	}

	public APISResult registerUser(String strLoginId, String strPassword,
			String strEmail, Map<String, Object> data) throws JSONException {
		APISResult responseObject = null;
		try {
			responseObject = APISUser.registUser(strLoginId, strPassword,
					strEmail, data);

		} catch (APISException e) {
			e.printStackTrace();
		}
		return responseObject;
	}
	
	public APISResult updateUser(String password, String email, HashMap<String, String> attr) throws JSONException {

		HashMap<String, Object> attribute = new HashMap<String, Object>();
		if(attr.size() >0 ){
			 Iterator it = attr.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			     
			        attribute.put(pairs.getKey().toString(), pairs.getValue());
			        System.out.println(pairs.getKey().toString() + " = " + pairs.getValue());
			        it.remove(); // avoids a ConcurrentModificationException
			    }
		}
		
		APISResult responseObject = null;
		String jsonString = "";

		try {

			responseObject = APISUser.updateUser(password, email, attribute);
			if (responseObject.getResponseCode() == 200)
				jsonString = TextHelper.convertToJSON(responseObject
						.getResponseData());

		} catch (APISException e) {

			e.printStackTrace();

		}

		Log.d("JSON Response: ", jsonString);

		return responseObject;
	}
	
	public User getUserInformation() throws JSONException {

		
		APISResult responseObject = null;
		String jsonString = "";

		try {

			responseObject = APISUser.getOwnInformation();
			if (responseObject.getResponseCode() == 200)
				jsonString = TextHelper.convertToJSON(responseObject
						.getResponseData());

		} catch (APISException e) {

			e.printStackTrace();

		}
		Log.d("JSON Response: ", jsonString);
		
		JSONObject obj = new  JSONObject(jsonString);
		
		User user = new User();
		
		if(!obj.isNull("login_id")){
			user.setLoginId(obj.getString("login_id"));
		}
		
		if(!obj.isNull("email")){
			user.setEmail(obj.getString("email"));
		}
		
		return user;
	}
}

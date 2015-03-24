package com.appiaries.todo.activities;

import java.util.HashMap;
import java.util.Map;


import com.appiaries.APISResult;
import com.appiaries.todo.R;
import com.appiaries.todo.common.APIHelper;
import com.appiaries.todo.managers.UserManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

@SuppressWarnings("unchecked")
public class LoginActivity extends BaseActivity {
	PlanetHolder planetHolder;
	ProgressDialog progressBar;
	String loginId;
	String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		CreateView();
	}

	private void CreateView() {

		planetHolder = new PlanetHolder();

		EditText txtId = (EditText) findViewById(R.id.txtIdLogin);
		EditText txtPassword = (EditText) findViewById(R.id.txtPasswordLogin);
		TextView txtValidation = (TextView) findViewById(R.id.validation);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);

		planetHolder.txtValidation = txtValidation;
		planetHolder.txtId = txtId;
		planetHolder.txtPassword = txtPassword;
		planetHolder.btnLogin = btnLogin;

		planetHolder.btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (validationFrom(planetHolder)) {
					String loginId = planetHolder.txtId.getText().toString();
					String password = planetHolder.txtPassword.getText()
							.toString();
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("loginId", loginId);
					data.put("password", password);

					new DoLoginAsynTask().execute(data);

				} else {
					planetHolder.txtValidation.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 
	 * @param planetHolder
	 * @return
	 */
	private boolean validationFrom(final PlanetHolder planetHolder) {
		boolean flg = true;
		final String txtId = planetHolder.txtId.getText().toString();
		if (!isValidPassword(txtId) || txtId.trim().length() < 3) {
			planetHolder.txtId
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtId
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		final String pass = planetHolder.txtPassword.getText().toString();
		if (!isValidPassword(pass) || pass.trim().length() < 6) {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}
		return flg;
	}

	/**
	 * validating password and user id not null
	 * 
	 * @param val
	 * @return false with val null else true
	 */
	private boolean isValidPassword(String val) {
		if (val != null && val.trim().length() > 0) {
			return true;
		}
		return false;
	}

	/* *********************************
	 * We use the holder pattern It makes the view faster and avoid finding the
	 * component *********************************
	 */
	private static class PlanetHolder {

		public TextView txtValidation;
		public EditText txtId;
		public EditText txtPassword;
		public Button btnLogin;
	}

	private class DoLoginAsynTask extends
			AsyncTask<HashMap<String, String>, Void, APISResult> {

		@Override
		protected APISResult doInBackground(HashMap<String, String>... params) {
			HashMap<String, String> data = params[0];
			loginId = data.get("loginId");
			password = data.get("password");

			try {

				return UserManager.getInstance().doLogin(loginId, password,
						true,getApplicationContext());

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(APISResult responseObj) {
			
			progressBar.dismiss();
			if (responseObj !=null && responseObj.getResponseCode() == 201) {
				
				Map<?, ?> userObj = responseObj.getResponseData();
				
				String userId = (String) userObj
						.get("_id");				
				String userToken = (String) userObj
						.get("_token");
				
				APIHelper.setUserID(getApplicationContext(), userId);
				APIHelper.setUserToken(getApplicationContext(), userToken);
				//APIHelper.setStringToLocalStorage(getApplicationContext(), Constants.USER_PASSWORD ,password);
				
				Intent myIntent = new Intent(getBaseContext(),
						DailyListActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				startActivity(myIntent);
				finish();
			} else {

				planetHolder.txtValidation.setVisibility(View.VISIBLE);

			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(LoginActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}
	}

}

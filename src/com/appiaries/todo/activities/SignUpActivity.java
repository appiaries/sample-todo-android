package com.appiaries.todo.activities;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.appiaries.APISResult;
import com.appiaries.todo.R;
import com.appiaries.todo.managers.UserManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author ntduc
 * 
 */
@SuppressWarnings("unchecked")
public class SignUpActivity extends BaseActivity {
	PlanetHolder planetHolder;
	ProgressDialog progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		CreateView();
	}

	private void CreateView() {

		planetHolder = new PlanetHolder();

		EditText txtId = (EditText) findViewById(R.id.txtIdAccount);
		EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
		EditText txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
		TextView txtValidation = (TextView) findViewById(R.id.validation);

		Button btnOk = (Button) findViewById(R.id.btnOk);

		planetHolder.txtValidation = txtValidation;
		planetHolder.txtEmailAddress = txtEmailAddress;
		planetHolder.txtId = txtId;
		planetHolder.txtPassword = txtPassword;
		planetHolder.btnOk = btnOk;

		planetHolder.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (validationFrom(planetHolder)) {
					String strLoginId = planetHolder.txtId.getText().toString();
					String strPassword = planetHolder.txtPassword.getText()
							.toString();
					String strEmail = planetHolder.txtEmailAddress.getText()
							.toString();

					HashMap<String, String> data = new HashMap<String, String>();
					data.put("loginId", strLoginId);
					data.put("password", strPassword);
					data.put("email", strEmail);

					DoRegisterAsyncTask task = new DoRegisterAsyncTask();
					task.execute(data);
				} else {
					planetHolder.txtValidation.setText(R.string.mesError);
					planetHolder.txtValidation.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	/**
	 * validation From
	 * 
	 * @param planetHolder
	 */
	private Boolean validationFrom(final PlanetHolder planetHolder) {

		Boolean flg = true;

		final String txtId = planetHolder.txtId.getText().toString();
		if (!isNullOrEmpty(txtId) || txtId.trim().length() < 3
				|| txtId.trim().length() > 20) {
			planetHolder.txtId
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtId
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		final String pass = planetHolder.txtPassword.getText().toString();
		if (!isNullOrEmpty(pass) || pass.trim().length() < 6) {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		final String emailAddress = planetHolder.txtEmailAddress.getText()
				.toString();
		if (!isValidEmail(emailAddress)) {
			planetHolder.txtEmailAddress
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtEmailAddress
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		return flg;
	}

	/**
	 * validating emails id
	 * 
	 * @param email
	 * @return
	 */
	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
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

	private boolean isNullOrEmpty(String val) {
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
		public EditText txtEmailAddress;
		public Button btnOk;
	}

	private class DoRegisterAsyncTask extends
			AsyncTask<HashMap<String, String>, Void, APISResult> {

		@SuppressWarnings("unused")
		@Override
		protected APISResult doInBackground(HashMap<String, String>... params) {
			try {

				HashMap<String, String> data = params[0];
				String strLoginId = data.get("loginId");
				String strPassword = data.get("password");
				String strEmail = data.get("email");

				HashMap<String, Object> dataObj = new HashMap<String, Object>();
				dataObj.put("auto_login", "true");
				return UserManager.getInstance().registerUser(strLoginId,
						strPassword, strEmail, dataObj);

			} catch (Exception ex) {

				ex.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(APISResult responseObject) {
			super.onPostExecute(responseObject);
			progressBar.dismiss();
			if (responseObject != null
					&& responseObject.getResponseCode() == 201) {
				// TO when the do login successful
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SignUpActivity.this);

				// customize and set title and message content
				TextView customTitle = new TextView(SignUpActivity.this);

				customTitle.setText("ã�”ç¢ºèª�");
				customTitle.setPadding(10, 10, 10, 10);
				customTitle.setGravity(Gravity.CENTER);
				customTitle.setTextSize(20);

				builder.setCustomTitle(customTitle);

				TextView customMessage = new TextView(SignUpActivity.this);

				customMessage.setPadding(10, 40, 10, 40);
				customMessage.setText("ã�”æ¡ˆå†…ã�®ã�Ÿã‚�ã�®ãƒ¡ãƒ¼ãƒ«ã�Œé€�ä¿¡ã�•ã‚Œã�¾ã�—ã�Ÿã€‚ã�”ç¢ºèª�ä¸‹ã�•ã�„ã€‚");
				customMessage.setGravity(Gravity.CENTER_HORIZONTAL);
				customMessage.setTextSize(20);

				builder.setView(customMessage);

				// handle cancel button click
				builder.setNegativeButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent myIntent = new Intent(
										SignUpActivity.this,
										LoginActivity.class);
								startActivity(myIntent);
								finish();
							}
						});

				builder.show();
			} else {
				planetHolder.txtValidation.setText(R.string.mesError);
				planetHolder.txtValidation.setVisibility(View.VISIBLE);
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(SignUpActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

	}
}

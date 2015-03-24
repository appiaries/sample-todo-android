package com.appiaries.todo.activities;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.appiaries.APISResult;
import com.appiaries.todo.R;
import com.appiaries.todo.jsonmodels.User;
import com.appiaries.todo.managers.UserManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

@SuppressWarnings("unchecked")
public class AccountActivity extends BaseActivity {

	String screenTag = "AccountActivity";
	PlanetHolder planetHolder;
	ProgressDialog progressBar;
	User userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		AccountActivity.this.setTitle("戻る");
		AccountActivity.this.getActionBar().setDisplayUseLogoEnabled(false);

		new getInformationAsynTask().execute();

		CreateView();

	}

	private void CreateView() {

		planetHolder = new PlanetHolder();

		TextView txtValidation = (TextView) findViewById(R.id.txtAccountValidation);
		TextView tvId = (TextView) findViewById(R.id.tvId);
		EditText txtPassword = (EditText) findViewById(R.id.txtPasswordAccount);
		EditText txtEmail = (EditText) findViewById(R.id.txtEmailAccount);
		Button btnChange = (Button) findViewById(R.id.btnChangeAccount);

		planetHolder.txtValidation = txtValidation;
		planetHolder.tvId = tvId;
		planetHolder.txtEmail = txtEmail;
		planetHolder.txtPassword = txtPassword;
		planetHolder.btnChange = btnChange;

		planetHolder.btnChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (validationForm(planetHolder)) {

					String password = "";

					if (planetHolder.txtPassword.getText().toString().length() > 0) {
						password = planetHolder.txtPassword.getText()
								.toString();
					}

					String email = planetHolder.txtEmail.getText().toString();
					HashMap<String, String> data = new HashMap<String, String>();
					if(!email.isEmpty() && !email.equals(userInfo.getEmail())){
						data.put("email", email);
					}

					if (!password.isEmpty() && !password.equals("")) {
						data.put("password", password);
					}
					if(data.size() > 0){
						new DoChangeAccountAsyncTask().execute(data);
					}
					

				} else {
					planetHolder.txtValidation.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	private boolean validationForm(final PlanetHolder planetHolder) {
		boolean flg = true;

		final String pass = planetHolder.txtPassword.getText().toString();
		final String email = planetHolder.txtEmail.getText().toString();
		if (!isValidPassword(pass)) {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtPassword
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		if (!isValidEmail(email)) {
			planetHolder.txtEmail
					.setBackgroundResource(R.drawable.focus_border_style);
			flg = false;
		} else {
			planetHolder.txtEmail
					.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		return flg;
	}

	private boolean isValidPassword(String val) {
		if (val.trim().length() > 6 && val.length() < 20) {
			return true;
		} else if (val.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/* *********************************
	 * We use the holder pattern It makes the view faster and avoid finding the
	 * component *********************************
	 */
	private static class PlanetHolder {

		public TextView tvId;
		public TextView txtValidation;
		public EditText txtEmail;
		public EditText txtPassword;
		public Button btnChange;
	}

	private class DoChangeAccountAsyncTask extends
			AsyncTask<HashMap<String, String>, Void, APISResult> {

		@Override
		protected APISResult doInBackground(HashMap<String, String>... params) {
			HashMap<String, String> data = params[0];
			String email = data.get("email");
			String password = data.get("password");
			HashMap<String, String> attribute = new HashMap<String, String>();

			try {

				return UserManager.getInstance().updateUser(password, email,
						attribute);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(AccountActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(APISResult responseObj) {
			progressBar.dismiss();
			if (responseObj.getResponseCode() == 200) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AccountActivity.this);

				TextView customTitle = new TextView(AccountActivity.this);
				customTitle.setText("ご確認");
				customTitle.setPadding(10, 10, 10, 10);
				customTitle.setGravity(Gravity.CENTER);
				customTitle.setTextSize(20);

				builder.setCustomTitle(customTitle);

				TextView customMessage = new TextView(AccountActivity.this);

				customMessage.setPadding(10, 40, 10, 40);
				customMessage.setText("ご案内のためのメールが送信されました。ご確認下さい。");
				customMessage.setGravity(Gravity.CENTER_HORIZONTAL);
				customMessage.setTextSize(20);

				builder.setView(customMessage);

				// handle cancel button click
				builder.setNegativeButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});

				builder.show();
			} else {

				planetHolder.txtValidation.setVisibility(View.VISIBLE);

			}

		}

	}

	private class getInformationAsynTask extends AsyncTask<Void, Void, User> {

		@Override
		protected User doInBackground(Void... params) {
			try {
				return UserManager.getInstance().getUserInformation();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			progressBar.dismiss();
			if (result != null) {
				userInfo = result;
				planetHolder.tvId.setText(result.getLoginId());
				planetHolder.txtEmail.setText(result.getEmail());
				//planetHolder.txtPassword.setText(APIHelper.getStringInLocalStorage(getApplicationContext(), Constants.USER_PASSWORD));
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(AccountActivity.this);
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d(screenTag, "back to M1 click");
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

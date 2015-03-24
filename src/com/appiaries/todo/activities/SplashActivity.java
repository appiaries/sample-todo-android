package com.appiaries.todo.activities;

import com.appiaries.APIS;
import com.appiaries.todo.R;
import com.appiaries.todo.common.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// APIS Initialize
		APIS.initialize(Constants.APIS_DATASTORE_ID, Constants.APIS_APP_ID, Constants.APIS_APP_TOKEN, getApplicationContext());

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// Session is still valid => Open Main Activity
				Intent i = new Intent(SplashActivity.this, TopActivity.class);

				startActivity(i);
				finish();
			}
		}, 3000);
	}
}

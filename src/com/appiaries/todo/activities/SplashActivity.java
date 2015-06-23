//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import com.appiaries.baas.sdk.AB;
import com.appiaries.todo.R;
import com.appiaries.todo.models.Task;
import com.appiaries.todo.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Arrays;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

        // Configure SDK
        AB.Config.setDatastoreID(getString(R.string.appiaries__datastore_id));
        AB.Config.setApplicationID(getString(R.string.appiaries__application_id));
        AB.Config.setApplicationToken(getString(R.string.appiaries__application_token));
        //>> Twitter
        AB.Config.Twitter.setEnabled(true);
        AB.Config.Twitter.setConsumerKey(getString(R.string.twitter__consumer_key));
        AB.Config.Twitter.setConsumerSecret(getString(R.string.twitter__consumer_secret));
        //>> Facebook
        AB.Config.Facebook.setEnabled(true);
        AB.Config.Facebook.setPermissions(Arrays.asList("email"));
        AB.Config.Facebook.setUrlSchemeSuffix("");
        //>> MISC
        AB.Config.setUserClass(User.class); //NOTE: use custom ABUser class
        //>> activate
        AB.activate(getApplicationContext());

        //FIXME: registerClasses
        AB.registerClass(Task.class);
        AB.registerClass(User.class);


        new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(SplashActivity.this, TopActivity.class);
				startActivity(intent);
				finish();
			}
		}, 3000);
	}

}

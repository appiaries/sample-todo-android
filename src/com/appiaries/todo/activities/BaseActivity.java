package com.appiaries.todo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread,
					Throwable paramThrowable) {
				
				Log.e("UnCaughtException", paramThrowable.getLocalizedMessage());
				
				Intent errorIntent = new Intent(BaseActivity.this, ErrorActivity.class);
				errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(errorIntent);
				System.exit(0);
			}
		});
	}
}
package com.appiaries.todo.activities;


import com.appiaries.todo.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ErrorActivity extends Activity{
	
	String screenTag = "ErrorActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		Button btnError = (Button) findViewById(R.id.btnError);
		
		btnError.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO exit app
				Log.d(screenTag,"exit app clicked" );
				System.exit(2);
			}
		});
	}
	
}

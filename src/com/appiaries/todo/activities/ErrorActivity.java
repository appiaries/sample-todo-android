//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import com.appiaries.todo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ErrorActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

    private void setupView() {
        setContentView(R.layout.activity_error);

        Button abortButton = (Button) findViewById(R.id.button_abort);
        abortButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(2);
            }
        });
    }

}

//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import com.appiaries.todo.R;
import com.appiaries.todo.common.PreferenceHelper;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
		}
	}

    private void setupView() {
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SettingsActivity.this.setTitle(R.string.settings__title);
        SettingsActivity.this.getActionBar().setDisplayUseLogoEnabled(false);

        if (!PreferenceHelper.loadIsSNSAccount(SettingsActivity.this)) {
            final TextView logoutRow = (TextView) findViewById(R.id.text_account);
            logoutRow.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(SettingsActivity.this, AccountSettingActivity.class);
                    startActivity(intent);
                }
            }));
        }
    }

}

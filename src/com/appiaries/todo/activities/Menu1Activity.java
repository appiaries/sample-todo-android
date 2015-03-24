package com.appiaries.todo.activities;

import com.appiaries.todo.R;
import com.appiaries.todo.activities.Menu1Activity;
import com.appiaries.todo.common.APIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Menu1Activity extends BaseActivity {
	String screenTag = "Menu1Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu1);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		Menu1Activity.this.setTitle("戻る");
		Menu1Activity.this.getActionBar().setDisplayUseLogoEnabled(false);

		if (!APIHelper.iSNSAccount(Menu1Activity.this)) {
			final TextView logoutRow = (TextView) findViewById(R.id.account);

			logoutRow.setOnClickListener((new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO call to account screen
					Intent myIntent = new Intent(Menu1Activity.this,
							AccountActivity.class);
					startActivity(myIntent);
					Log.d(screenTag, "account click");
				}
			}));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d(screenTag, "back to C1 click");
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

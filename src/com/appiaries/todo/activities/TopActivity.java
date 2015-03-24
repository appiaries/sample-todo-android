package com.appiaries.todo.activities;

import java.util.Map;

import org.apache.http.HttpStatus;

import com.appiaries.APISException;
import com.appiaries.APISFacebookUtils;
import com.appiaries.APISResult;
import com.appiaries.APISTwitterUtils;
import com.appiaries.GenericCallback;
import com.appiaries.todo.R;
import com.appiaries.todo.common.APIHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TopActivity extends BaseActivity {

	String screenTag = "TopActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);

		LinearLayout groupNormal = (LinearLayout) findViewById(R.id.groupNormal);
		LinearLayout groupSNS = (LinearLayout) findViewById(R.id.groupSNS);

		if (APIHelper.isAlreadyRegistered(TopActivity.this)) {
			// Check Account is SNS or Normal
			if (APIHelper.iSNSAccount(TopActivity.this)) {
				groupSNS.setVisibility(View.VISIBLE);
				groupNormal.setVisibility(View.GONE);
			} else {
				groupSNS.setVisibility(View.GONE);
				groupNormal.setVisibility(View.VISIBLE);
			}
		} else {
			groupSNS.setVisibility(View.VISIBLE);
			groupNormal.setVisibility(View.VISIBLE);
		}

		// ******* DO NOT REMOVE THIS CODE ********
		// ******* Generate Key Hashes for Facebook SDK *******
		/*
		 * try { PackageInfo info = getPackageManager().getPackageInfo(
		 * "com.appiaries.todo", PackageManager.GET_SIGNATURES); for (Signature
		 * signature : info.signatures) { MessageDigest md =
		 * MessageDigest.getInstance("SHA"); md.update(signature.toByteArray());
		 * String sign = Base64 .encodeToString(md.digest(), Base64.DEFAULT);
		 * 
		 * Log.e("MY KEY HASH:", sign);
		 * 
		 * } } catch (NameNotFoundException e) { } catch
		 * (NoSuchAlgorithmException e) { }
		 */

		// Initializing the Facebook connection utility
		APISFacebookUtils.initialize();

		// Initializing the Twitter connection utility
		APISTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
				getString(R.string.twitter_consumer_secret));

		Button btnLogin = (Button) findViewById(R.id.btnLoginTop);
		Button btnRegist = (Button) findViewById(R.id.btnRegistTop);
		Button btnFB = (Button) findViewById(R.id.btnFB);
		Button btnTwit = (Button) findViewById(R.id.btnTwit);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(screenTag, "btn loginTop clicked");
				Intent myIntent = new Intent(TopActivity.this,
						LoginActivity.class);
				startActivity(myIntent);
				// finish();
			}
		});

		btnRegist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(screenTag, "btn registTop clicked");
				Intent myIntent = new Intent(TopActivity.this,
						SignUpActivity.class);
				startActivity(myIntent);
				// finish();
			}
		});

		btnFB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(screenTag, "btn facebook clicked");

				boolean autoLogin = true;

				APISFacebookUtils.logIn(TopActivity.this, autoLogin,
						new GenericCallback() {

							@Override
							public void done(APISResult result, APISException e) {
								if (e != null) {
									// Error while authenticating
									Toast.makeText(
											getApplicationContext(),
											"Facebook connection failed ["
													+ "Reason:"
													+ e.getMessage() + "]",
											Toast.LENGTH_LONG).show();
								} else {
									if (result.getResponseCode() == HttpStatus.SC_CREATED) {
										// On Sign-up (App User registration +
										// login)
										Log.d("Facebook connection success",
												"["
														+ "Status:"
														+ result.getResponseCode()
														+ ", Response:"
														+ result.getResponseMessage()
														+ ", Location:"
														+ result.getLocation()
														+ "]");

										Map<?, ?> userData = result
												.getResponseData();

										String userId = (String) userData
												.get("_id");
										String userToken = (String) userData
												.get("_token");

										// Save userId
										APIHelper
												.setUserID(
														getApplicationContext(),
														userId);

										// Save token
										APIHelper.setUserToken(
												getApplicationContext(),
												userToken);

										// Marked as SNS account
										APIHelper.setIsSNSAccount(
												getApplicationContext(), true);

										// Transition to DailyListActivity
										Intent i = new Intent(TopActivity.this,
												DailyListActivity.class);

										startActivity(i);
										// finish();

									} else {
										// On Sign-in (login)
										Log.d("Facebook connection success",
												"Status:"
														+ result.getResponseCode()
														+ ", Response:"
														+ result.getResponseMessage()
														+ "]");

										Map<?, ?> userData = result
												.getResponseData();

										String userId = (String) userData
												.get("_id");
										String userToken = (String) userData
												.get("_token");

										// Save token
										APIHelper.setUserToken(
												getApplicationContext(),
												userToken);

										// Marked as SNS account
										APIHelper.setIsSNSAccount(
												getApplicationContext(), true);

										// Transition to DailyListActivity
										Intent i = new Intent(TopActivity.this,
												DailyListActivity.class);

										startActivity(i);
										// finish();
									}
								}
							}
						});

			}
		});

		btnTwit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(screenTag, "btn twitter clicked");

				boolean autoLogin = true;

				// Executing the SNS Login API
				APISTwitterUtils.logIn(TopActivity.this, autoLogin,
						new GenericCallback() {
							@Override
							// SNS Login API callback handler
							public void done(APISResult result, APISException e) {
								if (e != null) {
									// Error while authenticating
									Log.d("Twitter connection failed", "["
											+ "Reason:" + e.getMessage() + "]");
								} else {
									if (result.getResponseCode() == HttpStatus.SC_CREATED) {
										// On Sign-up (App User registration +
										// login)
										Log.d("Twitter connection success",
												"["
														+ "Status:"
														+ result.getResponseCode()
														+ ", Response:"
														+ result.getResponseMessage()
														+ ", Location:"
														+ result.getLocation()
														+ "]");

										Map<?, ?> userData = result
												.getResponseData();

										String userId = (String) userData
												.get("_id");
										String userToken = (String) userData
												.get("_token");

										// Save token
										APIHelper.setUserToken(
												getApplicationContext(),
												userToken);

										// Marked as SNS account
										APIHelper.setIsSNSAccount(
												getApplicationContext(), true);

										// Transition to DailyListActivity
										Intent i = new Intent(TopActivity.this,
												DailyListActivity.class);

										startActivity(i);
										finish();

									} else {
										// On Sign-in (login)
										Log.d("Twitter connection success",
												"["
														+ "Status:"
														+ result.getResponseCode()
														+ ", Response:"
														+ result.getResponseMessage()
														+ "]");

										Map<?, ?> userData = result
												.getResponseData();

										String userId = (String) userData
												.get("_id");
										String userToken = (String) userData
												.get("_token");

										// Save token
										APIHelper.setUserToken(
												getApplicationContext(),
												userToken);

										// Marked as SNS account
										APIHelper.setIsSNSAccount(
												getApplicationContext(), true);

										// Transition to DailyListActivity
										Intent i = new Intent(TopActivity.this,
												DailyListActivity.class);

										startActivity(i);
										finish();
									}
								}
							}
						});

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		APISFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

}

//
// Copyright (c) 2014 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.activities;

import com.appiaries.baas.sdk.AB;
import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.todo.R;
import com.appiaries.todo.common.PreferenceHelper;
import com.appiaries.todo.models.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class TopActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        AB.FacebookService.finishAuthentication(requestCode, resultCode, data);
    }

    private void setupView() {
        setContentView(R.layout.activity_top);

        LinearLayout layoutGroupNormal = (LinearLayout) findViewById(R.id.group_normal);
        LinearLayout layoutGroupSNS    = (LinearLayout) findViewById(R.id.group_sns);

        boolean isAlreadySignedUp = AB.Session.getToken() != null;
        if (isAlreadySignedUp) {
            // Check Account is SNS or Normal
            if (PreferenceHelper.loadIsSNSAccount(TopActivity.this)) {
                layoutGroupSNS.setVisibility(View.VISIBLE);
                layoutGroupNormal.setVisibility(View.GONE);
            } else {
                layoutGroupSNS.setVisibility(View.GONE);
                layoutGroupNormal.setVisibility(View.VISIBLE);
            }
        } else {
            layoutGroupSNS.setVisibility(View.VISIBLE);
            layoutGroupNormal.setVisibility(View.VISIBLE);
        }

        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button signUpButton = (Button) findViewById(R.id.button_signup);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Button loginFacebookButton = (Button) findViewById(R.id.button_login_facebook);
        loginFacebookButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AB.FacebookService.logIn(TopActivity.this, new ResultCallback<User>() {
                    @Override
                    public void done(ABResult<User> result, ABException e) {
                        if (e == null) {
                            int code = result.getCode();
                            if (code == ABStatus.OK || code == ABStatus.CREATED) {
                                User loggedInUser = AB.Session.getUser();
                                if (loggedInUser != null) {
                                    // Save userId, token and marked as SNS account
                                    Context context = getApplicationContext();
                                    PreferenceHelper.saveUserId(context, loggedInUser.getID());
                                    PreferenceHelper.saveToken(context, AB.Session.getToken());
                                    PreferenceHelper.saveIsSNSAccount(context, true);

                                    // Transition to Task List
                                    Intent intent = new Intent(TopActivity.this, TaskListActivity.class);
                                    startActivity(intent);
                                }
                                showMessage(TopActivity.this, R.string.message_success__signed_up);
                            } else {
                                showUnexpectedStatusCodeError(TopActivity.this, code);
                            }
                        } else {
                            if (e.getCode() == ABStatus.OPERATION_CANCELLED) {
                                showMessage(TopActivity.this, R.string.message_success__cancelled);
                            } else {
                                showError(TopActivity.this, e);
                            }
                        }
                    }
                }, AB.UserLogInOption.LOGIN_AUTOMATICALLY);
            }
        });

        Button loginTwitterButton = (Button) findViewById(R.id.button_login_twitter);
        loginTwitterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AB.TwitterService.logIn(TopActivity.this, new ResultCallback<User>() {
                    @Override
                    public void done(ABResult<User> result, ABException e) {
                        if (e == null) {
                            int code = result.getCode();
                            if (code == ABStatus.OK || code == ABStatus.CREATED) {
                                User loggedInUser = AB.Session.getUser();
                                if (loggedInUser != null) {
                                    // Save userId, token and marked as SNS account
                                    Context context = getApplicationContext();
                                    PreferenceHelper.saveUserId(context, loggedInUser.getID());
                                    PreferenceHelper.saveToken(context, AB.Session.getToken());
                                    PreferenceHelper.saveIsSNSAccount(context, true);

                                    // Transition to Task List
                                    Intent intent = new Intent(TopActivity.this, TaskListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                showMessage(TopActivity.this, R.string.message_success__signed_up);
                            } else {
                                showUnexpectedStatusCodeError(TopActivity.this, code);
                            }
                        } else {
                            if (e.getCode() == ABStatus.OPERATION_CANCELLED) {
                                showMessage(TopActivity.this, R.string.message_success__cancelled);
                            } else {
                                showError(TopActivity.this, e);
                            }
                        }
                    }
                }, AB.UserLogInOption.LOGIN_AUTOMATICALLY);
            }
        });
    }

}

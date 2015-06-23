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
import com.appiaries.todo.common.Constants;
import com.appiaries.todo.common.PreferenceHelper;
import com.appiaries.todo.common.Validator;
import com.appiaries.todo.models.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class LoginActivity extends BaseActivity {

    private static class ViewHolder {
        public TextView errorMessages;
        public EditText loginId;
        public EditText password;
        public Button loginButton;
    }

    ViewHolder mViewHolder;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupView();
	}

    private void setupView() {
        setContentView(R.layout.activity_login);

		mViewHolder = new ViewHolder();
		mViewHolder.errorMessages = (TextView) findViewById(R.id.text_error_messages);
		mViewHolder.loginId       = (EditText) findViewById(R.id.edit_login_id);
		mViewHolder.password      = (EditText) findViewById(R.id.edit_password);
		mViewHolder.loginButton   = (Button)   findViewById(R.id.button_login);
		mViewHolder.loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (validate(mViewHolder)) {
                    String loginId = mViewHolder.loginId.getText().toString();
                    String password = mViewHolder.password.getText().toString();

                    User user = new User();
                    user.setLoginId(loginId);
                    user.setPassword(password);

                    final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__processing);
                    user.logIn(new ResultCallback<User>() {
                        @Override
                        public void done(ABResult<User> result, ABException e) {
                            progress.dismiss();
                            if (e == null) {
                                int code = result.getCode();
                                if (code == ABStatus.CREATED) {
                                    Context context = getApplicationContext();
                                    PreferenceHelper.saveUserId(context, AB.Session.getUser().getID()); //FIXME: auto-login にしていれば不要なはず
                                    PreferenceHelper.saveToken(context, AB.Session.getToken()); //FIXME: auto-login にしていれば不要なはず
                                    //PreferenceHelper.saveString(ctx, PreferenceHelper.PREF_KEY_USER_PASSWORD, password);

                                    Intent intent = new Intent(getBaseContext(), TaskListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    showUnexpectedStatusCodeError(LoginActivity.this, code);
                                }
                            } else {
                                mViewHolder.errorMessages.setVisibility(View.VISIBLE);
                                if (e.getCode() == ABStatus.UNPROCESSABLE_ENTITY) {
                                    mViewHolder.errorMessages.setText(R.string.message_error__authentication_failure);
                                } else {
                                    mViewHolder.errorMessages.setText(R.string.message_error__wrong_input);
                                }
                                showError(LoginActivity.this, e);
                            }
                        }
                    });
                }
            }
        });

        // for Debug
        mViewHolder.loginId.setText(R.string.login__default_login_id);
        mViewHolder.password.setText(R.string.login__default_password);
	}

	private boolean validate(final ViewHolder viewHolder) {
		boolean isValid = true;
		final String loginId = viewHolder.loginId.getText().toString();
		if (TextUtils.isEmpty(loginId) || loginId.trim().length() < Constants.ID_MIN_LENGTH || loginId.trim().length() > Constants.ID_MAX_LENGTH) {
			viewHolder.loginId.setBackgroundResource(R.drawable.focus_border_style);
			isValid = false;
		} else {
			viewHolder.loginId.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		final String password = viewHolder.password.getText().toString();
		if (!isValidPassword(password)) {
			viewHolder.password.setBackgroundResource(R.drawable.focus_border_style);
			isValid = false;
		} else {
			viewHolder.password.setBackgroundResource(R.drawable.edit_text_border_style);
		}
		return isValid;
	}

	private boolean isValidPassword(String val) {
		boolean isValid = false;
        if (!Validator.isNotEmpty(val)) {
			mViewHolder.errorMessages.setVisibility(View.VISIBLE);
			mViewHolder.errorMessages.setText(R.string.message_error__wrong_input);
		} else if (!Validator.isValidPassword(val)) {
			mViewHolder.errorMessages.setVisibility(View.VISIBLE);
			mViewHolder.errorMessages.setText(R.string.message_error__invalid_password);
		} else {
			isValid = true;
		}
		return isValid;
	}

}

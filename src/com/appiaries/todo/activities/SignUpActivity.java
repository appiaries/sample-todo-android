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
import com.appiaries.todo.common.Validator;
import com.appiaries.todo.models.User;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressWarnings("unchecked")
public class SignUpActivity extends BaseActivity {

    private static class ViewHolder {
        public TextView errorMessages;
        public EditText loginId;
        public EditText password;
        public EditText email;
        public Button signUpButton;
    }

    ViewHolder mViewHolder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupView();
	}

	private void setupView() {
        setContentView(R.layout.activity_signup);

		mViewHolder = new ViewHolder();
		mViewHolder.errorMessages = (TextView) findViewById(R.id.text_error_messages);
		mViewHolder.email         = (EditText) findViewById(R.id.edit_email);
		mViewHolder.loginId       = (EditText) findViewById(R.id.edit_login_id);
		mViewHolder.password      = (EditText) findViewById(R.id.edit_password);
		mViewHolder.signUpButton  = (Button) findViewById(R.id.button_signup);
		mViewHolder.signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (validate(mViewHolder)) {

                    User user = new User();
                    user.setLoginId(mViewHolder.loginId.getText().toString());
                    user.setPassword(mViewHolder.password.getText().toString());
                    user.setEmail(mViewHolder.email.getText().toString());

                    final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__processing);
                    user.signUp(new ResultCallback<User>() {
                        @Override
                        public void done(ABResult<User> result, ABException e) {
                            progress.dismiss();
                            if (e == null) {
                                if (result.getCode() == ABStatus.CREATED) {
                                    createAndShowConfirmationDialog(
                                            R.string.signup__signup_confirm_title,
                                            R.string.signup__signup_confirm_message,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                    );
                                }
                            } else {
                                if (e.getCode() == ABStatus.CONFLICT) {
                                    mViewHolder.errorMessages.setText(R.string.message_error__duplicated_id);
                                    mViewHolder.errorMessages.setVisibility(View.VISIBLE);
                                } else {
                                    mViewHolder.errorMessages.setText(R.string.message_error__wrong_input);
                                    mViewHolder.errorMessages.setVisibility(View.VISIBLE);
                                }
                                showError(SignUpActivity.this, e);
                            }
                        }
                    }, AB.UserSignUpOption.LOGIN_AUTOMATICALLY);
                }
            }
        });
	}

	private boolean validate(final ViewHolder viewHolder) {
		boolean isValid = true;
		int errorMessage = R.string.message_error__wrong_input;
        //>> Login ID
		String loginId = viewHolder.loginId.getText().toString();
		if (TextUtils.isEmpty(loginId) || loginId.trim().length() < Constants.ID_MIN_LENGTH || loginId.trim().length() > Constants.ID_MAX_LENGTH) {
			viewHolder.loginId.setBackgroundResource(R.drawable.focus_border_style);
			isValid = false;
		} else {
			viewHolder.loginId.setBackgroundResource(R.drawable.edit_text_border_style);
		}
        //>> Password
		String password = viewHolder.password.getText().toString();
		if (TextUtils.isEmpty(password)) {
			viewHolder.password.setBackgroundResource(R.drawable.focus_border_style);
			isValid = false;
		} else if (password.trim().length() < Constants.PASSWORD_MIN_LENGTH || password.trim().length() > Constants.PASSWORD_MAX_LENGTH) {
			viewHolder.password.setBackgroundResource(R.drawable.focus_border_style);
			errorMessage = R.string.message_error__invalid_password;
			isValid = false;
		} else {
			viewHolder.password.setBackgroundResource(R.drawable.edit_text_border_style);
		}
        //>> Email
		String email = viewHolder.email.getText().toString();
		if (!Validator.isValidEmail(email)) {
			viewHolder.email.setBackgroundResource(R.drawable.focus_border_style);
			isValid = false;
		} else {
			viewHolder.email.setBackgroundResource(R.drawable.edit_text_border_style);
		}

		if (!isValid) {
			viewHolder.errorMessages.setText(errorMessage);
			viewHolder.errorMessages.setVisibility(View.VISIBLE);
		}
		return isValid;
	}

}

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
import com.appiaries.todo.common.Validator;
import com.appiaries.todo.models.User;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class AccountSettingActivity extends BaseActivity {

    private static class ViewHolder {
        public TextView loginId;
        public TextView errorMessages;
        public EditText email;
        public EditText password;
        public Button saveButton;
    }

    ViewHolder mViewHolder;
	User mUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setupView();

        User loggedInUser = AB.Session.getUser();
        if (loggedInUser != null) {
            final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__loading);
            AB.UserService.fetch(loggedInUser, new ResultCallback<User>() {
                @Override
                public void done(ABResult<User> result, ABException e) {
                    progress.dismiss();
                    if (e == null) {
                        if (result != null) {
                            mUser = result.getData();

                            // XXX: *** IMPORTANT *****************************************************************************************
                            // APIサーバから返却されるJSONには「パスワード」は含まれません。
                            // そのため本画面でパスワードをセットした場合、オブジェクト内部では「パスワード」
                            // フィールドが追加されたものと見なされ、SDK内部では処理効率の観点から、PATCH(フィールド更新) ではなく
                            // PUT(置換) リクエストが送信されます。しかしながら、2015.5時点のAPIサーバが提供する機能では、
                            // ユーザに任意のフィールドを追加／削除することができないため、結果としてエラーレスポンス (405) が返却されます。
                            // 以下では、取得したユーザ・オブジェクトに対してダミーの「パスワード」フィールドを追加することで、この問題を回避しています。
                            // ************************************************************************************************************
                            mUser.put(User.Field.PASSWORD, "");
                            mUser.apply();
                            mViewHolder.loginId.setText(mUser.getLoginId());
                            if (mUser.isEmailVerified()) {
                                mViewHolder.email.setText(mUser.getEmail());
                            } else {
                                mViewHolder.email.setEnabled(false);
                                mViewHolder.email.setBackgroundColor(Color.LTGRAY);
                                mViewHolder.email.setHint(R.string.account_setting__email_not_yet_verified);
                            }
                            /* mViewHolder.password.setText(PreferenceHelper.loadString(getApplicationContext(), Constants.PREF_KEY_USER_PASSWORD)); */
                        }
                    } else {
                        showError(AccountSettingActivity.this, e);
                    }
                }
            });
        }
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
        setContentView(R.layout.activity_account_setting);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        AccountSettingActivity.this.setTitle(R.string.account_setting__title);
        AccountSettingActivity.this.getActionBar().setDisplayUseLogoEnabled(false);

        mViewHolder = new ViewHolder();
        mViewHolder.errorMessages = (TextView) findViewById(R.id.text_error_messages);
		mViewHolder.loginId       = (TextView) findViewById(R.id.edit_login_id);
		mViewHolder.email         = (EditText) findViewById(R.id.edit_email);
		mViewHolder.password      = (EditText) findViewById(R.id.text_password);
		mViewHolder.saveButton    = (Button)   findViewById(R.id.button_save);
		mViewHolder.saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!validate(mViewHolder)) {
                    mViewHolder.errorMessages.setVisibility(View.VISIBLE);
                    return;
                }

                // Set password if needed
                String password = mViewHolder.password.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    mUser.setPassword(password);
                }
                // Set email if needed
                String email = mViewHolder.email.getText().toString();
                if (!email.isEmpty() && !email.equals(mUser.getEmail())) {
                    mUser.setEmail(email);
                }

                //Update user
                if (mUser.isDirty()) {
                    final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__loading);
                    mUser.save(new ResultCallback<User>() {
                        @Override
                        public void done(ABResult<User> result, ABException e) {
                            progress.dismiss();
                            if (e == null) {
                                int code = result.getCode();
                                if (code == ABStatus.OK && result.getData() != null) {
                                    mUser = result.getData();

                                    createAndShowConfirmationDialog(
                                            R.string.account_setting__save_confirm_title,
                                            R.string.account_setting__save_confirm_message,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });

                                } else {
                                    mViewHolder.errorMessages.setVisibility(View.VISIBLE);
                                    showUnexpectedStatusCodeError(AccountSettingActivity.this, code);
                                }
                            } else {
                                switch (e.getCode()) {
                                    case ABStatus.CONFLICT:
                                        mViewHolder.errorMessages.setText(R.string.message_error__duplicated_id);
                                        break;
                                    case ABStatus.UNPROCESSABLE_ENTITY:
                                        mViewHolder.errorMessages.setText(R.string.message_error__invalid_password);
                                        break;
                                    default:
                                        mViewHolder.errorMessages.setText(e.getMessage());
                                        break;
                                }
                                showError(AccountSettingActivity.this, e);
                            }
                        }
                    });
                }
            }
        });
	}

    private boolean validate(final ViewHolder planetHolder) {
		boolean isValid = true;
        if (mUser.isEmailVerified()) {
            //>> Email
            final String email = planetHolder.email.getText().toString();
            if (!Validator.isValidEmail(email)) {
                planetHolder.email.setBackgroundResource(R.drawable.focus_border_style);
                isValid = false;
            } else {
                planetHolder.email.setBackgroundResource(R.drawable.edit_text_border_style);
            }
        }
        //>> Password
        final String password = planetHolder.password.getText().toString();
        if (!Validator.isValidPassword(password)) {
            mViewHolder.errorMessages.setText(R.string.message_error__invalid_password);
            planetHolder.password.setBackgroundResource(R.drawable.focus_border_style);
            isValid = false;
        } else {
            planetHolder.password.setBackgroundResource(R.drawable.edit_text_border_style);
        }
		return isValid;
	}

}

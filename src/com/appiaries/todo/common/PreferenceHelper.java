//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.common;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class PreferenceHelper {

    /* Local Preferences Storage */
    public static final String PREFS_NAME = "TODOPrefsFile";

    public static final String PREF_KEY_USER_TOKEN     = "TODOAccessToken";
    public static final String PREF_KEY_IS_SNS_ACCOUNT = "TODOIsSNSAccount";
    public static final String PREF_KEY_USER_ID        = "TODOUserID";


    public static String loadUserToken(Context context) {
        SharedPreferences pref = getPreference(context);
        return pref.getString(PreferenceHelper.PREF_KEY_USER_TOKEN, "");
    }

    public static void saveToken(Context context, String userToken) {
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PreferenceHelper.PREF_KEY_USER_TOKEN, userToken);
        editor.apply();
    }

    public static boolean loadIsAlreadyRegistered(Context context) {
        SharedPreferences pref = getPreference(context);
        String accessToken = pref.getString(PreferenceHelper.PREF_KEY_USER_TOKEN, null);
        return (accessToken != null);
    }

    public static boolean loadIsSNSAccount(Context context) {
        SharedPreferences pref = getPreference(context);
        return pref.getBoolean(PreferenceHelper.PREF_KEY_IS_SNS_ACCOUNT, false);
    }

    public static void saveIsSNSAccount(Context context, boolean isSNSAccount) {
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PreferenceHelper.PREF_KEY_IS_SNS_ACCOUNT, isSNSAccount);
        editor.apply();
    }

    public static String loadUserId(Context context) {
        SharedPreferences pref = getPreference(context);
        return pref.getString(PreferenceHelper.PREF_KEY_USER_ID, null);
    }

    public static void saveUserId(Context context, String userId) {
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PreferenceHelper.PREF_KEY_USER_ID, userId);
        editor.apply();
    }

    public static String loadString(Context context, String key) {
        SharedPreferences pref = getPreference(context);
        return pref.getString(key, "");
    }

    public static void saveString(Context context, String key, String value){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PreferenceHelper.PREFS_NAME, 0);
    }

}

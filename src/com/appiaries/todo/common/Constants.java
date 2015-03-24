package com.appiaries.todo.common;

/**
 * @author nmcuong
 *
 */
public class Constants {

	/**
	 * Appiaries Application Token
	 */
	public static final String APIS_APP_TOKEN = "app6f95ed4b9a1183b9f8293328bb";
	
	
	/**
	 * collection name
	 */
	public static final String COLLECTION_TASK = "Tasks";
	public static final String COLLECTION_CATEGORY = "Categories";
	
	
	/**
	 * Appiaries Application ID
	 */
	public static final String APIS_APP_ID = "todo";

	/**
	 * Appiaries Client ID
	 */
	public static final String APIS_CLIENT_ID = "b4335bed6a0c8bf";

	/**
	 * Appiaries Datastore ID
	 */
	public static final String APIS_DATASTORE_ID = "appiaries_sample";	
	
	/**
	 * Appiaries Contract ID
	 */
	public static final String APIS_CONTRACT_ID = "temp";	

	/**
	 * Appiaries Profile API request URL
	 */
	public static final String AUTH_REQUEST_URL = "https://api-oauth.appiaries.com/v1/auth";

	/**
	 * Appiaries Profile API callback URL
	 */
	public static final String AUTH_CALLBACK_URL = "http://callback";

	/**
	 * Appiaries Datastore JSON API URL
	 */
	public static final String DATASTORE_JSON_URL_BASE = "https://api-datastore.appiaries.com/v1/dat";
	
	/**
	 * Appiaries Profile API URL
	 */
	public static final String PROFILE_URL_BASE = "https://api-profiles.appiaries.com/v1";

	/**
	 * Appiaries Datastore API URL
	 */
	public static final String DATASTORE_FILE_URL_BASE = "https://api-datastore.appiaries.com/v1/bin";
	
	// for admin03
	//public static final String DATASTORE_FILE_URL_BASE = "http://develop-datastore02.appiaries.com/v1/bin";
	
	/**
	 * Appiaries Datastore JSON API URL
	 */
	public static final String RESGISTER_REGISTRATION_URL = "https://api-datastore.appiaries.com/v1/push/gcm";
	//public static final String RESGISTER_REGISTRATION_URL = "http://develop-datastore02.appiaries.com/v1/push/gcm";

	/**
	 * Appiaries Datastore JSON API URL
	 */
	// for admin03
	//public static final String USER_PUSH_URL = "http://develop-datastore02.appiaries.com/v1/push/user";
	public static final String USER_PUSH_URL = "https://api-datastore.appiaries.com/v1/push/user";
	
	/**
	 * Image File Collection ID
	 */
	public static final String IMAGE_COLLECTION_ID = "imageFiles";

	
	/**
	 * Date format
	 */
	public static final String DATE_FORMAT = "yyyy/MM/dd";

	
	/**
	 * Date and Time format
	 */
	public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	
	/**
	 * Date and Hour, minute format
	 */
	public static final String DATE_HOUR = "yyyy/MM/dd HH:mm";
	

	/**
	 * Using for Push Notification communicate
	 */
	public static final String REGISTRATION_ID = "TODORegistrationID";

		
	/**
	 * Local Preferences Storage
	 */
	public static final String PREFS_NAME = "TODOPrefsFile";

	public static final String LOGIN_FLAG = "TODOLoginFlag";

	public static final String USER_TOKEN_KEY = "TODOAccessToken";

	public static final String STORE_TOKEN_KEY = "TODOStoreToken";

	public static final String TOKEN_EXPIRE_KEY = "TODOTokenExpire";

	public static final String BLANK_STRING = "";	
		
	public static final String IS_SNS_ACCOUNT  = "TODOIsSNSAccount";
	
	public static final String USER_ID = "TODOUserID";
	
	public static final String USER_PASSWORD = "TODOPassword";
}

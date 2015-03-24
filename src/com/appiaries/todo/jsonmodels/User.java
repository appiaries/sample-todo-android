package com.appiaries.todo.jsonmodels;

import java.util.HashMap;

public class User {

	private String id;
	private String loginId;
	private String password;
	private String email;
	private boolean autoLogin;
	private String nickName;
	private HashMap<String, Object> authData;
	private long cts;
	private long uts;
	private String cby;
	private String uby;

	public User() {}

	public User(String id, String loginId, String password, String email,
			boolean autoLogin, String nickName,
			HashMap<String, Object> authData, long cts, long uts, String cby,
			String uby) {

		this.id = id;
		this.loginId = loginId;
		this.password = password;
		this.email = email;
		this.autoLogin = autoLogin;
		this.nickName = nickName;
		this.authData = authData;
		this.cts = cts;
		this.uts = uts;
		this.cby = cby;
		this.uby = uby;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public HashMap<String, Object> getAuthData() {
		return authData;
	}

	public void setAuthData(HashMap<String, Object> authData) {
		this.authData = authData;
	}

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}

	public long getUts() {
		return uts;
	}

	public void setUts(long uts) {
		this.uts = uts;
	}

	public String getCby() {
		return cby;
	}

	public void setCby(String cby) {
		this.cby = cby;
	}

	public String getUby() {
		return uby;
	}

	public void setUby(String uby) {
		this.uby = uby;
	}

}

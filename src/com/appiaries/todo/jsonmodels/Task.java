package com.appiaries.todo.jsonmodels;

import java.io.Serializable;
import java.util.Date;

import com.appiaries.todo.common.TextHelper;

public class Task implements Comparable<Task>, Serializable{

	private static final long serialVersionUID = -6713532621492168723L;
	private String id;
	private String userId;
	private String categoryId;
	private int type;
	private String title;
	private String text;
	private int status;
	private long scheduledAt;
	private long cts;
	private long uts;
	private String cby;
	private String uby;

	public Task(){}
	
	public Task(String id, String userId, String categoryId, int type,
			String title, String text, int status, long scheduledAt, long cts,
			long uts, String cby, String uby) {
		this.id = id;
		this.userId = userId;
		this.categoryId = categoryId;
		this.type = type;
		this.title = title;
		this.text = text;
		this.status = status;
		this.scheduledAt = scheduledAt;
		this.cts = cts;
		this.uts = uts;
		this.cby = cby;
		this.uby = uby;
	}

	public enum Status {
		NEW, COMPLETED
	}
	
	public enum TaskType {
		NORMAL, IMPORTANT
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getScheduledAt() {
		return TextHelper.getDate(scheduledAt);
	}

	public void setScheduledAt(long scheduledAt) {
		this.scheduledAt = scheduledAt;
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

	@Override
	public int compareTo(Task another) {
		long compareCts = another.getCts();
		if(this.cts > compareCts){
			return 1;
		}else if(this.cts < compareCts){
			return -1;
		}else
			return 0;
	}

}

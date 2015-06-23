//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.models;

import java.util.Date;

public class TaskListItem {

	private boolean isGroupHeader = false;
	private String groupTitle;
	private Date groupDate;
	private Task task;

	public TaskListItem(boolean isHeader, String groupTitle, Date groupDate) {
		this.isGroupHeader = isHeader;
		this.groupTitle = groupTitle;
		this.groupDate = groupDate;
	}
	
	public TaskListItem(boolean isHeader, String groupTitle) {
		this.isGroupHeader = isHeader;
		this.groupTitle = groupTitle;
	}
	
	public TaskListItem(Task task) {
		this.isGroupHeader = false;
		this.task = task;
	}
	
	public boolean isGroupHeader() {
		return isGroupHeader;
	}
	public void setGroupHeader(boolean isGroupHeader) {
		this.isGroupHeader = isGroupHeader;
	}

	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}

	public String getGroupTitle() {
		return groupTitle;
	}
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public Date getGroupDate() {
		return groupDate;
	}
	public void setGroupDate(Date groupDate) {
		this.groupDate = groupDate;
	}		

}

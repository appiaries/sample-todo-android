package com.appiaries.todo.jsonmodels;

import java.util.Date;

public class DailyModel {
	private boolean isGroupHeader = false;
	private String categoryName;
	private Date categoryDate;
	private Task task;	
	
	public DailyModel(boolean isHeader, String categoryName, Date categoryDate)
	{
		this.isGroupHeader = isHeader;
		this.categoryName = categoryName;
		this.categoryDate = categoryDate;
	}
	
	public DailyModel(boolean isHeader, String categoryName)
	{
		this.isGroupHeader = isHeader;
		this.categoryName = categoryName;		
	}
	
	public DailyModel(Task task)
	{
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

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Date getCategoryDate() {
		return categoryDate;
	}

	public void setCategoryDate(Date categoryDate) {
		this.categoryDate = categoryDate;
	}		
}

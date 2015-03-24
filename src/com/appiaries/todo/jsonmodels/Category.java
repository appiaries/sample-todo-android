package com.appiaries.todo.jsonmodels;

import java.util.HashMap;
import java.util.List;

public class Category {

	private String id;
	private String name;
	private long cts;
	private long uts;
	private String cby;
	private String uby;
	
	public Category(){}
	
	public Category(String id, String name, long cts, long uts, String cby, String uby){
		this.id = id;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public HashMap<String, Object> mappingCategoryList(List<Category> categoryList){
		HashMap<String, Object> mappingList = new HashMap<String, Object>();

		for(Category categoryObj : categoryList){
		
			mappingList.put(categoryObj.getId(), categoryObj.getName());
		
		}
		
		return mappingList;
	}
}

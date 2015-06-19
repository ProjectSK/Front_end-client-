package com.d.localdb;


import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

public  class AppUsageRecord implements Record{

	
	private String[] columnNames =  {"package_name", "start_time", "elapsed_time"} ;
	private Integer[] primaryKeyIndexes = {0,1};
	private List<String> elements;
	private static String tableName = "usage";
	
	public AppUsageRecord(List<String> elements){
		
		this.elements = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			this.elements.add(elements.get(i));
		}
	}
	public AppUsageRecord(){		
		this.elements = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			this.elements.add("");
		}
	}
	public List<Integer> getPrimaryKeyIndexes(){
		List<Integer> indexes = new ArrayList<Integer>();		
		for(int i = 0 ; i < primaryKeyIndexes.length; i++){
			indexes.add(primaryKeyIndexes[i]);
		}
		return indexes;
		
	}
	public List<String> getElements() {
		return elements;
	}
	public void setElements(List<String> elements) {
		this.elements = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			this.elements.add(elements.get(i));
		}
	}
	public List<String> getColumnNames() {
		List<String> names = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			names.add(new String(columnNames[i]));
		}
		return names;
	}	
	public String getTableName(){
		return tableName;
	}
	
	
}

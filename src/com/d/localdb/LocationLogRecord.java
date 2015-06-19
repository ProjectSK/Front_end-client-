package com.d.localdb;

import java.util.ArrayList;
import java.util.List;

public class LocationLogRecord implements Record{
	private String[] columnNames =  {"time", "latitude", "longitude"} ;
	private Integer[] primaryKeyIndexes = {0};
	private List<String> elements;
	public static String tableName = "loc";
	
	public LocationLogRecord(List<String> elements){
		
		this.elements = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			this.elements.add(elements.get(i));
		}
	}
	public LocationLogRecord(){		
		this.elements = new ArrayList<String>();		
		for(int i = 0 ; i < columnNames.length; i++){
			this.elements.add("");
		}
	}
	public List<Integer> getPrimaryKeyIndexes(){
		List<Integer> indexes = new ArrayList<Integer>();		
		for(int i = 0 ; i < primaryKeyIndexes.length; i++){
			indexes.add(new Integer(primaryKeyIndexes[i]));
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

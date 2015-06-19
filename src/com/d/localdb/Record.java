package com.d.localdb;

import java.util.List;

public interface Record {
	
	public List<String> getElements();
	public List<Integer> getPrimaryKeyIndexes();
 	public void setElements(List<String> elements);
	public List<String> getColumnNames();
	public String getTableName();
}

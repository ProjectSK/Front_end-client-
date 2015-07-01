package com.d.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;

public class AppUsage {
	/**
	 *  가공된 정보를 건네주기 위한 structure
	 */	
	public class Resource{
		public int numberOfExecution;
		public int overallTime;
		public String PackageName;
		public Resource(String packgeName, int overallTime,
				int numberOfExecution) {
			PackageName = packgeName;
			this.overallTime = overallTime;
			this.numberOfExecution = numberOfExecution;
		}
		
		public void increaseNumberOfExecution() {
			this.numberOfExecution++;
		}
		public void increaseOverallTime(Long elapsedTime) {
			this.overallTime += elapsedTime/1000;
		}
		
	}
	private Context context;
	
	private LocalDB locdb;
	
	
	public AppUsage(Context context){
		this.context = context;
	}
	
	public List<AppUsageRecord> getRecords(int limit){
		List<AppUsageRecord> retValues;
		locdb = new LocalDB(context, AppUsageRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			retValues =locdb.getAll(new AppUsageRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			locdb.close();
		}
		return retValues;
	}
	public List<Resource> getStaticInfos(){
		List<Resource> resources = new ArrayList<Resource>();
		ArrayList<String> packageNames = new ArrayList<String>();
		locdb = new LocalDB(context, AppUsageRecord.TABLE);
		try{
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			List<AppUsageRecord> records  =locdb.getAll(new AppUsageRecord(),new Date(yesterday), null, true, 50000);
			for (AppUsageRecord record : records) {
				if(!packageNames.contains(record.packageName)){
					resources.add(new Resource(record.packageName, 0, 0));
					packageNames.add(record.packageName);
				}
				int idx = packageNames.indexOf(record.packageName);
				resources.get(idx).increaseNumberOfExecution();
				resources.get(idx).increaseOverallTime(record.elapsedTime);
			}
		}
		finally {
			locdb.close();
		}
		return resources;
	}
	

}

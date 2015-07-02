package com.d.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.LocalDB;

public class AppUsage {
	/**
	 *  App Usage 기록의 Statistical information을 전달하는 container
	 */	
	public class StatisticalInfo{
		public int numberOfExecution;
		public int overallTime;
		public String PackageName;
		public StatisticalInfo(String packgeName, int overallTime,
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
	
	/**
	 * 하룻동안 수집된 기록을 상한 만큼 불러오는 method.
	 * 저장된 것의 최신순으로 불려온다.
	 * @param limit DB에서 불러올 Record의 상한
	 * @return DB에서 불러온 a list of  Records.
	 */
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
	/**
	 * 하룻동안 App Usage 기록의 Statistical information을 획득하는 Method.
	 * 각 package의 이름, 하룻동안 실행된 시간, 하룻동안 실행된 횟수를 StatisticalInfo class에 저장하여 전달한다. 
	 * @return the list of statistical informations On App Usage.
	 */
	public List<StatisticalInfo> getStatisticalInfos(){
		List<StatisticalInfo> resources = new ArrayList<StatisticalInfo>();
		ArrayList<String> packageNames = new ArrayList<String>();
		locdb = new LocalDB(context, AppUsageRecord.TABLE);
		try{
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			List<AppUsageRecord> records  =locdb.getAll(new AppUsageRecord(),new Date(yesterday), null, true, 50000);
			for (AppUsageRecord record : records) {
				if(!packageNames.contains(record.packageName)){
					resources.add(new StatisticalInfo(record.packageName, 0, 0));
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

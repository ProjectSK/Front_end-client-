package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.BatteryRecord;
import com.d.localdb.LocalDB;

public class BatteryUsage {

	private Context context;
	LocalDB ldb;
	public BatteryUsage(Context context){
		this.context = context;
	}
	public List<BatteryRecord> getRecords(int limit){
		List<BatteryRecord> records;
		ldb = new LocalDB(context, BatteryRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new BatteryRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}
	/**
	 * return a single Record from battery usage DB
	 * if there are no record on DB, it returns null.
	 * @return
	 */
	public BatteryRecord getSingleRecord(){
		BatteryRecord record = null;
		List<BatteryRecord> records;
		ldb = new LocalDB(context, BatteryRecord.TABLE);
		try{
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new BatteryRecord(),new Date(yesterday), null, true, 1);
			if(records.size() > 0){
				record = records.get(0);
			}
			
		}
		finally {
			ldb.close();
		}
		return record;
	}
	

}

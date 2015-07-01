package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;

public class LocationTracer {
	private Context context;
	LocalDB ldb;
	public LocationTracer(Context context){
		this.context = context;
	}
	public List<LocationLogRecord> getRecords(int limit){
		List<LocationLogRecord> records;
		ldb = new LocalDB(context, LocationLogRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new LocationLogRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}
	
	public LocationLogRecord getSingleRecord(Date from, Date to){
		LocationLogRecord record = null;
		List<LocationLogRecord> records;
		ldb = new LocalDB(context, LocationLogRecord.TABLE);
		try{
			
			records =ldb.getAll(new LocationLogRecord(),from, to, true, 1);
			if(records.size()!=0){
				record = records.get(0);
			}
			
		}
		finally {
			ldb.close();
		}
		return record;
	}

}

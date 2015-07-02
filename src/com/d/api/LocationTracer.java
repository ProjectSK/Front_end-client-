package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;

public class LocationTracer {
	private Context context;
	private LocalDB ldb;
	public LocationTracer(Context context){
		this.context = context;
	}
	/**
	 * 하룻동안 수집된 기록을 상한 만큼 불러오는 method.
	 * 저장된 것의 최신순으로 불려온다.
	 * @param limit DB에서 불러올 Record의 상한
	 * @return DB에서 불러온 a list of  Records.
	 */
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

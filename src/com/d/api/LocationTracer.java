package com.d.api;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.anim;
import android.content.Context;

import com.d.localdb.LocalDB;
import com.d.localdb.LocationLogRecord;
import com.google.android.gms.maps.model.LatLng;

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
	private double getDistance(LocationLogRecord p1, LocationLogRecord p2){
		double R = 6378137; // Earth’s mean radius in meter
		  double dLat = Math.toRadians(p2.latitude - p1.latitude);
		  double dLong = Math.toRadians(p2.longitude - p1.longitude);
		  double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		    Math.cos(Math.toRadians(p1.latitude)) * Math.cos(Math.toRadians(p2.latitude)) *
		    Math.sin(dLong / 2) * Math.sin(dLong / 2);
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		  double d = R * c;
		  return d; // returns the distance in meter
	}
	
	
	/**
	 * 오차거리내의 기록을 같은 위치로 처리하여 유효한 Records를 골라내는 method
	 * @param limit DB에서 불러올 Record의 상한
	 * @param errorDistance 오차거리
	 * @return
	 */
	public List<LocationLogRecord> getUsableRecords(int limit, int errorDistance){
		List<LocationLogRecord> records;
		List<LocationLogRecord> usableRecords = new ArrayList<LocationLogRecord>();
		ldb = new LocalDB(context, LocationLogRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new LocationLogRecord(),new Date(yesterday), null, true, limit);
			if(records.size() != 0){
				usableRecords.add(records.get(0));
				for(int i = 1; i < records.size(); i++){
					LocationLogRecord r = records.get(i);
					LocationLogRecord r2 = usableRecords.get(usableRecords.size()-1);
					
					if(getDistance(r, r2) > errorDistance){
						usableRecords.add(r);
					}
					
				}
			}
			
		}
		finally {
			ldb.close();
		}
		return usableRecords;
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

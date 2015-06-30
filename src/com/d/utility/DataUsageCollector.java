package com.d.utility;

import java.util.Date;

import com.d.localdb.AppUsageRecord;
import com.d.localdb.DataUsageRecord;
import com.d.localdb.LocalDB;

import android.content.Context;
import android.net.TrafficStats;



public class DataUsageCollector {
	
	private class Log {
		public long trans_data;
		public long rec_data;
		public long elapsed_time;
		public long time;
		public Log(long trans_data, long rec_data) {
			super();
			this.trans_data = trans_data;
			this.rec_data = rec_data;
			this.time = System.currentTimeMillis();
		}
	}
	
	private Log previous;
	private Log present;
	private Context context;
	
	public DataUsageCollector(Context context) {
		this.context = context;
		
		long trans;
		long rec;
		locdb = new LocalDB(context, DataUsageRecord.TABLE);
		try {
			DataUsageRecord record;
			record = locdb.getAll(new DataUsageRecord(), null , null , true , 1).get(0);
			Date deviceBootTime = new Date(System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime());
			if(record.time.after(deviceBootTime)){
				trans = record.trans_data;
				rec = record.rec_data;
				previous = new Log(trans, rec);
				previous.time = record.time.getTime();
			}
			else {
				trans = TrafficStats.getMobileTxBytes();
				rec = TrafficStats.getMobileRxBytes();
				previous.elapsed_time = android.os.SystemClock.elapsedRealtime();
			}
			
		} finally {
			locdb.close();
		}
	}
	
	private LocalDB locdb;
	
	private DataUsageRecord getRecord(){
		long trans = TrafficStats.getMobileTxBytes() - previous.trans_data;
		long rec = TrafficStats.getMobileRxBytes() - previous.rec_data;
		present = new Log(trans, rec);
		present.elapsed_time = present.time - previous.time;
		
		previous = present;
		return new DataUsageRecord(new Date(present.time), present.trans_data, present.rec_data, present.elapsed_time);
		
	}
	
	public void saveRecord() {
		locdb = new LocalDB(context, DataUsageRecord.TABLE);
		try {
			DataUsageRecord record = getRecord();
			locdb.addRecord(record);
			
		} finally {
			locdb.close();
		}
	}

}

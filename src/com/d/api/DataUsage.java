package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.DataUsageRecord;
import com.d.localdb.LocalDB;

public class DataUsage {
	private Context context;
	LocalDB ldb;
	public DataUsage(Context context){
		this.context = context;
	}
	public List<DataUsageRecord> getRecords(int limit){
		List<DataUsageRecord> records;
		ldb = new LocalDB(context, DataUsageRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new DataUsageRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}

}

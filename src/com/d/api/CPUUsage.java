package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.CPURecord;
import com.d.localdb.LocalDB;

public class CPUUsage {
	private Context context;
	LocalDB ldb;
	public CPUUsage(Context context){
		this.context = context;
	}
	public List<CPURecord> getRecords(int limit){
		List<CPURecord> records;
		ldb = new LocalDB(context, CPURecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new CPURecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}
}

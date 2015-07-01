package com.d.api;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.d.localdb.LocalDB;
import com.d.localdb.MemoryRecord;

public class MemoryUsage {
	private Context context;
	LocalDB ldb;
	public MemoryUsage(Context context){
		this.context = context;
	}
	public List<MemoryRecord> getRecords(int limit){
		List<MemoryRecord> records;
		ldb = new LocalDB(context, MemoryRecord.TABLE);
		try{
			if(limit <= 0)
				limit = 0;
			long yesterday = System.currentTimeMillis() - (1000* 60 * 60 * 24);
			records =ldb.getAll(new MemoryRecord(),new Date(yesterday), null, true, limit);
			
		}
		finally {
			ldb.close();
		}
		return records;
	}
}

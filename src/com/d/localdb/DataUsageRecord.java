package com.d.localdb;


import java.util.Date;

import com.d.localdb.SQLVColumn.ColumnType;

public class DataUsageRecord implements Record {
	
	public DataUsageRecord(Date time, long trans_data, long rec_data,
			long elapsed_time) {
		super();
		this.time = time;
		this.trans_data = trans_data;
		this.rec_data = rec_data;
		this.elapsed_time = elapsed_time;
	}



	public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(DataUsageRecord.class), "DataUsageRecord", "time",
                new SQLVColumn[] {
                    new ReflVColumn(DataUsageRecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(DataUsageRecord.class, "transmitted", ColumnType.Long, false),
                    new ReflVColumn(DataUsageRecord.class, "received ", ColumnType.Long, false),
                    new ReflVColumn(DataUsageRecord.class, "elapsed_time", ColumnType.Long, false),
                });
	public long trans_data;
	public long rec_data;
	public long elapsed_time;
	public Date time;
	
	
    
    public DataUsageRecord() { }

  
    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }



	@Override
	public String toString() {
		return "DataUsageRecord [time=" + time + ", trans_data=" + trans_data
				+ ", rec_data=" + rec_data + ", elapsed_time=" + elapsed_time
				+ "]";
	}
   
	
}

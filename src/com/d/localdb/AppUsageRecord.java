package com.d.localdb;


import java.util.Date;

import com.d.localdb.SQLVColumn.ColumnType;

public class AppUsageRecord implements Record {
    public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(AppUsageRecord.class), "appUsage", "startTime",
                new SQLVColumn[] {
                    new ReflVColumn(AppUsageRecord.class, "packageName", ColumnType.String, true),
                    new ReflVColumn(AppUsageRecord.class, "startTime", ColumnType.Datetime, true),
                    new ReflVColumn(AppUsageRecord.class, "elapsedTime", ColumnType.Long, false)
                });
    public Long elapsedTime;
    public String packageName;

    
    public Date startTime;
    public AppUsageRecord() { }

    public AppUsageRecord(String packageName, Date startTime, Long elapsedTime) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
    }
    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }
	
    @Override
    public String toString() {
        return "[packageName="+packageName+", startTime="+startTime+", elapsedTime="+elapsedTime+"]";
    }
	
}

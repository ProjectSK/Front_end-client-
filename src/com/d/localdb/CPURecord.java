package com.d.localdb;


import java.util.Date;

import com.d.localdb.SQLVColumn.ColumnType;

public class CPURecord implements Record {
	
	public CPURecord(Date time, Long user, Long system, Long idle, Long other) {
		super();
		this.time = time;
		this.user = user;
		this.system = system;
		this.idle = idle;
		this.other = other;
	}


	public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(CPURecord.class), "CPURecord", "time",
                new SQLVColumn[] {
                    new ReflVColumn(CPURecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(CPURecord.class, "user", ColumnType.Long, false),
                    new ReflVColumn(CPURecord.class, "system", ColumnType.Long, false),
                    new ReflVColumn(CPURecord.class, "idle", ColumnType.Long, false),
                    new ReflVColumn(CPURecord.class, "other", ColumnType.Long, false)
                });

	public Long user;
	public Long system;
	public Long idle;
	public Long other;
	public Date time;
    
    public CPURecord() { }

    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }


	@Override
	public String toString() {
		return "CPURecord [time=" + time + ", user=" + user + ", system="
				+ system + ", idle=" + idle + ", other=" + other + "]";
	}


}

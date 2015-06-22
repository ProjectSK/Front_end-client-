package com.d.localdb;


import java.util.Date;

import com.d.localdb.SQLVColumn.ColumnType;

public class BatteryRecord implements Record {
	
	public static SQLVTable TABLE = 
        new SQLVDatedTable(RecordFactory.reflection(BatteryRecord.class), "batteryRecord", "time",
                new SQLVColumn[] {
                    new ReflVColumn(BatteryRecord.class, "time", ColumnType.Datetime, true),
                    new ReflVColumn(BatteryRecord.class, "capacity", ColumnType.Long, false),
                    new ReflVColumn(BatteryRecord.class, "level", ColumnType.Long, false),
                    new ReflVColumn(BatteryRecord.class, "scale", ColumnType.Long, false),
                    new ReflVColumn(BatteryRecord.class, "voltage", ColumnType.Long, false),
                    new ReflVColumn(BatteryRecord.class, "temperature", ColumnType.Float, false),
                    new ReflVColumn(BatteryRecord.class, "healthType", ColumnType.String, false),
                    new ReflVColumn(BatteryRecord.class, "plugType", ColumnType.String, false)
                });

	public Long capacity;
	public String healthType;
	public Long level;
	public String plugType;
	public Long scale;
	public Float temperature;
	public Date time;
	public Long voltage;
	
    
    public BatteryRecord() { }

    public BatteryRecord(Date time, Long capacity, Long level, Long scale,
			Long voltage, Float temperature, String healthType, String plugType) {
		super();
		this.time = time;
		this.capacity = capacity;
		this.level = level;
		this.scale = scale;
		this.voltage = voltage;
		this.temperature = temperature;
		this.healthType = healthType;
		this.plugType = plugType;
	}
    
    @Override
    public SQLVTable getTable() {
        return TABLE;
    }

	@Override
	public String toString() {
		return "BatteryRecord [time=" + time + ", capacity=" + capacity
				+ ", level=" + level + ", scale=" + scale + ", voltage="
				+ voltage + ", temperature=" + temperature + ", healthType="
				+ healthType + ", plugType=" + plugType + "]";
	}
	
   
	
}

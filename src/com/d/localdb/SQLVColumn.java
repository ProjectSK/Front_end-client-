package com.d.localdb;

public abstract class SQLVColumn {
    public static enum ColumnType {
        Datetime, Float, Long, String, Double
    }

    public final boolean isPrimaryKey;

    public final String name;
    
    protected SQLVTable table;

    public final ColumnType type;
    public SQLVColumn(String name, ColumnType type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }
    public abstract boolean fieldIsNull(Record record);
    
    public abstract String fromRecord(Record record);
    public void setTable(SQLVTable table) {
        this.table = table;
    }
    public abstract boolean toRecord(Record record, String value);
    
    
    
    public String toSQLType() {
        switch (this.type) {
        case Datetime:
            return "DATETIME";
        case Long:
            return "INTEGER";
        case String:
            return "TEXT";
        case Float:
            return "REAL";
        case Double:
        	return "REAL";
        default:
            return "UNKNOWN";
        }
    }
}
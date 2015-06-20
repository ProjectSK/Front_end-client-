package com.d.localdb;

public abstract class SQLVColumn {
    public static enum ColumnType {
        String, Long, Datetime, Float
    }

    public SQLVColumn(String name, ColumnType type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setTable(SQLVTable table) {
        this.table = table;
    }
    
    protected SQLVTable table;

    public final String name;
    public final ColumnType type;
    public final boolean isPrimaryKey;
    
    public abstract boolean fieldIsNull(Record record);
    public abstract String fromRecord(Record record);
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
        default:
            return "UNKNOWN";
        }
    }
}
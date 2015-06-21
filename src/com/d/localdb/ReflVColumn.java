package com.d.localdb;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReflVColumn extends SQLVColumn {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Field field;
    
    
    public ReflVColumn(Class<?> recordClass, String name, ColumnType type, boolean isPrimaryKey)  {
        super(name, type, isPrimaryKey);
        try {
            field = recordClass.getField(this.name);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        Class<?> expectedFieldType = properFieldType();
        if (!field.getType().equals(expectedFieldType)) {
            throw new IllegalArgumentException(new NoSuchFieldException("Found field'" + this.name + "' in " + recordClass + ", but its type is not " + expectedFieldType));
        }
    }

    @Override
    public boolean fieldIsNull(Record record) {
        Object obj;
        try {
            obj = field.get(record);
            return obj == null;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }
    @Override
    public String fromRecord(Record record) {
        try {
            return stringify(field.get(record));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private Object parseString(String s) {
        switch (this.type) {
        case Datetime:
            try {
                return dateFormat.parse(s);
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        case Long:
            return Long.parseLong(s);
        case String:
            return s;
        case Float:
            return Float.parseFloat(s);
        default:
            return null;
        }
    }

    private Class<?> properFieldType() {
        switch (this.type) {
        case Datetime:
            return Date.class;
        case Long:
            return Long.class;
        case String:
            return String.class;
        case Float:
            return Float.class;
        }
        return null;
    }

    private String stringify(Object obj) {
        switch (this.type) {
        case Datetime:
            return dateFormat.format((Date)obj);
        case Long:
            return ((Long)obj).toString();
        case String:
            return (String)obj;
        case Float:
            return obj.toString();
        default:
            return obj.toString();
        }
    }

    @Override
    public boolean toRecord(Record record, String s) {
        try {
            field.set(record, parseString(s));
            return true;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }

}
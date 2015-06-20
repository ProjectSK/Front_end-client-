package com.d.localdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class RecordFactory {
    public abstract Record newRecord();

    public static RecordFactory reflection(final Class<? extends Record> cls) {
        final Constructor<? extends Record> ctor;
        try {
            ctor = cls.getConstructor();
        } catch (NoSuchMethodException e1) {
            throw new IllegalArgumentException(e1);
        }
        return new RecordFactory() {
            @Override
            public Record newRecord() {
                try {
                    return (Record)ctor.newInstance();
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException(e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }
}

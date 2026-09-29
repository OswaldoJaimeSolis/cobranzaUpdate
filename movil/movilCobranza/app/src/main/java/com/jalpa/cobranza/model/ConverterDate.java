package com.jalpa.cobranza.model;

import java.util.Date;

import androidx.room.TypeConverter;

public class ConverterDate {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}

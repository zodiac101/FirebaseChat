package com.example.rohil.firebasechat.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Rohil on 6/15/2017.
 */

public class TimeConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}

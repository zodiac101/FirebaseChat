package com.example.rohil.firebasechat.room.messageRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.example.rohil.firebasechat.room.TimeConverter;

import java.util.Date;

/**
 * Created by Rohil on 6/6/2017.
 */


@Entity(tableName = "messageRoom")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    private int mid;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "participant")
    private String participant;

    @ColumnInfo(name = "type")                          //true->send false->received
    private Boolean type;

    @ColumnInfo(name = "read")                          //1->arrived, not read
    private int read;                                   //2->arrived, read
                                                        //3->sent
    @ColumnInfo(name = "timestamp")
    @TypeConverters(TimeConverter.class)
    private Date timestamp;


    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MessageEntity(String message, String participant, Boolean type, int read, Date timestamp) {
        this.message = message;
        this.participant = participant;
        this.type = type;
        this.read = read;
        this.timestamp = timestamp;

    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }



}

package com.example.rohil.firebasechat.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.rohil.firebasechat.room.messageRoom.MessageDao;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserDao;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;

/**
 * Created by Rohil on 6/6/2017.
 */


@Database(entities = {MessageEntity.class, UserEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract MessageDao messageDao();

    public abstract UserDao userDao();
}

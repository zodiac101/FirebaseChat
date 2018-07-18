package com.example.rohil.firebasechat.room.userRoom;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Rohil on 6/7/2017.
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM userRoom")
    List<UserEntity> getAllUsers();

    @Insert
    void insertUser (UserEntity userEntity);

    @Query("SELECT * FROM userRoom")
    public LiveData<List<UserEntity>> getAllUsersSync();

    @Query("SELECT * FROM userRoom WHERE uid = :str1")
    UserEntity getUser(String str1);

}

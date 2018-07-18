package com.example.rohil.firebasechat.room.messageRoom;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Rohil on 6/6/2017.
 */

@Dao
public interface MessageDao {

    @Insert
    void insertMessage (MessageEntity messageEntity);

    @Query("SELECT * FROM messageRoom WHERE participant = :str1")
    List<MessageEntity> loadAllMessages(String str1);

    @Query("SELECT * FROM (SELECT * FROM messageRoom ORDER BY mid ASC) GROUP BY participant ORDER BY mid DESC")
    List<MessageEntity> loadLastMessages();

    @Query("SELECT * FROM (SELECT * FROM messageRoom ORDER BY mid ASC) GROUP BY participant ORDER BY mid DESC")
    LiveData<List<MessageEntity>> loadLastMessagesSync();


    @Query("SELECT * FROM messageRoom WHERE participant LIKE :str1")
    LiveData<List<MessageEntity>> loadAllMessagesSync(String str1);

    @Query("UPDATE messageRoom SET read = '2' WHERE participant = :str1")
    void updateMessageStatus (String str1);
}

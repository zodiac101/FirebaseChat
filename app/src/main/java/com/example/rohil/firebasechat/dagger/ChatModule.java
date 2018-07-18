package com.example.rohil.firebasechat.dagger;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.room.AppDatabase;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Rohil on 6/9/2017.
 */


@Module
public class ChatModule {

    private ChatApplication chatApplication;
    private FirebaseDatabase firebaseDatabase;
    private SharedPreferences sharedPreferences;


    public ChatModule(ChatApplication chatApplication) {
        this.chatApplication = chatApplication;
    }

    @Provides
    Context applicationContext() {
        return chatApplication;
    }

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "testdb").build();
    }


    @Provides
    @Singleton
    FirebaseDatabase getFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase;
    }

    @Provides
    @Singleton
    SharedPreferences getSharedPreferences(){
        sharedPreferences = chatApplication.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}

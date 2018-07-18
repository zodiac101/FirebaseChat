package com.example.rohil.firebasechat.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rohil.firebasechat.BackgroundService;
import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.activity.SignUpActivity;
import com.example.rohil.firebasechat.room.AppDatabase;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by Rohil on 6/7/2017.
 */

public class MainViewModel extends AndroidViewModel {

    @Inject AppDatabase db;

    @Inject SharedPreferences sharedPreferences;

    private LiveData<List<MessageEntity>> liveDataLastMessages;
    private List<UserEntity> userEntities;


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public MainViewModel(final Application application) {
        super(application);


        ((ChatApplication) application).getChatComponent().inject(this);


        liveDataLastMessages = db.messageDao().loadLastMessagesSync();



    }


    public LiveData<List<MessageEntity>> getLiveDataLastMessages() {
        return liveDataLastMessages;
    }

    public List<UserEntity> getUserEntities() {
        try {
            userEntities = new testGetTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return userEntities;
    }



    private class testGetTask extends AsyncTask<Void, Void, List<UserEntity>> {

        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            userEntities = db.userDao().getAllUsers();
            return userEntities;
        }

    }








}

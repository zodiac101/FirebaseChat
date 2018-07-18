package com.example.rohil.firebasechat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.rohil.firebasechat.activity.MainActivity;
import com.example.rohil.firebasechat.room.AppDatabase;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class BackgroundService extends Service {

    @Inject FirebaseDatabase database;
    DatabaseReference myRef;

    @Inject AppDatabase appDatabase;

    @Inject SharedPreferences sharedPreferences;

    private String userId;

    List<MessageEntity> messageEntities;


    NotificationManager mNotifyMgr;

    PendingIntent resultPendingIntent;


//    RemoteInput remoteInput;
//
//    Notification.Action action;


    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        ((ChatApplication) getApplication()).getChatComponent().inject(this);
        myRef = database.getReference("message");

        userId = sharedPreferences.getString("userId", "");
        if (userId.equals("")){
            Log.v("BackgroundService", "Service Stopped");
            stopSelf();
        }

        Log.v("BackgroundService", "uid: "+ userId);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
//        remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                .setLabel(KEY_TEXT_REPLY)
//                .build();
//
//        action = new Notification.Action.Builder(R.drawable.ic_action_stat_reply,
//                        "REPLY", resultPendingIntent)
//                        .addRemoteInput(remoteInput)
//                        .build();

        myRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                messageEntities = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Log.v("SERVICE", "got data:"+snapshot.getValue());
                    String msg = snapshot.getValue().toString();
                    String rcvid = dataSnapshot.getKey();

                    if (snapshot.getKey().equals("--meta")){
                        addUser(rcvid, msg);
                        continue;
                    }
                    messageEntities.add(new MessageEntity(msg, rcvid, false, 1, new Date()));

                    new TestThrowNotification().execute(rcvid, msg);

//                    Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
//                            .setColor(getResources().getColor(R.color.colorPrimary))
//                            .setSmallIcon(android.R.drawable.ic_menu_compass)
//                            .setContentTitle(rcvid)
//                            .setContentText(snapshot.getValue().toString())
//
//                    mNotifyMgr.notify(1, mBuilder.build());
                    //new MessageNotification().notify(getApplicationContext(), "helllo", 1);

                }

                addMessage(messageEntities);
                myRef.child(userId).child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return START_STICKY;


    }


    public void addMessage(List<MessageEntity> messageEntities){

        for (int i =0; i<messageEntities.size(); i++){
            new ServiceAddMessage().execute(messageEntities.get(i));
        }

    }


    public void addUser(final String s1, String s2){

        database.getReference("users").child(s2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserEntity newUser = new UserEntity(
                        dataSnapshot.child("uid").getValue().toString(),
                        dataSnapshot.child("name").getValue().toString(),
                        s1,
                        dataSnapshot.child("status").getValue().toString(),
                        dataSnapshot.child("profilePic").getValue().toString());

                new TestAddUser().execute(newUser);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceAddMessage extends AsyncTask<MessageEntity, Void, Void> {

        @Override
        protected Void doInBackground(MessageEntity... messages) {
            appDatabase.messageDao().insertMessage(messages[0]);
            Log.v("SERVICE", "value added");
            return null;
        }
    }

    private class TestAddUser extends AsyncTask<UserEntity, Object, Void> {


        @Override
        protected Void doInBackground(UserEntity ... userEntities) {

            UserEntity userEntity = appDatabase.userDao().getUser(userEntities[0].getUid());
            if (userEntity==null){

                appDatabase.userDao().insertUser(userEntities[0]);

                Log.v("SERVICE", "user created: "+userEntities[0].getName()+" "+userEntities[0].getPhone()+" "+userEntities[0].getStatus()+" "+userEntities[0].getProfilePic());


            }
            return null;
        }
    }

    private class TestThrowNotification extends AsyncTask<String, String, String>{

        UserEntity userEntity;

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            Glide.with(BackgroundService.this).asBitmap().load(Uri.parse(userEntity.getProfilePic())).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                    Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.ic_stat_message)
                            .setLargeIcon(resource)
                            .setContentTitle(userEntity.getName())
                            .setContentText(s)
                            .setAutoCancel(true)
                            .setContentIntent(resultPendingIntent);

                     mNotifyMgr.notify(1, mBuilder.build());

                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {

            userEntity = appDatabase.userDao().getUser(strings[0]);
            return strings[1];
        }
    }



}


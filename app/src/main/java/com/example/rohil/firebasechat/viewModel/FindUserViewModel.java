package com.example.rohil.firebasechat.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.adapter.FindUserAdapter;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

/**
 * Created by Rohil on 6/14/2017.
 */

public class FindUserViewModel extends AndroidViewModel {

    @Inject FirebaseDatabase firebaseDatabase;

    @Inject SharedPreferences sharedPreferences;

    FindUserAdapter findUserAdapter;

    String userId;

    public void setFindUserAdapter(FindUserAdapter findUserAdapter) {
        this.findUserAdapter = findUserAdapter;
    }

    public FindUserViewModel(Application application) {
        super(application);

        ((ChatApplication) application).getChatComponent().inject(this);

        userId = sharedPreferences.getString("userId", "");

        firebaseDatabase.getReference("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (!userId.equals(dataSnapshot.child("uid").getValue().toString())) {

                    UserEntity userEntity = new UserEntity(dataSnapshot.child("uid").getValue().toString(),
                            dataSnapshot.child("name").getValue().toString(),
                            dataSnapshot.getKey(),
                            dataSnapshot.child("status").getValue().toString(),
                            dataSnapshot.child("profilePic").getValue().toString());

                    Log.v("FindUserViewModel", dataSnapshot.getKey());

                    findUserAdapter.addUser(userEntity);
                }

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

    }
}

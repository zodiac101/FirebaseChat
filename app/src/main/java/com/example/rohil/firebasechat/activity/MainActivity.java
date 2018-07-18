package com.example.rohil.firebasechat.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.rohil.firebasechat.BackgroundService;
import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.adapter.UserListAdapter;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;
import com.example.rohil.firebasechat.viewModel.MainViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;

    private MainViewModel mainViewModel;

    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    CircleImageView toolbar_logo;
    TextView toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        SharedPreferences sharedPreferences = mainViewModel.getSharedPreferences();



        if (sharedPreferences.getString("userId", "null").equals("null")){
            Log.d("MainActivityView", "onAuthStateChanged:signed_out");
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        else {
            Log.v("MainViewModel", "Starting service");
            final Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //                        .setAction("Action", null).show();
                    Intent intent = new Intent(getApplicationContext(), FindUserActivity.class);
                    startActivity(intent);

                }
            });

            getSupportActionBar().setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar_logo = (CircleImageView)findViewById(R.id.toolbar_logo1);
            toolbar_title = (TextView)findViewById(R.id.toolbar_title);


            toolbar_title.setText("Hello "+sharedPreferences.getString("userName", ""));

            Glide.with(this).asBitmap().load(sharedPreferences.getString("userPicture","")).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    toolbar_logo.setImageBitmap(resource);
                }
            });

            toolbar_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(MainActivity.this, GetDetailsActivity.class);
                    startActivity(intent1);
                }
            });


            recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUserList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            userListAdapter = new UserListAdapter(getApplicationContext());
            recyclerView.setAdapter(userListAdapter);

            mainViewModel.getLiveDataLastMessages().observe(this, new Observer<List<MessageEntity>>() {
                @Override
                public void onChanged(@Nullable List<MessageEntity> messageEntities) {

                    List<UserEntity> userEntities = mainViewModel.getUserEntities();

                    for (int i = 0; i < userEntities.size(); i++) {
                        Log.v("Adapter2", "user: " + userEntities.get(i).getUid());
                    }

                    for (int i = 0; i < messageEntities.size(); i++) {
                        Log.v("adapter2", "message: " + messageEntities.get(i).getParticipant() + " " + messageEntities.get(i).getRead());
                    }

                    //                for (int i = 0; i<messageEntities.size(); i++){
                    //                    for (int j =0; i<userEntities.size(); j++){
                    //
                    //                    }
                    //                }

                    if (userEntities.size() == messageEntities.size()) {

                        userListAdapter.addAll(userEntities, messageEntities);
                    }
                }
            });
        }


    }



    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}

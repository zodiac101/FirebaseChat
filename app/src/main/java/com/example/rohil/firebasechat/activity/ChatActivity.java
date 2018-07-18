package com.example.rohil.firebasechat.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.adapter.ChatListAdapter;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;
import com.example.rohil.firebasechat.viewModel.ChatViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements LifecycleRegistryOwner {


    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    RecyclerView messageView;
    EditText editTextMessage;
    ImageButton buttonSend;

    MessageEntity messageEntityLast;

    ChatListAdapter chatListAdapter;


    CircleImageView toolbar_logo;
    TextView toolbar_title;
    TextView toolbar_subtitle;
    ImageView toolbar_back;


//    AIConfiguration config;
//    AIDataService aiDataService;
//    AIRequest aiRequest;

    private String recvId, recvName, recvPhone, recvStatus, recvProfile;

    ChatViewModel chatViewModel;

    private boolean firstRun = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//
//        config = new AIConfiguration("8773725084fa4394a52197cf691ab6f7"       ,
//                AIConfiguration.SupportedLanguages.English);
//
//        aiDataService = new AIDataService(config);
//
//        aiRequest = new AIRequest();




        final Intent intent = getIntent();
        recvId = intent.getStringExtra("recvId");
        recvName = intent.getStringExtra("recvName");
        recvPhone = intent.getStringExtra("recvPhone");
        recvStatus = intent.getStringExtra("recvStatus");
        recvProfile = intent.getStringExtra("recvProfilePic");



        getSupportActionBar().setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_logo = (CircleImageView)findViewById(R.id.toolbar_logo1);
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_subtitle = (TextView)findViewById(R.id.toolbar_subtitle);
        toolbar_back = (ImageView) findViewById(R.id.toolbar_back);

        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar_title.setText(intent.getStringExtra("recvName"));
        toolbar_subtitle.setText(intent.getStringExtra("recvStatus"));

        Glide.with(this).asBitmap().load(intent.getStringExtra("recvProfilePic")).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                toolbar_logo.setImageBitmap(resource);
            }
        });





        messageView = (RecyclerView) findViewById(R.id.messageListView);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messageView.setLayoutManager(new LinearLayoutManager(this));

        chatListAdapter = new ChatListAdapter();
        messageView.setAdapter(chatListAdapter);

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        chatViewModel.setCredentials(recvId);
        chatViewModel.setActivity(this);



        chatViewModel.getLiveData().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable List<MessageEntity> messageEntities) {

                if (messageEntities.size() == 0) {


                }
                else if (firstRun && messageEntities.size()!=0) {
                    chatListAdapter.addAllMessages(messageEntities);
                    messageEntityLast = messageEntities.get(messageEntities.size()-1);
                    messageView.smoothScrollToPosition(messageEntities.size());
                    firstRun = false;
                }
                else if (messageEntityLast!=messageEntities.get(messageEntities.size()-1)) {
                    chatListAdapter.addMessage(messageEntities.get(messageEntities.size() - 1));
                    messageView.smoothScrollToPosition(messageEntities.size());
                    //chatViewModel.updateMessageStatus(messageEntities.get(messageEntities.size()-1).getMid());
                }

         //Log.v("ChatActivity", messageEntities.get(messageEntities.size()-1).getParticipant()+" "+recvId);
            }
        });


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        messageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    if (messageView.getAdapter().getItemCount() != 0) {
                        messageView.smoothScrollToPosition(
                                messageView.getAdapter().getItemCount() - 1);
                    }
                }
            }
        });


    }

    private void sendMessage() {

        if (firstRun){
            chatViewModel.addNewUserMessage();
            chatViewModel.addUser(new UserEntity(recvId, recvName, recvPhone, recvStatus, recvProfile));
            firstRun = false;
        }

        chatViewModel.addMessage(editTextMessage.getText().toString().replace("\n", ""));
        editTextMessage.setText("");

//

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatViewModel.updateMessageStatus();
    }
}
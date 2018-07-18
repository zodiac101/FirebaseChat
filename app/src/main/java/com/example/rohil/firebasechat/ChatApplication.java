package com.example.rohil.firebasechat;

import android.app.Application;

import com.example.rohil.firebasechat.dagger.ChatComponent;
import com.example.rohil.firebasechat.dagger.ChatModule;
import com.example.rohil.firebasechat.dagger.DaggerChatComponent;

/**
 * Created by Rohil on 6/9/2017.
 */


public class ChatApplication extends Application {

    private ChatComponent chatComponent;

    public ChatComponent getChatComponent() {
        return chatComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        chatComponent = DaggerChatComponent.builder().
                chatModule(new ChatModule(this)).build();




    }
}

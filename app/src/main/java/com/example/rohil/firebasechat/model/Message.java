package com.example.rohil.firebasechat.model;

/**
 * Created by Rohil on 6/2/2017.
 */

public class Message {

    private String messageText;
    private boolean isUser;

    public Message() {
    }

    public Message(String messageText, boolean isUser) {
        this.messageText = messageText;
        this.isUser = isUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}

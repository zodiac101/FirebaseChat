package com.example.rohil.firebasechat.viewModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DialogFragment;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.room.AppDatabase;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

/**
 * Created by Rohil on 6/7/2017.
 */

public class ChatViewModel extends AndroidViewModel {

    @Inject AppDatabase db;
    private LiveData<List<MessageEntity>> liveData;

    @Inject SharedPreferences sharedPreferences;

    AIConfiguration config;
    AIDataService aiDataService;
    AIRequest aiRequest;

    Activity activity;


    @Inject FirebaseDatabase database;
    DatabaseReference myRef;

    String USERID;
    String RECVID;


    public void setCredentials(String str2){
        RECVID = str2;
        //new testUpdateMessageStatus().execute();
    }

    public void updateMessageStatus(){
        new testUpdateMessageStatus().execute();
    }


    public ChatViewModel(Application application) {
        super(application);

        ((ChatApplication) application).getChatComponent().inject(this);

        USERID = sharedPreferences.getString("userId", "");
        Log.v("ChatViewModel", "userid: "+USERID);
        myRef = database.getReference("message");

        config = new AIConfiguration("5446c8e14ea946c189556b326a80b30c"       ,
                AIConfiguration.SupportedLanguages.English);

        aiDataService = new AIDataService(config);

        aiRequest = new AIRequest();
    }


    public void addMessage(String message){

        myRef.child(RECVID).child(USERID).push().setValue(message);
        MessageEntity messageEntity = new MessageEntity(message,RECVID, true, 3, new Date());
        new testAddMessage().execute(messageEntity);

        aiRequest.setQuery(message);
        new aiTask().execute(aiRequest);

    }

    public void addUser(UserEntity userEntity){
        new testAddUser().execute(userEntity);
    }



    public LiveData<List<MessageEntity>> getLiveData() {
        liveData = db.messageDao().loadAllMessagesSync(RECVID);
        return liveData;
    }

    public void addNewUserMessage() {

        myRef.child(RECVID).child(USERID).child("--meta").setValue(sharedPreferences.getString("userPhone", ""));

    }

    private class testAddMessage extends AsyncTask<MessageEntity, Void, Void> {

        @Override
        protected Void doInBackground(MessageEntity... messages) {
            db.messageDao().insertMessage(messages[0]);
            Log.v("ChatActivity", "value added");
            return null;
        }
    }

    public class testAddUser extends AsyncTask<UserEntity, Void, Void>{

        @Override
        protected Void doInBackground(UserEntity... userEntities) {
            db.userDao().insertUser(userEntities[0]);
            Log.v("ChatActivity", "User Created");
            return null;
        }
    }

    class testUpdateMessageStatus extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db.messageDao().updateMessageStatus(RECVID);
            Log.v("ChatViewModel", "Message Status updated");
            return null;
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public class aiTask extends AsyncTask<AIRequest, Void, AIResponse>{
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response = aiDataService.request(request);
                return response;
            } catch (AIServiceException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            if (aiResponse != null) {
                // process aiResponse here
                //final String speech = aiResponse.getResult().getFulfillment().getSpeech();
                Log.i("MainActivity", "response: " + aiResponse.getResult().getAction());
                //Message message = new Message(speech, false);
                //myRef.push().setValue(message);

                if (aiResponse.getResult().getAction().equals("meet.up")){
                    final Result result = aiResponse.getResult();
                    final HashMap<String, JsonElement> params = result.getParameters();
                    String location = "";
                    String date = "";
                    if (params != null && !params.isEmpty()) {
                        //Log.i("ChatViewModel", "Parameters: ");
                        for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                            //Log.i("ChatViewModel", String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                            if (entry.getKey().equals("location") && entry.getValue()!=null)
                                location = entry.getValue().toString();
                            if (entry.getKey().equals("date-time") && entry.getValue()!=null);
                                date = entry.getValue().toString();
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                   // Log.v("ChatViewModel", "dialog should be shown");
                    builder.setTitle("Set a reminder about the meeting?");
                    builder.setMessage("Place: "+location+"\nTime: "+date);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.v("ChatViewModel", "Create reminder");
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create();
                    builder.show();
                }

                else if (aiResponse.getResult().getAction().equals("action.emergency")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(aiResponse.getResult().getFulfillment().getSpeech()));
                    Log.v("ChatViewModel", aiResponse.toString());
                    getApplication().startActivity(intent);

                }

            }

        }
    }


}

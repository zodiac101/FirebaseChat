package com.example.rohil.firebasechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rohil on 6/2/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    List<MessageEntity> messages;


    public ChatListAdapter() {

        messages = new ArrayList<>();
    }

    public void addMessage(MessageEntity message){
        messages.add(message);
        notifyItemInserted(messages.size()-1);
    }

    public void addAllMessages(List<MessageEntity> messageEntities){
        messages = messageEntities;
        notifyDataSetChanged();
    }

    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ChatListViewHolder holder, int position) {

        holder.textViewMessage.setText(messages.get(position).getMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        //Log.v("ChatViewModel", );
        holder.textViewTimestamp.setText(dateFormat.format(messages.get(position).getTimestamp()));


        if (messages.get(position).getType()){
            Log.v("ADAPTER", "left:"+messages.get(position).getMessage());

            ((LinearLayout) holder.cardViewMessage).setGravity(Gravity.START);
        }
        else {
            Log.v("ADAPTER", "right:"+messages.get(position).getMessage());

            ((LinearLayout) holder.cardViewMessage).setGravity(Gravity.END);
        }

        holder.cardViewMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        LinearLayout cardViewMessage;
        TextView textViewTimestamp;

        public ChatListViewHolder(View itemView) {
            super(itemView);

            textViewMessage = (TextView)itemView.findViewById(R.id.textViewMessage);
            cardViewMessage = (LinearLayout) itemView.findViewById(R.id.viewMessage);
            textViewTimestamp = (TextView) itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}

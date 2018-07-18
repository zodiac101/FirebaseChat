package com.example.rohil.firebasechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.activity.ChatActivity;
import com.example.rohil.firebasechat.room.messageRoom.MessageEntity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rohil on 6/7/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    List<MessageEntity> messageEntities;
    List<UserEntity> userEntities;
    Context context;



    public UserListAdapter(Context context) {

        this.context = context;
        messageEntities = new ArrayList<>();
        userEntities = new ArrayList<>();
    }

    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        context = parent.getContext();
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserListAdapter.UserListViewHolder holder, int position) {

        for(int i = 0; i<userEntities.size(); i++){

            if (messageEntities.get(position).getParticipant().equals(userEntities.get(i).getUid())){

                final UserEntity userEntity  = userEntities.get(i);

                Log.v("UserListAdapter", userEntity.getProfilePic());

                holder.textViewLastMessage.setText(messageEntities.get(position).getMessage());
                holder.textViewName.setText(userEntity.getName());

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

                //Log.v("ChatViewModel", );
                holder.textViewTimestamp.setText(dateFormat.format(messageEntities.get(position).getTimestamp()));

                if ((messageEntities.get(position).getRead() == 1)){
                    holder.imageViewReadStatus.setVisibility(View.VISIBLE);
                    Log.v("UserListAdapter", "message is read");
                }
                else {
                    holder.imageViewReadStatus.setVisibility(View.GONE);
                }


                Glide.with(context).asBitmap().load(userEntity.getProfilePic()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        holder.imageViewProfilePic.setImageBitmap(resource);
                    }
                });

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("recvId", userEntity.getUid());
                        intent.putExtra("recvName", userEntity.getName());
                        intent.putExtra("recvStatus", userEntity.getStatus());
                        intent.putExtra("recvPhone", userEntity.getPhone());
                        intent.putExtra("recvProfilePic", userEntity.getProfilePic());
                        context.startActivity(intent);
                    }
                });

                break;
            }

        }




    }

    public void addAll (List<UserEntity> userEntities, List<MessageEntity> messageEntities){
        this.userEntities = userEntities;
        this.messageEntities = messageEntities;
        notifyDataSetChanged();
    }

    public void addUser(UserEntity userEntity, MessageEntity messageEntity){
        userEntities.add(userEntity);
        messageEntities.add(messageEntity);
        notifyItemInserted(userEntities.size()-1);
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageViewProfilePic;
        TextView textViewName;
        TextView textViewLastMessage;
        TextView textViewTimestamp;
        ImageView imageViewReadStatus;
        View view;


        public UserListViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageViewProfilePic  = (CircleImageView) itemView.findViewById(R.id.imageViewProfilePic);
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewLastMessage = (TextView)itemView.findViewById(R.id.textViewLastMessage);
            textViewTimestamp = (TextView)itemView.findViewById(R.id.textViewTimestamp);
            imageViewReadStatus = (ImageView)itemView.findViewById(R.id.imageViewReadStatus);
        }
    }
}

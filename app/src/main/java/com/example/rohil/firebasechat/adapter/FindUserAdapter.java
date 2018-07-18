package com.example.rohil.firebasechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.activity.ChatActivity;
import com.example.rohil.firebasechat.room.userRoom.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohil on 6/14/2017.
 */

public class FindUserAdapter extends RecyclerView.Adapter<FindUserAdapter.ViewHolder> {

    Context context;

    List<UserEntity> userEntities;

    ProgressBar progressBar;

    public FindUserAdapter(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
        userEntities = new ArrayList<>();
    }

    public void addUser(UserEntity userEntity){
        userEntities.add(userEntity);
        notifyItemInserted(userEntities.size()-1);
    }


    @Override
    public FindUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindUserAdapter.ViewHolder holder, final int position) {

        Glide.with(context).load(Uri.parse(userEntities.get(position).getProfilePic())).into(holder.imageViewProfilePic);
        holder.textViewName.setText(userEntities.get(position).getName());
        holder.textViewStatus.setText(userEntities.get(position).getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserEntity userEntity = userEntities.get(position);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("recvId", userEntity.getUid());
                intent.putExtra("recvName", userEntity.getName());
                intent.putExtra("recvStatus", userEntity.getStatus());
                intent.putExtra("recvProfilePic", userEntity.getProfilePic());
                context.startActivity(intent);
            }
        });

        progressBar.setVisibility( View.GONE);
    }

    @Override
    public int getItemCount() {
        return userEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageViewProfilePic;
        TextView textViewName;
        TextView textViewStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imageViewProfilePic = (ImageView)itemView.findViewById(R.id.imageViewProfilePic);
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewStatus = (TextView)itemView.findViewById(R.id.textViewStatus);
        }
    }
}

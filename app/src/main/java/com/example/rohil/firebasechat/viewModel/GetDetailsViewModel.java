package com.example.rohil.firebasechat.viewModel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.activity.GetDetailsActivity;
import com.example.rohil.firebasechat.activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;

/**
 * Created by Rohil on 6/14/2017.
 */

public class GetDetailsViewModel extends AndroidViewModel {


    @Inject SharedPreferences sharedPreferences;
    @Inject FirebaseDatabase firebaseDatabase;

    private StorageReference mStorageRef;

    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    ProgressBar progressBar;


    public GetDetailsViewModel(Application application) {
        super(application);

        ((ChatApplication) application).getChatComponent().inject(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void completeRegistration(final String name, final String status, final Uri profilePic){

        progressBar.setVisibility(View.VISIBLE);

        if (profilePic==null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", name);
            editor.putString("userStatus", status);
            editor.putString("userPicture", "");
            editor.commit();


            firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("name").setValue(name);
            firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("status").setValue(status);
            firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("profilePic").setValue(" ");

            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.getApplicationContext().startActivity(intent);
            activity.finish();
            return;
        }

        File file;

        try {
            String path = getRealPathFromURI(activity, profilePic);

            file = new File(path);

            File file1 = new Compressor(activity).compressToFile(file);
            if (file1.exists()){
                Log.v("GetDetailsViewModel", "file exists: "+file1.getAbsolutePath()+" "+file1.getTotalSpace());
            }

            mStorageRef.child("profileImages").child(sharedPreferences.getString("userId", "")+".jpg").putFile(Uri.fromFile(file1)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", name);
                    editor.putString("userStatus", status);
                    editor.putString("userPicture", taskSnapshot.getDownloadUrl().toString());
                    editor.commit();


                    firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("name").setValue(name);
                    firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("status").setValue(status);
                    firebaseDatabase.getReference("users").child(sharedPreferences.getString("userPhone", "")).child("profilePic").setValue(taskSnapshot.getDownloadUrl().toString()    );

                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getApplication().getApplicationContext().startActivity(intent);
                    activity.finish();
                }
            });



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

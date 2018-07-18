package com.example.rohil.firebasechat.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.viewModel.GetDetailsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetDetailsActivity extends AppCompatActivity {

    //ImageView imageView;
    CircleImageView imageView;

    EditText editTextName;
    EditText editTextStatus;

    ProgressBar progressBar;

    Uri uriProfilePic;


    GetDetailsViewModel getDetailsViewModel;


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDetailsViewModel = ViewModelProviders.of(this).get(GetDetailsViewModel.class);

        uriProfilePic = null;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (editTextName.getText().toString().equals("")){
                editTextName.setError("Required");
            }
            else if (editTextStatus.getText().toString().equals("")){
                editTextStatus.setError("Required");
            }
            else {

                getDetailsViewModel.completeRegistration(editTextName.getText().toString(),
                        editTextStatus.getText().toString(),
                        uriProfilePic);

            }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (CircleImageView) findViewById(R.id.imageView);
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextStatus = (EditText)findViewById(R.id.editTextStatus);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        editTextName.setText(getDetailsViewModel.getSharedPreferences().getString("userName", ""));
        editTextStatus.setText(getDetailsViewModel.getSharedPreferences().getString("userStatus", ""));

        Glide.with(this).asBitmap().load(getDetailsViewModel.getSharedPreferences().getString("userPicture", "")).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
            }
        });




        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GetDetailsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(GetDetailsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 100);
                }


            }
        };

        imageView.setOnClickListener(onClickListener);
        findViewById(R.id.fab1).setOnClickListener(onClickListener);

        getDetailsViewModel.setActivity(this);
        getDetailsViewModel.setProgressBar(progressBar);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 100:
                if(resultCode == RESULT_OK){

                    uriProfilePic = imageReturnedIntent.getData();

                    Glide.with(this).asBitmap().load(uriProfilePic).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }
                    });

                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 100);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




}

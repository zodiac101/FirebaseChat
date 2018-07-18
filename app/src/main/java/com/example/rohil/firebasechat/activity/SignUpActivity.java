package com.example.rohil.firebasechat.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.viewModel.SignUpViewModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SignUpActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private String TAG = "SignUpActivity";

    EditText editTextPhone;
    EditText editTextOTP;
    ProgressBar progressBar;
    FloatingActionButton fab;

    private String verificationCode;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private SignUpViewModel signUpViewModel;


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");

        editTextPhone = (EditText)findViewById(R.id.editTextPhoneNumber);
        editTextOTP = (EditText)findViewById(R.id.editTextOTP);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signUpViewModel.signInWithPhoneAuthCredential(phoneAuthCredential, SignUpActivity.this);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Snackbar.make(fab, "OTP SENT", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d(TAG, "onCodeSent:" + s);

                editTextOTP.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                verificationCode = s;


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

            }
        };


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editTextOTP.getVisibility()== View.GONE){
                    signUpViewModel.startPhoneNumberVerification(editTextPhone.getText().toString(), SignUpActivity.this, mCallbacks);
                    progressBar.setVisibility(View.VISIBLE);
                    editTextPhone.setFreezesText(true);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    signUpViewModel.verifyPhoneNumberWithCode(verificationCode, editTextOTP.getText().toString(), SignUpActivity.this);
                    progressBar.setVisibility(View.GONE);
                }



            }
        });
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}

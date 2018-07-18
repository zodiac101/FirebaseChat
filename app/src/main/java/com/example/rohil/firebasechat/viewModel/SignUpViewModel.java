package com.example.rohil.firebasechat.viewModel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.rohil.firebasechat.ChatApplication;
import com.example.rohil.firebasechat.activity.GetDetailsActivity;
import com.example.rohil.firebasechat.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by Rohil on 6/12/2017.
 */

public class SignUpViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    @Inject SharedPreferences sharedPreferences;
    @Inject FirebaseDatabase firebaseDatabase;

    public SignUpViewModel(Application application) {
        super(application);

        ((ChatApplication) application).getChatComponent().inject(this);

        mAuth = FirebaseAuth.getInstance();


    }

    public void startPhoneNumberVerification(String phoneNumber, Activity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,              // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }


    public void verifyPhoneNumberWithCode(String verificationId, String code, Activity activity) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential, activity);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final Activity activity) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();

                            Log.d("SignUpViewModel", "signInWithCredential:success");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", user.getUid());
                            editor.putString("userPhone", user.getPhoneNumber());
                            editor.commit();

                            firebaseDatabase.getReference("users").child(user.getPhoneNumber()).child("uid").setValue(user.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(activity.getApplicationContext(), GetDetailsActivity.class);
                                    getApplication().startActivity(intent);
                                    activity.finish();
                                }
                            });
                            // [START_EXCLUDE]
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("SignUpViewModel", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(activity, "INVALID OTP", Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            } // [END_EXCLUDE]
                        }
                    }
                });
    }

}

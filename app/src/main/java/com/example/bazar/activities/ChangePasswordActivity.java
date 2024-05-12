package com.example.bazar.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.bazar.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.example.bazar.databinding.ActivityChangePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {


    private ActivityChangePasswordBinding binding;
    private static final String TAG = "CHANGE_PASS_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validateData();
            }
        });
    }
    private String currentPassword = "";
    private String newPassword = "";
    private String confirmPassword = "";
    private  void validateData(){
        Log.d(TAG, "validateData: ");

        currentPassword = binding.currentPasswordEt.getText().toString();
        newPassword = binding.newPasswordEt.getText().toString();
        confirmPassword = binding.confirmNewPasswordEt.getText().toString();

        Log.d(TAG, "validateData: Passwords: ");

        if (currentPassword.isEmpty()){
            binding.currentPasswordEt.setError("Enter current password!");
            binding.currentPasswordEt.requestFocus();

        } else if (newPassword.isEmpty()) {
            binding.newPasswordEt.setError("Enter new password!");
            binding.newPasswordEt.requestFocus();

        } else if (confirmPassword.isEmpty()) {
            binding.confirmNewPasswordEt.setError("Enter confirm password!");
            binding.confirmNewPasswordEt.requestFocus();

        } else if (!newPassword.equals(confirmPassword)) {
            binding.confirmNewPasswordEt.setError("Password doesn`t match!");
            binding.confirmNewPasswordEt.requestFocus();

        } else {
            authenticateUserForUpdate();
        }
    }

    private void authenticateUserForUpdate(){

        Log.d(TAG, "authenticateUserForUpdate: ");

        progressDialog.setMessage("Authenticating User");
        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updatePassword();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e);
                        progressDialog.dismiss();
                        Utils.toast(ChangePasswordActivity.this, "Failed  to authenticate due to "+ e.getMessage());
                    }
                });
    }

    private void updatePassword() {
        Log.d(TAG, "updatePassword: ");

        progressDialog.setMessage("Updating Password");
        progressDialog.show();

        firebaseUser.updatePassword(newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Utils.toast(ChangePasswordActivity.this, "Password updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        progressDialog.dismiss();
                        Utils.toast(ChangePasswordActivity.this, "Failed  to update password due to "+ e.getMessage());
                    }
                });
    }
}
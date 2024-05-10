package com.example.bazar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.bazar.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private static final String TAG = "ACCOUNT_FRAGMENT_TAG";
    private FirebaseAuth firebaseAuth;
    private Context mContext;
    public void onAttach(@NonNull Context context)
    {
        mContext = context;
        super.onAttach(context);
    }
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(LayoutInflater.from(mContext), container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        binding.logoutCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(mContext, MainActivity.class));
                getActivity().finishAffinity();
            }
        });


        binding.editProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ProfileEditAcrivity.class));
            }
        });
        binding.changePasswordCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChangePasswordActivity.class));
            }
        });
    }


    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dob = ""+ snapshot.child("dob").getValue();
                        String email = ""+ snapshot.child("email").getValue();
                        String name = ""+ snapshot.child("name").getValue();
                        String phoneCode = ""+ snapshot.child("phoneCode").getValue();
                        String phoneNumber = ""+ snapshot.child("phoneNumber").getValue();
                        String profileImageUrl = ""+ snapshot.child("profileImageUrl").getValue();
                        String timestamp= ""+ snapshot.child("timestamp").getValue();
                        String userType = ""+ snapshot.child("userType").getValue();

                        String phone = phoneCode + phoneNumber;

                        if(timestamp.equals("null")){
                            timestamp = "0";
                        }
                        long timestampLong = Long.parseLong(timestamp);
                        Date date = new Date(timestampLong);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.doTv.setText(dob);
                        binding.phoneTv.setText(phone);
                        binding.memberSinceTv.setText(sdf.format(date));


                        if(userType.equals("Email")){
                            boolean isverified = firebaseAuth.getCurrentUser().isEmailVerified();

                            if(isverified) {

                                binding.verificationTv.setText("Verified");
                            } else {

                                binding.verificationTv.setText("Not Verified");
                            }

                        } else {
                            binding.verificationTv.setText("Verified");
                        }

                        try {

                            Glide.with(mContext)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.profileIv);

                        }catch (Exception e)
                        {
                            Log.e(TAG, "onDataChange: ",e );
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
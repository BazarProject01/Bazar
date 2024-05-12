package com.example.bazar.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bazar.fragments.AccountFragment;
import com.example.bazar.fragments.HomeFragment;
import com.example.bazar.fragments.MyAdsFragment;
import com.example.bazar.R;
import com.example.bazar.Utils;
import com.example.bazar.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            startLoginOptions();
        }
        showHomeFragment();
        binding.bottomNw.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.menu_home) {
                    showHomeFragment();
                    return true;
                }  else if (itemId == R.id.menu_my_ads) {
                    if(firebaseAuth.getCurrentUser() == null)
                    {
                        Utils.toast(MainActivity.this, "Login Required...");
                        startLoginOptions();
                        return false;
                    } else {
                        showMyAdsFragment();
                        return true;
                    }
                }
                else if (itemId == R.id.menu_account)
                {
                    if(firebaseAuth.getCurrentUser() == null)
                    {
                        Utils.toast(MainActivity.this, "Login Required...");
                        startLoginOptions();
                        return false;
                    } else {
                        showAccountFragment();
                        return true;
                    }

                } else if (itemId == R.id.menu_sell) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this, "Login Required...");
                        startLoginOptions();
                        return false;
                    } else {
                        showAdCreateFragment();
                        return true;
                    }
                }else {
                    return false;
                }

            }
        });

    }

    private void showHomeFragment() {
        binding.toolbarTitle.setText("Home");
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();
    }
    private void showMyAdsFragment() {
        binding.toolbarTitle.setText("My Ads");
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), fragment, "MyAdsFragment");
        fragmentTransaction.commit();
    }
    private void showAccountFragment() {
        binding.toolbarTitle.setText("Account");
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), fragment, "AccountFragment");
        fragmentTransaction.commit();
    }
    private void showAdCreateFragment() {
        startActivity(new Intent(MainActivity.this, AdCreateActivity.class));
    }

    private void startLoginOptions()
    {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }}
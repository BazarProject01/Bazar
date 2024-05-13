package com.example.bazar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bazar.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Utils {

    public static final String AD_STATUS_AVAILABLE ="AVAILABLE";
    public static final String AD_STATUS_SOLD ="SOLD";

    public static final String[] categories = {
            "Mobiles",
            "Computer/Laptop",
            "Electronics & Home Appliances",
            "Vehicles",
            "Furniture & Home Decor",
            "Fashion & Beauty",
            "Books",
            "Animals",
            "Businesses",
            "Agriculture"
    };

    public static final int[] categoryIcons = {
            R.drawable.ic_category_mobiles,
            R.drawable.ic_category_computer,
            R.drawable.ic_category_electronics,
            R.drawable.ic_category_vehicles,
            R.drawable.ic_category_furniture,
            R.drawable.ic_category_fasion,
            R.drawable.ic_category_books,
            R.drawable.ic_category_sports,
            R.drawable.ic_category_buisness,
            R.drawable.ic_category_agriculture
    };

    public static final String[] conditions = {"New", "Used", "Refurbished"};


    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }
    public static void goToMenu(Context context ) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void addToFavorite(Context context, String adId)
    {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            Utils.toast(context, "You`re not logged in!");

        } else {

            long timestamp = Utils.getTimestamp();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("adId", adId);
            hashMap.put("timestamp", timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Utils.toast(context, "Added to favorite!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.toast(context, "Failed to add to favorite due to: "+e.getMessage());
                        }
                    });


        }
    }

    public static void removeFromFavorite(Context context, String adId)
    {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){

            Utils.toast(context, "You`re not logged in!");

        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Utils.toast(context, "Removed from favorite!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.toast(context, "Failed to remove from favorite due to: "+e.getMessage());
                        }
                    });


        }
    }
    public static void callIntent(Context context, String phone){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + Uri.encode(phone)));
        context.startActivity(intent);
    }


}
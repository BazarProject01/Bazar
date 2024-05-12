package com.example.bazar.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bazar.R;
import com.example.bazar.Utils;
import com.example.bazar.adapters.AdapterImageSlider;
import com.example.bazar.databinding.ActivityAdCreateBinding;
import com.example.bazar.databinding.ActivityAdDetailsBinding;
import com.example.bazar.models.ModelAd;
import com.example.bazar.models.ModelImageSlider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AdDetailsActivity extends AppCompatActivity {

    private ActivityAdDetailsBinding binding;

    private static final String TAG = "AD_DETAILS_TAG";

    private FirebaseAuth firebaseAuth;
    private  String adId = "";
    private String sellerUid = null;
    private String  sellerPhone = "";

    private boolean favorite = false;

    private ArrayList<ModelImageSlider> imageSliderArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.toolbarEditBtn.setVisibility(View.GONE);
        binding.deleteBtn.setVisibility(View.GONE);
        binding.callBtn.setVisibility(View.GONE);

        adId = getIntent().getStringExtra("adId");
        Log.d(TAG, "onCreate: adId: "+adId);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            checkIsfavorite();
        }
        loadAdDetails();
        loadAdImages();

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);
                materialAlertDialogBuilder.setTitle("Delete Ad")
                        .setMessage("Are you sure want to delete this Ad?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAd();
                            }
                        })
                        .setNegativeButton("CANCELED", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();


            }
        });
        binding.toolbarEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.toolbarFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite) {
                    Utils.removeFromFavorite(AdDetailsActivity.this, adId);
                } else {
                    Utils.addToFavorite(AdDetailsActivity.this, adId);
                }
            }
        });

        binding.sellerProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callIntent(AdDetailsActivity.this, sellerPhone);
            }
        });
    }

    private void showMarkAsSoldDialog(){


        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Mark as Sold")
                .setMessage("Are you sure you want mark this Ad as sold?")
                .setPositiveButton("SOLD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Sold Clicekd...");

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", ""+ Utils.AD_STATUS_SOLD);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                        ref.child(adId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Marked as sold");
                                        Toast.makeText(AdDetailsActivity.this, "Marked as sold", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: ",e );
                                        Toast.makeText(AdDetailsActivity.this, "Failed to mark as sold due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Cancled");
                        dialog.dismiss();

                    }
                })
                .show();
    }
    private void loadAdDetails(){
        Log.d(TAG, "loadAdDetails: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{

                            ModelAd modelAd = snapshot.getValue(ModelAd.class);

                            sellerUid = modelAd.getUid();

                            String title = modelAd.getTitle();
                            String description = modelAd.getDescription();
                            String adress = modelAd.getAddress();
                            String сategory = modelAd.getCategory();
                            String condition = modelAd.getCondition();
                            String price = modelAd.getPrice();
                            long timestamp = modelAd.getTimestamp();

                            Date date = new Date(timestamp);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = sdf.format(date);


                            if(sellerUid.equals(firebaseAuth.getUid())){
                                binding.toolbarEditBtn.setVisibility(View.VISIBLE);
                                binding.deleteBtn.setVisibility(View.VISIBLE);

                                binding.callBtn.setVisibility(View.GONE);
                            } else {
                                binding.toolbarEditBtn.setVisibility(View.GONE);
                                binding.deleteBtn.setVisibility(View.GONE);

                                binding.callBtn.setVisibility(View.VISIBLE);
                            }

                            binding.titleTv.setText(title);
                            binding.descriptionTv.setText(description);
                            binding.adressTv.setText(adress);
                            binding.conditionTv.setText(condition);
                            binding.priceTv.setText(price);
                            binding.categoryTv.setText(сategory);
                            binding.dateTv.setText(formattedDate);


                            loadSellerDetails();

                        } catch (Exception e){
                            Log.e(TAG, "onDataChange: ",e );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void loadSellerDetails(){
        Log.d(TAG, "loadSellerDetails: ");

        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        String phoneCode = ""+snapshot.child("phoneCode").getValue();
                        String phoneNumber = ""+snapshot.child("phoneNuber").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImageUrl = ""+snapshot.child("profileImageUrl").getValue();
                        long timestamp = (Long) snapshot.child("timestamp").getValue();

                        Date date = new Date(timestamp);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = sdf.format(date);

                        sellerPhone = phoneCode +""+phoneNumber;

                        binding.sellerNameTv.setText(name);
                        binding.memberSinceTv.setText(formattedDate);

                        try {
                            Glide.with(AdDetailsActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.sellerProfileIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ",e );
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void  checkIsfavorite(){
        Log.d(TAG, "checkIsfavorite: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favorite = snapshot.exists();
                        Log.d(TAG, "onDataChange: favorite" + favorite);

                        if (favorite){
                         binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_no);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAdImages(){
        Log.d(TAG, "loadAdImages: ");
        imageSliderArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        imageSliderArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);

                            imageSliderArrayList.add(modelImageSlider);
                        }
                        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(AdDetailsActivity.this, imageSliderArrayList);
                        binding.imageSliderVp.setAdapter(adapterImageSlider);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteAd(){
        Log.d(TAG, "deleteAd: ");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: ");
                        Utils.toast(AdDetailsActivity.this, "Deleted");
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        Utils.toast(AdDetailsActivity.this, "Failed to delete due to "+e.getMessage());

                    }
                });
    }
}
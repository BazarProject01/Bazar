package com.example.bazar;

import android.content.Context;
import android.graphics.ColorSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bazar.databinding.RowAdBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterAd extends RecyclerView.Adapter<AdapterAd.Holder> implements Filterable {
    private RowAdBinding binding;
    private static final String TAG = "ADAPTER_AD_TAG";
    private FirebaseAuth firebaseAuth;
    private Context context;
    public ArrayList<ModelAd> adArrayList;
    private ArrayList<ModelAd> filterList;
    private FilterAd filter;

    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList) {
        this.context = context;
        this.adArrayList = adArrayList;
        this.filterList = adArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAdBinding binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        ModelAd modelAd = adArrayList.get(position);

        String title = modelAd.getTitle();
        String description = modelAd.getDescription();
        String address = modelAd.getAddress();
        String condition = modelAd.getCondition();
        String price = modelAd.getPrice();

        long timestamp = modelAd.getTimestamp();
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(date);

        loadAdFirstImage(modelAd, holder);

        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite(modelAd, holder);
        }

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.addressTv.setText(address);
        holder.conditionTv.setText(condition);
        holder.priceTv.setText(price);
        holder.dateTv.setText(formattedDate);

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean favorite = modelAd.isFavorite();

                    if(favorite){
                        Utils.removeFromFavorite(context, modelAd.getId());

                    } else {
                        Utils.addToFavorite(context, modelAd.getId());
                    }


                }
            });
        }



    private void checkIsFavorite(ModelAd modelAd, Holder holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(modelAd.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean favorite = snapshot.exists();

                        modelAd.setFavorite(favorite);

                        // Обновляем изображение кнопки в соответствии с состоянием favorite
                        if (favorite) {
                            holder.favBtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            holder.favBtn.setImageResource(R.drawable.ic_fav_no);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибок, если необходимо
                    }
                });
    }

    private void loadAdFirstImage(ModelAd modelAd, Holder holder) {
        Log.d(TAG, "loadAdFirstImage: ");

        String adId = modelAd.getId();
        Log.d(TAG, "Ad ID: " + adId); // Выводим значение adId для отладки

        if (adId != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
            reference.child(adId).child("Images").limitToFirst(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d(TAG, "onDataChange: imageId " + adId);
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String imageUrl = "" + ds.child("imageUrl").getValue();
                                Log.d(TAG, "onDataChange: ");
                                Log.d(TAG, "Image URL: " + imageUrl);

                                try {
                                    Glide.with(context)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_image_gray)
                                            .into(holder.imageIv);
                                    Log.d(TAG, "onDataChange: Loaded photo");

                                } catch (Exception e) {
                                    Log.e(TAG, "onDataChange: ", e);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        } else {
            Log.e(TAG, "Ad ID is null!");
        }
    }


    @Override
    public int getItemCount() {
        return adArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterAd(this, filterList);
        }
        return filter;
    }

    class Holder extends RecyclerView.ViewHolder{


        ShapeableImageView imageIv;
        TextView titleTv, descriptionTv, addressTv, conditionTv, priceTv, dateTv;
        ImageButton favBtn;
        public Holder(@NonNull RowAdBinding binding) {

            super(binding.getRoot());

            titleTv = binding.titleTv;
            favBtn = binding.favBtn;
            imageIv = binding.imageIv;
            descriptionTv = binding.descriptionTv;
            addressTv = binding.addressTv;
            conditionTv = binding.conditionTv;
            priceTv = binding.priceTv;
            dateTv = binding.dateTv;
        }
    }
}

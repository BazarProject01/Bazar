package com.example.bazar.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bazar.R;
import com.example.bazar.Utils;
import com.example.bazar.databinding.RowImagesPickedBinding;
import com.example.bazar.models.ModelImagePicked;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterImagePicked extends RecyclerView.Adapter<AdapterImagePicked.HolderImagesPicked> {

    private RowImagesPickedBinding binding;
    private static final String TAG ="IMAGES_TAG";

    private Context context;
    private ArrayList<ModelImagePicked> imagePickedArrayLst;


    private String adId;
    public AdapterImagePicked(Context context, ArrayList<ModelImagePicked> imagePickedArrayLst, String adId) {
        this.context = context;
        this.imagePickedArrayLst = imagePickedArrayLst;
        this.adId = adId;
    }

    @NonNull
    @Override
    public HolderImagesPicked onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderImagesPicked(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImagesPicked holder, int position) {

        ModelImagePicked  model = imagePickedArrayLst.get(position);


        if(model.getFromIntent()){
            String imageUrl = model.getImageUrl();


            Log.d(TAG, "onBindViewHolder: imageUri" +imageUrl);

            try {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageIv);

            } catch (Exception e){
                Log.e(TAG, "onBindViewHolder: ",e );
            }
        } else {

            Uri imageUri = model.getImageUri();

            Log.d(TAG, "onBindViewHolder: imageUri: "+imageUri);

            try {
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageIv);
            } catch (Exception e){
                Log.e(TAG, "onBindViewHolder: ",e );
            }
        }



        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.getFromIntent()){

                    deleteImageFirebase(model, holder, position);

                } else {
                    imagePickedArrayLst.remove(model);
                    notifyItemRemoved(position);
                }

            }
        });

    }

    private void deleteImageFirebase(ModelImagePicked model, HolderImagesPicked holder, int position) {
        String imageId = model.getId();
        Log.d(TAG, "deleteImageFirebase: adId: " +adId);
        Log.d(TAG, "deleteImageFirebase: imageId: " +imageId);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images").child(imageId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.d(TAG, "onSuccess: Delete");
                       Utils.toast(context, "Image Deleted!");


                       try {

                           imagePickedArrayLst.remove(model);

                           notifyItemRemoved(position);


                       } catch (Exception e){
                           Log.e(TAG, "onSuccess: ",e );
                       }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );

                        Utils.toast(context, "Failed to delete image due to " + e.getMessage());
                    }
                });


    }

    @Override
    public int getItemCount() {
        return imagePickedArrayLst.size();
    }

    class HolderImagesPicked extends RecyclerView.ViewHolder{

        ImageView imageIv;
        ImageButton closeBtn;
        public HolderImagesPicked(@NonNull View itemView) {
            super(itemView);

            imageIv = binding.imageIv;
            closeBtn = binding.closeBtn;
        }
    }
}
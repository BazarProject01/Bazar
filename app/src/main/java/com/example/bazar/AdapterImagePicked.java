package com.example.bazar;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bazar.databinding.RowImagesPickedBinding;

import java.util.ArrayList;

public class AdapterImagePicked extends RecyclerView.Adapter<AdapterImagePicked.HolderImagesPicked> {

    private RowImagesPickedBinding binding;
    private static final String TAG ="IMAGES_TAG";

    private Context context;
    private ArrayList<ModelImagePicked> imagePickedArrayLst;

    public AdapterImagePicked(Context context, ArrayList<ModelImagePicked> imagePickedArrayLst) {
        this.context = context;
        this.imagePickedArrayLst = imagePickedArrayLst;
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

        Uri imageUri = model.getImageUri();

        Log.d(TAG, "onBindViewHolder: imageUri" +imageUri);

        try {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_image_gray)
                    .into(holder.imageIv);

        } catch (Exception e){
            Log.e(TAG, "onBindViewHolder: ",e );
        }

        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             imagePickedArrayLst.remove(model);
             notifyItemRemoved(position);
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

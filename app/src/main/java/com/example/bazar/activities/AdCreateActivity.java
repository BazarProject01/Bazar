package com.example.bazar.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.bazar.R;
import com.example.bazar.Utils;
import com.example.bazar.adapters.AdapterImagePicked;
import com.example.bazar.databinding.ActivityAdCreateBinding;
import com.example.bazar.models.ModelImagePicked;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdCreateActivity extends AppCompatActivity {

    private ActivityAdCreateBinding binding;
    private static final String TAG = "AD_CREATE_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Uri imageUri = null;
    private AdapterImagePicked adapterImagePicked;
    private ArrayList<ModelImagePicked> imagePickedArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        ArrayAdapter<String> adaterCategories = new ArrayAdapter<>(this, R.layout.row_category_act, Utils.categories);
        binding.categoryAct.setAdapter(adaterCategories);

        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_conditions_act, Utils.conditions);
        binding.conditionAct.setAdapter(adapterConditions);

        imagePickedArrayList = new ArrayList<>();

        loadImages();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);



        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.toolbarAdImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOptions();
            }
        });

        binding.postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }
    private void loadImages(){
        Log.d(TAG, "loadImages: ");

        adapterImagePicked = new AdapterImagePicked(this, imagePickedArrayList);
        binding.imagesRv.setAdapter(adapterImagePicked);

    }

    private void showImagePickOptions() {
        Log.d(TAG, "showImagePickOptions: ");

        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarAdImageBtn);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == 1)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    {

                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermissions.launch(cameraPermissions);
                    } else {
                        String[] cameraPermissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE} ;
                        requestCameraPermissions.launch(cameraPermissions);
                    }
                } else if (itemId == 2) {

                    Log.d(TAG, "onMenuItemClick: Check storage permission");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    {
                        pickImageGallery();
                    } else {
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE ;
                        requestStoragePermission.launch(storagePermission);
                    }

                }

                return true;
            }
        });

    }
    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted "+ isGranted);

                    if(isGranted){

                        pickImageGallery();

                    }else {
                        Utils.toast(AdCreateActivity.this, "Storage permission denied...");
                    }

                }
            }
    );

    private  ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: " + result);

                    boolean areAllGranted = true;
                    for(Boolean isGranted : result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }


                    if(areAllGranted){

                        pickImageCamera();

                    } else {
                      Utils.toast(AdCreateActivity.this, "Camera or gallery or both permissions denied...");
                    }

                }
            }
    );
    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);


    }
    private void pickImageCamera(){
        Log.d(TAG, "pickImageCamera: ");

        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE-DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageuri" +imageUri);

                        String timestamp = ""+Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);

                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Canceled...!");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");

                    if(result.getResultCode() == Activity.RESULT_OK){

                        Log.d(TAG, "onActivityResult: imageuri" +imageUri);

                        String timestamp = ""+Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);

                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Canceled...!");
                    }
                }
            }
    );
    private String category = "";
    private String condition = "";
    private String address = "";
    private String price= "";
    private String title = "";
    private String description = "";



    private void validateData(){
        Log.d(TAG, "validateData: ");

        category = binding.categoryAct.getText().toString().trim();
        condition = binding.conditionAct.getText().toString().trim();
        address = binding.locatioEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();




        if (category.isEmpty()) {
            binding.categoryAct.setError("Enter Category");
            binding.categoryAct.requestFocus();
        }

        else if (condition.isEmpty()) {
            binding.conditionAct.setError("Enter Condition");
            binding.conditionAct.requestFocus();
        }

        else if (address.isEmpty()) {
            binding.locatioEt.setError("Enter Address");
            binding.locatioEt.requestFocus();
        }

        else if (price.isEmpty()) {
            binding.priceEt.setError("Enter Price");
            binding.priceEt.requestFocus();
        }

        else if (title.isEmpty()) {
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        }

        else if (description.isEmpty()) {
            binding.descriptionEt.setError("Enter Description");
            binding.descriptionEt.requestFocus();

        } else if(imagePickedArrayList.isEmpty()) {
            Utils.toast(AdCreateActivity.this, "Pick at-least one image!");

        } else {
            postAd();
        }
    }

    private void postAd(){
        Log.d(TAG, "postAd: ");

        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+keyId);
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("category", ""+category);
        hashMap.put("condition", ""+condition);
        hashMap.put("address", ""+address);
        hashMap.put("price", ""+price);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("status", ""+Utils.AD_STATUS_AVAILABLE);
        hashMap.put("timestamp", timestamp);


        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Ad Published");

                        uploadImageStorage(keyId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e);
                        progressDialog.dismiss();
                        Utils.toast(AdCreateActivity.this, "Failed to publish Ad due to " + e.getMessage());
                    }
                });

    }

    private void uploadImageStorage(String keyId) {
        Log.d(TAG, "uploadImageStorage: ");

        AtomicInteger uploadedImages = new AtomicInteger(0);
        int totalImages = imagePickedArrayList.size();

        for (int i = 0; i < totalImages; i++) {
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);
            String imageName = modelImagePicked.getId();
            String filePathandName = "Ads/" + imageName;
            int imageIndexForProgress = i + 1;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);

            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        String message = "Uploading " + imageIndexForProgress + " of " + totalImages + " images...\nProgress " + (int) progress + "%";
                        Log.d(TAG, "onProgress: message" + message);
                        progressDialog.setMessage(message);
                        progressDialog.show();
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        uploadedImages.incrementAndGet();

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;

                        Uri uploadedImageUrl = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", "" + modelImagePicked.getId());
                            hashMap.put("imageUrl", "" + uploadedImageUrl);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                            ref.child(keyId).child("Images")
                                    .child(imageName)
                                    .updateChildren(hashMap);
                        }

                        if (uploadedImages.get() == totalImages) {
                            progressDialog.dismiss();
                            Utils.goToMenu(AdCreateActivity.this);
                            Toast.makeText(AdCreateActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                    });
        }
    }




}
package com.example.bazar;

import android.net.Uri;

public class ModelImagePicked {
    String id ="";
    Uri imageUri = null;
    String imageUrl = null;
    Boolean fromIntent = false;

    public ModelImagePicked() {

    }

    public ModelImagePicked(String id, Uri imageUri, String imageUrl, Boolean fromIntent) {
        this.id = id;
        this.imageUri = imageUri;
        this.imageUrl = imageUrl;
        this.fromIntent = fromIntent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFromIntent() {
        return fromIntent;
    }

    public void setFromIntent(Boolean fromIntent) {
        this.fromIntent = fromIntent;
    }
}

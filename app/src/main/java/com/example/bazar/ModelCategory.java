package com.example.bazar;

public class ModelCategory {


    int icon;
    String category;
    public ModelCategory(String category, int icon) {
        this.category = category;
        this.icon = icon;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }



}

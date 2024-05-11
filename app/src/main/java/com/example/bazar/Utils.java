package com.example.bazar;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.CalendarView;
import android.widget.Toast;

import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    public static String formatTimestampDate(long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd//MM//yyyy", calendar).toString();

        return date;
    }


    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }
    public static void goToMenu(Context context ) {
        context.startActivity(new Intent(context, MainActivity.class));
    }



}

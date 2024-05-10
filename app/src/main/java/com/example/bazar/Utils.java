package com.example.bazar;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.CalendarView;
import android.widget.Toast;

import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }
    public static void goToMenu(Context context ) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd//MM//yyyy", calendar).toString();

        return date;
    }
}

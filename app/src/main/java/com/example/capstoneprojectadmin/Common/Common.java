package com.example.capstoneprojectadmin.Common;

import android.text.format.DateFormat;

import com.example.capstoneprojectadmin.Model.Admin;
import com.example.capstoneprojectadmin.Model.Order;
import com.example.capstoneprojectadmin.Model.Restaurant;

import java.util.Calendar;
import java.util.TimeZone;

public class Common {
    public static Admin currentAdmin;
    public static Restaurant currentRestaurant;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static Order currentOrder;

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        calendar.setTimeInMillis(time);
        int dayCount = calendar.get(Calendar.DAY_OF_WEEK);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        String day = "";

        switch(dayCount) {
            case 1:
                day = "Sun";
                break;
            case 2:
                day = "Mon";
                break;
            case 3:
                day = "Tue";
                break;
            case 4:
                day = "Wed";
                break;
            case 5:
                day = "Thu";
                break;
            case 6:
                day = "Fri";
                break;
            case 7:
                day = "Sat";
                break;
        }
        return day + " " +date.toString();
    }
}

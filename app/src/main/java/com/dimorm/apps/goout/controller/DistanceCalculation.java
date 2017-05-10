package com.dimorm.apps.goout.controller;

import com.dimorm.apps.goout.view.MainActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Dima on 4/22/2017.
 */

public class DistanceCalculation {




    public static String distance(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        if(MainActivity.DISTANCE_IN_KM){
            return  df.format(d) + " Km";
        }
        else{
            d= d/1.61;
            return df.format(d) + " Miles";
        }
    }

}

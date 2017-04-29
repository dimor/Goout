package com.dimorm.apps.goout;

import java.util.ArrayList;

/**
 * Created by Dima on 4/21/2017.
 */

public class ResultsCurrentPlacesJsonModel {

    String name;
    String vicinity;
    ArrayList<PhotoResultsJsonModel> photos;

    geometry geometry;
    String reference;

    String formatted_address;

    class geometry {
        location location;
        class  location{
             double lat;
             double lng;
        }
    }
}

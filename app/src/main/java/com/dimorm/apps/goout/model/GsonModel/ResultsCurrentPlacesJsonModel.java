package com.dimorm.apps.goout.model.GsonModel;

import java.util.ArrayList;

/**
 * Created by Dima on 4/21/2017.
 */

public class ResultsCurrentPlacesJsonModel {

    public String name;
    public String vicinity;
    public ArrayList<PhotoResultsJsonModel> photos;

    public String icon;
    public geometry geometry;

    public String formatted_address;

    public class geometry {
        public location location;

        public class location {
            public double lat;
            public double lng;
        }
    }
}

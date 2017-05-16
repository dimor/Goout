package com.dimorm.apps.goout.model.GsonModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ResultsCurrentPlacesJsonModel implements Parcelable {

    public String name;
    public String vicinity;
    public ArrayList<PhotoResultsJsonModel> photos;

    public String icon;
    public geometry geometry;

    public String formatted_address;

    private ResultsCurrentPlacesJsonModel(Parcel in) {
        name = in.readString();
        vicinity = in.readString();
        icon = in.readString();
        formatted_address = in.readString();
    }

    public static final Creator<ResultsCurrentPlacesJsonModel> CREATOR = new Creator<ResultsCurrentPlacesJsonModel>() {
        @Override
        public ResultsCurrentPlacesJsonModel createFromParcel(Parcel in) {
            return new ResultsCurrentPlacesJsonModel(in);
        }

        @Override
        public ResultsCurrentPlacesJsonModel[] newArray(int size) {
            return new ResultsCurrentPlacesJsonModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(vicinity);
        dest.writeString(icon);
        dest.writeString(formatted_address);
    }

    public class geometry {
        public location location;

        public class location {
            public double lat;
            public double lng;
        }
    }
}

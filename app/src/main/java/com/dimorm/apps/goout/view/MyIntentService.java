package com.dimorm.apps.goout.view;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.controller.ImageEncodeDecode;
import com.dimorm.apps.goout.controller.OkHttpNetCall;
import com.dimorm.apps.goout.model.DatabaseSQL;
import com.dimorm.apps.goout.model.GsonModel.GsonModel;
import com.dimorm.apps.goout.model.GsonModel.ResultsCurrentPlacesJsonModel;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String searchText = intent.getStringExtra("query_string");
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        boolean currentLocationToggle = intent.getBooleanExtra("currentLocation", false);
        boolean autoSearch = intent.getBooleanExtra("auto", false);
        Gson gson = new Gson();
        GsonModel gsonModel;
        String url;
        String JsonString = null;
        Bitmap bitmap = null;
        ArrayList gsonArray;
        String photoString;
        ArrayList stringImages = new ArrayList();

        if (autoSearch){
            url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=2000&key=AIzaSyCWZQEyD2UDp46H2qQHbfVec38kMK-dJ-k";
        }
        else{
            if (currentLocationToggle)
                url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=2000&keyword="+searchText+"&key=AIzaSyCWZQEyD2UDp46H2qQHbfVec38kMK-dJ-k";
            else
               url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+searchText+"&radius=2000&key=AIzaSyCWZQEyD2UDp46H2qQHbfVec38kMK-dJ-k";
        }


        OkHttpNetCall okHttpNetCall = new OkHttpNetCall();
        try {
            JsonString = okHttpNetCall.run(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gsonModel = gson.fromJson(JsonString, GsonModel.class);
        gsonArray  =  gsonModel.results;
        String pictureUrl = null;
        String photoReference;

        DatabaseSQL.getDatabaseInstance(this).getWritableDatabase().delete("history",null,null);

        for (int i=0 ; i<gsonModel.results.size(); i++ ){
            try{
                if (gsonModel.results.get(i).photos == null) {
                    pictureUrl = gsonModel.results.get(i).icon;
                }
                else {
                    photoReference = gsonModel.results.get(i).photos.get(0).photo_reference;
                    pictureUrl ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" + photoReference + "&key=AIzaSyCWZQEyD2UDp46H2qQHbfVec38kMK-dJ-k";
                }

            }catch (NullPointerException e){
                Log.d("NULLNULLNULL","" +i + gsonModel.results.get(i).name);
            }
     

            try {
                bitmap =   Picasso.with(this).load(pictureUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
           photoString = ImageEncodeDecode.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,100);
            stringImages.add(photoString);

            ContentValues values = new ContentValues();
            values.put("name", gsonModel.results.get(i).name);
            if (gsonModel.results.get(i).vicinity != null)
                values.put("address", gsonModel.results.get(i).vicinity);
            else {
                values.put("address", gsonModel.results.get(i).formatted_address);
            }
            values.put("lat", gsonModel.results.get(i).geometry.location.lat);
            values.put("lng", gsonModel.results.get(i).geometry.location.lng);
            values.put("imageString", photoString);

            DatabaseSQL.getDatabaseInstance(this).getWritableDatabase().insert("history", null, values);
        }

        Intent intentDownloadJson = new Intent("com.dimorm.apps.goout.broadcastSearch");
        intentDownloadJson.putExtra("lat",lat);
        intentDownloadJson.putExtra("lng",lng);
        intentDownloadJson.putParcelableArrayListExtra("result",gsonArray);
        intentDownloadJson.putParcelableArrayListExtra("stringImages",stringImages);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentDownloadJson);
    }
}

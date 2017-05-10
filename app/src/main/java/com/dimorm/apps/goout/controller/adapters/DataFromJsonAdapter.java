package com.dimorm.apps.goout.controller.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimorm.apps.goout.controller.DistanceCalculation;
import com.dimorm.apps.goout.controller.ImageEncodeDecode;
import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.controller.ChangeFragment;
import com.dimorm.apps.goout.model.DatabaseSQL;
import com.dimorm.apps.goout.model.GsonModel.ResultsCurrentPlacesJsonModel;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Dima on 4/21/2017.
 */

public class DataFromJsonAdapter extends RecyclerView.Adapter<DataFromJsonAdapter.ViewHolder> {


    ///////////////////////Variables/////////////////////////////////////
    private ArrayList<ResultsCurrentPlacesJsonModel> results;
    private ArrayList stringImages;
    private double lat, lng;
    private String name;
    private String address;
    private Context context;
    private String dis;

    ////////////////////////////////////////Constractor////////////////////////////////
    public DataFromJsonAdapter(ArrayList results, ArrayList stringImages, double lat, double lng, Context context) {
        this.results = results;
        this.lat = lat;
        this.lng = lng;
        this.stringImages = stringImages;
        this.context = context;

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  ////////////////////////////////////////////VIEW HOLDER CREATE///////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, results, context, lat, lng ,stringImages);
        return viewHolder;
    }
/////////////////////////////////////////////////BIND VIEW HOLDER/////////////////////////////////
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        name = results.get(position).name;
        if (results.get(position).vicinity != null) {
            address = results.get(position).vicinity;
        } else {
            address = results.get(position).formatted_address;
        }

        dis = String.valueOf(DistanceCalculation.distance(lat, lng, results.get(position).geometry.location.lat, results.get(position).geometry.location.lng));



        holder.bindText(name, address, stringImages, dis);
    }

//////////////////////////////////////////////////////////////GET COUNT ///////////////////////////////////////
    @Override
    public int getItemCount() {
        return results.size();
    }

/////////////////////////////////////////////////////VIEW HOLDER INNER CLASS////////////////////////////////////
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView nameTV = (TextView) itemView.findViewById(R.id.TextViewPlaceName);
        TextView addressTV = (TextView) itemView.findViewById(R.id.TextViewAdress);
        ImageView logo = (ImageView) itemView.findViewById(R.id.ImageViewLogo);
        TextView distance = (TextView) itemView.findViewById(R.id.TextViewDistance);
        Context context;
        ArrayList<ResultsCurrentPlacesJsonModel> results;
        ArrayList stringImage;

            ////////////////////////////////////////////////////CONSTRUCTOR/////////////////////////////////////////////////
        public ViewHolder(final View itemView, final ArrayList<ResultsCurrentPlacesJsonModel> results, final Context context, final double lat, final double lng, ArrayList stringImage) {
            super(itemView);
            this.context = context;
            this.results = results;
            this.stringImage = stringImage;

            itemView.setOnCreateContextMenuListener(mOnCreateContextMenuListener);

      ////////////////////////////////////////////CHANGE TO MAP FRAGMNET//////////////////////////////////
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng latLng = new LatLng(results.get(getAdapterPosition()).geometry.location.lat, results.get(getAdapterPosition()).geometry.location.lng);
                    ChangeFragment changeFragment = (ChangeFragment) itemView.getContext();
                    changeFragment.ChangeFragment(latLng);

                }
            });

        }

        //////////////////////////BIND DATA TO VIEWS/////////////////////////////////
                private void bindText(String name, String address, ArrayList pictureUrl, String dis) {
                    nameTV.setText(name);
                    addressTV.setText(address);
                    distance.setText(dis);
                    logo.setImageBitmap(ImageEncodeDecode.decodeBase64(stringImage.get(getAdapterPosition()).toString()));
}

        //////////////////////////////////////////////////////CONTEXT MENU//////////////////////////////////////////////////////////////
                private final View.OnCreateContextMenuListener mOnCreateContextMenuListener = new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            menu.setHeaderTitle("Select The Action");
                            MenuItem shareAction = menu.add(0,1,1,"Share");
                            MenuItem saveToFavAction = menu.add(0,2,2,"Add to Favorites");
                            shareAction.setOnMenuItemClickListener(mOnMyActionClickListener);
                            saveToFavAction.setOnMenuItemClickListener(mOnMyActionClickListener);

                        }
                    };

            private final MenuItem.OnMenuItemClickListener mOnMyActionClickListener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if(item.getItemId()==1)
                    {

                        Double latitude = results.get(getAdapterPosition()).geometry.location.lat;
                        Double longitude = results.get(getAdapterPosition()).geometry.location.lng;


                        String location="https://www.google.co.il/maps/@"+latitude+","+longitude+",18.79z?hl=en";
                        Intent sharingIntent=new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "place Details");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,location );
                        itemView.getContext().startActivity(sharingIntent);
                    }

                    if(item.getItemId()==2){
                        Log.d("CONTEXT", "onMenuItemClick: FAV " + getAdapterPosition());
                        ContentValues values = new ContentValues();
                        values.put("name",results.get(getAdapterPosition()).name);
                        if (results.get(getAdapterPosition()).vicinity != null )
                            values.put( "address",results.get(getAdapterPosition()).vicinity);
                        else{
                            values.put( "address",results.get(getAdapterPosition()).formatted_address);
                        }

                        values.put("lat",results.get(getAdapterPosition()).geometry.location.lat);
                        values.put("lng",results.get(getAdapterPosition()).geometry.location.lng);



                        BitmapDrawable drawable = (BitmapDrawable) logo.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();

                        String imageString = ImageEncodeDecode.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG,100);

                        values.put("imageString",imageString);
                        DatabaseSQL.getDatabaseInstance(context).getWritableDatabase().insert("favorites",null ,values);
                        Toast.makeText(context,results.get(getAdapterPosition()).name + " successfully added to favorites" , Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            };







        }

    }

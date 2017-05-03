package com.dimorm.apps.goout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Dima on 4/21/2017.
 */

public class CurrentLocationAdapter extends RecyclerView.Adapter<CurrentLocationAdapter.ViewHolder> {

    ArrayList<ResultsCurrentPlacesJsonModel> results;
    private double lat, lng;
    String searchData;
    String name;
    String address;
    Context context;
    boolean currentLocation;
    DatabaseSQL databaseSQL;
    String pictureUrl;
    Bitmap logoBitmap;
    public CurrentLocationAdapter(ArrayList<ResultsCurrentPlacesJsonModel> results, double lat, double lng, Boolean currentLocation, Context context) {
        this.results = results;
        this.lat = lat;
        this.lng = lng;
        this.currentLocation = currentLocation;
        this.context = context;
         pictureUrl = null;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        databaseSQL = new DatabaseSQL(context);
        ViewHolder viewHolder = new ViewHolder(v, results, context, lat, lng ,databaseSQL);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        name = results.get(position).name;
        if (results.get(position).vicinity != null) {
            address = results.get(position).vicinity;
        } else {
            address = results.get(position).formatted_address;
        }

        String dis = String.valueOf(DistanceCalculation.distance(lat, lng, results.get(position).geometry.location.lat, results.get(position).geometry.location.lng));



        holder.bindText(name, address, getPictureURL(position), dis);

    }


        public String getPictureURL(int position){
            if (results.get(position).photos == null) {
                pictureUrl = results.get(position).reference;
            } else
                pictureUrl = results.get(position).photos.get(0).photo_reference;

            return pictureUrl;
        }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView nameTV = (TextView) itemView.findViewById(R.id.TextViewPlaceName);
        TextView addressTV = (TextView) itemView.findViewById(R.id.TextViewAdress);
        ImageView logo = (ImageView) itemView.findViewById(R.id.ImageViewLogo);
        TextView distance = (TextView) itemView.findViewById(R.id.TextViewDistance);
        Context context;
        DatabaseSQL databaseSQL;
        Bitmap logoBitmap;
        ArrayList<ResultsCurrentPlacesJsonModel> results;
        String pictureUrl;


        public ViewHolder(final View itemView, final ArrayList<ResultsCurrentPlacesJsonModel> results, final Context context, final double lat, final double lng, DatabaseSQL databaseSQL) {
            super(itemView);
            this.context = context;
            this.results = results;
            this.databaseSQL = databaseSQL;

            itemView.setOnCreateContextMenuListener(mOnCreateContextMenuListener);



            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng latLng = new LatLng(results.get(getAdapterPosition()).geometry.location.lat, results.get(getAdapterPosition()).geometry.location.lng);
                    ChangeFragment changeFragment = (ChangeFragment) itemView.getContext();
                    changeFragment.ChangeFragment(latLng);

                }
            });

        }


        public void bindText(String name, String address, String pictureUrl, String dis) {
            nameTV.setText(name);
            addressTV.setText(address);
            distance.setText(dis);
            Picasso.with(itemView.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + pictureUrl + "&key=AIzaSyBSJ-6SEDmZs99TvgQcZ8mR_Eft6ao8hrY")
                    .into(logo);
        }


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

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Just find out " + results.get(getAdapterPosition()).name + " you should check it");
                        sendIntent.setType("text/plain");
                        itemView.getContext().startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.Hello)));
                        Log.d("CONTEXT", "onMenuItemClick: SHARE " + getAdapterPosition());
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
                        databaseSQL.getWritableDatabase().insert("favorites",null ,values);

                    }
                    return true;
                }
            };
        }

    }

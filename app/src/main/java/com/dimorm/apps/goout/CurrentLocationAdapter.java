package com.dimorm.apps.goout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dima on 4/21/2017.
 */

public class CurrentLocationAdapter extends RecyclerView.Adapter<CurrentLocationAdapter.ViewHolder> {

    ArrayList<ResultsCurrentPlacesJsonModel> results;
    private double lat,lng;
    String searchData;
    String name;
    String address;
    Context context;
    boolean currentLocation;
    public CurrentLocationAdapter(ArrayList<ResultsCurrentPlacesJsonModel> results, double lat, double lng, Boolean currentLocation, Context context) {
        this.results = results;
        this.lat = lat;
        this.lng = lng;
        this.currentLocation = currentLocation;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v,results,context,lat,lng);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        name = results.get(position).name;
        if (currentLocation){
            address = results.get(position).vicinity;
        }
        else{
            address = results.get(position).formatted_address;
        }
        String pictureUrl = null;
        String dis = String.valueOf(DistanceCalculation.distance(lat,lng ,results.get(position).geometry.location.lat,results.get(position).geometry.location.lng));



        if (results.get(position).photos == null){
            pictureUrl =results.get(position).reference;
        }
        else
            pictureUrl = results.get(position).photos.get(0).photo_reference;

        holder.bindText(name, address, pictureUrl,dis);

    }

    @Override
    public int getItemCount() {
        return results.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV = (TextView) itemView.findViewById(R.id.TextViewPlaceName);
        TextView addressTV = (TextView) itemView.findViewById(R.id.TextViewAdress);
        ImageView logo = (ImageView) itemView.findViewById(R.id.ImageViewLogo);
        TextView distance = (TextView)itemView.findViewById(R.id.TextViewDistance);



        public ViewHolder(final View itemView, final ArrayList<ResultsCurrentPlacesJsonModel> results, final Context context, final double lat, final double lng) {
            super(itemView);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng latLng = new LatLng(results.get(getAdapterPosition()).geometry.location.lat,results.get(getAdapterPosition()).geometry.location.lng);
                 ChangeFragment changeFragment = (ChangeFragment) itemView.getContext();
                    changeFragment.ChangeFragment(latLng);

                }
            });

        }


        public void bindText(String name, String address, String pictureUrl,String dis) {
            nameTV.setText(name);
            addressTV.setText(address);
            distance.setText(dis);
            Picasso.with(itemView.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+pictureUrl+"&key=AIzaSyBSJ-6SEDmZs99TvgQcZ8mR_Eft6ao8hrY")
                    .into(logo);
        }}}
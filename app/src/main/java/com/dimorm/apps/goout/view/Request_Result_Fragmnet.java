package com.dimorm.apps.goout.view;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dimorm.apps.goout.controller.adapters.DataFromJsonAdapter;
import com.dimorm.apps.goout.model.DatabaseSQL;
import com.dimorm.apps.goout.model.GsonModel.GsonModel;
import com.dimorm.apps.goout.controller.OkHttpNetCall;
import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.model.GsonModel.ResultsCurrentPlacesJsonModel;
import com.google.android.gms.common.api.Result;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */



public class Request_Result_Fragmnet extends Fragment {

    double lng, lat;

    DataFromJsonAdapter adapter;
    RecyclerView recyclerView;
    String SearchData;
    Context context;
    boolean autoSearch;
    boolean currentLocation;
    ProgressBar progressBar;
    DatabaseSQL databaseSQL;
    private Toolbar toolbar;
    ArrayList results;
    ArrayList stringImages;

    public Request_Result_Fragmnet() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultFragmentView = inflater.inflate(R.layout.fragment_request_result, container, false);


        progressBar = (ProgressBar) resultFragmentView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) resultFragmentView.findViewById(R.id.RecycleViewList);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        recyclerView.setVisibility(RecyclerView.INVISIBLE);
        context = getActivity().getBaseContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        DownloadResultReceiver finishedDownload = new DownloadResultReceiver();
        IntentFilter intentFilter = new IntentFilter("com.dimorm.apps.goout.broadcastSearch");
        LocalBroadcastManager.getInstance(context).registerReceiver(finishedDownload,intentFilter);






       ///////////////////////////////////////ON ROTATION////////////////////////////////////////////



        if(savedInstanceState == null){

        }


        return resultFragmentView;
    }


    //////////////////////////////RECEIVER FOR Result CONNECTION//////////////////////////
    public class DownloadResultReceiver extends BroadcastReceiver
    {
        public DownloadResultReceiver(){
        }
        @Override
        public void onReceive(Context context, Intent intent) {

            lat =  intent.getDoubleExtra("lat",0);
            lng =  intent.getDoubleExtra("lng",0);
            results = intent.getParcelableArrayListExtra("result");
            stringImages = intent.getParcelableArrayListExtra("stringImages");

            try{
                adapter = new DataFromJsonAdapter(results,stringImages, lat, lng, context);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                recyclerView.setVisibility(RecyclerView.VISIBLE);
            }catch (Exception x){
                x.printStackTrace();
                Toast.makeText(context, "Something went wrong , please try again later", Toast.LENGTH_SHORT).show();

            }

        }
    }
}




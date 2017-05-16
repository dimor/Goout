package com.dimorm.apps.goout.view;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dimorm.apps.goout.controller.adapters.DataFromCursorAdapter;
import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.model.DatabaseSQL;

import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 */
public class Request_Result_Fragment extends Fragment {

    double lng, lat;

    DataFromCursorAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    ProgressBar progressBar;
    ArrayList results;
    ArrayList stringImages;
    Cursor cursor;
    public Request_Result_Fragment() {
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



               ///////////////////////////////////////ON ROTATION////////////////////////////////////////////
        if(savedInstanceState != null){
            try {
                lat = savedInstanceState.getDouble("lat", 0);
                lng = savedInstanceState.getDouble("lng", 0);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        DownloadResultReceiver finishedDownload = new DownloadResultReceiver();
        IntentFilter intentFilter = new IntentFilter("com.dimorm.apps.goout.broadcastSearch");
        LocalBroadcastManager.getInstance(context).registerReceiver(finishedDownload,intentFilter);

        return resultFragmentView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try{
            outState.putDouble("lat",lat);
            outState.putDouble("lng", lng);
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e("GOOUT","error on result saveInstance");
        }

    }

    @Override
    public void onResume() {
        if (MainActivity.IS_FIRST_TIME) {

            MainActivity.IS_FIRST_TIME = false;
        } else {
        cursor=DatabaseSQL.getCursor(context);
        adapter = new DataFromCursorAdapter(cursor, context, lat, lng);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        recyclerView.setVisibility(RecyclerView.VISIBLE);
    }
        super.onResume();
    }

    //////////////////////////////RECEIVER FOR Result CONNECTION//////////////////////////
    public class DownloadResultReceiver extends BroadcastReceiver
    {
        public DownloadResultReceiver(){
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            cursor = DatabaseSQL.getCursor(context);
            lat =  intent.getDoubleExtra("lat",0);
            lng =  intent.getDoubleExtra("lng",0);
            try{
                adapter = new DataFromCursorAdapter(cursor,context, lat, lng);
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




package com.dimorm.apps.goout;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.IOException;



/**
 * A simple {@link Fragment} subclass.
 */



public class Request_Result_Fragmnet extends Fragment {

    double lng, lat;

    CurrentLocationAdapter adapter;
    RecyclerView recyclerView;
    String SearchData;
    Context context;
    boolean autoSearch;
    boolean currentLocation;
    ProgressBar progressBar;
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























        Bundle data = getArguments();
        SearchData = data.getString("query_string");
        lat = data.getDouble("lat");
        lng = data.getDouble("lng");
        autoSearch = data.getBoolean("auto");
        currentLocation = data.getBoolean("currentLocation");
        if (autoSearch){
            new netCall().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=2000&key=AIzaSyBSJ-6SEDmZs99TvgQcZ8mR_Eft6ao8hrY");
            autoSearch=false;
        }
        else{
            if (currentLocation)
                new netCall().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=2000&keyword="+SearchData+"&key=AIzaSyBSJ-6SEDmZs99TvgQcZ8mR_Eft6ao8hrY");
            else
                new netCall().execute("https://maps.googleapis.com/maps/api/place/textsearch/json?query="+SearchData+"&radius=2000&key=AIzaSyBSJ-6SEDmZs99TvgQcZ8mR_Eft6ao8hrY");
        }

        return resultFragmentView;
    }


    private class netCall extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            String response = null;

            CurrentLocatinNetCall currentLocatinNetCall = new CurrentLocatinNetCall();
            try {
                response = currentLocatinNetCall.run(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String JsonString) {


            progressBar.setVisibility(ProgressBar.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);

           // Log.d("@@@@@@@@@@@ received ", JsonString);
            Gson gson = new Gson();
            GsonModel gsonMainObject = gson.fromJson(JsonString, GsonModel.class);


            adapter = new CurrentLocationAdapter(gsonMainObject.results, lat, lng, currentLocation , context);


            recyclerView.setAdapter(adapter);
        }



    }



}




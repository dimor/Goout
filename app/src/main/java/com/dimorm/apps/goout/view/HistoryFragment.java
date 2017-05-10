package com.dimorm.apps.goout.view;


import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimorm.apps.goout.controller.adapters.DataFromCursorAdapter;
import com.dimorm.apps.goout.model.DatabaseSQL;
import com.dimorm.apps.goout.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    double lat ,lng;
  public Cursor cursor;
    Context context;
    DatabaseSQL databaseSQL;
    RecyclerView HistoryRV;
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        buildAlertMessageNoInternet();
        Bundle data = getArguments();
        lat =  data.getDouble("lat");
        lng = data.getDouble("lng");
        HistoryRV = (RecyclerView) view.findViewById(R.id.HistoryRV);
        HistoryRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        LoadCursorHistory thread = new LoadCursorHistory();
        thread.execute(DatabaseSQL.getDatabaseInstance(getActivity()));
        // Inflate the layout for this fragment
        return view;
    }

    //////////////////////////////////////ALERT DIALOG NO INTERNET CONNECTION///////////////////////////////////////////////////////////
    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("No Internet Connection");
        builder.setMessage("Internet Connection Not Found\nIn Offline Mode You Can See Your Last Search Result")
                .setCancelable(true);
        final AlertDialog alert = builder.create();
        alert.show();
    }










    class LoadCursorHistory extends AsyncTask<DatabaseSQL,String,Cursor> {

        @Override
        protected Cursor doInBackground(DatabaseSQL... params) {

             cursor = params[0].getReadableDatabase().query("history",null,null,null,null,null,null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            DataFromCursorAdapter dataFromCursorAdapter = new DataFromCursorAdapter(cursor,context,lat,lng);
            HistoryRV.setAdapter(dataFromCursorAdapter);
        }

    }

}

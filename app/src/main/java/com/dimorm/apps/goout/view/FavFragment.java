package com.dimorm.apps.goout.view;


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
public class FavFragment extends Fragment {

    RecyclerView recyclerView;
    Cursor cursor;
    double lat,lng;
    DataFromCursorAdapter adapter;
    DatabaseSQL databaseSQL;
    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fav_fragment, container, false);
        Bundle data =getArguments();
        lat =data.getDouble("lat");
        lng =  data.getDouble("lng");
        recyclerView =(RecyclerView) view.findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseSQL = DatabaseSQL.getDatabaseInstance(getActivity());

        AsyncTaskCursorLoader asyncTaskCursorLoader = new AsyncTaskCursorLoader();
        asyncTaskCursorLoader.execute(databaseSQL);



        return view;
    }

        class AsyncTaskCursorLoader extends AsyncTask<DatabaseSQL, String, String> {
            @Override
            protected String doInBackground(DatabaseSQL... params) {
                cursor =params[0].getReadableDatabase().query("favorites",null,null,null,null,null,null);
                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                boolean comesFromFav = true;
                adapter = new DataFromCursorAdapter(cursor,getActivity(),lat,lng);
                recyclerView.setAdapter(adapter);
                super.onPostExecute(s);
            }
        }

}



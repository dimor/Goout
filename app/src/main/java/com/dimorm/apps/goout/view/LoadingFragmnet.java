package com.dimorm.apps.goout.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimorm.apps.goout.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragmnet extends Fragment {


    public LoadingFragmnet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_fragmnet, container, false);
    }

}

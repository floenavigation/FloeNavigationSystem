package com.awi.floenavigationsystem;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awi.floenavigationsystem.views.GridLineView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GridContainerFragment extends Fragment {


    public GridContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_grid_container, container, false);
        return view;

    }

}

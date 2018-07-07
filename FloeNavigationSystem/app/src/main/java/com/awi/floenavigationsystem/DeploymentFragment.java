package com.awi.floenavigationsystem;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeploymentFragment extends Fragment {

    private String[] coordinates;
    Button btnAccept, btnCancel;
    TextView lat, lon;
    private Callbacks sCallbacks;
    public DeploymentFragment() {
        // Required empty public constructor
    }

    //Declare Interface
    public interface Callbacks{
        public void onSelected(String[] data);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Activity activity = getActivity();
        coordinates = this.getArguments().getStringArray("Coordinates");
        View view =  inflater.inflate(R.layout.fragment_deployment, container, false);
        lat = (TextView) view.findViewById(R.id.lat);
        lon = (TextView) view.findViewById(R.id.lon);
        btnAccept = (Button)view.findViewById(R.id.btnAccept);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        //Toast.makeText(activity,"inside deployment", Toast.LENGTH_LONG).show();

        lat.setText(coordinates[0]);
        lon.setText(coordinates[1]);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCallbacks.onSelected(coordinates);
                getFragmentManager().popBackStack();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sCallbacks = (Callbacks) context;
    }




}

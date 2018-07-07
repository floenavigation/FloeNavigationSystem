package com.awi.floenavigationsystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.awi.floenavigationsystem.views.GridLineView;

import java.util.ArrayList;

public class GridActivity extends AppCompatActivity implements DeploymentFragment.Callbacks{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView nvDrawer;
    private BroadcastReceiver broadcastReceiver;
    private String[] coordinates;
    private String coordString;
    private double[] macceptcoord;
    private float[] results;
    private double dist;
    private ArrayList<Double> listDouble;
    private double[] acceptedCoord;
    private Fragment gridFragment;
    private GridLineView gridLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        Bundle bundle = new Bundle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.gridDrawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nvDrawer = (NavigationView) findViewById(R.id.gridactionItemsView);
        setupDrawerContent(nvDrawer);

        //To be used for creating x and y axis
        receiveIntentData();
        gridLineView = (GridLineView)findViewById(R.id.grid_line_view);
        gridLineView.setInitialCoordinates(acceptedCoord);

        //gridFragment = (Fragment)getFragmentManager().findFragmentById(R.id.gridcontFrame);
        //gridFragment.getArguments().

        dist = distance(acceptedCoord[0], acceptedCoord[1], acceptedCoord[2], acceptedCoord[3]);
        Toast.makeText(this, String.format("%s",dist), Toast.LENGTH_LONG).show();

    }

    private void receiveIntentData() {

        listDouble = (ArrayList<Double>)getIntent().getSerializableExtra("acceptedCoordinates");
        acceptedCoord = new double[listDouble.size()];
        for(int index = 0; index < acceptedCoord.length; index++){
            acceptedCoord[index] = listDouble.get(index);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selItemDrawer(MenuItem item){
        Fragment mfrag;
        Bundle bundle = new Bundle();

        item.setChecked(true);
        nvDrawer.setCheckedItem(item.getItemId());

        switch (item.getItemId()){

            case R.id.DeploymentT:
                Toast.makeText(getApplicationContext(),"deploy", Toast.LENGTH_LONG).show();
                mfrag = new DeploymentFragment();
                bundle.putStringArray("Coordinates", coordinates);
                mfrag.setArguments(bundle);
                break;
            /*
            case R.id.Recovery:
                //fragmentClass = SettingsFragment.class;
                break;
            case R.id.Redeployment:
                //fragmentClass = .class;
                break;
             */

            case R.id.DashboardT:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return;

            default:
                mfrag = null;
                return;
        }

        fragSelection(mfrag);
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selItemDrawer(item);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    coordString = (intent.getExtras().get("coordinates")).toString();
                    coordinates = coordString.split(",");

                    //Toast.makeText(getApplicationContext(),"location_updates", Toast.LENGTH_LONG).show();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_updates"));
    }

    public void fragSelection(Fragment mfrag){

        FragmentManager mfragmgr = getFragmentManager();
        FragmentTransaction mfragTrans = mfragmgr.beginTransaction();
        mfragTrans.replace(R.id.gridfragLay, mfrag);
        mfragTrans.addToBackStack(null);
        mfragTrans.commit();
    }

    @Override
    public void onSelected(String[] data) {
        Toast.makeText(getApplicationContext(), String.format("%s,%s", data[0], data[1]), Toast.LENGTH_LONG).show();
    }

    public void extrapolatePosition(double lat, double lon, double speed, double bearing){

        final double r = 6371 * 1000; // Earth Radius in m
        double distance = speed * 10;

        double lat2 = Math.asin(Math.sin(Math.toRadians(lat)) * Math.cos(distance / r)
                + Math.cos(Math.toRadians(lat)) * Math.sin(distance / r) * Math.cos(Math.toRadians(bearing)));
        double lon2 = Math.toRadians(lon)
                + Math.atan2(Math.sin(Math.toRadians(bearing)) * Math.sin(distance / r) * Math.cos(Math.toRadians(lat)), Math.cos(distance / r)
                - Math.sin(Math.toRadians(lat)) * Math.sin(lat2));
        lat2 = Math.toDegrees( lat2);
        lon2 = Math.toDegrees(lon2);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}

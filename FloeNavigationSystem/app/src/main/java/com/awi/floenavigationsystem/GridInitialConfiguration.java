package com.awi.floenavigationsystem;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class GridInitialConfiguration extends AppCompatActivity implements DeploymentFragment.Callbacks{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private static final int REQUEST_CODE = 100;
    private BroadcastReceiver broadcastReceiver;
    private TextView firstcoord, secondcoord, firstcoordTitle, secondcoordTitle;
    private String coordString;
    private String[] coordinates;
    private double[] macceptcoord;
    int index = 0;
    private NavigationView nvDrawer;
    Dialog succDialog;
    Button okBtn;
    ImageView succIcon;
    TextView succMsg1, succMsg2, succMsg3;
    private boolean initConfCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_initial_configuration);
        mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nvDrawer = (NavigationView)findViewById(R.id.actionItemsView);
        setupDrawerContent(nvDrawer);
        macceptcoord = new double[4];
        succDialog = new Dialog(this);

        FabSpeedDial fabSpeedDial = (FabSpeedDial)findViewById(R.id.speeddial);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(),""+ menuItem.getTitle(), Toast.LENGTH_LONG).show();
                if(menuItem.getTitle().equals("Add Point")){
                    if(!initConfCompleted) {
                        Fragment mfrag;
                        Bundle bundle = new Bundle();
                        mfrag = new DeploymentFragment();
                        bundle.putStringArray("Coordinates", coordinates);
                        mfrag.setArguments(bundle);
                        fragSelection(mfrag);
                    }
                    else {
                        dialogBoxDisplay();
                    }
                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        firstcoord = (TextView) findViewById(R.id.firstcoord);
        secondcoord = (TextView) findViewById(R.id.secondcoord);
        firstcoordTitle = (TextView) findViewById(R.id.firstcoordTitle);
        secondcoordTitle = (TextView) findViewById(R.id.seccoordTitle);
        runtime_permissions();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23) /* && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)) */{

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            Toast.makeText(getApplicationContext(),"requestPerm", Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(), "sdk<23", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(getApplicationContext(),"permGranted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), GPS_Service.class);
                startService(intent);
            }else{
                //runtime_permissions();
                //Toast.makeText(getApplicationContext(),"permnotGranted", Toast.LENGTH_LONG).show();
            }
        }
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
                    if(!initConfCompleted) {
                        firstcoord.setText("\n Latitude: " + coordinates[0] + ", Longitude: " + coordinates[1]);
                    }
                    //Toast.makeText(getApplicationContext(),"location_updates", Toast.LENGTH_LONG).show();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_updates"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }


    public void fragSelection(Fragment mfrag){

        FragmentManager mfragmgr = getFragmentManager();
        FragmentTransaction mfragTrans = mfragmgr.beginTransaction();
        mfragTrans.replace(R.id.mainfragLayout, mfrag);
        mfragTrans.addToBackStack(null);
        //mfragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mfragTrans.commit();
    }


    public void selItemDrawer(MenuItem item){
        Fragment mfrag;
        Bundle bundle = new Bundle();


        item.setChecked(true);
        nvDrawer.setCheckedItem(item.getItemId());

        switch (item.getItemId()){

            case R.id.Deployment:
                if(!initConfCompleted) {
                    mfrag = new DeploymentFragment();
                    bundle.putStringArray("Coordinates", coordinates);
                    mfrag.setArguments(bundle);
                }
                else {
                    dialogBoxDisplay();
                    mDrawerLayout.closeDrawers();
                    return;
                }
                break;
            /*    will be implemeted later
            case R.id.Recovery:
                //fragmentClass = SettingsFragment.class;
                break;
            case R.id.Redeployment:
                //fragmentClass = .class;
                break;
             */
            case R.id.Sync:
                mfrag = new SyncFragment();
                break;

            case R.id.Dashboard:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("initComplete", initConfCompleted);
                ArrayList<Double> listDouble = new ArrayList<Double>();
                listDouble.add(macceptcoord[0]);
                listDouble.add(macceptcoord[1]);
                listDouble.add(macceptcoord[2]);
                listDouble.add(macceptcoord[3]);
                intent.putExtra("acceptedCoordinates", listDouble);
                startActivityForResult(intent, 100);
                return;

            default:
                mfrag = new CoordinateFragment();
                break;
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

    //Interface
    @Override
    public void onSelected(String[] data) {

        macceptcoord[index++] = Double.parseDouble(coordinates[0]);
        macceptcoord[index++] = Double.parseDouble(coordinates[1]);
        initConfCompleted = (index == 4);
        if(initConfCompleted)
        {
            Toast.makeText(getApplicationContext(), String.format("%s,%s", macceptcoord[0], macceptcoord[1]), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), String.format("%s,%s", macceptcoord[2], macceptcoord[3]), Toast.LENGTH_LONG).show();
            index = 0;
            //initConfCompleted = true;
            dialogBoxDisplay();


        }

    }

    private void dialogBoxDisplay() {
        succDialog.setContentView(R.layout.initialconfcompleted);
        succIcon = (ImageView)succDialog.findViewById(R.id.succIcon);
        succMsg1 = (TextView)succDialog.findViewById(R.id.succMsg1);
        succMsg2 = (TextView)succDialog.findViewById(R.id.succMsg2);
        succMsg3 = (TextView)succDialog.findViewById(R.id.succMsg3);
        okBtn = (Button)succDialog.findViewById(R.id.btnOk);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                succDialog.dismiss();
                firstcoordTitle.setText("\n First AIS Location");
                firstcoord.setText("\n Latitude: " + macceptcoord[0] + ", Longitude: " + macceptcoord[1]);
                secondcoordTitle.setText("\n Second AIS Location");
                secondcoord.setText("\n Latitude: " + macceptcoord[2] + ", Longitude: " + macceptcoord[3]);
            }
        });

        succDialog.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                succDialog.dismiss();
                firstcoordTitle.setText("\n First AIS Location");
                firstcoord.setText("\n Latitude: " + macceptcoord[0] + ", Longitude: " + macceptcoord[1]);
                secondcoordTitle.setText("\n Second AIS Location");
                secondcoord.setText("\n Latitude: " + macceptcoord[2] + ", Longitude: " + macceptcoord[3]);
            }
        });

        succDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        succDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("initComplete", initConfCompleted);
        ArrayList<Double> listDouble = new ArrayList<Double>();
        listDouble.add(macceptcoord[0]);
        listDouble.add(macceptcoord[1]);
        listDouble.add(macceptcoord[2]);
        listDouble.add(macceptcoord[3]);
        intent.putExtra("acceptedCoordinates", listDouble);
        startActivityForResult(intent, 100);

    }



}

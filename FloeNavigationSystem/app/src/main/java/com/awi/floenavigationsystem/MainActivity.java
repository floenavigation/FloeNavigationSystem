package com.awi.floenavigationsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private boolean initCompleted = false;
    private ArrayList<Double> listDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickListener(View view)
    {
        Intent calledIntent = getIntent();
        if(calledIntent.hasExtra("initComplete")){
            //Bundle args = getIntent().getExtras();
            //initCompleted = args.getBoolean("initComplete", false);
            initCompleted = true;
        }

        if(initCompleted) {
            listDouble = (ArrayList<Double>) getIntent().getSerializableExtra("acceptedCoordinates");
            Intent intent = new Intent(this, GridActivity.class);
            //Toast.makeText(getApplicationContext(),String.format("%s",GridInitialConfiguration.macceptcoord), Toast.LENGTH_LONG).show();
            intent.putExtra("acceptedCoordinates", listDouble);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Coordinate System not established!!", Toast.LENGTH_LONG).show();
        }
    }


    public void onListenerInitialConfig(View view)
    {
        //Toast.makeText(getApplicationContext(),"Inside Setup", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);

    }

}

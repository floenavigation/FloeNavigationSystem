package com.awi.floenavigationsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginPage extends Activity {

    RelativeLayout uname_pwd;
    EditText uname, pwd;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            uname_pwd.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        uname = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.password);
        uname_pwd = (RelativeLayout)findViewById(R.id.username_pwd);
        handler.postDelayed(runnable, 1000);
    }

    public void toInitialization(View view)
    {

        if(uname.getText().toString().equals("awi") && pwd.getText().toString().equals("awi")) {
            //Toast.makeText(getApplicationContext(),"Correct", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, GridInitialConfiguration.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_LONG).show();
        }
    }
}

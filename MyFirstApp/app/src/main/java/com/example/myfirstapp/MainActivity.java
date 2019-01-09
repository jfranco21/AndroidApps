package com.example.myfirstapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Locate the button in activity_main.xml
        button = (Button) findViewById(R.id.NewIncident);

        //Capture the button in activity_main.xml
        button.setOnClickListener(new OnClickListener(){
            public void onClick(View arg0) {
                //Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(myIntent);
            }
        });


        button = (Button) findViewById(R.id.EditDelete);
        button.setOnClickListener(new OnClickListener(){
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, EditDelete.class);
                startActivity(myIntent);
            }
        });
    }



}

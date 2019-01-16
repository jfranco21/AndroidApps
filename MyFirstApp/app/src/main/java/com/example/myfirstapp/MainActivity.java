package com.example.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the layout for the homepage
        setContentView(R.layout.activity_main);

        Button buttonNewIncident = (Button) findViewById(R.id.NewIncident);
        //When the new incident button is clicked...
        buttonNewIncident.setOnClickListener(new OnClickListener(){
            public void onClick(View arg0) {
                //Redirects the user to the NewActivity class which lets them create an incident
                Intent myIntent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(myIntent);
            }
        });

        Button buttonEditDelete = (Button) findViewById(R.id.EditDelete);
        //When the Edit/Delete Incident button is clicked...
        buttonEditDelete.setOnClickListener(new OnClickListener(){
            public void onClick(View arg0) {
                //Redirects the user to the EditDelete class which deletes them choose an incident they have previously created to edit/delete
                Intent myIntent = new Intent(MainActivity.this, EditDelete.class);
                startActivity(myIntent);
            }
        });
    }



}

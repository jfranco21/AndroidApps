package com.example.myfirstapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.List;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

public class EditDelete extends Activity {
    Context context;
    String text = "";
    int identifier = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the view from new_activity.xml
        setContentView(R.layout.new2_activity);

        /*DatabaseHelper db = new DatabaseHelper(this);
        List<Incident> incidents = db.getAllIncidents();
        for(Incident i: incidents){
            String log = "Fire number: " + i.getFireNumber() + ", Fire name: " + i.getName()
                    + "\n";
            text = text + log;
        }
        TextView textview = (TextView) findViewById(R.id.textView);
        textview.setText(text);*/

        final TableLayout tlContent = (TableLayout) findViewById(R.id.stuff);
        final DatabaseHelper db = new DatabaseHelper(this);
        List<Incident> incidents = db.getAllIncidents();
        for(final Incident i: incidents){
            TableLayout tl = new TableLayout(this);
            tl.setStretchAllColumns(true);
            tl.setTag("tl"+identifier);
            TableRow r = new TableRow(this);

            final Button deleteButton = new Button(this);
            deleteButton.setWidth(0);
            final Button editButton = new Button(this);
            editButton.setWidth(0);


            editButton.setText(i.getName());
            editButton.setTag(i.getFireNumber());
            deleteButton.setText("X");
            deleteButton.setId(identifier);
            deleteButton.setTag(i.getFireNumber());

            //delete button for each incident row
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) v.getTag();
                    View deleteElement = tlContent.findViewWithTag("tl"+v.getId());
                    tlContent.removeView(deleteElement);
                    db.deleteIncident2(id);
                }
            });

            //incident name button for editing the selected incident

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent myIntent = new Intent(EditDelete.this , modify.class);
                    String fireNum = (String)editButton.getTag();
                    myIntent.putExtra("FireNum", fireNum);
                    myIntent.putExtra("FireName", i.getName());
                    myIntent.putExtra("TotalCost", i.getCost());
                    myIntent.putExtra("TotalHours", i.getHours());
                    myIntent.putExtra("Entries", i.getEntries());
                    startActivity(myIntent);*/


                    Intent myIntent = new Intent(EditDelete.this, modify.class);
                    JSONObject IncidentJSON = new JSONObject();
                    try {
                        IncidentJSON.put("FireNumber", i.getFireNumber());
                        IncidentJSON.put("IncidentName", i.getName());
                        IncidentJSON.put("TotCost", i.getCost());
                        IncidentJSON.put("TotHours", i.getHours());
                        IncidentJSON.put("Entries", i.getEntries());
                    }
                    catch(JSONException e){

                    }
                    myIntent.putExtra("incidentJSON", IncidentJSON.toString());
                    startActivity(myIntent);

                }
            });

            r.addView(editButton);
            r.addView(deleteButton);
            tl.addView(r);
            tlContent.addView(tl);
            identifier++;
        }


        //onClick function for the Home button the returns user to the homepage
        Button homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EditDelete.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

    }

}

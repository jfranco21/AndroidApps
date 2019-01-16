package com.example.myfirstapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class EditDelete extends Activity {
    //Used for making unqiue identifiers for the incident entries that we will be making
    int identifier = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets the view layout for this class
        setContentView(R.layout.new2_activity);

        //For accessing the DatabaseHelper functions
        final DatabaseHelper db = new DatabaseHelper(this);

        //For accessing the TableLayout element that we will be storing entries of incidents that the user can choose from for editing/deleting them
        final TableLayout tlContent = (TableLayout) findViewById(R.id.stuff);

        //Returns a list of incidents from the database table
        List<Incident> incidents = db.getAllIncidents();

        //If a list of incidents was returned then...
        if(incidents.isEmpty() != true) {
            //For each incident create an entry
            for (final Incident i : incidents) {
                //Creates the parts needed for making an entry
                TableLayout tl = new TableLayout(this);
                tl.setStretchAllColumns(true);
                //Sets a tag for the incident entry, so that we can remove the entry in case the incident's delete button is clicked
                tl.setTag("tl" + identifier);
                TableRow r = new TableRow(this);
                final Button deleteButton = new Button(this);
                deleteButton.setWidth(0);
                //Note: the edit button for an incident will be its name
                final Button editButton = new Button(this);
                editButton.setWidth(0);
                editButton.setText(i.getName());
                editButton.setTextColor(Color.BLACK);
                editButton.setTag(i.getFireNumber());
                deleteButton.setText("X");
                //Sets an id for the delete button, so that we can identify the entry
                deleteButton.setId(identifier);
                //Sets the delete button tag equal to the incident's fire #, so that we can delete it from the database table
                deleteButton.setTag(i.getFireNumber());

                //Removes the incident entry and the incident from the database table
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Gets the incident's fire # from the delete button tag
                        String id = (String) v.getTag();
                        //Removes the incident entry
                        View deleteElement = tlContent.findViewWithTag("tl" + v.getId());
                        tlContent.removeView(deleteElement);
                        //Calls the deleteIncident function in the DatabaseHelper class and passes the fire # to remove the incident from the database table
                        db.deleteIncident(id);
                    }
                });

                //When the user clicks on the incident's edit button...
                //Note: the edit button for an incident is a button with the incident's name on it
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Creates an intent that will take the user to the modify class, so that they can edit the incident
                        Intent myIntent = new Intent(EditDelete.this, modify.class);
                        //Creates a JSON obj containing the incident's information
                        JSONObject IncidentJSON = new JSONObject();
                        try {
                            IncidentJSON.put("FireNumber", i.getFireNumber());
                            IncidentJSON.put("IncidentName", i.getName());
                            IncidentJSON.put("TotCost", i.getCost());
                            IncidentJSON.put("TotHours", i.getHours());
                            IncidentJSON.put("Entries", i.getEntries());
                        } catch (JSONException e) {

                        }
                        //Stores the incident JSON obj as a string in the intent
                        myIntent.putExtra("incidentJSON", IncidentJSON.toString());
                        //Executes the intent
                        startActivity(myIntent);
                    }
                });

                //Puts together the components for the entry and adds it to the TableLayout that contains all the incident options that the user can choose from
                r.addView(editButton);
                r.addView(deleteButton);
                tl.addView(r);
                tlContent.addView(tl);
                //Increments the unique identifier
                identifier++;
            }
        }

        //Returns the user to the homepage
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

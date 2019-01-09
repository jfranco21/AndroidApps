package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class modify extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Input text fields that contain the times for on/off for the shift
    EditText TextON1;
    EditText TextOFF1;
    EditText TextON2;
    EditText TextOFF2;
    //the input field for the date
    EditText dateEditText;
    //Input text fields for the hourly rate and # of crew members
    EditText HourlyRate;
    EditText CrewMembers;
    //id is used to assign unique identifiers for the entry results for removal purposes
    Integer id = 1;
    //variables for calculating the total cost and total cost for an incident
    double TotalHours = 0.0;
    double TotalCost = 0.0;
    //The input text field that contains the total cost for the shift
    EditText result;
    EditText hours;
    //The input text fields for calculating the invoice total hours and cost
    EditText TotInvoice;
    EditText TotHours;

    Spinner spin;
    String [] BriefingOptions = {"Yes", "No"};

    //
    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets the layout that will be used for this class
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.briefing_spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, BriefingOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        //Variables used for accessing the elements on the app
        HourlyRate = (EditText)findViewById(R.id.Rate);
        CrewMembers = (EditText)findViewById(R.id.NumPeople);
        TextON1 = (EditText)findViewById(R.id.on1);
        TextON2 = (EditText)findViewById(R.id.on2);
        TextOFF1 = (EditText)findViewById(R.id.off1);
        TextOFF2 = (EditText)findViewById(R.id.off2);
        result = (EditText)findViewById(R.id.totCost);
        hours = (EditText)findViewById(R.id.totHours);
        dateEditText = (EditText)findViewById(R.id.dateInput);


        //onClick function for the Home button the returns user to the homepage
        Button homeButton = (Button)findViewById(R.id.Home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(modify.this, MainActivity.class);
                startActivity(myIntent);
            }

        });

        //Code for generating the saved incident results that the user selected
        Bundle bundle =getIntent().getExtras();
        String stringJSON = bundle.getString("incidentJSON");
        try {
            JSONObject jsonObject = new JSONObject(stringJSON);
            String fireNumber = jsonObject.getString("FireNumber");
            String name = jsonObject.getString("IncidentName");
            Double hours = Double.parseDouble(jsonObject.getString("TotHours"));
            Double cost = Double.parseDouble(jsonObject.getString("TotCost"));
            String entries = jsonObject.getString("Entries");

            EditText editTextName = (EditText) findViewById(R.id.IncidentName);
            editTextName.setText(name);
            EditText editTextNumber = (EditText) findViewById(R.id.fireNumber);
            editTextNumber.setText(fireNumber);
            EditText editTextHours = (EditText) findViewById(R.id.TotalHours);
            editTextHours.setText(String.valueOf(hours));
            EditText editTextInvoice = (EditText) findViewById(R.id.TotalInvoice);
            editTextInvoice.setText(String.valueOf(cost));

            JSONArray Entries = new JSONArray(entries);
            for(int i = 0; i < Entries.length(); i++){
                JSONObject Incident = Entries.getJSONObject(i);
                layoutAddResult(Incident.getString("Cost"), Incident.getString("Hours"), Incident.getString("Date"));

            }
        }
        catch(JSONException e){

        }



        result = (EditText)findViewById(R.id.totCost);
        hours = (EditText)findViewById(R.id.totHours);
        //gets the total shift cost when the result button is clicked on the app
        Button resultButton = (Button)findViewById(R.id.result1);
        resultButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String hourlyRateText = HourlyRate.getText().toString();
                String NumPeopleText = CrewMembers.getText().toString();
                String textVal = TextON1.getText().toString();
                String textVal2 = TextOFF1.getText().toString();
                String textVal3 = TextON2.getText().toString();
                String textVal4 = TextOFF2.getText().toString();

                String textVal5 = spin.getSelectedItem().toString();
                double briefingTime = 0.0;
                if(textVal5 == "Yes"){
                    briefingTime = 0.5;
                }

                int hourlyRate = Integer.parseInt(hourlyRateText);
                int numPeople = Integer.parseInt(NumPeopleText);
                int num1 = getCorrectValue(textVal);
                int num2 = getCorrectValue(textVal2);
                int num3 = getCorrectValue(textVal3);
                int num4 = getCorrectValue(textVal4);

                double totalHours = ((num2 - num1) + (num4 - num3));
                totalHours = ((totalHours * numPeople)/100) + briefingTime;
                TotalHours = TotalHours + totalHours;
                String TotalHoursText = String.valueOf(TotalHours);

                double totalCost = totalHours * hourlyRate;
                TotalCost = TotalCost + totalCost;
                String TotalCostText = String.valueOf(TotalCost);

                result.setText(TotalCostText);
                hours.setText(TotalHoursText);

            }
        });

        //when save button is clicked it saves an entry of the total cost and total hours
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String TotalHoursText = String.valueOf(TotalHours);
                String TotalCostText = String.valueOf(TotalCost);
                String DateText = dateEditText.getText().toString();

                layoutAddResult(TotalCostText, TotalHoursText, DateText);
                CalcInvoice();
            }

        });


        //stores the incident name and entry results into a database using a json
        Button storeButton = (Button)findViewById(R.id.SaveIncident);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeJSON();
                String jsonString = MakeJSON();
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String fireNumber = jsonObject.getString("FireNumber");
                    String name = jsonObject.getString("IncidentName");
                    Double hours = Double.parseDouble(jsonObject.getString("TotHours"));
                    Double cost = Double.parseDouble(jsonObject.getString("TotCost"));
                    String entries = jsonObject.getString("Entries");

                    //db.addIncident(new Incident(fireNumber, name, cost, hours, entries));

                    db.update(fireNumber, name, cost, hours, entries);
                }
                catch(JSONException ex){

                }
            }
        });








    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), BriefingOptions[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }


    //function for returning the correct value for the number of hours since 30 minutes is considered .5
    public int getCorrectValue(String numberText){
        if(numberText.charAt(2) == '3' && numberText.charAt(3) == '0'){
            int position = numberText.indexOf("30");
            String editNumberText = numberText.substring(0, position)+'5'+numberText.substring(position+1);
            int desiredVal = Integer.parseInt(editNumberText);
            return desiredVal;
        }
        else{
            int numberVal = Integer.parseInt(numberText);
            return numberVal;
        }
    }

    public void CalcInvoice(){
        TableLayout tlContent = (TableLayout)findViewById(R.id.resultContent);
        double totHours = 0.0;
        double totCost = 0.0;
        if(tlContent.getChildCount() != 0) {
            int count = 0;
            //Calculates the total hours and cost for the entries that were created
            do {
                count++;
                TableLayout tl = (TableLayout)tlContent.getChildAt(count-1);
                //gets the row of the layout
                TableRow row = (TableRow)tl.getChildAt(0);

                //Gets the cost value from TextView in the row and adds it to totCost variable
                TextView costTextView = (TextView)row.getChildAt(1);
                String costText = costTextView.getText().toString();
                double cost = Double.parseDouble(costText);
                totCost = totCost + cost;

                TextView hoursTextView = (TextView)row.getChildAt(2);
                String hoursText = hoursTextView.getText().toString();
                double hours = Double.parseDouble(hoursText);
                totHours = totHours + hours;

            } while (count != tlContent.getChildCount());
        }

        String totCostText = String.valueOf(totCost);
        String totHoursText = String.valueOf(totHours);
        TotInvoice = (EditText)findViewById(R.id.TotalInvoice);
        TotInvoice.setText(totCostText);
        TotHours = (EditText)findViewById(R.id.TotalHours);
        TotHours.setText(totHoursText);

    }

    public void layoutAddResult(String cost, String hours, String date){
        final TableLayout tlContent = (TableLayout)findViewById(R.id.resultContent);
        //Creates the parts of the layout for a saved entry and along with a unique identifier for each
        TableLayout tl = new TableLayout(this);
        tl.setTag("tl" + id);
        tl.setStretchAllColumns(true);
        TableRow r = new TableRow(this);
        r.setTag("r" + id);
        TextView dateText  = new TextView(this);
        TextView costText = new TextView(this);
        costText.setTag("c" + id);
        TextView hoursText = new TextView(this);
        hoursText.setTag("h" + id);
        Button deleteButton = new Button(this);
        deleteButton.setTag("b" + id);
        deleteButton.setId(id);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View deleteElement = tlContent.findViewWithTag("tl"+v.getId());
                tlContent.removeView(deleteElement);
                CalcInvoice();
            }
        });
        deleteButton.setText("X");
        dateText.setText(date);
        costText.setText(cost);
        hoursText.setText(hours);
        r.addView(dateText);
        r.addView(costText);
        r.addView(hoursText);
        r.addView(deleteButton);
        tl.addView(r);
        tlContent.addView(tl);

        id++;


    }

    public String MakeJSON() {
        JSONObject IncidentJSON = new JSONObject();
        try {
            //Stores the name, total cost, total hours, and number of entries in the JSON object
            String TotCost = ((EditText) findViewById(R.id.TotalInvoice)).getText().toString();
            String TotHours = ((EditText) findViewById(R.id.TotalHours)).getText().toString();
            String IncidentName = ((EditText) findViewById(R.id.IncidentName)).getText().toString();
            String fireNumber = ((EditText) findViewById(R.id.fireNumber)).getText().toString();
            IncidentJSON.put("FireNumber", fireNumber);
            IncidentJSON.put("IncidentName", IncidentName);
            IncidentJSON.put("TotCost", TotCost);
            IncidentJSON.put("TotHours", TotHours);


            TableLayout tlContent = (TableLayout) findViewById(R.id.resultContent);
            if (tlContent.getChildCount() != 0) {
                JSONArray Entries = new JSONArray();
                int count = 0;
                //Calculates the total hours and cost for the entries that were created
                do {
                    count++;
                    TableLayout tl = (TableLayout) tlContent.getChildAt(count - 1);
                    //gets the row of the layout
                    TableRow row = (TableRow) tl.getChildAt(0);

                    JSONObject Entry = new JSONObject();
                    try{
                        //Gets the cost value from TextView in the row and adds it to totCost variable
                        String dateText = ((TextView) row.getChildAt(0)).getText().toString();
                        String costText = ((TextView) row.getChildAt(1)).getText().toString();
                        String hoursText = ((TextView) row.getChildAt(2)).getText().toString();
                        Entry.put("Date", dateText);
                        Entry.put("Cost", costText);
                        Entry.put("Hours", hoursText);
                        Entries.put(Entry);
                    }
                    catch(JSONException f){
                    }
                } while (count != tlContent.getChildCount());
                IncidentJSON.put("Entries", Entries);
            }

        }
        catch (JSONException e) {

        }

        return IncidentJSON.toString();
    }


}

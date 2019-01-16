package com.example.myfirstapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class modify extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //id is used to assign unique identifiers for the entry results for removal purposes
    Integer id = 1;

    //Input text fields for the hourly rate and # of crew members
    EditText HourlyRate;
    EditText CrewMembers;

    //Input text fields that contain the times for on/off for the shift
    EditText TextON1;
    EditText TextOFF1;
    EditText TextON2;
    EditText TextOFF2;

    //the input field for the date
    EditText dateEditText;

    //The input text field that contains the total cost for the shift
    EditText result;
    EditText hours;

    //Options for whether or not the crew boss attended the briefing in the drop down menu
    Spinner spin;
    String [] BriefingOptions = {"Yes", "No"};

    //Global variables for keeping track of the total invoice hours and cost
    double TotalHours = 0.0;
    double TotalCost = 0.0;

    //For accessing the database functions in the DatabaseHelper class
    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets the layout that will be used for this class
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);

        //Displays text that tells the user that they are in the activity that lets them create a new incident
        TextView NameOfActivity = (TextView)findViewById(R.id.NameOfActivity);
        NameOfActivity.setText("Edit Incident");

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.briefing_spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the options for crew boss briefing
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

        //Code for generating the saved incident's information based on the user's selection
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

        //Gets the total shift cost when the reset button is clicked
        Button resultButton = (Button)findViewById(R.id.result1);
        resultButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                //Gets the values entered and selected by the user for their shift
                int hourlyRate = Integer.parseInt(HourlyRate.getText().toString());
                int numPeople = Integer.parseInt(CrewMembers.getText().toString());
                String textVal = TextON1.getText().toString();
                String textVal2 = TextOFF1.getText().toString();
                String textVal3 = TextON2.getText().toString();
                String textVal4 = TextOFF2.getText().toString();
                String textVal5 = spin.getSelectedItem().toString();
                double briefingTime = 0.0;
                if(textVal5 == "Yes"){
                    briefingTime = 0.5;
                }

                //If the user leaves the on/off fields empty or enters a 0 set the int values to 0 this prevents the app from closing due to an error
                //And does the correct calculation when a crew does not take lunch due to an emergency
                int num1, num2, num3, num4;
                if(TextUtils.isEmpty(textVal) || textVal.equals("0")){
                    num1 = 0;
                }
                else{
                    num1 = getCorrectValue(textVal);
                }

                if(TextUtils.isEmpty(textVal2) || textVal2.equals("0")){
                    num2 = 0;
                }
                else{
                    num2 = getCorrectValue(textVal2);
                }

                if(TextUtils.isEmpty(textVal3) || textVal3.equals("0")){
                    num3 = 0;
                }
                else{
                    num3 = getCorrectValue(textVal3);
                }

                if(TextUtils.isEmpty(textVal4) || textVal4.equals("0")){
                    num4 = 0;
                }
                else{
                    num4 = getCorrectValue(textVal4);
                }

                //Calculates the hours and cost depending how on/off input fields were filled
                double totalHours = 0.0;
                if(num1 != 0 && num2 != 0 && num3 != 0 && num4 != 0) {
                    totalHours = ((num2 - num1) + (num4 - num3));
                    totalHours = calcTotHours(totalHours, numPeople, briefingTime);
                    calcTotCost(totalHours, hourlyRate);
                }
                else if(num1 != 0 && num2 == 0 && num3 == 0 && num4 != 0){
                    totalHours = num4 - num1;
                    totalHours = calcTotHours(totalHours, numPeople, briefingTime);
                    calcTotCost(totalHours, hourlyRate);
                }
                else if(num1 != 0 && num2 != 0 && num3 == 0 && num4 == 0){
                    totalHours = num2 - num1;
                    totalHours = calcTotHours(totalHours, numPeople, briefingTime);
                    calcTotCost(totalHours, hourlyRate);
                }
                else if(num1 == 0 && num2 == 0 && num3 != 0 && num4 != 0){
                    totalHours = num4 - num3;
                    totalHours = calcTotHours(totalHours, numPeople, briefingTime);
                    calcTotCost(totalHours, hourlyRate);
                }
                else if(num1 == 0 && num2 != 0 && num3 != 0 && num4 == 0){
                    totalHours = num3 - num2;
                    totalHours = calcTotHours(totalHours, numPeople, briefingTime);
                    calcTotCost(totalHours, hourlyRate);
                }
            }
        });

        //When Save Entry button is clicked it saves an entry that includes the date, cost, and hours for the shift
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String TotalHoursText = String.valueOf(TotalHours);
                String TotalCostText = String.valueOf(TotalCost);
                String DateText = dateEditText.getText().toString();
                layoutAddResult(TotalCostText, TotalHoursText, DateText);
                //Updates the Total crew Invoice and Total Hours Worked input fields since a new shift entry was added
                CalcInvoice();
            }

        });

        //When the Save Incident button is clicked it saves the incident's name, fire #, entries, total crew invoice, and total hours into a database table
        //Note: The database is a file in the phone and is deleted by uninstalling the app
        Button storeButton = (Button)findViewById(R.id.SaveIncident);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = MakeJSON();
                try{
                    String fireNumber = json.getString("FireNumber");
                    String name = json.getString("IncidentName");
                    Double hours = Double.parseDouble(json.getString("TotHours"));
                    Double cost = Double.parseDouble(json.getString("TotCost"));
                    String entries = json.getString("Entries");
                    db.update(fireNumber, name, cost, hours, entries);
                }
                catch(JSONException ex){

                }
            }
        });

        //When the Home button is clicked it returns the user to the homepage
        Button homeButton = (Button)findViewById(R.id.Home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(modify.this, MainActivity.class);
                startActivity(myIntent);
            }

        });

        //When the Reset button is clicked it resets the input fields for calculating a shift
        Button resetButton = (Button)findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextON1.setText("0");
                TextON2.setText("0");
                TextOFF1.setText("0");
                TextOFF2.setText("0");
                result.setText("0");
                hours.setText("0");
                dateEditText.setText("mm/dd/yyyy");
                TotalCost = 0.0;
                TotalHours = 0.0;
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

    //Returns the correct value for the number of hours since 30 minutes is considered 50
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

    //Calculates the total hours for the Total Hours field
    public double calcTotHours(double totalHours, int numPeople, double briefingTime){
        totalHours = ((totalHours * numPeople) / 100) + briefingTime;
        TotalHours = TotalHours + totalHours;
        String TotalHoursText = String.valueOf(TotalHours);
        hours.setText(TotalHoursText);
        return totalHours;
    }

    //Calculates the total cost for the Shift Cost field
    public void calcTotCost(double totalHours, double hourlyRate){
        double totalCost = totalHours * hourlyRate;
        TotalCost = TotalCost + totalCost;
        String TotalCostText = String.valueOf(TotalCost);
        result.setText(TotalCostText);
    }

    //Adds a shift entry for the incident
    public void layoutAddResult(String cost, String hours, String date){
        final TableLayout tlContent = (TableLayout)findViewById(R.id.resultContent);
        //Creates the parts needed for making a shift entry
        TableLayout tl = new TableLayout(this);
        //Sets and identifier for the TableLayout for a shift entry for deleting purposes
        tl.setTag("tl" + id);
        tl.setStretchAllColumns(true);
        TableRow r = new TableRow(this);
        TextView dateText  = new TextView(this);
        dateText.setTextColor(Color.BLACK);
        TextView costText = new TextView(this);
        costText.setTextColor(Color.BLACK);
        TextView hoursText = new TextView(this);
        hoursText.setTextColor(Color.BLACK);
        Button deleteButton = new Button(this);
        //Sets an identifier for the delete button, so that we can find the entry
        deleteButton.setId(id);
        //Deletes the entry when its delete button is clicked
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View deleteElement = tlContent.findViewWithTag("tl"+v.getId());
                tlContent.removeView(deleteElement);
                //When the shift entry is deleted the total hrs and total cost for the incident is recalculated
                CalcInvoice();
            }
        });
        deleteButton.setText("X");
        dateText.setText(date);
        costText.setText(cost);
        hoursText.setText(hours);

        //Adds the parts of a shift entry together and adds the shift entry to the table layout containing all of the shift entries
        r.addView(dateText);
        r.addView(costText);
        r.addView(hoursText);
        r.addView(deleteButton);
        tl.addView(r);
        tlContent.addView(tl);

        //Increments unique identifier for the shift entries
        id++;
    }

    //Calculates the total cost and hours for an incident
    public void CalcInvoice(){
        //Gets the TableLayout that contains all of the shift entries
        TableLayout tlContent = (TableLayout)findViewById(R.id.resultContent);

        double totHours = 0.0;
        double totCost = 0.0;
        //If there are shift entries...
        if(tlContent.getChildCount() != 0) {
            int count = 0;
            //Iterate through each shift entry and add the shift entry's hours and cost to their corresponding totals
            do {
                count++;
                TableLayout tl = (TableLayout)tlContent.getChildAt(count-1);
                TableRow row = (TableRow)tl.getChildAt(0);
                //Gets the shift entry's cost and adds it to the total cost
                String costText = ((TextView)row.getChildAt(1)).getText().toString();
                double cost = Double.parseDouble(costText);
                totCost = totCost + cost;
                //Gets the shift entry's # of hours and adds it to the total # of hours
                String hoursText = ((TextView)row.getChildAt(2)).getText().toString();
                double hours = Double.parseDouble(hoursText);
                totHours = totHours + hours;
            } while (count != tlContent.getChildCount());
        }
        //Sets the total cost for the Shift Cost input field
        EditText TotInvoice = (EditText)findViewById(R.id.TotalInvoice);
        TotInvoice.setText(String.valueOf(totCost));
        //Sets the total # of hours for the Total Hours input field
        EditText TotHours = (EditText)findViewById(R.id.TotalHours);
        TotHours.setText(String.valueOf(totHours));
    }

    //Returns a JSON object for storing an Incident's information into the database
    public JSONObject MakeJSON() {
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

            //If there are shift entries it creates an array of JSON obj entries and stores the array in Incident JSON obj
            TableLayout tlContent = (TableLayout) findViewById(R.id.resultContent);
            if (tlContent.getChildCount() != 0) {
                JSONArray Entries = new JSONArray();
                int count = 0;
                //Iterates through each shift entry
                do {
                    count++;
                    TableLayout tl = (TableLayout) tlContent.getChildAt(count - 1);
                    TableRow row = (TableRow) tl.getChildAt(0);
                    JSONObject Entry = new JSONObject();
                    //Stores shift entries date, cost, and hours in the Entry JSON obj
                    try{
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
                //Adds array of JSON Entry objs in the Incident JSON
                IncidentJSON.put("Entries", Entries);
            }
        }
        catch (JSONException e) {

        }
        return IncidentJSON;
    }


}

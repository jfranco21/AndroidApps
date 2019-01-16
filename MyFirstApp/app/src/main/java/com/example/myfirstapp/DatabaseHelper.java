package com.example.myfirstapp;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "IncidentsManager";
    private static final String TABLE_INCIDENTS = "Incidents";
    //The columns for the table and the unique id that will be used for the incidents is the incident fire #
    private static final String KEY_ID = "incidentNumber";
    private static final String KEY_NAME = "name";
    private static final String KEY_COST = "cost";
    private static final String KEY_HOURS = "hours";
    private static final String KEY_ENTRIES = "entries";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates the TABLE_INCIDENTS and its columns
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_INCIDENTS_TABLE = "CREATE TABLE " + TABLE_INCIDENTS + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_COST + " REAL," + KEY_HOURS + " REAL, " + KEY_ENTRIES + " TEXT" + ")";
        db.execSQL(CREATE_INCIDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
        onCreate(db);
    }

    //Adds a new incident row to the TABLE_INCIDENTS
    void addIncident(Incident incident){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, incident.getName());
        values.put(KEY_ID, incident.getFireNumber());
        values.put(KEY_COST, incident.getCost());
        values.put(KEY_HOURS, incident.getHours());
        values.put(KEY_ENTRIES, incident.getEntries());

        db.insert(TABLE_INCIDENTS, null, values);
    }

    //Returns an Incident's information from the TABLE_INCIDENTS by using its fire #
    Incident getIncident(String fireNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INCIDENTS, new String[] {KEY_ID, KEY_NAME, KEY_COST, KEY_HOURS, KEY_ENTRIES}, KEY_ID + "=?",
                new String[]{fireNumber}, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Incident incident = new Incident(cursor.getString(0), cursor.getString(1),
                cursor.getDouble(2), cursor.getDouble(3), cursor.getString(4));
        return incident;
    }

    //Returns a list of incidents that includes their fire #, name, total cost, total hrs, and shift entries
    public List<Incident> getAllIncidents(){
        List<Incident> incidentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INCIDENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Incident incident = new Incident();

                incident.setFireNumber(cursor.getString(0));
                incident.setName(cursor.getString(1));
                incident.setCost(cursor.getDouble(2));
                incident.setHours(cursor.getDouble(3));
                incident.setEntries(cursor.getString(4));

                incidentList.add(incident);

            } while (cursor.moveToNext());

        }
        return incidentList;
    }

    //Deletes an incident from the database table using its fire number
    public void deleteIncident(String fireNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INCIDENTS, KEY_ID + "=?", new String[]{fireNumber});
        db.close();
    }

    //Removes all of the row entries in the TABLE_INCIDENTS
    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INCIDENTS, null, null);
    }

    //Deletes the IncidentManager database
    public void deleteDatabase(Context context){

        context.deleteDatabase("IncidentsManager");
    }

    //Deletes the TABLE_INCIDENTS
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
    }

    //Updates an edited incident's information by updating its corresponding row
    public void update(String fireNumber, String name, Double cost, Double hours, String entries){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("incidentNumber", fireNumber);
        values.put("name", name);
        values.put("cost", cost);
        values.put("hours", hours);
        values.put("entries", entries);
        db.update(TABLE_INCIDENTS, values, KEY_ID+ " = ?", new String[]{fireNumber});
    }
}

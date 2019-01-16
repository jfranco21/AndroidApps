package com.example.myfirstapp;

public class Incident {
    //Variables for a fire incident
    String fireNumber;
    String name;
    Double cost;
    Double hours;
    String entries;

    //Empty constructor
    public Incident(){

    }

    //Constructor that sets an incident's parameters using the variables that were passed
    public Incident(String fireNumber, String name, Double cost, Double hours, String entries){
        this.fireNumber = fireNumber;
        this.name = name;
        this.cost = cost;
        this.hours = hours;
        this.entries = entries;
    }

    //Returns the name for the incident
    public String getName(){
        return name;
    }

    //Returns the fire # for the incident
    public String getFireNumber(){
        return fireNumber;
    }

    //Returns the total cost for the incident
    public Double getCost(){
        return cost;
    }

    //Returns the # of total hours for the incident
    public Double getHours(){
        return hours;
    }

    //Returns the shift entries for the incident
    public String getEntries(){
        return entries;
    }

    //Updates the name of the incident
    public void setName(String name){
        this.name = name;
    }

    //Updates the fire number for the incident
    public void setFireNumber(String fireNumber){
        this.fireNumber = fireNumber;
    }

    //Updates the total cost the incident
    public void setCost(Double cost){
        this.cost = cost;
    }

    //Updates the total hours for the incident
    public void setHours(Double hours){
        this.hours = hours;
    }

    //Updates the shift entries for the incident
    public void setEntries(String entries){
        this.entries = entries;
    }
}

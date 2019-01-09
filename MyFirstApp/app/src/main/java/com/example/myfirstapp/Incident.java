package com.example.myfirstapp;

import java.util.List;

public class Incident {

    String fireNumber;
    String name;
    Double cost;
    Double hours;
    String entries;


    //Constructors
    public Incident(){

    }

    public Incident(String fireNumber, String name, Double cost, Double hours, String entries){
        this.fireNumber = fireNumber;
        this.name = name;
        this.cost = cost;
        this.hours = hours;
        this.entries = entries;
    }

    /*public Incident(int id, String fireNumber, String name, Double cost, Double hours){

        this.fireNumber = fireNumber;
        this.name = name;
        this.cost = cost;
        this.hours = hours;
    }*/

    //Functions that return a value of an incident

    public String getName(){
        return name;
    }

    public String getFireNumber(){
        return fireNumber;
    }

    public Double getCost(){
        return cost;
    }

    public Double getHours(){
        return hours;
    }

    public String getEntries(){
        return entries;
    }



    //Functions that change a value for an incident

    public void setName(String name){
        this.name = name;
    }

    public void setFireNumber(String fireNumber){
        this.fireNumber = fireNumber;
    }

    public void setCost(Double cost){
        this.cost = cost;
    }

    public void setHours(Double hours){
        this.hours = hours;
    }

    public void setEntries(String entries){
        this.entries = entries;
    }
}

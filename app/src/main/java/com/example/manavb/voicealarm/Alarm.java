package com.example.manavb.voicealarm;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Alarm {
    private String name;
    private String time; // time is stored as 24hr format string
    private boolean[] days = new boolean[7];
    private boolean vibration;
    private String words;
    private String ringtoneURI;
    private boolean status;

    public Alarm(String name){
        this.name = name;
    }

    public Alarm(String name, String time, boolean status, boolean[] days, String ringtoneuri, boolean vibration){
        this.name = name;
        this.time = time;
        this.days = new boolean[7];
        this.days = days;
        this.vibration = vibration;
        this.ringtoneURI = ringtoneuri;
        this.status = status;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String n){
        this.name = n;
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public boolean getStatus(){
        return this.status;
    }
    public void setStatus(boolean s){
        this.status = s;
    }

    public boolean[] getDays(){
        return this.days;
    }
    public void setDays(boolean[] d){
        for(int i = 0; i < d.length; i++){
            if(d[i])
                days[i] = true;
            else
                days[i] = false;
        }
    }

    public String getWords(){
        return this.words;
    }
    public void setWords(String w){
        this.words = w;
    }

    public void setRingtoneURI(String uri){
        this.ringtoneURI = uri;
    }
    public String getRingtoneURIText(){
        return ringtoneURI;
    }

    public boolean getVibration(){
        return this.vibration;
    }
    public void setVibration(boolean v){ this.vibration = v;}

    @NonNull
    public String toString(){
        String res = "Name: " + name + ", time: " + time + "\n";
        res += "days: " + Arrays.toString(days) + "\n";
        res += "ringtone: " + ringtoneURI + "\n";
        res += "vibration: " + vibration + "\n";
        return res;
    }

}

package com.nllk.megaweatherapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;

import java.lang.reflect.Array;

public class Preferencies {

    SharedPreferences prefs;

    public Preferencies(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city", "Moskva, RU");
    }

    public void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

    public String getZipcode(){
        return prefs.getString("zip", "644000, RU");
    }

    public void setZipcode(String zip){
        prefs.edit().putString("zip", zip).apply();
    }

    public void setCitySourse(String source){
        prefs.edit().putString("CitySource", source).apply();
    }

    public String getCitySourse(){
        return prefs.getString("CitySource", "GPS");//ZIP or Name
    }

    public void setDegrees(String source){
        prefs.edit().putString("Degrees", source).apply();
    }

    public String getDegrees(){
        return prefs.getString("Degrees", "Cel");
    }
}
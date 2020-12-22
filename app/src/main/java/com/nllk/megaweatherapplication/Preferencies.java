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
    //получить город
    public String getCity(){
        return prefs.getString("city", "Moskva, RU");
    }
    //задать город
    public void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }
    //получить индекс
    public String getZipcode(){
        return prefs.getString("zip", "644000, RU");
    }
    //задать индекс
    public void setZipcode(String zip){
        prefs.edit().putString("zip", zip).apply();
    }
    //задать источник города
    public void setCitySourse(String source){
        prefs.edit().putString("CitySource", source).apply();
    }
    //получить источник города
    public String getCitySourse(){ return prefs.getString("CitySource", "GPS");//ZIP, name or gps
    }
    //задать единицы измерения
    public void setDegrees(String source){
        prefs.edit().putString("Degrees", source).apply();
    }
    //получить единцы измерения
    public String getDegrees(){
        return prefs.getString("Degrees", "Cel");
    }
}
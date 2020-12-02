package com.nllk.megaweatherapplication;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preferencies {

    SharedPreferences prefs;

    public Preferencies(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city", "Moskva, RU");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

    String getZipcode(){
        return prefs.getString("zip", "644000, RU");
    }

    void setZipcode(String zip){
        prefs.edit().putString("zip", zip).apply();
    }

}
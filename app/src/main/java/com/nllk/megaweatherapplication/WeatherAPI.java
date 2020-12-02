package com.nllk.megaweatherapplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
public class WeatherAPI
{
    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?zip=%s&units=metric";
    private static final String OPEN_WEATHER_MAP_API_CITY =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static JSONObject getJSON(Context context, String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key","908cab75dea287a2d3e9fd5b65fbaf0d");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not successful
            if(data.getInt("cod") != 200){
                return null;
            }
            else Log.e("Weather", "request code:"+data.getInt("cod"));

            return data;
        }catch(Exception e){
            return null;
        }
    }
}

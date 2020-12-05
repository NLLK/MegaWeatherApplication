package com.nllk.megaweatherapplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
public class WeatherAPI
{
    private static final String OPEN_WEATHER_MAP_API_FORECAST =
            "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=hourly,minutely&lang=ru&units=metric";
    private static final String OPEN_WEATHER_MAP_API_CITY =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ru";
    private static final String OPEN_WEATHER_MAP_API_ZIP =
            "https://api.openweathermap.org/data/2.5/weather?zip=%s&units=metric&lang=ru";

    public static JSONObject getJSONCity(Context context, String city){
        try {
            Log.i("Weather", "Get JSON");
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_CITY, city));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

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

            JSONObject coord =  data.getJSONObject("coord");

            url = new URL(String.format(OPEN_WEATHER_MAP_API_FORECAST, coord.getString("lat"), coord.getString("lon")));
            connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key","908cab75dea287a2d3e9fd5b65fbaf0d");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            json = new StringBuffer(1024);
            tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data2 = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                Log.e("Weather", "request code:"+data.getInt("cod"));
                return null;
            }
            else Log.e("Weather", "request code:"+data.getInt("cod"));

            data2.put("city",data.getString("name"));

            return data2;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static JSONObject getJSON(Context context, String zip){
        try {
            Log.i("Weather", "Get JSON");
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_ZIP, zip));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

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

            JSONObject coord =  data.getJSONObject("coord");

            url = new URL(String.format(OPEN_WEATHER_MAP_API_FORECAST, coord.getString("lat"), coord.getString("lon")));
            connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key","908cab75dea287a2d3e9fd5b65fbaf0d");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            json = new StringBuffer(1024);
            tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data2 = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                Log.e("Weather", "request code:"+data.getInt("cod"));
                return null;
            }
            else Log.e("Weather", "request code:"+data.getInt("cod"));

            data2.put("city",data.getString("name"));

            return data2;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

package com.nllk.megaweatherapplication;

import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity2 extends Activity {

    Typeface weatherFont;

    TextView cityField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView updatedField;
    ImageView weatherIcon;
    Preferencies preferencies;

    Location currentLocation;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        cityField = findViewById(R.id.city_field);
        detailsField = findViewById(R.id.details_field);
        updatedField = findViewById(R.id.updated_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        weatherIcon = (ImageView)findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        preferencies = new Preferencies(this);
        //updateWeatherData(preferencies.getZipcode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadLocation(null);
    }
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void setWeatherIcon(String iconId){
        Thread t = new Thread()  {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = BitmapFactory.decodeStream(new URL("https://openweathermap.org/img/wn/"+iconId+"@2x.png").openStream());
                    weatherIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherIcon.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            };
        };
        t.start();
    }
    Handler handler = new Handler();

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = WeatherAPI.getJSON(getApplicationContext(), city);
                if(json == null){
                    handler.post(() -> Toast.makeText(getApplicationContext(),
                           "А нет такого города, хах",
                            Toast.LENGTH_LONG).show());
                } else {
                    handler.post(() -> renderWeather(json));
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getString("icon"));

        }catch(Exception e){
            Log.e("Weather", "One or more fields not found in the JSON data");
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            editLocation(location);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);}
            editLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void editView()
    {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try
        {
            List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);

            if (addressList!=null)
            {
                String zip = addressList.get(0).getPostalCode();
                preferencies.setZipcode(zip);
                updateWeatherData(preferencies.getZipcode()+","+addressList.get(0).getCountryCode());
            }
            else {
                handler.post(() -> Toast.makeText(getApplicationContext(),
                        "А нет такого города, хах",
                        Toast.LENGTH_LONG).show());
            }

        } catch (IOException e) {
           e.printStackTrace();
        }
    }
    private void editLocation(Location location) {
        if (location == null)
            return;
        currentLocation = location;
    }
    public void reloadLocation(View view)
    {
        if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        Runnable runnable = () -> {
            while (currentLocation==null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*Message msg = handlerReloadLocation.obtainMessage();
            handlerReloadLocation.sendMessage(msg);*/
            handler.post(() -> editView());
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
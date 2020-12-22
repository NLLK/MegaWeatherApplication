package com.nllk.megaweatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainPageWeather extends AppCompatActivity {

    Typeface font;

    TextView cityField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView updatedField;
    TextView windField;
    TextView pressureField;
    TextView humidityField;
    TextView chanceOfRainField;
    Switch switchDegrees;
    Chip chipGPS;
    JSONObject lastJSON;
    ImageView weatherIcon;

    Preferencies preferencies;

    Location currentLocation;

    private LocationManager locationManager;

    Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_weather);
        cityField = findViewById(R.id.city_field);
        cityField.setOnClickListener(view -> callDialog());
        detailsField = findViewById(R.id.details_field);
        updatedField = findViewById(R.id.updated_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);
        chipGPS = findViewById(R.id.chip_gps);
        chipGPS.setOnClickListener(view -> showGPSChip());
        switchDegrees = findViewById(R.id.switchDegrees);
        switchDegrees.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if (preferencies.getDegrees().equals("Cel")) {
                preferencies.setDegrees("F");
            } else {
                preferencies.setDegrees("Cel");
            }
            updateAccordingToSource();
        });

        windField = findViewById(R.id.wind_field);
        pressureField = findViewById(R.id.pressure_field);
        humidityField = findViewById(R.id.humidity_field);
        chanceOfRainField = findViewById(R.id.chance_of_rain_field);

        font = Typeface.createFromAsset(getAssets(), "fonts/LatoMedium.ttf");

        cityField.setTypeface(font);
        detailsField.setTypeface(font);
        updatedField.setTypeface(font);
        currentTemperatureField.setTypeface(font);
        windField.setTypeface(font);
        pressureField.setTypeface(font);
        humidityField.setTypeface(font);
        chanceOfRainField.setTypeface(font);

        Button reload = findViewById(R.id.btnReloadLocation);
        reload.setOnClickListener(view -> reloadView(null));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        preferencies = new Preferencies(this);
        reloadLocation(null);
        preferencies.setCitySourse("GPS");
        preferencies.setDegrees("Cel");
    }

    private void showGPSChip() {
        chipGPS.setVisibility(View.INVISIBLE);
        reloadLocation(null);
        updateWeatherData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAccordingToSource();
    }

    public void updateAccordingToSource() {
        Log.i("Weather", "Start Circle");
        switch (preferencies.getCitySourse()) {
            case "GPS": {
                chipGPS.setVisibility(View.GONE);
                reloadLocation(null);
                updateWeatherData();
                break;
            }
            case "Name": {
                chipGPS.setVisibility(View.VISIBLE);
                updateWeatherDataCity();
                break;
            }
            case "ZIP": {
                chipGPS.setVisibility(View.VISIBLE);
                updateWeatherData();
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void setWeatherIcon(String iconId) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = BitmapFactory.decodeStream(new URL("https://openweathermap.org/img/wn/" + iconId + "@2x.png").openStream());
                    weatherIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherIcon.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        t.start();
    }

    private void updateWeatherData() {
        MainPageWeather activity = this;
        String zipcode = preferencies.getZipcode();
        new Thread() {
            public void run() {
                final JSONObject json = WeatherAPI.getJSON(activity, zipcode);
                lastJSON = json;
                if (json == null) {
                    handler.post(() -> Toast.makeText(getApplicationContext(),
                            "Ошибка! Город не обнаружен!",
                            Toast.LENGTH_LONG).show());
                } else {
                    handler.post(() -> renderWeather(json));
                }
            }
        }.start();
    }

    private void updateWeatherDataCity() {
        String city = preferencies.getCity();
        MainPageWeather activity = this;
        new Thread() {
            public void run() {
                final JSONObject json = WeatherAPI.getJSONCity(activity, city);
                lastJSON = json;
                if (json == null) {
                    handler.post(() -> Toast.makeText(getApplicationContext(),
                            "Ошибка! Город не обнаружен!",
                            Toast.LENGTH_LONG).show());
                } else {
                    handler.post(() -> renderWeather(json));
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        Log.i("Weather", "Render view");
        try {
            cityField.setText(json.getString("city"));

            JSONObject main = json.getJSONObject("current");
            JSONObject daily = json.getJSONArray("daily").getJSONObject(0);

            String tempDegrees = " ℃";
            if (preferencies.getDegrees().equals("F")) tempDegrees = " °F";

            currentTemperatureField.setText((int) main.getDouble("temp") + tempDegrees);

            int deg = main.getInt("wind_deg") / 45;
            String direction = "";
            switch (deg) {
                case 0:
                    direction = "С";
                    break;
                case 1:
                    direction = "С/В";
                    break;
                case 2:
                    direction = "В";
                    break;
                case 3:
                    direction = "Ю/В";
                    break;
                case 4:
                    direction = "Ю";
                    break;
                case 5:
                    direction = "Ю/З";
                    break;
                case 6:
                    direction = "З";
                    break;
                case 7:
                    direction = "С/З";
                    break;
            }

            String wind_speed = String.valueOf((int) Double.parseDouble(main.getString("wind_speed")));

            String speedDegrees = " м/с, ";
            if (preferencies.getDegrees().equals("F")) speedDegrees = " м/ч, ";
            windField.setText(wind_speed + speedDegrees + direction);

            int pressure = (int)(Double.parseDouble(main.getString("pressure"))*0.7500615758456601);

            pressureField.setText(pressure + " мм рт.ст.");
            humidityField.setText(main.getString("humidity") + "%");

            String pop = String.valueOf((int) Double.parseDouble(daily.getString("pop")) * 100);
            chanceOfRainField.setText(pop + "%");

            DateFormat df = DateFormat.getTimeInstance();
            String updatedOn = df.format(new Date(main.getLong("dt") * 1000));
            updatedField.setText("Обновлено в " + updatedOn);

            JSONObject details = main.getJSONArray("weather").getJSONObject(0);
            detailsField.setText(details.getString("description"));
            setWeatherIcon(details.getString("icon"));

        } catch (Exception e) {
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
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
            }
            editLocation(locationManager.getLastKnownLocation(provider));
        }
    };

    private void updateLocation() {
        Log.i("Weather", "Update Location");
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);

            if (addressList != null) {
                String zip = addressList.get(0).getPostalCode();
                preferencies.setZipcode(zip + "," + addressList.get(0).getCountryCode());
            } else {
                handler.post(() -> Toast.makeText(getApplicationContext(),
                        "Ошибка! Город не обнаружен.",
                        Toast.LENGTH_LONG).show());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editLocation(Location location) {
        Log.i("Weather", "Location edited");
        if (location == null)
            return;
        currentLocation = location;
    }

    public void reloadLocation(View view) {
        Log.i("Weather", "Reload Location");
        if (this.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&  this.checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        Runnable runnable = () -> {
            while (currentLocation == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.post(() -> updateLocation());
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void reloadView(View view) {
        Log.i("Weather", "Reload View");
        updateAccordingToSource();
    }

    public void callDialog() {
        ChooseCityDialog dialog = new ChooseCityDialog();
        dialog.show(getSupportFragmentManager(), "ChooseCity");
    }
}
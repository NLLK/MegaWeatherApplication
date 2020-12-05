package com.nllk.megaweatherapplication.ui.home;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.nllk.megaweatherapplication.Preferencies;
import com.nllk.megaweatherapplication.R;
import com.nllk.megaweatherapplication.WeatherAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment {

    Typeface font;

    TextView cityField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView updatedField;
    TextView windField;
    TextView pressureField;
    TextView humidityField;
    TextView chanceOfRainField;
    Chip chipGPS;
    JSONObject lastJSON;
    ImageView weatherIcon;

    Preferencies preferencies;

    Location currentLocation;

    private LocationManager locationManager;

    Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        cityField = root.findViewById(R.id.city_field);
        cityField.setOnClickListener(view -> callDialog());
        detailsField = root.findViewById(R.id.details_field);
        updatedField = root.findViewById(R.id.updated_field);
        currentTemperatureField = root.findViewById(R.id.current_temperature_field);
        weatherIcon = root.findViewById(R.id.weather_icon);
        chipGPS = root.findViewById(R.id.chip_gps);

        windField = root.findViewById(R.id.wind_field);
        pressureField = root.findViewById(R.id.pressure_field);
        humidityField = root.findViewById(R.id.humidity_field);
        chanceOfRainField = root.findViewById(R.id.chance_of_rain_field);

        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LatoMedium.ttf");

        cityField.setTypeface(font);
        detailsField.setTypeface(font);
        updatedField.setTypeface(font);
        currentTemperatureField.setTypeface(font);
        windField.setTypeface(font);
        pressureField.setTypeface(font);
        humidityField.setTypeface(font);
        chanceOfRainField.setTypeface(font);

        Button reload  = root.findViewById(R.id.btnReloadLocation);
        reload.setOnClickListener(view -> reloadView(null));

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        preferencies = new Preferencies(getActivity());
        reloadLocation(null);
        return root;
    }
    @Override
    public void onResume() {

        super.onResume();
        reloadLocation(null);
        updateWeatherData(preferencies.getZipcode());
/*        if (lastJSON==null) updateWeatherData(preferencies.getZipcode());
        else {
            Date cals = Calendar.getInstance(TimeZone.getDefault()).getTime();
            Long cur = System.currentTimeMillis();
            Long lastUpdate = null;
            try {
                lastUpdate = lastJSON.getJSONObject("current").getLong("dt") * 1000;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (lastUpdate-cur > 5*60*1000) updateWeatherData(preferencies.getZipcode());
            }
        }*/
    }
    @Override
    public void onPause() {
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
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = WeatherAPI.getJSON(getActivity().getApplicationContext(), city);
                lastJSON = json;
                if(json == null){
                    handler.post(() -> Toast.makeText(getActivity().getApplicationContext(),
                            "Ошибка! Город не обнаружен!",
                            Toast.LENGTH_LONG).show());
                } else {
                    handler.post(() -> renderWeather(json));
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("city"));

            JSONObject main = json.getJSONObject("current");
            JSONObject daily = json.getJSONArray("daily").getJSONObject(0);
            currentTemperatureField.setText((int)main.getDouble("temp")+ " ℃");

            int deg = main.getInt("wind_deg")/45;
            String direction="";
            switch (deg)
            {
                case 0: direction = "С"; break;
                case 1: direction = "С/В"; break;
                case 2: direction = "В"; break;
                case 3: direction = "Ю/В"; break;
                case 4: direction = "Ю"; break;
                case 5: direction = "Ю/З"; break;
                case 6: direction = "З"; break;
                case 7: direction = "С/З"; break;
            }

            String wind_speed = String.valueOf((int)Double.parseDouble(main.getString("wind_speed")));

            windField.setText(wind_speed+" м/с, "+direction);

            pressureField.setText(main.getString("pressure")+" мм рт.ст.");
            humidityField.setText(main.getString("humidity")+"%");

            String pop = String.valueOf((int)Double.parseDouble(daily.getString("pop"))*100);
            chanceOfRainField.setText( pop+"%");

            DateFormat df = DateFormat.getTimeInstance();
            String updatedOn = df.format(new Date(main.getLong("dt")*1000));
            updatedField.setText("Обновлено в " + updatedOn);

            JSONObject details = main.getJSONArray("weather").getJSONObject(0);
            detailsField.setText(details.getString("description"));
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
            if (getActivity().getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().getApplicationContext().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);}
            editLocation(locationManager.getLastKnownLocation(provider));
        }
    };

    private void updateLocation()
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        try
        {
            List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);

            if (addressList!=null)
            {
                String zip = addressList.get(0).getPostalCode();
                preferencies.setZipcode(zip+","+addressList.get(0).getCountryCode());
                //updateWeatherData(preferencies.getZipcode());
            }
            else {
                handler.post(() -> Toast.makeText(getActivity().getApplicationContext(),
                        "Ошибка! Город не обнаружен.",
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
        Log.i("Weather", "Reload Location");
        if (getActivity().getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().getApplicationContext().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
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
            handler.post(() -> updateLocation());
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void reloadView(View view)
    {
        Log.i("Weather", "Reload View");
        if (lastJSON==null) updateWeatherData(preferencies.getZipcode());
        else renderWeather(lastJSON);

    }
    public void callDialog()
    {
        ChooseCityDialog dialog = new ChooseCityDialog();
        dialog.show(getFragmentManager(),"ChooseCity");
    }
}
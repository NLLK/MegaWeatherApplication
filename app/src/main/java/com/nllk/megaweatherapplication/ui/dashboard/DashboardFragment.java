package com.nllk.megaweatherapplication.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nllk.megaweatherapplication.GPSLocationListener;
import com.nllk.megaweatherapplication.MainActivity2;
import com.nllk.megaweatherapplication.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    GPSLocationListener gps;
    private LocationManager locationManager;
    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = rootView.findViewById(R.id.text_dashboard);

        Context context = getContext();

        if (context.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        }


/*
        GPSLocationListener gps = new GPSLocationListener();
        gps.SetUpLocationListener(getContext());
        final Context mainContext = getContext();
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        gps.SetUpLocationListener(mainContext);
        Location currentLocation = gps.currentLocation;*/
        textView.setText("aa");
        startActivity(new Intent(getActivity(), MainActivity2.class));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getContext().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        }

    }
}
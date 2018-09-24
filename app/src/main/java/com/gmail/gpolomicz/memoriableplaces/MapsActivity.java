package com.gmail.gpolomicz.memoriableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = "";
                try {
                    List<Address> locationPosition = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


                    if (locationPosition.get(0).getThoroughfare() != null) {
                        address += locationPosition.get(0).getThoroughfare() + " ";
                    }

                    if (locationPosition.get(0).getFeatureName() != null) {
                        address += locationPosition.get(0).getFeatureName() + "\n";
                    }

                    if (locationPosition.get(0).getPostalCode() != null) {
                        address += locationPosition.get(0).getPostalCode() + " ";
                    }

                    if (locationPosition.get(0).getLocale() != null) {
                        address += locationPosition.get(0).getLocality() + " ";
                    }

                    if (address.equals("")) {
                        MainActivity.placesArray.add(getDate());
                    }

                    MainActivity.placesArray.add(address);
                    MainActivity.locationsArray.add(latLng);
                    MainActivity.adapter.notifyDataSetChanged();


                } catch (Exception e) {
                    Log.e(TAG, "onItemClick: ERROR", e);
                }

                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        if (position == 0) {

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerMapAtLocation(location, "Your Location");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            checkPermission();
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locationsArray.get(intent.getIntExtra("position", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locationsArray.get(intent.getIntExtra("position", 0)).longitude);

            centerMapAtLocation(placeLocation, MainActivity.placesArray.get(intent.getIntExtra("position", 0)));
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);

        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
                } else {
                    Toast.makeText(this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
            Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centerMapAtLocation(lastKnowLocation, "Your Location");
        }
    }

    private String getDate() {
        DateFormat dfDate = new SimpleDateFormat("yyyy/MM/dd");
        String date = dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }

    private void centerMapAtLocation(Location location, String title) {

        if (location != null) {
            LatLng place = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12));
        }
    }
}

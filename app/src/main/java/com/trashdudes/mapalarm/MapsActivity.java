package com.trashdudes.mapalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SelectAlarmCallback {

    private final int REQ_PERMISSION = 999;

    private GoogleMap mMap;
    private FloatingActionButton floatButton;
    private List<AlarmModel> alarmModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.loadUI();
        this.loadActions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SelectAlarmsAsyncTask alarmsAsyncTask = new SelectAlarmsAsyncTask(this);
        alarmsAsyncTask.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermission()) {
            loadMapInformation();
        } else {
            askPermission();
        }
    }

    private void loadMapInformation() {

        if (checkPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            if (this.alarmModels != null && this.alarmModels.size() > 0) {
                this.loadAlarmItems();
            }
        }
    }

    private boolean checkPermission() {

        boolean fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        return coarseLocation && fineLocation;
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    loadMapInformation();

                } else {
                    // Permission denied
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void loadActions() {
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AlarmsListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUI() {
        this.floatButton = (FloatingActionButton) findViewById(R.id.floatButton);
    }

    private void loadAlarmItems() {

        for (AlarmModel alarmModel : alarmModels) {
            LatLng location = new LatLng(alarmModel.getLatitude(), alarmModel.getLongitude());

            String title = location.latitude + ", " + location.longitude;

            // Define marker options
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(title);
            CircleOptions circleOptions = new CircleOptions()
                    .center(location)
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius(alarmModel.getRadius());
            if (mMap != null) {

                mMap.addCircle(circleOptions);
                mMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public void didGetItens(List<AlarmModel> alarmModels) {
        this.alarmModels = alarmModels;

        if (mMap != null) {
            mMap.clear();
            if (this.mMap != null) {
                this.loadAlarmItems();
            }
        }
    }
}
package com.trashdudes.mapalarm;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

public class InsertAlarmActivity
        extends AppCompatActivity
        implements
            OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            ResultCallback<Status> {

    private static final String TAG = InsertAlarmActivity.class.getSimpleName();

    private GoogleMap mMap;
    private EditText radiusEditText;
    private EditText notesEditText;
    private Button confirmButton;
    private ProgressBar progressBar;

    private AlarmModel currentAlarm;

    private GoogleApiClient googleApiClient;

    private static final String NOTIFICATION_MSG = "Você chegou na área";
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, MapsActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_alarm);

        setTitle("Adicionar Alarme");

        this.currentAlarm = new AlarmModel();

        this.loadUI();
        this.loadActions();
        this.loadMap();

        this.progressBar.setVisibility(View.GONE);
        this.confirmButton.setVisibility(View.VISIBLE);

        createGoogleApi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void loadActions() {
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertAlarmActivity.this.progressBar.setVisibility(View.VISIBLE);
                InsertAlarmActivity.this.confirmButton.setVisibility(View.GONE);
                saveAlarmGeofence();
            }
        });
    }

    private void loadUI() {
        this.confirmButton = (Button) findViewById(R.id.confirmButton);
        this.progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        this.radiusEditText = (EditText) findViewById(R.id.radiusEditText);
        this.notesEditText= (EditText) findViewById(R.id.notesEditText);
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void createGoogleApi() {

        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermission()) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    currentAlarm.setLatitude(latLng.latitude);
                    currentAlarm.setLongitude(latLng.longitude);
                }
            });
        }
    }

    private boolean checkPermission() {

        boolean fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        return coarseLocation && fineLocation;
    }

    private void saveAlarmGeofence() {

        if (!showErrorMessageIfNeeded()) {

            this.currentAlarm.setNotes(this.notesEditText.getText().toString());

            Double radius = Double.parseDouble(this.radiusEditText.getText().toString());
            this.currentAlarm.setRadius(radius);

            LatLng location = new LatLng(this.currentAlarm.getLatitude(), this.currentAlarm.getLongitude());

            Geofence geofence = createGeofence(location, this.currentAlarm.getRadius().floatValue());
            GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence( geofenceRequest );
        }
    }

    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        String geofenceId = UUID.randomUUID().toString();
        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }

    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission()) {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
        }
        else {
            Log.d(TAG, "Without permission");
        }
    }

    private PendingIntent createGeofencePendingIntent() {

            Intent intent = new Intent(this, GeofenceTrasitionService.class);
            return PendingIntent.getService(this, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);

//        Intent intent = new Intent(th is, GeofenceTrasitionService.class);
//        return PendingIntent.getService(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    private void saveBackend() {

        AlarmManager alarmManager = new AlarmManager(this);
        InsertAlermsAsyncTask insertAlermsAsyncTask = new InsertAlermsAsyncTask(this, this.currentAlarm);
        insertAlermsAsyncTask.execute();
    }

    private boolean showErrorMessageIfNeeded() {

        String message =  "";
        Boolean error = false;

        if (this.notesEditText.getText().toString() == null || this.notesEditText.getText().toString().equals("")) {
            error = true;
            message = "Adicione uma nota";
        }

        if (this.radiusEditText.getText().toString() == null || this.radiusEditText.getText().toString().equals("")) {
            error = true;
            message = "Adicione um raio";
        }

        if (this.currentAlarm.getLatitude() == null) {
            error = true;
            message = "Selecione uma posição no mapa";
        }

        if (error) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        return error;
    }

    public void requestFinished() {
        onBackPressed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            saveBackend();
        } else {
            Toast.makeText(this, "Erro ao inserir na google", Toast.LENGTH_LONG).show();
            this.progressBar.setVisibility(View.GONE);
            this.confirmButton.setVisibility(View.VISIBLE);
        }
    }
}

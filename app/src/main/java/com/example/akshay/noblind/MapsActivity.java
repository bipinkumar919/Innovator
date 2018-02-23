package com.example.akshay.noblind;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private LocationRequest mLocationRequest;

    private LocationCallback mLocationCallback;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";

    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private ChildEventListener mChildEventListener;


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    List<LatLng> mLatLngList = new ArrayList<LatLng>();
    List<ModelMarker> mMarkerList = new ArrayList<ModelMarker>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNotifyDatabaseReference;


    private Location currLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mLatLngList.add(new LatLng(30.75859179605642, 76.77120331674814));

        mLatLngList.add(new LatLng(30.763459337802367, 76.77049823105334));

        mLatLngList.add(new LatLng(30.762445226881272, 76.76981158554553));

        mLatLngList.add(new LatLng(30.75656490156738, 76.7646587267518));

        mLatLngList.add(new LatLng(30.749519562695873, 76.75758138298988));

        mLatLngList.add(new LatLng(30.748796040713152, 76.7565044760704));

        int i=0;
        String[] CnameList = new String[6];
        CnameList[0] = "Prof Ramesh Verma";
        CnameList[1] = "Prof Rahul Vargese";
        CnameList[2] = "Dr Sanjay Bhadada";
        CnameList[3] = "Dr Anu Grover";
        CnameList[4] = "Prof Amrit Pal";
        CnameList[5] = "Prof Sabit Patra";

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        for(LatLng latLng : mLatLngList){

            DatabaseReference userinfo = database.getReference("TeacherMarker").child(CnameList[i]);
            userinfo.push().setValue(new ModelMarker(CnameList[i], "", mLatLngList.get(i).latitude, mLatLngList.get(i).longitude));
            i++;
        }

        mFirebaseDatabase=FirebaseDatabase.getInstance();

        mNotifyDatabaseReference = database.getReference("TeacherMarker").child("Prof Ramesh Verma");


        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    ModelMarker marker = dataSnapshot.getValue(ModelMarker.class);

                    // [START_EXCLUDE]
                    if (marker == null) {
                        // User is null, error out
                        Toast.makeText(getApplicationContext(),
                                "value aa gyi.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Write new post
                        mMarkerList.add(marker);
                        Toast.makeText(getApplicationContext(),
                                "Error: could not fetch user."+  String.valueOf(dataSnapshot.getValue(ModelMarker.class).getLat()),
                                Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    mMap.clear();
                    ModelMarker marker = dataSnapshot.getValue(ModelMarker.class);

                    // [START_EXCLUDE]
                    if (marker == null) {
                        // User is null, error out
                        Toast.makeText(getApplicationContext(),
                                "value aa gyi.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Write new post
                        mMarkerList.add(marker);
                        Toast.makeText(getApplicationContext(),
                                "Error: could not fetch user."+  String.valueOf(dataSnapshot.getValue(ModelMarker.class).getLat()),
                                Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mNotifyDatabaseReference.addChildEventListener(mChildEventListener);




            // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mMap.setOnMarkerClickListener(MapsActivity.this);
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data

                   /* mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),
                                    location.getLongitude()), 17)
                            );
                    */
                    currLocation = location;
                    LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .tilt(90)
                                    .zoom(17)
                                    .target(curr)
                                    .build()));


                    for(ModelMarker m: mMarkerList) {
                        float[] distance = new float[2];

                        Toast.makeText(getApplicationContext(),String.valueOf(m.getLat()), Toast.LENGTH_SHORT).show();
                        if(m.isInRange()) {
                            LatLng l = new LatLng(m.getLat(), m.getLung());
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(l)
                                    .title(m.getCname())
                                    .draggable(true));
                            marker.setTag(m);
                            marker.showInfoWindow();
                            Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude,
                                    location.getLatitude(), location.getLongitude(), distance);
                        }


                        Log.i("message", String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
                        if (distance[0] / 1000 > .05) {
                            //Toast.makeText(getBaseContext(), "Outside" + String.valueOf(distance[0]/1000), Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getBaseContext(), "Inside" + String.valueOf(distance[0]/1000), Toast.LENGTH_LONG).show();
                            //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        }
                    }

                }
            };

        };
/*
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    if(user.getDisplayName() == null) {



                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
*/
    }}
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        getLocationPermission();


        updateLocationUI();

        getDeviceLocation();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();


                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        updateLocationUI();
    }
    private void requestPermissions() {

        Log.i(TAG, "Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }


    private void startLocationUpdates() {
        try {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        float[] dis = new float[2];
        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude,
                currLocation.getLatitude(), currLocation.getLongitude(), dis);
        ModelMarker temp = (ModelMarker) marker.getTag();

        Intent i = new Intent(this, TeacherActivity.class);

        i.putExtra("InRange", temp);

        startActivity(i);

        return false;
    }

}
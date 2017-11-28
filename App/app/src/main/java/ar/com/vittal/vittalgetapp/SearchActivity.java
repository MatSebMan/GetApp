package ar.com.vittal.vittalgetapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class SearchActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection, LocalServiceHandler<GetAppLatLng>{

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted;
    private LatLng mLastKnownLocation;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final int M_MAX_ENTRIES = 20;

    private GoogleMap mMap;
    LocalService mService;
    boolean mBound = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLastKnownLocation = null;

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task != null && task.isSuccessful())
                {
                    mLastKnownLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.searchMap);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Maty \
        mMap.getUiSettings().setMapToolbarEnabled(Boolean.FALSE);

        // Do other setup activities here too, as described elsewhere in this tutorial.

        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        showCurrentPlace();
    }

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            ArrayList<String> ids = new ArrayList<>();
            //ids.add("" + Place.TYPE_ESTABLISHMENT);
            PlaceFilter placeFilter = new PlaceFilter(Boolean.TRUE, ids);
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(placeFilter);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                for (PlaceLikelihood pLikehood : likelyPlaces)
                                {
                                    String markerSnippet = "";
                                    if (pLikehood.getPlace() != null && pLikehood.getPlace().getPlaceTypes().contains(Place.TYPE_ESTABLISHMENT))
                                    {
                                        markerSnippet = markerSnippet + "\n" + pLikehood.getPlace();

                                        // Add a marker for the selected place, with an info window
                                        // showing information about that place.
                                        mMap.addMarker(new MarkerOptions()
                                                .title((String) pLikehood.getPlace().getName())
                                                .position(pLikehood.getPlace().getLatLng())
                                                .snippet(markerSnippet));
                                    }
                                }

                                // Position the map's camera at the location of the marker.
                                if (mLastKnownLocation != null)
                                {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastKnownLocation, DEFAULT_ZOOM));
                                }
                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                            } else {
                                Log.e("showCurrentPlace", "Exception: %s", task.getException());
                            }
                        }
                    });
        }
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
                getLocationPermission();
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
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = Boolean.TRUE;
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((LocalService.LocalBinder) iBinder).getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mBound = false;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mBound)
        {
            unbindService(this);
            mBound = false;
        }
    }

    public void lookupHospitals(Location mLastKnownLocation)
    {
        Comunicator comunicator = new Comunicator(Comunicator.GET, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={location}&radius={radius}&types={types}&sensor={sensor}&key={key}");
        comunicator.addParam("location", String.valueOf(mLastKnownLocation.getLatitude()) + "," + String.valueOf(mLastKnownLocation.getLongitude()));
        comunicator.addParam("radius", "" + 500);
        comunicator.addParam("types", "grocery_or_supermarket");
        comunicator.addParam("sensor", "true");
        mService.sendRequest(this, comunicator);
    }

    @Override
    public void handleResult(GetAppLatLng object, String message) {

    }
}

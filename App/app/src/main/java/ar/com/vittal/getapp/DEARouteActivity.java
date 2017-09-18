package ar.com.vittal.getapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DEARouteActivity extends MapListenerActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-34.6131500, -58.3772300);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static final String NEAREST_DEA_LATITUDE = "NEAREST_DEA_LATITUDE";
    public static final String NEAREST_DEA_LONGITUDE = "NEAREST_DEA_LONGITUDE";

    private com.google.maps.model.LatLng destination;

    private LocationUtilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button button = (Button) findViewById(R.id.listAllNearestDEAS);
        button.setOnClickListener(this);

        Intent intent = getIntent();
        this.destination = new com.google.maps.model.LatLng(intent.getDoubleExtra(NEAREST_DEA_LATITUDE, -34.6131500), intent.getDoubleExtra(NEAREST_DEA_LONGITUDE, -58.3772300));

        utilities = LocationUtilities.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        utilities.prepareMap(mMap);
    }

    @Override
    public void locationReady() {
        DirectionsResult result = utilities.getRouteFromCurrentLocation(this.destination);
        if (result != null)
        {
            utilities.drawResult(result, mMap);
            utilities.centerCamera(mMap);
        }
    }

    public void listAllNearestDEAS(View view) {
        Intent intent = new Intent(this, DEASActivity.class);
        intent.putParcelableArrayListExtra(DEASActivity.LIST_OF_DEAS, getNearestDEAS());
        startActivity(intent);
    }

    private ArrayList<LatLng> getNearestDEAS()
    {
        ArrayList<LatLng> listaDeDeas = new ArrayList<>();
        listaDeDeas.add(new LatLng(-34.576465, -58.455882));
        listaDeDeas.add(new LatLng(-34.578465, -58.462882));
        listaDeDeas.add(new LatLng(-34.578465, -58.452882));
        listaDeDeas.add(new LatLng(-34.583465, -58.457882));
        listaDeDeas.add(new LatLng(-34.573465, -58.457882));
        return listaDeDeas;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listAllNearestDEAS:
                listAllNearestDEAS(v);
                break;
        }
    }
}

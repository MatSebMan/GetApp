package ar.com.vittal.vittalgetapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DirectionsResult;

public class DEARouteActivity extends MapListenerActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-34.6131500, -58.3772300);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MAX_DEAS_CANT = 5;

    public static final String NEAREST_DEA_LATITUDE = "NEAREST_DEA_LATITUDE";
    public static final String NEAREST_DEA_LONGITUDE = "NEAREST_DEA_LONGITUDE";

    private com.google.maps.model.LatLng destination;

    private LocationUtilities utilities;

    private MapListenerActivity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._activity = this;
        setContentView(R.layout.activity_maps);

        Button button = (Button) findViewById(R.id.listAllNearestDEAS);
        button.setOnClickListener(this);

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
        utilities.lookupDEAS(LocationUtilities.DEAS_CANT_MIN);
    }

    @Override
    public void lookupReady(GetAppLatLng[] latLng)
    {
        DirectionsResult result = utilities.getRouteFromCurrentLocation(new com.google.maps.model.LatLng(latLng[0].getLatitud(),latLng[0].getLongitud()));
        if (result != null)
        {
            utilities.drawResult(result, mMap);
            utilities.centerCamera(mMap);
        }
    }

    @Override
    public void sendResponse(final ResponseObject ro) {
        if (ro.getStatus() == ResponseObject.STATUS_ERROR)
        {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(_activity, ro.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
        else
        {
            switch (ro.getMethod())
            {
                case "refreshDeviceLocation":
                    utilities.lookupDEAS(LocationUtilities.DEAS_CANT_MIN);
                    break;
                case "lookupDEAS":
                    GetAppLatLng[] latLng = (GetAppLatLng[])ro.getObject();
                    DirectionsResult result = utilities.getRouteFromCurrentLocation(new com.google.maps.model.LatLng(latLng[0].getLatitud(),latLng[0].getLongitud()));
                    if (result != null)
                    {
                        utilities.drawResult(result, mMap);
                        utilities.centerCamera(mMap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void listAllNearestDEAS(View view) {
        Intent intent = new Intent(this, DEASActivity.class);
        startActivity(intent);
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

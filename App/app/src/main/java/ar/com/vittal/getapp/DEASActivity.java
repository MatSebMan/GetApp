package ar.com.vittal.getapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;

public class DEASActivity extends MapListenerActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> destinations;
    private LocationUtilities utilities;

    public static String LIST_OF_DEAS = "LIST_OF_DEAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        this.destinations = intent.getParcelableArrayListExtra(LIST_OF_DEAS);

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
        ArrayList<DirectionsResult> result = utilities.getRouteFromCurrentLocation(this.destinations);
        if (result != null)
        {
            utilities.drawResult(result, mMap);
            utilities.centerCamera(mMap, this.destinations);
        }
    }
}

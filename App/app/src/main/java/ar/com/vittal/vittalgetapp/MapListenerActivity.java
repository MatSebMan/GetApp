package ar.com.vittal.vittalgetapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by mmanzano on 07/09/2017.
 */

public abstract class MapListenerActivity extends FragmentActivity implements OnMapReadyCallback, LocationReadyListener
{

    protected MapListenerActivity _activity;

    protected GoogleMap mMap;

    protected LocationUtilities utilities;

    protected com.google.maps.model.LatLng direction;

    public static final String LATITUD = "Latitud";
    public static final String LONGITUD = "Longitud";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._activity = this;

        this.direction = new com.google.maps.model.LatLng(getIntent().getDoubleExtra(MapListenerActivity.LATITUD, 0),
                getIntent().getDoubleExtra(MapListenerActivity.LONGITUD, 0));

        specificSettings();

        this.utilities = LocationUtilities.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public abstract void specificSettings();

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
}

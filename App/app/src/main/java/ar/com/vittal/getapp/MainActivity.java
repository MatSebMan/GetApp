package ar.com.vittal.getapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;

    private FragmentManager mgr;

    private Intent intent;

    protected static final int REQUEST_CHECK_SETTINGS = 0x56;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mgr = getFragmentManager();
                    mgr.beginTransaction()
                            .replace(R.id.content, HomeFragment.newInstance("Home", ""))
                            .commit();
                    return true;
                case R.id.navigation_buscador:
                    mgr = getFragmentManager();
                    mgr.beginTransaction()
                            .replace(R.id.content, SearchFragment.newInstance("Search", ""))
                            .commit();
                    return true;
                case R.id.navigation_instrucciones:
                    mgr = getFragmentManager();
                    mgr.beginTransaction()
                            .replace(R.id.content, InformationFragment.newInstance("Information",""))
                            .commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mgr = getFragmentManager();
        mgr.beginTransaction()
                .add(R.id.content, HomeFragment.newInstance("home", ""))
                .commit();
    }

    private ArrayList<LatLng> getNearestDEAS()
    {
        ArrayList<LatLng> listaDeDeas = new ArrayList<>();
        listaDeDeas.add(new LatLng(-34.589690, -58.459044));
        listaDeDeas.add(new LatLng(-34.588690, -58.459044));
        listaDeDeas.add(new LatLng(-34.589690, -58.458044));
        listaDeDeas.add(new LatLng(-34.589690, -58.460044));
        listaDeDeas.add(new LatLng(-34.589190, -58.459544));
        return listaDeDeas;
    }

    @Override
    public void onGoToNearestDEAPressed(View view, Class clas) {
        this.intent = new Intent(this, DEARouteActivity.class);
        ArrayList<LatLng> lista = getNearestDEAS();
        this.intent.putExtra(DEARouteActivity.NEAREST_DEA_LATITUDE, lista.get(0).latitude);
        this.intent.putExtra(DEARouteActivity.NEAREST_DEA_LONGITUDE, lista.get(0).longitude);
        displayLocationSettingsRequest(this);
    }

    /*
    @Override
    public void onListAllNearestDEASPressed(View view, Class clas) {
        this.intent = new Intent(this, DEASActivity.class);
        this.intent.putParcelableArrayListExtra(DEASActivity.LIST_OF_DEAS, getNearestDEAS());
        displayLocationSettingsRequest(this);
    }*/

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        doResult();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("TAG", "User agreed to make required location settings changes.");
                        doResult();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void doResult()
    {
        startActivity(this.intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

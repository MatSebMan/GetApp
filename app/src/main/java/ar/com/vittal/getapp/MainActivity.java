package ar.com.vittal.getapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;

    private FragmentManager mgr;

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
        if(requestGPSActivation())
        {
            Intent intent = new Intent(this, DEARouteActivity.class);
            ArrayList<LatLng> lista = getNearestDEAS();
            intent.putExtra(DEARouteActivity.NEAREST_DEA_LATITUDE, lista.get(0).latitude);
            intent.putExtra(DEARouteActivity.NEAREST_DEA_LONGITUDE, lista.get(0).longitude);
            startActivity(intent);
        }
    }

    @Override
    public void onListAllNearestDEASPressed(View view, Class clas) {
        if(requestGPSActivation())
        {
            Intent intent = new Intent(this, DEASActivity.class);
            intent.putParcelableArrayListExtra(DEASActivity.LIST_OF_DEAS, getNearestDEAS());
            startActivity(intent);
        }
    }

    public Boolean requestGPSActivation()
    {
        int off = 0;
        try
        {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        if(off==0)
        {
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
            return false;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

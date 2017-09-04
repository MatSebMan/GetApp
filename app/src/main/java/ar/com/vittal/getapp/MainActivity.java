package ar.com.vittal.getapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_buscador:
                    mgr = getFragmentManager();
                    mgr.beginTransaction()
                            .replace(R.id.content, SearchFragment.newInstance("Search", ""))
                            .commit();
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_instrucciones:
                    //mTextMessage.setText(R.string.title_notifications);
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

    @Override
    public void onGoToNearestDEAPressed(View view, Class clas) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(HomeFragment.NEAREST_LATLONG, 0);
        startActivity(intent);
    }

    @Override
    public void onListAllNearestDEASPressed(View view, Class clas) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(HomeFragment.ALL_LATLONG, 0);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

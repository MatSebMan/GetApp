package ar.com.vittal.getapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DEASActivity extends MapListenerActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> destinations;
    private LocationUtilities utilities;

    public static String LIST_OF_DEAS = "LIST_OF_DEAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deas);

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
            /*ArrayList<String> addresses = new ArrayList<>();
            for (DirectionsResult r : result)
            {
                addresses.add(utilities.getAddress(r));
            }
            ArrayAdapter<String> at=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,addresses);*/

            ListView list = (ListView) findViewById(R.id.listaDeDeas);
            list.setAdapter(new DEASArrayAdapter(this, R.layout.row_layout, result));
            utilities.drawResult(result, mMap);
            utilities.centerCamera(mMap);
        }
    }

    private class DEASArrayAdapter extends ArrayAdapter<DirectionsResult>
    {

        private final Context context;

        private HashMap<Integer, String> latLongMap = new HashMap<>();
        private HashMap<Integer, DirectionsResult> results = new HashMap<>();

        public DEASArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DirectionsResult> objects) {

            super(context, resource, objects);
            this.context = context;
            for (int i = 0; i < objects.size(); i++)
            {
                this.latLongMap.put(i, utilities.getAddress(objects.get(i)));
                this.results.put(i, objects.get(i));
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            TextView direccion = (TextView) rowView.findViewById(R.id.direccion);
            direccion.setText(latLongMap.get(position));
            TextView distancia = (TextView) rowView.findViewById(R.id.distancia);
            distancia.setText("Tiempo: " + utilities.getTimeToDestination(this.results.get(position)) + ", Distancia: " + utilities.getDistanceToDestination(this.results.get(position)));
            TextView razonSocial = (TextView) rowView.findViewById(R.id.razonSocial);
            razonSocial.setText("Farmacity");
            return rowView;
        }
    }
}

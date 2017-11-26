package ar.com.vittal.vittalgetapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DEASActivity extends MapListenerActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private ArrayList<DEASListObject> lista;

    @Override
    public Integer getQuantityOfDEASToSearch() {
        return DEAS_CANT_MAX;
    }

    @Override
    public void specificSettings() {
        setContentView(R.layout.activity_deas);
    }

    @Override
    public void drawResult() {
        ArrayList<LatLng> destinations = new ArrayList<>();
        for (GetAppLatLng gall : this.currentDEASFound)
        {
            destinations.add(new LatLng(gall.getLatitud(),gall.getLongitud()));
        }
        ArrayList<DirectionsResult> result = getRouteFromCurrentLocation(destinations);
        if (result != null)
        {
            ListView list = findViewById(R.id.listaDeDeas);
            this.lista = new ArrayList<>();
            for (int i = 0; i < result.size(); i++)
            {
                lista.add(new DEASListObject(result.get(i), this.currentDEASFound[i]));
            }
            list.setAdapter(new DEASArrayAdapter(this, R.layout.row_layout, lista));
            list.setOnItemClickListener(this);
            drawResult(result, mMap);
            centerCamera(mMap);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(this.lista != null)
        {

            /*Intent i = new Intent(Intent.ACTION_VIEW,

                    Uri.parse("google.navigation:ll=" + this.lista.get(position).getgALL().getLatitud() + "," + this.lista.get(position).getgALL().getLatitud() + "&mode=w"));

            startActivity(i);*/

            Intent intent = new Intent(this, DEARouteActivity.class);
            intent.putExtra(MapListenerActivity.LATITUD, this.lista.get(position).getgALL().getLatitud());
            intent.putExtra(MapListenerActivity.LONGITUD, this.lista.get(position).getgALL().getLongitud());
            intent.putExtra(MapListenerActivity.NOMBRE, this.lista.get(position).getgALL().getNombre());
            intent.putExtra(MapListenerActivity.DIRECCION, this.lista.get(position).getgALL().getDireccion());
            intent.putExtra(MapListenerActivity.ID, this.lista.get(position).getgALL().getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, DEARouteActivity.class);
        intent.putExtra("Latitud", marker.getPosition().latitude);
        intent.putExtra("Longitud", marker.getPosition().longitude);
        startActivity(intent);
        return false;
    }

    private class DEASArrayAdapter extends ArrayAdapter<DEASListObject>
    {

        private final Context context;

        private SparseArray<String> latLongMap = new SparseArray<>();
        private SparseArray<DEASListObject> results = new SparseArray<>();

        DEASArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DEASListObject> objects) {

            super(context, resource, objects);
            this.context = context;
            for (int i = 0; i < objects.size(); i++)
            {
                this.latLongMap.put(i, objects.get(i).getgALL().getDireccion());
                this.results.put(i, objects.get(i));
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            if (rowView != null)
            {
                TextView direccion = (TextView) rowView.findViewById(R.id.direccion);
                direccion.setText(latLongMap.get(position));
                TextView distancia = (TextView) rowView.findViewById(R.id.distancia);
                distancia.setText("Tiempo: " + getTimeToDestination(this.results.get(position).getDirResult()) + ", Distancia: " + getDistanceToDestination(this.results.get(position).getDirResult()));
                TextView razonSocial = (TextView) rowView.findViewById(R.id.razonSocial);
                razonSocial.setText(this.results.get(position).getgALL().getNombre());
            }
            return rowView;
        }
    }
}

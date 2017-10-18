package ar.com.vittal.vittalgetapp;

import android.content.Context;
import android.content.Intent;
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
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DEASActivity extends MapListenerActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private ArrayList<DEASListObject> lista;

    @Override
    public void specificSettings() {
        setContentView(R.layout.activity_deas);
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
                    utilities.lookupDEAS(LocationUtilities.DEAS_CANT_MAX);
                    break;
                case "lookupDEAS":
                    GetAppLatLng[] latLng = (GetAppLatLng[])ro.getObject();
                    ArrayList<LatLng> destinations = new ArrayList<>();
                    for (GetAppLatLng gall : latLng)
                    {
                        destinations.add(new LatLng(gall.getLatitud(),gall.getLongitud()));
                    }
                    ArrayList<DirectionsResult> result = utilities.getRouteFromCurrentLocation(destinations);
                    if (result != null)
                    {
                        ListView list = (ListView) findViewById(R.id.listaDeDeas);
                        this.lista = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++)
                        {
                            lista.add(new DEASListObject(result.get(i), latLng[i]));
                        }
                        list.setAdapter(new DEASArrayAdapter(this, R.layout.row_layout, lista));
                        list.setOnItemClickListener(this);
                        utilities.drawResult(result, mMap);
                        utilities.centerCamera(mMap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(this.lista != null)
        {
            Intent intent = new Intent(this, DEARouteActivity.class);
            intent.putExtra("Latitud", this.lista.get(position).getgALL().getLatitud());
            intent.putExtra("Longitud", this.lista.get(position).getgALL().getLongitud());
            startActivity(intent);
        }
    }

    private class DEASArrayAdapter extends ArrayAdapter<DEASListObject>
    {

        private final Context context;

        private SparseArray<String> latLongMap = new SparseArray<>();
        private SparseArray<DEASListObject> results = new SparseArray<>();

        public DEASArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DEASListObject> objects) {

            super(context, resource, objects);
            this.context = context;
            for (int i = 0; i < objects.size(); i++)
            {
                this.latLongMap.put(i, utilities.getAddress(objects.get(i).getDirResult()));
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
            distancia.setText("Tiempo: " + utilities.getTimeToDestination(this.results.get(position).getDirResult()) + ", Distancia: " + utilities.getDistanceToDestination(this.results.get(position).getDirResult()));
            TextView razonSocial = (TextView) rowView.findViewById(R.id.razonSocial);
            razonSocial.setText(this.results.get(position).getgALL().getNombre());
            return rowView;
        }
    }
}

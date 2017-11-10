package ar.com.vittal.vittalgetapp;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

public class DEARouteActivity extends MapListenerActivity implements View.OnClickListener{

    private LatLng latLng;

    @Override
    public Integer getQuantityOfDEASToSearch() {
        return DEAS_CANT_MIN;
    }

    @Override
    public void specificSettings() {
        setContentView(R.layout.activity_maps);
        Button button = findViewById(R.id.listAllNearestDEAS);
        button.setOnClickListener(this);
    }

    @Override
    public void drawResult(GetAppLatLng[] latLng) {
        DirectionsResult direction;
        if (this.direction == null || (this.direction.lat == 0 && this.direction.lng == 0))
        {
            this.latLng = new com.google.maps.model.LatLng(latLng[0].getLatitud(),latLng[0].getLongitud());
            direction = getRouteFromCurrentLocation(new com.google.maps.model.LatLng(latLng[0].getLatitud(),latLng[0].getLongitud()));
        }
        else
        {
            this.latLng = new com.google.maps.model.LatLng(this.direction.lat,this.direction.lng);
            direction = getRouteFromCurrentLocation(this.direction);
        }
        if (direction != null)
        {
            drawResult(direction, mMap);
            centerCamera(mMap);
        }
    }

    public void listAllNearestDEAS(View view) {
        Intent intent = new Intent(this, DEASActivity.class);
        startActivity(intent);
    }

    public void navigateToDEA(View view)
    {
        navigate();
    }

    private void navigate()
    {
        Intent i = new Intent(Intent.ACTION_VIEW,

                Uri.parse("google.navigation:ll=" + this.latLng.lat + "," + this.latLng.lng + "&mode=w"));

        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listAllNearestDEAS:
                this.direction = null;
                listAllNearestDEAS(v);
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        navigate();
        return true;
    }
}

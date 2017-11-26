package ar.com.vittal.vittalgetapp;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DirectionsResult;

public class DEARouteActivity extends MapListenerActivity implements View.OnClickListener{

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
    public void drawResult() {
        DirectionsResult direction;
        if (this.DEASelected == null || (this.DEASelected.getLatitud() == 0 && this.DEASelected.getLongitud() == 0))
        {
            direction = getRouteFromCurrentLocation(new com.google.maps.model.LatLng(currentDEASFound[0].getLatitud(),currentDEASFound[0].getLongitud()));
        }
        else
        {
            direction = getRouteFromCurrentLocation(new com.google.maps.model.LatLng(this.DEASelected.getLatitud(),this.DEASelected.getLongitud()));
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
        if (this.DEASelected == null || (this.DEASelected.getLatitud() == 0 && this.DEASelected.getLongitud() == 0))
        {
            mService.setDeaInUse(this.currentDEASFound[0].getId(), this);
            Intent i = new Intent(Intent.ACTION_VIEW,

                    Uri.parse("google.navigation:ll=" + this.currentDEASFound[0].getLatitud() + "," + this.currentDEASFound[0].getLongitud() + "&mode=w"));

            startActivity(i);
        }
        else
        {
            mService.setDeaInUse(this.DEASelected.getId(), this);
            Intent i = new Intent(Intent.ACTION_VIEW,

                    Uri.parse("google.navigation:ll=" + this.DEASelected.getLatitud() + "," + this.DEASelected.getLongitud() + "&mode=w"));

            startActivity(i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listAllNearestDEAS:
                this.currentDEASFound = null;
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

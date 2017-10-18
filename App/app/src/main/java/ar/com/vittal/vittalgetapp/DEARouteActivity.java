package ar.com.vittal.vittalgetapp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.maps.model.DirectionsResult;

public class DEARouteActivity extends MapListenerActivity implements View.OnClickListener{

    @Override
    public void specificSettings() {
        setContentView(R.layout.activity_maps);
        Button button = (Button) findViewById(R.id.listAllNearestDEAS);
        button.setOnClickListener(this);
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
                    if (this.direction == null || (this.direction.lat == 0 && this.direction.lng == 0))
                    {
                        utilities.lookupDEAS(LocationUtilities.DEAS_CANT_MIN);
                        break;
                    }
                case "lookupDEAS":
                    DirectionsResult direction;
                    if (this.direction == null || (this.direction.lat == 0 && this.direction.lng == 0))
                    {
                        GetAppLatLng[] latLng = (GetAppLatLng[])ro.getObject();
                        direction = utilities.getRouteFromCurrentLocation(new com.google.maps.model.LatLng(latLng[0].getLatitud(),latLng[0].getLongitud()));
                    }
                    else
                    {
                        direction = utilities.getRouteFromCurrentLocation(this.direction);
                    }
                    if (direction != null)
                    {
                        utilities.drawResult(direction, mMap);
                        utilities.centerCamera(mMap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void listAllNearestDEAS(View view) {
        Intent intent = new Intent(this, DEASActivity.class);
        startActivity(intent);
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
}

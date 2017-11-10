package ar.com.vittal.vittalgetapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class LocalService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public static final String TAG = "LocalService";
    private static final float LOCATION_TRACKING_MIN_DISTANCE = 15f;
    private static final float ACCEPTED_ACCURACY = 50f;
    private static final long AGENT_LOCATION_TRACKING_UPDATE_INTERVAL = 60;
    private static final long EXPIRATION_DURATION = 60;

    public LocalService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class LocalBinder extends Binder {
        LocalService getService()
        {
            return LocalService.this;
        }
    }

    public void prepare(Context context)
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
    }

    public void getLastKnownLocation(OnCompleteListener<Location> listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(listener);
    }

    public void startPeriodicLocationUpdate(LocationCallback callBack)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.i(TAG, "periodic update");
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, callBack, Looper.myLooper());
    }

    public void stopPeriodicLocationUpdate(LocationCallback callBack) {
        mFusedLocationClient.removeLocationUpdates(callBack)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "removed location updates!");
                    }
                });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(600000)
                .setFastestInterval(60)
                .setExpirationDuration(600000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}

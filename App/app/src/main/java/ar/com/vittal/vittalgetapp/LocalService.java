package ar.com.vittal.vittalgetapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class LocalService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public static final String TAG = "LocalService";
    private static final float LOCATION_TRACKING_MIN_DISTANCE = 15f;
    private static final float ACCEPTED_ACCURACY = 50f;
    private static final long AGENT_LOCATION_TRACKING_UPDATE_INTERVAL = 60;
    private static final long EXPIRATION_DURATION = 60;
    private static final String ATRIBUTO_CANTIDAD = "cantidad";
    private static final String ATRIBUTO_LATITUD = "latitud";
    private static final String ATRIBUTO_LONGITUD = "longitud";
    private static final String ATRIBUTO_ID = "id";
    private static final String URL = "URL";
    private static final String TYPE = "tipo";
    private static final String GET = "get";
    private static final String POST = "post";
    private static final String DELETE = "delete";

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

    public void lookupDEAS(Integer ammount, Location mLastKnownLocation, MapListenerActivity mLA)
    {
        Comunicator comunicator = new Comunicator(Comunicator.GET, getString(R.string.webAPI_address) + getString(R.string.lookup_service));
        comunicator.addParam(ATRIBUTO_CANTIDAD, ammount.toString());
        comunicator.addParam(ATRIBUTO_LATITUD, String.valueOf(mLastKnownLocation.getLatitude()));
        comunicator.addParam(ATRIBUTO_LONGITUD, String.valueOf(mLastKnownLocation.getLongitude()));
        new HttpRequestTask(mLA).execute(comunicator);
    }

    public void setDeaInUse(Integer idDEA, MapListenerActivity mLA)
    {
        Comunicator comunicator = new Comunicator(Comunicator.PUT, getString(R.string.webAPI_address) + getString(R.string.use_service));
        comunicator.addParam(ATRIBUTO_ID, "" + idDEA);
        comunicator.setObject(new InUseDEA(true));
        new HttpRequestTask(mLA).execute(comunicator);
    }

    private class HttpRequestTask extends AsyncTask<Comunicator, Void, GetAppLatLng[]> {

        private MapListenerActivity _activity;
        private Comunicator comunicator;

        public HttpRequestTask(MapListenerActivity mLA)
        {
            this._activity = mLA;
        }

        @Override
        protected GetAppLatLng[] doInBackground(Comunicator... params) {
            try {
                comunicator = params[0];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                GetAppLatLng[] response = null;


                if (comunicator.getInteractionType() == Comunicator.GET)
                {
                    response = restTemplate.getForObject(comunicator.getUrl(),
                            GetAppLatLng[].class,
                            comunicator.getParameters());
                }
                else if (comunicator.getInteractionType() == Comunicator.POST)
                {
                    response = restTemplate.postForObject(comunicator.getUrl(), comunicator.getObject(), GetAppLatLng[].class, comunicator.getParameters());
                }
                else if (comunicator.getInteractionType() == Comunicator.PUT)
                {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("id", "16");
                    InUseDEA inUseDEA = new InUseDEA(Boolean.TRUE);
                    HttpEntity<InUseDEA> requestEntity = new HttpEntity<>(inUseDEA);
                    HttpEntity<String> zaraza = restTemplate.exchange(comunicator.getUrl(), HttpMethod.PUT, requestEntity, String.class, comunicator.getParameters());
                    String code = zaraza.getBody();
                    if(code != null)
                    {

                    }
                    //restTemplate.put(comunicator.getUrl(), comunicator.getObject(), comunicator.getParameters());
                }

                return response;

            } catch (ResourceAccessException rae) {
                _activity.handleResult(null, "Servidor inaccesible");
            } catch (Exception e)
            {
                _activity.handleResult(null, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(GetAppLatLng[] latLng)
        {
            if(comunicator.getInteractionType() == Comunicator.GET)
            {
                if (latLng == null || latLng.length == 0)
                {
                    _activity.handleResult(null, "No se encontraron DEAS cerca");
                    //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", null, "No se encontraron DEAS cerca"));
                }
                else
                {
                    _activity.handleResult(latLng, "Ok");
                    //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "lookupDEAS", latLng));
                    //_activity.lookupReady(latLng);
                }
            }
        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(600000)
                .setFastestInterval(60)
                .setExpirationDuration(600000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}

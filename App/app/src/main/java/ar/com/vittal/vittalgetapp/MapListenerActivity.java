package ar.com.vittal.vittalgetapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mmanzano on 07/09/2017.
 */

public abstract class MapListenerActivity extends FragmentActivity implements OnMapReadyCallback, OnCompleteListener<Location>, ServiceConnection, GoogleMap.OnMarkerClickListener
{

    protected GoogleMap mMap;

    protected com.google.maps.model.LatLng direction;

    public static final String LATITUD = "Latitud";
    public static final String LONGITUD = "Longitud";


    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String GOOGLE_DIRECTIONS_KEY = "AIzaSyDi4whs1INWEBhFQ4bdB7ptIT1K7GrViRg";
    private static final Integer TIMEOUT_TIME = 10;
    private static final String ATRIBUTO_CANTIDAD = "cantidad";
    private static final String ATRIBUTO_LATITUD = "latitud";
    private static final String ATRIBUTO_LONGITUD = "longitud";

    public static final int DEAS_CANT_MAX = 5;
    public static final int DEAS_CANT_MIN = 1;

    private static final float LOCATION_TRACKING_MIN_DISTANCE = 15f;

    LocalService mService;
    boolean mBound = false;

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "mensaje", "onLocationResult"));
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                Toast.makeText(getApplicationContext(), "Cambio de ubicacion", Toast.LENGTH_SHORT).show();
                if (mLastKnownLocation == null || (location != null && location.distanceTo(mLastKnownLocation) >= LOCATION_TRACKING_MIN_DISTANCE)) {
                    mLastKnownLocation = location;
                    lookupDEAS(getQuantityOfDEASToSearch());
                }
            }
        };
    }
    
    public abstract Integer getQuantityOfDEASToSearch();
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((LocalService.LocalBinder) service).getService();
        mBound = true;
        mService.prepare(this);
        //mService.getLastKnownLocation(this);
        mService.startPeriodicLocationUpdate(mLocationCallback);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(MapListenerActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
        mBound = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.direction = new com.google.maps.model.LatLng(getIntent().getDoubleExtra(MapListenerActivity.LATITUD, 0),
                getIntent().getDoubleExtra(MapListenerActivity.LONGITUD, 0));

        specificSettings();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationCallback();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mBound)
        {
            mService.stopPeriodicLocationUpdate(mLocationCallback);
            unbindService(this);
            mBound = false;
        }
    }

    public abstract void specificSettings();

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(GOOGLE_DIRECTIONS_KEY)
                .setConnectTimeout(TIMEOUT_TIME, TimeUnit.SECONDS)
                .setReadTimeout(TIMEOUT_TIME, TimeUnit.SECONDS)
                .setWriteTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
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

        // Maty \
        mMap.getUiSettings().setMapToolbarEnabled(Boolean.FALSE);
        // Maty / - Requerimiento: Sacar los botones de mapa y navegacion de google.

        // Construct a FusedLocationProviderClient.
        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mMap.setOnMarkerClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI(mMap);

        // Get the current location of the device and set the position of the map.
        //refreshDeviceLocation();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = Boolean.TRUE;
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void refreshDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, this);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /*Centra la cámara en el punto medio entre mi ubicación y la posicion media de todos los DEAs
    public void centerCamera(GoogleMap mMap, ArrayList<LatLng> destination)
    {
        double latitude = 0.0;
        double longitude = 0.0;
        for (LatLng ll : destination)
        {
            latitude += ll.latitude;
            longitude += ll.longitude;
        }
        latitude = latitude / destination.size();
        longitude = longitude / destination.size();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                // new LatLng((latitude + mLastKnownLocation.getLatitude())/2, (longitude + mLastKnownLocation.getLongitude())/2)
                new LatLng(latitude, longitude), DEFAULT_ZOOM));
    }

    public void centerCamera(GoogleMap mMap, com.google.maps.model.LatLng destination)
    {
        ArrayList<LatLng> lista = new ArrayList<>();
        lista.add(new LatLng(destination.lat, destination.lng));
        centerCamera(mMap, lista);
    }
    */

    // Centra la cámara a mi ubicación
    public void centerCamera(GoogleMap mMap)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    public DirectionsResult getRouteFromCurrentLocation(com.google.maps.model.LatLng destination)
    {
        DirectionsResult result = null;
        try {
            if (mLastKnownLocation != null)
            {
                DirectionsApiRequest request = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.WALKING)
                        .origin(new com.google.maps.model.LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                        .destination(destination)
                        .departureTime(new DateTime());
                result = request.await();
            }
        } catch (ApiException e) {
            handleResult(null, e.getMessage());
            //this.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "getRouteFromCurrentLocation", e.getMessage()));
        } catch (InterruptedException e) {
            handleResult(null, e.getMessage());
            //this.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "getRouteFromCurrentLocation", e.getMessage()));
        } catch (IOException e) {
            handleResult(null, e.getMessage());
            //this.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "getRouteFromCurrentLocation", e.getMessage()));
        }
        return result;
    }

    public ArrayList<DirectionsResult> getRouteFromCurrentLocation(ArrayList<LatLng> destinations)
    {
        ArrayList<DirectionsResult> returnList = new ArrayList<>();

        for(LatLng ll : destinations)
        {
            returnList.add(getRouteFromCurrentLocation(new com.google.maps.model.LatLng(ll.latitude, ll.longitude)));
        }

        return returnList;
    }

    private void updateLocationUI(GoogleMap mMap) {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public String getAddress(DirectionsResult results)
    {
        // Maty \
        StringBuilder address = new StringBuilder();
        for (String s : results.routes[0].legs[0].endAddress.split(","))
        {
            if (s.contains("CABA"))
            {
                address.append("CABA, ");
            }
            else
            {
                address.append(s).append(", ");
            }
        }
        address = new StringBuilder(address.substring(0, address.lastIndexOf(", ")));
        // Maty / - Fix para sacarle el código postal a la dirección

        return address.toString();
    }

    public String getTimeToDestination(DirectionsResult result)
    {
        return result.routes[0].legs[0].duration.humanReadable;
    }

    public String getDistanceToDestination(DirectionsResult result)
    {
        return result.routes[0].legs[0].distance.humanReadable;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addMarkersToMap(DirectionsResult result, GoogleMap mMap) {
        if (result.routes.length != 0)
        {
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_marker))
                    .position(new LatLng(result.routes[0].legs[0].endLocation.lat,result.routes[0].legs[0].endLocation.lng))
                    .title(getAddress(result))
                    .snippet(getEndLocationTitle(result))).showInfoWindow();
        }
    }

    private String getEndLocationTitle(DirectionsResult result){
        String cadena = "";
        if (result.routes.length != 0)
        {
            cadena = "Tiempo: "+ getTimeToDestination(result) + ", Distancia: " + getDistanceToDestination(result);
        }
        return cadena;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        if (results.routes.length != 0)
        {
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10.0f));
        }
    }

    public void drawResult(ArrayList<DirectionsResult> result, GoogleMap mMap)
    {
        if (mMap != null) {
            mMap.clear();
            for (DirectionsResult dr : result) {
                if (dr.routes.length != 0) {
                    addMarkersToMap(dr, mMap);
                    if (result.size() == 1) {
                        addPolyline(dr, mMap);
                    }
                }
            }
        }
    }

    public void drawResult(DirectionsResult result, GoogleMap mMap)
    {
        ArrayList<DirectionsResult> list = new ArrayList<>();
        list.add(result);
        drawResult(list, mMap);
    }

    public void lookupDEAS(Integer ammount)
    {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put(ATRIBUTO_CANTIDAD, ammount.toString());
        mapa.put(ATRIBUTO_LATITUD, String.valueOf(mLastKnownLocation.getLatitude()));
        mapa.put(ATRIBUTO_LONGITUD, String.valueOf(mLastKnownLocation.getLongitude()));
        new HttpRequestTask(this).execute(mapa);
    }

    private class HttpRequestTask extends AsyncTask<HashMap<String, String>, Void, GetAppLatLng[]> {

        private MapListenerActivity _activity;

        public HttpRequestTask(MapListenerActivity mLA)
        {
            this._activity = mLA;
        }

        @Override
        protected GetAppLatLng[] doInBackground(HashMap<String, String>... params) {
            try {
                final String url = _activity.getString(R.string.webAPI_address);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                GetAppLatLng getAppLatLng[] = restTemplate.getForObject(url, GetAppLatLng[].class, params[0]);
                return getAppLatLng;
            } catch (ResourceAccessException rae) {
                handleResult(null, "Servidor inaccesible");
                //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", "Servidor inaccesible"));
                //Log.e("LocationUtilities", rae.getMessage(), rae);
            } catch (Exception e)
            {
                handleResult(null, e.getMessage());
                //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", e.getMessage()));
                //Log.e("LocationUtilities", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(GetAppLatLng[] latLng)
        {
            if (latLng == null || latLng.length == 0)
            {
                handleResult(null, "No se encontraron DEAS cerca");
                //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", null, "No se encontraron DEAS cerca"));
            }
            else
            {
                handleResult(latLng, "Ok");
                //_activity.sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "lookupDEAS", latLng));
                //_activity.lookupReady(latLng);
            }
        }

    }

    public void handleResult(GetAppLatLng[] latLng, String mensaje)
    {
        if (latLng != null)
        {
            drawResult(latLng);
        }
        else
        {
            //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public abstract void drawResult(GetAppLatLng[] latLng);

    @Override
    public void onComplete(@NonNull Task<Location> task) {
        /*if (task.isSuccessful() && task.getResult() != null) {
            // Set the map's camera position to the current location of the device.
            mLastKnownLocation = task.getResult();
            //this.sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "refreshDeviceLocation", mLastKnownLocation));
        } else {
            mLastKnownLocation = null;
            //this.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "refreshDeviceLocation", "Error al obtener su ubicación"));
        }*/
        //_activity.locationReady();
    }
}

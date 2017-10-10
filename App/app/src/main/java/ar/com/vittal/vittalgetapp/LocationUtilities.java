package ar.com.vittal.vittalgetapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mmanzano on 06/09/2017.
 */

public final class LocationUtilities {

    private static LocationUtilities _instance;
    private static MapListenerActivity _activity;

    private Boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String GOOGLE_DIRECTIONS_KEY = "AIzaSyDi4whs1INWEBhFQ4bdB7ptIT1K7GrViRg";
    private static final Integer TIMEOUT_TIME = 10;

    public static final int DEAS_CANT_MAX = 5;
    public static final int DEAS_CANT_MIN = 1;

    private LocationUtilities() {}

    public static final LocationUtilities getInstance(MapListenerActivity activity)
    {
        if (_instance == null || _activity == null || !_activity.equals(activity))
        {
            _instance = new LocationUtilities();
            _activity = activity;
        }
        return _instance;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(GOOGLE_DIRECTIONS_KEY)
                .setConnectTimeout(TIMEOUT_TIME, TimeUnit.SECONDS)
                .setReadTimeout(TIMEOUT_TIME, TimeUnit.SECONDS)
                .setWriteTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
    }

    public void prepareMap(GoogleMap mMap)
    {
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(_activity);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI(mMap);

        // Get the current location of the device and set the position of the map.
        refreshDeviceLocation();
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
        if (ContextCompat.checkSelfPermission(_activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = Boolean.TRUE;
        }
        else
        {
            ActivityCompat.requestPermissions(_activity,
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
                locationResult.addOnCompleteListener(_activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                        } else {
                            mLastKnownLocation = null;
                        }
                        _activity.sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "refreshDeviceLocation", mLastKnownLocation));
                        //_activity.locationReady();
                    }
                });
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
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public String getAddress(DirectionsResult results)
    {
        // Maty \
        String address = "";
        for (String s : results.routes[0].legs[0].endAddress.split(","))
        {
            if (s.contains("CABA"))
            {
                address += "CABA, ";
            }
            else
            {
                address += s + ", ";
            }
        }
        address = address.substring(0, address.lastIndexOf(", "));
        // Maty / - Fix para sacarle el código postal a la dirección

        return address;
    }

    public String getTimeToDestination(DirectionsResult result)
    {
        return result.routes[0].legs[0].duration.humanReadable;
    }

    public String getDistanceToDestination(DirectionsResult result)
    {
        return result.routes[0].legs[0].distance.humanReadable;
    }

    private void addMarkersToMap(DirectionsResult result, GoogleMap mMap) {
        if (result.routes.length != 0)
        {
            mMap.addMarker(new MarkerOptions()
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
        for (DirectionsResult dr : result)
        {
            if (dr.routes.length != 0)
            {
                addMarkersToMap(dr, mMap);
                if (result.size() == 1)
                {
                    addPolyline(dr, mMap);
                }
            }
        }
    }

    public void drawResult(DirectionsResult result, GoogleMap mMap)
    {
        ArrayList<DirectionsResult> list = new ArrayList<DirectionsResult>();
        list.add(result);
        drawResult(list, mMap);
    }

    public void lookupDEAS(Integer ammount)
    {
        new HttpRequestTask().execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, GetAppLatLng[]> {
        @Override
        protected GetAppLatLng[] doInBackground(Void... params) {
            try {
                final String url = "http://190.245.173.230:4000/api/v1/deaLocation";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                GetAppLatLng getAppLatLng[] = restTemplate.getForObject(url, GetAppLatLng[].class);
                return getAppLatLng;
            } catch (ResourceAccessException rae) {
                _activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", "Servidor inaccesible"));
                //Log.e("LocationUtilities", rae.getMessage(), rae);
            } catch (Exception e)
            {
                _activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "Error inesperado"));
                //Log.e("LocationUtilities", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(GetAppLatLng[] latLng)
        {
            if (latLng == null || latLng.length == 0)
            {
                _activity.sendResponse(new ResponseObject(ResponseObject.STATUS_ERROR, "lookupDEAS", null, "No se encontraron DEAS cerca"));
            }
            else
            {
                _activity.sendResponse(new ResponseObject(ResponseObject.STATUS_OK, "lookupDEAS", latLng, "No se encontraron DEAS cerca"));
                //_activity.lookupReady(latLng);
            }
        }

    }
}

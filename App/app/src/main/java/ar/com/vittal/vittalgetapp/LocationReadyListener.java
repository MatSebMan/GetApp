package ar.com.vittal.vittalgetapp;

/**
 * Created by mmanzano on 07/09/2017.
 */

public interface LocationReadyListener
{
    public void locationReady();

    //public void lookupReady(GetAppLatLng[] latng);

    public void sendResponse(final ResponseObject ro);
}

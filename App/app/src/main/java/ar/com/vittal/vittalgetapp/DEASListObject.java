package ar.com.vittal.vittalgetapp;

import com.google.maps.model.DirectionsResult;

import java.io.Serializable;

/**
 * Created by mmanzano on 13/10/2017.
 */

public class DEASListObject {

    private DirectionsResult dirResult;
    private GetAppLatLng gALL;

    public DEASListObject(DirectionsResult dirResult, GetAppLatLng gALL)
    {
        this.dirResult = dirResult;
        this.gALL = gALL;
    }

    public DirectionsResult getDirResult() {
        return dirResult;
    }

    public GetAppLatLng getgALL() {
        return gALL;
    }
}

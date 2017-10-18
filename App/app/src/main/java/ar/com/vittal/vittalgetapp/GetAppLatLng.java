package ar.com.vittal.vittalgetapp;

import java.io.Serializable;

/**
 * Created by mmanzano on 02/10/2017.
 */

public class GetAppLatLng {

    private String id;
    private String nombre;
    private Double latitud;
    private Double longitud;

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }
}

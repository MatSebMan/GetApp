package ar.com.vittal.vittalgetapp;

import java.io.Serializable;

/**
 * Created by mmanzano on 02/10/2017.
 */

public class GetAppLatLng {

    private Integer id;
    private String nombre;
    private Double latitud;
    private Double longitud;

    public Integer getId() {
        return id;
    }

    String getNombre() {
        return nombre;
    }

    Double getLatitud() {
        return latitud;
    }

    Double getLongitud() {
        return longitud;
    }
}

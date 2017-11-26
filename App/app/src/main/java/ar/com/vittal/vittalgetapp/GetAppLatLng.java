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
    private String direccion;

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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

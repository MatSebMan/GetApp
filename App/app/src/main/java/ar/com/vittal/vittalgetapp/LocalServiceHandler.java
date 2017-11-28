package ar.com.vittal.vittalgetapp;

/**
 * Created by mmanzano on 27/11/2017.
 */

public interface LocalServiceHandler<T> {
    void handleResult(T object, String message);
}

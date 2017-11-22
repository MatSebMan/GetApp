package ar.com.vittal.vittalgetapp;

import java.util.HashMap;

/**
 * Created by mmanzano on 14/11/2017.
 */

public class Comunicator {

    public static final Integer GET = 0;
    public static final Integer POST = 1;
    public static final Integer DELETE = 2;
    public static final Integer PUT = 3;

    private Integer interactionType;
    private String url;
    private Class responseClass;
    private HashMap<String, String> parameters;
    private Object object;

    public Comunicator(Integer interactionType, String url)
    {
        this.interactionType = interactionType;
        this.url = url;
        this.parameters = new HashMap<>();
    }

    public Integer getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(Integer interactionType) {
        this.interactionType = interactionType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class responseClass) {
        this.responseClass = responseClass;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void addParam(String name, String value)
    {
        this.parameters.put(name, value);
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

package ar.com.vittal.vittalgetapp;

/**
 * Created by mmanzano on 03/10/2017.
 */

public class ResponseObject {

    public static Short STATUS_OK = 1;
    public static Short STATUS_ERROR = 0;
    private static String NO_MESSAGE = "NO_MESSAGE";

    private String message;
    private String method;
    private Short status;
    private Object object;

    public ResponseObject (Short status, String method, Object object, String message)
    {
        this.status = status;
        this.method = method;
        this.object = object;
        this.message = message;
    }

    public ResponseObject (Short status, String method, Object object)
    {
        this(status, method, object, NO_MESSAGE);
    }

    public ResponseObject (Short status, String method)
    {
        this(status, method, null, NO_MESSAGE);
    }

    public ResponseObject (Short status, String method, String message)
    {
        this(status, method, null, message);
    }

    public String getMessage() {
        return message;
    }

    public String getMethod() {
        return method;
    }

    public Short getStatus() {
        return status;
    }

    public Object getObject() {
        return object;
    }
}

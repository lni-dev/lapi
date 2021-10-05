package me.linusdev.discordbotapi.api.communication.lapihttprequest;

public enum Method {
    GET("GET"),
    POST("POST"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    PUT("PUT");

    private final String method;

    Method(String method){
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

package me.linusdev.discordbotapi.api.communication.lapihttprequest;

/**
 * Http request methods
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods" target="_top">HTTP request methods</a>
 */
public enum Method {

    //methods used by Discord

    /**
     * The GET method requests an object from Discord. This method as no {@link me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody request body}
     */
    GET("GET"),

    /**
     * The POST method submits a new object to Discord
     */
    POST("POST"),

    /**
     * The PATCH method partially modifies on an object on Discord
     */
    PATCH("PATCH"),

    /**
     * The DELETE method deletes something from Discord
     */
    DELETE("DELETE"),

    /**
     * The PUT method adds an object to a collection on Discord
     */
    PUT("PUT"),

    //other methods
    HEAD("HEAD"),
    CONNECT("CONNECT"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),

    ;

    private final String method;

    Method(String method){
        this.method = method;
    }

    /**
     * @return the method formatted for the HttpRequest
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @return the method name
     */
    @Override
    public String toString() {
        return method;
    }
}

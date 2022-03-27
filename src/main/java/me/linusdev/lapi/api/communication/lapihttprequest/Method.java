/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.lapihttprequest;

/**
 * Http request methods
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods" target="_top">HTTP request methods</a>
 */
public enum Method {

    //methods used by Discord

    /**
     * The GET method requests an object from Discord. This method as no {@link me.linusdev.lapi.api.communication.lapihttprequest.body.LApiHttpBody request body}
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

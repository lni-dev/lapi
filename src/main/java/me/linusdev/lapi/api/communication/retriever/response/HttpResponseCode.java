/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.communication.retriever.response;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/opcodes-and-status-codes#http-http-response-codes" target="_top">HTTP Response Codes</a>
 */
public enum HttpResponseCode implements SimpleDatable {

    /**
     * Unknown Response Code
     */
    UNKNOWN(-1),

    /**
     * The request completed successfully.
     */
    OK(200),

    /**
     * The entity was created successfully.
     */
    CREATED(201),

    /**
     * The request completed successfully but returned no content.
     */
    NO_CONTENT(204),

    /**
     * The entity was not modified (no action was taken).
     */
    NOT_MODIFIED(304),

    /**
     * The request was improperly formatted, or the server couldn't understand it.
     */
    BAD_REQUEST(400),

    /**
     * The Authorization header was missing or invalid.
     */
    UNAUTHORIZED(401),

    /**
     * The Authorization token you passed did not have permission to the resource.
     */
    FORBIDDEN(403),

    /**
     * The resource at the location specified doesn't exist.
     */
    NOT_FOUND(404),

    /**
     * The HTTP method used is not valid for the location specified.
     */
    METHOD_NOT_ALLOWED(405),

    /**
     * You are being rate limited, see
     * <a href="https://discord.com/developers/docs/topics/rate-limits" target="_top">Rate Limits</a>.
     */
    TOO_MANY_REQUESTS(429),

    /**
     * There was not a gateway available to process your request. Wait a bit and retry.
     */
    GATEWAY_UNAVAILABLE(502),

    /**
     * The server had an error processing your request (these are rare).
     */
    SERVER_ERROR(500, 599),
    ;

    private final int code;

    /**
     * -1 if no max code is set
     */
    private final int maxCode;

    HttpResponseCode(int code, int maxCode) {
        this.code = code;
        this.maxCode = maxCode;
    }

    HttpResponseCode(int code) {
        this(code, -1);
    }

    public static @NotNull HttpResponseCode fromValue(int value){
        for(HttpResponseCode code : values()){
            if(code.matches(value)) return code;
        }

        return UNKNOWN;
    }

    public boolean matches(int code){
        if(code == this.code) return true;
        if(maxCode != -1) return code >= this.code && code <= this.maxCode;
        return false;
    }


    @Override
    public Object simplify() {
        return code;
    }
}

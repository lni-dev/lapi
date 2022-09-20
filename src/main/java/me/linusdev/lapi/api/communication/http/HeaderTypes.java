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

package me.linusdev.lapi.api.communication.http;

import org.jetbrains.annotations.NotNull;

public enum HeaderTypes implements HeaderType{

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                     Standard Http Header                      *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    CONTENT_TYPE("Content-Type"),

    RETRY_AFTER("Retry-After"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                      Discord Rate Limit                       *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * The number of requests that can be made
     */
    X_RATE_LIMIT_LIMIT("X-RateLimit-Limit"),

    /**
     * The number of remaining requests that can be made
     */
    X_RATE_LIMIT_REMAINING("X-RateLimit-Remaining"),

    /**
     * Epoch time (seconds since 00:00:00 UTC on January 1, 1970) at which the rate limit resets
     */
    X_RATE_LIMIT_RESET("X-RateLimit-Reset"),

    /**
     * Total time (in seconds) of when the current rate limit bucket will reset. Can have decimals to match previous
     * millisecond rate-limit precision
     */
    X_RATE_LIMIT_RESET_AFTER("X-RateLimit-Reset-After"),

    /**
     * A unique string denoting the rate limit being encountered (non-inclusive of top-level resources in the path)
     */
    X_RATE_LIMIT_BUCKET("X-RateLimit-Bucket"),

    /**
     * Returned only on HTTP 429 responses if the rate limit encountered is the global rate limit (not per-route)
     */
    X_RATE_LIMIT_GLOBAL("X-RateLimit-Global"),

    /**
     * Returned only on HTTP 429 responses. Value can be user (per bot or user limit),
     * global (per bot or user global limit), or shared (per resource limit)
     */
    X_RATE_LIMIT_SCOPE("X-RateLimit-Scope"),

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                               *
     *                                                               *
     *                             Other                             *
     *                                                               *
     *                                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    X_AUDIT_LOG_REASON("X-Audit-Log-Reason");

    ;

    private final @NotNull String name;

    HeaderTypes(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}

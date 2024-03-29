/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.request.Method;
import me.linusdev.lapi.api.communication.http.request.body.LApiHttpBody;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.interfaces.HasLApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to make a {@link LApiHttpRequest} of a {@link Link}
 */
public class LinkQuery implements Query, HasLApi {

    private final @NotNull LApi lApi;
    private final @NotNull AbstractLink link;
    private final @Nullable LApiHttpBody body;
    private final @Nullable SOData queryStringsData;
    private final @NotNull PlaceHolder[] placeHolders;
    private volatile @Nullable String constructed;

    public LinkQuery(@NotNull LApi lApi, @NotNull AbstractLink link, @Nullable LApiHttpBody body, @Nullable SOData queryStringsData, @NotNull PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.body = body;
        this.queryStringsData = queryStringsData;
        this.placeHolders = placeHolders;
    }

    public LinkQuery(@NotNull LApi lApi, @NotNull AbstractLink link, @Nullable SOData queryStringsData, @NotNull PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.body = null;
        this.queryStringsData = queryStringsData;
        this.placeHolders = placeHolders;
    }

    public LinkQuery(@NotNull LApi lApi, @NotNull AbstractLink link, @Nullable LApiHttpBody body, @NotNull PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.body = body;
        this.queryStringsData = null;
        this.placeHolders = placeHolders;
    }

    public LinkQuery(@NotNull LApi lApi, @NotNull AbstractLink link, @NotNull PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.body = null;
        this.queryStringsData = null;
        this.placeHolders = placeHolders;
    }

    @Override
    public Method getMethod() {
        return link.getMethod();
    }

    @Override
    public @NotNull PlaceHolder[] getPlaceHolders() {
        return placeHolders;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        LApiHttpRequest request = new LApiHttpRequest(constructLink(), getMethod(), body, queryStringsData);

        return lApi.appendHeader(request);
    }

    private @NotNull String constructLink() {
        if(constructed == null) constructed = link.construct(lApi.getHttpRequestApiVersion(), placeHolders);
        return constructed;
    }

    @Override
    public String asString() {
        return link.getMethod() + " " + constructLink();
    }

    @Override
    public @NotNull AbstractLink getLink() {
        return link;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to make a {@link LApiHttpRequest} of a {@link Link}
 */
public class LinkQuery implements Query, HasLApi {

    private final @NotNull LApi lApi;
    private final @NotNull Link link;
    private final @Nullable LApiHttpBody body;
    private final @Nullable Data queryStringsData;
    private final @NotNull PlaceHolder[] placeHolders;

    public LinkQuery(@NotNull LApi lApi, @NotNull Link link, @Nullable LApiHttpBody body, @Nullable Data queryStringsData, PlaceHolder... placeHolders){
        this.lApi = lApi;
        this.link = link;
        this.body = body;
        this.queryStringsData = queryStringsData;
        this.placeHolders = placeHolders;
    }

    @Override
    public Method getMethod() {
        return link.getMethod();
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        String url = link.getLink();
        for(PlaceHolder p : placeHolders)
            url = p.place(url);

        LApiHttpRequest request = new LApiHttpRequest(url, getMethod(), body, queryStringsData);

        return lApi.appendHeader(request);
    }

    @Override
    public String asString() {
        String url = link.getLink();
        for(PlaceHolder p : placeHolders)
            url = p.place(url);

        return link.getMethod() + " " + url;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

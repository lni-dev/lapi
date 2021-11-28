package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.AbstractLink;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;

/**
 * This query is used to retrieve images from discord through the cdn import
 * @see @link ImageLink
 */
public class ImageQuery implements Query {

    /**
     * @see <a href="https://discord.com/developers/docs/reference#image-formatting-image-base-url" target="_top">Image Formatting</a>
     */
    public static final String SIZE_QUERY_PARAM_KEY = "size";
    public static final int SIZE_QUERY_PARAM_MIN = 16;
    public static final int SIZE_QUERY_PARAM_MAX = 4096;

    private final LApi lApi;
    private final AbstractLink link;
    private final int desiredSize;
    private final PlaceHolder[] placeHolders;

    /**
     *
     * desiredSize is not checked!
     *
     * @param lApi {@link LApi}
     * @param link {@link ImageLink}
     * @param desiredSize The size, the retrieved image should be. any power of 2 between {@value #SIZE_QUERY_PARAM_MIN} and {@value #SIZE_QUERY_PARAM_MAX}. -1 for unset
     * @param placeHolders the placeholders required for given link
     */
    public ImageQuery(LApi lApi, AbstractLink link, int desiredSize, PlaceHolder... placeHolders) {
        this.lApi = lApi;
        this.link = link;
        this.desiredSize = desiredSize;
        this.placeHolders = placeHolders;
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        String uri = link.getLink();

        for(PlaceHolder p : placeHolders){
            uri = p.place(uri);
        }

        if(desiredSize > 0) {
            Data queryParamsData = new Data(1);
            queryParamsData.add(SIZE_QUERY_PARAM_KEY, desiredSize);
            return new LApiHttpRequest(uri, getMethod(), null, queryParamsData);
        }

        return new LApiHttpRequest(uri, getMethod(), null, null);
    }

    @Override
    public String asString() {
        String uri = link.getLink();

        for(PlaceHolder p : placeHolders)
            uri = p.place(uri);

        return getMethod().toString() + " " + uri;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

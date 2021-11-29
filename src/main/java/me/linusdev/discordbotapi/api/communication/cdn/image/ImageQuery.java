package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.file.types.AbstractFileType;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.AbstractLink;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import org.jetbrains.annotations.NotNull;

/**
 * This query is used to retrieve images from discord through the cdn import
 * @see ImageLink
 */
public class ImageQuery implements Query {

    /**
     * @see <a href="https://discord.com/developers/docs/reference#image-formatting-image-base-url" target="_top">Image Formatting</a>
     */
    public static final String SIZE_QUERY_PARAM_KEY = "size";
    public static final int SIZE_QUERY_PARAM_MIN = 16;
    public static final int SIZE_QUERY_PARAM_MAX = 4096;

    public static final int NO_DESIRED_SIZE = -1;

    private final LApi lApi;
    private final AbstractLink link;
    private final int desiredSize;
    private final AbstractFileType fileType;
    private final PlaceHolder[] placeHolders;

    /**
     *
     * desiredSize is not checked!
     *
     * @param lApi {@link LApi}
     * @param link {@link ImageLink}
     * @param desiredSize The size, the retrieved image should be. any power of 2 between {@value #SIZE_QUERY_PARAM_MIN} and {@value #SIZE_QUERY_PARAM_MAX}. If you want to let Discord decide, use {@value #NO_DESIRED_SIZE}
     * @param fileType The file-type you want to retrieve. Should be {@link FileType#PNG PNG}, {@link FileType#JPEG JPEG}, {@link FileType#GIF GIF}, {@link FileType#WEBP WEBP} or {@link FileType#LOTTIE LOTTIE}
     * @param placeHolders the placeholders required for given link
     */
    public ImageQuery(@NotNull LApi lApi, @NotNull AbstractLink link, int desiredSize, AbstractFileType fileType, PlaceHolder... placeHolders ) {
        this.lApi = lApi;
        this.link = link;
        this.desiredSize = desiredSize;
        this.fileType = fileType;
        this.placeHolders = placeHolders;
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
        String uri = link.getLink();

        for(PlaceHolder p : placeHolders)
            uri = p.place(uri);

        uri = new PlaceHolder(PlaceHolder.FILE_ENDING, fileType.getFileEndings()[0]).place(uri);

        if(desiredSize != NO_DESIRED_SIZE) {
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

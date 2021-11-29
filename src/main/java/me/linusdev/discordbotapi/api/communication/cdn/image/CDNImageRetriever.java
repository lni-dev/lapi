package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.retriever.Retriever;
import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class CDNImageRetriever extends Retriever<InputStream>  {

    /**
     * @param image the {@link CDNImage image}
     * @param desiredSize a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param check whether to check if the size is a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     */
    public CDNImageRetriever(@NotNull CDNImage image, int desiredSize, boolean check) {
        super(image.getLApi(), image.getQuery(desiredSize));
        if(check) checkDesiredSize(desiredSize);
    }

    /**
     *
     * No desired size. Discord will decide.
     *
     * @param image the {@link CDNImage image}
     */
    public CDNImageRetriever(@NotNull CDNImage image) {
        this(image, ImageQuery.NO_DESIRED_SIZE , false);
    }

    @Override
    protected @Nullable InputStream retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return lApi.getResponseAtInputStream(query.getLApiRequest());
    }

    /**
     * checks if given size, is a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     */
    public static void checkDesiredSize(int desiredSize){
        if(!(desiredSize >= ImageQuery.SIZE_QUERY_PARAM_MIN && desiredSize <= ImageQuery.SIZE_QUERY_PARAM_MAX))
            throw new IllegalArgumentException("desiredSize must be a power of 2 between "
                    + ImageQuery.SIZE_QUERY_PARAM_MIN + " and " + ImageQuery.SIZE_QUERY_PARAM_MAX);

        int i = ImageQuery.SIZE_QUERY_PARAM_MIN == 0 ? 1 : ImageQuery.SIZE_QUERY_PARAM_MIN;

        while(i <= ImageQuery.SIZE_QUERY_PARAM_MAX){
            if(i == desiredSize) return;
            i *=2 ;
        }

        throw new IllegalArgumentException("desiredSize must be a power of 2 between "
                + ImageQuery.SIZE_QUERY_PARAM_MIN + " and " + ImageQuery.SIZE_QUERY_PARAM_MAX);
    }

    /**
     *
     * This will write the http-response-body to the file.
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link BiConsumer}, what to do after the file as been written or an error has occurred. the InputStream will already be closed.
     * @return {@link Future}
     */
    @Override
    public @NotNull Future<InputStream> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable BiConsumer<InputStream, Error> after) {
        return queue(new BiConsumer<InputStream, Error>() {
            @Override
            public void accept(InputStream t, Error error) {

                if(error != null){
                    if(after != null) after.accept(t, error);
                    return;
                }

                if(Files.exists(file)){
                    if(!overwriteIfExists){
                        if(after != null) after.accept(t, new Error(new FileAlreadyExistsException(file + " already exists.")));
                        return;
                    }
                }

                OutputStream out = null;
                try {
                    Files.deleteIfExists(file);
                    out = Files.newOutputStream(file);
                    t.transferTo(out);
                    if(after != null) after.accept(t, null);

                } catch (IOException e) {
                    StringBuilder s = new StringBuilder();
                    s.append(e);
                    for (StackTraceElement traceElement : e.getStackTrace())
                        s.append("\nat ").append(traceElement);

                    Logger.log(Logger.Type.ERROR, this.getClass().getSimpleName(), null, s.toString(), true);
                    if(after != null) after.accept(t, new Error(e));
                }finally {
                    if(out != null && t != null) {
                        try {
                            out.close();
                            t.close();
                        } catch (IOException ignored) {  }
                    }
                }
            }
        });
    }
}

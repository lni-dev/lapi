package me.linusdev.discordbotapi.api.communication.retriever;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.lapiandqueue.Future;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.lapiandqueue.Queueable;
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

public abstract class DataRetriever<T> extends Retriever<T>{

    protected boolean storeData = false;
    protected Data data = null;

    /**
     * @param lApi  {@link LApi}
     * @param query the {@link Query} used to retrieve the {@link Data}
     */
    public DataRetriever(@NotNull LApi lApi, @NotNull Query query) {
        super(lApi, query);
    }


    /**
     * Retrieves the wanted Objects JSON and saves it into a {@link Data}.<br>
     * You are probably looking for {@link Queueable#queue()} or {@link #completeHereAndIgnoreQueueThread()}
     *
     * @return {@link Data} with all JSON fields
     * @see Queueable#queue()
     * @see #completeHereAndIgnoreQueueThread()
     */
    protected @NotNull Data retrieveData() throws LApiException, IOException, ParseException, InterruptedException{
        if(storeData){
            data = lApi.getResponseAsData(query.getLApiRequest());
            return data;
        }
        return lApi.getResponseAsData(query.getLApiRequest());
    }

    /**
     *
     * This will write the {@link Data data}, that was retrieved.<br>
     * As for the {@link ArrayRetriever}, the retrieved json-array will be wrapped in a json-object
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link BiConsumer}, what to do after the file as been written or an error has occurred
     * @return {@link Future}
     */
    @Override
    public @NotNull Future<T> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable BiConsumer<T, Error> after) {
        storeData = true;
        return queue(new BiConsumer<T, Error>() {
            @Override
            public void accept(T t, Error error) {

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

                try {
                    Files.deleteIfExists(file);
                    Files.writeString(file, data.getJsonString());
                    if(after != null) after.accept(t, null);

                } catch (IOException e) {
                    StringBuilder s = new StringBuilder();
                    s.append(e);
                    for (StackTraceElement traceElement : e.getStackTrace())
                        s.append("\tat ").append(traceElement);

                    Logger.log(Logger.Type.ERROR, this.getClass().getSimpleName(), null, s.toString(), true);
                    if(after != null) after.accept(t, new Error(e));
                }
            }
        });
    }
}

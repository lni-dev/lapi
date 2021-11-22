package me.linusdev.discordbotapi.api.communication.retriever.response.body;

import me.linusdev.data.Data;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThreadResponseBody extends ResponseBody{

    public static final String THREADS_KEY = "threads";
    public static final String MEMBERS_KEY = "members";
    public static final String HAS_MORE_KEY = "has_more";

    public ThreadResponseBody(@NotNull LApi lApi, @NotNull Data data) throws Exception {
        super(lApi, data);
        ArrayList<Thread> threads = data.getAndConvertArrayList(THREADS_KEY, new ExceptionConverter<Data, Thread>() {
            @Override
            public Thread convert(Data convertible) throws Exception {
                return (Thread) Channel.fromData(lApi, convertible);
            }
        });
    }
}

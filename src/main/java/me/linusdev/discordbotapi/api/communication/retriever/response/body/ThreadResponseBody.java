package me.linusdev.discordbotapi.api.communication.retriever.response.body;

import me.linusdev.data.Data;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.Thread;
import me.linusdev.discordbotapi.api.objects.channel.thread.ThreadMember;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThreadResponseBody extends ResponseBody{

    public static final String THREADS_KEY = "threads";
    public static final String MEMBERS_KEY = "members";
    public static final String HAS_MORE_KEY = "has_more";


    @SuppressWarnings("ConstantConditions")
    public ThreadResponseBody(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        super(lApi, data);
        ArrayList<Thread> threads = data.getAndConvertArrayList(THREADS_KEY,
                (ExceptionConverter<Data, Thread, InvalidDataException>) convertible -> (Thread) Channel.fromData(lApi, convertible));

        ArrayList<ThreadMember> members = data.getAndConvertArrayList(MEMBERS_KEY,
                (ExceptionConverter<Data, ThreadMember, InvalidDataException>) convertible -> ThreadMember.fromData(data));

        boolean hasMore = data.getAndConvert(HAS_MORE_KEY,
                (ExceptionConverter<Boolean, Boolean, InvalidDataException>) convertible -> {
            if(convertible == null) throw new InvalidDataException(data, "has more may not be null!").addMissingFields(HAS_MORE_KEY);
            return convertible;
        });


    }
}

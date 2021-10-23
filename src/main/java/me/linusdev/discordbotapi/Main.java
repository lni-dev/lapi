package me.linusdev.discordbotapi;


import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.ChannelRetriever;
import me.linusdev.discordbotapi.api.objects.channel.abstracts.GuildChannel;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException {
        LApi api = new LApi(Private.TOKEN);

        ChannelRetriever retriever = new ChannelRetriever(api, "785619023055814717");
        Data data = retriever.retrieveData();
        System.out.println(data.getJsonString());

        GuildChannel channel = (GuildChannel) retriever.retrieveChannel();
        System.out.println(channel);


        System.out.println(channel.getName());
        System.out.println(channel.getGuildId());
        System.out.println(channel.getParentId());


    }
}

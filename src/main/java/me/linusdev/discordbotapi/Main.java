package me.linusdev.discordbotapi;


import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.ChannelRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.MessageRetriever;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.objects.channel.GuildStageChannel;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException {

        Config conf = new Config(Config.LOAD_VOICE_REGIONS_ON_STARTUP);

        LApi api = new LApi(Private.TOKEN, conf);

        ChannelRetriever retriever = new ChannelRetriever(api, "902836109892010005");
        Data data = retriever.retrieveData();
        System.out.println(data.getJsonString());

        GuildStageChannel channel = (GuildStageChannel) retriever.retrieveChannel();
        System.out.println(channel);


        System.out.println(channel.getName());
        System.out.println(channel.getGuildId());
        System.out.println(channel.getParentId());
        System.out.println(channel.getTopic());

        // Private Message
        // MessageRetriever msgRetriever = new MessageRetriever(api, "765540315905130516", "905348121675071508");

        // Guild Sticker Message
        MessageRetriever msgRetriever = new MessageRetriever(api, "751169122775334942", "905396558969847879");

        Data data1 = msgRetriever.retrieveData();
        System.out.println(data1.getJsonString());

    }
}

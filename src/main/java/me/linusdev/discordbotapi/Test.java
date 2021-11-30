package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImage;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.log.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import static me.linusdev.discordbotapi.api.lapiandqueue.LApi.CREATOR_ID;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));


        lApi.getCurrentUserRetriever()
                .queueAndWriteToFile(Paths.get("C:\\Users\\Linus\\Desktop\\DiscordBotApi\\download\\file.json"), true, (user, err) -> {
            System.out.println(err);
            assert user.getAvatarHash() != null;
            System.out.println(user.getAvatarHash());

            CDNImageRetriever retriever = new CDNImageRetriever(
                    CDNImage.ofUserAvatar(lApi, CREATOR_ID, (String) user.getAvatarHash(), FileType.PNG));

            retriever.queueAndWriteToFile(Paths.get("C:\\Users\\Linus\\Desktop\\DiscordBotApi\\download\\file.png"), true,
                    (inputStream, error) -> {
                        System.out.println(error);
            });

        });

        System.out.println(new BigInteger(new byte[]{1}));

    }


}

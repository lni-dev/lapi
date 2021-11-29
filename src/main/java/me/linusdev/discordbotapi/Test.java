package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImage;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.file.types.FileType;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.O_DISCORD_API_VERSION_LINK;
import static me.linusdev.discordbotapi.api.lapiandqueue.LApi.CREATOR_ID;

public class Test {


    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));


        lApi.getUserRetriever("247421526532554752").queue(user -> {
            assert user.getAvatar() != null;
            System.out.println(user.getAvatar().simplify());

            CDNImageRetriever retriever = new CDNImageRetriever(
                    CDNImage.ofUserAvatar(lApi, CREATOR_ID, (String) user.getAvatar().simplify(), FileType.PNG));

            retriever.queueAndWriteToFile(Paths.get("C:\\Users\\Linus\\Desktop\\DiscordBotApi\\download\\file.png"), false,
                    (inputStream, error) -> {
                        System.out.println(error);
            });

        });


    }


}

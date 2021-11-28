package me.linusdev.discordbotapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.retriever.query.Query;
import me.linusdev.discordbotapi.api.config.Config;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.user.User;
import me.linusdev.discordbotapi.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.O_DISCORD_API_VERSION_LINK;

public class Test {

    public static void main(String... a) throws LApiException, IOException, ParseException, InterruptedException, ExecutionException, URISyntaxException {

        Logger.start();
        final LApi lApi = new LApi(Private.TOKEN, new Config(0, null));

        //https://cdn.discordapp.com/avatars/884371915488239636/user_avatar.png

        lApi.getUserRetriever("247421526532554752").queue(user -> {
            assert user.getAvatar() != null;
            System.out.println(user.getAvatar().simplify());
        });



        final String userId = "";
        final String userAvatar = "";

        Query query = new Query() {
            @Override
            public Method getMethod() {
                return Method.GET;
            }

            @Override
            public LApiHttpRequest getLApiRequest() throws LApiException {
                return new LApiHttpRequest("https://cdn.discordapp.com/" + "avatars/" + userId + "/" + userAvatar + ".png",
                        getMethod());
            }

            @Override
            public String asString() {
                return null;
            }

            @Override
            public @NotNull LApi getLApi() {
                return lApi;
            }
        };
    }


}

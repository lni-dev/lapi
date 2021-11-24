package me.linusdev.discordbotapi.api.communication.retriever.query;

import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.Method;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.LApiHttpRequest;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.DiscordApiCommunicationHelper.O_DISCORD_API_VERSION_LINK;

/**
 * This class will probably be removed, it is replaced by {@link GetLinkQuery}
 */
@Deprecated
public class SimpleGetLinkQuery implements Query {

    public enum Links{
        GET_BOT_APPLICATION(O_DISCORD_API_VERSION_LINK + "oauth2/applications/@me"),
        GET_VOICE_REGIONS(O_DISCORD_API_VERSION_LINK + "voice/regions"),
        ;

        private final String link;

        Links(String link){
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    private final LApi lApi;
    private final Links link;

    public SimpleGetLinkQuery(@NotNull LApi lApi, @NotNull Links link){
        this.lApi = lApi;
        this.link = link;
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }

    @Override
    public LApiHttpRequest getLApiRequest() throws LApiException {
            return lApi.appendHeader(new LApiHttpRequest(link.getLink(), Method.GET));
    }

    @Override
    public String asString() {
        return link.getLink();
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

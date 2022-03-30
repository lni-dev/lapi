/*
 * Copyright  2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.retriever.query;

import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.lapihttprequest.Method;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.communication.DiscordApiCommunicationHelper.O_DISCORD_API_VERSION_LINK;
import static me.linusdev.lapi.api.communication.PlaceHolder.*;
import static me.linusdev.lapi.api.communication.lapihttprequest.Method.*;

/**
 * These are links to communicate with the official discord api.
 * Some more links are still in {@link GetLinkQuery.Links}
 */
public enum Link implements AbstractLink{

    /**
     * Post a message to a guild text or DM channel. Returns a {@link MessageImplementation message} object.
     *
     * @see PlaceHolder#CHANNEL_ID
     * @see <a href="https://discord.com/developers/docs/resources/channel#create-message" target="_top">Create Message</a>
     */
    CREATE_MESSAGE(POST, O_DISCORD_API_VERSION_LINK + "channels/" + CHANNEL_ID + "/messages"),

    /**
     * Create a response to an Interaction from the gateway. Body is an interaction response. Returns 204 No Content.<br>
     *
     * This endpoint also supports file attachments similar to the webhook endpoints. Refer to Uploading Files for details on uploading files and multipart/form-data requests.
     *
     * @see PlaceHolder#INTERACTION_ID
     * @see PlaceHolder#INTERACTION_TOKEN
     * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#create-interaction-response" target="_top">Create Interaction Response</a>
     */
    CREATE_INTERACTION_RESPONSE(POST, O_DISCORD_API_VERSION_LINK + "interactions/" + INTERACTION_ID + "/"  + INTERACTION_TOKEN + "/callback"),

    /**
     * Returns an object with a single valid WSS URL, which the client can use for Connecting.
     * Clients should cache this value and only call this endpoint to retrieve a new URL if
     * they are unable to properly establish a connection using the cached version of the URL.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway" target="_top">Get Gateway</a>
     */
    GET_GATEWAY(GET, O_DISCORD_API_VERSION_LINK + "gateway"),

    /**
     * Returns an object based on the information in Get Gateway,
     * plus additional metadata that can help during the operation of large or sharded bots.
     * Unlike the Get Gateway, this route should not be cached for extended periods of time
     * as the value is not guaranteed to be the same per-call, and changes as the bot joins/leaves
     * guilds.
     *
     * @see <a href="https://discord.com/developers/docs/topics/gateway#get-gateway-bot" target="_top">Get Gateway Bot</a>
     */
    GET_GATEWAY_BOT(GET, O_DISCORD_API_VERSION_LINK + "gateway/bot"),

    /**
     * Returns an array of {@link me.linusdev.lapi.api.objects.voice.region.VoiceRegion voice region objects}
     * that can be used when setting a voice or stage channel's rtc_region.
     *
     * @see <a href="https://discord.com/developers/docs/resources/voice#list-voice-regions" target="_top">List Voice Regions</a>
     */
    GET_VOICE_REGIONS(GET, O_DISCORD_API_VERSION_LINK + "voice/regions"),
    ;

    private final @NotNull Method method;
    private final @NotNull String link;

    Link(@NotNull Method method, @NotNull String link){
        this.method = method;
        this.link = link;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @NotNull String getLink() {
        return link;
    }
}

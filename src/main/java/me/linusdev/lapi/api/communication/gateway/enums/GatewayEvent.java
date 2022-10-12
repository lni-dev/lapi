/*
 * Copyright (c) 2021-2022 Linus Andera
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

package me.linusdev.lapi.api.communication.gateway.enums;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;

/**
 * <h2 style="padding:0;margin:0;">Gateway Events</h2>
 * <p style="padding:0;margin:0;">
 *     Gateway events are events received from Discord. You can add a {@link EventListener} to listen to these events.
 *     Some events are also split into sub-events by LApi. You can find a list of all events {@link EventIdentifier here}.<br>
 *     For more information on how to listen to events see {@link EventListener}.
 * </p>
 * <p>
 *     {@link #HELLO}, {@link #READY}, {@link #RESUMED}, {@link #RECONNECT} and {@link #INVALID_SESSION} are not
 *     important for you and are already handled by the {@link GatewayWebSocket}.
 * </p>
 *
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events" target="_top">Gateway Events</a>
 */
public enum GatewayEvent implements SimpleDatable {

    /**
     * LApi specific
     */
    UNKNOWN(""),






    /**
     * defines the heartbeat interval
     */
    HELLO("HELLO"),

    /**
     * contains the initial state information
     */
    READY("READY"),

    /**
     * response to Resume
     */
    RESUMED("RESUMED"),

    /**
     * server is going away, client should reconnect to gateway and resume
     */
    RECONNECT("RECONNECT"),

    /**
     * failure response to Identify or Resume or invalid active session
     */
    INVALID_SESSION("INVALID_SESSION"),

    /**
     * new guild channel created
     */
    CHANNEL_CREATE("CHANNEL_CREATE"),

    /**
     * channel was updated
     */
    CHANNEL_UPDATE("CHANNEL_UPDATE"),

    /**
     * channel was deleted
     */
    CHANNEL_DELETE("CHANNEL_DELETE"),

    /**
     * message was pinned or unpinned
     */
    CHANNEL_PINS_UPDATE("CHANNEL_PINS_UPDATE"),

    /**
     * thread created, also sent when being added to a private thread
     */
    THREAD_CREATE("THREAD_CREATE"),

    /**
     * thread was updated
     */
    THREAD_UPDATE("THREAD_UPDATE"),

    /**
     * thread was deleted
     */
    THREAD_DELETE("THREAD_DELETE"),

    /**
     * sent when gaining access to a channel, contains all active threads in that channel
     */
    THREAD_LIST_SYNC("THREAD_LIST_SYNC"),

    /**
     * thread member for the current user was updated
     */
    THREAD_MEMBER_UPDATE("THREAD_MEMBER_UPDATE"),

    /**
     * some user(s) were added to or removed from a thread
     */
    THREAD_MEMBERS_UPDATE("THREAD_MEMBERS_UPDATE"),

    /**
     * lazy-load for unavailable guild, guild became available, or user joined a new guild
     */
    GUILD_CREATE("GUILD_CREATE"),

    /**
     * guild was updated
     */
    GUILD_UPDATE("GUILD_UPDATE"),

    /**
     * guild became unavailable, or user left/was removed from a guild
     */
    GUILD_DELETE("GUILD_DELETE"),

    /**
     * user was banned from a guild
     */
    GUILD_BAN_ADD("GUILD_BAN_ADD"),

    /**
     * user was unbanned from a guild
     */
    GUILD_BAN_REMOVE("GUILD_BAN_REMOVE"),

    /**
     * guild emojis were updated
     */
    GUILD_EMOJIS_UPDATE("GUILD_EMOJIS_UPDATE"),

    /**
     * guild stickers were updated
     */
    GUILD_STICKERS_UPDATE("GUILD_STICKERS_UPDATE"),

    /**
     * guild integration was updated
     */
    GUILD_INTEGRATIONS_UPDATE("GUILD_INTEGRATIONS_UPDATE"),

    /**
     * new user joined a guild
     */
    GUILD_MEMBER_ADD("GUILD_MEMBER_ADD"),

    /**
     * user was removed from a guild
     */
    GUILD_MEMBER_REMOVE("GUILD_MEMBER_REMOVE"),

    /**
     * guild member was updated
     */
    GUILD_MEMBER_UPDATE("GUILD_MEMBER_UPDATE"),

    /**
     * response to Request GuildImpl Members
     */
    GUILD_MEMBERS_CHUNK("GUILD_MEMBERS_CHUNK"),

    /**
     * guild role was created
     */
    GUILD_ROLE_CREATE("GUILD_ROLE_CREATE"),

    /**
     * guild role was updated
     */
    GUILD_ROLE_UPDATE("GUILD_ROLE_UPDATE"),

    /**
     * guild role was deleted
     */
    GUILD_ROLE_DELETE("GUILD_ROLE_DELETE"),

    /**
     * guild scheduled event was created
     */
    GUILD_SCHEDULED_EVENT_CREATE("GUILD_SCHEDULED_EVENT_CREATE"),

    /**
     * guild scheduled event was updated
     */
    GUILD_SCHEDULED_EVENT_UPDATE("GUILD_SCHEDULED_EVENT_UPDATE"),

    /**
     * guild scheduled event was deleted
     */
    GUILD_SCHEDULED_EVENT_DELETE("GUILD_SCHEDULED_EVENT_DELETE"),

    /**
     * user subscribed to a guild scheduled event
     */
    GUILD_SCHEDULED_EVENT_USER_ADD("GUILD_SCHEDULED_EVENT_USER_ADD"),

    /**
     * user unsubscribed from a guild scheduled event
     */
    GUILD_SCHEDULED_EVENT_USER_REMOVE("GUILD_SCHEDULED_EVENT_USER_REMOVE"),

    /**
     * guild integration was created
     */
    INTEGRATION_CREATE("INTEGRATION_CREATE"),

    /**
     * guild integration was updated
     */
    INTEGRATION_UPDATE("INTEGRATION_UPDATE"),

    /**
     * guild integration was deleted
     */
    INTEGRATION_DELETE("INTEGRATION_DELETE"),

    /**
     * user used an interaction, such as an Application Command
     */
    INTERACTION_CREATE("INTERACTION_CREATE"),

    /**
     * invite to a channel was created
     */
    INVITE_CREATE("INVITE_CREATE"),

    /**
     * invite to a channel was deleted
     */
    INVITE_DELETE("INVITE_DELETE"),

    /**
     * message was created
     */
    MESSAGE_CREATE("MESSAGE_CREATE"),

    /**
     * message was edited
     * @see <a href="https://discord.com/developers/docs/topics/gateway-events#message-update">Discord Documentation</a>
     */
    MESSAGE_UPDATE("MESSAGE_UPDATE"),

    /**
     * message was deleted
     */
    MESSAGE_DELETE("MESSAGE_DELETE"),

    /**
     * multiple messages were deleted at once
     */
    MESSAGE_DELETE_BULK("MESSAGE_DELETE_BULK"),

    /**
     * user reacted to a message
     */
    MESSAGE_REACTION_ADD("MESSAGE_REACTION_ADD"),

    /**
     * user removed a reaction from a message
     */
    MESSAGE_REACTION_REMOVE("MESSAGE_REACTION_REMOVE"),

    /**
     * all reactions were explicitly removed from a message
     */
    MESSAGE_REACTION_REMOVE_ALL("MESSAGE_REACTION_REMOVE_ALL"),

    /**
     * all reactions for a given emoji were explicitly removed from a message
     */
    MESSAGE_REACTION_REMOVE_EMOJI("MESSAGE_REACTION_REMOVE_EMOJI"),

    /**
     * user was updated
     */
    PRESENCE_UPDATE("PRESENCE_UPDATE"),

    /**
     * stage instance was created
     */
    STAGE_INSTANCE_CREATE("STAGE_INSTANCE_CREATE"),

    /**
     * stage instance was deleted or closed
     */
    STAGE_INSTANCE_DELETE("STAGE_INSTANCE_DELETE"),

    /**
     * stage instance was updated
     */
    STAGE_INSTANCE_UPDATE("STAGE_INSTANCE_UPDATE"),

    /**
     * user started typing in a channel
     */
    TYPING_START("TYPING_START"),

    /**
     * properties about the user changed
     */
    USER_UPDATE("USER_UPDATE"),

    /**
     * someone joined, left, or moved a voice channel
     */
    VOICE_STATE_UPDATE("VOICE_STATE_UPDATE"),

    /**
     * guild's voice server was updated
     */
    VOICE_SERVER_UPDATE("VOICE_SERVER_UPDATE"),

    /**
     * guild channel webhook was created, update, or deleted
     */
    WEBHOOKS_UPDATE("WEBHOOKS_UPDATE"),

    ;

    private final @NotNull String value;

    GatewayEvent(@NotNull String value) {
        this.value = value;
    }

    /**
     *
     * @param value event-string
     * @return {@link GatewayEvent} matching given string or {@link #UNKNOWN} if none matches. Or {@code null} if given value is {@code null}
     */
    public static @Nullable GatewayEvent fromString(@Nullable String value){
        if(value == null) return null;
        for(GatewayEvent event : GatewayEvent.values()){
            if(event.value.equalsIgnoreCase(value)){
                return event;
            }
        }

        return UNKNOWN;
    }

    public @NotNull String getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }

    public static void main(String... a){
        //used to create the code for the instances of this enum

        String tableFromDiscord = ""; //copy and paste the table from discord here
                                      //(https://discord.com/developers/docs/topics/gateway#commands-and-events-gateway-events)

        String[] lines = tableFromDiscord.split("\n");

        for(String line : lines){

            String[] split = line.split("\t");
            String name = split[0].replace(" ", "_").toUpperCase();
            String docs = split[1];


            System.out.println("/**");
            System.out.println("* " + docs);
            System.out.println("*/");
            System.out.println(name + "(\"" + name + "\"),");
            System.out.println();
        }
    }
}

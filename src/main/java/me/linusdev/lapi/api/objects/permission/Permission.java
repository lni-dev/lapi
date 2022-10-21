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

package me.linusdev.lapi.api.objects.permission;

import me.linusdev.lapi.api.objects.enums.SimpleChannelType;
import me.linusdev.lapi.api.objects.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;

import static me.linusdev.lapi.api.objects.enums.SimpleChannelType.*;


/**
 * This enum contains all of discords permissions.
 *
 * @see <a href="https://discord.com/developers/docs/topics/permissions#permissions">Discord Documentation</a>
 * @updated 17.10.2022
 */
public enum Permission {

    CREATE_INSTANT_INVITE       (0, "Allows creation of instant invites", TEXT, VOICE, STAGE),
    KICK_MEMBERS                (1, "Allows kicking members"),
    BAN_MEMBERS                 (2, "Allows banning members"),
    ADMINISTRATOR               (3, "Allows all permissions and bypasses channel permission overwrites"),
    MANAGE_CHANNELS             (4, "Allows management and editing of channels", TEXT, VOICE, STAGE),
    MANAGE_GUILD                (5, "Allows management and editing of the guild"),
    ADD_REACTIONS               (6, "Allows for the addition of reactions to messages", TEXT),
    VIEW_AUDIT_LOG              (7, "Allows for viewing of audit logs"),
    PRIORITY_SPEAKER            (8, "Allows for using priority speaker in a voice channel", VOICE),
    STREAM                      (9, "Allows the user to go live", VOICE),
    VIEW_CHANNEL                (10, "Allows guild members to view a channel, which includes reading messages in text channels", TEXT, VOICE, STAGE),
    SEND_MESSAGES               (11, "Allows for sending messages in a channel", TEXT),
    SEND_TTS_MESSAGES           (12, "Allows for sending of /tts messages", TEXT),
    MANAGE_MESSAGES             (13, "Allows for deletion of other users messages", TEXT),
    EMBED_LINKS                 (14, "Links sent by users with this permission will be auto-embedded", TEXT),
    ATTACH_FILES                (15, "Allows for uploading images and files", TEXT),
    READ_MESSAGE_HISTORY        (16, "Allows for reading of message history", TEXT),
    MENTION_EVERYONE            (17, "Allows for using the @everyone tag to notify all users in a channel, and the @here tag to notify all online users in a channel", TEXT),
    USE_EXTERNAL_EMOJIS         (18, "Allows the usage of custom emojis from other servers", TEXT),
    VIEW_GUILD_INSIGHTS         (19, "Allows for viewing guild insights"),
    CONNECT                     (20, "Allows for joining of a voice channel", VOICE, STAGE),
    SPEAK                       (21, "Allows for speaking in a voice channel", VOICE),
    MUTE_MEMBERS                (22, "Allows for muting members in a voice channel", VOICE, STAGE),
    DEAFEN_MEMBERS              (23, "Allows for deafening of members in a voice channel", VOICE, STAGE),
    MOVE_MEMBERS                (24, "Allows for moving of members between voice channels", VOICE, STAGE),
    USE_VAD                     (25, "Allows for using voice-activity-detection in a voice channel", VOICE),
    CHANGE_NICKNAME             (26, "Allows for modification of own nickname"),
    MANAGE_NICKNAMES            (27, "Allows for modification of other users nicknames"),
    MANAGE_ROLES                (28, "Allows management and editing of roles", TEXT, VOICE, STAGE),
    MANAGE_WEBHOOKS             (29, "Allows management and editing of webhooks", TEXT),
    MANAGE_EMOJIS_AND_STICKERS  (30, "Allows management and editing of emojis and stickers"),
    USE_APPLICATION_COMMANDS    (31, "Allows members to use application commands, including slash commands and context menu commands.", TEXT),
    REQUEST_TO_SPEAK            (32, "Allows for requesting to speak in stage channels)", STAGE),
    MANAGE_EVENTS               (33, "Allows for creating, editing, and deleting scheduled events", VOICE, STAGE),
    MANAGE_THREADS              (34, "Allows for deleting and archiving threads, and viewing all private threads", TEXT),
    CREATE_PUBLIC_THREADS       (35, "Allows for creating threads", TEXT),
    CREATE_PRIVATE_THREADS      (36, "Allows for creating private threads", TEXT),
    USE_EXTERNAL_STICKERS       (37, "Allows the usage of custom stickers from other servers", TEXT),
    SEND_MESSAGES_IN_THREADS    (38, "Allows for sending messages in threads", TEXT),
    USE_EMBEDDED_ACTIVITIES     (39, "Allows for using activities (applications with the EMBEDDED flag) in a voice channel", VOICE),
    MODERATE_MEMBERS            (40, "Allows for timing out users to prevent them from sending or reacting to messages in chat and threads, and from speaking in voice and stage channels"),
    ;

    /**
     * @deprecated - replaced by {@link #USE_EMBEDDED_ACTIVITIES}
     */
    @Deprecated()
    public static final Permission START_EMBEDDED_ACTIVITIES = USE_EMBEDDED_ACTIVITIES;

    /**
     * The bit set for this permission. to get the actual value you would have to do: 1 << setBit
     */
    final int setBitIndex;

    /**
     * A description, on what the permission allows (as of 30.06.2021) (updated 13.11.2021)
     */
    final @NotNull String description;

    /**
     * The permission value. Useful for bitwise operations
     */
    private @Nullable BigInteger hexInt = null;

    /**
     * Array which hold information about which types of channels this permission can be changed on (as of 30.06.2021).
     * If the array is empty it's a guild-wide permission
     */
    private final @NotNull SimpleChannelType[] appliesTo;

    Permission(int setBitIndex, @NotNull String description, SimpleChannelType... appliesTo){
        this.setBitIndex = setBitIndex;
        this.description = description;
        this.appliesTo = appliesTo;
    }

    /**
     * The permission value. Useful for bitwise operations
     */
    @NotNull
    public BigInteger getHexInt() {
        if(hexInt == null) hexInt = new BigInteger(new byte[]{1}).shiftLeft(setBitIndex);
        return hexInt;
    }

    /**
     * The index, of the bit, which is set for this permission. 0 would be the first bit
     */
    public int getSetBitIndex() {
        return setBitIndex;
    }

    /**
     * checks if this permission can be applied to given {@link Channel}
     */
    public boolean appliesTo(Channel channel){
        if(this.appliesTo.length == 0) return false;
        else{
            if(channel.getType().getSimpleChannelType() == CATEGORY)
                return true;

            for(SimpleChannelType type : this.appliesTo)
                if(channel.getType().getSimpleChannelType() == type)
                    return true;

            return false;
        }
    }

    /**
     * This iterates through {@link Permission#values()} and checks if (bits &amp; permissionBits) == permissionBits.
     * If this is {@code true}, the {@link Permission} with these permissionBits is added to the list
     *
     * @param bits the set permission bits
     * @return a list of {@link Permission}, which permissionBits are "contained" in bits
     */
    @NotNull
    public static ArrayList<Permission> getPermissionsFromBits(@NotNull BigInteger bits){

        ArrayList<Permission> perms = new ArrayList<>(bits.bitCount());

        for(Permission permission : Permission.values())
            if(bits.testBit(permission.getSetBitIndex()))
                perms.add(permission);

        return perms;
    }


}

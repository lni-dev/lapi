package me.linusdev.discordbotapi.api.objects.enums;

import me.linusdev.discordbotapi.api.objects.channel.abstracts.Channel;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static me.linusdev.discordbotapi.api.objects.enums.SimpleChannelType.*;


/**
 * <a href="https://discord.com/developers/docs/topics/permissions#permissions">Permissions</a>
 */
public enum Permissions {

    CREATE_INSTANT_INVITE       ("0000000001", "Allows creation of instant invites", TEXT, VOICE, STAGE),
    KICK_MEMBERS                ("0000000002", "Allows kicking members"),
    BAN_MEMBERS                 ("0000000004", "Allows banning members"),
    ADMINISTRATOR               ("0000000008", "Allows all permissions and bypasses channel permission overwrites"),
    MANAGE_CHANNELS             ("0000000010", "Allows management and editing of channels", TEXT, VOICE, STAGE),
    MANAGE_GUILD                ("0000000020", "Allows management and editing of the guild"),
    ADD_REACTIONS               ("0000000040", "Allows for the addition of reactions to messages", TEXT),
    VIEW_AUDIT_LOG              ("0000000080", "Allows for viewing of audit logs"),
    PRIORITY_SPEAKER            ("0000000100", "Allows for using priority speaker in a voice channel", VOICE),
    STREAM                      ("0000000200", "Allows the user to go live", VOICE),
    VIEW_CHANNEL                ("0000000400", "Allows guild members to view a channel, which includes reading messages in text channels", TEXT, VOICE, STAGE),
    SEND_MESSAGES               ("0000000800", "Allows for sending messages in a channel", TEXT),
    SEND_TTS_MESSAGES           ("0000001000", "Allows for sending of /tts messages", TEXT),
    MANAGE_MESSAGES             ("0000002000", "Allows for deletion of other users messages", TEXT),
    EMBED_LINKS                 ("0000004000", "Links sent by users with this permission will be auto-embedded", TEXT),
    ATTACH_FILES                ("0000008000", "Allows for uploading images and files", TEXT),
    READ_MESSAGE_HISTORY        ("0000010000", "Allows for reading of message history", TEXT),
    MENTION_EVERYONE            ("0000020000", "Allows for using the @everyone tag to notify all users in a channel, and the @here tag to notify all online users in a channel", TEXT),
    USE_EXTERNAL_EMOJIS         ("0000040000", "Allows the usage of custom emojis from other servers", TEXT),
    VIEW_GUILD_INSIGHTS         ("0000080000", "Allows for viewing guild insights"),
    CONNECT                     ("0000100000", "Allows for joining of a voice channel", VOICE, STAGE),
    SPEAK                       ("0000200000", "Allows for speaking in a voice channel", VOICE),
    MUTE_MEMBERS                ("0000400000", "Allows for muting members in a voice channel", VOICE, STAGE),
    DEAFEN_MEMBERS              ("0000800000", "Allows for deafening of members in a voice channel", VOICE, STAGE),
    MOVE_MEMBERS                ("0001000000", "Allows for moving of members between voice channels", VOICE, STAGE),
    USE_VAD                     ("0002000000", "Allows for using voice-activity-detection in a voice channel", VOICE),
    CHANGE_NICKNAME             ("0004000000", "Allows for modification of own nickname"),
    MANAGE_NICKNAMES            ("0008000000", "Allows for modification of other users nicknames"),
    MANAGE_ROLES                ("0010000000", "Allows management and editing of roles", TEXT, VOICE, STAGE),
    MANAGE_WEBHOOKS             ("0020000000", "Allows management and editing of webhooks", TEXT),
    MANAGE_EMOJIS_AND_STICKERS  ("0040000000", "Allows management and editing of emojis and stickers"),
    USE_APPLICATION_COMMANDS    ("0080000000", "Allows members to use application commands, including slash commands and context menu commands.", TEXT),
    REQUEST_TO_SPEAK            ("0100000000", "Allows for requesting to speak in stage channels)", STAGE),
    MANAGE_THREADS              ("0400000000", "Allows for deleting and archiving threads, and viewing all private threads", TEXT),
    USE_PUBLIC_THREADS          ("0800000000", "Allows for creating and participating in threads", TEXT),
    USE_PRIVATE_THREADS         ("1000000000", "Allows for creating and participating in private threads", TEXT),
    USE_EXTERNAL_STICKERS       ("2000000000", "Allows the usage of custom stickers from other servers", TEXT),
    SEND_MESSAGES_IN_THREADS    ("4000000000", "Allows for sending messages in threads", TEXT),
    START_EMBEDDED_ACTIVITIES   ("8000000000", "Allows for launching activities (applications with the EMBEDDED flag) in a voice channel", VOICE),
    ;

    /**
     * Permission value as hex string. Does NOT start with "0x"
     */
    final @NotNull String hex;

    /**
     * A description, on what the permission allows (as of 30.06.2021) (updated 13.11.2021)
     */
    final @NotNull String description;

    /**
     * The permission value. Useful for bitwise operations
     */
    private final @NotNull BigInteger hexInt;

    /**
     * Array which hold information about which types of channels this permission can be changed on (as of 30.06.2021).
     * If the array is empty it's a guild-wide permission
     */
    private final @NotNull SimpleChannelType[] appliesTo;

    Permissions(@NotNull String hex, @NotNull String description, SimpleChannelType... appliesTo){
        this.hex = hex;
        this.description = description;
        this.appliesTo = appliesTo;

        this.hexInt = new BigInteger(hex, 16);
    }

    /**
     * The permission value. Useful for bitwise operations
     */
    @NotNull
    public BigInteger getHexInt() {
        return hexInt;
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
     * This iterates through {@link Permissions#values()} and checks if (bits & permissionBits) == permissionBits.
     * If this is {@code true}, the {@link Permissions} with these permissionBits is added to the list
     *
     * @param bits the set permission bits
     * @return a list of {@link Permissions}, which permissionBits are "contained" in bits
     */
    @NotNull
    public static List<Permissions> getPermissionsFromBits(@NotNull BigInteger bits){

        ArrayList<Permissions> perms = new ArrayList<>();

        for(Permissions permission : Permissions.values())
            if(bits.and(permission.getHexInt()).equals(permission.getHexInt()))
                perms.add(permission);

        return perms;
    }


}

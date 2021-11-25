package me.linusdev.discordbotapi.api.templates.message.builder;

import me.linusdev.discordbotapi.api.communication.PlaceHolder;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.discordbotapi.api.communication.PlaceHolder.*;

/**
 * This class can give you a string to mention persons or objects<br><br>
 *
 * Example (247421526532554752 is the id of the user to mention):
 * <pre>
 * {@code
 *
 * String userMention = MentionType.USER.get(
 *                      new PlaceHolder(PlaceHolder.USER_ID, "247421526532554752")
 *                      );
 * //if above String is contained in a message-content,
 * //the user with given id will be @-mentioned
 * }
 * </pre>
 */
public enum MentionType {

    /**
     * @see PlaceHolder#USER_ID
     */
    USER                            ("<@" + USER_ID + ">"),

    /**
     * @see PlaceHolder#USER_ID
     */
    USER_NICKNAME                   ("<@!" + USER_ID + ">"),

    /**
     * @see PlaceHolder#CHANNEL_ID
     */
    CHANNEL                         ("<#" + CHANNEL_ID + ">"),

    /**
     * @see PlaceHolder#ROLE_ID
     */
    ROLE                            ("<@&" + ROLE_ID + ">"),

    /**
     * @see PlaceHolder#EMOJI
     */
    STANDARD_EMOJI                  (EMOJI),

    /**
     * @see PlaceHolder#EMOJI_NAME
     * @see PlaceHolder#EMOJI_ID
     */
    CUSTOM_EMOJI                    ("<:" + EMOJI_NAME + ":" + EMOJI_ID +">"),

    /**
     * @see PlaceHolder#EMOJI_NAME
     * @see PlaceHolder#EMOJI_ID
     */
    CUSTOM_EMOJI_ANIMATED           ("<a:" + EMOJI_NAME + ":" + EMOJI_ID +">"),

    /**
     * @see PlaceHolder#TIMESTAMP
     */
    TIMESTAMP                       ("<t:" + PlaceHolder.TIMESTAMP +">"),

    /**
     * @see PlaceHolder#TIMESTAMP
     * @see PlaceHolder#TIMESTAMP_STYLE
     */
    TIMESTAMP_STYLED                ("<t:" + PlaceHolder.TIMESTAMP +":" + TIMESTAMP_STYLE + ">"),


    /**
     * This will mention everyone who is in the guild
     */
    EVERYONE                        ("@everyone"),

    /**
     * This will mention everyone who has access to the channel
     */
    HERE                            ("@here"),
    ;

    private final String string;


    MentionType(String string) {
        this.string = string;
    }

    /**
     *
     * @param placeHolders to place
     * @return mention string, with all placeholders replaced
     */
    public @NotNull String get(PlaceHolder... placeHolders){
        String mention = string;

        for(PlaceHolder placeHolder : placeHolders)
            mention = placeHolder.place(mention);

        return mention;
    }
}

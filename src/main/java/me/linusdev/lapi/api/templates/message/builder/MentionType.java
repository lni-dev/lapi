/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.templates.message.builder;

import me.linusdev.lapi.api.other.placeholder.Name;
import me.linusdev.lapi.api.other.placeholder.PlaceHolder;
import org.jetbrains.annotations.NotNull;

import static me.linusdev.lapi.api.other.placeholder.Name.*;

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
     * @see Name#USER_ID
     */
    USER                            ("<@" + USER_ID + ">"),

    /**
     * @see Name#USER_ID
     */
    USER_NICKNAME                   ("<@!" + USER_ID + ">"),

    /**
     * @see Name#CHANNEL_ID
     */
    CHANNEL                         ("<#" + CHANNEL_ID + ">"),

    /**
     * @see Name#ROLE_ID
     */
    ROLE                            ("<@&" + ROLE_ID + ">"),

    /**
     * @see Name#EMOJI
     */
    STANDARD_EMOJI                  (EMOJI.toString()),

    /**
     * @see Name#EMOJI_NAME
     * @see Name#EMOJI_ID
     */
    CUSTOM_EMOJI                    ("<:" + EMOJI_NAME + ":" + EMOJI_ID +">"),

    /**
     * @see Name#EMOJI_NAME
     * @see Name#EMOJI_ID
     */
    CUSTOM_EMOJI_ANIMATED           ("<a:" + EMOJI_NAME + ":" + EMOJI_ID +">"),

    /**
     * @see Name#TIMESTAMP
     */
    TIMESTAMP                       ("<t:" + Name.TIMESTAMP +">"),

    /**
     * @see Name#TIMESTAMP
     * @see Name#TIMESTAMP_STYLE
     */
    TIMESTAMP_STYLED                ("<t:" + Name.TIMESTAMP +":" + TIMESTAMP_STYLE + ">"),


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

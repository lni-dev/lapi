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

package me.linusdev.lapi.api.objects.message.interaction;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     This is sent on the {@link me.linusdev.lapi.api.objects.message.abstracts.Message message object}
 *     when the message is a response to an Interaction without an existing message.
 * </p>
 * <p>
 *     This means responses to Message Components do not include this property, instead including a message reference
 *     object as components always exist on preexisting messages.
 * </p>
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#message-interaction-object" target="_top">Message Interaction Object</a>
 */
public class MessageInteraction implements Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String TYPE_KEY = "type";
    public static final String NAME_KEY = "name";
    public static final String USER_KEY = "user";

    private final @NotNull Snowflake id;
    private final @NotNull InteractionType type;
    private final @NotNull String name;
    private final @NotNull User user;

    private final @NotNull LApi lApi;

    /**
     *
     * @param id id of the interaction
     * @param type the type of interaction
     * @param name the name of the application command
     * @param user the user who invoked the interaction
     */
    public MessageInteraction(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull InteractionType type, @NotNull String name, @NotNull User user){
        this.lApi = lApi;
        this.id = id;
        this.type = type;
        this.name = name;
        this.user = user;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link MessageInteraction} or {@code null} if data is {@code null}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #TYPE_KEY}, {@link #NAME_KEY} or {@link #USER_KEY} are null or missing
     */
    public static @Nullable MessageInteraction fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        String name = (String) data.get(NAME_KEY);
        SOData user = (SOData) data.get(USER_KEY);

        if(id == null || type == null || name == null || user == null){
            InvalidDataException.throwException(data, null, MessageInteraction.class,
                    new Object[]{id, type, name, user},
                    new String[]{ID_KEY, TYPE_KEY, NAME_KEY, USER_KEY});
            //we will never reach this, because above method will throw an exception,
            //but I have it here anyways, so IntelliJ doesn't annoy me with things that could be null
            return null;
        }

        return new MessageInteraction(lApi, Snowflake.fromString(id), InteractionType.fromValue(type.intValue()), name, User.fromData(lApi, user));
    }

    /**
     * id as {@link Snowflake} of the interaction
     */
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * id as {@link String} of the interaction
     */
    public @NotNull String getId() {
        return id.asString();
    }

    /**
     * the type of interaction
     */
    public @NotNull InteractionType getType() {
        return type;
    }

    /**
     * the name of the application command
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * the user who invoked the interaction
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * Generates {@link SOData} from this {@link MessageInteraction}
     */
    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(ID_KEY, id);
        data.add(TYPE_KEY, type);
        data.add(NAME_KEY, name);
        data.add(USER_KEY, user);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

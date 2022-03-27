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

package me.linusdev.lapi.api.objects.command.option;

import me.linusdev.data.Data;
import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.channel.abstracts.Channel;
import me.linusdev.lapi.api.objects.role.Role;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.other.LApiConverter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Don't get spooked by this class, it is basically an enum with generics.
 *
 * @param <C>  {@link Object}
 * @param <R>  result
 * @param <E>  {@link InvalidDataException}
 * @see <a href="https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type" target="_top">Application Command Option Type</a>
 */
public final class ApplicationCommandOptionType<C, R, E extends InvalidDataException> implements SimpleDatable {

    public static final ApplicationCommandOptionType<Object, Object, InvalidDataException>
            UNKNOWN = new ApplicationCommandOptionType<>(0, (lApi, convertible) -> convertible);

    public static final ApplicationCommandOptionType<Object, Object, InvalidDataException>
            SUB_COMMAND = new ApplicationCommandOptionType<>(1, (lApi, convertible) -> {
                if(convertible == null) return null;
                //TODO SubCommand
                return null;
    });

    public static final ApplicationCommandOptionType<Object, Object, InvalidDataException>
            SUB_COMMAND_GROUP = new ApplicationCommandOptionType<>(2, (lApi, convertible) -> {

                if(convertible == null) return null;
                //TODO SubCommandGroup
                return null;
    });

    public static final ApplicationCommandOptionType<Object, String, InvalidDataException>
            STRING = new ApplicationCommandOptionType<>(3, (lApi, convertible) -> (String) convertible);

    /**
     * 	Any integer between -2^53 and 2^53
     */
    public static final ApplicationCommandOptionType<Object, Long, InvalidDataException>
            INTEGER = new ApplicationCommandOptionType<>(4, (lApi, convertible) -> {
                if(convertible == null) return null;
                return ((Number) convertible).longValue();
    });

    public static final ApplicationCommandOptionType<Object, Boolean, InvalidDataException>
            BOOLEAN = new ApplicationCommandOptionType<>(5, (lApi, convertible) -> (Boolean) convertible);

    public static final ApplicationCommandOptionType<Object, User, InvalidDataException>
            USER = new ApplicationCommandOptionType<>(6, (lApi, convertible) -> User.fromData(lApi, (Data) convertible));

    /**
     * Includes all channel types + categories
     */
    public static final ApplicationCommandOptionType<Object, Channel, InvalidDataException>
            CHANNEL = new ApplicationCommandOptionType<>(7, (lApi, convertible) -> Channel.fromData(lApi, (Data) convertible));

    public static final ApplicationCommandOptionType<Object, Role, InvalidDataException>
            ROLE = new ApplicationCommandOptionType<>(8, (lApi, convertible) -> Role.fromData(lApi, (Data) convertible));

    /**
     * Includes users and roles
     */
    public static final ApplicationCommandOptionType<Object, Object, InvalidDataException>
            MENTIONABLE = new ApplicationCommandOptionType<>(9, (lApi, convertible) -> {
                if(convertible == null) return null;
                //TODO MentionAble
                return null;
    });

    /**
     * Any double between -2^53 and 2^53
     */
    public static final ApplicationCommandOptionType<Object, Double, InvalidDataException>
            NUMBER = new ApplicationCommandOptionType<>(10, (lApi, convertible) ->{
                if(convertible == null) return null;
                return ((Number) convertible).doubleValue();
    });

    public static final ApplicationCommandOptionType[] values = {SUB_COMMAND, SUB_COMMAND_GROUP, STRING,
            INTEGER, BOOLEAN, USER, CHANNEL, ROLE, MENTIONABLE, NUMBER, UNKNOWN};

    private final int value;
    private final LApiConverter<C, R, E> converter;

    private ApplicationCommandOptionType(int value, LApiConverter<C, R, E> converter) {
        this.value = value;
        this.converter = converter;
    }

    /**
     *
     * @param value int
     * @return {@link ApplicationCommandOptionType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull ApplicationCommandOptionType fromValue(int value){
        for(ApplicationCommandOptionType type : values){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    public R convertValue(LApi lApi, C convertible) throws E{
        return converter.convert(lApi, convertible);
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }

}

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

package me.linusdev.lapi.api.communication;

import me.linusdev.lapi.api.other.placeholder.Name;

import java.time.Duration;


/**
 * @see <a href="https://discord.com/developers/docs/resources/application" target="_top">Resources</a>
 * @see <a href="https://discord.com/developers/docs/reference" target="_top">References</a>
 */
public class DiscordApiCommunicationHelper {

    public static Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(10L);



    //https://discord.com/developers/docs/resources/user
    public static final String DISCORD_COM = "https://discord.com";

    /**
     * <a href="https://discord.com/developers/docs/reference#authentication" target="_top">Authentication</a>
     */
    public static final String ATTRIBUTE_AUTHORIZATION_NAME = "Authorization";
    public static final String ATTRIBUTE_AUTHORIZATION_VALUE = "Bot " + Name.TOKEN;

    /**
     * <a href="https://discord.com/developers/docs/reference#user-agent" target="_top">User Agent</a>
     */
    public static final String ATTRIBUTE_USER_AGENT_NAME = "User-Agent";
    public static final String ATTRIBUTE_USER_AGENT_VALUE = "DiscordBot (" + Name.LAPI_URL +", " + Name.LAPI_VERSION + ")";

    /**
     * Time from 1. January 1970 to the first second of 2015
     * @see <a href="https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right" target="_top">Discord Epoch</a>
     */
    public static final long DISCORD_EPOCH = 1420070400000L;

}

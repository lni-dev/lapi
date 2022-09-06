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

package me.linusdev.lapi;


import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.invite.Invite;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import me.linusdev.lapi.api.objects.message.Reaction;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.message.embed.EmbedBuilder;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
import me.linusdev.lapi.api.templates.message.builder.MentionType;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import me.linusdev.lapi.api.templates.message.builder.TimestampStyle;
import me.linusdev.lapi.helper.Helper;
import me.linusdev.lapi.list.LinusLinkedList;
import me.linusdev.lapi.list.LinusLinkedListEntry;
import me.linusdev.lapi.log.LogInstance;
import me.linusdev.lapi.log.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("removal")
public class Main {

    public static void main(String... args) throws IOException, InterruptedException, ParseException, LApiException, ExecutionException, URISyntaxException {

        Logger.start(true, false);
        LogInstance log = Logger.getLogger("main");

        //final LApi api = new ConfigBuilder(Helper.getConfigPath()).buildLApi();


    }
}

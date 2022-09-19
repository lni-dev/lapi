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

package me.linusdev.lapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageCreateEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageDeleteEvent;
import me.linusdev.lapi.api.communication.gateway.events.message.MessageUpdateEvent;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * Heading 1:<br>
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">Title</span><br>
 * Heading 2:<br>
 * <span style="margin-bottom:0;padding-bottom:0;font-size:15px;font-weight:'bold';">Title</span><br>
 */
public class Examples {

    public static void main(String... args) throws LApiException, IOException, ParseException, InterruptedException {

        Config config = ConfigBuilder.getDefault("TOKEN", true).build();
        LApi lApi = ConfigBuilder.getDefault("TOKEN", true).buildLApi();

        lApi.getEventTransmitter().addListener(new EventListener() {
            @Override
            public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
                //code
            }

            @Override
            public void onMessageUpdate(@NotNull LApi lApi, @NotNull MessageUpdateEvent event) {
                //code
            }

            @Override
            public void onMessageDelete(@NotNull LApi lApi, @NotNull MessageDeleteEvent event) {
                //code
            }
        });

        lApi.getEventTransmitter().addListener(new EventListener() {
            @Override
            public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
                System.out.println("Message: " + event.getMessage().getContent());

                if(!event.getMessage().getAuthor().isBot()
                    && event.getMessage().getContent().equals("Hi")){
                    lApi.getRequestFactory().createMessage(event.getChannelId(), "Hi").queue();
                }
            }
        });


        Queueable<MessageImplementation> msgRetriever = lApi.getRequestFactory().getChannelMessage("channelId", "messageId");

        msgRetriever.queue((result, response, error) -> {
            if(error != null) {
                System.out.println("could not get message.");
                return;
            }

            System.out.println("Message content: " + result.getContent());

        });


    }

}

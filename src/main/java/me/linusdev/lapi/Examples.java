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
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.lapiandqueue.LApi;
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

        Config config = ConfigBuilder.getDefault("TOKEN").build();
        LApi lApi = ConfigBuilder.getDefault("TOKEN").buildLApi();

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


    }

}

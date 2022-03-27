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

package me.linusdev.lapi.api.communication.retriever;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.PlaceHolder;
import me.linusdev.lapi.api.communication.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.query.GetLinkQuery;
import me.linusdev.lapi.api.objects.message.MessageImplementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * This class is used to retrieve a {@link MessageImplementation}.<br>
 * <br>
 * Example:<br>
 * <pre>{@code
 * LApi api = new LApi(TOKEN, config);
 * MessageRetriever retriever = new MessageRetriever(api, "820084724693073921",
 *                                      "854807543603003402");
 *
 * retriever.queue().then((message, error) -> {
 *             if(error != null){
 *                 error.getThrowable().printStackTrace();
 *                 return;
 *             }
 *             System.out.println("Message Content: " + message.getContent());
 *         });
 * }
 * </pre>
 *
 *
 * @see MessageImplementation
 */
public class MessageRetriever extends DataRetriever<MessageImplementation> {

    public MessageRetriever(@NotNull LApi lApi, @NotNull String channelId, @NotNull String messageId) {
        super(lApi, new GetLinkQuery(lApi, GetLinkQuery.Links.GET_CHANNEL_MESSAGE,
                new PlaceHolder(PlaceHolder.CHANNEL_ID, channelId),
                new PlaceHolder(PlaceHolder.MESSAGE_ID, messageId)));
    }

    @Override
    public @Nullable MessageImplementation retrieve() throws LApiException, IOException, ParseException, InterruptedException {
        return new MessageImplementation(lApi, retrieveData());
    }
}

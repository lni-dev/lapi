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

package me.linusdev.lapi.api.async.queue;

import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.ResultConsumer;
import me.linusdev.lapi.api.async.Task;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.objects.HasLApi;


/**
 * <p>
 *     A {@link Queueable} is Task, that can be queued and executed. It is used for all http requests to discord.<br>
 *     By invoking {@link #queue()}, a {@link Future} will be created and {@link LApiImpl#queue(QueueableFuture) queued}.
 *     All Objects in the {@link LApi#queue queue} will be completed in the queued order. Two Tasks may never
 *     run at the same time.
 * </p>
 *
 * <p>
 *     The result can be processed by using listeners like {@link Future#then(ResultConsumer)}. These can be set directly when queuing by
 *     calling {@link Queueable#queue(ResultConsumer)}.
 *     <b style="color:red">{@link Object#wait()}, {@link Thread#sleep(long)} or any other waiting tasks may never be called inside these listeners!</b>.
 *     This will delay the queue and could lead to an infinite {@link Object#wait()}.
 * </p><br><br>
 *
 * <span style="margin-bottom:0;padding-bottom:0;font-size:10px;font-weight:'bold';">How to use a {@link Queueable}:</span>
 * <pre>{@code
 * LApi api = new LApi(Private.TOKEN, config);
 *
 * //Retrieve the Message with id=messageId
 * //inside the channel with id=channelId
 * var msgRetriever = lApi.getRequestFactory()
 *                        .getChannelMessage("channelId", "messageId");
 *
 * //Queue and create a listener
 * msgRetriever.queue((result, response, error) -> {
 *             if(error != null) {
 *                 System.out.println("could not get message.");
 *                 return;
 *             }
 *
 *             System.out.println("Message content: " + result.getContent());
 *
 *         });
 * }</pre>
 *
 * @param <T> Type of the data that should be retrieved / the result of the Task
 * @see Future
 */
public interface Queueable<T> extends Task<T, QResponse>, HasLApi {

}

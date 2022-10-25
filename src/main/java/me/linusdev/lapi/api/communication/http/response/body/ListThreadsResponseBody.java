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

package me.linusdev.lapi.api.communication.http.response.body;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.objects.channel.Channel;
import me.linusdev.lapi.api.objects.channel.thread.ThreadMember;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 *
 * This is a {@link ResponseBody} often used when retrieving an array of {@link Thread threads}
 *
 * @see <a href="https://discord.com/developers/docs/resources/channel#list-public-archived-threads-response-body" target="_top">Response Body</a>
 */
public class ListThreadsResponseBody extends ResponseBody{

    public static final String THREADS_KEY = "threads";
    public static final String MEMBERS_KEY = "members";
    public static final String HAS_MORE_KEY = "has_more";

    private final @NotNull ArrayList<Channel> threads;
    private @NotNull ArrayList<ThreadMember> members;
    private final boolean hasMore;

    /**
     *
     * @param lApi {@link LApi}
     * @param data ResponseData with all required fields
     * @throws InvalidDataException if {@link #THREADS_KEY}, {@link #MEMBERS_KEY} or {@link #HAS_MORE_KEY} are null or missing
     */
    @SuppressWarnings("ConstantConditions")
    public ListThreadsResponseBody(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        super(lApi, data);
        threads = data.getListAndConvertWithException(THREADS_KEY, (SOData c) -> Channel.channelFromData(lApi, c));
        members = data.getListAndConvertWithException(MEMBERS_KEY, ThreadMember::fromData);
        hasMore = data.getAsAndRequireNotNull(HAS_MORE_KEY, InvalidDataException.SUPPLIER);

        if(members == null){
            InvalidDataException.throwException(data, null, ListThreadsResponseBody.class,
                    new Object[]{null},
                    new String[]{ MEMBERS_KEY});
        }
    }

    /**
     * the active threads
     */
    public @NotNull ArrayList<Channel> getThreads() {
        return threads;
    }

    /**
     * a thread member object for each returned thread the current user has joined. <br>
     * <br>
     * This means: for each {@link Thread thread} in {@link #getThreads()} the current user (your bot) is a member of,
     * a {@link ThreadMember thread member object} is in this list. Meaning, the list doesn't have to be the same size
     * as {@link #getThreads()}
     */
    public @NotNull ArrayList<ThreadMember> getMembers() {
        return members;
    }

    /**
     * whether there are potentially additional threads that could be returned on a subsequent call
     */
    public boolean hasMore() {
        return hasMore;
    }
}

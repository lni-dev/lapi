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

package me.linusdev.lapi.api.manager.guild.emoji;

import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface EmojiPool extends HasLApi {

    /**
     * Get {@link EmojiObject} with given id
     * @param id of the emoji
     * @return {@link EmojiObject} with given id or {@code null} if no such {@link EmojiObject} exists.
     */
    @Nullable EmojiObject getEmoji(@NotNull String id);

    /**
     * Get {@link EmojiObject} with given id
     * @param id of the emoji
     * @return {@link EmojiObject} with given id or {@code null} if no such {@link EmojiObject} exists.
     */
    default @Nullable EmojiObject getEmoji(@NotNull Snowflake id){
        return getEmoji(id.asString());
    }

    /**
     * The Collection, may change at any time, but should never be changed manually.
     * @return {@link Collection} of Emojis, which is possibly backed by this {@link EmojiPool}.
     */
    @NotNull Collection<EmojiObject> getEmojis();
}

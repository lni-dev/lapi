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

package me.linusdev.lapi.api.templates.channel;

import me.linusdev.data.so.SOData;
import me.linusdev.data.so.SODatable;
import me.linusdev.lapi.api.objects.channel.forum.ForumTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForumTagTemplate implements SODatable {

    private final @Nullable String id;
    private final @NotNull String name;
    private final @Nullable Boolean moderated;
    private final @Nullable String emojiId;
    private final @Nullable String emojiName;

    public ForumTagTemplate(@Nullable String id, @NotNull String name, @Nullable Boolean moderated, @Nullable String emojiId, @Nullable String emojiName) {
        this.id = id;
        this.name = name;
        this.moderated = moderated;
        this.emojiId = emojiId;
        this.emojiName = emojiName;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.addIfNotNull(ForumTag.ID_KEY, id);
        data.add(ForumTag.NAME_KEY, name);
        data.addIfNotNull(ForumTag.MODERATED_KEY, moderated);
        data.addIfNotNull(ForumTag.EMOJI_ID_KEY, emojiId);
        data.addIfNotNull(ForumTag.EMOJI_NAME_KEY, emojiName);

        return data;
    }
}

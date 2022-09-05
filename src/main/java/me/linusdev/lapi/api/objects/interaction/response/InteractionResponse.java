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

package me.linusdev.lapi.api.objects.interaction.response;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-response-structure" target="_top">Interaction Response Structure</a>
 */
public class InteractionResponse implements Datable, Template {

    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";

    private final @NotNull InteractionCallbackType type;
    private final @Nullable Template data;

    /**
     *
     * @param type the type of response
     * @param data an optional response message
     */
    public InteractionResponse(@NotNull InteractionCallbackType type, @Nullable Template data) {
        this.type = type;
        this.data = data;
    }

    /**
     * the type of response
     */
    public @NotNull InteractionCallbackType getType() {
        return type;
    }

    /**
     * an optional response message
     */
    public @Nullable Template getResponseData(){
        return data;
    }

    @Override
    public FilePart[] getFileParts() {
        if(data != null) return data.getFileParts();
        return Template.super.getFileParts();
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(TYPE_KEY, type);
        data.addIfNotNull(DATA_KEY, this.data);

        return data;
    }
}

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

package me.linusdev.lapi.api.templates.message;

import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.enums.MessageFlag;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.Nullable;

public class EditMessageTemplate extends MessageTemplate implements Template {


    /**
     * @param content          pure text content of the message
     * @param embeds           {@link Embed embeds} for this message
     * @param allowedMentions  {@link AllowedMentions}. if {@code null}, Discord will generate the mentions of the content
     * @param components       the {@link Component components} to include with the message
     * @param attachments      attachment objects with filename and description
     * @param flags            {@link MessageFlag message flags} combined as a bitfield (only {@link MessageFlag#SUPPRESS_EMBEDS} can be set)
     */
    public EditMessageTemplate(@Nullable String content, @Nullable Embed[] embeds,
                               @Nullable AllowedMentions allowedMentions, @Nullable Component[] components,
                               @Nullable Attachment[] attachments, @Nullable Long flags) {
        super(content, null, null, embeds, allowedMentions, null, components, null, attachments, flags);
    }
}

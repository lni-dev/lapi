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

package me.linusdev.lapi.api.templates.message;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.http.request.body.FilePart;
import me.linusdev.lapi.api.objects.attachment.abstracts.Attachment;
import me.linusdev.lapi.api.objects.message.MessageReference;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.message.embed.Embed;
import me.linusdev.lapi.api.objects.nonce.Nonce;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.templates.abstracts.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * <p>
 *     {@link Template} used to {@link RequestFactory#createMessage(String, MessageTemplate) create a message}.
 * </p>
 * @see <a href="https://discord.com/developers/docs/resources/channel#create-message-jsonform-params" target="_top">JSON/Form Params</a>
 * @see RequestFactory#createMessage(String, MessageTemplate)
 */
public class MessageTemplate implements Template {

    public static final int NONCE_MAX_CHARS = 25;

    public static final String CONTENT_KEY = "content";
    public static final String TTS_KEY = "tts";
    public static final String EMBEDS_KEY = "embeds";
    public static final String ALLOWED_MENTIONS_KEY = "allowed_mentions";
    public static final String MESSAGE_REFERENCE_KEY = "message_reference";
    public static final String COMPONENTS_KEY = "components";
    public static final String STICKER_IDS_KEY = "sticker_ids";
    public static final String ATTACHMENTS_KEY = "attachments";
    public static final String FLAGS_KEY = "flags";

    protected final @Nullable String content;
    protected final @Nullable Nonce nonce;
    protected final @Nullable Boolean tts;
    protected final @Nullable Embed[] embeds;
    protected final @Nullable AllowedMentions allowedMentions;
    protected final @Nullable MessageReference messageReference;
    protected final @Nullable Component[] components;
    protected final @Nullable String[] stickerIds;
    protected final @Nullable Attachment[] attachments;
    protected final @Nullable Long flags;

    /**
     * @param content          pure text content of the message
     * @param nonce            Can be used to verify a message was sent (up to 25 characters). Value will appear in the {@link GatewayEvent#MESSAGE_CREATE Message Create event}.
     * @param tts              whether this message is text to speech
     * @param embeds           {@link Embed embeds} for this message
     * @param allowedMentions  {@link AllowedMentions}. if {@code null}, Discord will generate the mentions of the content
     * @param messageReference {@link MessageReference}, include to make your message a reply
     * @param components       the {@link Component components} to include with the message
     * @param stickerIds       IDs of up to 3 stickers in the server to send in the message
     * @param attachments      attachment objects with filename and description
     * @param flags            {@link me.linusdev.lapi.api.objects.enums.MessageFlag message flags} combined as a bitfield (only {@link me.linusdev.lapi.api.objects.enums.MessageFlag#SUPPRESS_EMBEDS} can be set)
     */
    public MessageTemplate(@Nullable String content, @Nullable Nonce nonce, @Nullable Boolean tts, @Nullable Embed[] embeds,
                           @Nullable AllowedMentions allowedMentions, @Nullable MessageReference messageReference, @Nullable Component[] components, @Nullable String[] stickerIds, @Nullable Attachment[] attachments, @Nullable Long flags){
        this.content = content;
        this.nonce = nonce;
        this.tts = tts;
        this.embeds = embeds;
        this.allowedMentions = allowedMentions;
        this.messageReference = messageReference;
        this.components = components;
        this.stickerIds = stickerIds;
        this.attachments = attachments;
        this.flags = flags;
    }

    @Override
    public FilePart[] getFileParts() {
        if(attachments == null) return new FilePart[0];

        ArrayList<FilePart> fileParts = new ArrayList<>();
        for(Attachment attachment : attachments){
            if(attachment instanceof FilePart){
                fileParts.add((FilePart) attachment);
            }
        }
        return fileParts.toArray(new FilePart[0]);
    }

    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(9);

        data.addIfNotNull(CONTENT_KEY, content);
        data.addIfNotNull(TTS_KEY, tts);
        data.addIfNotNull(EMBEDS_KEY, embeds);
        data.addIfNotNull(ALLOWED_MENTIONS_KEY, allowedMentions);
        data.addIfNotNull(MESSAGE_REFERENCE_KEY, messageReference);
        data.addIfNotNull(COMPONENTS_KEY, components);
        data.addIfNotNull(STICKER_IDS_KEY, stickerIds);
        data.addIfNotNull(ATTACHMENTS_KEY, attachments);
        data.addIfNotNull(FLAGS_KEY, flags);

        return data;
    }
}

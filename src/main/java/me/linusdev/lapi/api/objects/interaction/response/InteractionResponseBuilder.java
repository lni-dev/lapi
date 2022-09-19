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

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.communication.retriever.response.LApiHttpResponse;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.interaction.response.data.Autocomplete;
import me.linusdev.lapi.api.objects.interaction.response.data.AutocompleteBuilder;
import me.linusdev.lapi.api.objects.interaction.response.data.Modal;
import me.linusdev.lapi.api.templates.abstracts.Template;
import me.linusdev.lapi.api.templates.message.MessageTemplate;
import me.linusdev.lapi.api.templates.message.builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InteractionResponseBuilder implements HasLApi {

    private final @NotNull LApi lApi;

    private final @NotNull Interaction interaction;

    private @Nullable InteractionCallbackType type = null;
    private @Nullable Template data =  null;

    public InteractionResponseBuilder(@NotNull LApi lApi, @NotNull Interaction interaction) {
        this.interaction = interaction;
        this.lApi = lApi;
    }

    public @NotNull Queueable<LApiHttpResponse> getQueueable(){
        return lApi.getRequestFactory().createInteractionResponse(interaction.getId(), interaction.getToken(), build());
    }

    /**
     * ACK a Ping
     * @return this
     */
    public InteractionResponseBuilder pong(){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.PONG);
        type = InteractionCallbackType.PONG;
        return this;
    }

    /**
     * respond to an interaction with a message.<br>
     *
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     *
     * @return this
     */
    public InteractionResponseBuilder channelMessageWithSource(@NotNull MessageTemplate message){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE);
        type = InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE;
        data = message;
        return this;
    }

    /**
     * respond to an interaction with a message.<br>
     *
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     *
     * @return this
     */
    public InteractionResponseBuilder channelMessageWithSource(@NotNull Consumer<MessageBuilder> msgBuilderConsumer, boolean check){
        MessageBuilder builder = new MessageBuilder(lApi);
        msgBuilderConsumer.accept(builder);
        return channelMessageWithSource(builder.build(check));
    }

    /**
     * ACK an interaction and edit a response later, the user sees a loading state
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder deferredChannelMessageWithSource(@NotNull MessageTemplate message){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE);
        type = InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE;
        data = message;
        return this;
    }

    /**
     * ACK an interaction and edit a response later, the user sees a loading state
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder deferredChannelMessageWithSource(@NotNull Consumer<MessageBuilder> msgBuilderConsumer, boolean check){
        MessageBuilder builder = new MessageBuilder(lApi);
        msgBuilderConsumer.accept(builder);
        return deferredChannelMessageWithSource(builder.build(check));
    }

    /**
     * for components, ACK an interaction and edit the original message later; the user does not see a loading state
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder deferredUpdateMessage(@NotNull MessageTemplate message){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.DEFERRED_UPDATE_MESSAGE);
        type = InteractionCallbackType.DEFERRED_UPDATE_MESSAGE;
        data = message;
        return this;
    }

    /**
     * for components, ACK an interaction and edit the original message later; the user does not see a loading state
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder deferredUpdateMessage(@NotNull Consumer<MessageBuilder> msgBuilderConsumer, boolean check){
        MessageBuilder builder = new MessageBuilder(lApi);
        msgBuilderConsumer.accept(builder);
        return deferredUpdateMessage(builder.build(check));
    }

    /**
     * for components, edit the message the component was attached to.<br>
     * Only valid for component-based interaction.<br>
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder updateMessage(@NotNull MessageTemplate message){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.UPDATE_MESSAGE);
        type = InteractionCallbackType.UPDATE_MESSAGE;
        data = message;
        return this;
    }

    /**
     * for components, edit the message the component was attached to.<br>
     * Only valid for component-based interaction.<br>
     * Not all message fields are currently supported,
     * see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-messages" target="_top">here</a>
     * @return this
     */
    public InteractionResponseBuilder updateMessage(@NotNull Consumer<MessageBuilder> msgBuilderConsumer, boolean check){
        MessageBuilder builder = new MessageBuilder(lApi);
        msgBuilderConsumer.accept(builder);
        return updateMessage(builder.build(check));
    }

    /**
     * respond to an autocomplete interaction with suggested choices
     * @return this
     */
    public InteractionResponseBuilder applicationCommandAutocompleteResult(@NotNull Autocomplete autocomplete){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.APPLICATION_COMMAND_AUTOCOMPLETE_RESULT);
        type = InteractionCallbackType.APPLICATION_COMMAND_AUTOCOMPLETE_RESULT;
        data = autocomplete;
        return this;
    }

    /**
     * respond to an autocomplete interaction with suggested choices
     * @return this
     * @throws LApiIllegalStateException if check is true: see {@link AutocompleteBuilder#check()}
     */
    public InteractionResponseBuilder applicationCommandAutocompleteResult(
            @NotNull Consumer<AutocompleteBuilder> acBuilderConsumer, boolean check){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.APPLICATION_COMMAND_AUTOCOMPLETE_RESULT);
        type = InteractionCallbackType.APPLICATION_COMMAND_AUTOCOMPLETE_RESULT;
        AutocompleteBuilder builder = new AutocompleteBuilder();
        acBuilderConsumer.accept(builder);
        data = builder.build(check);
        return this;
    }

    /**
     * respond to an interaction with a popup modal<br>
     * Not available for MODAL_SUBMIT and PING interactions.
     * @return this
     */
    public InteractionResponseBuilder modal(@NotNull Modal modal){
        if(type != null)
            throw new UnsupportedOperationException("type is already set to " + type
                    + " and cannot be set to " + InteractionCallbackType.MODAL);

        type = InteractionCallbackType.MODAL;
        data = modal;
        return this;
    }

    /**
     *
     * @return built {@link InteractionResponse}
     */
    public @NotNull InteractionResponse build() {
        if(type == null) throw new UnsupportedOperationException("A type must be chosen!");

        return new InteractionResponse(type, data);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

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

package me.linusdev.lapi.api.objects.component.button;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.component.ComponentType;
import me.linusdev.lapi.api.objects.emoji.EmojiObject;
import me.linusdev.lapi.api.objects.emoji.abstracts.Emoji;
import me.linusdev.lapi.api.objects.interaction.Interaction;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.component.ComponentLimits;
import me.linusdev.lapi.api.objects.component.actionrow.ActionRow;
import me.linusdev.lapi.api.objects.component.selectmenu.SelectMenu;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h2 style="margin-bottom:0;padding-bottom:0">
 *     <a href="https://discord.com/developers/docs/interactions/message-components#buttons" target="_top">
 *         Buttons
 *     </a>
 * </h2>
 * <p>
 *     Buttons are interactive components that render on messages. They can be clicked by users, and send an {@link Interaction interaction} to your app when clicked.
 * </p>
 *
 *  <ul>
 *      <li>
 *          Buttons must be sent inside an {@link ActionRow Action Row}
 *      </li>
 *      <li>
 *          An {@link ActionRow Action Row} can contain up to 5 buttons
 *      </li>
 *      <li>
 *          An {@link ActionRow Action Row} containing buttons cannot also contain a {@link SelectMenu select menu}
 *      </li>
 *  </ul>
 *
 *  <p>
 *      Buttons come in a variety of styles to convey different types of actions. These styles also define what fields are valid for a button.
 *  </p>
 *  <ul>
 *      <li>
 *          Non-link buttons <b>must</b> have a {@link #getCustomId() custom_id}, and cannot have a {@link #getUrl() url}
 *      </li>
 *      <li>
 *          Link buttons must have a {@link #getUrl() url}, and cannot have a {@link #getCustomId() custom_id}
 *      </li>
 *      <li>
 *          Link buttons do not send an {@link Interaction interaction} to your app when clicked
 *      </li>
 *  </ul>
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#buttons" target="_top">
 *          Buttons
 *      </a>
 */
public class Button implements Component, HasLApi {

    private final @NotNull ComponentType type;
    private final @NotNull ButtonStyle style;
    private final @Nullable String label;
    private final @Nullable Emoji emoji;
    private final @Nullable String customId;
    private final @Nullable String url;
    private final @Nullable Boolean disabled;

    private final @NotNull LApi lApi;

    /**
     *
     * @param type {@link ComponentType#BUTTON 2} for a button
     * @param style one of {@link ButtonStyle button styles}
     * @param label text that appears on the button, max {@value ComponentLimits#LABEL_MAX_CHARS} characters
     * @param emoji partial {@link Emoji emoji} with name, id, and animated
     * @param customId a developer(yes, YOU)-defined identifier for the button, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     * @param url a url for link-style buttons
     * @param disabled whether the button is disabled (default false)
     */
    public Button(@NotNull LApi lApi, @NotNull ComponentType type, @NotNull ButtonStyle style, @Nullable String label, @Nullable Emoji emoji,
                  @Nullable String customId, @Nullable String url, @Nullable Boolean disabled){
        this.lApi = lApi;
        this.type = type;
        this.style = style;
        this.label = label;
        this.emoji = emoji;
        this.customId = customId;
        this.url = url;
        this.disabled = disabled;
    }

    /**
     *
     * @param data with required fields
     * @return {@link Button}
     * @throws InvalidDataException if {@link #TYPE_KEY} or {@link #STYLE_KEY}
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable Button fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        Number type = (Number) data.get(TYPE_KEY);
        Number style = (Number)  data.get(STYLE_KEY);
        String label = (String) data.get(LABEL_KEY);
        SOData emoji = (SOData) data.get(EMOJI_KEY);
        String customId = (String) data.get(CUSTOM_ID_KEY);
        String url = (String) data.get(URL_KEY);
        Boolean disabled = (Boolean) data.get(DISABLED_KEY);

        if(type == null || style == null){
            InvalidDataException.throwException(data, null, Button.class, new Object[]{type, style}, new String[]{TYPE_KEY, STYLE_KEY});
            return null; // this will never happen, because above method will throw an Exception
        }

        return new Button(lApi, ComponentType.fromValue(type.intValue()), ButtonStyle.fromValue(style.intValue()), label,
                emoji == null ? null : EmojiObject.fromData(lApi, emoji), customId, url, disabled);
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * 	one of {@link ButtonStyle button styles}
     */
    public @NotNull ButtonStyle getStyle() {
        return style;
    }

    /**
     * text that appears on the button, max {@value ComponentLimits#LABEL_MAX_CHARS} characters
     */
    public @Nullable String getLabel() {
        return label;
    }

    /**
     * partial {@link Emoji} or {@link EmojiObject} with:
     * <br> {@link Emoji#getId() id}, {@link Emoji#getName() name}, and {@link EmojiObject#isAnimated() animated}
     */
    public @Nullable Emoji getEmoji() {
        return emoji;
    }

    /**
     * a developer-defined identifier for the component, max {@value ComponentLimits#CUSTOM_ID_MAX_CHARS} characters
     */
    public @Nullable String getCustomId() {
        return customId;
    }

    /**
     * a url for link-style buttons
     */
    public @Nullable String getUrl() {
        return url;
    }

    /**
     * whether the component is disabled, default false
     * @see #isDisabled()
     */
    public @Nullable Boolean getDisabled() {
        return disabled;
    }

    /**
     * Whether this {@link Button} is disabled.
     *
     * @return {@code false} if {@link #getDisabled()} is {@code null}, {@link #getDisabled()} otherwise
     */
    public Boolean isDisabled(){
        return !(disabled == null) && disabled;
    }

    /**
     *
     * @return {@link SOData} representing this {@link Button}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(7);

        data.add(TYPE_KEY, type);
        data.add(STYLE_KEY, style);
        if(label != null) data.add(LABEL_KEY, label);
        if(emoji != null) data.add(EMOJI_KEY, emoji);
        if(customId != null) data.add(CUSTOM_ID_KEY, customId);
        if(url != null) data.add(URL_KEY, url);
        if(disabled != null) data.add(DISABLED_KEY, disabled);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

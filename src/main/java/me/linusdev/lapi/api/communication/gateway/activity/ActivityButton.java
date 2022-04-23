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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.message.component.ComponentLimits;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * <p>
 *     When received over the gateway, the buttons field is an array of strings, which are the button labels.
 *     Bots cannot access a user's activity button URLs. When sending, the buttons field must be an array of the below
 *     object:
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-buttons" target="_top">Activity Buttons</a>
 */
public class ActivityButton implements Datable {

    public static final String LABEL_KEY = "label";
    public static final String URL_KEY = "url";

    private final @NotNull String label;
    private final @NotNull String url;

    /**
     *
     * @param label text that appears on the button, max {@value ComponentLimits#LABEL_MAX_CHARS} characters
     * @param url the url opened when clicking the button (1-{@value ComponentLimits#URL_MAX_CHARS} characters)
     */
    public ActivityButton(@NotNull String label, @NotNull String url) {
        this.label = label;
        this.url = url;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link ActivityButton}
     * @throws InvalidDataException if {@link #LABEL_KEY} or {@link #URL_KEY} are missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable ActivityButton fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String label = (String) data.get(LABEL_KEY);
        String url = (String) data.get(URL_KEY);

        if(label == null || url == null){
            InvalidDataException.throwException(data, null, ActivityButton.class,
                    new Object[]{label, url},
                    new String[]{LABEL_KEY, URL_KEY});
        }

        //noinspection ConstantConditions
        return new ActivityButton(label, url);
    }

    /**
     * text that appears on the button, max {@value ComponentLimits#LABEL_MAX_CHARS} characters
     */
    public @NotNull String getLabel() {
        return label;
    }

    /**
     *
     * the url opened when clicking the button (1-{@value ComponentLimits#URL_MAX_CHARS} characters)
     */
    public @NotNull String getUrl() {
        return url;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(LABEL_KEY, label);
        data.add(URL_KEY, url);

        return data;
    }
}

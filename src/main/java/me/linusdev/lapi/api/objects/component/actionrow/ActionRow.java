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

package me.linusdev.lapi.api.objects.component.actionrow;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.component.ComponentType;
import me.linusdev.lapi.api.objects.component.Component;
import me.linusdev.lapi.api.objects.component.ComponentLimits;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>
 *   An Action Row is a non-interactive container component for other types of components.
 *   It has a {@link #getType() type}: {@link ComponentType#ACTION_ROW 1} and a sub-array of
 *   {@link Component components} of other types.
 * </p>
 * <ul>
 *     <li>
 *          You can have up to {@value ComponentLimits#ACTION_ROW_MAX_CHILD_COMPONENTS} Action Rows per message
 *     </li>
 *     <li>
 *          An Action Row cannot contain another Action Row
 *     </li>
 * </ul>
 *
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#action-rows" target="_top">
 *          Action Rows
 *     </a>
 */
public class ActionRow implements Datable, Component, HasLApi {

    private final @NotNull ComponentType type;
    private final @NotNull Component[] components; //TODO: check if this is really @NotNull

    private final @NotNull LApi lApi;

    /**
     *
     * @param type {@link ComponentType component type}
     * @param components a list of child components
     */
    public ActionRow(@NotNull LApi lApi, @NotNull ComponentType type, @NotNull Component[] components){
        this.lApi = lApi;
        this.type = type;
        this.components = components;
    }

    /**
     *
     * @param data {@link SOData} with required fields
     * @return {@link ActionRow}
     * @throws InvalidDataException if {@link #TYPE_KEY} or {@link #COMPONENTS_KEY} are missing
     */
    public static @NotNull ActionRow fromData(@NotNull LApi lApi, @NotNull SOData data) throws InvalidDataException {
        Number type = (Number) data.get(TYPE_KEY);
        List<Object> componentsData = data.getList(COMPONENTS_KEY);

        if(type == null || componentsData == null){
            InvalidDataException.throwException(data, null, ActionRow.class,
                    new Object[]{type, componentsData}, new String[]{TYPE_KEY, COMPONENTS_KEY});
            return null; //this will never happen, because above method will throw an Exception
        }

        Component[] components = new Component[componentsData.size()];
        int i = 0;
        for(Object o : componentsData){
            SOData d = (SOData) o;
            components[i++] = Component.fromData(lApi, d);
        }

        return new ActionRow(lApi, ComponentType.fromValue(type.intValue()), components);
    }

    @Override
    public @NotNull ComponentType getType() {
        return type;
    }

    /**
     * a list of child components
     */
    public @NotNull Component[] getComponents() {
        return components;
    }

    /**
     *
     * @return {@link SOData} representing this {@link ActionRow}
     */
    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(2);

        data.add(TYPE_KEY, type);
        data.add(COMPONENTS_KEY, components);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

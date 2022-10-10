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

package me.linusdev.lapi.api.objects.component;

/**
 * Component Limits
 * @see <a href="https://discord.com/developers/docs/interactions/message-components#component-object-component-structure" target="_top">
 *      Component Strcuture
 *     </a>
 */
public final class ComponentLimits {

    public static final int LABEL_MAX_CHARS = 80;
    public static final int URL_MAX_CHARS = 512; //Only in ActivityButton AFAIK
    public static final int CUSTOM_ID_MAX_CHARS = 100;
    public static final int SELECT_OPTIONS_MAX = 25;
    public static final int PLACEHOLDER_MAX_CHARS = 100;
    public static final int MIN_VALUE_FIELD_MIN = 0;
    public static final int MIN_VALUE_FIELD_MAX = 25;
    public static final int MAX_VALUE_FIELD_MAX = 25;
    public static final int ACTION_ROW_MAX_CHILD_COMPONENTS = 5;

    //SelectOption Limits
    public static final int SO_LABEL_MAX_CHARS = 100;
    public static final int SO_VALUE_MAX_CHARS = 100;
    public static final int SO_DESCRIPTION_MAX_CHARS = 100;
}

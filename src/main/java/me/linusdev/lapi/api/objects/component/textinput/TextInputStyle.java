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

package me.linusdev.lapi.api.objects.component.textinput;

import org.jetbrains.annotations.NotNull;

public enum TextInputStyle {

    UNKNOWN(0),

    /**
     * Single-line input
     */
    SHORT(1),

    /**
     * Multi-line input
     */
    PARAGRAPH(2),
    ;

    private final int value;

    TextInputStyle(int value) {
        this.value = value;
    }

    public static @NotNull TextInputStyle fromValue(int value) {

        for(TextInputStyle style : TextInputStyle.values()) {
            if(style.value == value)
                return style;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }
}

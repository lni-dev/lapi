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

package me.linusdev.lapi.api.objects.user;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 *
 * <p>
 *     Premium types denote the level of premium a user has. Visit the <a href="https://discord.com/nitro" target="_top">Nitro</a> page to learn more about the premium plans we currently offer.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object-premium-types" target="_top">Premium Types</a>
 */
public enum PremiumType implements SimpleDatable {
    UNKNOWN(-1),
    NONE(0),
    NITRO_CLASSIC(1),
    NITRO(2),
    ;

    private final int value;

    PremiumType(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link PremiumType} matching given value or {@link #UNKNOWN} if none matches
     */
    public static @NotNull PremiumType fromValue(int value){
        for(PremiumType type : PremiumType.values()){
            if(type.value == value) return type;
        }

        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object simplify() {
        return value;
    }
}

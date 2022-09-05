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

package me.linusdev.lapi.api.objects.application.team;

import me.linusdev.data.SimpleDatable;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://discord.com/developers/docs/topics/teams#data-models-membership-state-enum" target="_top">Membership State Enum</a>
 */
public enum MembershipState implements SimpleDatable {
    INVITED(1),
    ACCEPTED(2),
    ;

    private final int value;

    MembershipState(int value){
        this.value = value;
    }

    /**
     *
     * @param value int
     * @return {@link #ACCEPTED} if value equals 1, {@link #INVITED} otherwise
     */
    public static @NotNull MembershipState fromValue(int value){
        if(value == ACCEPTED.value) return ACCEPTED;
        return INVITED;
    }

    @Override
    public Object simplify() {
        return value;
    }
}

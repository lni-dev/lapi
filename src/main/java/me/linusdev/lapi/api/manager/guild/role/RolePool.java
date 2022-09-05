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

package me.linusdev.lapi.api.manager.guild.role;

import me.linusdev.lapi.api.objects.role.Role;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RolePool {

    /**
     * @return A Collection of {@link Role roles}, which are managed by this {@link RoleManager}.
     * This collection should be backed by the {@link RoleManager}, meaning that changes on the {@link RoleManager} will
     * have direct effect to the returned {@link Collection}.
     */
    @Contract(pure = false)
    @NotNull Collection<Role> getRoles();
}

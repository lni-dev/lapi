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

package me.linusdev.lapi.api.objects.invite;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.timestamp.ISO8601Timestamp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Extra information about an invite, will extend the {@link Invite invite} object.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/resources/invite#invite-metadata-object" target="_top">Invite Metadata Object</a>
 */
public class InviteMetadata {

    public static final String USES_KEY = "uses";
    public static final String MAX_USES_KEY = "max_uses";
    public static final String MAX_AGE_KEY = "max_age";
    public static final String TEMPORARY_KEY = "temporary";
    public static final String CREATED_AT_KEY = "created_at";

    private final int uses;
    private final int maxUses;
    private final int maxAge;
    private final boolean temporary;
    private final @NotNull ISO8601Timestamp createdAt;

    /**
     * @param uses      number of times this invite has been used
     * @param maxUses   max number of times this invite can be used
     * @param maxAge    duration (in seconds) after which the invite expires
     * @param temporary whether this invite only grants temporary membership
     * @param createdAt when this invite was created
     */
    public InviteMetadata(int uses, int maxUses, int maxAge, boolean temporary, @NotNull ISO8601Timestamp createdAt) {
        this.uses = uses;
        this.maxUses = maxUses;
        this.maxAge = maxAge;
        this.temporary = temporary;
        this.createdAt = createdAt;
    }

    /**
     * @param data {@link SOData} with required fields or {@code null}
     * @return {@link InviteMetadata}
     * @throws InvalidDataException if {@link #USES_KEY}  {@link #MAX_USES_KEY}, {@link #MAX_AGE_KEY}, {@link #TEMPORARY_KEY} or {@link #CREATED_AT_KEY} are missing ir {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable InviteMetadata fromData(@Nullable SOData data) throws InvalidDataException {
        if (data == null) return null;
        Number uses = (Number) data.get(USES_KEY);
        Number maxUses = (Number) data.get(MAX_USES_KEY);
        Number maxAge = (Number) data.get(MAX_AGE_KEY);
        Boolean temporary = (Boolean) data.get(TEMPORARY_KEY);
        String createdAt = (String) data.get(CREATED_AT_KEY);

        if (uses == null || maxUses == null || maxAge == null || temporary == null || createdAt == null) {
            InvalidDataException.throwException(data, null, InviteMetadata.class,
                    new Object[]{uses, maxUses, maxAge, temporary, createdAt},
                    new String[]{USES_KEY, MAX_USES_KEY, MAX_AGE_KEY, TEMPORARY_KEY, CREATED_AT_KEY});
        }

        //noinspection ConstantConditions: handled by above if
        return new InviteMetadata(uses.intValue(), maxUses.intValue(), maxAge.intValue(), temporary, ISO8601Timestamp.fromString(createdAt));
    }

    /**
     * number of times this invite has been used
     */
    public int getUses() {
        return uses;
    }

    /**
     * max number of times this invite can be used
     */
    public int getMaxUses() {
        return maxUses;
    }

    /**
     * duration (in seconds) after which the invite expires
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * whether this invite only grants temporary membership
     */
    public boolean isTemporary() {
        return temporary;
    }

    /**
     * when this invite was created
     */
    public @NotNull ISO8601Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * this doesn't implement Datable, but instead has a function to extend an already existing Data by its values
     *
     * @param data {@link SOData} to extend
     * @return extended {@link SOData}
     */
    public @NotNull SOData extendData(@NotNull SOData data) {

        data.add(USES_KEY, uses);
        data.add(MAX_USES_KEY, maxUses);
        data.add(MAX_AGE_KEY, maxAge);
        data.add(TEMPORARY_KEY, temporary);
        data.add(CREATED_AT_KEY, createdAt);

        return data;
    }

}

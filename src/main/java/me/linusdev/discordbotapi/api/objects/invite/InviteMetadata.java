package me.linusdev.discordbotapi.api.objects.invite;

import me.linusdev.data.Data;
import me.linusdev.discordbotapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.discordbotapi.api.objects.timestamp.ISO8601Timestamp;
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
     * @param data {@link Data} with required fields or {@code null}
     * @return {@link InviteMetadata}
     * @throws InvalidDataException if {@link #USES_KEY}  {@link #MAX_USES_KEY}, {@link #MAX_AGE_KEY}, {@link #TEMPORARY_KEY} or {@link #CREATED_AT_KEY} are missing ir {@code null}
     */
    public static @Nullable InviteMetadata fromData(@Nullable Data data) throws InvalidDataException {
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
     * @param data {@link Data} to extend
     * @return extended {@link Data}
     */
    public @NotNull Data extendData(@NotNull Data data) {

        data.add(USES_KEY, uses);
        data.add(MAX_USES_KEY, maxUses);
        data.add(MAX_AGE_KEY, maxAge);
        data.add(TEMPORARY_KEY, temporary);
        data.add(CREATED_AT_KEY, createdAt);

        return data;
    }

}

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

package me.linusdev.lapi.api.communication.gateway.activity;

import me.linusdev.data.Datable;
import me.linusdev.data.functions.Converter;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <p>
 *     Bots are only able to send name, type, and optionally url.
 * </p>
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-structure" target="_top">Activity Strcuture</a>
 */
public class Activity implements Datable {

    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String URL_KEY = "url";
    public static final String CREATED_AT_KEY = "created_at";
    public static final String TIMESTAMPS_KEY = "timestamps";
    public static final String APPLICATION_ID_KEY = "application_id";
    public static final String DETAILS_KEY = "details";
    public static final String STATE_KEY = "state";
    public static final String EMOJI_KEY = "emoji";
    public static final String PARTY_KEY = "party";
    public static final String ASSETS_KEY = "assets";
    public static final String SECRETS_KEY = "secrets";
    public static final String INSTANCE_KEY = "instance";
    public static final String FLAGS_KEY = "flags";
    public static final String BUTTONS_KEY = "buttons";

    private final @NotNull String name;
    private final @NotNull ActivityType type;
    private final @Nullable String url;
    private final @Nullable Long createdAt; //This should not be null, but bots are not allowed to send it, so it has to be null when we send an Activity to discord....
    private final @Nullable ActivityTimestamps timestamps;
    private final @Nullable Snowflake applicationId;
    private final @Nullable String details;
    private final @Nullable String state;
    private final @Nullable ActivityEmoji emoji;
    private final @Nullable ActivityParty party;
    private final @Nullable ActivityAssets assets;
    private final @Nullable ActivitySecrets secrets;
    private final @Nullable Boolean instance;
    private final @Nullable Integer flags;
    private final @Nullable ArrayList<ActivityButton> buttons;

    /**
     *
     * @param name the activity's name
     * @param type {@link ActivityType activity type}
     * @param url stream url, is validated when type is {@link ActivityType#STREAMING}
     * @param createdAt unix timestamp (in milliseconds) of when the activity was added to the user's session
     * @param timestamps unix timestamps for start and/or end of the game
     * @param applicationId application id for the game
     * @param details what the player is currently doing
     * @param state the user's current party status
     * @param emoji the emoji used for a custom status
     * @param party information for the current party of the player
     * @param assets images for the presence and their hover texts
     * @param secrets secrets for Rich Presence joining and spectating
     * @param instance whether or not the activity is an instanced game session
     * @param flags activity flags OR d together, describes what the payload includes
     * @param buttons the custom buttons shown in the Rich Presence (max 2)
     */
    public Activity(@NotNull String name, @NotNull ActivityType type, @Nullable String url, @Nullable Long createdAt, @Nullable ActivityTimestamps timestamps, @Nullable Snowflake applicationId, @Nullable String details, @Nullable String state, @Nullable ActivityEmoji emoji, @Nullable ActivityParty party, @Nullable ActivityAssets assets, @Nullable ActivitySecrets secrets, @Nullable Boolean instance, @Nullable Integer flags, @Nullable ArrayList<ActivityButton> buttons) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.createdAt = createdAt;
        this.timestamps = timestamps;
        this.applicationId = applicationId;
        this.details = details;
        this.state = state;
        this.emoji = emoji;
        this.party = party;
        this.assets = assets;
        this.secrets = secrets;
        this.instance = instance;
        this.flags = flags;
        this.buttons = buttons;
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link Activity}
     * @throws InvalidDataException if {@link #NAME_KEY} or {@link #TYPE_KEY} are missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable Activity fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String name = (String) data.get(NAME_KEY);
        Number type = (Number) data.get(TYPE_KEY);
        String url = (String) data.get(URL_KEY);
        Number createdAt = (Number) data.get(CREATED_AT_KEY);

        ActivityTimestamps timestamps = data.getAndConvert(TIMESTAMPS_KEY,
                (Converter<SOData, ActivityTimestamps>) ActivityTimestamps::fromData);

        String applicationId = (String) data.get(APPLICATION_ID_KEY);
        String details = (String) data.get(DETAILS_KEY);
        String state = (String) data.get(STATE_KEY);

        ActivityEmoji emoji = data.getAndConvertWithException(EMOJI_KEY, ActivityEmoji::fromData, null);

        ActivityParty party = data.getAndConvert(PARTY_KEY,
                (Converter<SOData, ActivityParty>) ActivityParty::fromData);

        ActivityAssets assets = data.getAndConvert(ASSETS_KEY,
                (Converter<SOData, ActivityAssets>) ActivityAssets::fromData);

        ActivitySecrets secrets = data.getAndConvert(SECRETS_KEY,
                (Converter<SOData, ActivitySecrets>) ActivitySecrets::fromData);

        Boolean instance = (Boolean) data.get(INSTANCE_KEY);

        Number flags = (Number) data.get(FLAGS_KEY);

        ArrayList<ActivityButton> buttons = data.getListAndConvertWithException(BUTTONS_KEY, ActivityButton::fromObject);

        if(name == null || type == null){
            InvalidDataException.throwException(data, null, Activity.class,
                    new Object[]{name, type},
                    new String[]{NAME_KEY, TYPE_KEY});
        }

        //noinspection ConstantConditions
        return new Activity(name, ActivityType.fromValue(type.intValue()), url, createdAt == null ? null : createdAt.longValue(),
                timestamps, Snowflake.fromString(applicationId), details, state, emoji, party, assets, secrets, instance,
                flags == null ? null : flags.intValue(), buttons);
    }

    /**
     * the activity's name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * {@link ActivityType activity type}
     */
    public @NotNull ActivityType getType() {
        return type;
    }

    /**
     * stream url, is validated when type is 1
     */
    public @Nullable String getUrl() {
        return url;
    }

    /**
     * unix timestamp (in milliseconds) of when the activity was added to the user's session
     */
    public @Nullable Long getCreatedAt() {
        return createdAt;
    }

    /**
     * unix timestamps for start and/or end of the game
     */
    public @Nullable ActivityTimestamps getTimestamps() {
        return timestamps;
    }

    /**
     * application id for the game
     */
    public @Nullable Snowflake getApplicationId() {
        return applicationId;
    }

    /**
     * what the player is currently doing
     */
    public @Nullable String getDetails() {
        return details;
    }

    /**
     * the user's current party status
     */
    public @Nullable String getState() {
        return state;
    }

    /**
     * the emoji used for a custom status
     */
    public @Nullable ActivityEmoji getEmoji() {
        return emoji;
    }

    /**
     * information for the current party of the player
     */
    public @Nullable ActivityParty getParty() {
        return party;
    }

    /**
     * images for the presence and their hover texts
     */
    public @Nullable ActivityAssets getAssets() {
        return assets;
    }

    /**
     * secrets for Rich Presence joining and spectating
     */
    public @Nullable ActivitySecrets getSecrets() {
        return secrets;
    }

    /**
     * whether or not the activity is an instanced game session
     */
    public @Nullable Boolean getInstance() {
        return instance;
    }

    /**
     * activity flags OR d together, describes what the payload includes
     */
    public @Nullable Integer getFlags() {
        return flags;
    }

    /**
     *
     * @return {@link ActivityFlag} array
     */
    public @NotNull ActivityFlag[] getFlagsAsArray(){
        if(flags == null) return new ActivityFlag[0];
        return ActivityFlag.fromInt(flags);
    }

    /**
     * the custom buttons shown in the Rich Presence (max 2)
     */
    public @Nullable ArrayList<ActivityButton> getButtons() {
        return buttons;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(15);

        data.add(NAME_KEY, name);
        data.add(TYPE_KEY, type);
        data.addIfNotNull(URL_KEY, url);
        data.addIfNotNull(CREATED_AT_KEY, createdAt);
        data.addIfNotNull(TIMESTAMPS_KEY, timestamps);
        data.addIfNotNull(APPLICATION_ID_KEY, applicationId);
        data.addIfNotNull(DETAILS_KEY, details);
        data.addIfNotNull(STATE_KEY, state);
        data.addIfNotNull(EMOJI_KEY, emoji);
        data.addIfNotNull(PARTY_KEY, party);
        data.addIfNotNull(ASSETS_KEY, assets);
        data.addIfNotNull(SECRETS_KEY, secrets);
        data.addIfNotNull(INSTANCE_KEY, instance);
        data.addIfNotNull(FLAGS_KEY, flags);
        data.addIfNotNull(BUTTONS_KEY, buttons);

        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return name.equals(activity.name) && type == activity.type && Objects.equals(url, activity.url) && Objects.equals(createdAt, activity.createdAt) && Objects.equals(timestamps, activity.timestamps) && Objects.equals(applicationId, activity.applicationId) && Objects.equals(details, activity.details) && Objects.equals(state, activity.state) && Objects.equals(emoji, activity.emoji) && Objects.equals(party, activity.party) && Objects.equals(assets, activity.assets) && Objects.equals(secrets, activity.secrets) && Objects.equals(instance, activity.instance) && Objects.equals(flags, activity.flags) && Objects.equals(buttons, activity.buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, url, createdAt, timestamps, applicationId, details, state, emoji, party, assets, secrets, instance, flags, buttons);
    }
}

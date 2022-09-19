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

package me.linusdev.lapi.api.objects.integration;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/guild#integration-application-object" target="_top">Integration Application</a>
 */
public class IntegrationApplication implements Datable, HasLApi, SnowflakeAble {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String ICON_KEY = "icon";
    public static final String DESCRIPTION_KEY = "description";
    public static final String SUMMARY_KEY = "summary";
    public static final String BOT_KEY = "bot";

    private final @NotNull LApi lApi;

    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final @Nullable String iconHash;
    private final @NotNull String description;
    private final @NotNull String summary;
    private final @Nullable User bot;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the id of the app
     * @param name the name of the app
     * @param iconHash the icon hash of the app
     * @param description the description of the app
     * @param summary the summary of the app
     * @param bot the bot associated with this application
     */
    public IntegrationApplication(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name, @Nullable String iconHash, @NotNull String description, @NotNull String summary, @Nullable User bot) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.iconHash = iconHash;
        this.description = description;
        this.summary = summary;
        this.bot = bot;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link SOData} with required fields
     * @return {@link IntegrationApplication}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY}, {@link #DESCRIPTION_KEY} or {@link #SUMMARY_KEY} is missing or {@code null}
     */
    public static @Nullable IntegrationApplication fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String iconHash = (String) data.get(ICON_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        String summary = (String) data.get(SUMMARY_KEY);
        SOData bot = (SOData) data.get(BOT_KEY);

        if(id == null || name == null || description == null || summary == null){
            InvalidDataException.throwException(data, null, IntegrationApplication.class,
                    new Object[]{id, name, description, summary},
                    new String[]{ID_KEY, NAME_KEY, DESCRIPTION_KEY, SUMMARY_KEY});
        }

        //noinspection ConstantConditions
        return new IntegrationApplication(lApi, Snowflake.fromString(id), name, iconHash, description, summary, User.fromData(lApi, bot));
    }

    /**
     * the id as {@link Snowflake} of the app
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the name of the app
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * the icon hash of the app
     */
    public @Nullable String getIconHash() {
        return iconHash;
    }

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofApplicationIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the icon
     */
    public @NotNull CDNImageRetriever getIcon(int desiredSize, @NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This application object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofApplicationIcon(lApi, getId(), getIconHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofApplicationIcon(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link Queueable Queueable} to retrieve the icon
     */
    public @NotNull CDNImageRetriever getIcon(@NotNull AbstractFileType fileType){
        if(getIconHash() == null) throw new IllegalArgumentException("This application object has no icon hash");
        return new CDNImageRetriever(CDNImage.ofApplicationIcon(lApi, getId(), getIconHash(), fileType));
    }

    /**
     * the description of the app
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * the summary of the app
     */
    public @NotNull String getSummary() {
        return summary;
    }

    /**
     * the bot associated with this application
     */
    public @Nullable User getBot() {
        return bot;
    }

    @Override
    public SOData getData() {

        SOData data = SOData.newOrderedDataWithKnownSize(6);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(ICON_KEY, iconHash);
        data.add(DESCRIPTION_KEY, description);
        data.add(SUMMARY_KEY, summary);
        data.addIfNotNull(BOT_KEY, bot);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

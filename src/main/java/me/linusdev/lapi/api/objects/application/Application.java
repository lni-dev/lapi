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

package me.linusdev.lapi.api.objects.application;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.application.team.Team;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @see <a href="https://discord.com/developers/docs/resources/application#application-object" target="_top">Application Object</a>
 */
public class Application implements ApplicationAbstract, Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String ICON_KEY = "icon";
    public static final String DESCRIPTION_KEY = "description";
    public static final String RPC_ORIGINS_KEY = "rpc_origins";
    public static final String BOT_PUBLIC_KEY = "bot_public";
    public static final String BOT_REQUIRE_CODE_GRANT_KEY = "bot_require_code_grant";
    public static final String TERMS_OF_SERVICE_URL_KEY = "terms_of_service_url?";
    public static final String PRIVACY_POLICY_URL_KEY = "privacy_policy_url";
    public static final String OWNER_KEY = "owner";
    public static final String SUMMARY_KEY = "summary";
    public static final String VERIFY_KEY_KEY = "verify_key";
    public static final String TEAM_KEY = "team";
    public static final String GUILD_ID_KEY = "guild_id";
    public static final String PRIMARY_SKU_ID_KEY = "primary_sku_id";
    public static final String SLUG_KEY = "slug";
    public static final String COVER_IMAGE_KEY = "cover_image";
    public static final String FLAGS_KEY = "flags";

    private final @NotNull Snowflake id;
    private final @NotNull String name;
    private final @Nullable String icon;
    private final @NotNull String description;
    private final @Nullable String[] rpcOrigins;
    private final boolean botPublic;
    private final boolean botRequireCodeGrant;
    private final @Nullable String termsOfServiceUrl;
    private final @Nullable String privacyPolicyUrl;
    private final @Nullable User owner;
    private final @Deprecated(forRemoval = true, since = "will be removed in discord api v11") @Nullable String summary;
    private final @NotNull String verifyKey;
    private final @Nullable Team team;
    private final @Nullable Snowflake guildId;
    private final @Nullable Snowflake primarySkuId;
    private final @Nullable String slug;
    private final @Nullable String coverImage;
    private final @Nullable Integer flags;
    private final @Nullable ApplicationFlag[] flagsArray;

    private final @NotNull LApi lApi;

    /**
     *
     * @param id the id of the app
     * @param name the name of the app
     * @param icon the icon hash of the app
     * @param description the description of the app
     * @param rpcOrigins an array of rpc origin urls, if rpc is enabled
     * @param botPublic when false only app owner can join the app's bot to guilds
     * @param botRequireCodeGrant when true the app's bot will only join upon completion of the full oauth2 code grant flow
     * @param termsOfServiceUrl the url of the app's terms of service
     * @param privacyPolicyUrl the url of the app's privacy policy
     * @param owner partial user object containing info on the owner of the application
     * @param summary if this application is a game sold on Discord, this field will be the summary field for the store page of its primary sku
     * @param verifyKey the hex encoded key for verification in interactions and the GameSDK's GetTicket
     * @param team if the application belongs to a team, this will be a list of the members of that team
     * @param guildId if this application is a game sold on Discord, this field will be the guild to which it has been linked
     * @param primarySkuId if this application is a game sold on Discord, this field will be the id of the "Game SKU" that is created, if exists
     * @param slug if this application is a game sold on Discord, this field will be the URL slug that links to the store page
     * @param coverImage the application's default rich presence invite cover image hash
     * @param flags the application's public flags
     */
    public Application(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String name, @Nullable String icon, @NotNull String description, @Nullable String[] rpcOrigins,
                       boolean botPublic, boolean botRequireCodeGrant, @Nullable String termsOfServiceUrl, @Nullable String privacyPolicyUrl,
                       @Nullable User owner, @Nullable String summary, @NotNull String verifyKey, @Nullable Team team, @Nullable Snowflake guildId,
                       @Nullable Snowflake primarySkuId, @Nullable String slug, @Nullable String coverImage, @Nullable Integer flags){
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.rpcOrigins = rpcOrigins;
        this.botPublic = botPublic;
        this.botRequireCodeGrant = botRequireCodeGrant;
        this.termsOfServiceUrl = termsOfServiceUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.owner = owner;
        this.summary = summary;
        this.verifyKey = verifyKey;
        this.team = team;
        this.guildId = guildId;
        this.primarySkuId = primarySkuId;
        this.slug = slug;
        this.coverImage = coverImage;
        this.flags = flags;
        this.flagsArray = flags == null ? null : ApplicationFlag.getFlagsFromInteger(flags);
    }

    /**
     *
     * @param data with required fields
     * @return {@link Application} or {@code null} if data was {@code null}
     * @throws InvalidDataException if (id == null || name == null || description == null || botPublic == null || botRequireCodeGrant == null || summary == null || verifyKey == null) == true
     */
    public static @Nullable Application fromData(@NotNull LApi lApi, @Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;

        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String icon = (String) data.get(ICON_KEY);
        String description = (String) data.get(DESCRIPTION_KEY);
        List<Object> rpcOriginsList = data.getList(RPC_ORIGINS_KEY);
        Boolean botPublic = (Boolean) data.get(BOT_PUBLIC_KEY);
        Boolean botRequireCodeGrant = (Boolean) data.get(BOT_REQUIRE_CODE_GRANT_KEY);
        String tosUrl = (String) data.get(TERMS_OF_SERVICE_URL_KEY);
        String ppUrl = (String) data.get(PRIVACY_POLICY_URL_KEY);
        SOData ownerData = (SOData) data.get(OWNER_KEY);
        String summary = (String) data.get(SUMMARY_KEY);
        String verifyKey = (String) data.get(VERIFY_KEY_KEY);
        SOData teamData = (SOData) data.get(TEAM_KEY);
        String guildId = (String) data.get(GUILD_ID_KEY);
        String primarySkuId = (String) data.get(PRIMARY_SKU_ID_KEY);
        String slug = (String) data.get(SLUG_KEY);
        String coverImage = (String) data.get(COVER_IMAGE_KEY);
        Number flags = (Number) data.get(FLAGS_KEY);

        if(id == null || name == null || description == null || botPublic == null || botRequireCodeGrant == null || verifyKey == null){
            InvalidDataException exception = new InvalidDataException(data, "Cannot create " + Application.class.getSimpleName());

            if(id == null) exception.addMissingFields(ID_KEY);
            if(name == null) exception.addMissingFields(NAME_KEY);
            if(description == null) exception.addMissingFields(DESCRIPTION_KEY);
            if(botPublic == null) exception.addMissingFields(BOT_PUBLIC_KEY);
            if(botRequireCodeGrant == null) exception.addMissingFields(BOT_REQUIRE_CODE_GRANT_KEY);
            if(verifyKey == null) exception.addMissingFields(VERIFY_KEY_KEY);

            throw exception;
        }

        String[] rpcOrigins = null;
        if(rpcOriginsList != null){
            rpcOrigins = new String[rpcOriginsList.size()];
            int i = 0;
            for(Object o : rpcOriginsList){
                rpcOrigins[i++] = (String) o;
            }
        }

        return new Application(lApi, Snowflake.fromString(id), name, icon, description, rpcOrigins, botPublic, botRequireCodeGrant, tosUrl, ppUrl,
                ownerData == null ? null : User.fromData(lApi, ownerData), summary, verifyKey, teamData == null ? null : Team.fromData(lApi, teamData),
                Snowflake.fromString(guildId), Snowflake.fromString(primarySkuId), slug, coverImage, flags == null ? null : flags.intValue());
    }

    /**
     * 	the id as {@link Snowflake} of the app
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
    public @Nullable String getIcon() {
        return icon;
    }

    /**
     * the description of the app
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * an array of rpc origin urls, if rpc is enabled
     */
    public @Nullable String[] getRpcOrigins() {
        return rpcOrigins;
    }

    /**
     * when false only app owner can join the app's bot to guilds
     */
    public boolean isBotPublic() {
        return botPublic;
    }

    /**
     * when true the app's bot will only join upon completion of the full oauth2 code grant flow
     */
    public boolean isBotRequireCodeGrant() {
        return botRequireCodeGrant;
    }

    /**
     * the url of the app's terms of service
     */
    public @Nullable String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    /**
     * the url of the app's privacy policy
     */
    public @Nullable String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    /**
     * partial user object containing info on the owner of the application
     */
    public @Nullable User getOwner() {
        return owner;
    }

    /**
     * if this application is a game sold on Discord, this field will be the summary field for the store page of its primary sku
     */
    @Deprecated(forRemoval = true, since = "will be removed in discord api v11")
    public @Nullable String getSummary() {
        return summary;
    }

    /**
     * the hex encoded key for verification in interactions and the GameSDK's GetTicket
     */
    public @NotNull String getVerifyKey() {
        return verifyKey;
    }

    /**
     * if the application belongs to a team, this will be a list of the members of that team
     */
    public @Nullable Team getTeam() {
        return team;
    }

    /**
     * if this application is a game sold on Discord, this field will be the guild id as {@link Snowflake} to which it has been linked
     */
    public @Nullable Snowflake getGuildIdAsSnowflake() {
        return guildId;
    }

    /**
     * if this application is a game sold on Discord, this field will be the guild id as {@link String} to which it has been linked
     */
    public @Nullable String getGuildId() {
        return guildId == null ? null : guildId.asString();
    }

    /**
     * if this application is a game sold on Discord, this field will be the id as {@link Snowflake} of the "Game SKU" that is created, if exists
     */
    public @Nullable Snowflake getPrimarySkuIdAsSnowflake() {
        return primarySkuId;
    }

    /**
     * if this application is a game sold on Discord, this field will be the id as {@link String} of the "Game SKU" that is created, if exists
     */
    public @Nullable String getPrimarySkuId() {
        return primarySkuId == null ? null : primarySkuId.asString();
    }

    /**
     * if this application is a game sold on Discord, this field will be the URL slug that links to the store page
     */
    public @Nullable String getSlug() {
        return slug;
    }

    /**
     * the application's default rich presence invite cover image hash
     */
    public @Nullable String getCoverImage() {
        return coverImage;
    }

    /**
     * the application's public flags
     * @see #getFlagsArray()
     */
    public @Nullable Integer getFlags() {
        return flags;
    }

    /**
     * the application's public flags
     * @see ApplicationFlag
     */
    public ApplicationFlag[] getFlagsArray() {
        return flagsArray;
    }

    @Override
    public @NotNull SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(17);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        if(icon != null) data.add(ICON_KEY, icon);
        data.add(DESCRIPTION_KEY, description);
        if(rpcOrigins != null) data.add(RPC_ORIGINS_KEY, rpcOrigins);
        data.add(BOT_PUBLIC_KEY, botPublic);
        data.add(BOT_REQUIRE_CODE_GRANT_KEY, botRequireCodeGrant);
        if(termsOfServiceUrl != null) data.add(TERMS_OF_SERVICE_URL_KEY, termsOfServiceUrl);
        if(privacyPolicyUrl != null) data.add(PRIVACY_POLICY_URL_KEY, privacyPolicyUrl);
        if(owner != null) data.add(OWNER_KEY, owner);
        data.add(SUMMARY_KEY, summary);
        data.add(VERIFY_KEY_KEY, verifyKey);
        data.add(TEAM_KEY, team);
        if(guildId != null) data.add(GUILD_ID_KEY, guildId);
        if(primarySkuId != null) data.add(PRIMARY_SKU_ID_KEY, primarySkuId);
        if(slug != null) data.add(SLUG_KEY, slug);
        if(coverImage != null) data.add(COVER_IMAGE_KEY, coverImage);
        if(flags != null) data.add(FLAGS_KEY, flags);

        return data;
    }

    @Override
    public LApi getLApi() {
        return lApi;
    }
}

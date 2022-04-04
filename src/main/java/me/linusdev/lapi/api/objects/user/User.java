/*
 * Copyright  2022 Linus Andera
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

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.communication.cdn.image.CDNImage;
import me.linusdev.lapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.lapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.lapi.api.communication.file.types.AbstractFileType;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.snowflake.Snowflake;
import me.linusdev.lapi.api.objects.snowflake.SnowflakeAble;
import me.linusdev.lapi.api.objects.user.abstracts.BasicUserInformation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a style="margin-bottom:0;padding-bottom:0;font-size:20px;font-weight:'bold';" href="https://discord.com/developers/docs/resources/user#users-resource" target="_top">User Resource</a><br>
 * <p>
 *     Users in Discord are generally considered the base entity. Users can spawn across the entire platform, be members of guilds, participate in text and voice chat, and much more. Users are separated by a distinction of "bot" vs "normal." Although they are similar, bot users are automated users that are "owned" by another user. Unlike normal users, bot users do not have a limitation on the number of Guilds they can be a part of.
 * </p>
 * <br>
 * <a style="margin-bottom:0;padding-bottom:0;font-size:15px;font-weight:'bold';" href="https://discord.com/developers/docs/resources/user#usernames-and-nicknames" target="_top">Usernames and Nicknames</a><br>
 * <p>
 *      Discord enforces the following restrictions for usernames and nicknames:
 * </p>
 *  <ol>
 *      <li>
 *          Names can contain most valid unicode characters. We limit some zero-width and non-rendering characters.
 *      </li>
 *      <li>
 *          Usernames must be between 2 and 32 characters long.
 *      </li>
 *      <li>
 *          Nicknames must be between 1 and 32 characters long.
 *      </li>
 *      <li>
 *          Names are sanitized and trimmed of leading, trailing, and excessive internal whitespace.
 *      </li>
 *  </ol>
 *  <p>
 *      The following restrictions are additionally enforced for usernames:
 *  </p>
 *  <ol>
 *      <li>
 *          Names cannot contain the following substrings: '@', '#', ':', '```'.
 *      </li>
 *      <li>
 *          Names cannot be: 'discordtag', 'everyone', 'here'.
 *      </li>
 *  </ol>
 *
 * @see <a href="https://discord.com/developers/docs/resources/user#users-resource" target="_top">User Resource</a>
 */
public class User implements BasicUserInformation, SnowflakeAble, Datable, HasLApi {
    public static final String ID_KEY = "id";
    public static final String USERNAME_KEY = "username";
    public static final String DISCRIMINATOR_KEY = "discriminator";
    public static final String AVATAR_KEY = "avatar";
    public static final String BOT_KEY = "bot";
    public static final String SYSTEM_KEY = "system";
    public static final String MFA_ENABLED_KEY = "mfa_enabled";
    public static final String BANNER_KEY = "banner";
    public static final String ACCENT_COLOR_KEY = "accent_color";
    public static final String LOCALE_KEY = "locale";
    public static final String VERIFIED_KEY = "verified";
    public static final String EMAIL_KEY = "email";
    public static final String FLAGS_KEY = "flags";
    public static final String PREMIUM_TYPE_KEY = "premium_type";
    public static final String PUBLIC_FLAGS_KEY = "public_flags";

    private final @NotNull Snowflake id;
    private final @NotNull String username;
    private final @NotNull String discriminator;
    private final @Nullable String avatarHash;
    private final @Nullable Boolean bot;
    private final @Nullable Boolean system;
    private final @Nullable Boolean mfaEnabled;
    private final @Nullable String bannerHash;
    private final @Nullable Integer accentColor;
    private final @Nullable String locale;
    private final @Nullable Boolean verified;
    private final @Nullable String email;
    private final @Nullable Integer flagsAsInt;
    private final @NotNull UserFlag[] flags;
    private final @Nullable PremiumType premiumType;
    private final @Nullable Integer publicFlagsAsInt;
    private final @NotNull UserFlag[] publicFlags;

    private final @NotNull LApi lApi;

    /**
     *
     * @param lApi {@link LApi}
     * @param id the user's id
     * @param username the user's username, not unique across the platform
     * @param discriminator the user's 4-digit discord-tag
     * @param avatarHash the user's avatar hash
     * @param bot whether the user belongs to an OAuth2 application
     * @param system whether the user is an Official Discord System user (part of the urgent message system)
     * @param mfaEnabled whether the user has two factor enabled on their account
     * @param bannerHash the user's banner hash
     * @param accentColor the user's banner color encoded as an integer representation of hexadecimal color code
     * @param locale the user's chosen language option
     * @param verified whether the email on this account has been verified
     * @param email the user's email
     * @param flagsAsInt the flags as int on a user's account
     * @param flags the flags on a user's account
     * @param premiumType the type of Nitro subscription on a user's account
     * @param publicFlagsAsInt the public flags on a user's account
     * @param publicFlags the public flags on a user's account
     */
    public User(@NotNull LApi lApi, @NotNull Snowflake id, @NotNull String username, @NotNull String discriminator, @Nullable String avatarHash, @Nullable Boolean bot, @Nullable Boolean system, @Nullable Boolean mfaEnabled, @Nullable String bannerHash, @Nullable Integer accentColor, @Nullable String locale, @Nullable Boolean verified, @Nullable String email, @Nullable Integer flagsAsInt, @NotNull UserFlag[] flags, @Nullable PremiumType premiumType, @Nullable Integer publicFlagsAsInt, @NotNull UserFlag[] publicFlags) {
        this.lApi = lApi;
        this.id = id;
        this.username = username;
        this.discriminator = discriminator;
        this.avatarHash = avatarHash;
        this.bot = bot;
        this.system = system;
        this.mfaEnabled = mfaEnabled;
        this.bannerHash = bannerHash;
        this.accentColor = accentColor;
        this.locale = locale;
        this.verified = verified;
        this.email = email;
        this.flagsAsInt = flagsAsInt;
        this.flags = flags;
        this.premiumType = premiumType;
        this.publicFlagsAsInt = publicFlagsAsInt;
        this.publicFlags = publicFlags;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data} with required fields
     * @return {@link User}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #USERNAME_KEY}, {@link #DISCRIMINATOR_KEY} are missing or null
     */
    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable User fromData(@NotNull LApi lApi, @Nullable Data data) throws InvalidDataException {
        if(data == null) return null;
        String id = (String) data.get(ID_KEY);
        String username = (String) data.get(USERNAME_KEY);
        String discriminator = (String) data.get(DISCRIMINATOR_KEY);
        String avatarHash = (String) data.get(AVATAR_KEY);
        Boolean bot = (Boolean) data.get(BOT_KEY);
        Boolean system = (Boolean) data.get(SYSTEM_KEY);
        Boolean mfaEnabled = (Boolean) data.get(MFA_ENABLED_KEY);
        String bannerHash = (String) data.get(BANNER_KEY);
        Number accentColor = (Number) data.get(ACCENT_COLOR_KEY);
        String locale = (String) data.get(LOCALE_KEY);
        Boolean verified = (Boolean) data.get(VERIFIED_KEY);
        String email = (String) data.get(EMAIL_KEY);
        Number flags = (Number) data.get(FLAGS_KEY);
        Number premiumType = (Number) data.get(PREMIUM_TYPE_KEY);
        Number publicFlags = (Number) data.get(PUBLIC_FLAGS_KEY);

        if(id == null || username == null || discriminator == null){
            InvalidDataException.throwException(data, null, User.class,
                    new Object[]{id, username, discriminator},
                    new String[]{ID_KEY, USERNAME_KEY, DISCRIMINATOR_KEY});
            return null; //this will never happen, because above method will throw an exception
        }

        return new User(lApi, Snowflake.fromString(id), username, discriminator, avatarHash,
                bot, system, mfaEnabled, bannerHash, accentColor == null ? null : accentColor.intValue(),
                locale, verified, email, flags == null ? null : flags.intValue(),
                UserFlag.getFlagsFromInt(flags == null ? null : flags.intValue()),
                premiumType == null ? null : PremiumType.fromValue(premiumType.intValue()),
                publicFlags == null ? null : publicFlags.intValue(),
                UserFlag.getFlagsFromInt(publicFlags == null ? null : publicFlags.intValue()));
    }

    /**
     * the user's username, not unique across the platform
     */
    @Override
    public @NotNull String getUsername() {
        return username;
    }

    /**
     * the user's 4-digit discord-tag
     */
    @Override
    public @NotNull String getDiscriminator() {
        return discriminator;
    }

    /**
     * the user's id as {@link Snowflake}
     * @see #getId()
     */
    @Override
    public @NotNull Snowflake getIdAsSnowflake() {
        return id;
    }

    /**
     * the user's avatar hash
     */
    @Override
    public @Nullable String getAvatarHash() {
        return avatarHash;
    }

    /**
     * whether the user belongs to an OAuth2 application
     */
    public @Nullable Boolean getBot() {
        return bot;
    }

    /**
     * whether the user belongs to an OAuth2 application
     * @return {@code false} if {@link #getBot()} is {@code null}, otherwise {@link #getBot()}
     */
    public boolean isBot(){
        return !(bot == null) && bot;
    }

    /**
     * whether the user is an Official Discord System user (part of the urgent message system)
     */
    public @Nullable Boolean getSystem() {
        return system;
    }

    /**
     * whether the user is an Official Discord System user (part of the urgent message system)
     * @return {@code false} if {@link #getSystem()} is {@code null}, otherwise {@link #getSystem()}
     */
    public boolean isSystem(){
        return !(system == null) && system;
    }

    /**
     * whether the user has two factor enabled on their account
     */
    public @Nullable Boolean getMfaEnabled() {
        return mfaEnabled;
    }

    /**
     * the user's banner hash
     */
    public @Nullable String getBannerHash() {
        return bannerHash;
    }

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofUserBanner(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.lapi.api.lapiandqueue.Queueable Queueable} to retrieve the banner
     */
    public @NotNull CDNImageRetriever getBanner(int desiredSize, @NotNull AbstractFileType fileType){
        if(getBannerHash() == null) throw new IllegalArgumentException("This user object has no banner hash");
        return new CDNImageRetriever(CDNImage.ofUserBanner(lApi, getId(), getBannerHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofUserBanner(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.lapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.lapi.api.lapiandqueue.Queueable Queueable} to retrieve the banner
     */
    public @NotNull CDNImageRetriever getBanner(@NotNull AbstractFileType fileType){
        if(getBannerHash() == null) throw new IllegalArgumentException("This user object has no banner hash");
        return new CDNImageRetriever(CDNImage.ofUserBanner(lApi, getId(), getBannerHash(), fileType));
    }

    /**
     * the user's banner color encoded as an integer representation of hexadecimal color code
     */
    public @Nullable Integer getAccentColor() {
        return accentColor;
    }

    /**
     * the user's chosen language option
     */
    public @Nullable String getLocale() {
        return locale;
    }

    /**
     * whether the email on this account has been verified
     */
    public @Nullable Boolean getVerified() {
        return verified;
    }

    /**
     * the user's email
     */
    public @Nullable String getEmail() {
        return email;
    }

    /**
     * the flags on a user's account
     */
    public @Nullable Integer getFlagsAsInt() {
        return flagsAsInt;
    }

    /**
     * the flags on a user's account as {@link UserFlag UserFlag[]}
     */
    public @NotNull UserFlag[] getFlags() {
        return flags;
    }

    /**
     * the type of Nitro subscription on a user's account
     * @see PremiumType
     */
    public @Nullable PremiumType getPremiumType() {
        return premiumType;
    }

    /**
     * the public flags on a user's account
     */
    public @Nullable Integer getPublicFlagsAsInt() {
        return publicFlagsAsInt;
    }

    /**
     * the public flags on a user's account as {@link UserFlag UserFlag[]}
     */
    public @NotNull UserFlag[] getPublicFlags() {
        return publicFlags;
    }

    /**
     *
     * @return {@link Data} for this {@link User}
     */
    @Override
    public @NotNull Data getData() {
        Data data = new Data(15);

        data.add(ID_KEY, id);
        data.add(USERNAME_KEY, username);
        data.add(DISCRIMINATOR_KEY, discriminator);
        data.add(AVATAR_KEY, avatarHash);
        if(bot != null) data.add(BOT_KEY, bot);
        if(system != null) data.add(SYSTEM_KEY, system);
        if(mfaEnabled != null) data.add(MFA_ENABLED_KEY, mfaEnabled);
        if(bannerHash != null) data.add(BANNER_KEY, bannerHash);
        if(accentColor != null) data.add(ACCENT_COLOR_KEY, accentColor);
        if(locale != null) data.add(LOCALE_KEY, locale);
        if(verified != null) data.add(VERIFIED_KEY, verified);
        if(email != null) data.add(EMAIL_KEY, email);
        if(flagsAsInt != null) data.add(FLAGS_KEY, flagsAsInt);
        if(premiumType != null) data.add(PREMIUM_TYPE_KEY, premiumType);
        if(publicFlagsAsInt != null) data.add(PUBLIC_FLAGS_KEY, publicFlagsAsInt);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

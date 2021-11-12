package me.linusdev.discordbotapi.api.objects.application;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.application.team.Team;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/resources/application#application-object" target="_top">Application Object</a>
 */
public class Application implements Datable {

    //TODO

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
    private final @NotNull String summary;
    private final @NotNull String verifyKey;
    private final @Nullable Team team;
    private final @Nullable Snowflake guildId;
    private final @Nullable Snowflake primarySkuId;
    private final @Nullable String slug;
    private final @Nullable String coverImage;
    private final @Nullable Integer flags;
    private final @Nullable ApplicationFlag[] flagsArray;

    public Application(@NotNull Snowflake id, @NotNull String name, @Nullable String icon, @NotNull String description, @Nullable String[] rpcOrigins,
                       boolean botPublic, boolean botRequireCodeGrant, @Nullable String termsOfServiceUrl, @Nullable String privacyPolicyUrl,
                       @Nullable User owner, @NotNull String summary, @NotNull String verifyKey, @Nullable Team team, @Nullable Snowflake guildId,
                       @Nullable Snowflake primarySkuId, @Nullable String slug, @Nullable String coverImage, @Nullable Integer flags){
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


    @Override
    public Data getData() {
        Data data = new Data(8);

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
}

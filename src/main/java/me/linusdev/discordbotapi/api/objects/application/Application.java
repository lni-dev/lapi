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

    private @NotNull Snowflake id;
    private @NotNull String name;
    private @Nullable String icon;
    private @NotNull String description;
    private @Nullable String[] rpcOrigins;
    private boolean botPublic;
    private boolean botRequireCodeGrant;
    private @Nullable String termsOfServiceUrl;
    private @Nullable String privacyPolicyUrl;
    private @Nullable User owner;
    private @NotNull String summary;
    private @NotNull String verifyKey;
    private @Nullable Team team;
    private @Nullable Snowflake guildId;
    private @Nullable Snowflake primarySkuId;
    private @Nullable String slug;
    private @Nullable String coverImage;
    private @Nullable Integer flags;
    private @Nullable ApplicationFlag[] flagsArray;

    public Application(@NotNull Snowflake id, @NotNull String name, @Nullable String icon, @NotNull String description, @Nullable String[] rpcOrigins,
                       boolean botPublic, boolean botRequireCodeGrant, @Nullable String termsOfServiceUrl, @Nullable String privacyPolicyUrl,
                       @Nullable User owner, @NotNull String summary, @NotNull String verifyKey, /*@Nullable Team team,*/ @Nullable Snowflake guildId,
                       @Nullable Snowflake primarySkuId, @Nullable String slug, @Nullable String coverImage, @Nullable Integer flags){

    }


    @Override
    public Data getData() {
        Data data = new Data(0);
        return data;
    }
}

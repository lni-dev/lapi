package me.linusdev.discordbotapi.api.objects.role;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/topics/permissions#role-object-role-tags-structure" target="_top">Role Tags Structure</a>
 */
public class RoleTags implements Datable {

    public static final String BOT_ID_KEY = "bot_id";
    public static final String INTEGRATION_ID_KEY = "integration_id";
    public static final String PREMIUM_SUBSCRIBER_KEY = "premium_subscriber";

    private final @Nullable Snowflake botId;
    private final @Nullable Snowflake integrationId;
    private final boolean isPremiumSubscriberRole; //This depends on whether a PREMIUM_SUBSCRIBER_KEY exists or not

    /**
     *
     * @param botId the id of the bot this role belongs to
     * @param integrationId the id of the integration this role belongs to
     * @param isPremiumSubscriberRole whether this is the guild's premium subscriber role
     */
    public RoleTags(@Nullable Snowflake botId, @Nullable Snowflake integrationId, boolean isPremiumSubscriberRole) {
        this.botId = botId;
        this.integrationId = integrationId;
        this.isPremiumSubscriberRole = isPremiumSubscriberRole;
    }

    /**
     *
     * @param data {@link Data}
     * @return {@link RoleTags}
     */
    public static @Nullable RoleTags fromData(@Nullable Data data){
        if(data == null) return null;
        String botId = (String) data.get(BOT_ID_KEY);
        String integrationId = (String) data.get(INTEGRATION_ID_KEY);

        Object premiumSubscriptionKey = data.get(PREMIUM_SUBSCRIBER_KEY, new Object(), false);
        boolean isPremiumSubscriberRole = false;
        if(premiumSubscriptionKey == null){ //it will only be null if the field was really null
            isPremiumSubscriberRole = true;
        }

        return new RoleTags(Snowflake.fromString(botId), Snowflake.fromString(integrationId), isPremiumSubscriberRole);
    }

    /**
     * the id as {@link Snowflake} of the bot this role belongs to
     */
    public @Nullable Snowflake getBotIdAsSnowflake() {
        return botId;
    }

    /**
     * the id as {@link String} of the bot this role belongs to
     */
    public @Nullable String getBotId() {
        if(botId == null) return null;
        return botId.asString();
    }

    /**
     * the id as {@link Snowflake} of the integration this role belongs to
     */
    public @Nullable Snowflake getIntegrationIdAsSnowflake() {
        return integrationId;
    }

    /**
     * the id as {@link String} of the integration this role belongs to
     */
    public @Nullable String getIntegrationId() {
        if(integrationId == null) return null;
        return integrationId.asString();
    }

    /**
     * whether this is the guild's premium subscriber role
     */
    public boolean isPremiumSubscriberRole() {
        return isPremiumSubscriberRole;
    }

    /**
     *
     * @return {@link Data} for this {@link RoleTags}
     */
    @Override
    public Data getData() {
        Data data = new Data(3);

        data.add(BOT_ID_KEY, botId);
        data.add(INTEGRATION_ID_KEY, integrationId);
        if(isPremiumSubscriberRole) data.add(PREMIUM_SUBSCRIBER_KEY, null);

        return data;
    }
}

package me.linusdev.discordbotapi.api.communication.gateway.presence;

import me.linusdev.discordbotapi.api.communication.gateway.activity.Activity;
import me.linusdev.discordbotapi.api.communication.gateway.activity.ActivityType;
import me.linusdev.discordbotapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SelfUserPresenceManager implements HasLApi {

    private final @NotNull LApi lApi;
    private @Nullable GatewayWebSocket gateway;

    private @NotNull StatusType status;

    private final @NotNull ArrayList<Activity> activities;

    private boolean afk;
    private long afkSince;

    public SelfUserPresenceManager(@NotNull LApi lApi){
        this.lApi = lApi;

        this.status = StatusType.ONLINE;

        this.activities = new ArrayList<>();

        this.afk = false;
        this.afkSince = 0;
    }

    /**
     * Changes the small dot next to your bots profile picture.
     * @param status {@link StatusType status} of your bot
     */
    public SelfUserPresenceManager setStatus(@NotNull StatusType status) {
        this.status = status;
        return this;
    }

    /**
     * <p>
     *     If url is not {@code null}, type should be {@link ActivityType#STREAMING}
     * </p>
     * @param name the activity's name
     * @param type type {@link ActivityType activity type}
     * @param streamUrl stream url, is validated when type is {@link ActivityType#STREAMING}
     */
    public SelfUserPresenceManager addActivity(@NotNull String name, @NotNull ActivityType type, @Nullable String streamUrl){
        return addActivity(new Activity(name, type, streamUrl, null, null, null, null,
                null, null, null, null, null, null, null, null));
    }

    /**
     * <p>
     *     Parameter type should not be {@link ActivityType#STREAMING}
     * </p>
     * @param name the activity's name
     * @param type type {@link ActivityType activity type}
     */
    public SelfUserPresenceManager addActivity(@NotNull String name, @NotNull ActivityType type){
        return addActivity(name, type, null);
    }

    /**
     *
     * @param activity may only contain name, type and optionally url
     * @see #addActivity(String, ActivityType)
     * @see #addActivity(String, ActivityType, String)
     */
    public SelfUserPresenceManager addActivity(@NotNull Activity activity){
        activities.add(activity);
        return this;
    }

    /**
     * <p>
     *     Removes the first occurrence of given activity.
     * </p>
     * @param activity activity to remove
     */
    public SelfUserPresenceManager removeActivity(@NotNull Activity activity){
        activities.remove(activity);
        return this;
    }

    /**
     * <p>
     *     Removes all activities with given name
     * </p>
     * @param name name of activities to remove
     */
    public SelfUserPresenceManager removeActivity(@NotNull String name){
        activities.removeIf(activity -> activity.getName().equals(name));
        return this;
    }

    /**
     * <p>
     *     Changes to this list, will have a direct effect to this class.
     * </p>
     * @return list of current activities.
     */
    public @NotNull ArrayList<Activity> getActivities() {
        return activities;
    }

    /**
     * <p>
     *     {@link #afkSince} will be set to {@link System#currentTimeMillis()}
     * </p>
     * <p>
     *     If you want your bot to appear afk, you should {@link #setStatus(StatusType) set the status} to {@link StatusType#IDLE idle}
     * </p>
     * @param afk whether your bot is afk
     * @see #setAfk(boolean, long) 
     */
    public SelfUserPresenceManager setAfk(boolean afk) {
        this.afk = afk;
        this.afkSince = System.currentTimeMillis();
        return this;
    }

    /**
     * <p>
     *     If you want your bot to appear afk, you should {@link #setStatus(StatusType) set the status} to {@link StatusType#IDLE idle}
     * </p>
     *
     * @param afk whether your bot is afk
     * @param afkSince since when your bot is afk
     * @see #setAfk(boolean) 
     */
    public SelfUserPresenceManager setAfk(boolean afk, long afkSince) {
        this.afk = afk;
        this.afkSince = afkSince;
        return this;
    }

    /**
     * Get a {@link PresenceUpdate} of the current local presence.
     * @return {@link PresenceUpdate}
     */
    @ApiStatus.Internal
    public PresenceUpdate getPresenceUpdate(){

        Long since = afk ? afkSince : null;
        Activity[] activities = this.activities.toArray(new Activity[0]);

        return new PresenceUpdate(since, activities, status, afk);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

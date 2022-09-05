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

package me.linusdev.lapi.api.communication.gateway.presence;

import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.activity.Activity;
import me.linusdev.lapi.api.communication.gateway.activity.ActivityType;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommand;
import me.linusdev.lapi.api.communication.gateway.command.GatewayCommandType;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SelfUserPresenceUpdater {

    protected @Nullable GatewayWebSocket gateway;

    protected @NotNull StatusType status;

    protected final @NotNull ArrayList<Activity> activities;

    protected boolean afk;
    protected long afkSince;

    protected boolean updatePresence;

    /**
     *
     * @param updatePresence whether to send a {@link PresenceUpdate} to Discord ({@code true}) or not ({@code false})
     */
    public SelfUserPresenceUpdater(boolean updatePresence){
        this.updatePresence = updatePresence;
        this.status = StatusType.ONLINE;

        this.activities = new ArrayList<>();

        this.afk = false;
        this.afkSince = 0;
    }

    public SelfUserPresenceUpdater(){
        this(true);
    }

    /**
     *
     * @param data of an {@link PresenceUpdate} or {@code null}
     * @return {@link SelfUserPresenceUpdater}
     * @throws InvalidDataException if data is invalid
     */
    public static @NotNull SelfUserPresenceUpdater fromPresenceUpdateData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return new SelfUserPresenceUpdater(false);

        PresenceUpdate presence = PresenceUpdate.fromData(data);

        SelfUserPresenceUpdater updater = new SelfUserPresenceUpdater(true);
        updater.status = presence.getStatus();
        updater.activities.addAll(List.of(presence.getActivities()));
        updater.afk = presence.getAfk();
        updater.afkSince = presence.getSince() == null ? 0 : presence.getSince();

        return updater;
    }

    /**
     * Changes the small dot next to your bots profile picture.
     * @param status {@link StatusType status} of your bot
     */
    public SelfUserPresenceUpdater setStatus(@NotNull StatusType status) {
        this.updatePresence = true;
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
    public SelfUserPresenceUpdater addActivity(@NotNull String name, @NotNull ActivityType type, @Nullable String streamUrl){
        this.updatePresence = true;
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
    public SelfUserPresenceUpdater addActivity(@NotNull String name, @NotNull ActivityType type){
        this.updatePresence = true;
        return addActivity(name, type, null);
    }

    /**
     *
     * @param activity may only contain name, type and optionally url
     * @see #addActivity(String, ActivityType)
     * @see #addActivity(String, ActivityType, String)
     */
    public SelfUserPresenceUpdater addActivity(@NotNull Activity activity){
        this.updatePresence = true;
        activities.add(activity);
        return this;
    }

    /**
     * <p>
     *     Removes the first occurrence of given activity.
     * </p>
     * @param activity activity to remove
     */
    public SelfUserPresenceUpdater removeActivity(@NotNull Activity activity){
        this.updatePresence = true;
        activities.remove(activity);
        return this;
    }

    /**
     * <p>
     *     Removes all activities with given name
     * </p>
     * @param name name of activities to remove
     */
    public SelfUserPresenceUpdater removeActivity(@NotNull String name){
        this.updatePresence = true;
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
    public SelfUserPresenceUpdater setAfk(boolean afk) {
        this.updatePresence = true;
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
    public SelfUserPresenceUpdater setAfk(boolean afk, long afkSince) {
        this.updatePresence = true;
        this.afk = afk;
        this.afkSince = afkSince;
        return this;
    }

    /**
     * Will ask the gateway to send a {@link PresenceUpdate} as soon as possible.
     */
    public SelfUserPresenceUpdater updateNow(){
        if(gateway == null) throw new IllegalStateException("This SelfUserPresenceUpdater has not gateway! Are you currently adjusting the config? If yes, don't call this method.");
        gateway.queueCommand(new GatewayCommand(GatewayCommandType.UPDATE_PRESENCE, getPresenceUpdate()));
        return this;
    }

    /**
     * <p>
     *     whether to send a {@link PresenceUpdate} to Discord ({@code true}) or not ({@code false}).<br>
     *     If this is set to {@code false}, {@link #getPresenceUpdate()} will return {@code null}
     * </p>
     * @param updatePresence whether to send a {@link PresenceUpdate} to Discord ({@code true}) or not ({@code false}).
     */
    @ApiStatus.Internal
    public SelfUserPresenceUpdater setUpdatePresence(boolean updatePresence) {
        this.updatePresence = updatePresence;
        return this;
    }

    /**
     * Get a {@link PresenceUpdate} of the current local presence.
     * @return {@link PresenceUpdate} or {@code null} if we don't want to update the presence
     */
    @ApiStatus.Internal
    public @Nullable PresenceUpdate getPresenceUpdate(){
        if(!updatePresence) return null;

        Long since = afk ? afkSince : null;
        Activity[] activities = this.activities.toArray(new Activity[0]);

        return new PresenceUpdate(since, activities, status, afk);
    }

    @ApiStatus.Internal
    public SelfUserPresenceUpdater setGateway(@NotNull GatewayWebSocket gateway){
        this.gateway = gateway;
        return this;
    }
}

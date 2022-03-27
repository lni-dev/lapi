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

package me.linusdev.lapi.api.objects.user.connection;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;
import me.linusdev.lapi.api.objects.HasLApi;
import me.linusdev.lapi.api.lapiandqueue.LApi;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.objects.integration.Integration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/resources/user#connection-object" target="_top">Connection Object</a>
 */
public class Connection implements Datable, HasLApi {

    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String REVOKED_KEY = "revoked";
    public static final String INTEGRATIONS_KEY = "integrations";
    public static final String VERIFIED_KEY = "verified";
    public static final String FRIEND_SYNC_KEY = "friend_sync";
    public static final String SHOW_ACTIVITY_KEY = "show_activity";
    public static final String VISIBILITY_KEY = "visibility";

    private final @NotNull LApi lApi;

    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull String type;
    private final @Nullable Boolean revoked;
    private final @Nullable Integration[] integrations;
    private final boolean verified;
    private final boolean friendSync;
    private final boolean showActivity;
    private final VisibilityType visibility;

    /**
     *
     * @param lApi {@link LApi}
     * @param id id of the connection account
     * @param name the username of the connection account
     * @param type the service of the connection (twitch, youtube)
     * @param revoked whether the connection is revoked
     * @param integrations an array of partial {@link Integration server integrations}
     * @param verified whether the connection is verified
     * @param friendSync whether friend sync is enabled for this connection
     * @param showActivity whether activities related to this connection will be shown in presence updates
     * @param visibility {@link VisibilityType visibility} of this connection
     */
    public Connection(@NotNull LApi lApi, @NotNull String id, @NotNull String name, @NotNull String type, @Nullable Boolean revoked, @Nullable Integration[] integrations, boolean verified, boolean friendSync, boolean showActivity, VisibilityType visibility) {
        this.lApi = lApi;
        this.id = id;
        this.name = name;
        this.type = type;
        this.revoked = revoked;
        this.integrations = integrations;
        this.verified = verified;
        this.friendSync = friendSync;
        this.showActivity = showActivity;
        this.visibility = visibility;
    }

    /**
     *
     * @param lApi {@link LApi}
     * @param data {@link Data} with required Keys
     * @return {@link Connection}
     * @throws InvalidDataException if {@link #ID_KEY}, {@link #NAME_KEY}, {@link #TYPE_KEY}, {@link #VERIFIED_KEY}, {@link #FRIEND_SYNC_KEY}, {@link #SHOW_ACTIVITY_KEY}, {@link #VISIBILITY_KEY}
     */
    @SuppressWarnings("unchecked cast")
    public static @NotNull Connection fromData(@NotNull LApi lApi, @NotNull Data data) throws InvalidDataException {
        String id = (String) data.get(ID_KEY);
        String name = (String) data.get(NAME_KEY);
        String type = (String) data.get(TYPE_KEY);
        Boolean revoked = (Boolean) data.get(REVOKED_KEY);
        ArrayList<Object> integrationsData = (ArrayList<Object>) data.get(INTEGRATIONS_KEY);
        Boolean verified = (Boolean) data.get(VERIFIED_KEY);
        Boolean friendSync = (Boolean) data.get(FRIEND_SYNC_KEY);
        Boolean showActivity = (Boolean) data.get(SHOW_ACTIVITY_KEY);
        Number visibility = (Number) data.get(VISIBILITY_KEY);

        if(id == null || name == null || type == null || verified == null || friendSync == null || showActivity == null || visibility == null){
            InvalidDataException.throwException(data, null, Connection.class,
                    new Object[]{id, name, type, verified, friendSync, showActivity, visibility},
                    new String[]{ID_KEY, NAME_KEY, TYPE_KEY, VERIFIED_KEY, FRIEND_SYNC_KEY, SHOW_ACTIVITY_KEY, VISIBILITY_KEY});
            return null;//this will never happen, because above method will throw an exception
        }

        Integration[] integrations = null;
        if(integrationsData != null){
            integrations = new Integration[integrationsData.size()];
            int i = 0;
            for(Object o : integrationsData){
                integrations[i++] = Integration.fromData(lApi, (Data) o);
            }
        }

        return new Connection(lApi, id, name, type, revoked, integrations, verified, friendSync, showActivity,
                VisibilityType.fromValue(visibility.intValue()));
    }

    /**
     * id of the connection account
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * the username of the connection account
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * the service of the connection (twitch, youtube)
     */
    public @NotNull String getType() {
        return type;
    }

    /**
     * whether the connection is revoked
     */
    public @Nullable Boolean getRevoked() {
        return revoked;
    }

    /**
     * an array of partial server integrations
     */
    public @Nullable Integration[] getIntegrations() {
        return integrations;
    }

    /**
     * whether the connection is verified
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * whether friend sync is enabled for this connection
     */
    public boolean isFriendSync() {
        return friendSync;
    }

    /**
     * whether activities related to this connection will be shown in presence updates
     */
    public boolean isShowActivity() {
        return showActivity;
    }

    /**
     * {@link VisibilityType visibility} of this connection
     */
    public VisibilityType getVisibility() {
        return visibility;
    }

    /**
     *
     * @return {@link Data} for this {@link Connection}
     */
    @Override
    public Data getData() {
        Data data = new Data(9);

        data.add(ID_KEY, id);
        data.add(NAME_KEY, name);
        data.add(TYPE_KEY, type);
        if(revoked != null) data.add(REVOKED_KEY, revoked);
        if(integrations != null) data.add(INTEGRATIONS_KEY, integrations);
        data.add(VERIFIED_KEY, verified);
        data.add(FRIEND_SYNC_KEY, friendSync);
        data.add(SHOW_ACTIVITY_KEY, showActivity);
        data.add(VISIBILITY_KEY, visibility);

        return data;
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

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

package me.linusdev.lapi.api.manager.voiceregion;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.lapi.api.communication.retriever.query.Link;
import me.linusdev.lapi.api.communication.retriever.query.LinkQuery;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.lapi.LApiImpl;
import me.linusdev.lapi.api.manager.Manager;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.objects.voice.region.VoiceRegion;
import me.linusdev.lapi.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class VoiceRegionManager implements Manager, HasLApi {
    ArrayList<VoiceRegion> regions = null;

    private final LApiImpl lApi;

    public VoiceRegionManager(LApiImpl lApi){
        this.lApi = lApi;
    }

    /**
     * retrieves and updates the voice regions in {@link #regions} and adds new ones.<br>
     * This will wait the current Thread until the voice regions are retrieved from Discord!
     */
    public void update() throws LApiException, IOException, ParseException, InterruptedException {
        ArrayRetriever<SOData, VoiceRegion> retriever = new ArrayRetriever<SOData, VoiceRegion>(
                new LinkQuery(lApi, Link.LIST_VOICE_REGIONS), (lApi1, data) -> VoiceRegion.fromData(data));
        ArrayList<VoiceRegion> list = retriever.queueAndWait();

        if(list == null) return;

        if(regions == null){
            regions = list;
        }

        loop: for(VoiceRegion o : list){

            for(VoiceRegion region : regions){
                if(region.equalsId(o.getId())){
                    region.updateSelfByData(o.getData());
                    continue loop;
                }
            }

            regions.add(o);
        }

    }

    /**
     * retrieves and saves all voice regions into the array, ignores current content of the array
     * @param initialCapacity is ignored
     */
    @Override
    public void init(int initialCapacity) {
        ArrayRetriever<SOData, VoiceRegion> retriever = new ArrayRetriever<>(
                new LinkQuery(lApi, Link.LIST_VOICE_REGIONS), (lApi1, data) -> VoiceRegion.fromData(data));

        retriever.queue((voiceRegions, s, error) -> {

            if(error != null) {
                //TODO: handle error
                error.log(Logger.getLogger(this));

            } else {
                regions = voiceRegions;
                lApi.transmitEvent().
                        onVoiceRegionManagerReady(lApi, new VoiceRegionManagerReadyEvent(lApi, this));
            }
        });
    }

    /**
     *
     * @return true if {@link #regions} is not {@code null}
     */
    @Override
    public boolean isInitialized(){
        return regions != null;
    }

    /**
     *
     * @return {@link ArrayList} of {@link VoiceRegion VoiceRegions} backed by this {@link VoiceRegionManager}
     */
    @Nullable
    public ArrayList<VoiceRegion> getRegions() {
        return regions;
    }

    /**
     *
     * Iterates through {@link #regions the regions list} and returns the first voice region with given id.
     * <br>
     * This list will only be available if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_VOICE_REGIONS CACHE_VOICE_REGIONS} is enabled.
     * <br><br>
     * If none matches a <b>new</b> {@link VoiceRegion} is created, which will not be added to this manager!
     * This new voice region can be considered unknown,
     * that can also be checked with {@link VoiceRegion#isUnknown()}.
     *
     * @param id voice region id
     * @return {@link VoiceRegion} with given id
     */
    @NotNull
    public static VoiceRegion getVoiceRegionById(@NotNull LApi lApi, @NotNull String id){
        VoiceRegionManager vrm = lApi.getVoiceRegionManager();
        if(vrm != null && vrm.regions != null){
            for(VoiceRegion region : vrm.regions){
                if(region.equalsId(id))
                    return region;
            }
        }

        //Either voice regions are not retrieved or id was not found...
        //Make new unknown Voice region object
        return new VoiceRegion(id, null, false, false, false);
    }

    @Override
    public @NotNull LApi getLApi() {
        return lApi;
    }
}

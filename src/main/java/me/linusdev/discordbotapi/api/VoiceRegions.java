package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.query.SimpleGetLinkQuery;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class VoiceRegions {
    ArrayList<VoiceRegion> regions = null;

    public VoiceRegions(){

    }

    /**
     * retrieves and updates the voice regions in {@link #regions} and adds new ones
     * @param lApi
     * @throws LApiException
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     */
    public void update(LApi lApi) throws LApiException, IOException, ParseException, InterruptedException {
        ArrayRetriever<Data, VoiceRegion> retriever = new ArrayRetriever<Data, VoiceRegion>(lApi,
                new SimpleGetLinkQuery(lApi, SimpleGetLinkQuery.Links.GET_VOICE_REGIONS), (lApi1, data) -> VoiceRegion.fromData(data));
        ArrayList<VoiceRegion> list = retriever.completeHere().get();

        if(list == null) return;

        if(regions == null){
            regions = list;
        }

        loop:
        for(VoiceRegion o : list){

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
     * @param lApi
     * @throws LApiException
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     */
    public void init(LApi lApi) throws LApiException, IOException, ParseException, InterruptedException {
        ArrayRetriever<Data, VoiceRegion> retriever = new ArrayRetriever<Data, VoiceRegion>(lApi,
                new SimpleGetLinkQuery(lApi, SimpleGetLinkQuery.Links.GET_VOICE_REGIONS), (lApi1, data) -> VoiceRegion.fromData(data));

        retriever.queue(list -> regions = list);
    }

    /**
     *
     * @return true if {@link #regions} is not {@code null}
     */
    public boolean isInitialized(){
        return regions != null;
    }

    @Nullable
    public ArrayList<VoiceRegion> getRegions() {
        return regions;
    }
}

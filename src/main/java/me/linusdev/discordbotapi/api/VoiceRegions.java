package me.linusdev.discordbotapi.api;

import me.linusdev.data.Data;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.discordbotapi.api.communication.exceptions.LApiException;
import me.linusdev.discordbotapi.api.communication.retriever.ArrayRetriever;
import me.linusdev.discordbotapi.api.communication.retriever.query.SimpleGetLinkQuery;
import me.linusdev.discordbotapi.api.objects.voice.region.VoiceRegion;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

class VoiceRegions {
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
        ArrayRetriever retriever = new ArrayRetriever(lApi, new SimpleGetLinkQuery(lApi, SimpleGetLinkQuery.Links.GET_VOICE_REGIONS));
        ArrayList<Object> list = retriever.retrieve();

        if(regions == null){
            regions = new ArrayList<>(list.size());
        }

        loop:
        for(Object o : list){
            Data data = (Data) o;
            String id = (String) data.get(VoiceRegion.ID_KEY);

            for(VoiceRegion region : regions){
                if(region.equalsId(id)){
                    region.updateSelfByData(data);
                    continue loop;
                }
            }

            regions.add(VoiceRegion.fromData(data));
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
        ArrayRetriever retriever = new ArrayRetriever(lApi, new SimpleGetLinkQuery(lApi, SimpleGetLinkQuery.Links.GET_VOICE_REGIONS));
        ArrayList<Object> list = retriever.retrieve();

        regions = new ArrayList<>(list.size());

        for(Object o : list)
            regions.add(VoiceRegion.fromData((Data) o));
    }

    /**
     *
     * @return true if {@link #regions} is not {@code null}
     */
    public boolean initialized(){
        return regions != null;
    }

    @Nullable
    public ArrayList<VoiceRegion> getRegions() {
        return regions;
    }
}

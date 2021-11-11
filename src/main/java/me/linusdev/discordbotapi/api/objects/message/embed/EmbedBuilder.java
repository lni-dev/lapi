package me.linusdev.discordbotapi.api.objects.message.embed;

import org.jetbrains.annotations.NotNull;

public class EmbedBuilder {

    public EmbedBuilder(){

    }

    public @NotNull Embed build(){
        return new Embed(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}

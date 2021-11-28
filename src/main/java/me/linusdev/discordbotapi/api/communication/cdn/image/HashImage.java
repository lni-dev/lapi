package me.linusdev.discordbotapi.api.communication.cdn.image;

import me.linusdev.discordbotapi.api.communication.retriever.query.AbstractLink;


public interface HashImage {

    String getHash();

    AbstractLink getLink();

}

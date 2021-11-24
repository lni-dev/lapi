package me.linusdev.discordbotapi.api.templates.abstracts;

import me.linusdev.data.Datable;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.FilePart;
import me.linusdev.discordbotapi.api.communication.lapihttprequest.body.LApiHttpBody;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Template} is a template, which can be sent to Discord to create an object
 */
public interface Template extends Datable {

    /**
     * The Files used in this Template
     */
    default FilePart[] getFileParts(){
        return new FilePart[0];
    }

    /**
     * This will create a {@link LApiHttpBody} for this {@link Template}
     * @return {@link LApiHttpBody}
     */
    default LApiHttpBody getBody(){
        return new LApiHttpBody(getData(), getFileParts());
    }
}

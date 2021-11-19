package me.linusdev.discordbotapi.api.communication.lapihttprequest.body;

import me.linusdev.data.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;

/**
 * This class is used to create a {@link LApiHttpMultiPartBodyPublisher},
 * which will then be used as {@link java.net.http.HttpRequest.BodyPublisher BodyPublisher} to create a {@link java.net.http.HttpRequest HttpRequest}
 */
public class LApiHttpBody {

    private @Nullable Data jsonPart;
    private final @NotNull ArrayList<FilePart> fileParts;

    //TODO single file (especially image) body

    /**
     *
     * @param assumedFileAmount how many file-parts you will probably add to this Body
     * @param jsonPart the json-part of this Body, may be {@code null}
     */
    public LApiHttpBody(@Range(from = 0, to= Integer.MAX_VALUE) int assumedFileAmount, @Nullable Data jsonPart){
        this.jsonPart = jsonPart;
        fileParts = new ArrayList<>(assumedFileAmount);
    }

    /**
     * Creates a new {@link LApiHttpBody} which will probably not hold any file-parts
     *
     * @param jsonPart the json-part of this Body, may be {@code null}
     */
    public LApiHttpBody(@Nullable Data jsonPart){
        this(0, jsonPart);
    }

    /**
     *
     * @param assumedFileAmount how many file-parts you will probably add to this Body
     */
    public LApiHttpBody(int assumedFileAmount){
        this(assumedFileAmount, null);
    }

    /**
     * Creates a new {@link LApiHttpBody} which will probably not hold any file-parts
     */
    public LApiHttpBody(){
        this(0);
    }

    /**
     * adds a {@link FilePart}
     * @param filePart {@link FilePart} to add
     * @return this
     */
    public LApiHttpBody addFilePart(FilePart filePart){
        this.fileParts.add(filePart);
        return this;
    }

    /**
     * Sets the jsonPart for this Body
     * @param jsonPart {@link Data} for this Body
     * @return this
     */
    public LApiHttpBody setJSONPart(Data jsonPart){
        this.jsonPart = jsonPart;
        return this;
    }

    /**
     * The json-part for this Body
     */
    public @Nullable Data getJsonPart() {
        return jsonPart;
    }

    /**
     * @return {@code true} if this Body has file-parts, {@code false} otherwise
     */
    public boolean hasFileParts(){
        return fileParts.size() != 0;
    }

    /**
     * whether this is a MultiPartBody or not
     */
    public boolean isMultiPart(){
        //TODO in the future, this may not be true. If a single file is uploaded, we don't it a multipart
        return fileParts.size() != 0;
    }

    /**
     *
     * @return {@code true} if this Body has a json-part, {@code false} otherwise
     */
    public boolean hasJsonPart(){
        return jsonPart != null;
    }

    /**
     * {@link ArrayList} of file-parts for this Body. <br>
     * The order of the file-parts should be kept. <br>
     * You should not change the order or content of the returned {@link ArrayList}
     */
    public @NotNull ArrayList<FilePart> getFileParts() {
        return fileParts;
    }
}

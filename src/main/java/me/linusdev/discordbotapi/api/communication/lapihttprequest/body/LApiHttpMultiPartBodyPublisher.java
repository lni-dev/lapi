package me.linusdev.discordbotapi.api.communication.lapihttprequest.body;

import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Flow;

/**
 * I was trying to write my own MultiPart{@link java.net.http.HttpRequest.BodyPublisher BodyPublisher}. I don't really know if this was a success
 * <br>
 * <p>
 *   A multi-part-body is a body which can contain multiple different content-types.<br>
 *   The Http-header content-type is: multipart/form-data; boundary=
 * </p><br>
 *
 * <span style="margin-bottom:0;padding-bottom:0;font-size:15px;font-weight:'bold';">Format</span><br>
 *
 * <div>
 *     each data starts with a "--{@link #boundaryString boundary}" (yes, 2 dashes are added in front of the boundary) and is then followed by a sub-part header.
 *     The sub-part header looks like this:
 *     <pre>
 *         {@code
 *         "Content-Disposition: form-data; name=\"...\"" + END_OF_LINE +
 *         "Content-Type: application/json" + END_OF_LINE + END_OF_LINE;
 *         }
 *     </pre>
 *     where END_OF_LINE is {@code "\r\n"}<br>
 *     I have added two END_OF_LINEs add the end of the sub-part header, because I saw that a lot<br><br>
 *
 *     After the sub-part header, the data will follow.<br>
 *     At the end another {@link #boundaryString boundary} will be appended. If this is the very last boundary of the whole body,
 *     two dashes will be added in front and after the boundary. like this: "--{@link #boundaryString boundary}--"<br>
 *     Note that every line (including the last one) is terminated by a {@code "\r\n"} sequence.
 *
 * </div><br><br>
 *
 * <span style="margin-bottom:0;padding-bottom:0;font-size:15px;font-weight:'bold';">Information Sources</span><br>
 * I have gathered most of this information from different sites and stack overflow questions:
 * <ul>
 *
 *     <li>
 *         <a href="https://datatracker.ietf.org/doc/html/rfc7578#section-4" target="_top">https://datatracker.ietf.org/doc/html/rfc7578#section-4</a>
 *     </li>
 *     <li>
 *         <a href="https://stackoverflow.com/a/23517227" target="_top">https://stackoverflow.com/a/23517227</a>
 *     </li>
 *     <li>
 *        <a href="https://stackoverflow.com/a/28380690/895245" target="_top">https://stackoverflow.com/a/28380690/895245</a>
 *     </li>
 *     <li>
 *         <a href="https://discord.com/developers/docs/reference#editing-message-attachments" target="_top">https://discord.com/developers/docs/reference#editing-message-attachments</a>
 *     </li>
 *
 * </ul>
 *
 *
 *
 */
public class LApiHttpMultiPartBodyPublisher implements HttpRequest.BodyPublisher {

    public final static String END_OF_LINE = "\r\n";
    public final static String BOUNDARY = "boundary-1919-time-191191-boundary";

    /**
     * Always ends with the bytes of "{@link #END_OF_LINE}"
     */
    private final byte[] boundaryBytes;
    /**
     * The String used for the boundary. Does not end with {@link #END_OF_LINE}
     */
    private final String boundaryString;


    /**
     * JSON string, all "\n"s are already converted to "{@link #END_OF_LINE}". This String will also always end with a "{@link #END_OF_LINE}"
     */
    private String json;
    private final FilePart[] fileParts;


    private final byte[][] byteArrays;

    public LApiHttpMultiPartBodyPublisher(@NotNull LApiHttpBody body) throws IOException {
        //this is the boundary string, which is also used for the header
        this.boundaryString = BOUNDARY.replace("time", Long.toString(System.currentTimeMillis()));
        //before each boundary in the content there are two extra dashes!
        this.boundaryBytes = ("--" + boundaryString + END_OF_LINE).getBytes(StandardCharsets.UTF_8);

        if(body.getJsonPart() != null) {
            this.json = new JsonParser().getJsonString(body.getJsonPart()).toString();
            this.json = this.json.replace("\n", END_OF_LINE);
            if (!this.json.endsWith(END_OF_LINE)) {
                this.json = this.json + END_OF_LINE;
            }
        }else{
            this.json = null;
        }

        this.fileParts = body.getFileParts();

        this.byteArrays = getBytes();

    }

    /**
     *
     * @return an Array of byte-arrays. These are the bytes of this body
     */
    private byte[][] getBytes() throws IOException {

        byte[][] bytes = new byte[4 + (fileParts.length * 4)][];
        int i = 0;
        bytes[i++] = boundaryBytes;

        if(json != null) {
            //Payload JSON
            String partHeader = "Content-Disposition: form-data; name=\"payload_json\"" + END_OF_LINE +
                    "Content-Type: application/json" + END_OF_LINE + END_OF_LINE;

            bytes[i++] = partHeader.getBytes(StandardCharsets.UTF_8);
            bytes[i++] = json.getBytes(StandardCharsets.UTF_8);
            if (i == bytes.length - 1) {
                //                  ADD LAST BOUNDARY!
                //the last boundary has to extra dashes at the end!
                bytes[i++] = ("--" + boundaryString + "--" + END_OF_LINE).getBytes(StandardCharsets.UTF_8);
            } else {
                bytes[i++] = boundaryBytes;
            }

            //Payload JSON END
        }
        //Files

        for(FilePart filePart : fileParts){
            String partHeader = "Content-Disposition: form-data; name=\"files[" + filePart.getAttachmentId() + "]\"; filename=\"" + filePart.getFilename() + "\"" + END_OF_LINE +
            "Content-Type: " + filePart.getContentType().getContentTypeAsString() + END_OF_LINE + END_OF_LINE;

            bytes[i++] = partHeader.getBytes(StandardCharsets.UTF_8);
            bytes[i++] = filePart.getBytes();
            bytes[i++] = END_OF_LINE.getBytes(StandardCharsets.UTF_8); //before the boundary we need a new line!
            if(i == bytes.length -1){
                //                  ADD LAST BOUNDARY!
                //the last boundary has to extra dashes at the end!
                bytes[i++] = ("--" + boundaryString + "--" + END_OF_LINE).getBytes(StandardCharsets.UTF_8);
            }else
                bytes[i++] = boundaryBytes;
        }
        //Files END

        //if anything else is added here, the last boundary (see 6 lines above) must be added again!

        return bytes;


    }

    /**
     * The boundary String used in the Http Content-type header
     */
    public String getBoundaryString() {
        return boundaryString;
    }

    @Override
    public long contentLength() {
        //this is the content length for a single Subscriber.onNext().
        //that is why I decided to go with an unknown length
        return -1;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        subscriber.onSubscribe(new Subscription(subscriber));
    }

    public class Subscription implements Flow.Subscription{

        private final Flow.Subscriber<? super ByteBuffer> subscriber;
        private int index = 0;

        public Subscription(Flow.Subscriber<? super ByteBuffer> subscriber){
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            for(long i = 0; i < n && index < byteArrays.length; i++){
                subscriber.onNext(ByteBuffer.wrap(byteArrays[index++]));
            }
            if(index == byteArrays.length){
                subscriber.onComplete();
            }
        }

        @Override
        public void cancel() {

        }
    }
}

package me.linusdev.discordbotapi.api.lapiandqueue;

import me.linusdev.discordbotapi.api.other.Container;
import me.linusdev.discordbotapi.api.other.Error;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>
 *     A {@link Queueable} is an Object, that can be queued and execute a Task (mostly retrieving an Object).<br>
 *     By invoking {@link #queue()}, a {@link Future} will be created and {@link LApi#queue(Future) queued}.
 *     All Objects in the {@link LApi#queue queue} will be completed in the queued order. Two Tasks may never
 *     run at the same time. For more information about the process, see {@link Future}.
 * </p>
 *
 * <p>
 *     Usually the {@link Future#result result} of the {@link Queueable} should be processed right after it has been retrieved.
 *     This can be achieved by using listeners like {@link Future#then(BiConsumer)}. These can be set directly when queuing by
 *     calling {@link Queueable#queue(BiConsumer)} or {@link Queueable#queue(Consumer)}.
 *     <b style="color:red">{@link Object#wait()}, {@link Thread#sleep(long)} or any other waiting tasks may never be called inside these listeners!</b>.
 *     This will delay the queue and could lead to an infinite {@link Object#wait()}
 * </p><br><br>
 *
 * <h3 style="margin-bottom:0;padding-bottom:0">How to use a {@link Queueable}:</h3>
 * <pre>{@code
 * LApi api = new LApi(Private.TOKEN, config);
 *
 * //Retrieve the Message with id=912377554105688074
 * //inside the channel with id=912377387868639282
 * Queueable<Message> msgRetriever
 *         = api.getChannelMessageRetriever(
 *                   "912377387868639282", //channel-id
 *                   "912377554105688074"  //message-id
 *                   );
 *
 * //Queue and create a BiConsumer
 * msgRetriever.queue(new BiConsumer<Message, Error>() {
 *      @Override
 *      public void accept(Message message, Error error) {
 *          //This will be executed once the message has been retrieved
 *          if(error != null){
 *              System.out.println("Error!");
 *              return;
 *          }
 *
 *          System.out.println(message.getContent());
 *      }
 * });
 *
 * //Or simpler using a lambda expression
 * msgRetriever.queue((message, error) -> {
 *             //This will be executed once the message has been retrieved
 *             if(error != null){
 *                 System.out.println("Error!");
 *                 return;
 *             }
 *
 *             System.out.println(message.getContent());
 *         });
 * }</pre>
 *
 * @param <T> Type of the data that should be retrieved / the result of the Task
 * @see Future
 */
public abstract class Queueable<T> implements HasLApi {

    /**
     * queues this {@link Queueable} and returns a {@link Future}<br>
     * see {@link Future} and {@link LApi#queue(Queueable, BiConsumer, Consumer, Consumer)  LApi.queue(...)} for more information!
     * @return {@link Future}
     * @see Future
     * @see LApi#queue(Queueable, BiConsumer, Consumer, Consumer)  LApi.queue(...)
     */
    public @NotNull Future<T> queue(){
        return getLApi().queue(this, null, null, null);
    }

    /**
     * Queues this {@link Queueable} after given delay and returns a {@link Future}.<br>
     * See {@link Future} and {@link LApi#queueAfter(Queueable, long, TimeUnit)}  LApi.queueAfter(Queueable, long, TimeUnit)} for more information!
     * @param delay the delay
     * @param timeUnit {@link TimeUnit} to use for the delay
     * @return {@link Future}
     * @see Future
     * @see LApi#queueAfter(Queueable, long, TimeUnit) LApi.queueAfter(Queueable, long, TimeUnit)
     */
    public @NotNull Future<T> queueAfter(long delay, TimeUnit timeUnit){
        return getLApi().queueAfter(this, delay, timeUnit);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#then(BiConsumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#then(BiConsumer)
     */
    public @NotNull Future<T> queue(BiConsumer<T, Error> then){
        return getLApi().queue(this, then, null, null);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#then(Consumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#then(Consumer)
     */
    public @NotNull Future<T> queue(Consumer<T> thenSingle){
        return getLApi().queue(this, null, thenSingle, null);
    }

    /**
     * Queues this {@link Queueable} and returns a {@link Future}.<br>
     * This also sets the {@link Future#beforeComplete(Consumer)} Listener.<br>
     * See {@link #queue()} for more information.
     * @return {@link Future}
     * @see #queue()
     * @see Future#beforeComplete(Consumer)
     */
    public @NotNull Future<T> queueAndSetBeforeComplete(Consumer<Future<T>> beforeComplete){
        return getLApi().queue(this, null, null, beforeComplete);
    }

    /**
     * Queues this {@link Queueable} and return its result.<br>
     * This will call {@link Future#get(long, TimeUnit)} and wait this Thread!<br>
     * @return the result, should not be {@code null}
     * @throws ExecutionException if an {@link Error} occurred. If you use this method, you won't have access to this Error.
     * @throws InterruptedException if the Thread was interrupted
     */
    public @NotNull T queueAndWait() throws ExecutionException, InterruptedException {
        return queue().get();
    }

    /**
     *
     * Note that this has different implementations and may not always write to the file, what you would expect.
     *
     * @param file Path to the file to save to
     * @param overwriteIfExists whether to overwrite the file if it already exists.
     * @param after {@link BiConsumer}, what to do after the file as been written or an error has occurred
     * @return {@link Future}
     */
    public abstract @NotNull Future<T> queueAndWriteToFile(@NotNull Path file, boolean overwriteIfExists, @Nullable BiConsumer<T, Error> after);

    public @NotNull Container<T> completeHere(){
        getLApi().checkQueueThread();
        return completeHereAndIgnoreQueueThread();
    }

    /**
     * Complete this {@link Queueable<T>} in this Thread.
     */
    protected abstract @NotNull Container<T> completeHereAndIgnoreQueueThread();

}

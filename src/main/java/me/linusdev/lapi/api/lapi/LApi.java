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

package me.linusdev.lapi.api.lapi;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.lapi.api.async.queue.Queueable;
import me.linusdev.lapi.api.async.queue.QueueableFuture;
import me.linusdev.lapi.api.async.Future;
import me.linusdev.lapi.api.async.queue.QueueableImpl;
import me.linusdev.lapi.api.cache.Cache;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.EventIdentifier;
import me.linusdev.lapi.api.event.ReadyEventAwaiter;
import me.linusdev.lapi.api.lapi.shutdown.ShutdownOption;
import me.linusdev.lapi.api.lapi.shutdown.ShutdownOptions;
import me.linusdev.lapi.api.lapi.shutdown.Shutdownable;
import me.linusdev.lapi.api.manager.command.CommandManager;
import me.linusdev.lapi.api.manager.command.CommandManagerImpl;
import me.linusdev.lapi.api.manager.guild.GuildPool;
import me.linusdev.lapi.api.manager.voiceregion.VoiceRegionManager;
import me.linusdev.lapi.api.communication.ApiVersion;
import me.linusdev.lapi.api.exceptions.LApiException;
import me.linusdev.lapi.api.exceptions.LApiRuntimeException;
import me.linusdev.lapi.api.exceptions.NoInternetException;
import me.linusdev.lapi.api.communication.gateway.events.transmitter.AbstractEventTransmitter;
import me.linusdev.lapi.api.communication.gateway.presence.SelfUserPresenceUpdater;
import me.linusdev.lapi.api.communication.gateway.websocket.GatewayWebSocket;
import me.linusdev.lapi.api.communication.http.request.IllegalRequestMethodException;
import me.linusdev.lapi.api.communication.http.request.LApiHttpRequest;
import me.linusdev.lapi.api.communication.http.response.LApiHttpResponse;
import me.linusdev.lapi.api.config.Config;
import me.linusdev.lapi.api.config.ConfigBuilder;
import me.linusdev.lapi.api.config.ConfigFlag;
import me.linusdev.lapi.api.interfaces.HasLApi;
import me.linusdev.lapi.api.request.RequestFactory;
import me.linusdev.lapi.api.thread.LApiThread;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.List;

/**
 * <h2 style="margin:0;padding:0;">What is LApi?</h2>
 * <p style="margin:0;padding:0;">
 *     LApi is the facade, that handles all communications with Discord's api
 * </p>
 *
 * <br>
 * <h2 style="margin:0;padding:0;">How to get an Instance?</h2>
 * <p style="margin:0;padding:0;">
 *     The easiest way to get an instance is:<br>
 *     {@code LApi lApi = ConfigBuilder.getDefault(TOKEN).buildLapi()}.<br>
 *     {@code TOKEN} must be replaced with {@link ConfigBuilder#setToken(String) your bot token}
 * </p>
 * <br>
 * <div>
 *     Alternatively you can also make your own Config. For example:<br>
 *     <pre>
 *         {@code
 *  LApi lApi = new ConfigBuilder(TOKEN)
 *          .enable(ConfigFlag.ENABLE_GATEWAY)
 *          .adjustGatewayConfig(gatewayConfigBuilder -> {
 *              gatewayConfigBuilder
 *                      .addIntent(GatewayIntent.GUILD_MESSAGES,
 *                                 GatewayIntent.DIRECT_MESSAGES);
 *                      .adjustStartupPresence(presence -> {
 *                                 presence.setStatus(StatusType.ONLINE);
 *                             });
 *          }).buildLapi();
 *  }
 *     </pre>
 *     {@code TOKEN} must be replaced with {@link ConfigBuilder#setToken(String) your bot token}
 * </div>
 * <br>
 * <br>
 * <h2 style="margin:0;padding:0;">How to listen to events?</h2>
 * <div style="margin:0;padding:0;">
 *     You can listen to events by adding a listener to the {@link AbstractEventTransmitter event transmitter}:
 *     <pre>{@code lApi.getEventTransmitter().addListener(yourListener)}</pre>
 *     for more information see {@link me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener EventListener}
 * </div>
 */
@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface LApi extends HasLApi {

    /**
     * The Discord id of the creator of this API
     */
    public static final String CREATOR_ID = "247421526532554752";

    public static final String LAPI_URL = "https://github.com/lni-dev/lapi";
    public static final String LAPI_VERSION = "1.0.6";
    public static final String LAPI_NAME = "LApi";

    public static final @NotNull ApiVersion NEWEST_API_VERSION = ApiVersion.V10;
    public static final @NotNull ApiVersion DEFAULT_API_VERSION = NEWEST_API_VERSION;

    /**
     * Default max shutdown time in milliseconds.
     */
    public static final long DEFAULT_MAX_SHUTDOWN_TIME = 3000;

    public static final String LAPI_ARRAY_WRAPPER_KEY = "lapi_array_wrapper";

    @Contract(value = "_ -> new", pure = true)
    static LApi newInstance(@NotNull Config config) throws LApiException, IOException, ParseException, InterruptedException {
        return new LApiImpl(config, StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    /**
     *
     * This queues a {@link QueueableFuture} in the {@link LApiImpl#queue Queue}.<br>
     * This is used for sending all {@link LApiHttpRequest}.
     * This will NOT wait the Thread until the {@link QueueableFuture} has been completed.
     * <br><br>
     * If you want to wait your Thread you could use:
     * <ul>
     *     <li>
     *         {@link Future#get()}, to wait until the {@link Queueable}'s {@link QueueableImpl#execute() execution} has completed.
     *     </li>
     *     <li>
     *          {@link Queueable#executeHere()} to execute it in the current thread.
     *     </li>
     * </ul>
     *
     *
     * @param queueable {@link Queueable}
     * @param <T> Return Type of {@link Queueable}
     */
    @ApiStatus.Internal
    <T> void queue(@NotNull QueueableFuture<T> queueable);


    LApiHttpResponse getResponse(@NotNull LApiHttpRequest request) throws IllegalRequestMethodException, IOException, InterruptedException, NoInternetException, ParseException;

    /**
     * Appends the required headers to the {@link LApiHttpRequest}.<br>
     * These headers are required for Discord to accept the request
     * @param request the {@link LApiHttpRequest} to append the headers to
     * @return the {@link LApiHttpRequest} with the appended headers
     */
    LApiHttpRequest appendHeader(@NotNull LApiHttpRequest request);

    /**
     * This throws a {@link LApiRuntimeException} if the {@link Thread#currentThread() current thread} is a {@link LApiThread}
     * and {@link LApiThread#allowBlockingOperations() does not allow blocking operations}.
     *
     * @throws LApiRuntimeException if the currentThread is a {@link LApiThread} and {@link LApiThread#allowBlockingOperations() does not allow blocking operations}.
     */
    void checkThread() throws LApiRuntimeException;

    /**
     * waits the current thread until {@link EventIdentifier#LAPI_READY LAPI_READY} event hast been triggered.
     * @throws InterruptedException if interrupted while waiting
     */
    void waitUntilLApiReadyEvent() throws InterruptedException;

    /**
     * This function will probably be changed or adjusted in the future and should not be used, if you
     * are not willing to change your code
     * <br><br>
     * <p>
     *     This will run given {@link Runnable} in a new thread and if a {@link Throwable} is thrown it
     *     will be logged.
     * </p>
     * @param delay runnable will run after given delay
     */
    @ApiStatus.Internal
    void runSupervised(@NotNull Runnable runnable, long delay);

    /**
     *
     * @see #runSupervised(Runnable, long)
     */
    @ApiStatus.Internal
    void runSupervised(@NotNull Runnable runnable);

    /**
     * Attempts to shut {@link LApi} down with given {@link ShutdownOption}s.<br>
     * This will block the current thread.
     * @param options {@link ShutdownOptions}
     */
    @Blocking
    void shutdown(@NotNull List<ShutdownOption> options);

    /**
     * Attempts to shut down {@link LApi} as quick as possible.
     * Does not block the current thread.
     */
    @NonBlocking
    public void shutdownNow();

    @ApiStatus.Internal
    void registerShutdownable(@NotNull Shutdownable shutdownable);

    //Getter

    /**
     *
     * @return {@link ApiVersion} used for Http Requests
     */
    @NotNull ApiVersion getHttpRequestApiVersion();

    @NotNull RequestFactory getRequestFactory();

    default @NotNull RequestFactory requests() {
        return getRequestFactory();
    }

    @NotNull ReadyEventAwaiter getReadyEventAwaiter();

    /**
     * <p>
     *     The event transmitter is used to listen to events from Discord. For more information on
     *     how to add a listener, see {@link me.linusdev.lapi.api.communication.gateway.events.transmitter.EventListener EventListener}
     * </p>
     * @return {@link AbstractEventTransmitter}
     */
    AbstractEventTransmitter getEventTransmitter();

    /**
     * <p>
     *     let's you update the presence of the current self user (your bot).
     * </p>
     * <p>
     *     <b>After you finished adjusting your presence, you will have to call {@link SelfUserPresenceUpdater#updateNow()}!</b>
     * </p>
     * @return {@link SelfUserPresenceUpdater}
     */
    SelfUserPresenceUpdater getSelfPresenceUpdater();

    /**
     * <p>
     *     Manager for the {@link me.linusdev.lapi.api.objects.voice.region.VoiceRegion VoiceRegions}
     *     retrieved from Discord if {@link me.linusdev.lapi.api.config.ConfigFlag#CACHE_VOICE_REGIONS} is enabled
     * </p>
     * @return {@link VoiceRegionManager}
     */
    VoiceRegionManager getVoiceRegionManager();

    /**
     *
     * @return {@link GatewayWebSocket} or {@code null} if {@link ConfigFlag#ENABLE_GATEWAY ENABLE_GATEWAY} is not enabled.
     */
    GatewayWebSocket getGateway();

    /**
     *
     * @return {@link Cache} or {@code null} if {@link ConfigFlag#BASIC_CACHE} is not enabled.
     */
    Cache getCache();

    /**
     *
     * @return {@link CommandManagerImpl} or {@code null} if {@link ConfigFlag#COMMAND_MANAGER} is not enabled.
     */
    CommandManager getCommandManager();

    /**
     *
     * @return {@link GuildPool} or {@code null} if {@link ConfigFlag#CACHE_GUILDS} is not enabled.
     */
    GuildPool getGuildPool();

}

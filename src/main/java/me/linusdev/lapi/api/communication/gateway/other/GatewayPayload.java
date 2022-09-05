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

package me.linusdev.lapi.api.communication.gateway.other;

import me.linusdev.data.Datable;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.communication.exceptions.InvalidDataException;
import me.linusdev.lapi.api.communication.gateway.abstracts.GatewayPayloadAbstract;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayEvent;
import me.linusdev.lapi.api.communication.gateway.enums.GatewayOpcode;
import me.linusdev.lapi.api.communication.gateway.identify.Identify;
import me.linusdev.lapi.api.communication.gateway.resume.Resume;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @see <a href="https://discord.com/developers/docs/topics/gateway#payloads-gateway-payload-structure" target="_top">Gateway payload strcuture</a>
 */
public class GatewayPayload implements GatewayPayloadAbstract, Datable {

    public static final String OPCODE_KEY = "op";
    public static final String DATA_KEY = "d";
    public static final String SEQUENCE_KEY = "s";
    public static final String TYPE_KEY = "t";
    public static final String _TRACE_KEY = "_trace";

    private final @NotNull GatewayOpcode opcode;
    private final @Nullable Object data;

    /**
     * -1 can be considered {@code null}
     */
    private final @Nullable Long sequence;
    private final @Nullable GatewayEvent type;
    private final @Nullable String[] _trace;

    /**
     *  @param opcode opcode for the payload
     * @param data event data
     * @param sequence sequence number, used for resuming sessions and heartbeats. -1 can be considered null
     * @param type the event name for this payload
     * @param trace
     */
    public GatewayPayload(@NotNull GatewayOpcode opcode, @Nullable Object data, @Nullable Long sequence, @Nullable GatewayEvent type, @Nullable String[] trace) {
        this.opcode = opcode;
        this.data = data;
        this.sequence = sequence;
        this.type = type;
        _trace = trace;
    }

    /**
     * Creates a Heartbeat GatewayPayload with opcode {@link GatewayOpcode#HEARTBEAT} and given sequence as data.
     * If we have not received a sequence yet, the sequence should be {@code null}
     * @param sequence last sequence received from Discord
     * @return {@link GatewayPayload}
     * @see <a href="https://discord.com/developers/docs/topics/gateway#heartbeating" target="_top">Heartbeating</a>
     */
    public static @NotNull GatewayPayload newHeartbeat(@Nullable Long sequence){
        return new GatewayPayload(GatewayOpcode.HEARTBEAT, sequence, null, null, null);
    }

    /**
     * Creates an Identify GatewayPayload with opcode {@link GatewayOpcode#IDENTIFY} and given {@link Identify} as data.
     * @param identify the identify data
     * @return {@link GatewayPayload}
     * @see <a href="https://discord.com/developers/docs/topics/gateway#identifying" target="_top">Identifying</a>
     */
    public static @NotNull GatewayPayload newIdentify(@NotNull Identify identify){
        return new GatewayPayload(GatewayOpcode.IDENTIFY, identify.getData(), null, null, null);
    }

    /**
     * Creates a Resume GatewayPayload with opcode {@link GatewayOpcode#RESUME} and given {@link Resume} as data.
     * @param resume the resume data
     * @return {@link GatewayPayload}
     * @see <a href="https://discord.com/developers/docs/topics/gateway#resume" target="_top">Resume</a>
     */
    public static @NotNull GatewayPayload newResume(@NotNull Resume resume){
        return new GatewayPayload(GatewayOpcode.RESUME, resume.getData(), null, null, null);
    }

    /**
     *
     * @param data {@link SOData}
     * @return {@link GatewayPayload}
     * @throws InvalidDataException if {@link #OPCODE_KEY} is missing or {@code null}
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable GatewayPayload fromData(@Nullable SOData data) throws InvalidDataException {
        if(data == null) return null;
        Number op = (Number) data.get(OPCODE_KEY);
        Object d = data.get(DATA_KEY);
        Number s = (Number) data.get(SEQUENCE_KEY);
        String t = (String) data.get(TYPE_KEY);

        ArrayList<String> trace = data.getListAndConvert(_TRACE_KEY, convertible -> (String) convertible);

        if(op == null) {
            InvalidDataException.throwException(data, null, GatewayPayload.class, new Object[]{op}, new String[]{OPCODE_KEY});
        }

        //op cannot be null!
        //noinspection ConstantConditions
        return new GatewayPayload(
                GatewayOpcode.fromValue(op.intValue()), d,
                s == null ? null : s.longValue(), GatewayEvent.fromString(t),
                trace == null ? null : trace.toArray(new String[0]));
    }

    /**
     * opcode for the payload
     */
    public @NotNull GatewayOpcode getOpcode() {
        return opcode;
    }

    /**
     * event data
     */
    public @Nullable Object getPayloadData() {
        return data;
    }

    /**
     * sequence number, used for resuming sessions and heartbeats
     * @return -1 if no sequence is given
     */
    public @Nullable Long getSequence() {
        return sequence;
    }

    /**
     * the event name for this payload
     */
    public @Nullable GatewayEvent getType() {
        return type;
    }

    @Override
    public @Nullable String toJsonString() {
        return getData().toJsonString().toString();
    }

    public String[] get_trace() {
        return _trace;
    }

    @Override
    public SOData getData() {
        SOData data = SOData.newOrderedDataWithKnownSize(4);

        data.add(OPCODE_KEY, opcode);
        data.add(DATA_KEY, this.data);
        data.addIfNotNull(SEQUENCE_KEY, sequence);
        data.addIfNotNull(TYPE_KEY, type);

        return data;
    }
}

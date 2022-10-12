/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.api.objects.message;

import me.linusdev.lapi.api.objects.message.concrete.ChannelMessage;
import me.linusdev.lapi.api.objects.message.concrete.CreateEventMessage;
import me.linusdev.lapi.api.objects.message.concrete.UpdateEventMessage;

public enum ImplementationType {
    CHANNEL_MESSAGE {
        @Override
        public ChannelMessage cast(AnyMessage message) {
            return (ChannelMessage) message;
        }
    },

    MESSAGE_CREATE_EVENT_MESSAGE {
        @Override
        public CreateEventMessage cast(AnyMessage message) {
            return (CreateEventMessage) message;
        }
    },

    MESSAGE_UPDATE_EVENT_MESSAGE {
        @Override
        public UpdateEventMessage cast(AnyMessage message) {
            return (UpdateEventMessage) message;
        }
    },
    ;

    public abstract AnyMessage cast(AnyMessage message);
}

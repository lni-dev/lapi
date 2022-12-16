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

package me.linusdev.lapi.api.lapi.shutdown;

public enum ShutdownOptions implements ShutdownOption {

    QUEUE_STOP_IF_EMPTY(1 << 0, 1 << 1),
    QUEUE_STOP_IMMEDIATELY(1 << 1, 1 << 0),
    QUEUE_ACCEPT_NEW_FUTURES(1 << 2, 0),
    ;

    private final long id;
    private final long mutuallyExclusiveWith;

    ShutdownOptions(long id, long mutuallyExclusiveWith) {
        this.id = id;
        this.mutuallyExclusiveWith = mutuallyExclusiveWith;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public long mutuallyExclusiveWith() {
        return mutuallyExclusiveWith;
    }
}

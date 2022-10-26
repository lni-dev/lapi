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

package me.linusdev.lapi.api.templates.abstracts;

import me.linusdev.data.so.SOData;
import me.linusdev.data.so.SODatable;
import me.linusdev.lapi.api.communication.http.request.body.FilePart;
import me.linusdev.lapi.api.communication.http.request.body.LApiHttpBody;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Template} is a template, which can be sent to Discord to create an object
 */
public interface Template extends SODatable {

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

    @Override
    @NotNull SOData getData();
}

/*
 * Copyright  2022 Linus Andera
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

package me.linusdev.lapi.api.objects.interaction.response;

import me.linusdev.data.Data;
import me.linusdev.data.Datable;

/**
 * @see <a href="https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-response-structure" target="_top">Interaction Response Structure</a>
 */
public class InteractionResponse implements Datable {
    @Override
    public Data getData() {
        return null;
    }
}

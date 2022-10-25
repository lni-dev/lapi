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

package me.linusdev.lapi.api.exceptions;

import me.linusdev.lapi.api.interfaces.Requireable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MissingRequirementsException extends LApiRuntimeException {

    private @NotNull final Requireable checkedFor;
    private @NotNull final ArrayList<Requireable> missingRequirements;

    public MissingRequirementsException(@NotNull Requireable checkedFor, @NotNull Requireable missing) {
        super();
        this.checkedFor = checkedFor;
        missingRequirements = new ArrayList<>();
        missingRequirements.add(missing);
    }

    public MissingRequirementsException addMissing(@NotNull Requireable requireable) {
        missingRequirements.add(requireable);
        return this;
    }

    @Override
    public String getMessage() {
        StringBuilder ms = new StringBuilder();

        boolean first = true;
        for(Requireable r : missingRequirements) {
            if(first) first = false;
            else ms.append(", ");
            ms.append(r.getClass().getSimpleName()).append(".").append(r.toString());
        }

        return checkedFor.getClass().getSimpleName() + "." + checkedFor + " is missing the following requirements: " + ms + ".";
    }
}

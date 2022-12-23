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

package me.linusdev.lapi.api.interfaces;

import me.linusdev.lapi.api.exceptions.MissingRequirementsException;
import me.linusdev.lapi.api.lapi.LApiImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface Requireable {

    /**
     * the {@link Requireable}s required by this {@link Requireable}.
     * @return Array of {@link Requireable} or {@code null}.
     */
    @NotNull Requireable @Nullable[] requires();

    boolean isPresent(@NotNull LApiImpl lApi);

    /**
     *
     * @param requireable The {@link Requireable}, whose requirements should be checked.
     * @param lApi {@link LApiImpl}
     * @throws MissingRequirementsException if a {@link Requireable requirement} for given {@link Requireable} is missing.
     */
    @SuppressWarnings("UnnecessaryModifier")
    public static void isValid(@NotNull Requireable requireable, @NotNull LApiImpl lApi) {
        if(requireable.requires() == null) return;
        //noinspection ConstantConditions: checked by above if
        MissingRequirementsException e = isValid(requireable, requireable.requires(), lApi, null, new ArrayList<>());
        if( e != null) throw e;
    }

    private static @Nullable MissingRequirementsException isValid(@NotNull Requireable checkFor, @NotNull Requireable @NotNull[] requires, @NotNull LApiImpl lApi,
                                                        @Nullable MissingRequirementsException e, @NotNull ArrayList<Requireable> alreadyChecked) {
        loop: for(Requireable r : requires) {
            for(Requireable checked : alreadyChecked) {
                //avoid endless recursion
                if(r.equals(checked)) continue loop;
            }
            alreadyChecked.add(r);
            if(!r.isPresent(lApi)) {
                if(e == null) e = new MissingRequirementsException(checkFor, r);
                else e.addMissing(r);
            }

            if(r.requires() != null){
                //noinspection ConstantConditions: checked by above if
                e = isValid(checkFor, r.requires(), lApi, e, alreadyChecked);
            }
        }

        return e;
    }

}

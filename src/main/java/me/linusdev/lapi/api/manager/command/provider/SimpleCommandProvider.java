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

package me.linusdev.lapi.api.manager.command.provider;

import me.linusdev.lapi.api.communication.exceptions.LApiIllegalStateException;
import me.linusdev.lapi.api.lapi.LApi;
import me.linusdev.lapi.api.manager.command.BaseCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

/**
 * Simple Command Provider. Just pass all classes of your {@link BaseCommand}s in the constructor.
 */
public class SimpleCommandProvider implements CommandProvider{

    private final List<Class<? extends BaseCommand>> commandClasses;

    public SimpleCommandProvider(List<Class<? extends BaseCommand>> commandClasses) {
        this.commandClasses = commandClasses;
    }


    @NotNull
    @Override
    public Iterator<BaseCommand> iterator(@NotNull LApi lApi) {
        return new Iterator<>() {

            private Iterator<Class<? extends BaseCommand>> it = commandClasses.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public BaseCommand next() {

                Class<? extends BaseCommand> clazz = it.next();

                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new LApiIllegalStateException("Command '" + clazz.getCanonicalName()
                            + "' is missing default constructor,");
                }

            }
        };
    }
}

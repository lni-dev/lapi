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

package me.linusdev.lapi.api.other.localization;

import me.linusdev.data.Datable;
import me.linusdev.data.entry.Entry;
import me.linusdev.data.so.SOData;
import me.linusdev.lapi.api.objects.command.ApplicationCommand;
import me.linusdev.lapi.api.objects.local.Locale;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * This stores localizations for different {@link Locale locales}.
 * @see ApplicationCommand
 */
public class LocalizationDictionary implements Datable, Iterable<Localization> {

    private final @NotNull SOData data;

    public LocalizationDictionary(){
        data = SOData.newOrderedDataWithUnknownSize();
    }

    private LocalizationDictionary(@NotNull SOData data) {
        this.data = data;
    }

    @Contract(value = "null -> null; !null -> new", pure = true)
    public static @Nullable LocalizationDictionary fromData(@Nullable SOData data) {
        if(data == null) return null;
        return new LocalizationDictionary(data);
    }

    /**
     * Get localization for given locale
     * @param locale {@link Locale} to fetch localization for
     * @return {@link String} localization or {@code null} if the localization for given {@link Locale} is not set
     */
    public @Nullable String getLocalization(@NotNull Locale locale) {
        return (String) data.get(locale.getLocale());
    }

    /**
     * Set localization for given locale
     * @param locale {@link Locale} to set localization for
     * @param localization {@link String} localization to set or {@code null} to remove localization
     * @see #removeLocalization(Locale)
     */
    public void setLocalization(@NotNull Locale locale, @Nullable String localization) {
        if(localization == null) data.remove(locale.getLocale());
        else data.addOrReplace(locale.getLocale(), localization);
    }

    /**
     * Remove localization for given locale
     * @param locale {@link Locale} to remove localization for
     */
    public void removeLocalization(@NotNull Locale locale) {
        data.remove(locale.getLocale());
    }

    @Override
    public @NotNull SOData getData() {
        return data;
    }

    @NotNull
    @Override
    public Iterator<Localization> iterator() {
        return new Iterator<>() {
            private final Iterator<Entry<String, Object>> it = data.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Localization next() {
                Entry<String, Object> entry = it.next();
                return new Localization(entry.getKey(), entry.getValue().toString());
            }
        };
    }
}

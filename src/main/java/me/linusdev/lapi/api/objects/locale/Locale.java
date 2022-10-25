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

package me.linusdev.lapi.api.objects.locale;

import me.linusdev.lapi.helper.Helper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * @see <a href="https://discord.com/developers/docs/reference#locales" target="_top">Locales</a>
 */
public enum Locale {
    UNKNOWN("unknown", "unknown", "unknown"),


    DA("da", "Danish", "Dansk"),
    DE("de", "German", "Deutsch"),
    EN_GB("en-GB", "English, UK", "English, UK"),
    EN_US("en-US", "English, US", "English, US"),
    ES_ES("es-ES", "Spanish", "Español"),
    FR("fr", "French", "Français"),
    HR("hr", "Croatian", "Hrvatski"),
    IT("it", "Italian", "Italiano"),
    LT("lt", "Lithuanian", "Lietuviškai"),
    HU("hu", "Hungarian", "Magyar"),
    NL("nl", "Dutch", "Nederlands"),
    NO("no", "Norwegian", "Norsk"),
    PL("pl", "Polish", "Polski"),
    PT_BR("pt-BR", "Portuguese, Brazilian", "Português do Brasil"),
    RO("ro", "Romanian, Romania", "Română"),
    FI("fi", "Finnish", "Suomi"),
    SV_SE("sv-SE", "Swedish", "Svenska"),
    VI("vi", "Vietnamese", "Tiếng Việt"),
    TR("tr", "Turkish", "Türkçe"),
    CS("cs", "Czech", "Čeština"),
    EL("el", "Greek", "Ελληνικά"),
    BG("bg", "Bulgarian", "български"),
    RU("ru", "Russian", "Pусский"),
    UK("uk", "Ukrainian", "Українська"),
    HI("hi", "Hindi", "हिन्दी"),
    TH("th", "Thai", "ไทย"),
    ZH_CN("zh-CN", "Chinese, China", "中文"),
    JA("ja", "Japanese", "日本語"),
    ZH_TW("zh-TW", "Chinese, Taiwan", "繁體中文"),
    KO("ko", "Korean", "한국어"),


    ;

    private final @NotNull String locale;
    private final @NotNull String languageName;
    private final @NotNull String nativeName;

    Locale(@NotNull String locale, @NotNull String languageName, @NotNull String nativeName){
        this.locale = locale;
        this.languageName = languageName;
        this.nativeName = nativeName;
    }

    public @NotNull String getLocale() {
        return locale;
    }

    public @NotNull String getLanguageName() {
        return languageName;
    }

    public @NotNull String getNativeName() {
        return nativeName;
    }

    /**
     *
     * @param locale String locale or {@code null}
     * @return {@link Locale} matching given string or {@link #UNKNOWN} if none matches or {@code null} if given string was {@code null}
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    public static Locale fromString(@Nullable String locale){
        if(locale == null) return null;
        for(Locale loc : Locale.values()){
            if(loc.locale.equals(locale)) return loc;
        }

        return UNKNOWN;
    }

    /**
     * Generation for this enum
     * @see <a href="https://discord.com/developers/docs/reference#locales" target="_top">Discord Documentation</a>
     */
    @ApiStatus.Internal
    public static void main(String... args) throws URISyntaxException, IOException {
        String s = "da\tDanish\tDansk\n" +
                "de\tGerman\tDeutsch\n" +
                "en-GB\tEnglish, UK\tEnglish, UK\n" +
                "en-US\tEnglish, US\tEnglish, US\n" +
                "es-ES\tSpanish\tEspañol\n" +
                "fr\tFrench\tFrançais\n" +
                "hr\tCroatian\tHrvatski\n" +
                "it\tItalian\tItaliano\n" +
                "lt\tLithuanian\tLietuviškai\n" +
                "hu\tHungarian\tMagyar\n" +
                "nl\tDutch\tNederlands\n" +
                "no\tNorwegian\tNorsk\n" +
                "pl\tPolish\tPolski\n" +
                "pt-BR\tPortuguese, Brazilian\tPortuguês do Brasil\n" +
                "ro\tRomanian, Romania\tRomână\n" +
                "fi\tFinnish\tSuomi\n" +
                "sv-SE\tSwedish\tSvenska\n" +
                "vi\tVietnamese\tTiếng Việt\n" +
                "tr\tTurkish\tTürkçe\n" +
                "cs\tCzech\tČeština\n" +
                "el\tGreek\tΕλληνικά\n" +
                "bg\tBulgarian\tбългарски\n" +
                "ru\tRussian\tPусский\n" +
                "uk\tUkrainian\tУкраїнська\n" +
                "hi\tHindi\tहिन्दी\n" +
                "th\tThai\tไทย\n" +
                "zh-CN\tChinese, China\t中文\n" +
                "ja\tJapanese\t日本語\n" +
                "zh-TW\tChinese, Taiwan\t繁體中文\n" +
                "ko\tKorean\t한국어";

        String[] lines = s.split("\n");

        StringBuilder out = new StringBuilder();

        for(String line : lines){
            String[] split = line.split("\t");
            out.append(split[0].toUpperCase().replace("-", "_")
                    + "(\"" + split[0] + "\", \"" + split[1] + "\", \"" + split[2] + "\"),\n");
        }

        Files.writeString(Helper.getJarPath().getParent().resolve("locale-enum.txt"), out);
    }
}

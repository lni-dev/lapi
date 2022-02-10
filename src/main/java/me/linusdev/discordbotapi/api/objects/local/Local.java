package me.linusdev.discordbotapi.api.objects.local;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://discord.com/developers/docs/reference#locales" target="_top">Locales</a>
 */
public enum Local {
    UNKNOWN("unknown", "unknown"),



    EN_US("en-US", "English (United States)"),
    EN_GB("en-GB", "English (Great Britain)"),
    BG("bg", "Bulgarian"),
    ZH_CN("zh-CN", "Chinese (China)"),
    ZH_TW("zh-TW", "Chinese (Taiwan)"),
    HR("hr", "Croatian"),
    CS("cs", "Czech"),
    DA("da", "Danish"),
    NL("nl", "Dutch"),
    FI("fi", "Finnish"),
    FR("fr", "French"),
    DE("de", "German"),
    EL("el", "Greek"),
    HI("hi", "Hindi"),
    HU("hu", "Hungarian"),
    IT("it", "Italian"),
    JA("ja", "Japanese"),
    KO("ko", "Korean"),
    LT("lt", "Lithuanian"),
    NO("no", "Norwegian"),
    PL("pl", "Polish"),
    PT_BR("pt-BR", "Portuguese (Brazil)"),
    RO("ro", "Romanian"),
    RU("ru", "Russian"),
    ES_ES("es-ES", "Spanish (Spain)"),
    SV_SE("sv-SE", "Swedish"),
    TH("th", "Thai"),
    TR("tr", "Turkish"),
    UK("uk", "Ukrainian"),
    VI("vi", "Vietnamese"),

    ;

    private final String local;
    private final String languageName;

    Local(String local, String languageName){
        this.local = local;
        this.languageName = languageName;
    }

    public String getLocal() {
        return local;
    }

    public String getLanguageName() {
        return languageName;
    }

    /**
     *
     * @param local String local or {@code null}
     * @return {@link Local} matching given string or {@link #UNKNOWN} if none matches or {@code null} if given string was {@code null}
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    public static Local fromString(@Nullable String local){
        if(local == null) return null;
        for(Local loc : Local.values()){
            if(loc.local.equals(local)) return loc;
        }

        return UNKNOWN;
    }

    /**
     * Generation for this enum
     */
    @ApiStatus.Internal
    public static void main(String... args){
        String s = "en-US\tEnglish (United States)\n" +
                "en-GB\tEnglish (Great Britain)\n" +
                "bg\tBulgarian\n" +
                "zh-CN\tChinese (China)\n" +
                "zh-TW\tChinese (Taiwan)\n" +
                "hr\tCroatian\n" +
                "cs\tCzech\n" +
                "da\tDanish\n" +
                "nl\tDutch\n" +
                "fi\tFinnish\n" +
                "fr\tFrench\n" +
                "de\tGerman\n" +
                "el\tGreek\n" +
                "hi\tHindi\n" +
                "hu\tHungarian\n" +
                "it\tItalian\n" +
                "ja\tJapanese\n" +
                "ko\tKorean\n" +
                "lt\tLithuanian\n" +
                "no\tNorwegian\n" +
                "pl\tPolish\n" +
                "pt-BR\tPortuguese (Brazil)\n" +
                "ro\tRomanian\n" +
                "ru\tRussian\n" +
                "es-ES\tSpanish (Spain)\n" +
                "sv-SE\tSwedish\n" +
                "th\tThai\n" +
                "tr\tTurkish\n" +
                "uk\tUkrainian\n" +
                "vi\tVietnamese";

        String[] lines = s.split("\n");

        for(String line : lines){
            String[] split = line.split("\t");
            System.out.println(split[0].toUpperCase().replace("-", "_") + "(\"" + split[0] + "\", \"" + split[1] + "\"),");
        }
    }
}

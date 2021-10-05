package me.linusdev.discordbotapi.api.communication.lapihttprequest;

public class LApiHttpHeader {
    private final String name;
    private final String value;

    public LApiHttpHeader(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}

package rr.json;

class JsonToken {
    private final String value;
    private final JsonTokenType type;

    public JsonToken(String value, JsonTokenType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public JsonTokenType getType() {
        return type;
    }
}

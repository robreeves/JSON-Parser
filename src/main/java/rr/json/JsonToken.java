package rr.json;

public class JsonToken {
    private final Object value;
    private final JsonTokenType type;

    public JsonToken(Object value, JsonTokenType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public JsonTokenType getType() {
        return type;
    }
}

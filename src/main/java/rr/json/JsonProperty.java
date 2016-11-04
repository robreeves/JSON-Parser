package rr.json;

public class JsonProperty {
    private final String name;
    private final Object value;

    public JsonProperty(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}

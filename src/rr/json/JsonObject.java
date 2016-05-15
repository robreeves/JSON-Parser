package rr.json;

import java.util.HashMap;
import java.util.Map;

public class JsonObject {
    private final Map<String, Object> properties = new HashMap<>();

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    //TODO - get and set property methods. Since this is just a parsing exercise we don't really need these now.

    @Override
    public String toString() {
        return properties.toString();
    }
}

package rr.json;

/**
 * The public API for the JSON serializer
 */
public class Json {

    /**
     * Converts a JSON string to an object.
     * @param jsonString The JSON string representation of the object.
     * @param classType The class for the type of object that the JSON string represents.
     * @param <T> The type of object that the JSON string represents.
     * @return The deserialized object.
     */
    public static <T> T deserialize(String jsonString, Class<T> classType) {

        JsonLexer lexer = new JsonLexer(jsonString);
        JsonParser parser = new JsonParser(lexer, classType);

        return (T)parser.object();
    }
}

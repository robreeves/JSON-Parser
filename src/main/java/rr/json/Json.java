package rr.json;

public class Json {

    public static <T> T deserialize(String jsonString, Class<T> classType) {

        JsonLexer lexer = new JsonLexer(jsonString);
        JsonParser<T> parser = new JsonParser(lexer, classType);

        return parser.object();
    }
}

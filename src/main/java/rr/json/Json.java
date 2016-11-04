package rr.json;

public class Json {

    public static <T> T deserialize(String jsonString, Class<T> classType) {

        JsonLexer lexer = new JsonLexer(jsonString);
        JsonParser parser = new JsonParser(lexer);

        System.out.println(parser.object());

        return null; //todo
    }
}

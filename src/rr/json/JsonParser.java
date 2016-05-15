package rr.json;

/*
Parser rules (ANTLR notation):

object: '{' (property (',' property)*)? '}' ;
property: STRING ':' value ;
value: STRING | NUMBER ;
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Stream;

public class JsonParser {
    private final JsonLexer lexer;
    private JsonToken lookAhead;

    public JsonParser(JsonLexer lexer) {
        this.lexer = lexer;
        lookAhead = lexer.getNext();
    }

    public JsonObject object() {
        match(JsonTokenType.LCURL);

        JsonObject jsonObject = new JsonObject();
        if (lookAhead.getType() != JsonTokenType.RCURL) {
            //At least one property in JSON object
            //Get first property
            JsonProperty property = property();
            jsonObject.addProperty(property.getName(), property.getValue());

            //Get rest of properties
            while (lookAhead.getType() == JsonTokenType.COMMA) {
                match(JsonTokenType.COMMA);
                property = property();
                jsonObject.addProperty(property.getName(), property.getValue());
            }
        }

        match(JsonTokenType.RCURL);
        return jsonObject;
    }

    private JsonProperty property() {
        JsonToken propertyName = lookAhead;
        match(JsonTokenType.STRING);
        match(JsonTokenType.COLON);

        JsonToken propertyValue = lookAhead;
        match(JsonTokenType.STRING, JsonTokenType.NUMBER);

        return new JsonProperty((String)propertyName.getValue(), propertyValue.getValue());
    }

    private void consume() {
        lookAhead = lexer.getNext();
    }

    private void match(JsonTokenType... expected) {
        List<JsonTokenType> expectedOptions = Arrays.asList(expected);
        if (expectedOptions.contains(lookAhead.getType())) {
            consume();
        }
        else {
            throw new InputMismatchException(String.format("Expected token type options: '%s', Actual token type: '%s'", expectedOptions, lookAhead.getType()));
        }
    }
}

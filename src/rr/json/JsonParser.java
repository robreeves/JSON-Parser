package rr.json;

/*
Parser rules (ANTLR notation):

object: '{' (property (',' property)*)? '}' ;
property: STRING ':' value ;
value: STRING | NUMBER | object ;
 */

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

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

        JsonProperty property;
        switch (lookAhead.getType()) {
            case STRING:
            case NUMBER:
                JsonToken propertyValue = lookAhead;
                match(JsonTokenType.STRING, JsonTokenType.NUMBER);
                property = new JsonProperty((String)propertyName.getValue(), propertyValue.getValue());
                break;
            case LCURL:
                property = new JsonProperty((String)propertyName.getValue(), object());
                break;
            default:
                throw new InputMismatchException(String.format("Token type: '%s' unexpected", lookAhead.getType()));
        }

        return property;
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

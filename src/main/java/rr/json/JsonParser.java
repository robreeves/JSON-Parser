package rr.json;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Parses the input and output the object representation.
 *
 * Parser rules (ANTLR notation):
 * object: '{' (property (',' property)*)? '}' ;
 * property: STRING ':' value ; //todo allow property names that are not enclosed in quotations.
 * value: STRING | NUMBER | object ;
 */
class JsonParser<T> {
    private final JsonLexer lexer;
    private final Class<T> classType;
    private JsonToken lookAhead;

    public JsonParser(JsonLexer lexer, Class<T> classType) {
        this.lexer = lexer;
        this.classType = classType;
        lookAhead = lexer.getNext();
    }

    /**
     * Parses the input.
     * @return The object representation of the input.
     */
    public T object() {
        T object = null;

        try {
            match(JsonTokenType.LCURL);
            object = classType.newInstance();

            if (lookAhead.getType() != JsonTokenType.RCURL) {
                //At least one property in JSON object
                //Get first property
                JsonProperty property = property();
                setField(object, property);

                //Get rest of properties
                while (lookAhead.getType() == JsonTokenType.COMMA) {
                    match(JsonTokenType.COMMA);
                    property = property();
                    setField(object, property);
                }
            }

            match(JsonTokenType.RCURL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return object;
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

    private void setField(T object, JsonProperty jsonProperty) throws NoSuchFieldException, IllegalAccessException {
        Field field = classType.getField(jsonProperty.getName());
        field.set(object, jsonProperty.getValue());
    }
}

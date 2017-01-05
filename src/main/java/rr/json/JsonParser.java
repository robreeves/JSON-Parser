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
class JsonParser {

    /**
     * The tokenizer for the input JSON string
     */
    private final JsonLexer lexer;

    /**
     * The class for the type of Java object to create
     */
    private final Class<?> classType;

    /**
     * The next token to process in the input sequence
     */
    private JsonToken lookAhead;

    public JsonParser(JsonLexer lexer, Class<?> classType) {
        this.lexer = lexer;
        this.classType = classType;
        lookAhead = lexer.getNext();
    }

    /**
     * Parses the input.
     * @return The object representation of the input. If the input is invalid, null will be returned.
     */
    public Object object() {
        Object object = null;

        try {
            match(JsonTokenType.LCURL);
            object = classType.newInstance();

            if (lookAhead.getType() != JsonTokenType.RCURL) {
                //There is at least one property in the JSON object
                //Get the first property
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

    /**
     * Creates a property from the input.
     * Rule:
     * property: STRING ':' value ;
     * @return
     */
    private JsonProperty property() {
        JsonToken propertyName = lookAhead;
        match(JsonTokenType.STRING);
        match(JsonTokenType.COLON);

        JsonProperty property;
        switch (lookAhead.getType()) {
            case STRING:
            case NUMBER:
                //The value is a primitive
                JsonToken propertyValue = lookAhead;
                match(JsonTokenType.STRING, JsonTokenType.NUMBER);
                property = new JsonProperty((String)propertyName.getValue(), propertyValue.getValue());
                break;
            case LCURL:
                //The value is an object
                property = new JsonProperty((String)propertyName.getValue(), object());
                break;
            default:
                throw new InputMismatchException(String.format("Token type: '%s' unexpected", lookAhead.getType()));
        }

        return property;
    }

    /**
     * Moves the look ahead to the next token in the sequence
     */
    private void consume() {
        lookAhead = lexer.getNext();
    }

    /**
     * Matches the look ahead token to verify that it is the expected token type
     * @param expected
     */
    private void match(JsonTokenType... expected) {
        List<JsonTokenType> expectedOptions = Arrays.asList(expected);
        if (expectedOptions.contains(lookAhead.getType())) {
            consume();
        }
        else {
            throw new InputMismatchException(String.format("Expected token type options: '%s', Actual token type: '%s'", expectedOptions, lookAhead.getType()));
        }
    }

    private void setField(Object object, JsonProperty jsonProperty) throws NoSuchFieldException, IllegalAccessException {
        Field field = classType.getField(jsonProperty.getName());
        Object fieldValue = field.getType().cast(jsonProperty.getValue());
        field.set(object, fieldValue);
    }
}

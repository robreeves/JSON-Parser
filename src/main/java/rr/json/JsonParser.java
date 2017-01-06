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
        return object(classType);
    }

    private Object object(Class<?> classType) {
        Object object = null;

        try {
            match(JsonTokenType.LCURL);
            object = classType.newInstance();

            if (lookAhead.getType() != JsonTokenType.RCURL) {
                //There is at least one property in the JSON object
                //Get the first property
                property(object);

                //Get rest of properties
                while (lookAhead.getType() == JsonTokenType.COMMA) {
                    match(JsonTokenType.COMMA);
                    property(object);
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
    private void property(Object outputObj) throws Exception {
        JsonToken propertyNameToken = lookAhead;
        match(JsonTokenType.STRING);
        match(JsonTokenType.COLON);

        Object propertyValue;
        Field field = outputObj.getClass().getField(propertyNameToken.getValue());

        switch (lookAhead.getType()) {
            case STRING:
                propertyValue = lookAhead.getValue();
                match(JsonTokenType.STRING);
                break;
            case NUMBER:
                //The value is a primitive
                propertyValue = Convert.toObject(lookAhead.getValue(), field.getType());
                match(JsonTokenType.NUMBER);
                break;
            case LCURL:
                //The value is an object
                propertyValue = object(field.getType());
                break;
            default:
                throw new InputMismatchException(String.format("Token type: '%s' unexpected", lookAhead.getType()));
        }

        field.set(outputObj, propertyValue);
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
}

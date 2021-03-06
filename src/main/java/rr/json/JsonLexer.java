package rr.json;

import java.util.InputMismatchException;

/**
 * Converts the input JSON string into tokens.
 *
 * Lexer rules:
 *
 * LCURL: '{' ;
 * RCURL: '}' ;
 * COMMA: ',' ;
 * COLON: ':' ;
 * NUMBER: [0-9]* '.' [0-9]* ;
 * STRING: '"' .* '"' ; TODO - right now this does not account for special characters
 *
 */
class JsonLexer {
    private final String input;
    private int lookAheadIndex = -1;
    private int lookAhead;
    private static final int EOF = -1;

    public JsonLexer(String input) {
        this.input = input;
        consume();
    }

    /**
     * Gets the next token in the input
     * @return The next token.
     */
    public JsonToken getNext() {
        while (lookAhead != EOF) {
            switch (lookAhead) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    //Ignore formatting characters
                    consume();
                    break;
                case '{':
                    matchChar('{');
                    return new JsonToken(null, JsonTokenType.LCURL);
                case '}':
                    matchChar('}');
                    return new JsonToken(null, JsonTokenType.RCURL);
                case ',':
                    matchChar(',');
                    return new JsonToken(null, JsonTokenType.COMMA);
                case ':':
                    matchChar(':');
                    return new JsonToken(null, JsonTokenType.COLON);
                case '"':
                    matchChar('"');
                    String stringValue = string();
                    matchChar('"');
                    return new JsonToken(stringValue, JsonTokenType.STRING);
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                    return new JsonToken(number(), JsonTokenType.NUMBER);
                default:
                    throw new InputMismatchException(String.format("Character: '%c' at look ahead index %d does not match anything in language.", (char)lookAhead, lookAheadIndex));
            }
        }

        return new JsonToken(null, JsonTokenType.EOF);
    }

    /**
     * Gets a string value from the input
     * @return The string value
     */
    private String string() {
        StringBuilder buffer = new StringBuilder();
        while (lookAhead != '"' && lookAhead != EOF) {
            buffer.append((char)lookAhead);
            consume();
        }

        return buffer.toString();
    }

    /**
     * Helper method to get the integer text from the input
     * @return The string representation of the int in the input.
     */
    private String intText() {
        StringBuilder buffer = new StringBuilder();

        while (lookAheadIsInt()) {
            buffer.append((char)lookAhead);
            consume();
        }

        return buffer.toString();
    }

    /**
     * Helper to check if the look ahead is an int character
     * @return True if the look ahead is an int character
     */
    private boolean lookAheadIsInt() {
        return lookAhead >= '0' && lookAhead <= '9';
    }

    /**
     * Extracts the string representation of the numeric value
     * @return The numeric string
     */
    private String number() {
        StringBuilder buffer = new StringBuilder(intText());

        if (lookAhead == '.') {
            consume();
            buffer.append('.');
            buffer.append(intText());
        }

        return buffer.toString();
    }

    /**
     * Consumes a character and sets the next look ahead character.
     */
    private void consume() {
        //Consume character
        ++lookAheadIndex;

        //Set new lookahead character
        if (lookAheadIndex < input.length()) {
            lookAhead = input.charAt(lookAheadIndex);
        }
        else {
            lookAhead = EOF;
        }
    }

    /**
     * Asserts that the look ahead matches the expected character and if so consumes a character.
     * @param expected The expected look ahead character
     */
    private void matchChar(int expected) {
        if (lookAhead == expected) {
            consume();
        }
        else {
            throw new InputMismatchException(String.format("Expected character: '%c', Actual character: '%c', Look ahead index: %d", (char)expected, (char)lookAhead, lookAheadIndex));
        }
    }
}

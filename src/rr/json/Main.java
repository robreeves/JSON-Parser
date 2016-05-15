package rr.json;

public class Main {

    public static void main(String[] args) {
	    if (args.length == 1) {
            JsonLexer lexer = new JsonLexer(args[0]);

            JsonToken token = lexer.getNext();
            while (token.getType() != JsonTokenType.EOF) {
                System.out.println(String.format("Type: %s, Value: %s", token.getType(), token.getValue()));
                token = lexer.getNext();
            }
        }
        else {
            System.err.println("***Invalid input***");
            printUsage();
        }
    }

    private static void printUsage() {
        //TODO
    }
}

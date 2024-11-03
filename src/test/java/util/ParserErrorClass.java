package util;

public class ParserErrorClass {

    private final String ref;
    private final String expectedFormat;
    private final String message;

    public ParserErrorClass(String ref, String expectedFormat, String message) {
        this.ref = ref;
        this.expectedFormat = expectedFormat;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

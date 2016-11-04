package rr.json;

import org.junit.Test;

public class JsonTests {
    @Test
    public void dev() throws Exception {
        SimpleType output = Json.deserialize("{\"id\":\"abc\", \"code\":123}", SimpleType.class);
    }

    private class SimpleType {
        public String id;
        public int code;
    }
}

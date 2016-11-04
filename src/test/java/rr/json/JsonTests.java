package rr.json;

import org.junit.Assert;
import org.junit.Test;
import rr.json.types.SimpleType;

public class JsonTests {
    @Test
    public void simpleType() throws Exception {
        SimpleType output = Json.deserialize("{\"id\":\"abc\", \"code\":123}", SimpleType.class);
        Assert.assertEquals("abc", output.id);
        Assert.assertEquals(123, output.code);
    }
}

package rr.json;

import org.junit.Assert;
import org.junit.Test;
import rr.json.types.ComplexType;
import rr.json.types.SimpleType;

public class JsonTests {
    @Test
    public void simpleType() throws Exception {
        SimpleType output = Json.deserialize("{\"id\":\"abc\", \"code\":123}", SimpleType.class);
        Assert.assertEquals("abc", output.id);
        Assert.assertEquals((Integer) 123, output.code);
    }

    @Test
    public void complexType() throws Exception {
        ComplexType output = Json.deserialize("{\"id\":1.23, \"obj\": {\"id\":\"abc\", \"code\":123} }", ComplexType.class);
        Assert.assertEquals((Double) 1.23, output.id);
        Assert.assertEquals("abc", output.obj.id);
        Assert.assertEquals((Integer) 123, output.obj.code);
    }
}

package com.mooreb.config.client.fastproperty;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class BooleanPropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        String p2Name = "test.name." + UUID.randomUUID().toString();
        BooleanProperty p1 = BooleanProperty.findOrCreate(p1Name, true);
        BooleanProperty p2 = BooleanProperty.findOrCreate(p2Name, false);
        Assert.assertTrue("p1 default value is true, should get true",p1.get());
        Assert.assertFalse("p2 default value is false, should get false",p2.get());
    }
}
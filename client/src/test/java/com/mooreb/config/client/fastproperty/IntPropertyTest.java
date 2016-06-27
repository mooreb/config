package com.mooreb.config.client.fastproperty;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class IntPropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        String p2Name = "test.name." + UUID.randomUUID().toString();
        String p3Name = "test.name." + UUID.randomUUID().toString();
        IntProperty p1 = IntProperty.findOrCreate(p1Name, -12345);
        IntProperty p2 = IntProperty.findOrCreate(p2Name, 0);
        IntProperty p3 = IntProperty.findOrCreate(p3Name, 56789);
        Assert.assertEquals("p1 default value is -12345, should get the same", -12345, p1.get());
        Assert.assertEquals("p2 default value is 0, should get the same", 0, p2.get());
        Assert.assertEquals("p3 default value is 56789, should get the same", 56789, p3.get());
    }
}
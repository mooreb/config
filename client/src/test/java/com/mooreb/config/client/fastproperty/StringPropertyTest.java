package com.mooreb.config.client.fastproperty;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class StringPropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        final String p1Value = "only_the_shadow_knows";
        StringProperty p1 = StringProperty.findOrCreate(p1Name, p1Value);
        Assert.assertEquals("p1 default value is only_the_shadow_knows, should get the same", p1Value, p1.get());
    }
}
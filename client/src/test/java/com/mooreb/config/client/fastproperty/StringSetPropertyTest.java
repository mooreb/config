package com.mooreb.config.client.fastproperty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class StringSetPropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        final Set<String> p1Value = new HashSet<String>();
        p1Value.add("red");
        p1Value.add("orange");
        p1Value.add("yellow");
        p1Value.add("green");
        p1Value.add("blue");
        p1Value.add("indigo");
        p1Value.add("violet");
        StringSetProperty p1 = StringSetProperty.findOrCreate(p1Name, p1Value);
        Assert.assertEquals("p1 default value is a rainbow, should get the same", p1Value, p1.get());
        Assert.assertTrue("p1 should contain this color", p1.get().contains("red"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("orange"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("yellow"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("green"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("blue"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("indigo"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("violet"));
    }
}
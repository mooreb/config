package com.mooreb.config.client.fastproperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class StringListPropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        final List<String> p1Value = new ArrayList<String>();
        p1Value.add("red");
        p1Value.add("orange");
        p1Value.add("yellow");
        p1Value.add("green");
        p1Value.add("blue");
        p1Value.add("indigo");
        p1Value.add("violet");
        p1Value.add("red");
        p1Value.add("orange");
        p1Value.add("yellow");
        p1Value.add("green");
        p1Value.add("blue");
        p1Value.add("indigo");
        p1Value.add("violet");
        StringListProperty p1 = StringListProperty.findOrCreate(p1Name, p1Value);
        Assert.assertEquals("p1 default value is a DOUBLE rainbow, should get the same", p1Value, p1.get());
        Assert.assertTrue("p1 should contain this color", p1.get().contains("red"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("orange"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("yellow"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("green"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("blue"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("indigo"));
        Assert.assertTrue("p1 should contain this color", p1.get().contains("violet"));
        Assert.assertTrue("duplicates are allowed, should have 14 members", 14 == p1.get().size());
    }
}
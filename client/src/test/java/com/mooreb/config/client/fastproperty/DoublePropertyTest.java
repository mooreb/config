package com.mooreb.config.client.fastproperty;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class DoublePropertyTest {
    @Test
    public void testDefaultGetters() {
        String p1Name = "test.name." + UUID.randomUUID().toString();
        final double p1Value = 1234.5678;
        DoubleProperty p1 = DoubleProperty.findOrCreate(p1Name, p1Value);
        double fetchedValue = p1.get();
        double epsilon = 1e-6;
        double lowerBound = p1Value - epsilon;
        double upperBound = p1Value + epsilon;
        boolean inRange = ((lowerBound < fetchedValue) && (fetchedValue < upperBound));
        Assert.assertTrue("p1 default value is 1234.5678, should get the same", inRange);
    }
}
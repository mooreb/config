package com.mooreb.config.client.fastproperty.holder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.DoubleProperty;
import com.mooreb.config.client.fastproperty.holder.DoublePropertyHolder;

public class DoublePropertyHolderTest {

  @Test
  public void testGetValue() {
    double propValue = 12345.12;
    DoublePropertyHolder holder = new DoublePropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get(), 0);
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    double propValue = 123.987;
    DoubleProperty p1 = DoubleProperty.findOrCreate(propName, propValue);
    DoublePropertyHolder holder = new DoublePropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get(), 0);
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    DoubleProperty nullProp = null;
    new DoublePropertyHolder(nullProp);
  }
}

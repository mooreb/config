package com.mooreb.config.client.fastproperty.holder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.IntProperty;
import com.mooreb.config.client.fastproperty.holder.IntPropertyHolder;

public class IntPropertyHolderTest {

  @Test
  public void testGetValue() {
    int propValue = 15;
    IntPropertyHolder holder = new IntPropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    int propValue = 123456789;
    IntProperty p1 = IntProperty.findOrCreate(propName, propValue);
    IntPropertyHolder holder = new IntPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    IntProperty nullProp = null;
    new IntPropertyHolder(nullProp);
  }
}

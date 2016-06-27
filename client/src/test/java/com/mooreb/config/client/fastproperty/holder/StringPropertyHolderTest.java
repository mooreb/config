package com.mooreb.config.client.fastproperty.holder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.StringProperty;
import com.mooreb.config.client.fastproperty.holder.StringPropertyHolder;

public class StringPropertyHolderTest {

  @Test
  public void testGetValue() {
    String propValue = "a string";
    StringPropertyHolder holder = new StringPropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    String propValue = "propValue";
    StringProperty p1 = StringProperty.findOrCreate(propName, propValue);
    StringPropertyHolder holder = new StringPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    StringProperty nullProp = null;
    new StringPropertyHolder(nullProp);
  }
}

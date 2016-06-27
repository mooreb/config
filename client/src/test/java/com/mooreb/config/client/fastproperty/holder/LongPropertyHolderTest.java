package com.mooreb.config.client.fastproperty.holder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.LongProperty;
import com.mooreb.config.client.fastproperty.holder.LongPropertyHolder;

public class LongPropertyHolderTest {

  @Test
  public void testGetValue() {
    long propValue = 31231231234L;
    LongPropertyHolder holder = new LongPropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    long propValue = -98765432198765L;
    LongProperty p1 = LongProperty.findOrCreate(propName, propValue);
    LongPropertyHolder holder = new LongPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    LongProperty nullProp = null;
    new LongPropertyHolder(nullProp);
  }
}

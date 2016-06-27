package com.mooreb.config.client.fastproperty.holder;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.BooleanProperty;
import com.mooreb.config.client.fastproperty.holder.BooleanPropertyHolder;

public class BooleanPropertyHolderTest {

  @Test
  public void testGetValue() {
    boolean propValueTrue = true;
    BooleanPropertyHolder holderTrue = new BooleanPropertyHolder(propValueTrue);
    Assert.assertEquals(propValueTrue, holderTrue.get());

    boolean propValueFalse = false;
    BooleanPropertyHolder holderFalse = new BooleanPropertyHolder(propValueFalse);
    Assert.assertEquals(propValueFalse, holderFalse.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    boolean propValue = true;
    BooleanProperty p1 = BooleanProperty.findOrCreate(propName, propValue);
    BooleanPropertyHolder holder = new BooleanPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    BooleanProperty nullProp = null;
    new BooleanPropertyHolder(nullProp);
  }
}

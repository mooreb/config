package com.mooreb.config.client.fastproperty.holder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.StringListProperty;
import com.mooreb.config.client.fastproperty.holder.StringListPropertyHolder;

public class StringListPropertyHolderTest {

  @Test
  public void testGetValue() {
    List<String> propValue = Arrays.asList("firstString", "secondString", "");
    StringListPropertyHolder holder = new StringListPropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    List<String> propValue = Arrays.asList("firstString", "secondString", "", "4");
    StringListProperty p1 = StringListProperty.findOrCreate(propName, propValue);
    StringListPropertyHolder holder = new StringListPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    StringListProperty nullProp = null;
    new StringListPropertyHolder(nullProp);
  }
}

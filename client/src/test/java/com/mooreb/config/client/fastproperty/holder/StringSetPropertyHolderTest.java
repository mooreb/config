package com.mooreb.config.client.fastproperty.holder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.mooreb.config.client.fastproperty.StringSetProperty;
import com.mooreb.config.client.fastproperty.holder.StringSetPropertyHolder;

public class StringSetPropertyHolderTest {

  @Test
  public void testGetValue() {
    Set<String> propValue = new HashSet<String>(Arrays.asList("string1", "string2", ""));
    StringSetPropertyHolder holder = new StringSetPropertyHolder(propValue);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test
  public void testGetProperty() {
    String propName = "test.name." + UUID.randomUUID().toString();
    Set<String> propValue = new HashSet<String>(Arrays.asList("string1", "string2", "", "4"));
    StringSetProperty p1 = StringSetProperty.findOrCreate(propName, propValue);
    StringSetPropertyHolder holder = new StringSetPropertyHolder(p1);
    Assert.assertEquals(propValue, holder.get());
  }

  @Test(expected = NullPointerException.class)
  public void testPropertyNull() {
    StringSetProperty nullProp = null;
    new StringSetPropertyHolder(nullProp);
  }
}

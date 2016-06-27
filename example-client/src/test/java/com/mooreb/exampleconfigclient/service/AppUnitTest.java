package com.mooreb.exampleconfigclient.service;

import org.junit.Assert;
import org.junit.Test;

public class AppUnitTest {

  @Test
  public void testPass() {
    Assert.assertTrue(true);
  }
  
  // @Test
  public void testFail() {
    Assert.assertTrue(false);
  }
  
  @Test(expected=NullPointerException.class) 
  public void expectedExceptionTest() {
    Object o = null;
    System.out.println(o.hashCode());
  }
}
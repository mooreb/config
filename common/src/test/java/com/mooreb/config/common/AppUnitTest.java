package com.mooreb.config.common;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppUnitTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppUnitTest.class);
  @Test
  public void testPass() {
    Assert.assertTrue(true);
  }

  @Test @Ignore("run this test if you have lost confidence that your tests are running properly. This one should fail.")
  public void testFail() {
    Assert.assertTrue(false);
  }

  @Test
  public void testLogger() {
    LOGGER.info("The logger is working!");
  }

  @Test(expected=NullPointerException.class) 
  public void expectedExceptionTest() {
    Object o = null;
    System.out.println(o.hashCode());
  }

}
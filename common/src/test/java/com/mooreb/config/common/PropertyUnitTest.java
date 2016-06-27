package com.mooreb.config.common;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class PropertyUnitTest {
  
  @Test
  public void testParseContextSuccess() {
    Property property = createProperty("host=lvdma1006|env=FastFeedback|app=exciting-app!");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("host", "lvdma1006");
    expectedMap.put("env", "FastFeedback");
    expectedMap.put("app", "exciting-app!");
    
    Assert.assertEquals(expectedMap, actualMap);
  }
  
  @Test
  public void testParseContextSuccessMultiplePipes() {
    Property property = createProperty("|||a=1|b=2|||||c=3||||d=4||");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("a", "1");
    expectedMap.put("b", "2");
    expectedMap.put("c", "3");
    expectedMap.put("d", "4");
    
    Assert.assertEquals(expectedMap, actualMap);
  }
  
  @Test
  public void testParseContextSuccessPunctuation() {
    Property property = createProperty("app=(£${}exciting-\"app\"!)|host=<odd>:[port]");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("app", "(£${}exciting-\"app\"!)");
    expectedMap.put("host", "<odd>:[port]");
    
    Assert.assertEquals(expectedMap, actualMap);
  }  
  
  @Test
  public void testParseContextSuccessSingle() {
    Property property = createProperty("env=Dev");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("env", "Dev");
    
    Assert.assertEquals(expectedMap, actualMap);
  }
  
  @Test
  public void testParseContextSuccessMalformedNoEqualsIgnored() {
    Property property = createProperty("malformed|env=Dev");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("env", "Dev");
    
    Assert.assertEquals(expectedMap, actualMap);
  }
  
  @Test
  public void testParseContextSuccessMultiEquals() {
    Property property = createProperty("multi=equals=here");
    Map<String, String> actualMap = property.parseContext(); 
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    expectedMap.put("multi", "equals=here");
    
    Assert.assertEquals(expectedMap, actualMap);
  }   
  
  @Test
  public void testParseContextFailures() {
    //all total failures map to empty map, no exceptions thrown 
    Map<String, String> emptyStringMap = createProperty("").parseContext();
    Map<String, String> nullMap = createProperty(null).parseContext(); 
    Map<String, String> singleMalformedMap = createProperty("malformed").parseContext();
    Map<String, String> allMalformedMap = createProperty("malformed|1234").parseContext();
    
    Map<String, String> expectedMap = new HashMap<String, String>();
    
    Assert.assertEquals(expectedMap, emptyStringMap);
    Assert.assertEquals(expectedMap, nullMap);
    Assert.assertEquals(expectedMap, singleMalformedMap);
    Assert.assertEquals(expectedMap, allMalformedMap);
  }  

  private Property createProperty(String context) {
    return new Property("", context, "", "", "");
  }
  
}

package com.mooreb.config.client.fastproperty.holder;

import com.mooreb.config.client.fastproperty.StringProperty;

/**
 * <p>
 * Holds a StringProperty, or a String. In the case where a class accepts a config property or an
 * override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class StringPropertyHolder {

  private final StringProperty property;
  private final String value;

  /**
   * @param property Not null
   */
  public StringPropertyHolder(StringProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = null;
    this.property = property;
  }

  public StringPropertyHolder(String value) {
    this.value = value;
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see StringProperty#get()
   * @return the wrapped property value
   */
  public String get() {
    return property != null ? property.get() : value;
  }
}

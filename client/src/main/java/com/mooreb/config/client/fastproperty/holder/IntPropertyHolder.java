package com.mooreb.config.client.fastproperty.holder;

import com.mooreb.config.client.fastproperty.IntProperty;

/**
 * <p>
 * Holds a IntProperty, or a int. In the case where a class accepts a config property or an
 * override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class IntPropertyHolder {

  private final IntProperty property;
  private final int value;

  /**
   * @param property Not null
   */
  public IntPropertyHolder(IntProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = 0;
    this.property = property;
  }

  public IntPropertyHolder(int value) {
    this.value = value;
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see IntProperty#get()
   * @return the wrapped property value
   */
  public int get() {
    return property != null ? property.get() : value;
  }
}

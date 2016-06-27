package com.mooreb.config.client.fastproperty.holder;

import com.mooreb.config.client.fastproperty.LongProperty;

/**
 * <p>
 * Holds a LongProperty, or a long. In the case where a class accepts a config property or an
 * override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class LongPropertyHolder {

  private final LongProperty property;
  private final long value;

  /**
   * @param property Not null
   */
  public LongPropertyHolder(LongProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = 0;
    this.property = property;
  }

  public LongPropertyHolder(long value) {
    this.value = value;
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see LongProperty#get()
   * @return the wrapped property value
   */
  public long get() {
    return property != null ? property.get() : value;
  }
}

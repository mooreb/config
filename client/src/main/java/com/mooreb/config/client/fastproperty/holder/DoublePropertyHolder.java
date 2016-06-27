package com.mooreb.config.client.fastproperty.holder;

import com.mooreb.config.client.fastproperty.DoubleProperty;

/**
 * <p>
 * Holds a DoubleProperty, or a double. In the case where a class accepts a config property or an
 * override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class DoublePropertyHolder {

  private final DoubleProperty property;
  private final double value;

  /**
   * @param property Not null
   */
  public DoublePropertyHolder(DoubleProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = 0;
    this.property = property;
  }

  public DoublePropertyHolder(double value) {
    this.value = value;
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see DoubleProperty#get()
   * @return the wrapped property value
   */
  public double get() {
    return property != null ? property.get() : value;
  }
}

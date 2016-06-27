package com.mooreb.config.client.fastproperty.holder;

import com.mooreb.config.client.fastproperty.BooleanProperty;

/**
 * <p>
 * Holds a BooleanProperty, or a boolean. In the case where a class accepts a config property or an
 * override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class BooleanPropertyHolder {

  private final BooleanProperty property;
  private final boolean value;

  /**
   * @param property Not null
   */
  public BooleanPropertyHolder(BooleanProperty property) {
    if (property == null){
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = false;
    this.property = property;
  }
  
  public BooleanPropertyHolder(boolean value) {
    this.value = value;
    this.property = null;
  }  

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * @see BooleanProperty#get()
   * @return the wrapped property value
   */
  public boolean get() {
    return property != null ? property.get() : value;
  }
}

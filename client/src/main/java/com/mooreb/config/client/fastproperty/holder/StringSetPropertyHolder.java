package com.mooreb.config.client.fastproperty.holder;

import java.util.Collections;
import java.util.Set;

import com.mooreb.config.client.fastproperty.StringSetProperty;

/**
 * <p>
 * Holds a StringSetProperty, or a Set&lt;String&gt;. In the case where a class accepts a config
 * property or an override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class StringSetPropertyHolder {

  private final StringSetProperty property;
  private final Set<String> value;

  /**
   * @param property Not null
   */
  public StringSetPropertyHolder(StringSetProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = null;
    this.property = property;
  }

  public StringSetPropertyHolder(Set<String> value) {
    this.value = Collections.unmodifiableSet(value);
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see StringSetProperty#get()
   * @return the wrapped property value
   */
  public Set<String> get() {
    return property != null ? property.get() : value;
  }
}

package com.mooreb.config.client.fastproperty.holder;

import java.util.Collections;
import java.util.List;

import com.mooreb.config.client.fastproperty.StringListProperty;

/**
 * <p>
 * Holds a StringListProperty, or a List&lt;String&gt;. In the case where a class accepts a config
 * property or an override, this class can be injected to allow consistent access to either.
 * <p/>
 * Note the comments on memoization inside each FastProperty, ensure the full FastProperty is held
 * rather than the result of the .get() call.
 */
public class StringListPropertyHolder {

  private final StringListProperty property;
  private final List<String> value;

  /**
   * @param property Not null
   */
  public StringListPropertyHolder(StringListProperty property) {
    if (property == null) {
      throw new NullPointerException("Property passed to holder can not be null");
    }
    this.value = null;
    this.property = property;
  }

  public StringListPropertyHolder(List<String> value) {
    this.value = Collections.unmodifiableList(value);
    this.property = null;
  }

  /**
   * Note the comments on memoization as get() should be called on each access to a FastProperty.
   * 
   * @see StringListProperty#get()
   * @return the wrapped property value
   */
  public List<String> get() {
    return property != null ? property.get() : value;
  }
}

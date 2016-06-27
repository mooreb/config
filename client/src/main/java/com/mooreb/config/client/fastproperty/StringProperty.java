package com.mooreb.config.client.fastproperty;

import com.mooreb.config.client.StaticContainer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringProperty extends FastProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringProperty.class);
    private final String defaultValue;
    private final AtomicReference<String> currentValue; // a touch overkill, but keeps the pattern
    private static final ConcurrentHashMap<String, StringProperty> properties = new ConcurrentHashMap<String, StringProperty>();

    /**
     * It is necessary to register each subtype of FastProperty with the fetcher
     */
    static {
        StaticContainer.getFetcherInstance().register(properties);
    }

    /**
     * This constructor is not intended to be called except by this class;
     * instead please call #findOrCreate(propertyName, defaultValue).
     * @param propertyName
     * @param defaultValue
     */
    private StringProperty(String propertyName, String defaultValue) {
        super(propertyName);
        this.defaultValue = defaultValue;
        this.currentValue = new AtomicReference<String>(defaultValue);
    }

    /**
     * This method simplifies use for the caller by relieving them from the burden of
     * needing to know if a property of a given name can be created from whole cloth
     * and the values are tied together in some layer underneath, or if it must be reused;
     * in the latter case the property must be threaded through from construction to use,
     * which could be really painful for the caller.
     *
     * import com.mooreb.config.client.fastproperty.StringProperty;
     * ...
     * StringProperty myProperty = StringProperty.findOrCreate("my.favorite.color", "blue");
     * ...
     * if("green".equals(myProperty.get())) {
     *     ...
     * }
     * else {
     *     ...
     * }
     *
     * @param propertyName the name of the property, used to associate a value with a name
     * @param defaultValue this is the value returned by get() before the service can be reached,
     *                     or the value returned when the service does not override the value for propertyName.
     * @return a property on which get() may be called.
     */
    public static StringProperty findOrCreate(String propertyName, String defaultValue) {
        StringProperty retval;
        if(properties.containsKey(propertyName)) {
            retval = properties.get(propertyName);
        }
        else {
            StringProperty newProperty = new StringProperty(propertyName, defaultValue);
            properties.put(propertyName, newProperty);
            StaticContainer.getFetcherInstance().assign(newProperty);
            retval = newProperty;
        }
        return retval;
    }

    /**
     * It is critical that the return value from this method is not memoized,
     * but instead get() is called for every retrieval. The implementation will ensure
     * that get() is constant-time and require on the close order of a few machine cycles.
     *
     * import com.mooreb.config.client.fastproperty.StringProperty;
     * ...
     * StringProperty myProperty = StringProperty.findOrCreate("my.favorite.color", "blue");
     * ...
     * if("green".equals(myProperty.get())) {
     *     ...
     * }
     * else {
     *     ...
     * }
     *
     * @return the current value associated with this property.
     */
    public String get() {
        return currentValue.get();
    }

    @Override
    public void resetToDefault() {
        currentValue.set(defaultValue);
    }

    // package scope
    @Override
    void assignFromString(final String string) {
        if(null == string) {
            LOGGER.error("cannot assign from a null string: propertyName: {}", propertyName);
            return;
        }
        currentValue.set(string);
    }

}

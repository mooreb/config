package com.mooreb.config.client.fastproperty;

import com.mooreb.config.client.StaticContainer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringSetProperty extends FastProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringSetProperty.class);
    private final Set<String> defaultValue;
    private final AtomicReference<Set<String>> currentValue; // a touch overkill, but keeps the pattern
    private static final ConcurrentHashMap<String, StringSetProperty> properties = new ConcurrentHashMap<String, StringSetProperty>();

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
    private StringSetProperty(String propertyName, Set<String> defaultValue) {
        super(propertyName);
        this.defaultValue = Collections.unmodifiableSet(new HashSet(defaultValue));
        this.currentValue = new AtomicReference<Set<String>>(this.defaultValue);
    }

    /**
     * This method simplifies use for the caller by relieving them from the burden of
     * needing to know if a property of a given name can be created from whole cloth
     * and the values are tied together in some layer underneath, or if it must be reused;
     * in the latter case the property must be threaded through from construction to use,
     * which could be really painful for the caller.
     *
     * import com.mooreb.config.client.fastproperty.StringSetProperty;
     * ...
     * Set<String> tmp = new HashSet<String>();
     * tmp.add("red");
     * tmp.add("white");
     * tmp.add("blue");
     * Set<String> stringSet = Collections.unmodifiableSet(tmp);
     * StringSetProperty myProperty = StringSetProperty.findOrCreate("my.favorite.colors", stringSet);
     * ...
     * if(myProperty.get().contains("orange")) {
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
    public static StringSetProperty findOrCreate(String propertyName, Set<String> defaultValue) {
        StringSetProperty retval;
        if(properties.containsKey(propertyName)) {
            retval = properties.get(propertyName);
        }
        else {
            StringSetProperty newProperty = new StringSetProperty(propertyName, defaultValue);
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
     * import com.mooreb.config.client.fastproperty.StringSetProperty;
     * ...
     * Set<String> tmp = new HashSet<String>();
     * tmp.add("red");
     * tmp.add("white");
     * tmp.add("blue");
     * Set<String> stringSet = Collections.unmodifiableSet(tmp);
     * StringSetProperty myProperty = StringSetProperty.findOrCreate("my.favorite.colors", stringSet);
     * ...
     * if(myProperty.get().contains("orange")) {
     *     ...
     * }
     * else {
     *     ...
     * }
     *
     * @return the current value associated with this property.
     */
    public Set<String> get() {
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
        final Set<String> s = new HashSet<String>();
        String[] tokens = string.split(","); // Potential BUG: hardcoded comma
        for(final String token : tokens) {
            s.add(token);
        }
        currentValue.set(Collections.unmodifiableSet(s));
    }
}

package com.mooreb.config.client.fastproperty;

import com.mooreb.config.client.StaticContainer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongProperty extends FastProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongProperty.class);
    private final long defaultValue;
    private final AtomicLong currentValue;
    private static final ConcurrentHashMap<String, LongProperty> properties = new ConcurrentHashMap<String, LongProperty>();

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
    private LongProperty(String propertyName, long defaultValue) {
        super(propertyName);
        this.defaultValue = defaultValue;
        this.currentValue = new AtomicLong(defaultValue);
    }

    /**
     * This method simplifies use for the caller by relieving them from the burden of
     * needing to know if a property of a given name can be created from whole cloth
     * and the values are tied together in some layer underneath, or if it must be reused;
     * in the latter case the property must be threaded through from construction to use,
     * which could be really painful for the caller.
     *
     * import com.mooreb.config.client.fastproperty.LongProperty;
     * ...
     * LongProperty myProperty = LongProperty.findOrCreate("my.longproperty.name", 34359738368); // 2^35
     * ...
     * if(myProperty.get() > Integer.MAX_VALUE) {
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
    public static LongProperty findOrCreate(String propertyName, long defaultValue) {
        LongProperty retval;
        if(properties.containsKey(propertyName)) {
            retval = properties.get(propertyName);
        }
        else {
            LongProperty newProperty = new LongProperty(propertyName, defaultValue);
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
     * import com.mooreb.config.client.fastproperty.LongProperty;
     * ...
     * LongProperty myProperty = LongProperty.findOrCreate("my.longproperty.name", 34359738368); // 2^35
     * ...
     * if(myProperty.get() > Integer.MAX_VALUE) {
     *     ...
     * }
     * else {
     *     ...
     * }
     *
     * @return the current value associated with this property.
     */
    public long get() {
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
        try {
            Long l = Long.valueOf(string);
            currentValue.set(l);
        }
        catch(NumberFormatException e) {
            LOGGER.error("refusing to set {} to {}: unrecognized value", propertyName, string, e);
        }
    }

}

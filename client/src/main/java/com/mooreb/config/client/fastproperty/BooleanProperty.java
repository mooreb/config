package com.mooreb.config.client.fastproperty;

import com.mooreb.config.client.StaticContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;

public class BooleanProperty extends FastProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanProperty.class);
    private final boolean defaultValue;
    private final AtomicBoolean currentValue;

    /** an internal map of property names to fast properties
     *
     */
    private static final ConcurrentHashMap<String, BooleanProperty> properties = new ConcurrentHashMap<String, BooleanProperty>();

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
    private BooleanProperty(String propertyName, boolean defaultValue) {
        super(propertyName);
        this.defaultValue = defaultValue;
        this.currentValue = new AtomicBoolean(defaultValue);
    }

    /**
     * This method simplifies use for the caller by relieving them from the burden of
     * needing to know if a property of a given name can be created from whole cloth
     * and the values are tied together in some layer underneath, or if it must be reused;
     * in the latter case the property must be threaded through from construction to use,
     * which could be really painful for the caller.
     *
     * import com.mooreb.config.client.fastproperty.BooleanProperty;
     * ...
     * BooleanProperty myFeatureToggle = BooleanProperty.findOrCreate("my.booleanproperty.name", false);
     * ...
     * if(myFeatureToggle.get()) {
     *     ... // new code
     * }
     * else {
     *     ... // BAU
     * }
     *
     * @param propertyName the name of the property, used to associate a value with a name
     * @param defaultValue this is the value returned by get() before the service can be reached,
     *                     or the value returned when the service does not override the value for propertyName.
     * @return a property on which get() may be called.
     */
    public static BooleanProperty findOrCreate(String propertyName, boolean defaultValue) {
        BooleanProperty retval;
        if(properties.containsKey(propertyName)) {
            retval = properties.get(propertyName);
        }
        else {
            BooleanProperty newProperty = new BooleanProperty(propertyName, defaultValue);
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
     * import com.mooreb.config.client.fastproperty.BooleanProperty;
     * ...
     * BooleanProperty myFeatureToggle = BooleanProperty.findOrCreate("my.booleanproperty.name", false);
     * ...
     * if(myFeatureToggle.get()) {
     *     ... // new code
     * }
     * else {
     *     ... // BAU
     * }
     *
     * @return the current value associated with this property.
     */
    public boolean get() {
        return currentValue.get();
    }

    @Override
    public void resetToDefault() {
        currentValue.set(defaultValue);
    }

    // package scoope
    @Override
    void assignFromString(final String string) {
        if(null == string) {
            LOGGER.error("cannot assign from a null string: propertyName: {}", propertyName);
            return;
        }
        final String lower = string.toLowerCase();
        if("true".equals(lower) || "yes".equals(lower) || "on".equals(lower)) {
            currentValue.set(true);
        }
        else if("false".equals(lower) || "no".equals(lower) || "off".equals(lower)) {
            currentValue.set(false);
        }
        else {
            LOGGER.error("refusing to set {} to {}: unrecognized value", propertyName, string);
        }
    }
}

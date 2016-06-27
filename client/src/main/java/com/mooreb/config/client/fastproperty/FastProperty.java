package com.mooreb.config.client.fastproperty;

/** This class exists  to tie the other typed fast properties together
 *  for the purpose of registration with the fetcher.
 *
 *  I would have liked to have made this an interface but
 *  java doesn't support polymorphism on return types,
 *  and each property getter returns a different type
 */
public abstract class FastProperty {
    protected String uuid;
    protected final String propertyName;

    protected FastProperty(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public abstract void resetToDefault();

    // package scope
    abstract void assignFromString(final String value);

    public String getUUID() { return uuid; }

    // package scope
    void setUUID(final String uuid) {
        this.uuid = uuid;
    }
}

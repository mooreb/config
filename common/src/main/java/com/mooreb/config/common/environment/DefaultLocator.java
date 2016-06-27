package com.mooreb.config.common.environment;

public enum DefaultLocator {
    INSTANCE;

    private Locator instance;
    private DefaultLocator() {
        if("localhost-8080".equals(System.getProperty("com.mooreb.config.locator"))) {
            instance = new Localhost8080Locator();
        }
        else {
	    // BUG: Make a better default locator
            instance = new Localhost8080Locator();
        }
    }

    public Locator getDefaultLocator() {
        return instance;
    }
}

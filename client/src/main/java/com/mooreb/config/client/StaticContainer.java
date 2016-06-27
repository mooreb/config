package com.mooreb.config.client;

import com.mooreb.config.client.fastproperty.Fetcher;
import com.mooreb.config.common.environment.DefaultLocator;
import com.mooreb.config.common.environment.Locator;

public class StaticContainer {
    private static final Locator locator = DefaultLocator.INSTANCE.getDefaultLocator();
    private static final Fetcher fetcher = new Fetcher(locator);

    public static Fetcher getFetcherInstance() {
        return fetcher;
    }

}

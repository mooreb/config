package com.mooreb.config.client.fastproperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.mooreb.config.common.ConfigUtils;
import com.mooreb.config.common.EncodingUtils;
import com.mooreb.config.common.Property;
import com.mooreb.config.common.PropertyUtils;
import com.mooreb.config.common.environment.Locator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fetcher.class);
    private static ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);
    private final Locator locator;

    /**
     * an internal list of maps; each map maps property names to a FastProperty
     */
    private final ConcurrentLinkedQueue<ConcurrentHashMap<String, ? extends FastProperty>> propertyTypes =
            new ConcurrentLinkedQueue<ConcurrentHashMap<String, ? extends FastProperty>>();

    /**
     * an internal map of uuid to properties fetched from the service
     */
    private final AtomicReference<List<Property>> lastFetch = new AtomicReference<List<Property>>();

    /**
     * an internal map of property name to properties fetched from the service
     */
    private final AtomicReference<Map<String, List<Property>>> lastReverseIndex = new AtomicReference<Map<String, List<Property>>>();

    /**
     * System property names to control how long the client will wait for a first fetch from the service
     */
    private static final String FIRST_FETCH_BLOCK_TIME_MILLIS = "com.mooreb.config.firstFetchBlockTimeMillis";
    private static final String FIRST_FETCH_COUNT = "com.mooreb.config.firstFetchBlockCount";
    /**
     * Default values for these properties
     */
    private static final long DEFAULT_FIRST_FETCH_BLOCK_TIME_MILLIS = 2000;
    private static final long DEFAULT_FIRST_FETCH_COUNT = 5;

    // package scope
    static long fetcherIntervalValue = 15;
    static TimeUnit fetcherIntervalUnits = TimeUnit.SECONDS;
    final AtomicLong numAttemptedFetchesSinceBoot = new AtomicLong(0);
    final AtomicLong numSuccessfulFetchesSinceBoot = new AtomicLong(0);
    static final long timeToBlockForFirstFetchValueMillis = ConfigUtils.safeGetPositiveLongSystemProperty(
      FIRST_FETCH_BLOCK_TIME_MILLIS, DEFAULT_FIRST_FETCH_BLOCK_TIME_MILLIS);;
    static final long numTimesToBlockForFirstFetch = ConfigUtils.safeGetPositiveLongSystemProperty(
      FIRST_FETCH_COUNT, DEFAULT_FIRST_FETCH_COUNT);

    public Fetcher(final Locator locator) {
        this.locator = locator;
        init();
    }

    private void init() {
        lastFetch.set(Collections.unmodifiableList(new ArrayList<Property>()));
        lastReverseIndex.set(Collections.unmodifiableMap(new HashMap<String, List<Property>>()));
        ScheduledFuture ignoredScheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(new FetchingRunnable(locator),
                        0L, fetcherIntervalValue, fetcherIntervalUnits);
    }

    public void register(ConcurrentHashMap<String, ? extends FastProperty> properties) {
        propertyTypes.add(properties);
    }

    private class FetchingRunnable implements Runnable {
        private final Locator locator;
        public FetchingRunnable(Locator locator) {
            this.locator = locator;
        }

        public void run() {
            try {
                long ignore1 = numAttemptedFetchesSinceBoot.incrementAndGet();
                fetchAll(locator);
                long ignored2 = numSuccessfulFetchesSinceBoot.incrementAndGet();
            }
            catch(Exception e) {
                LOGGER.warn("experienced an error during property fetch; will try again in {} {}",
                        fetcherIntervalValue,
                        fetcherIntervalUnits.toString().toLowerCase(),
                        e);
                // intentionally swallow the exception so as not to stop the scheduledExecutorService
            }
        }
        private void fetchAll(Locator locator) throws IOException {
            List<Property> propertiesFromService = getPropertiesFromService(locator);
            Map<String, List<Property>> reverseIndex = PropertyUtils.buildReverseIndex(propertiesFromService);
            lastFetch.set(propertiesFromService);
            lastReverseIndex.set(reverseIndex);
            iterateOverAllKnownClientProperties();
        }
    }

    private void iterateOverAllKnownClientProperties() {
        for(final ConcurrentHashMap<String, ? extends FastProperty> properties : propertyTypes) {
            for(final FastProperty fastProperty : properties.values()) {
                assign(fastProperty, false);
            }
        }
    }

    public void assign(FastProperty fastProperty) {
        assign(fastProperty, true);
    }

    private void assign(final FastProperty fastProperty, boolean blockForFirstFetch) {
        if(blockForFirstFetch) waitForFirstFetch();
        final Property bestProperty = findBestMatch(fastProperty);
        if (null != bestProperty) {
            fastProperty.assignFromString(bestProperty.getValue());
            fastProperty.setUUID(bestProperty.getUuid());
        } else {
            fastProperty.resetToDefault();
            fastProperty.setUUID(null);
        }
    }

    private volatile boolean alreadyTriedToWait = false;
    private void waitForFirstFetch() {
        if(alreadyTriedToWait) return;
        if(0 != numSuccessfulFetchesSinceBoot.get()) return;
        for(int i = 0; i < numTimesToBlockForFirstFetch; i++) {
            if (0 == numSuccessfulFetchesSinceBoot.get()) {
                LOGGER.info("waiting for the first fetch");
                ConfigUtils.safeSleep(timeToBlockForFirstFetchValueMillis / numTimesToBlockForFirstFetch);
            } else {
                LOGGER.info("got first successful property fetch");
                alreadyTriedToWait = true;
                return;
            }
        }
        LOGGER.warn("timed out waiting for the first property fetch");
        alreadyTriedToWait = true;
    }

    private Property findBestMatch(final FastProperty fastProperty) {
        Property retval = null;
        final List<Property> potentialMatches = getPotentialMatches(fastProperty);
        int maxScore = 0;
        final Map<String, String> clientSideContext = locator.getConfigClientContextAsKeyValuePairs();
        for (final Property property : potentialMatches) {
            Map<String, String> serverSideContext = property.parseContext();
            int thisScore = contextMatchingScore(serverSideContext, clientSideContext);
            if (thisScore > maxScore) {
                maxScore = thisScore;
                retval = property;
            }
        }
        return retval;
    }

    private List<Property> getPotentialMatches(final FastProperty fastProperty) {
        final String propertyName = fastProperty.getPropertyName();
        final Map<String, List<Property>> reverseIndex = lastReverseIndex.get();
        List<Property> retval = reverseIndex.get(propertyName);
        if(null == retval) {
            retval= Collections.emptyList();
        }
        return retval;
    }

    private static int contextMatchingScore(Map<String, String> serverSideContext, Map<String, String> clientSideContext) {
        if(containsSameKeyButDifferentValues(serverSideContext, clientSideContext, "env")) {
            return 0;
        }
        if(containsSameKeyButDifferentValues(serverSideContext, clientSideContext, "app")) {
            return 0;
        }
        if(containsSameKeyButDifferentValues(serverSideContext, clientSideContext, "host")) {
            return 0;
        }
        if(containsSameKeyAndValue(serverSideContext, clientSideContext, "env") &&
                containsSameKeyAndValue(serverSideContext, clientSideContext, "app") &&
                containsSameKeyAndValue(serverSideContext, clientSideContext, "host")) {
            return 7;
        }
        if(containsSameKeyAndValue(serverSideContext, clientSideContext, "host")) {
            return 6;
        }
        if(containsSameKeyAndValue(serverSideContext, clientSideContext, "env") &&
                containsSameKeyAndValue(serverSideContext, clientSideContext, "app")) {
            return 5;
        }
        if(containsSameKeyAndValue(serverSideContext, clientSideContext, "app")) {
            return 4;
        }
        if(containsSameKeyAndValue(serverSideContext, clientSideContext, "env")) {
            return 3;
        }
        if(isEmpty(serverSideContext)) {
            return 2;
        }
        return 0;
    }

    private static boolean isEmpty(Map<String, String> map) {
        return ((null == map) || (0 == map.size()));
    }

    private static boolean containsSameKeyAndValue(Map<String, String> m1, Map<String, String> m2, String key) {
        return ((null != m1) && (null != m2) && m1.containsKey(key) && m2.containsKey(key) && m1.get(key).equals(m2.get(key)));
    }

    private static boolean containsSameKeyButDifferentValues(Map<String, String> m1, Map<String, String> m2, String key) {
        return ((null != m1) && (null != m2) && m1.containsKey(key) && m2.containsKey(key) && (!(m1.get(key).equals(m2.get(key)))));
    }

    private static List<Property> getPropertiesFromService(final Locator locator) throws IOException {
        final String scheme = locator.getConfigServiceVipScheme();
        if(null == scheme) {
            LOGGER.error("found unexpected null vip for the config service. returning an empty list of properties.");
            return Collections.emptyList();
        }
        final String vip = locator.getConfigServiceVip();
        if(null == vip) {
            LOGGER.error("found unexpected null vip for the config service. returning an empty list of properties.");
            return Collections.emptyList();
        }
        final int vipPort = locator.getConfigServiceVipPort();
        final String vipWithPort = vip + ":" + Integer.toString(vipPort);
        final String queryParamString = getQueryParamString(locator);
        final List<Property> retval = get(scheme, vipWithPort, "/config/api/v1/properties/list", queryParamString);
        LOGGER.debug("successfully retreived {} properties from {}", retval.size(), vip);
        return retval;
    }

    /**
     * execute an HTTP GET on http://host:port/path
     *
     */
    private static List<Property> get(String scheme, String hostWithPort, String path, String queryParamString) throws IOException {
        final URL url = new URL(scheme + "://" + hostWithPort + path + queryParamString);
        return get(url);
    }

    private static List<Property> get(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Property> properties = objectMapper.readValue(bufferedReader, new TypeReference<List<Property>>(){});
        return Collections.unmodifiableList(properties);
    }

    private static String getQueryParamString(Locator locator) {
        String retval = "";
        Map<String, String> myContext = locator.getConfigClientContextAsKeyValuePairs();
        if (null != myContext) {
            byte[] queryParams = new byte[0];
            try {
                queryParams = EncodingUtils.myEncode(myContext);
            }
            catch(IOException e) {
                LOGGER.error("can't encode my context; swallowing exception", e);
            }
            if (queryParams.length > 0) {
                retval = "?" + new String(queryParams); // Potential BUG: is this right???
            }
        }
        return retval;
    }
}

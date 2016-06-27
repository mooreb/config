package com.mooreb.exampleconfigclient.service;

import com.mooreb.config.client.fastproperty.BooleanProperty;
import com.mooreb.config.client.fastproperty.DoubleProperty;
import com.mooreb.config.client.fastproperty.IntProperty;
import com.mooreb.config.client.fastproperty.LongProperty;
import com.mooreb.config.client.fastproperty.StringListProperty;
import com.mooreb.config.client.fastproperty.StringProperty;
import com.mooreb.config.client.fastproperty.StringSetProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    static {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    main(new String[0]);
                }
                catch(Exception e) {
                    LOGGER.error("caught exception; exiting.", e);
                }
            }
        });
        t.start();
    }

    public static void main(String[] argv) throws Exception {
        final boolean booleanDefault = true;
        final double doubleDefault = 12.345;
        final int intDefault = 42;
        final long longDefault = (1L << 36);
        final String stringDefault = "string";
        BooleanProperty booleanProperty = BooleanProperty.findOrCreate("exampleconfigclient.BooleanProperty", booleanDefault);
        DoubleProperty doubleProperty = DoubleProperty.findOrCreate("exampleconfigclient.DoubleProperty", doubleDefault);
        IntProperty intProperty = IntProperty.findOrCreate("exampleconfigclient.IntProperty", intDefault);
        LongProperty longProperty = LongProperty.findOrCreate("exampleconfigclient.LongProperty", longDefault);
        StringProperty stringProperty = StringProperty.findOrCreate("exampleconfigclient.StringProperty", stringDefault);
        List<String> stringListDefault = new ArrayList<String>();
        stringListDefault.add("duck");
        stringListDefault.add("duck");
        stringListDefault.add("goose");
        StringListProperty stringListProperty = StringListProperty.findOrCreate("exampleconfigclient.StringListProperty", stringListDefault);
        Set<String> stringSetDefault = new HashSet<String>();
        stringSetDefault.add("red");
        stringSetDefault.add("white");
        stringSetDefault.add("blue");
        StringSetProperty stringSetProperty = StringSetProperty.findOrCreate("exampleconfigclient.StringSetProperty", stringSetDefault);

        while(true) {
            LOGGER.info("value of exampleconfigclient.BooleanProperty (uuid: {}) (default {}): {} ", booleanProperty.getUUID(), booleanDefault, booleanProperty.get());
            LOGGER.info("value of exampleconfigclient.DoubleProperty (uuid: {}) (default {}): {} ", doubleProperty.getUUID(), doubleDefault, doubleProperty.get());
            LOGGER.info("value of exampleconfigclient.IntProperty (uuid: {}) (default: {}): {} ", intProperty.getUUID(), intDefault, intProperty.get());
            LOGGER.info("value of exampleconfigclient.LongProperty (uuid: {}) (default: {}): {} ", longProperty.getUUID(), longDefault, longProperty.get());
            LOGGER.info("value of exampleconfigclient.StringProperty (uuid: {}) (default: {}): {} ", stringProperty.getUUID(), stringDefault, stringProperty.get());
            LOGGER.info("value of exampleconfigclient.StringListProperty (uuid: {}) (default: {}): {} ", stringListProperty.getUUID(), stringListDefault, stringListProperty.get());
            LOGGER.info("value of exampleconfigclient.StringSetProperty (uuid: {}) (default: {}): {} ", stringSetProperty.getUUID(), stringSetDefault, stringSetProperty.get());
            Thread.sleep(10000);
        }

    }

}

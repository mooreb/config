package com.mooreb.config.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyUtils {

    /** Build a reverse index of properties by property name to simplify lookup
     *
     * @param properties a list of Properties
     * @return a map of property name -> list of property objects
     */
    public static Map<String, List<Property>> buildReverseIndex(List<Property> properties) {
        Map<String, List<Property>> retval = new HashMap<String, List<Property>>();
        for(final Property property : properties) {
            final String propertyName = property.getPropertyName();
            List<Property> list;
            if(retval.containsKey(propertyName)) {
               list  = retval.get(propertyName);
            }
            else {
                list = new ArrayList<Property>();
                retval.put(propertyName, list);
            }
            list.add(property);
        }
        return Collections.unmodifiableMap(retval);
    }
}

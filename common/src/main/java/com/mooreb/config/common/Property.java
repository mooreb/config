package com.mooreb.config.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Property {
    private static final Logger LOGGER = LoggerFactory.getLogger(Property.class);
    final String uuid;
    final String propertyName;
    final String context;
    final String value;
    final String author;
    final String comments;
    final Date lastModified;
    final URI uri;

    // this method is necessary for jackson to deserialize json to a Property
    public Property() {
        this.uuid = null;
        this.propertyName = null;
        this.context = null;
        this.value = null;
        this.author = null;
        this.comments = null;
        this.lastModified = null;
        this.uri = null;
    }

    public Property(String propertyName, String context, String value, String author, String comments) {
        this(UUID.randomUUID().toString(), propertyName, context, value, author, comments);
    }


    /**
     * Please don't use these constructors unless you really know what you're doing.
     *
     * The edit case needs to preserve the same uuid.
     *
     * I had some terrible choices:
     *   (1) introduce a setter for the uuid field
     *   (2) introduce a copy constructor (imo just as bad as providing this constructor)
     *   (3) introduce this constructor
     *
     * I decided on (3) as the lesser of evils; if you have a better idea please let me
     * know or make the change.
     *
     * @param uuid
     * @param propertyName
     * @param context
     * @param value
     * @param author
     * @param comments
     */

    public Property(String uuid, String propertyName, String context, String value, String author, String comments) {
        this(uuid, propertyName, context, value, author, comments, System.currentTimeMillis());
    }

    public Property(String uuid, String propertyName, String context, String value, String author, String comments, long lastModified) {
        this.uuid = uuid;
        this.propertyName = propertyName;
        this.context = context;
        this.value = value;
        this.author = author;
        this.comments = comments;
        this.lastModified = new Date(lastModified);
        this.uri = assignURIFromUUID();
    }

    public String getUuid() {
        return uuid;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getContext() {
        return context;
    }

    public String getValue() {
        return value;
    }

    public String getAuthor() {
        return author;
    }

    public String getComments() {
        return comments;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public URI getUri() {
        return uri;
    }

    private URI assignURIFromUUID() {
        URI retval = null;
        StringBuilder sb = new StringBuilder();
        sb.append("/config").append("/api").append("/v1").append("/properties").append("/property-uuid").append("/");
        sb.append(uuid);
        try {
            retval = new URI(sb.toString());
        }
        catch(URISyntaxException e) {
            LOGGER.error("could not construct URI for property {}", this, e);
        }
        return retval;
    }

    /**
     * Parse a string of the form:
     *   host=lvdma1006|env=FastFeedback|app=config
     * into a map containing three keys:
     *   host, env, app
     * that map (sorry) into three values respectively:
     *   lvdma1006, FastFeedback, config
     *
     * @return a map of the serialized form described above
     */
    public Map<String, String> parseContext() {
        Map<String, String> retval = new HashMap<String, String>();
        if((null != context) && (!"".equals(context))) {
            String[] tokens = context.split("[|]");
            for(final String token : tokens) {
                final String[] keyValue = token.split("=", 2);
                if(2 != keyValue.length) {
                    LOGGER.error("cannot parse at least part of {}. bad token: {}", context, token);
                }
                else {
                    retval.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return Collections.unmodifiableMap(retval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (author != null ? !author.equals(property.author) : property.author != null) return false;
        if (comments != null ? !comments.equals(property.comments) : property.comments != null) return false;
        if (context != null ? !context.equals(property.context) : property.context != null) return false;
        if (lastModified != null ? !lastModified.equals(property.lastModified) : property.lastModified != null)
            return false;
        if (propertyName != null ? !propertyName.equals(property.propertyName) : property.propertyName != null)
            return false;
        if (uuid != null ? !uuid.equals(property.uuid) : property.uuid != null) return false;
        if (value != null ? !value.equals(property.value) : property.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
                "uuid='" + uuid + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", context='" + context + '\'' +
                ", value='" + value + '\'' +
                ", author='" + author + '\'' +
                ", comments='" + comments + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}

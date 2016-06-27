package com.mooreb.config.service;

import com.mooreb.config.common.AuditLogEntry;
import com.mooreb.config.common.Property;
import com.mooreb.config.common.environment.DefaultLocator;

import java.util.List;

public enum DefaultPersistence {
    INSTANCE;

    private Persistence persistence;

    private DefaultPersistence() {
        persistence = new Persistence(DefaultLocator.INSTANCE.getDefaultLocator());
    }

    public List<Property> getAllProperties() {
        return persistence.getAllProperties();
    }

    public List<AuditLogEntry> getAuditLog() {
        return persistence.getAuditLog();
    }

    public boolean createNewProperty(final Property property) {
        return persistence.createNewProperty(property);
    }

    public boolean editProperty(final Property oldProperty, final Property newProperty) {
        return persistence.editProperty(oldProperty, newProperty);
    }

    public boolean deleteProperty(final String author, final String comment, final Property oldProperty) {
        return persistence.deleteProperty(author, comment, oldProperty);
    }

    public Property getProperty(final String uuid) {
        return persistence.getProperty(uuid);
    }

    public List<Property> searchForProperty(final String pattern) {
        return persistence.searchForProperty(pattern);
    }
}

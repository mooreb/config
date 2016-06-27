package com.mooreb.config.common;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogEntry.class);

    private final Date modificationTimestamp;
    private final String action;
    private final String author;
    private final Property oldProperty;
    private final Property newProperty;
    private final String comments;

    public AuditLogEntry(String action, String author, Property oldProperty, Property newProperty, String comments) {
        this(action, author, oldProperty, newProperty, comments, System.currentTimeMillis());
    }

    public AuditLogEntry(String action, String author, Property oldProperty, Property newProperty, String comments, long epoch) {
        this.modificationTimestamp = new Date(epoch);
        this.action = action;
        this.author = author;
        this.oldProperty = oldProperty;
        this.newProperty = newProperty;
        this.comments = comments;
    }

    // this method is necessary for jackson to deserialize json to an AuditLogEntry
    public AuditLogEntry() {
        this.modificationTimestamp = null;
        this.action = null;
        this.author = null;
        this.oldProperty = null;
        this.newProperty = null;
        this.comments = null;
    }

    public Date getModificationTimestamp() {
        return modificationTimestamp;
    }

    public String getAction() {
        return action;
    }

    public String getAuthor() {
        return author;
    }

    public Property getOldProperty() {
        return oldProperty;
    }

    public Property getNewProperty() {
        return newProperty;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogEntry that = (AuditLogEntry) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (modificationTimestamp != null ? !modificationTimestamp.equals(that.modificationTimestamp) : that.modificationTimestamp != null)
            return false;
        if (newProperty != null ? !newProperty.equals(that.newProperty) : that.newProperty != null) return false;
        if (oldProperty != null ? !oldProperty.equals(that.oldProperty) : that.oldProperty != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modificationTimestamp != null ? modificationTimestamp.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (oldProperty != null ? oldProperty.hashCode() : 0);
        result = 31 * result + (newProperty != null ? newProperty.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuditLogEntry{" +
                "modificationTimestamp=" + modificationTimestamp +
                ", action='" + action + '\'' +
                ", author='" + author + '\'' +
                ", oldProperty=" + oldProperty +
                ", newProperty=" + newProperty +
                ", comments='" + comments + '\'' +
                '}';
    }
}

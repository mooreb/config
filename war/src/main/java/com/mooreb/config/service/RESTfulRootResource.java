package com.mooreb.config.service;

import com.mooreb.config.common.AuditLogEntry;
import com.mooreb.config.common.Property;
import com.mooreb.config.common.environment.DefaultLocator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v1")
@Produces("application/json")
public class RESTfulRootResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTfulRootResource.class);

    @GET
    @Path("/properties/list")
    public Response propertiesList() {
        try {
            final List<Property> properties = DefaultPersistence.INSTANCE.getAllProperties();
            final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            final String output = mapper.writeValueAsString(properties);
            return Response.ok().entity(output).build();
        }
        catch(JsonProcessingException e) {
            LOGGER.error("failed to return properties", e);
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/properties/create")
    @Consumes("application/x-www-form-urlencoded")
    public Response propertiesCreate(
            @FormParam("propertyName") String propertyName,
            @FormParam("context") String context,
            @FormParam("value") String value,
            @FormParam("author") String author,
            @FormParam("comments") String comments
    ) {
        final Property property = new Property(propertyName, context, value, author, comments);
        final boolean success = DefaultPersistence.INSTANCE.createNewProperty(property);
        final URI uri = property.getUri();
        if(success && (null != uri)) {
            return Response.created(uri).build();
        }
        else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/properties/property-uuid/{uuid}")
    public Response propertiesGetPropertyByUUID(@PathParam("uuid") String uuid) {
        final Property property = DefaultPersistence.INSTANCE.getProperty(uuid);
        if(null == property) return Response.status(Response.Status.NOT_FOUND).build();
        try {
            final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            final String output = mapper.writeValueAsString(property);
            return Response.ok().entity(output).build();
        }
        catch(JsonProcessingException e) {
            LOGGER.error("failed to return property {}", property, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/properties/edit")
    @Consumes("application/x-www-form-urlencoded")
    public Response propertiesEdit(
            @FormParam("uuid") String uuid,
            @FormParam("newPropertyName") String newPropertyName,
            @FormParam("newContext") String newContext,
            @FormParam("newValue") String newValue,
            @FormParam("newAuthor") String newAuthor,
            @FormParam("newComments") String newComments
    ) {
        final Property oldProperty = DefaultPersistence.INSTANCE.getProperty(uuid);
        if(null == oldProperty) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else {
            final Property newProperty = new Property(uuid, newPropertyName, newContext, newValue, newAuthor, newComments);
            boolean success = DefaultPersistence.INSTANCE.editProperty(oldProperty, newProperty);
            if(success) {
                return Response.created(newProperty.getUri()).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Path("/properties/delete")
    @Consumes("application/x-www-form-urlencoded")
    public Response propertiesDelete(
            @FormParam("uuid") String uuid,
            @FormParam("author") String author,
            @FormParam("comments") String comments
    ) {
        final Property oldProperty = DefaultPersistence.INSTANCE.getProperty(uuid);
        if(null == oldProperty) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        else {
            final boolean success = DefaultPersistence.INSTANCE.deleteProperty(author, comments, oldProperty);
            if(success) {
                return Response.noContent().build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Path("/properties/search")
    @Consumes("application/x-www-form-urlencoded")
    public Response propertiesDelete(
            @FormParam("pattern") String pattern
     ) {
        final List<Property> propertyList = DefaultPersistence.INSTANCE.searchForProperty(pattern);
        try {
            final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            final String output = mapper.writeValueAsString(propertyList);
            return Response.ok().entity(output).build();
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to return propertyList {}", propertyList, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GET
    @Path("/auditlog/list")
    public Response auditLogList() {
        try {
            final List<AuditLogEntry> auditLogEntries = DefaultPersistence.INSTANCE.getAuditLog();
            final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            final String output = mapper.writeValueAsString(auditLogEntries);
            return Response.status(200).entity(output).build();
        }
        catch(JsonProcessingException e) {
            LOGGER.error("failed to return properties", e);
        }
        return Response.noContent().build();
    }
}

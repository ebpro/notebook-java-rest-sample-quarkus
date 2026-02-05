package org.acme.api.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JacksonMapper implements ExceptionMapper<com.fasterxml.jackson.core.JsonProcessingException> {
    @Override
    public Response toResponse(com.fasterxml.jackson.core.JsonProcessingException ex) {
        return Response.status(400).entity("Invalid JSON structure").build();
    }
}

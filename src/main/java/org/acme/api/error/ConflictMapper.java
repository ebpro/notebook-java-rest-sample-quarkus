package org.acme.api.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictMapper implements ExceptionMapper<IllegalStateException> {
    @Override
    public Response toResponse(IllegalStateException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(ex.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}

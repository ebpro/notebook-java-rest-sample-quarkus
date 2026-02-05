package org.acme.api.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        // Transforme l'erreur de validation du record Product en HTTP 400
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(exception.getMessage())
                       .build();
    }
}

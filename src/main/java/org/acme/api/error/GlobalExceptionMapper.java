package org.acme.api.error;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.USER + 100) // Très basse priorité
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {

        // 1. Si c'est une exception JAX-RS (ex: 400 Bad Request de Jackson)
        // on la laisse passer telle quelle pour respecter le code HTTP original.
        if (exception instanceof WebApplicationException webEx) {
            Response originalResponse = webEx.getResponse();

            // Optionnel : On s'assure que le corps ne contient pas de détails techniques
            // Si le statut est >= 500, on peut décider de le masquer ici aussi.
            if (originalResponse.getStatus() < 500) {
                return originalResponse;
            }
        }

        // 2. Pour tout le reste (NullPointerException, erreurs DB, etc.)
        // C'est ici que le "balai" agit pour éviter les fuites (NFR-SEC-03)
        LOG.error("Unhandled error caught by Global Mapper:", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("An unexpected error occurred. Please contact support.")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}

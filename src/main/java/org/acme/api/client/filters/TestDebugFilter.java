package org.acme.api.client.filters;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestDebugFilter implements ClientResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TestDebugFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();

        // On ne traite que les erreurs >= 400 ET si un flux de données existe
        if (status >= 400 && responseContext.hasEntity()) {
            InputStream stream = responseContext.getEntityStream();
            if (stream != null) {
                byte[] entityBytes = stream.readAllBytes();
                String body = new String(entityBytes, StandardCharsets.UTF_8);

                LOG.error("--- API FAILURE DEBUG ---");
                LOG.error("Method: {}", requestContext.getMethod());
                LOG.error("URI:    {}", requestContext.getUri());
                LOG.error("Status: {}", status);
                LOG.error("Body:   {}", body.isEmpty() ? "<empty>" : body);
                LOG.error("-------------------------");

                // Ré-injection impérative pour que Cucumber puisse lire le body après
                responseContext.setEntityStream(new ByteArrayInputStream(entityBytes));
            }
        }
    }
}

package io.floci.gcp.services.gke;

import com.fasterxml.jackson.databind.JsonNode;
import io.floci.gcp.core.common.GcpException;
import io.floci.gcp.core.common.kubernetes.ops.KubernetesEventProcessor;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/_floci-gcp/kubernetes")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KubernetesController {

    private final KubernetesEventProcessor processor;

    @Inject
    public KubernetesController(
            KubernetesEventProcessor processor
    ) {
        this.processor = processor;
    }

    @POST
    @Path("/clusters")
    public Response createCluster(
            Map<String, Object> body
    ) {
        String clusterName =
                requiredString(body, "clusterName");

        JsonNode response = processor.process(
                new KubernetesEvent(
                        KubernetesOperation.CREATE_CLUSTER,
                        Map.of("clusterName", clusterName)
                )
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("/clusters/{clusterName}/nodes")
    public Response getNodes(
            @PathParam("clusterName") String clusterName
    ) {
        JsonNode response = processor.process(
                new KubernetesEvent(
                        KubernetesOperation.GET_NODES,
                        Map.of("clusterName", clusterName)
                )
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("/clusters/{clusterName}/namespaces")
    public Response listNamespaces(
            @PathParam("clusterName") String clusterName
    ) {
        JsonNode response = processor.process(
                new KubernetesEvent(
                        KubernetesOperation.LIST_NAMESPACES,
                        Map.of("clusterName", clusterName)
                )
        );

        return Response.ok(response).build();
    }

    private String requiredString(
            Map<String, Object> body,
            String key
    ) {
        Object value = body.get(key);

        if (value instanceof String stringValue
                && !stringValue.isBlank()) {
            return stringValue;
        }

        throw GcpException.invalidArgument(
                "Missing required field: " + key
        );
    }
}

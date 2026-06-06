package io.floci.gcp.core.common.kubernetes.ops.handler.cluster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.floci.gcp.core.common.kubernetes.cluster.K3dDriver;
import io.floci.gcp.core.common.kubernetes.ops.handler.OperationHandler;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateClusterHandler implements OperationHandler {

    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    private final K3dDriver driver;

    @Inject
    public CreateClusterHandler(
            K3dDriver driver
    ) {
        this.driver = driver;
    }

    @Override
    public JsonNode execute(KubernetesEvent event) {
        String clusterName =
                stringPayload(event, "clusterName");

        boolean created =
                driver.createCluster(clusterName);

        ObjectNode response =
                MAPPER.createObjectNode();

        response.put("clusterName", clusterName);
        response.put("created", created);

        return response;
    }

    private String stringPayload(
            KubernetesEvent event,
            String key
    ) {
        Object value = event.payload().get(key);

        if (value instanceof String stringValue
                && !stringValue.isBlank()) {
            return stringValue;
        }

        throw new IllegalArgumentException(
                "Missing required payload field: " + key
        );
    }
}

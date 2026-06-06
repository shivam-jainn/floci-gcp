package io.floci.gcp.core.common.kubernetes.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;
import io.floci.gcp.core.common.kubernetes.ops.registry.OperationDefinition;
import io.floci.gcp.core.common.kubernetes.ops.registry.KubernetesOperationRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KubernetesEventProcessor {

    private final KubernetesOperationRegistry registry;

    @Inject
    public KubernetesEventProcessor(
            KubernetesOperationRegistry registry
    ) {
        this.registry = registry;
    }

    public JsonNode process(
            KubernetesEvent event
    ) {
        OperationDefinition definition =
                registry.get(event.operation());

        if (definition == null) {
            throw new IllegalArgumentException(
                    "Unknown operation: " + event.operation()
            );
        }

        if (definition.handler() != null) {
            return definition.handler().execute(event);
        }

        throw new IllegalArgumentException(
                "No handler registered for operation: " + event.operation()
        );
    }
}

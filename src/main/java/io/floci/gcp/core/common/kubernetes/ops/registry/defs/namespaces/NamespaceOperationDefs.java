package io.floci.gcp.core.common.kubernetes.ops.registry.defs.namespaces;

import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.floci.gcp.core.common.kubernetes.ops.registry.OperationDefinition;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

public final class NamespaceOperationDefs {

    private NamespaceOperationDefs() {
    }

    public static Map<KubernetesOperation, OperationDefinition> create() {
        return Map.of(
                KubernetesOperation.LIST_NAMESPACES,
                new OperationDefinition(
                        HttpMethod.GET,
                        "/api/v1/namespaces",
                        null
                )
        );
    }
}

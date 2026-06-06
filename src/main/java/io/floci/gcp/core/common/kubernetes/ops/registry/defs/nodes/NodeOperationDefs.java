package io.floci.gcp.core.common.kubernetes.ops.registry.defs.nodes;

import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.floci.gcp.core.common.kubernetes.ops.registry.OperationDefinition;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

public final class NodeOperationDefs {

    private NodeOperationDefs() {
    }

    public static Map<KubernetesOperation, OperationDefinition> create() {
        return Map.of(
                KubernetesOperation.GET_NODES,
                new OperationDefinition(
                        HttpMethod.GET,
                        "/api/v1/nodes",
                        null
                )
        );
    }
}

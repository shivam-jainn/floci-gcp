package io.floci.gcp.core.common.kubernetes.ops.registry.defs.cluster;

import io.floci.gcp.core.common.kubernetes.ops.handler.cluster.CreateClusterHandler;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.floci.gcp.core.common.kubernetes.ops.registry.OperationDefinition;

import java.util.Map;

public final class ClusterOperationDefs {

    private ClusterOperationDefs() {
    }

    public static Map<KubernetesOperation, OperationDefinition> create(
            CreateClusterHandler createClusterHandler
    ) {
        return Map.of(
                KubernetesOperation.CREATE_CLUSTER,
                new OperationDefinition(
                        null,
                        null,
                        createClusterHandler
                )
        );
    }
}

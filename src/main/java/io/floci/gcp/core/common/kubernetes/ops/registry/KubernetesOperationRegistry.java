package io.floci.gcp.core.common.kubernetes.ops.registry;

import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;

import java.util.Map;

public class KubernetesOperationRegistry {

    private final Map<KubernetesOperation, OperationDefinition> operations;

    public KubernetesOperationRegistry(
            Map<KubernetesOperation, OperationDefinition> operations
    ) {
        this.operations = operations;
    }

    public OperationDefinition get(
            KubernetesOperation operation
    ) {
        return operations.get(operation);
    }
}

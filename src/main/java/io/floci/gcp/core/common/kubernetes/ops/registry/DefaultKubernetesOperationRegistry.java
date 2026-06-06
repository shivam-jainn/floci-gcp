package io.floci.gcp.core.common.kubernetes.ops.registry;

import io.floci.gcp.core.common.kubernetes.cluster.K3dDriver;
import io.floci.gcp.core.common.kubernetes.ops.handler.cluster.CreateClusterHandler;
import io.floci.gcp.core.common.kubernetes.ops.registry.defs.cluster.ClusterOperationDefs;
import io.floci.gcp.core.common.kubernetes.ops.registry.defs.namespaces.NamespaceOperationDefs;
import io.floci.gcp.core.common.kubernetes.ops.registry.defs.nodes.NodeOperationDefs;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class DefaultKubernetesOperationRegistry extends KubernetesOperationRegistry {

    @Inject
    public DefaultKubernetesOperationRegistry(
            CreateClusterHandler createClusterHandler
    ) {
        super(buildOperations(createClusterHandler));
    }

    public static KubernetesOperationRegistry createForTests() {
        return createForTests(new K3dDriver());
    }

    public static KubernetesOperationRegistry createForTests(
            K3dDriver driver
    ) {
        return new KubernetesOperationRegistry(
                buildOperations(new CreateClusterHandler(driver))
        );
    }

    private static Map<KubernetesOperation, OperationDefinition> buildOperations(
            CreateClusterHandler createClusterHandler
    ) {
        Map<KubernetesOperation, OperationDefinition> operations =
                new LinkedHashMap<>();

        operations.putAll(
                ClusterOperationDefs.create(createClusterHandler)
        );
        operations.putAll(
                NodeOperationDefs.create()
        );
        operations.putAll(
                NamespaceOperationDefs.create()
        );

        return Map.copyOf(operations);
    }
}

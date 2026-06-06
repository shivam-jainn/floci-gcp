package io.floci.gcp.core.common.kubernetes.ops.registry;

import io.floci.gcp.core.common.kubernetes.cluster.K3dDriver;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class DefaultKubernetesOperationRegistryTest {

    @Test
    void shouldRegisterClusterAndApiOperationsByDomain() {
        KubernetesOperationRegistry registry =
                DefaultKubernetesOperationRegistry.createForTests(
                        mock(K3dDriver.class)
                );

        assertNotNull(registry.get(KubernetesOperation.CREATE_CLUSTER));
        assertNotNull(registry.get(KubernetesOperation.GET_NODES));
        assertNotNull(registry.get(KubernetesOperation.LIST_NAMESPACES));

        assertNotNull(
                registry.get(KubernetesOperation.CREATE_CLUSTER).handler()
        );
        assertNull(
                registry.get(KubernetesOperation.GET_NODES).handler()
        );
        assertEquals(
                "/api/v1/nodes",
                registry.get(KubernetesOperation.GET_NODES).path()
        );
    }
}

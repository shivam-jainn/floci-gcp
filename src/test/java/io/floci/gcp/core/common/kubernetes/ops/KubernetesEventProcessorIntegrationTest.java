package io.floci.gcp.core.common.kubernetes.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.floci.gcp.core.common.kubernetes.client.KubernetesClientManager;
import io.floci.gcp.core.common.kubernetes.cluster.K3dDriver;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.floci.gcp.core.common.kubernetes.ops.registry.DefaultKubernetesOperationRegistry;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KubernetesEventProcessorIntegrationTest {

    @Test
    void shouldCreateClusterViaHandlerPath() {
        String clusterName = "event-create-cluster";
        K3dDriver driver = new K3dDriver();
        KubernetesEventProcessor processor = new KubernetesEventProcessor(
                DefaultKubernetesOperationRegistry.createForTests(driver)
        );

        cleanupCluster(driver, clusterName);

        try {
            JsonNode response = processor.process(
                    new KubernetesEvent(
                            KubernetesOperation.CREATE_CLUSTER,
                            Map.of("clusterName", clusterName)
                    )
            );

            assertNotNull(response);
            assertTrue(response.path("created").asBoolean());
            assertTrue(driver.clusterExists(clusterName));

            Path clusterDir = clusterDir(clusterName);

            assertTrue(Files.exists(clusterDir.resolve("metadata.json")));
            assertTrue(Files.exists(clusterDir.resolve("kubeconfig.yaml")));
        } finally {
            cleanupCluster(driver, clusterName);
        }
    }



    private static Path clusterDir(String clusterName) {
        return Path.of(
                System.getProperty("user.home"),
                ".floci",
                "clusters",
                clusterName
        );
    }

    private static void cleanupCluster(
            K3dDriver driver,
            String clusterName
    ) {
        KubernetesClientManager.remove(clusterName);

        try {
            if (driver.clusterExists(clusterName)) {
                driver.deleteCluster(clusterName);
            }
        } catch (Exception ignored) {
        }
    }
}

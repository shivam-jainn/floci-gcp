package io.floci.gcp.core.common.kubernetes.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.floci.gcp.core.common.kubernetes.cluster.K3dDriver;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.floci.gcp.core.common.kubernetes.ops.registry.DefaultKubernetesOperationRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Table-driven tests for Kubernetes operations.
 * Each operation is defined as a test case with expected outcomes.
 */
class OperationProcessorTableDrivenTest {

    private static final K3dDriver DRIVER = new K3dDriver();

    /**
     * Test cases for Kubernetes operations.
     * Each case defines an operation and expectations.
     */
    static Stream<OperationTestCase> operationTestCases() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        
        return Stream.of(
            // Cluster Operations
            new OperationTestCase(
                KubernetesOperation.CREATE_CLUSTER,
                Map.of("clusterName", "test-create-" + uniqueSuffix),
                "should create a cluster",
                result -> {
                    assertTrue(result.has("clusterName"));
                    assertTrue(result.path("created").asBoolean());
                }
            ),
            new OperationTestCase(
                KubernetesOperation.CREATE_CLUSTER,
                Map.of("clusterName", "test-create-2-" + uniqueSuffix),
                "should create multiple clusters",
                result -> {
                    assertTrue(result.has("clusterName"));
                    assertTrue(result.path("created").asBoolean());
                }
            ),
            new OperationTestCase(
                KubernetesOperation.GET_NODES,
                Map.of("clusterName", "test-nodes-" + uniqueSuffix),
                "should throw for unregistered operation without handler",
                result -> {
                    // This operation has no handler, so processor should throw
                    fail("Should have thrown exception for operation without handler");
                },
                true // expectException
            ),
            new OperationTestCase(
                KubernetesOperation.LIST_NAMESPACES,
                Map.of("clusterName", "test-list-ns-" + uniqueSuffix),
                "should throw for operation without handler",
                result -> {
                    fail("Should have thrown exception for operation without handler");
                },
                true // expectException
            )
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("operationTestCases")
    void testOperations(OperationTestCase testCase) {
        KubernetesEventProcessor processor = new KubernetesEventProcessor(
            DefaultKubernetesOperationRegistry.createForTests(DRIVER)
        );

        String clusterName = (String) testCase.payload().get("clusterName");
        cleanupCluster(clusterName);

        try {
            KubernetesEvent event = new KubernetesEvent(
                testCase.operation(),
                testCase.payload()
            );

            if (testCase.expectException()) {
                assertThrows(IllegalArgumentException.class, () -> {
                    processor.process(event);
                });
            } else {
                JsonNode result = processor.process(event);
                assertNotNull(result, "Operation should return a result");
                testCase.assertions().accept(result);
                
                // Verify cluster was actually created for CREATE_CLUSTER operations
                if (testCase.operation() == KubernetesOperation.CREATE_CLUSTER) {
                    assertTrue(DRIVER.clusterExists(clusterName), 
                        "Cluster should exist after creation");
                    
                    Path clusterDir = clusterDir(clusterName);
                    assertTrue(Files.exists(clusterDir.resolve("metadata.json")),
                        "Cluster metadata should exist");
                    assertTrue(Files.exists(clusterDir.resolve("kubeconfig.yaml")),
                        "Kubeconfig should exist");
                }
            }
        } catch (Exception e) {
            if (!testCase.expectException()) {
                throw new AssertionError("Unexpected exception", e);
            }
        } finally {
            cleanupCluster(clusterName);
        }
    }

    /**
     * Test case record for parameterized testing.
     */
    record OperationTestCase(
        KubernetesOperation operation,
        Map<String, Object> payload,
        String description,
        java.util.function.Consumer<JsonNode> assertions,
        boolean expectException
    ) {
        OperationTestCase(
            KubernetesOperation operation,
            Map<String, Object> payload,
            String description,
            java.util.function.Consumer<JsonNode> assertions
        ) {
            this(operation, payload, description, assertions, false);
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

    private static void cleanupCluster(String clusterName) {
        try {
            if (DRIVER.clusterExists(clusterName)) {
                DRIVER.deleteCluster(clusterName);
            }
        } catch (Exception ignored) {
            // Cleanup is best-effort
        }
    }
}

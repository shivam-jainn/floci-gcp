package io.floci.gcp.services.gke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.floci.gcp.core.common.kubernetes.ops.KubernetesEventProcessor;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesOperation;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class KubernetesControllerTest {

    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    @InjectMock
    KubernetesEventProcessor processor;

    @Test
    void createClusterEndpointShouldDispatchCreateClusterEvent() {
        JsonNode response = MAPPER.valueToTree(
                Map.of(
                        "clusterName", "demo-cluster",
                        "created", true
                )
        );

        when(processor.process(argThat(event ->
                event.operation() == KubernetesOperation.CREATE_CLUSTER
                        && "demo-cluster".equals(event.payload().get("clusterName"))
        ))).thenReturn(response);

        given()
                .contentType("application/json")
                .body(Map.of("clusterName", "demo-cluster"))
                .when()
                .post("/_floci-gcp/kubernetes/clusters")
                .then()
                .statusCode(200)
                .body("clusterName", equalTo("demo-cluster"))
                .body("created", equalTo(true));

        verify(processor).process(argThat(event ->
                event.operation() == KubernetesOperation.CREATE_CLUSTER
                        && "demo-cluster".equals(event.payload().get("clusterName"))
        ));
    }

    @Test
    void nodesEndpointShouldDispatchGetNodesEvent() {
        JsonNode response = MAPPER.valueToTree(
                Map.of(
                        "items", List.of(Map.of("metadata", Map.of("name", "node-1")))
                )
        );

        when(processor.process(argThat(event ->
                event.operation() == KubernetesOperation.GET_NODES
                        && "demo-cluster".equals(event.payload().get("clusterName"))
        ))).thenReturn(response);

        given()
                .when()
                .get("/_floci-gcp/kubernetes/clusters/demo-cluster/nodes")
                .then()
                .statusCode(200)
                .body("items.size()", equalTo(1))
                .body("items[0].metadata.name", equalTo("node-1"));
    }

    @Test
    void createClusterEndpointShouldRejectMissingClusterName() {
        given()
                .contentType("application/json")
                .body(Map.of())
                .when()
                .post("/_floci-gcp/kubernetes/clusters")
                .then()
                .statusCode(400)
                .body("error.status", equalTo("INVALID_ARGUMENT"));
    }
}

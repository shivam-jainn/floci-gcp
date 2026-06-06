package io.floci.gcp.core.common.kubernetes.ops.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.floci.gcp.core.common.kubernetes.ops.model.KubernetesEvent;

public interface OperationHandler {

    JsonNode execute(KubernetesEvent event);
}

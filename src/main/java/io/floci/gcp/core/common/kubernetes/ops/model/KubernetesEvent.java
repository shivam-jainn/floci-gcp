package io.floci.gcp.core.common.kubernetes.ops.model;

import java.util.Map;

public record KubernetesEvent(

    KubernetesOperation operation,

    Map<String, Object> payload

) {}

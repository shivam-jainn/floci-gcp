package io.floci.gcp.core.common.kubernetes.ops.registry;

import io.floci.gcp.core.common.kubernetes.ops.handler.OperationHandler;
import io.vertx.core.http.HttpMethod;

public record OperationDefinition(

    HttpMethod method,

    String path,

    OperationHandler handler

) {}

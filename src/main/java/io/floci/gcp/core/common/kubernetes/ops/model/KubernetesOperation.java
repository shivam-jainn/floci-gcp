package io.floci.gcp.core.common.kubernetes.ops.model;

public enum KubernetesOperation {

    CREATE_CLUSTER,
    DELETE_CLUSTER,

    GET_CLUSTER,
    LIST_CLUSTERS,

    GET_NODES,

    CREATE_NAMESPACE,
    LIST_NAMESPACES,

    CREATE_DEPLOYMENT,
    DELETE_DEPLOYMENT
}

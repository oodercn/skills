package net.ooder.skillcenter.service.k8s;

import net.ooder.skillcenter.dto.*;

import java.util.List;

public interface K8sService {
    List<ClusterInfo> listClusters();
    ClusterInfo getClusterInfo(String clusterId);
    List<NodeInfo> listNodes();
    NodeInfo getNodeInfo(String nodeName);
    List<NamespaceInfo> listNamespaces();
    NamespaceInfo createNamespace(String name);
    boolean deleteNamespace(String name);
    List<PodInfo> listPods(String namespace);
    PodInfo getPodInfo(String namespace, String podName);
    String getPodLogs(String namespace, String podName, Integer tailLines);
    boolean isMockMode();
}

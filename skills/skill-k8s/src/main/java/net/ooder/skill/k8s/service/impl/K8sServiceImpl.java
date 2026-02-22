package net.ooder.skill.k8s.service.impl;

import net.ooder.skill.k8s.dto.*;
import net.ooder.skill.k8s.service.K8sService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class K8sServiceImpl implements K8sService {

    private static final Logger log = LoggerFactory.getLogger(K8sServiceImpl.class);

    @Value("${k8s.master-url:}")
    private String masterUrl;

    @Value("${k8s.token:}")
    private String token;

    @Value("${k8s.namespace:default}")
    private String defaultNamespace;

    @Value("${k8s.mock-enabled:true}")
    private boolean mockEnabled;

    @PostConstruct
    public void init() {
        log.info("K8s Service initialized, mock mode: {}", mockEnabled);
    }

    @Override
    public boolean isMockMode() {
        return mockEnabled;
    }

    @Override
    public List<ClusterInfo> listClusters() {
        return getMockClusters();
    }

    @Override
    public ClusterInfo getClusterInfo(String clusterId) {
        return getMockClusterInfo(clusterId);
    }

    @Override
    public List<NodeInfo> listNodes() {
        return getMockNodes();
    }

    @Override
    public NodeInfo getNodeInfo(String nodeName) {
        return getMockNodes().stream().filter(n -> n.getNodeName().equals(nodeName)).findFirst().orElse(null);
    }

    @Override
    public List<NamespaceInfo> listNamespaces() {
        return getMockNamespaces();
    }

    @Override
    public NamespaceInfo createNamespace(String name) {
        NamespaceInfo ns = new NamespaceInfo();
        ns.setName(name);
        ns.setStatus("Active");
        ns.setPhase("Active");
        ns.setCreateTime(System.currentTimeMillis());
        return ns;
    }

    @Override
    public boolean deleteNamespace(String name) {
        return true;
    }

    @Override
    public List<PodInfo> listPods(String namespace) {
        return getMockPods(namespace);
    }

    @Override
    public PodInfo getPodInfo(String namespace, String podName) {
        return getMockPods(namespace).stream().filter(p -> p.getPodName().equals(podName)).findFirst().orElse(null);
    }

    @Override
    public String getPodLogs(String namespace, String podName, Integer tailLines) {
        return "Mock logs for pod " + podName + " in namespace " + namespace;
    }

    private List<ClusterInfo> getMockClusters() {
        List<ClusterInfo> clusters = new ArrayList<>();
        clusters.add(getMockClusterInfo("default"));
        return clusters;
    }

    private ClusterInfo getMockClusterInfo(String clusterId) {
        ClusterInfo cluster = new ClusterInfo();
        cluster.setClusterId(clusterId);
        cluster.setName("mock-cluster");
        cluster.setVersion("v1.28.0");
        cluster.setStatus("healthy");
        cluster.setEndpoint("https://mock.k8s.local:6443");
        cluster.setNodeCount(3);
        cluster.setNamespaceCount(5);
        cluster.setPodCount(25);
        cluster.setCreateTime(System.currentTimeMillis() - 86400000L);
        Map<String, Object> capacity = new HashMap<>();
        capacity.put("cpu", "12");
        capacity.put("memory", "48Gi");
        capacity.put("pods", "300");
        cluster.setCapacity(capacity);
        return cluster;
    }

    private List<NodeInfo> getMockNodes() {
        List<NodeInfo> nodes = new ArrayList<>();
        String[] nodeNames = {"node-1", "node-2", "node-3"};
        for (String name : nodeNames) {
            NodeInfo node = new NodeInfo();
            node.setNodeName(name);
            node.setStatus("Ready");
            node.setKubeletVersion("v1.28.0");
            node.setOsImage("Ubuntu 22.04.3 LTS");
            node.setKernelVersion("5.15.0-91-generic");
            node.setContainerRuntime("containerd://1.7.2");
            node.setCpuUsage(Math.random() * 60 + 10);
            node.setMemoryUsage(Math.random() * 50 + 20);
            Map<String, Object> capacity = new HashMap<>();
            capacity.put("cpu", "4");
            capacity.put("memory", "16Gi");
            capacity.put("pods", "100");
            node.setCapacity(capacity);
            node.setCreateTime(System.currentTimeMillis() - 86400000L);
            nodes.add(node);
        }
        return nodes;
    }

    private List<NamespaceInfo> getMockNamespaces() {
        List<NamespaceInfo> namespaces = new ArrayList<>();
        String[] nsNames = {"default", "kube-system", "kube-public", "development", "production"};
        for (String name : nsNames) {
            NamespaceInfo ns = new NamespaceInfo();
            ns.setName(name);
            ns.setStatus("Active");
            ns.setPhase("Active");
            ns.setPodCount((int) (Math.random() * 10 + 1));
            ns.setCreateTime(System.currentTimeMillis() - 86400000L);
            namespaces.add(ns);
        }
        return namespaces;
    }

    private List<PodInfo> getMockPods(String namespace) {
        List<PodInfo> pods = new ArrayList<>();
        String[] podNames = {"app-deployment-abc123-xyz", "nginx-pod-123", "redis-master-0"};
        for (String name : podNames) {
            PodInfo pod = new PodInfo();
            pod.setPodName(name);
            pod.setNamespace(namespace != null ? namespace : "default");
            pod.setStatus("Running");
            pod.setPodIP("10.244.0." + (int) (Math.random() * 100 + 1));
            pod.setHostIP("192.168.1." + (int) (Math.random() * 10 + 1));
            pod.setNodeName("node-" + (int) (Math.random() * 3 + 1));
            pod.setRestartPolicy("Always");
            pod.setRestartCount((int) (Math.random() * 3));
            pod.setCreateTime(System.currentTimeMillis() - 3600000L);
            pods.add(pod);
        }
        return pods;
    }
}

# Kubernetes Cluster Management Service

## Description

Provides Kubernetes cluster, node, namespace and pod management capabilities.

## Features

- **Cluster Management**: List and get cluster information
- **Node Management**: List and get node details
- **Namespace Management**: Create, list, delete namespaces
- **Pod Management**: List pods, get pod info, get pod logs
- **Mock Mode**: Supports mock mode for development without real K8s cluster

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/k8s/clusters | List clusters |
| GET | /api/k8s/clusters/{clusterId} | Get cluster info |
| GET | /api/k8s/nodes | List nodes |
| GET | /api/k8s/nodes/{nodeName} | Get node info |
| GET | /api/k8s/namespaces | List namespaces |
| POST | /api/k8s/namespaces | Create namespace |
| DELETE | /api/k8s/namespaces/{namespace} | Delete namespace |
| GET | /api/k8s/pods | List pods |
| GET | /api/k8s/pods/{namespace}/{podName} | Get pod info |
| GET | /api/k8s/pods/{namespace}/{podName}/logs | Get pod logs |
| GET | /api/k8s/status | Get service status |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| k8s.master-url | "" | Kubernetes API server URL |
| k8s.token | "" | Kubernetes access token |
| k8s.namespace | default | Default namespace |
| k8s.mock-enabled | true | Enable mock mode |

## Usage

```bash
# List clusters
curl http://localhost:8095/api/k8s/clusters

# List nodes
curl http://localhost:8095/api/k8s/nodes

# List namespaces
curl http://localhost:8095/api/k8s/namespaces

# Create namespace
curl -X POST http://localhost:8095/api/k8s/namespaces \
  -H "Content-Type: application/json" \
  -d '{"name": "my-namespace"}'

# List pods
curl http://localhost:8095/api/k8s/pods

# Get pod logs
curl http://localhost:8095/api/k8s/pods/default/my-pod/logs?tailLines=100
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| K8S_MASTER_URL | Kubernetes API server URL |
| K8S_TOKEN | Kubernetes access token |
| K8S_NAMESPACE | Default namespace |
| K8S_MOCK_ENABLED | Enable mock mode (true/false) |

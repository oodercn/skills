# Hosting Service Skill

## Description

Provides container hosting, instance management, and resource quota capabilities for Ooder Agent Platform.

## Capabilities

- **instance-management**: Create, update, delete and manage instances
- **resource-quota**: Manage resource quotas and limits
- **health-monitoring**: Monitor instance health and metrics

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/hosting/instances | Get all instances |
| POST | /api/hosting/instances | Create instance |
| GET | /api/hosting/instances/{id} | Get instance |
| PUT | /api/hosting/instances/{id} | Update instance |
| DELETE | /api/hosting/instances/{id} | Delete instance |
| POST | /api/hosting/instances/{id}/start | Start instance |
| POST | /api/hosting/instances/{id}/stop | Stop instance |
| POST | /api/hosting/instances/{id}/restart | Restart instance |
| POST | /api/hosting/instances/{id}/scale | Scale instance |
| GET | /api/hosting/instances/{id}/health | Get instance health |
| GET | /api/hosting/instances/{id}/metrics | Get instance metrics |
| GET | /api/hosting/instances/{id}/logs | Get instance logs |
| GET | /api/hosting/quota | Get quota |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| hosting.max-instances | 100 | Maximum instances |
| hosting.max-cpu-cores | 16 | Maximum CPU cores |
| hosting.max-memory-mb | 32768 | Maximum memory (MB) |

## Supported Types

- docker
- kubernetes
- ecs

## Version

0.7.3

## Author

Ooder Team

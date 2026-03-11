# Health Check Skill

## Description

Provides system health check, service check, and report generation capabilities for Ooder Agent Platform.

## Capabilities

- **health-check**: Perform system health checks
- **service-check**: Check service status
- **report-generation**: Generate health reports

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/health/check | Run health check |
| GET | /api/health/report | Export health report |
| POST | /api/health/schedule | Schedule health check |
| GET | /api/health/service/{name} | Check service |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| health.check-interval | 60000 | Health check interval (ms) |
| health.timeout | 30000 | Health check timeout (ms) |
| health.max-concurrent | 10 | Maximum concurrent checks |

## Version

0.7.3

## Author

Ooder Team

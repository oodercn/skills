# Agent Management Skill

## Description

Provides end agent registration, management, and command execution capabilities for Ooder Agent Platform.

## Capabilities

- **agent-management**: Register and manage end agents
- **command-execution**: Execute commands on agents
- **network-status**: Monitor network status

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/agents/status | Get network status |
| GET | /api/agents/stats | Get command stats |
| GET | /api/agents | Get agent list |
| POST | /api/agents | Add agent |
| PUT | /api/agents/{id} | Edit agent |
| DELETE | /api/agents/{id} | Delete agent |
| GET | /api/agents/{id} | Get agent details |
| POST | /api/agents/test-command | Test command execution |
| GET | /api/agents/logs | Get log list |
| DELETE | /api/agents/logs | Clear logs |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| agent.heartbeat-interval | 30000 | Heartbeat interval (ms) |
| agent.timeout | 60000 | Agent timeout (ms) |
| agent.max-retry-count | 3 | Maximum retry count |

## Dependencies

- skill-network >= 0.7.3

## Version

0.7.3

## Author

Ooder Team

# Protocol Management Skill

## Description

Provides protocol handler registration and command dispatch capabilities for Ooder Agent Platform.

## Capabilities

- **handler-registration**: Register protocol handlers
- **command-dispatch**: Dispatch commands to handlers
- **handler-management**: Manage protocol handlers

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/protocol/handlers | Get protocol handlers |
| POST | /api/protocol/handlers | Register protocol handler |
| DELETE | /api/protocol/handlers/{type} | Remove protocol handler |
| POST | /api/protocol/command | Handle protocol command |
| POST | /api/protocol/refresh | Refresh protocol handlers |
| GET | /api/protocol/search | Search protocol handlers |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| protocol.scan-interval | 60000 | Handler scan interval (ms) |
| protocol.command-timeout | 30000 | Command timeout (ms) |
| protocol.max-handlers | 100 | Maximum handlers |

## Version

0.7.3

## Author

Ooder Team

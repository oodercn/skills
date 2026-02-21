# OpenWrt Management Skill

## Description

Provides OpenWrt router connection, configuration, and command execution capabilities for Ooder Agent Platform.

## Capabilities

- **router-connection**: Connect to OpenWrt routers
- **uci-config**: Manage UCI configuration
- **command-execution**: Execute commands on router

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/openwrt/connect | Connect to router |
| POST | /api/openwrt/disconnect | Disconnect from router |
| GET | /api/openwrt/status | Check connection status |
| GET | /api/openwrt/settings | Get network settings |
| PUT | /api/openwrt/settings/{type} | Update network settings |
| GET | /api/openwrt/ips | Get IP addresses |
| GET | /api/openwrt/blacklist | Get IP blacklist |
| POST | /api/openwrt/command | Execute command |
| POST | /api/openwrt/reboot | Reboot router |
| GET | /api/openwrt/system | Get system status |
| GET | /api/openwrt/version | Get version info |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| openwrt.connection-timeout | 30000 | Connection timeout (ms) |
| openwrt.command-timeout | 60000 | Command timeout (ms) |
| openwrt.retry-count | 3 | Retry count |

## Dependencies

- skill-network >= 0.7.3

## Version

0.7.3

## Author

Ooder Team

# Network Management Skill

## Description

Provides network configuration, IP management, and device scanning capabilities for Ooder Agent Platform.

## Capabilities

- **network-config**: Manage network settings including DNS, DHCP, WiFi
- **ip-management**: Manage IP addresses and blacklists
- **device-scanning**: Scan and discover network devices

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/network/settings | Get all network settings |
| GET | /api/network/settings/{type} | Get network setting by type |
| PUT | /api/network/settings/{type} | Update network setting |
| GET | /api/network/ips | Get IP address list |
| POST | /api/network/ips | Add static IP address |
| DELETE | /api/network/ips/{id} | Delete IP address |
| GET | /api/network/blacklist | Get IP blacklist |
| POST | /api/network/blacklist | Add IP to blacklist |
| DELETE | /api/network/blacklist/{id} | Remove IP from blacklist |
| GET | /api/network/devices | Get network devices |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| network.scan-interval | 300000 | Network scan interval (ms) |
| network.dhcp-lease-file | /var/lib/dhcp/dhcpd.leases | DHCP lease file path |
| network.arp-cache-file | /proc/net/arp | ARP cache file path |

## Version

0.7.3

## Author

Ooder Team

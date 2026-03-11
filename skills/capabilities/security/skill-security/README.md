# Security Management Skill

## Description

Provides user management, permission control, and security audit capabilities for Ooder Agent Platform.

## Capabilities

- **user-management**: Manage users and their profiles
- **permission-control**: Control user permissions and roles
- **security-audit**: Audit security events

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/security/status | Get security status |
| GET | /api/security/users | Get user list |
| POST | /api/security/users | Add user |
| PUT | /api/security/users/{id} | Edit user |
| DELETE | /api/security/users/{id} | Delete user |
| POST | /api/security/users/{id}/enable | Enable user |
| POST | /api/security/users/{id}/disable | Disable user |
| GET | /api/security/permissions | Get permissions |
| POST | /api/security/permissions | Save permissions |
| GET | /api/security/logs | Get security logs |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| security.session-timeout | 1800000 | Session timeout (ms) |
| security.max-login-attempts | 5 | Maximum login attempts |
| security.lockout-duration | 1800000 | Lockout duration (ms) |

## Version

0.7.3

## Author

Ooder Team

# Skill Share Skill

## Description

Provides skill sharing and receiving management capabilities for Ooder Agent Platform.

## Capabilities

- **skill-sharing**: Share skills with others
- **skill-receiving**: Receive shared skills
- **share-management**: Manage skill shares

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/share/skill | Share skill |
| GET | /api/share/shared | Get shared skills |
| GET | /api/share/received | Get received skills |
| DELETE | /api/share/{id} | Cancel share |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| share.max-skills | 100 | Maximum shared skills |
| share.expiration-days | 30 | Share expiration days |

## Version

0.7.3

## Author

Ooder Team

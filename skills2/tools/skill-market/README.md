# Skill Market Service

## Description

Skill Market Service provides skill discovery, search, installation and management capabilities.

## Features

- **Skill Discovery**: Browse and list available skills
- **Skill Search**: Search skills by keyword, category, and tags
- **Skill Installation**: Install, update, and uninstall skills
- **Skill Authentication**: Query skill authentication status

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/skillcenter/market/list | List all available skills |
| POST | /api/skillcenter/market/search | Search skills |
| GET | /api/skillcenter/market/{skillId} | Get skill details |
| POST | /api/skillcenter/market/{skillId}/install | Install a skill |
| DELETE | /api/skillcenter/market/{skillId} | Uninstall a skill |
| PUT | /api/skillcenter/market/{skillId}/update | Update a skill |
| GET | /api/skillcenter/market/{skillId}/auth | Get authentication status |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| market.cache-enabled | true | Enable skill cache |
| market.cache-ttl | 3600 | Cache TTL in seconds |

## Usage

```bash
# List all skills
curl http://localhost:8091/api/skillcenter/market/list

# Search skills
curl -X POST http://localhost:8091/api/skillcenter/market/search \
  -H "Content-Type: application/json" \
  -d '{"keyword": "feishu", "page": 1, "pageSize": 10}'

# Install a skill
curl -X POST http://localhost:8091/api/skillcenter/market/skill-org-feishu/install
```

# Collaboration Scene Service

## Description

Collaboration Scene Service provides scene creation, member management and key management capabilities for team collaboration.

## Features

- **Scene Management**: Create, update, delete and manage collaboration scenes
- **Member Management**: Add, remove and list scene members with roles
- **Key Management**: Generate and rotate scene keys for secure access
- **Status Management**: Start, stop, pause collaboration scenes

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/collaboration/scene/create | Create a collaboration scene |
| GET | /api/collaboration/scene/list | List scenes |
| GET | /api/collaboration/scene/{sceneId} | Get scene details |
| PUT | /api/collaboration/scene/{sceneId} | Update scene |
| DELETE | /api/collaboration/scene/{sceneId} | Delete scene |
| POST | /api/collaboration/scene/{sceneId}/member | Add member |
| DELETE | /api/collaboration/scene/{sceneId}/member/{memberId} | Remove member |
| GET | /api/collaboration/scene/{sceneId}/members | List members |
| POST | /api/collaboration/scene/{sceneId}/key | Generate/rotate key |
| POST | /api/collaboration/scene/{sceneId}/status | Change status |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| collaboration.max-members | 500 | Maximum members per scene |
| collaboration.key-ttl | 86400 | Scene key TTL in seconds |

## Usage

```bash
# Create a scene
curl -X POST http://localhost:8092/api/collaboration/scene/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Project Alpha",
    "description": "Project Alpha collaboration scene",
    "ownerId": "user001",
    "ownerName": "John Doe",
    "skillIds": ["skill-network", "skill-security"]
  }'

# List scenes
curl http://localhost:8092/api/collaboration/scene/list?ownerId=user001

# Add member
curl -X POST http://localhost:8092/api/collaboration/scene/scene-xxx/member \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": "user002",
    "memberName": "Jane Doe",
    "role": "admin"
  }'

# Generate key
curl -X POST http://localhost:8092/api/collaboration/scene/scene-xxx/key
```

## Scene Status

| Status | Description |
|--------|-------------|
| CREATED | Scene created, not started |
| ACTIVE | Scene is active |
| PAUSED | Scene is paused |
| STOPPED | Scene is stopped |
| ARCHIVED | Scene is archived |

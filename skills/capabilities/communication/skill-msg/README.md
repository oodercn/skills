# Message Service

## Description

Message Service provides message sending, broadcast, group messaging and message management capabilities.

## Features

- **Message Sending**: Send messages to users
- **Broadcast**: Broadcast messages to groups
- **Message Management**: Mark read, recall messages
- **Group Messaging**: Create groups, join groups, list groups

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/msg/send | Send message to user |
| POST | /api/msg/broadcast | Broadcast message to group |
| POST | /api/msg/list | Get message list |
| POST | /api/msg/read | Mark message as read |
| POST | /api/msg/recall | Recall message |
| POST | /api/msg/group/create | Create message group |
| POST | /api/msg/group/join | Join message group |
| POST | /api/msg/group/list | List message groups |

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| msg.max-message-size | 65536 | Maximum message size in bytes |
| msg.history-limit | 1000 | Message history limit |

## Usage

```bash
# Send message
curl -X POST http://localhost:8093/api/msg/send \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserId": "user001",
    "fromUserName": "John",
    "toUserId": "user002",
    "content": "Hello!",
    "type": "text"
  }'

# Broadcast to group
curl -X POST http://localhost:8093/api/msg/broadcast \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserId": "user001",
    "groupId": "group-xxx",
    "content": "Group announcement"
  }'

# Create group
curl -X POST http://localhost:8093/api/msg/group/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Project Team",
    "type": "project",
    "ownerId": "user001",
    "ownerName": "John"
  }'
```

## Message Types

| Type | Description |
|------|-------------|
| text | Text message |
| image | Image message |
| file | File message |
| broadcast | Broadcast message |

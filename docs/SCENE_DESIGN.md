# Scene Design Specification

## Overview

Scenes define collaboration contexts where multiple skills work together.

## Scene Definition

```yaml
scenes:
  - name: scene-name
    description: Scene description
    capabilities:
      - capability-1
      - capability-2
    roles:
      - roleId: provider
        name: Provider Role
        required: true
        capabilities: [capability-1]
    maxMembers: 10
```

## Built-in Scenes

### ui-generation
UI generation scene for code generation and preview.

**Required Capabilities:**
- generate-ui
- preview-ui

### auth
Authentication scene for user auth and organization data.

**Required Capabilities:**
- user-auth
- org-data-read

### utility
Utility scene for general purpose tasks.

**Required Capabilities:**
- execute-task

## Scene Collaboration

Skills join scenes through SceneGroups:

```java
SceneGroup group = sdk.createSceneGroup("ui-generation");
group.join(endAgent);

// Collaborate with other members
List<AgentInfo> members = group.getMembers();
for (AgentInfo member : members) {
    // Invoke capabilities from other skills
}
```

## Scene Group Lifecycle

1. **Created** - Scene group is created
2. **Active** - Skills can join/leave
3. **Inactive** - No active members
4. **Closed** - Scene group is destroyed

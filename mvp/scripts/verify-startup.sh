#!/bin/bash

# MVP Core Startup Verification Script

PROFILE=${1:-micro}

echo "========================================"
echo "  MVP Core Startup Verification Script"
echo "========================================"
echo ""

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MVP_PATH="$(dirname "$SCRIPT_DIR")"
ROOT_PATH="$(dirname "$MVP_PATH")"

echo "[1/5] Checking profile: $PROFILE"
PROFILE_PATH="$MVP_PATH/profiles/$PROFILE.json"
if [ -f "$PROFILE_PATH" ]; then
    echo "  Profile file found: $PROFILE_PATH"
else
    echo "  Profile file not found, using default"
fi

echo ""
echo "[2/5] Checking core modules..."

MODULES=(
    "skills/_system/skill-common"
    "skills/_system/skill-capability"
)

for MODULE in "${MODULES[@]}"; do
    MODULE_PATH="$ROOT_PATH/$MODULE"
    if [ -d "$MODULE_PATH" ]; then
        echo "  [OK] $MODULE"
    else
        echo "  [MISSING] $MODULE"
    fi
done

echo ""
echo "[3/5] Checking optional skills..."

OPTIONAL_SKILLS=(
    "skills/_drivers/llm/skill-llm-base"
    "skills/_drivers/llm/skill-llm-openai"
    "skills/capabilities/knowledge/skill-knowledge-base"
    "skills/capabilities/security/skill-audit"
)

for SKILL in "${OPTIONAL_SKILLS[@]}"; do
    SKILL_PATH="$ROOT_PATH/$SKILL"
    if [ -d "$SKILL_PATH" ]; then
        echo "  [OK] $SKILL"
    else
        echo "  [SKIP] $SKILL"
    fi
done

echo ""
echo "[4/5] Checking Java environment..."

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "  Java: $JAVA_VERSION"
else
    echo "  Java not found!"
fi

echo ""
echo "[5/5] Checking Maven environment..."

if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version 2>&1 | head -n 1)
    echo "  Maven: $MAVEN_VERSION"
else
    echo "  Maven not found!"
fi

echo ""
echo "========================================"
echo "  Verification Complete!"
echo "========================================"
echo ""
echo "To start MVP Core with profile '$PROFILE':"
echo "  cd $MVP_PATH"
echo "  mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE"
echo ""
echo "Or build and run:"
echo "  mvn clean package -DskipTests"
echo "  java -jar target/mvp-core-2.3.jar --spring.profiles.active=$PROFILE"

#!/bin/bash

set -e

PROJECT_DIR="${1:-.}"
OUTPUT_FILE="${2:-pom.xml}"

if [[ -f "$PROJECT_DIR/build.gradle" ]]; then
  BUILD_FILE="$PROJECT_DIR/build.gradle"
  IS_KOTLIN_DSL=false
elif [[ -f "$PROJECT_DIR/build.gradle.kts" ]]; then
  BUILD_FILE="$PROJECT_DIR/build.gradle.kts"
  IS_KOTLIN_DSL=true
else
  exit 1
fi

if [[ "$IS_KOTLIN_DSL" == "false" ]]; then
  PROJECT_VERSION=$(grep -E '^version' "$BUILD_FILE" | cut -d"'" -f2 || echo "1.0.0")
  PROJECT_GROUP=$(grep -E '^group' "$BUILD_FILE" | cut -d"'" -f2 || echo "com.example")
else
  PROJECT_VERSION=$(grep -E '^version' "$BUILD_FILE" | sed -n 's/version\s*=\s*"\(.*\)"/\1/p' || echo "1.0.0")
  PROJECT_GROUP=$(grep -E '^group' "$BUILD_FILE" | sed -n 's/group\s*=\s*"\(.*\)"/\1/p' || echo "com.example")
fi
PROJECT_NAME=$(basename "$PROJECT_DIR")

DEP_FILE="$PROJECT_DIR/dependencies.txt"
gradle -q dependencies --configuration runtimeClasspath > "$DEP_FILE"

DEPENDENCIES=$(grep -E '^[^| ]+:[^:]+:[^ ]+$' "$DEP_FILE" | sort | uniq | while read -r line; do
  GROUP_ID=$(echo "$line" | cut -d':' -f1)
  ARTIFACT_ID=$(echo "$line" | cut -d':' -f2)
  VERSION=$(echo "$line" | cut -d':' -f3)
  cat <<EOF
        <dependency>
            <groupId>$GROUP_ID</groupId>
            <artifactId>$ARTIFACT_ID</artifactId>
            <version>$VERSION</version>
        </dependency>
EOF
done)

cat <<EOF > "$OUTPUT_FILE"
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>$PROJECT_GROUP</groupId>
    <artifactId>$PROJECT_NAME</artifactId>
    <version>$PROJECT_VERSION</version>
    <dependencies>
$DEPENDENCIES
    </dependencies>
</project>
EOF

echo "POM file generated successfully: $OUTPUT_FILE"

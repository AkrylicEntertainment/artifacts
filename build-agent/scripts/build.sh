#!/bin/bash

# Steps of this build script
# 1. Clone github repository
# 2. Detect the type of build system they use
# 3. Run build system specific build command
# 4. Move artifact to ../artifacts/output.jar

owner=$(head -n 1 "$1")
repository=$(head -n 1 "$2")

git clone https://github.com/$owner/$repository.git project

build_system=$(determineBuildSystem)

if [[ "$build_system" == "unknown" ]]; then
  echo "Unknown build system"
  exit 1
fi

echo "Build system: $build_system"
if [[ "$build_system" == "gradle" ]]; then
  gradle build
  mv build/libs/*.jar ../artifacts/output.jar
elif [[ "$build_system" == "maven" ]]; then
  mvn clean install
  mv target/*.jar ../artifacts/output.jar
fi

function determineBuildSystem() {
  if [[ -f "build.gradle" ]]; then
    echo "gradle"
  elif [[ -f "pom.xml" ]]; then
    echo "maven"
  else
    echo "unknown"
  fi
}
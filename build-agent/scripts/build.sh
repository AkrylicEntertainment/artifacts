#!/bin/bash

# Steps of this build script
# 1. Clone GitHub repository
# 2. Detect the type of build system they use
# 3. Run build system-specific build command
# 4. Move artifact to ../artifacts/output.jar

owner="$1"
repository="$2"

mkdir -p ../artifacts
git clone https://github.com/"$owner"/"$repository".git project || exit

cd project || exit

function determineBuildSystem() {
  if [[ -f "build.gradle" ]]; then
    echo "gradle"
  elif [[ -f "pom.xml" ]]; then
    echo "maven"
  else
    echo "unknown"
  fi
}

build_system=$(determineBuildSystem)

if [[ "$build_system" == "unknown" ]]; then
  echo "Unknown build system"
  exit 1
fi

echo "Build system: $build_system"

if [[ "$build_system" == "gradle" ]]; then
  gradle build || { echo "Gradle build failed"; exit 1; }
  mv build/libs/*.jar ../artifacts/output.jar || exit
elif [[ "$build_system" == "maven" ]]; then
  mvn clean install || { echo "Maven build failed"; exit 1; }
  mv target/*.jar ../artifacts/output.jar || exit
fi

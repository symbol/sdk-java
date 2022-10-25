#!/bin/bash
set -e

rootDir="$(dirname $0)/.."

ARTIFACT_NAME="catbuffer-cpp"
RELEASE_VERSION="$(head -n 1 ${rootDir}/version.txt)"
OPERATION="$1"
SNAPSHOT_VERSION="${RELEASE_VERSION}-SNAPSHOT"
CURRENT_VERSION="$SNAPSHOT_VERSION"
if [[ $OPERATION == "release" ]]; then
  CURRENT_VERSION="$RELEASE_VERSION"
fi

echo "Building C++ version $CURRENT_VERSION, operation $OPERATION"

${rootDir}/scripts/generate_all.sh cpp_builder

# TODO Fix aggregate and compile c++

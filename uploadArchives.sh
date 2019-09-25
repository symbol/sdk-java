#!/usr/bin/env bash
set -e

echo "Uploading generated clients jars"
./gradlew -b ./openapi-generator/build.gradle uploadArchives -x javadoc

echo "Uploading sdk jars"
./gradlew uploadArchives -x javadoc

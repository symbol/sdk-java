#!/usr/bin/env bash
set -e

echo "Releasing sdk jars and javadocs"
./gradlew release uploadArchives publishGhPages

git push

#!/usr/bin/env bash
set -e


if [ "$RELEASE_ENABLED" = "true" ]
then
  echo "Releasing sdk jars and javadocs"
  ./gradlew release uploadArchives publishGhPages
  git push
else
  echo "Release is disabled"
fi
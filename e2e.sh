#!/usr/bin/env bash
set -e

if [ "$TRAVIS" = "true" ]; then
    echo "Installing node"
    nvm install v12.18.3
    npm install -g symbol-bootstrap
fi
./gradlew build
source bootstrap-start.sh -d
./gradlew integrationTest
source bootstrap-stop.sh

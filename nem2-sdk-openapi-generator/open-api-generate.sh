#!/usr/bin/env bash
set -e
set -o pipefail

VERSION="0.7.17"
CURRENT_FOLDER="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

function exe() { echo "\$ $@" ; "$@" ; }

function generateLib() {
	LIBRARY="$1"
	ARTIFACT_ID="nem2-sdk-java-openapi-$LIBRARY"
	echo "generating lib for $LIBRARY"
    	exe rm -rf "$CURRENT_FOLDER/target/$ARTIFACT_ID"

	exe openapi-generator generate -g java \
	--output "$CURRENT_FOLDER/target/$ARTIFACT_ID" \
	--library "2$LIBRARY" \
	--artifact-version "$VERSION" \
	--artifact-id "$ARTIFACT_ID" \
	--group-id 'io.nem' -i "$CURRENT_FOLDER/openapi3-any-of-patch.yaml" \
	--invoker-package "io.nem.sdk.openapi.$LIBRARY.invoker" \
	--api-package "io.nem.sdk.openapi.$LIBRARY.api" \
	--model-package "io.nem.sdk.openapi.$LIBRARY.model"

	exe "$CURRENT_FOLDER/../gradlew" -b "$CURRENT_FOLDER/target/$ARTIFACT_ID/build.gradle" install
}

generateLib "vertx"
generateLib "jersey2"
generateLib "okhttp-gson"

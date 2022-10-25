#!/bin/bash
set -e

generatorsRootDir="$(dirname $0)/.."
topLevelRoot="$(git rev-parse --show-superproject-working-tree)"

ARTIFACT_NAME="catbuffer-java"
RELEASE_VERSION="$(head -n 1 ${generatorsRootDir}/version.txt)"
OPERATION="$1"
SNAPSHOT_VERSION="${RELEASE_VERSION}-SNAPSHOT"
CURRENT_VERSION="$SNAPSHOT_VERSION"
if [[ $OPERATION == "release" ]]; then
  CURRENT_VERSION="$RELEASE_VERSION"
fi

echo "Building Java version $CURRENT_VERSION, operation $OPERATION, rootDir = ${generatorsRootDir}"


rm -rf "${generatorsRootDir}/build/java/$ARTIFACT_NAME"
mkdir -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/main/java/io/nem/symbol/catapult/builders"
PYTHONPATH=".:${topLevelRoot}/catbuffer-parser:${PYTHONPATH}" python3 "${topLevelRoot}/catbuffer-parser/main.py" \
  --schema "${topLevelRoot}/catbuffer-schemas/schemas/all.cats" \
  --include "${topLevelRoot}/catbuffer-schemas/schemas" \
  --output "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/main/java/io/nem/symbol/catapult/builders" \
  --generator java \
  --copyright catbuffer/HEADER.inc

#python3 -m generators \
#  --input "${generatorsRootDir}/schemas/symbol.yml" \
#  --output "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/main/java/io/nem/symbol/catapult/builders" \
#  --generator java \
#  --copyright HEADER.inc

if [[ $OPERATION == "release" ]]; then
  ARTIFACT_VERSION="${ARTIFACT_VERSION%$SNAPSHOT_PREFIX}"
fi
echo $ARTIFACT_VERSION

mkdir -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/java/io/nem/symbol/catapult/builders"
mkdir -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/resources"
cp -r "${generatorsRootDir}/test/vector" "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/resources"

rm "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/resources/vector/states.yml"

#echo find "${generatorsRootDir}/test/vector" -name '*.yml' -print0 \| xargs -0 -I FILES cp FILES "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/resources/"
#find "${generatorsRootDir}/test/vector" -name '*.yml' -print0 | xargs -0 -I FILES cp FILES "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/resources/"
cp "${generatorsRootDir}/generators/java/VectorTest.java" "${generatorsRootDir}/build/java/$ARTIFACT_NAME/src/test/java/io/nem/symbol/catapult/builders"


cp "${generatorsRootDir}/generators/java/build.gradle" "${generatorsRootDir}/build/java/$ARTIFACT_NAME"
cp "${generatorsRootDir}/generators/java/settings.gradle" "${generatorsRootDir}/build/java/$ARTIFACT_NAME"

sed -i -e "s/#artifactName/$ARTIFACT_NAME/g" "${generatorsRootDir}/build/java/$ARTIFACT_NAME/settings.gradle"
sed -i -e "s/#artifactVersion/$CURRENT_VERSION/g" "${generatorsRootDir}/build/java/$ARTIFACT_NAME/build.gradle"

# if [[ $OPERATION == "release" ]]; then
#   echo "Releasing artifact $CURRENT_VERSION"
#   #${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish closeAndReleaseRepository
#   echo gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish
#   #gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish closeAndReleaseRepository
# elif [[ $OPERATION == "publish" ]]; then
#   echo "Publishing artifact $CURRENT_VERSION"
#   #${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish
#   echo gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish
#   #gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish
# else
#   echo "Installing artifact $CURRENT_VERSION"
#   #${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test install
#   echo gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test install
#   #gradle -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test install
# fi

# if [[ $OPERATION == "release" ]]; then
#   echo "Releasing artifact $CURRENT_VERSION"
#   ${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish closeAndReleaseRepository
# elif [[ $OPERATION == "publish" ]]; then
#   echo "Publishing artifact $CURRENT_VERSION"
#   ${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test publish
# else
  echo "Installing artifact $CURRENT_VERSION"
  ${generatorsRootDir}/gradlew -p "${generatorsRootDir}/build/java/$ARTIFACT_NAME/" test install
# fi

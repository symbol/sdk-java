#!/bin/bash
set -e

rootDir="$(dirname $0)/.."

RELEASE_VERSION="$(head -n 1 ${rootDir}/version.txt)"
OPERATION="$1"
ARTIFACT_NAME="catbuffer-typescript"
ALPHA_VERSION="${RELEASE_VERSION}-alpha-$(date +%Y%m%d%H%M)"
CURRENT_VERSION="$ALPHA_VERSION"
if [[ $OPERATION == "release" ]]; then
  CURRENT_VERSION="$RELEASE_VERSION"
fi

echo "Building Typescript version $CURRENT_VERSION, operation $OPERATION"

#rm -rf "$rootDir/build/typescript/$ARTIFACT_NAME"

mkdir -p "$rootDir/build/typescript/$ARTIFACT_NAME/src/"
PYTHONPATH=".:${PYTHONPATH}" python3 "catbuffer/main.py" \
  --schema catbuffer/schemas/all.cats \
  --include catbuffer/schemas \
  --output "$rootDir/build/typescript/$ARTIFACT_NAME/src" \
  --generator typescript \
  --copyright catbuffer/HEADER.inc

mkdir -p "$rootDir/build/typescript/$ARTIFACT_NAME/test/vector"
cp -r "$rootDir/test/vector" "$rootDir/build/typescript/$ARTIFACT_NAME/test"
cp "$rootDir/generators/typescript/VectorTest.test.ts" "$rootDir/build/typescript/$ARTIFACT_NAME/test"

cp "$rootDir/generators/typescript/.npmignore" "$rootDir/build/typescript/$ARTIFACT_NAME"
cp "$rootDir/generators/typescript/package.json" "$rootDir/build/typescript/$ARTIFACT_NAME"
cp "$rootDir/generators/typescript/README.md" "$rootDir/build/typescript/$ARTIFACT_NAME"
cp "$rootDir/generators/typescript/tsconfig.json" "$rootDir/build/typescript/$ARTIFACT_NAME"
cp "$rootDir/generators/typescript/.eslintrc.js" "$rootDir/build/typescript/$ARTIFACT_NAME"
cp "$rootDir/generators/typescript/.prettierrc.js" "$rootDir/build/typescript/$ARTIFACT_NAME"
sed -i -e "s/#artifactName/$ARTIFACT_NAME/g" "$rootDir/build/typescript/$ARTIFACT_NAME/package.json"
sed -i -e "s/#artifactVersion/$CURRENT_VERSION/g" "$rootDir/build/typescript/$ARTIFACT_NAME/package.json"

npm install --prefix "$rootDir/build/typescript/$ARTIFACT_NAME/"
npm run style:fix --prefix "$rootDir/build/typescript/$ARTIFACT_NAME/"
npm run test --prefix "$rootDir/build/typescript/$ARTIFACT_NAME/"
npm run build --prefix "$rootDir/build/typescript/$ARTIFACT_NAME/"

if [[ $OPERATION == "release" ]]; then
  echo "Releasing artifact $CURRENT_VERSION"
  cp "$rootDir/generators/typescript/.npmignore" "$rootDir/build/typescript/$ARTIFACT_NAME/"
  cp "$rootDir/generators/typescript/.npmrc" "$rootDir/build/typescript/$ARTIFACT_NAME/"
  cd "$rootDir/build/typescript/$ARTIFACT_NAME/" && npm publish
elif [[ $OPERATION == "publish" ]]; then
  echo "Publishing artifact $CURRENT_VERSION"
  cp "$rootDir/generators/typescript/.npmignore" "$rootDir/build/typescript/$ARTIFACT_NAME/"
  cp "$rootDir/generators/typescript/.npmrc" "$rootDir/build/typescript/$ARTIFACT_NAME/"
  cd "$rootDir/build/typescript/$ARTIFACT_NAME/" && npm publish --tag alpha
fi

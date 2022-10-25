#!/bin/bash
set -e

HOME=~/
rootDir="$(dirname "$0")/.."
echo "${rootDir}"

# Artifact naming
artifactPrefix="symbol"
artifactName="catbuffer"

# PEP 440 compliant semantic version is used for uploading to PyPI
# Examples
#   1.2.0.dev1    # Development release
#   1.2.0a1       # Alpha Release
#   1.2.0b1       # Beta Release
#   1.2.0rc1      # Release Candidate
#   1.2.0         # Final Release
#   1.2.0.post1   # Post Release
# For pre-releases (alpha, beta, rc):
# - A UTC timestamp (YYYYMMDD.HHMMSS) is embedded for automatic publishing; no need to increment the prerelease version.
#   e.g. 0.0.3.20200522.070728a1
# - Any leading zeros in the date and/or time portions are dropped during the package build version normalization.
#   e.g. 0.0.3.20200522.70728a1
# PyPI: https://pypi.org/project/catbuffer/
# Test: https://test.pypi.org/project/catbuffer/

releaseArtifactVersion="$(head -n 1 ${rootDir}/version.txt)" # Artifact version
prereleaseSuffix="a1"                           # Pre-release suffix
snapshotDateTime=".$(date -u +'%Y%m%d.%H%M%S')" # Pre-release timestamp
prereleaseVersion="${releaseArtifactVersion}${snapshotDateTime}${prereleaseSuffix}"
snapshot=true
repo="pypi"
upload=false
OPERATION="$1"
artifactVersion="${prereleaseVersion}"

if [[ $OPERATION == "publish" ]]; then
  upload=true
elif [[ $OPERATION == "test" ]]; then
  repo="testpypi"
  REPO_URL="https://test.pypi.org/legacy/"
  upload=true
elif [[ $OPERATION == "release" ]]; then
  artifactVersion="${releaseArtifactVersion}"
  snapshot=false
  upload=true
fi

echo "Building Python version $artifactVersion, operation $OPERATION"

echo "artifactName=${artifactName}"
echo "artifactVersion=${artifactVersion}"
echo "snapshot=${snapshot}"
echo "repo=${repo}"

GIT_USER_ID="$(cut -d'/' -f1 <<<"$TRAVIS_REPO_SLUG")"
GIT_REPO_ID="$(cut -d'/' -f2 <<<"$TRAVIS_REPO_SLUG")"
echo "Travis Repo Slug: $TRAVIS_REPO_SLUG"
echo "Git User ID: $GIT_USER_ID"
echo "Git Repo ID: $GIT_REPO_ID"
if [[ $upload == true ]] && [[ $repo == "pypi" ]] && [[ -n $TRAVIS_REPO_SLUG ]] && [[ $GIT_USER_ID != 'nemtech' ]]; then
  upload=false
  echo "User is not 'nemtech': Disable upload to PyPI"
fi

artifactProjectName="catbuffer-python"
artifactBuildDir="${rootDir}/build/python/${artifactProjectName}"
artifactSrcDir="${artifactBuildDir}/src"
artifactPackageDir="${artifactSrcDir}/${artifactPrefix}_${artifactName}"
artifactTestDir="${artifactBuildDir}/test"

rm -rf "${rootDir}/catbuffer/_generated/python"
rm -rf "${artifactBuildDir}"

mkdir -p "${artifactPackageDir}"
PYTHONPATH=".:${PYTHONPATH}" python3 "catbuffer/main.py" \
  --schema catbuffer/schemas/all.cats \
  --include catbuffer/schemas \
  --output "${artifactPackageDir}" \
  --generator python \
  --copyright catbuffer/HEADER.inc

touch "${artifactPackageDir}/__init__.py"
cp "$rootDir/LICENSE" "${artifactBuildDir}"
cp "$rootDir/.pylintrc" "${artifactBuildDir}"
cp "$rootDir/generators/python/README.md" "${artifactBuildDir}"
cp "$rootDir/generators/python/setup.py" "${artifactBuildDir}"
cp "$rootDir/generators/python/"test_*.py "${artifactBuildDir}"
cp -r "$rootDir/test/vector" "${artifactBuildDir}"
cp "$rootDir/generators/python/.pypirc" "${HOME}"
sed -i -e "s/#artifactName/$artifactName/g" "${artifactBuildDir}/setup.py"
sed -i -e "s/#artifactVersion/$artifactVersion/g" "${artifactBuildDir}/setup.py"

mkdir -p "${artifactTestDir}"
PYTEST_CACHE="$rootDir/test/python/.pytest_cache/"
if [ -d "$PYTEST_CACHE" ]; then rm -Rf "$PYTEST_CACHE"; fi

# Build
cd "${artifactBuildDir}"
echo "Building..."
PYTHONPATH=".:${PYTHONPATH}" python3 setup.py sdist bdist_wheel build

# Test
echo "Testing..."
PYTHONPATH="./src:${PYTHONPATH}" pytest -v --color=yes --exitfirst --showlocals --durations=5
# Linter
echo "Linting..."
PYTHONPATH="./src:${PYTHONPATH}" pylint --rcfile .pylintrc --load-plugins pylint_quotes symbol_catbuffer
# Deploy
if [[ $upload == true ]]; then
  # Log intention
  if [[ $OPERATION == "release" ]]; then
    echo "Releasing python artifact[$artifactName $artifactVersion] to $repo"
  else
    echo "Publishing python artifact[$artifactName $artifactVersion] to $repo"
  fi
  # Do upload
  if [[ $repo == "pypi" ]]; then
    if [[ -n ${PYPI_USER} ]] && [[ -n ${PYPI_PASS} ]]; then
      echo "PYPI_USER and PYPI_PASS are already set: Uploading to PyPI"
      PYTHONPATH=".:${PYTHONPATH}" python3 -m twine upload -u "$PYPI_USER" -p "$PYPI_PASS" dist/*
    else
      echo "PYPI_USER and/or PYPI_PASS not set: Cancelled upload to PyPI"
    fi
  else
    if [[ -n ${TEST_PYPI_USER} ]] && [[ -n ${TEST_PYPI_PASS} ]]; then
      echo "TEST_PYPI_USER and TEST_PYPI_PASS are already set: Uploading to PyPI"
      PYTHONPATH=".:${PYTHONPATH}" python3 -m twine upload --repository-url $REPO_URL -u "$TEST_PYPI_USER" -p "$TEST_PYPI_PASS" dist/*
    else
      echo "TEST_PYPI_USER and/or TEST_PYPI_PASS not set: Initiated manual upload"
      PYTHONPATH=".:${PYTHONPATH}" python3 -m twine upload --repository $repo dist/*
    fi
  fi
fi

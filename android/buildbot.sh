#!/bin/sh
set -e

# gradle build
cp /opt/keys-pros/*.properties ./

java -version
javac -version
gradle --version
# no teacher client temporarily
gradle assembleParentDevrelease assembleParentStagerelease assembleParentRelease
gradle testParentDevreleaseUnitTest testParentStagereleaseUnitTest testParentReleaseUnitTest

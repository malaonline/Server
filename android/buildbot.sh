#!/bin/sh
set -e

# gradle build
cp /opt/keys-pros/*.properties ./
# no teacher client temporarily
gradle assembleParentDevrelease assembleParentStagerelease assembleParentRelease

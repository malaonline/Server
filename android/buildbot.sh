#!/bin/sh

# gradle build
cp /opt/keys-pros/*.properties ./
# no teacher client temporarily
gradle assembleParentDevrelease
gradle assembleParentRelease

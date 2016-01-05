#!/bin/sh
#
# I am AutoBuildBot!

buildTime=$(date +%Y%m%d%H%M)

profile="For test"

buildConfiguration="QA"
buildPath="AutoBuild/ArchiveProduction/QA_${buildTime}.xcarchive"
ipaName="AutoBuild/IPA/Auto_QA_${buildTime}.ipa"
mkdir -p AutoBuild/IPA

xctool -workspace mala-ios.xcworkspace -scheme parent -configuration Release clean
xctool -workspace mala-ios.xcworkspace -scheme parent -configuration Release archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${profile}"

#!/bin/sh
#
# I am AutoBuildBot!

buildTime=$(date +%Y%m%d%H%M)
schema="parent"

buildPath="AutoBuild/ArchiveProduction/${schema}_${buildTime}.xcarchive"
ipaName="AutoBuild/IPA/${schema}_${buildTime}.ipa"
mkdir -p AutoBuild/IPA

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}

xctool -workspace mala-ios.xcworkspace -scheme ${schema} -configuration Release clean
xctool -workspace mala-ios.xcworkspace -scheme ${schema} -configuration Release archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "For jenkins"

#!/bin/sh
#
# I am AutoBuildBot!

buildTime=$(date +%Y%m%d%H%M)

buildConfiguration="QA"
buildPath="AutoBuild/ArchiveProduction/QA_${buildTime}.xcarchive"
ipaName="AutoBuild/IPA/Auto_QA_${buildTime}.ipa"
mkdir -p AutoBuild/IPA

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}

xctool -workspace mala-ios.xcworkspace -scheme parent -configuration Release clean
xctool -workspace mala-ios.xcworkspace -scheme parent -configuration Release archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile ${PROVISIONING_PROFILE}

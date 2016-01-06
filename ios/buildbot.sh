#!/bin/sh

buildTime=$(date +%Y%m%d%H%M)
schema="parent"
provisioning="For test"
buildPath="build/archive/${schema}_${buildTime}.xcarchive"
ipaDir="build/ipa/"
ipaName="${ipaDir}${schema}_${buildTime}.ipa"

mkdir -p ${ipaDir}

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}

xctool -workspace mala-ios.xcworkspace -scheme ${schema} -configuration Release clean
xctool -workspace mala-ios.xcworkspace -scheme ${schema} -configuration Release archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${provisioning}"

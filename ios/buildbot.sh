#!/bin/sh
set -e

scheme="parent"
provisioning="For test"
ipaDir="build/ipa/"
mkdir -p ${ipaDir}

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}

configuration="DevRelease"
buildPath="build/archive/${scheme}_dev_release.xcarchive"
ipaName="${ipaDir}${scheme}_dev_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} test

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
rm -f ${ipaName}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${provisioning}"


configuration="PrdRelease"
buildPath="build/archive/${scheme}_prd_release.xcarchive"
ipaName="${ipaDir}${scheme}_prd_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
rm -f ${ipaName}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${provisioning}"

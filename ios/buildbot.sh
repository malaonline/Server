#!/bin/sh
set -e

## Build configurations
scheme="parent"
schemeSDK="iphoneos"
configuration="DevRelease"
ipaDir="build/ipa/"
archs="armv7"
appName="parent"


## Provisioning configurations
AdHocProvisioning="com.malalaoshi.app-AdHoc"
security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}


# Clean
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa


# Unit test
xcodebuild test -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -destination 'platform=iOS Simulator,name=iPhone 6s,OS=8.4'


# Compile Project DevRelease
echo Building Project
buildPath="build/archive/${scheme}_dev_release.xcarchive"
ipaName="${ipaDir}${scheme}_dev_release.ipa"

xcodebuild -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"


# Compile Project PrdRelease
configuration="PrdRelease"
buildPath="build/archive/${scheme}_prd_release.xcarchive"
ipaName="${ipaDir}${scheme}_prd_release.ipa"

xcodebuild -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

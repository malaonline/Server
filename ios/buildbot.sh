#!/bin/sh
set -e

# Build configurations
scheme="parent"
configuration="DevRelease"
ipaDir="build/ipa/"


# Clean
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa


# Provisioning configurations
AdHocProvisioning="com.malalaoshi.app-AdHoc"
security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}


# Unit Test
xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 5s,OS=8.4' test -freshInstall -freshSimulator


# Compile Project DevRelease
scheme="parent"
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

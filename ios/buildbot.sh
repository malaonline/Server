#!/bin/sh
set -e

scheme="parent"
AdHocProvisioning="com.malalaoshi.app-AdHoc"
ipaDir="build/ipa/"
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}

# UnitTest
scheme="parentTests"

# xctool should upgrade to 0.2.9 or above
xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -sdk iphonesimulator test -freshInstall -freshSimulator


# DevRelease
scheme="parent"
configuration="DevRelease"
buildPath="build/archive/${scheme}_dev_release.xcarchive"
ipaName="${ipaDir}${scheme}_dev_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"


# PrdRelease
configuration="PrdRelease"
buildPath="build/archive/${scheme}_prd_release.xcarchive"
ipaName="${ipaDir}${scheme}_prd_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

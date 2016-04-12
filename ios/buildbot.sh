#!/bin/sh
set -e

scheme="parent"
AppStoreProvisioning="com.malalaoshi.app-AppStore"
AdHocProvisioning="com.malalaoshi.app-AdHoc"
ipaDir="build/ipa/"
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}

configuration="DevRelease"
buildPath="build/archive/${scheme}_dev_release.xcarchive"
ipaName="${ipaDir}${scheme}_dev_release.ipa"

# xctool should upgrade to 0.2.9 or above
#xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 5s,OS=8.4' test

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"


configuration="PrdRelease"
buildPath="build/archive/${scheme}_prd_release.xcarchive"
ipaName="${ipaDir}${scheme}_prd_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AppStoreProvisioning}"

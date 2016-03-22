#!/bin/sh

scheme="parent"
provisioning="For test"
ipaDir="build/ipa/"
mkdir -p ${ipaDir}

security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}

configuration="Release"
buildPath="build/archive/${scheme}_release.xcarchive"
ipaName="${ipaDir}${scheme}_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} clean archive -archivePath ${buildPath}
rm -f ${ipaName}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${provisioning}"


configuration="DevRelease"
buildPath="build/archive/${scheme}_dev_release.xcarchive"
ipaName="${ipaDir}${scheme}_dev_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} clean archive -archivePath ${buildPath}
rm -f ${ipaName}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${provisioning}"

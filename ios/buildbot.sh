#!/bin/sh
set -e

# Build configurations
ipaDir="build/ipa/"
configuration="Release"
derivedDataPath="build/derivedData"

# Clean
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa
rm -rf ${derivedDataPath}

# Provisioning configurations
AdHocProvisioning="com.malalaoshi.app-AppStore"
security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings -l -u -t 3600 ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}


# Unit Test
# xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 5s,OS=8.4' test -freshInstall -freshSimulator


# Compile Project Release


# Export dev package
scheme="parent-dev"
buildPath="build/${scheme}.xcarchive"
ipaName="${ipaDir}${scheme}_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath} -derivedDataPath ${derivedDataPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"


if [ $1 = "dev" ]
then
    echo 'Only build dev for features branches.'
    exit
fi

# Building stage package
scheme="parent-stage"
buildPath="build/${scheme}.xcarchive"
ipaName="${ipaDir}${scheme}_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath} -derivedDataPath ${derivedDataPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

# Buildng prd package
scheme="parent-prd"
buildPath="build/${scheme}.xcarchive"
ipaName="${ipaDir}${scheme}_release.ipa"

xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath} -derivedDataPath ${derivedDataPath}
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

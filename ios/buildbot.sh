#!/bin/sh
set -e

# Build configurations
scheme="parent"
ipaDir="build/ipa/"
configuration="Debug"
derivedDataPath="build/derivedDada"

# Clean
mkdir -p ${ipaDir}
rm -rf ${ipaDir}*.ipa
rm -rf ${derivedDataPath}

# Provisioning configurations
security -v unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH}
security set-keychain-settings ${KEYCHAIN_PATH}
security list-keychains -s ${KEYCHAIN_PATH}


# Unit Test
# xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration DevRelease -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 5s,OS=8.4' test -freshInstall -freshSimulator


# Compile Project Release
buildPath="build/${scheme}.xcarchive"
xctool -workspace mala-ios.xcworkspace -scheme ${scheme} -configuration ${configuration} archive -archivePath ${buildPath} -derivedDataPath ${derivedDataPath}

python --version
python replace_info.py

# Export dev package
cfg="dev"
AdHocProvisioning="Mala_Dev-AdHoc"
ipaName="${ipaDir}${scheme}_${cfg}_release.ipa"
mv build/${cfg}-Info.plist build/${scheme}.xcarchive/Info.plist
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

# Export stage package
cfg="stage"
AdHocProvisioning="Mala_Stage-AdHoc"
ipaName="${ipaDir}${scheme}_${cfg}_release.ipa"
mv build/${cfg}-Info.plist build/${scheme}.xcarchive/Info.plist
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

# Export prd package
cfg="prd"
AdHocProvisioning="Mala_Prd-AdHoc"
ipaName="${ipaDir}${scheme}_${cfg}_release.ipa"
mv build/${cfg}-Info.plist build/${scheme}.xcarchive/Info.plist
xcodebuild -exportArchive -exportFormat IPA -archivePath ${buildPath} -exportPath ${ipaName} -exportProvisioningProfile "${AdHocProvisioning}"

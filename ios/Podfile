inhibit_all_warnings!
# Uncomment this line to define a global platform for your project
platform :ios, '8.0'
# Uncomment this line if you're using Swift
use_frameworks!
inhibit_all_warnings!
workspace 'mala-ios'

pod 'SnapKit', '~> 0.20.0'
pod 'DateTools', '~> 1.7.0'
pod 'Alamofire', '~> 3.3.0'
pod 'Kingfisher', '~> 2.2.0'
pod 'IQKeyboardManagerSwift', '~> 4.0.0'
pod 'Charts', '~> 2.2.4'
pod 'Pingpp/Alipay', '~> 2.2.0'
pod 'Pingpp/Wx', '~> 2.2.0'
pod 'Google/Analytics'

# ShareSDK
pod 'ShareSDK3'
pod 'MOBFoundation'
pod 'ShareSDK3/ShareSDKUI'
pod 'ShareSDK3/ShareSDKPlatforms/WeChat'



target 'parent-dev' do
  target 'parentTests' do
    inherit! :search_paths
  end
end

target 'parent-stage' do
end

target 'parent-prd' do
end

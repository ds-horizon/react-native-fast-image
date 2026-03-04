require 'json'

fabric_enabled = ENV['RCT_NEW_ARCH_ENABLED'] == '1'
disable_svg = ENV['DISABLE_SVG'] == '1'

Pod::Spec.new do |s|
  package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

  s.name          = "RNFastImage"
  s.version       = package['version']
  s.summary       = package['description']
  s.authors       = { "Dream Horizon" => "info@dreamhorizon.org" }
  s.homepage      = "https://github.com/dream-horizon-org/react-native-fast-image#readme"
  s.license       = "MIT"
  s.framework = 'UIKit'
  s.requires_arc  = true
  s.source        = { :git => "https://github.com/dream-horizon-org/react-native-fast-image.git", :tag => "v#{s.version}" }
  disable_svg_flag = disable_svg ? 'DISABLE_SVG=1' : ''

  if fabric_enabled
    folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_CFG_NO_COROUTINES=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

    xcconfig = {
      'HEADER_SEARCH_PATHS' => '"$(PODS_ROOT)/boost" "$(PODS_ROOT)/boost-for-react-native"  "$(PODS_ROOT)/RCT-Folly"',
      "CLANG_CXX_LANGUAGE_STANDARD" => "c++17",
    }
    xcconfig['GCC_PREPROCESSOR_DEFINITIONS'] = "$(inherited) #{disable_svg_flag}" if disable_svg
    s.pod_target_xcconfig = xcconfig
    s.platforms       = { ios: '11.0', tvos: '11.0' }
    s.compiler_flags  = folly_compiler_flags + ' -DRCT_NEW_ARCH_ENABLED'
    s.source_files    = 'ios/**/*.{h,m,mm,cpp}'

    install_modules_dependencies(s)
  else
    s.platforms     = { :ios => "8.0", :tvos => "9.0" }
    s.source_files  = "ios/**/*.{h,mm}"
    s.dependency 'React-Core'
    s.pod_target_xcconfig = { 'GCC_PREPROCESSOR_DEFINITIONS' => "$(inherited) #{disable_svg_flag}" } if disable_svg
  end
  s.dependency 'SDWebImage', '~> 5.21.0'
  s.dependency 'SDWebImageWebPCoder', '~> 0.14.6'
  s.dependency 'SDWebImageAVIFCoder', '~> 0.11.0'
  if !disable_svg
    s.dependency 'SDWebImageSVGCoder', '~> 1.7.0'
  end
  s.dependency 'libavif/libdav1d', '~> 0.11.1'
  s.dependency 'libavif/core', '~> 0.11.1'
end

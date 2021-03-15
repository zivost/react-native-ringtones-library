
Pod::Spec.new do |s|
  s.name         = "RNRingtonesLibrary"
  s.version      = "1.0.0"
  s.summary      = "RNRingtonesLibrary"
  s.description  = <<-DESC
                  RNRingtonesLibrary
                   DESC
  s.homepage     = "https://github.com/sieuhuflit/react-native-ringtone/blob/master/README.md"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNRingtonesLibrary.git", :tag => "master" }
  s.source_files  = "RNRingtonesLibrary/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
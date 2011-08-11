require "fileutils"

SAMPLE_PACKAGE = "com.example.android.sampleapp"
SAMPLE_PACKAGE_DIR = SAMPLE_PACKAGE.gsub('.', "/")

def bail_with(project)
  puts ">>> #{project} failed! <<<"
    exit(1)
end

def replace(filename, original_name, new_name)
  return unless File.exists? filename
  text = File.read(filename)
  replaced = text.gsub!(original_name, new_name)
  File.open(filename, "w") {|file| file.puts text}
  puts ">>> Replaced '#{original_name}' in #{filename} with '#{new_name}'" if replaced
end

def project_setup(name)
  init_android
  rename_project_to(name)
  rename_package
  init_git_repo
end

def init_android
  system "android update project -p ."

  # android update wrongly stomps build.xml
  system "git checkout build.xml"
end

def config_files
  ["AndroidIntelliJStarter.iml",
   ".idea/.name",
   "AndroidManifest.xml",
   "build.xml",
   "res/values/strings.xml"] + Dir.glob('.idea/**/*.xml')
end

def rename_project_to(name)
  files = config_files()
  puts "Searching: #{files.join(",")}\n"
  files.each do |filename|
    replace(filename, "AndroidIntelliJStarter", name)
    replace(filename, "AndroidIntelliJStarter", name)
  end

  File.rename("AndroidIntelliJStarter.iml", "#{name}.iml") if File.exists? "AndroidIntelliJStarter.iml"
  puts ">>> Renamed 'AndroidIntelliJStarter.iml' to '#{name}.iml'"

  # delete workspace.xml, which might hold bad references. It will be regenerated.
  File.delete ".idea/workspace.xml" if File.exists? ".idea/workspace.xml"
end

def validate_package(package)
  if package.index(" ")
    puts "!!! invalid package name: '#{package}'. Using default package."
    return false
  elsif package.nil? || package.length == 0
    puts ">>> Using default package."
    return false
  end

  return true
end

def replace_package(files, new_package, old_package)
  files.each { |filename| replace(filename, old_package, new_package) }
end

def move_source_files(old_package_path, new_package_path)
  puts ">>> Moving #{old_package_path} to #{new_package_path}"
  FileUtils.mv Dir.glob(old_package_path),  new_package_path
end

def rename_package
  puts "\n> Please provide a package name, such as com.example.yourproject"
  puts "> Leave blank to change this later."
  print "> "
  package = gets.chomp!
  return unless validate_package(package)
  package_path = package.gsub('.', '/')

  FileUtils.mkdir_p "src/#{package_path}/"
  FileUtils.mkdir_p "test/java/#{package_path}"

  replace_package(Dir.glob("src/#{SAMPLE_PACKAGE_DIR}/**/*.java"), package, SAMPLE_PACKAGE)
  replace_package(config_files, package, SAMPLE_PACKAGE)
  replace_package(Dir.glob("test/java/#{SAMPLE_PACKAGE_DIR}/**/*.java"), package, SAMPLE_PACKAGE)

  move_source_files("src/#{SAMPLE_PACKAGE_DIR}/*", "src/#{package_path}")
  move_source_files("test/java/#{SAMPLE_PACKAGE_DIR}/*", "test/java/#{package_path}")

  FileUtils.rm_rf "src/com/example"
  FileUtils.rm_rf "test/java/com/example"
end

def init_git_repo
  puts "\n!!! Do you want to create a new git repository? "
  puts "!!! Type 'yes' to back up your .git directory and create a new git repository"
  print "> "
  should_init = gets.chomp!
  
  if should_init.downcase == "yes" 
    puts "!!! Moving .git to .git.bak. Delete this if you don't want it."
    FileUtils.mv ".git", ".git.bak"
    puts "!!! Initializing a new git repository!"
    system "git init ."

    reset_robolectric

    system "git add ."
    system "git commit -am 'Initial Commit'"
    puts "\nNew repository created. It is a local repo only. Add a remote and push it somewhere."
  else 
    puts "!!! You typed '#{should_init}'. Leaving existing git repository."
    puts "!!! Run ./script/init_git (or ruby script/init_git) to try again."
    init_robolectric_default
  end  
end

def reset_robolectric
  puts "Add Robolectric as a non-pushable submodule pointing at HEAD"
  
  FileUtils.rm_rf "submodules"
  system "echo ''> .gitmodules"
  
  system "git submodule add git://github.com/pivotal/robolectric.git submodules/robolectric"
  system "git submodule update --init"
end


def init_robolectric_default
  puts "initializing default robolectric"
  system "git submodule update --init"
  system "(cd submodules/robolectric && git checkout master)"
  system "(cd submodules/robolectric && ant compile)"
  puts ">>> Default robolectric initialized. Change to your fork later."
end



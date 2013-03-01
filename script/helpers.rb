require "fileutils"

SAMPLE_PACKAGE = "com.example.android.sampleapp"
SAMPLE_PACKAGE_DIR = SAMPLE_PACKAGE.gsub('.', "/")
STARTER_PROJECT_DIR = Dir.getwd
ANDROID_HOME = `which android | sed 's|/tools/android$||'`.chomp
SRC_DIR = "src/main/java"
TEST_SRC_DIR = "src/test/java"

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

def project_setup(name, project_directory)
  raise "Error: project directory is not a git repo" unless is_git_repo?(project_directory)
  raise "Error: We couldn't find 'android' in your path" if ANDROID_HOME.empty?

  copy_starter_files_to_project_directory(project_directory)

  in_dir project_directory do
    create_local_properties_file(".")
    rename_project_to(name)
    rename_package
  end

  init_robolectric_as_a_project_submodule(project_directory)

  clear_out_license_file(project_directory)

  puts "#{project_directory} has been prepared and is ready to go. Enjoy!"
end

def is_git_repo?(project_directory)
  File.directory?( File.join(project_directory, ".git") )
end

def has_uncommited_changes?(project_directory)
  has_changes = false
  in_dir project_directory do
    has_changes = ! system("git diff --quiet HEAD")
  end
  has_changes
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

# todo change group id and artifact id in pom.xml
def rename_package
  puts "\n> Please provide a package name, such as com.yourcompany.yourproject"
  puts "> Leave blank to change this later."
  print "> "
  package = gets.chomp!
  return unless validate_package(package)
  package_path = package.gsub('.', '/')

  FileUtils.mkdir_p "#{SRC_DIR}/#{package_path}/"
  FileUtils.mkdir_p "#{TEST_SRC_DIR}/#{package_path}"

  replace_package(Dir.glob("#{SRC_DIR}/#{SAMPLE_PACKAGE_DIR}/**/*.java"), package, SAMPLE_PACKAGE)
  replace_package(config_files, package, SAMPLE_PACKAGE)
  replace_package(Dir.glob("#{TEST_SRC_DIR}/#{SAMPLE_PACKAGE_DIR}/**/*.java"), package, SAMPLE_PACKAGE)

  move_source_files("#{SRC_DIR}/#{SAMPLE_PACKAGE_DIR}/*", "#{SRC_DIR}/#{package_path}")
  move_source_files("#{TEST_SRC_DIR}/#{SAMPLE_PACKAGE_DIR}/*", "#{TEST_SRC_DIR}/#{package_path}")

  FileUtils.rm_rf "#{SRC_DIR}/com/example" unless package.start_with? "com.example"
  FileUtils.rm_rf "#{TEST_SRC_DIR}/com/example" unless package.start_with? "com.example"
end

def copy_starter_files_to_project_directory(project_directory)
  puts "Copying starter files to #{project_directory}"

  # copy everything excluding .git, .gitmodules, and submodules
  system( 'cp -ai `ls -a | egrep -v \'^\.$|^\.\.$|^\.git$|^\.gitmodules$|^submodules$\'` ' + project_directory )

end

def init_robolectric_as_a_project_submodule(project_directory)
  puts "\n> Please provide a remote repo for your robolectric fork, such as git@github.com:yourcompany/robolectric.git"
  puts "> Leave blank to add robolectric as a submodule from the main repo."
  print "> "
  remote_repo = gets.chomp!

  if remote_repo.nil? || remote_repo.length == 0
    remote_repo = "https://github.com/pivotal/robolectric.git"
    puts "Adding Robolectric submodule to #{project_directory} as a submodule from the main repo"
  else
    puts "Adding Robolectric submodule to #{project_directory}"
  end

  in_dir project_directory do
    system! "git submodule add #{remote_repo} submodules/robolectric"
    system! "git submodule update --init"
    create_local_properties_file("submodules/robolectric");
  end
end

def create_local_properties_file(directory)
  puts directory
  File.open(directory + "/local.properties", 'w') {|f| f.write("sdk.dir=" + ANDROID_HOME) }
end

def clear_out_license_file(project_directory)
  File.open(File.join(project_directory, 'LICENSE.txt'), 'w') {|file| file.truncate(0) }
end

def system!(command)
  system(command) || raise("There was an error while executing: #{command}")
end

def in_dir(dir)
  original_dir = Dir.getwd
  Dir.chdir dir
  yield
  Dir.chdir original_dir
end

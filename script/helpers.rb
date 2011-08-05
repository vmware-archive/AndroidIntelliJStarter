require "fileutils"

def bail_with(project)
    puts ">>>> #{project} failed! <<<<"
    exit(1)
end

def rename_project_to(name)
  files = [ "AndroidIntelliJStarter.iml",
            ".idea/.name",
            "AndroidManifest.xml"] + Dir.glob('.idea/**/*.xml')
  puts "Searching: #{files.join(",")}\n"          
  files.each do |filename|
    replace(filename, "AndroidIntelliJStarter", name)
  end
  
  puts ">>> Renamed 'AndroidIntelliJStarter.iml' to '#{name}.iml'"
  File.rename "AndroidIntelliJStarter.iml", "#{name}.iml"

  # delete workspace.xml, which might hold bad references. It will be regenerated.
  File.delete ".idea/workspace.xml" if File.exists? ".idea/workspace.xml" 
  
  init_git_repo
end

def replace(filename, original_name, new_name) 
  text = File.read(filename)
  replaced = text.gsub!(original_name, new_name)
  File.open(filename, "w") {|file| file.puts text}
  puts ">>> Replaced '#{original_name}' in #{filename} with '#{new_name}'" if replaced
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
    system "echo '' > .gitmodules"
    system "git add ."
    system "git ci -am 'Initial Commit'"
    
    reset_robolectric
  else 
    puts "!!! You typed '#{should_init}'. Leaving existing git repository."
    puts "!!! Run ./script/init_git (or ruby script/init_git) to try again."
  end  
end

def reset_robolectric
  puts "Add Robolectric as a non-pushable submodule pointing at HEAD"
  
  system "git rm --cached submodules/robolectric"
  FileUtils.rm_rf "submodules"
  system "echo ''> .gitmodules"
  
  system "git submodule add git://github.com/pivotal/robolectric.git submodules/robolectric"
  system "git submodule update --init"
end  

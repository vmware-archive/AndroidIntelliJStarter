def bail_with(project)
    puts ">>>> #{project} failed! <<<<"
    exit(1)
end


def rename_project_to(name)
  Dir.glob('.idea/**/*.xml').each do |filename|
      text = File.read(filename).gsub("AndroidIntelliJStarter", "AndroidNapikins")
      File.open(filename, "w") {|file| file.puts text}
  end
  
  text = File.read(".idea/.name").gsub("AndroidIntelliJStarter", "AndroidNapikins")
  File.open(".idea/.name", "w") {|file| file.puts text}
end
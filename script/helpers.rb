def bail_with(project)
    puts ">>>> #{project} failed! <<<<"
    exit(1)
end


def rename_to(name)
  Dir.glob('.idea/**/*.xml').each do |filename|
    File.open(filename, File::RDWR) do |file|
      line 
      p line.gsub!("AndroidIntelliJStarter", "AndroidNapikins")
    end
  end
end

File.open("readfile.rb", "r") do |infile|
    while (line = infile.gets)
      puts "#{counter}: #{line}"
        counter = counter + 1
    end
end
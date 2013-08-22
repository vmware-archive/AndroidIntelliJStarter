### Current Issues/ToDos

- Build release apks without requiring a password. This will enable auto-generation of apks in continuous integration.

# Android IntelliJ Starter
This is a "template" IntelliJ project created to bootstrap Android development.
We have included as many of our go-to tools and as much hard earned
configuration knowledge as possible to aid new projects. This includes out-of-the-box support for
robolectric, robojuice, C2DM, great-expectations, android source Jars, and other important libraries.

## Configuration By Deletion
There is a lot of stuff in here, everything from android source jars to dependency injection;
we acknowledge that your project might not want all of it. We encourage your project to remove
whatever you don't want.

## Assumptions
We make the following assumptions. Feel free to deviate but you will likely need to fix some
things as you go.

- You are working on a Mac
- Your android SDK is in ~/android-sdk-macosx, or you are going to put it there, or create a symlink, etc.
- Robolectric will live in submodules/robolectric inside your project directory. (We'll put it there for you.)
- You have java, ruby and git installed

**Don't open IntelliJ yet.**
# Summary Instructions (for Power Users)

- **Don't open IntelliJ yet.** Did you already launch it? Close it.
- Install Android to ~/android-sdk-macosx/
- Install Android platform tools, Android 2.3.3 with Google APIs, and the most recent Android with Google APIs (needed for Robolectric). You change the Android version for your project later.

        android update sdk -u --filter platform-tools,android-10,addon-google_apis-google-10,extra-android-support,android-16,addon-google_apis-google-16

- Create a local git repo for your new project, or create one on GitHub and clone it. http://help.github.com/create-a-repo/
- Optionally, fork the robolectric repo on GitHub if you wish to use a fork for your project to make it easy to contribute changes back to robolectric.
  We recommend that you fork robolectric.
  Go to https://github.com/pivotal/robolectric and click the "Fork" button.
- Clone the AndroidIntelliJStarter project, then add all of its code and setup to your new project's repo by running:

        git clone git://github.com/pivotal/AndroidIntelliJStarter starter_tmp
        cd starter_tmp
        ./script/setup_project YourProjectName path_to_your_project_repo #or ruby script/setup_project ...

        # This script will prompt you for a package name and a robolectric repo, both are optional.
        # Default package name: com.example.android.sampleapp
        # Default robolectric repo is the read-only offical robolectric repo: git://github.com/pivotal/robolectric.git

		# To make sure everything is OK, run tests for both Robolectric and your project:
    	cd path_to_your_project_repo && (cd submodules/robolectric && ant clean test) && ant clean test


- **Open YourProject in IntelliJ 10.5 or higher**.
- Import IntelliJ Settings: File => Import Settings => YourProject/support/IntellijSettings.jar.
This will destroy your existing IntelliJ settings!

Notes:

- "Import Settings" should have fixed the global Project SDKs and Module SDKs. Fix them if they are still broken.
See "4. IntelliJ: Some Manual Configuration" below.
- Robolectric's unit test suite requires SDK 10 with Google APIs (2.3.3). If you do not install this SDK you cannot run Robolectric's tests.
- Once you are done, you can delete the starter_tmp directory.

In IntelliJ, Run Unit Tests, Robolectric Unit Tests, and launch StarterApp and make sure they work.

At least glance at the stuff below about robojuice, C2DM, gp and gpp, forking robolectric, etc.

Stuck? Keep reading!

# Super Detailed Instructions
Salvation lies within.

## 1. Android Setup
**Don't open IntelliJ yet.**

Download the latest Mac SDK: http://developer.android.com/sdk/index.html

Unzip the archive and move the android-sdk-macosx dir to ~/android-sdk-macosx.
*This project assumes that android lives in ~/android-sdk-macosx*. You will need to
fix paths in several places if you choose a different location.

Add the android tools to the PATH.

    # Note: change .bash_profile to .bashrc or something else if needed
    echo 'export PATH="$PATH:$HOME/android-sdk-macosx/tools:$HOME/android-sdk-macosx/platform-tools"' >> $HOME/.bash_profile

Open a new Terminal window and run `android`:

    # in a new Terminal window:
    android

Use the "Android SDK and AVD Manager" to download all of the SDKs. This project assumes SDK v10 (2.3.3) with Google APIs.
You can change this later.

To install:

- Available packages => Android Repository => SDK Platform 2.3.3
- Available packages => Third party Add-ons => Google Inc => Google APIs by Google Inc., Android API 10

Install other SDKs using this method.

### Virtual Devices
Make at least one Virtual Device (emulator) for the SDK(s) you installed above.  Instructions at http://developer.android.com/tools/devices/index.html

Note: This project assumes you have SDK 10 with Google APIs (2.3.3) installed. You can change this in
`build.properties`.

## 2. Setting Up Your New Project's Repo
**Don't open IntelliJ yet.**

Before you can start, you have to have a git repo for your new project on your machine.

If you are using GitHub, to create a new repo for your project, create it on GitHub and clone it to your local machine.

If you are NOT using GitHub, just create a repo on your local machine for your project (i.e. `git init`).

Optionally, if you are using `git pair` on your project, create a .pairs file in your new project's repo and run `git pair`.

Finally, clone `pivotal/AndroidIntelliJStarter` be sure to use the *read-only* URI to avoid accidentally pushing
changes to it.

    git clone git://github.com/pivotal/AndroidIntelliJStarter starter_tmp

### Project Setup Script
`setup_project` will rename the files and file contents that need to be changed from
AndroidIntelliJStarter to YourProject and copy/commit all of the files into your project's repo.

    cd starter_tmp
    ./script/setup_project YourProject path_to_your_project_repo

This script will prompt you for a package name and a robolectric repo, both are optional.

- The default package name is `com.example.android.sampleapp`.
- Robolectric is a git submodule in this project. By default, submodules/robolectric is a non-pushable clone of
http://github.com/pivotal/robolectric (HEAD). You can specify your own fork at the prompt.

We recommend that you fork robolectric for your project. For details on how to set up your fork
to easily sync with pivotal/robolectric, see "Open Source Robolectric" below.

Note that Robolectric unit test require SDK v10 (2.3.3) with Google APIs. If you do not install this SDK
then you will not be able to run Robolectric's own test suite.

## 3. IntelliJ: Settings, Libraries, and SDKs
**Open YourProject in IntelliJ 10.5 or higher.**

### Import IntellijSettings.jar
Import support/IntellijSettings.jar to automatically configure your SDKs and other important settings:

File => Import Settings => YourProject/support/IntellijSettings.jar

If everything goes well everything will be fixed when IntelliJ restarts.

### Troubleshooting: My IntelliJ SDKs are Broken!
Something about your machine's configuration does not match our settings. Manually fix all
using the following instructions. Likely issues include:

- Are you are not running IntelliJ 11.X?
- Android SDKs are not installed in ~/android-sdk-macosx/.

If these are not the issue keep going to the SDK sections below.

### Platform Settings: SDKs -- JSDK

- File => Project Structure
- Platform Settings => SDKs

If 1.6 is not listed, add it:

- Add (plus sign) => JSDK
- Take the default if you can, deep in /System/Library/.../CurrentJDK/Home

### Platform Settings: SDKs -- Android SDKs
Check your Android SDKs:

- File => Project Structure
- Platform Settings => SDKs

Your Android SDKs are listed here. You might need to add a few. Note that if you want to run Robolectric's
own test suite you will need to add Google APIs (2.3.3). For example:

- Add (plus sign) => Android SDK
- locate and choose ~/android-sdk-macosx
- Select internal Java Platform: 1.6
- Create new Android SDK: Google APIs (2.3.3)

If you need SDKs that are not listed you will need to install it via the
Android SDK and AVD Manager. See above.

### Module SDKs
You might need to fix the Module SDKs for YourProject and Robolectric:

- File => Project Structure
- Modules => YourProject => Dependencies
- Module SDK: choose one.
- Repeat for Modules => Robolectric => Dependencies

# Highlighted Libraries And Tools
We have included several libraries and configurations that we use on most projects. You are free to
keep them or remove them.

## Roboguice
By default this project uses Roboguice for dependency injection. https://github.com/roboguice/roboguice

Configure dependency injection in MySampleApplication.ApplicationModule and
RobolectricTestRunnerWithInjection.TestApplicationModule.

RobolectricTestRunnerWithInjection is a test runner configured to use Roboguice.
See StarterActivityWithRoboguiceTest for example usage.

### To remove Roboguice:
- Delete MySampleApplication and remove references
- Delete RobolectricTestRunnerWithInjection and remove references
- Delete guice* and roboguice* jars in libs/main and libs-src/
- Remove all uses of @Inject, @InjectView, etc.
- Remove reference to MySampleApplication from AndroidManifest


## Fest
We've included the Fest expectation matchers to provide Jasmine-style expectations to your project. The fest-android
extensions are also included for simpler android-specific tests.

Further documentation at http://fest.easytesting.org/ and https://github.com/square/fest-android

## Lots of Jars
We have added many handy Jars, such as apache commons, google's Guava, the Jackson JSON parsing libraries,
and more. Check them out in `libs/main/` and `libs/test`-- keep them or delete them.

## Scripts
These Ruby scripts should make your life easier. Feel free to edit them. They assume ruby
lives in `/usr/bin/ruby` so you might need to edit their `#!/usr/bin/ruby` if yours is different. Alternatively
these scripts can be run with ruby explicitly: `ruby script/[the script]`.

Be sure to check out "Project Setup Script", above, for more details on many of these scripts.

- `script/gp` -- "Git Pull" script. This pulls and rebases your project and robolectric.
- `script/gpp` -- "Git Pull Push" script. Same as script/gp but also runs all tests in robolectric.
and your project. If they pass it will `git push`.
- `script/setup_project [YourProject] [path_to_your_project_repo]` -- Copies AndroidIntelliJStarter into a project
git repo, and gives the project a name of [YourProject].
- `script/set_package` -- Change the Java package from the default to the provided package name.
- `script/init_git_repo` -- create a new local git repository. Existing `.git` directory safely moved to `.git.bak`.

## ant (DEPRECATED)
In addition to the built-in Android `ant` tasks you will likely use the following additions often.
You can chain them, such as `ant clean test`. Feel free to edit `build.xml` to fit your needs.

- `ant clean` -- deletes all output dirs
- `ant test` -- executes the project tests

## Maven
There are a couple of useful maven tasks:

- `mvn clean` -- deletes all output dirs
- `mvn test` -- executes the project tests
- `mvn install` -- writes the target (your apk by default) to the local maven repo

## Open Source Robolectric
Robolectric is open source and it continuously improves. We recommend that your project fork robolectric.
By forking you have the freedom to choose when (if ever) to update to later versions of robolectric,
make changes to your fork as needed, and contribute those changes back to pivotal/robolectric using
the official github pull-request workflow.

### Merging in pivotal/robolectric
The official Github workflow (http://help.github.com/fork-a-repo/) details how to merge another
repo's code into your own fork, such as merging pivotal/robolectric into yourproject/robolectric:

Do the following once per machine:

    # add pivotal/robolectric HEAD as an upstream remote
	cd submodules/robolectric
    git remote add upstream git://github.com/pivotal/robolectric.git

When you want to merge in upstream:

    # merge pivotal/robolectric into your fork
    cd submodules/robolectric
    git fetch upstream
    git merge --no-commit upstream/master

Then:

- Fix merge conflicts
- Run robolectric tests
- Run main project tests
- Commit robolectric and push
- Commit project and push

### Contributing Back

Make a pull request: http://help.github.com/send-pull-requests/

### Changing from using the default Robolectric to using your own fork
If you started with the default (non-pushable submodule) robolectric, you can change your mind later and set up your
project to use your own fork later by following these instructions.

.gitmodules -- delete the '[submodule "submodules/robolectric"]' section if present.

.git/config -- delete the '[submodule "robolectric"]' section if present.

Clean up git and directories

    git rm --cached submodules/robolectric
    rm -rf submodules

After forking robolectric on Github, add a submodule that points to your robolectric repository:

    git submodule add ***YOUR-GIT-REPOSITORY-URI-HERE*** submodules/robolectric
    git submodule init
    (cd submodules/robolectric && ant clean test)

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
- Your android SDK is in ~/android-sdk-mac_x86, or you are going to put it there, or create a symlink, etc.
- Robolectric will live in submodules/robolectric
- You have java, ruby and git installed

# Summary Instructions (for Power Users)
**Don't open IntelliJ yet.**

So you know what you're doing, eh? Let's do this thing!

- **Don't open IntelliJ yet.** Did you already launch it? Close it.
- Install Android to ~/android-sdk-mac_x86/
- Install SDKs -- this project assumes SDK 10 with Google APIs (2.3.3). You change this later.
- Create a local git repo for your new project, or create one on GitHub and clone it. http://help.github.com/create-a-repo/
- Optionally, fork the robolectric repo on GitHub if you wish to use a fork for your project to make it easy to contribute changes back to robolectric.
  We recommend that you fork robolectric.
  Go to https://github.com/pivotal/robolectric and click the "Fork" button.
- Clone this project to get the code, then add all of its code to your new project's repo by running:

        git clone git://github.com/pivotal/AndroidIntelliJStarter starter_tmp
        cd starter_tmp
        ./script/project_setup YourProjectName path_to_your_project_repo #or ruby script/project_setup ...

        # This script will prompt you for a package name and a robolectric repo, both are optional.
        # Default package name: com.example.android.sampleapp
        # Default robolectric repo is the read-only offical robolectric repo: git://github.com/pivotal/robolectric.git

		# Run tests: 
    	cd path_to_your_project_repo && (cd submodules/robolectric && ant clean findAndroidUnix && ant test) && ant clean test

- **Open YourProject in IntelliJ 10.5 or higher**.
- Import IntelliJ Settings: File => Import Settings => YourProject/support/IntellijSettings.jar. 
This will destroy your existing IntelliJ settings!

Notes:

- "Import Settings" should have fixed the global Project SDKs and Module SDKs. Fix them if they are still broken.
See "4. IntelliJ: Some Manual Configuration" below.
- Robolectric's unit test suite requires SDK 10 with Google APIs (2.3.3). If you do not install this SDK you cannot run Robolectric's tests.
- Once you are done, you can delete the starter_tmp directory.

In IntelliJ, Run Unit Tests, Robolectric Unit Tests, and launch StarterApp and make sure they work.

At least glace at the stuff below about robojuice, C2DM, gp and gpp, forking robolectric, etc.

Stuck? Keep reading!

# Super Detailed Instructions
Salvation lies within.

## 1. Android Setup
**Don't open IntelliJ yet.**

Download the latest Mac SDK: http://developer.android.com/sdk/index.html

Unzip the archive and move the android-sdk-mac_x86 dir to ~/android-sdk-mac_x86. 
*This project assumes that android lives in ~/android-sdk-mac_x86*. You will need to 
fix paths in several places if you choose a different location.

Add the android tools to the PATH. 

    # Note: change .bash_profile to .bashrc or something else if needed
    echo "export PATH='$PATH:$HOME/android-sdk-mac_x86/tools:$HOME/android-sdk-mac_x86/platform-tools'" >> $HOME/.bash_profile

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
Make at least one Virtual Device (emulator) for the SDK(s) you installed above.

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
`project_setup` will rename the files and file contents that need to be changed from
AndroidIntelliJStarter to YourProject and copy/commit all of the files into your project's repo.

    cd starter_tmp
    ./script/project_setup YourProject path_to_your_project_repo

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

### My IntelliJ SDKs are Broken!
Something about your machine's configuration does not match our settings. Manually fix all 
using the following instructions. Likely issues include: 

- Are you are not running IntelliJ 10.5
- Android SDKs are not installed in ~/android-sdk-mac_x86/. Check out that "x"! It's "mac_x86", not "mac_86".

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
- locate and choose ~/android-sdk-mac_x86
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
By default this project uses Roboguice for dependency injection. http://code.google.com/p/roboguice/

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

## C2DM
We have added base support for C2DM - http://code.google.com/android/c2dm/. C2DM is Google's push 
notification service used for Android and is available in API v. 2.2 and above, though it is 
safely ignored in lower versions.

While 2.2 devices support C2DM, Android SKDs do not provide hooks for integrating with the service -- no
registration, unregistration, or notification-receipt handling code. Google suggests copying code from one of
their sample projects for this support, which we have done.  See com.google.android.c2dm. Note that we modified
com.google.android.c2dm.C2DMBaseReceiver to support Roboguice.

To handle C2DM notifications you will need to implement C2DMReceiver, which is stubbed-out but heavily documented.

### C2DM Resources:
- Official C2DM site: http://code.google.com/android/c2dm
- Pivotal Blog articles: http://pivotallabs.com/blabs/categories/c2dm
- Source of com.google.android.c2dm: http://www.google.com/codesearch#JWblrwroAxw/trunk/android/c2dm/com/google/android/c2dm/&q=C2DMBaseReceiver&type=cs

### To Remove C2DM:
- Remove C2DMReceiver and test
- Delete com.google.android.c2dm
- Remove the C2DM Section of AndroidManifest.xml

## Great Expectations
great-expectations (https://github.com/xian/great-expectations) provides Jasmine-style test expectations for Java.
While the framework is a little rough around the edges, we love this style of expectation assertions; see
`com.example.android.sampleapp.StarterActivityTestWithGreatExpectations.java`.

### Robolectric-gem (Robolectric Great Expectations Matchers)
Robolectric-gem is a library of matcher classes for using great-expectations in Robolectric-powered unit tests.
For example, it provides a matcher on `View` called `toBeVisible()`:

    TextView titleView = (TextView) activity.findViewById(R.id.title);
    expect(titleView).toBeVisible();

You can add your own matcher classes too. Add the robolectric-gem repo as a submodule instead of using the jar,
or keep it as a jar file and add your own matcher classes by subclassing RunnableExpectGenerator and running that
class to create your own Expect.java in your project.

For more information, see https://github.com/pivotal/robolectric-gem

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
- `script/project_setup [YourProject]` -- changes this project from being configured for 
AndroidIntelliJStarter to YourProject, including a creating a new local git repo if desired.
- `script/set_package` -- Change the Java package from the default to the provided package name.
- `script/init_git_repo` -- create a new local git repository. Existing `.git` directory safely moved to `.git.bak`.

## ant
In addition to the build-in Android `ant` tasks you will likely use the following additions often. 
You can chain them, such as `ant clean test`. Edit `build.xml` at will. In addition to the 

- `ant clean` -- deletes up all output dirs
- `ant test` -- executes the project tests

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

- Fix merge conflicts
- Run robolectric tests
- Run main project tests
- Commit robolectric and push
- Commit project and push

### Contributing Back
***Note: get permission from your client before contributing code back to any open source project.***

Assuming you forked as detailed above, make a pull request as your client user: http://help.github.com/send-pull-requests/

The pull request can be handled by someone with commit right to robolectric, maybe even you!
See "Managing Pull Requests" at http://help.github.com/send-pull-requests/.

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

Also see *Open Source Robolectric*.

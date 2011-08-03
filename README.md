## Android Setup
Download the latest Mac SDK from here: http://developer.android.com/sdk/index.html

Unzip the archive and move the android-sdk-mac_x86 dir to ~/android-sdk-mac_x86. 
*This project assumes that android lives in ~/android-sdk-mac_x86*. You will need to 
fix paths in several places if you choose a different location.

Add the android tools to the PATH. Assuming the SDK directory is ~/android-sdk-mac_x86
and that you are using `.bash_pivotal` rather than `.bash_profile` or `.bashrc`:

    # Note: change .bash_pivotal to .bash_profile or .bashrc if needed
    echo "export PATH='$PATH:$HOME/android-sdk-mac_x86/tools'" >> $HOME/.bash_pivotal

Open a new Terminal window and run `android`:
 
    # in a new Terminal window:
    android 

Use the "Android SDK and AVD Manager" to download all of the SDKs you think you need.
If you need Google Maps then install the Google APIs under 
Available packages => Third party Add-ons.

Note: This project assumes you have SDK Platform Android 2.1 installed. You can change this in 
`default.properties`

## Bootstrapping AndroidIntelliJStarter
You will likely fork and rename this repository on Github. If you clone this repo be sure to use the *read-only* url
to avoid accidentally making changes to this template project.

    git clone git://github.com/pivotal/AndroidIntelliJStarter # or your fork
    cd AndroidIntelliJStarter

Generate local.properties

    android update project -p .

*If this fails with the following error* then either install the SDK referenced
in `default.properties` or change the "target" within that file appropriately.

    Error: The project either has no target set or the target is invalid.
    Please provide a --target to the 'android update' command.

## Robolectric
Robolectric is a git submodule in this project. By default, submodules/robolectric is a read-only clone of
http://github.com/pivotal/robolectric (HEAD). If you want to fork robolectric 
(recommended) skip to *Forking Robolectric* below.

### Initializing Robolectric (HEAD by default)
    git submodule update --init
    (cd submodules/robolectric && git checkout master)
    (cd submodules/robolectric && ant clean test) # make sure it runs

### Forking Robolectric (Recommended)
We recommend forking robolectric for your project. By forking you have the freedom to choose when (if ever) 
to update to later versions of robolectric, make changes to your fork as needed, and contribute
those changes back to pivotal/robolectric using the official github pull-request workflow (http://help.github.com/fork-a-repo/.)
Start by removing the default pivotal/robolectric:

.gitmodules -- delete the '[submodule "submodules/robolectric"]' section if present.

.git/config -- delete the '[submodule "robolectric"]' section if present.

Clean up git and directories

    git rm --cached submodules/robolectric
    rm -rf submodules

After forking robolectric on Github, add a submodule that points to your robolectric repository:

    git submodule add ***YOUR-GIT-REPOSITORY-URI-HERE*** submodules/robolectric
    git submodule init
    (cd submodules/robolectric && ant clean test)

Also see *Contributing back to Robolectric* below.

## IntelliJ: Some Manual Configuration
Open the project in IntelliJ 10.5 or higher.

### Platform SDKs
You will likely need to configure IntelliJ's Platform Android SDKs.  IntelliJ stores Platform SDK
configurations somewhere outside of individual projects. Upshot: IntelliJ SDKs are not committed
in git and you will need to manually add them.

File => Project Structure

Platform Settings => SDKs

Add (plus sign) => Android SDK

locate and choose ~/android-sdk-mac_x86

Choose Java SDK 1.6

Choose your Android SDK. If you don't see it there you will need to install it via the
Android SDK and AVD Manager. See above.

### Setting AndroidIntelliJStarter's SDK
Now you need to set the app's SDK.

File => Project Structure

Modules => AndroidIntelliJStarter => Dependencies

Module SDK: choose one.

## Roboguice
By default this project uses Roboguice for dependency injection. 

Configure dependency injection in MySampleApplication.ApplicationModule and RobolectricTestRunnerWithInjection.TestApplicationModule.

RobolectricTestRunnerWithInjection is a test runner configured to use Roboguice. 
See StarterActivityWithRoboguiceTest for example usage.

### To remove Roboguice:
- Delete MySampleApplication and remove references
- Delete RobolectricTestRunnerWithInjection and remove references
- Delete guice* and roboguice* jars in libs/main and libs-src/
- Remove all uses of @Inject, @InjectView, etc.
- Remove reference to MySampleApplication from AndroidManifest

## C2DM Support
We have added base support for C2DM. C2DM is Google's push notification service for Android and is available in
API v. 2.2 and above, though it is safely ignored in lower versions.

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

--------------
## Open Source Robolectric
Robolectric is open source and it continuously improves. We recommend that you merge with pivotal/robolectric
often to both stay current. We also recommend you contribute your projects's changes back to the community. 

### Merging in pivotal/robolectric
The official Github workflow (http://help.github.com/fork-a-repo/) details how to merge another 
repo's code into your own fork, such as merging pivotal/robolectric into yourproject/robolectric:

    # add pivotal/robolectric HEAD as an upstream remote
    cd submodules/robolectric
    git remote add upstream git://github.com/pivotal/robolectric.git
    git fetch upstream

    # merge pivotal/robolectric into your fork
    git merge upstream/master

Resolve conflicts, fix test, commit, and push.

### Contributing
***Note: get permission from your client before contributing code back to any open source project.***

Assuming you forked as detailed above, make a pull request as your client user: http://help.github.com/send-pull-requests/

The pull request can be handled by someone with commit right to robolectric, maybe even you!
See "Managing Pull Requests" at http://help.github.com/send-pull-requests/.
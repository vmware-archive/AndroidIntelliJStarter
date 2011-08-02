Instructions on installing pivotal's git scripts for supporting `git pair xx yy`, for example.

Based on IntelliJ 10.5

# Robolectric:
robolectric starts out as a read-only submodule of pivotal/robolectric (HEAD).

## To initialize robolectric:
    git submodule init
    (cd submodules/robolectric && ant clean test) # make sure it runs

## Forking Robolectric
At this time we recommend forking robolectric for your project.

Start by removing the default pivotal/robolectric

1. Delete the relevant line from the .gitmodules file.
2. Delete the relevant section from .git/config.
3. Clean up git and directories:

    git rm --cached submodules/robolectric
    rm -rf submodules
    See "Robolectric" above, but use your own robolectric repository git uri.

Add your own fork

    git submodule add git://github.com/***YOUR-REPO-HERE***/robolectric.git submodules/robolectric
    git submodule init
    cd submodules/robolectric
    (cd submodules/robolectric && ant clean test)


## Contributing back to Robolectric
Contributing back to pivotal/robolectric is accomplished by following the official Github fork
model: http://help.github.com/fork-a-repo/

    # add pivotal/robolectric HEAD as an upstream remote
    cd submodules/robolectric
    git remote add upstream git://github.com/pivotal/robolectric.git
    git fetch upstream

    # merge pivotal/robolectric into your fork
    git merge upstream/master

Next, make a pull request as your client user: http://help.github.com/send-pull-requests/

The pull request can be handled by someone with commit right to robolectric, maybe even you!
See "Managing Pull Requests" at http://help.github.com/send-pull-requests/.

# Roboguice
This project is set up by default to use Roboguice for dependency injection.  See MySampleApplication.ApplicationModule
and SampleRoboguiceTestRunner.TestApplicationModule for injection configuration.  See StarterActivityWithRoboguiceTest
for an example of injection usage.

## To remove Roboguice injection:
- Delete MySampleApplication and remove references
- Delete SampleRoboguiceTestRunner and remove references
- Delete guice* and roboguice* jars in libs/main and libs-src/
- Remove all uses of @Inject, @InjectView, etc.
- Remove reference to MySampleApplication from AndroidManifest

# C2DM Support
We have added base support for C2DM. C2DM is Google's push notification service for Android and is available in
API v. 2.2 and above, though is safely ignored in lower versions.

While 2.2 devices are support C2DM, Google does not provide API code for integrating with the service -- no
registration, unregistration, or notification-receipt handling code. Google suggests copying code from one of
their sample projects for this support, which we have done.  See com.google.android.c2dm. Note that we modified
com.google.android.c2dm.C2DMBaseReceiver to support Roboguice.

To handle C2DM notifications you will need to implement C2DMReceiver, which is currently empty but heavily documented.

## C2DM Resources:
- Official C2DM site: http://code.google.com/android/c2dm
- Pivotal Blog articles: http://pivotallabs.com/blabs/categories/c2dm
- Sample code: http://www.google.com/codesearch#JWblrwroAxw/trunk/android/c2dm/com/google/android/c2dm/&q=C2DMBaseReceiver&type=cs

## To Remove C2DM:
- Remove C2DMReceiver and test
- Delete com.google.android.c2dm
- Remove the C2DM Section of AndroidManifest.xml

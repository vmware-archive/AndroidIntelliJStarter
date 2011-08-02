Instructions on installing pivotal's git scripts for supporting `git pair xx yy`, for example.

Based on IntelliJ 10.5

# Robolectric:
# read-only submodule of pivotal/robolectric (HEAD)
git submodule add git://github.com/pivotal/robolectric.git submodules/robolectric
git submodule init
cd submodules/robolectric
(cd submodules/robolectric && ant clean test)

# Instruction for removing the default robolectric and adding your own fork:
- Delete the relevant line from the .gitmodules file.
- Delete the relevant section from .git/config.
- git rm --cached submodules/robolectric
- rm -rf submodules
- See "Robolectric" above, but use your own robolectric repository git uri.


# Roboguice
This project is set up by default to use Roboguice for dependency injection.  See MySampleApplication.ApplicationModule
and SampleRoboguiceTestRunner.TestApplicationModule for injection configuration.  See StarterActivityWithRoboguiceTest
for an example of injection usage.

To remove Roboguice injection:
- Delete MySampleApplication and remove references
- Delete SampleRoboguiceTestRunner and remove references
- Delete guice* and roboguice* jars in libs/main and libs-src/
- Remove all uses of @Inject, @InjectView, etc.
- Remove reference to MySampleApplication from AndroidManifest

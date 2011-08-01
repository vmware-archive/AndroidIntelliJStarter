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

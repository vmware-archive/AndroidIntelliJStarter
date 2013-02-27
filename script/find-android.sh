#!/bin/sh

# ANDROID_HOME has been set before running ant (e.g. ANDROID_HOME=/usr/local/Cellar/android-sdk/r10 ant)

if [ -z $ANDROID_HOME ]; then
  ANDROID_HOME=`which android | sed 's/\/tools\/android$//'`
fi

echo "sdk.dir=$ANDROID_HOME" > local.properties

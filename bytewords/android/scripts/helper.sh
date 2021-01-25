#!/bin/bash

set -e

JAVA_ROOT=$(
  cd ../java
  pwd
)

source "$JAVA_ROOT"/scripts/helper.sh

install_cmdline_tool() {
  FILE=commandlinetools-linux-6858069_latest.zip
  if is_osx; then
    FILE=commandlinetools-mac-6858069_latest.zip
  fi
  wget -O "$FILE" -q "https://dl.google.com/android/repository/$FILE"

  unzip $FILE >/dev/null
  rm -f $FILE
}

install_android_sdk() {
  SDK_DIR=$1
  COMPILE_SDK_VERSION=$2
  BUILD_TOOL_VERSION=$3
  NDK_VERSION=$4
  CMAKE_VERSION=$5

  if [ ! -f "cmdline-tools/bin/sdkmanager" ]; then
    echo "Installing command line tool..."
    install_cmdline_tool
  fi

  pushd "cmdline-tools/bin"

  echo y | ./sdkmanager --sdk_root="$SDK_DIR" "platforms;android-$COMPILE_SDK_VERSION"
  echo y | ./sdkmanager --sdk_root="$SDK_DIR" "build-tools;$BUILD_TOOL_VERSION"
  echo y | ./sdkmanager --sdk_root="$SDK_DIR" "ndk;$NDK_VERSION"
  echo y | ./sdkmanager --sdk_root="$SDK_DIR" "cmake;$CMAKE_VERSION"

  popd
}

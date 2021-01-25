#!/bin/bash

set -e

source scripts/helper.sh

ANDROID_SDK_DIR=$HOME/Android

if ! is_osx; then
  deps=(wget sudo unzip)
  echo "Checking and installing dependencies '${deps[*]}'..."
  apt-get update
  for dep in "${deps[@]}"; do
    check_dep "$dep"
  done
fi

# Check for JDK
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ $java_version < "1.8.0" ]]; then
  pushd "$HOME"
  echo "Installing JDK 8..."
  install_java || exit
  popd
else
  echo "JDK 8 has been installed at $JAVA_HOME"
fi

# Check for Android SDK
if [[ -z $ANDROID_SDK_ROOT ]]; then
  pushd "$HOME"
  echo "Installing Android SDK..."
  mkdir -p "$ANDROID_SDK_DIR"
  install_android_sdk "$ANDROID_SDK_DIR" 30 "30.0.2" "21.0.6113669" "3.10.2.4988404"
  echo "Android SDK has been installed at '$ANDROID_SDK_DIR'"
  popd
else
  echo "Android SDK has been installed at '$ANDROID_SDK_ROOT'"
fi

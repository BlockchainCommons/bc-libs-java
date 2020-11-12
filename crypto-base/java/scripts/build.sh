#!/bin/bash

source scripts/helper.sh

sskr_lib_name="libbc-crypto-base-jni.dylib"
out_dir=build/release
jni_md_dir="darwin"
if is_osx; then
  export CC="clang"
  export CXX="clang++"
else
  sskr_lib_name="libbc-crypto-base-jni.so"
  jni_md_dir="linux"

  export CC="clang-10"
  export CXX="clang++-10"

fi

java_home="/usr/java/jdk8u265-b01"
if is_osx; then
  java_home=$(/usr/libexec/java_home 2>/dev/null)
fi

if [ "$JAVA_HOME" == "" ]; then
  export JAVA_HOME=$java_home
fi

# Install bc-crypto-base
pushd ../../deps/bc-crypto-base || exit
./configure
make clean
make CFLAGS=-fPIC check
sudo make CFLAGS=-fPIC install
popd || exit

# Install jni lib
echo "Building $sskr_lib_name..."
mkdir -p $out_dir
$CC -I$JAVA_HOME/include -I$JAVA_HOME/include/$jni_md_dir -fexceptions -frtti -shared -fPIC src/main/jniLibs/*.c ../../base-jni/*.c ../../deps/bc-crypto-base/src/libbc-crypto-base.a -o $out_dir/$sskr_lib_name || exit
echo "Done. Checkout the release file at $out_dir/$sskr_lib_name"

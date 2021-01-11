#!/bin/bash

set -e

source scripts/helper.sh

echo "${JAVA_HOME:?}"
echo "${CC:?}"

PARENT_ROOT_DIR=$(
  cd ../..
  pwd
)
LIB_NAME="libbc-bip39-jni.dylib"
OUT_DIR=src/main/libs
JNI_MD_DIR="darwin"
if ! is_osx; then
  LIB_NAME="libbc-bip39-jni.so"
  JNI_MD_DIR="linux"
fi
BUILD_LOG_DIR="log"
BUILD_LOG="$BUILD_LOG_DIR/$(date +%s)-log.txt"

clean_up() {
  ./scripts/cleanup.sh
}

build_bc_crypto_base() {
  pushd "$PARENT_ROOT_DIR/deps/bc-crypto-base"
  ./configure
  make clean
  make CFLAGS=-fPIC check
  make CFLAGS=-fPIC install
  popd
}

build_bc_bip39() {
  pushd "$PARENT_ROOT_DIR/deps/bc-bip39"
  ./configure
  make clean
  make CFLAGS=-fPIC check
  popd
}

build_jni() {
  mkdir -p $OUT_DIR
  $CC -I"$JAVA_HOME/include" \
    -I"$JAVA_HOME/include/$JNI_MD_DIR" \
    -I"$PARENT_ROOT_DIR/base-jni" \
    -I"$PARENT_ROOT_DIR/deps/bc-bip39/src" \
    -fexceptions -frtti -shared -fPIC \
    src/main/jniLibs/*.c \
    "$PARENT_ROOT_DIR"/base-jni/*.c \
    "$PARENT_ROOT_DIR"/deps/bc-bip39/src/libbc-bip39.a \
    "$PARENT_ROOT_DIR"/deps/bc-crypto-base/src/libbc-crypto-base.a \
    -o \
    $OUT_DIR/$LIB_NAME
}

(
  mkdir -p ${BUILD_LOG_DIR}
  echo -n >"${BUILD_LOG}"

  echo 'Cleanup...'
  clean_up

  echo 'Building bc-crypto-base...'
  build_bc_crypto_base

  echo 'Building bc-bip39...'
  build_bc_bip39

  echo "Building $LIB_NAME..."
  build_jni
  echo "Done. Checkout the release file at $OUT_DIR/$LIB_NAME"
) | tee "${BUILD_LOG}"

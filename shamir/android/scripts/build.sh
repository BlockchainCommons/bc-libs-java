#!/bin/bash

set -e

source scripts/helper.sh

echo "${JAVA_HOME:?}"
echo "${ANDROID_SDK_ROOT:?}"
PARENT_ROOT_DIR=$(
  cd ../..
  pwd
)
BUILD_LOG_DIR="log"
BUILD_LOG="$BUILD_LOG_DIR/$(date +%s)-log.txt"

copy_headers() {
  mkdir -p /usr/local/include/bc-crypto-base
  cp "$PARENT_ROOT_DIR"/deps/bc-crypto-base/src/*.h /usr/local/include/bc-crypto-base
}

(
  mkdir -p ${BUILD_LOG_DIR}
  echo -n >"${BUILD_LOG}"

  copy_headers

  ./gradlew clean assembleRelease --info
  echo "DONE. Checkout 'app/build/outputs/aar/app-release.aar'"

) | tee "${BUILD_LOG}"
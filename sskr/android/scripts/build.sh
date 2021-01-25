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
  mkdir -p /usr/local/include/bc-shamir
  cp "$PARENT_ROOT_DIR"/deps/bc-crypto-base/src/*.h /usr/local/include/bc-crypto-base
  cp "$PARENT_ROOT_DIR"/deps/bc-shamir/src/*.h /usr/local/include/bc-shamir
}

(
  mkdir -p ${BUILD_LOG_DIR}
  echo -n >"${BUILD_LOG}"

  copy_headers

  ./gradlew clean
  OUTPUT=app/build/outputs/aar/app-release.aar
  case $1 in
  --test-only)
    ./gradlew connectedDebugAndroidTest --info
    ;;
  --bundle-only)
    ./gradlew assembleRelease --info
    echo "DONE. Checkout '$OUTPUT'"
    ;;
  *)
    ./gradlew connectedDebugAndroidTest assembleRelease --info
    echo "DONE. Checkout '$OUTPUT'"
    ;;
  esac

) | tee "${BUILD_LOG}"
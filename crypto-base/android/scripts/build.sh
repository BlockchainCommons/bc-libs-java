#!/bin/bash

set -e

source scripts/helper.sh

echo "${JAVA_HOME:?}"
echo "${ANDROID_SDK_ROOT:?}"
BUILD_LOG_DIR="log"
BUILD_LOG="$BUILD_LOG_DIR/$(date +%s)-log.txt"

(
  mkdir -p ${BUILD_LOG_DIR}
  echo -n >"${BUILD_LOG}"

  ./gradlew clean assembleRelease --info
  echo "DONE. Checkout 'app/build/outputs/aar/app-release.aar'"

) | tee "${BUILD_LOG}"
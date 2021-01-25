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
# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)
project(bc-shamir)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

get_filename_component(PARENT_ROOT_DIR ../../ ABSOLUTE)
get_filename_component(ROOT_DIR ${CMAKE_SOURCE_DIR} DIRECTORY)

# define source files
file(GLOB BC_CRYPTO_BASE ${PARENT_ROOT_DIR}/deps/bc-crypto-base/src/*.c)
file(GLOB BC_SHAMIR ${PARENT_ROOT_DIR}/deps/bc-shamir/src/*.c)
set(TARGET_INCLUDE_DIRS ${PARENT_ROOT_DIR}/base-jni ${PARENT_ROOT_DIR}/deps/bc-shamir/src)

# define include dir
include_directories(/usr/local/include)

# compile
add_library(bc-crypto-base STATIC ${BC_CRYPTO_BASE})

add_library(bc-shamir STATIC ${BC_SHAMIR})

add_library(jni-utils STATIC ${PARENT_ROOT_DIR}/base-jni/jni-utils.c)

add_library(bc-shamir-jni SHARED ${ROOT_DIR}/java/src/main/jniLibs/shamir-jni.c)

# link
target_link_libraries(bc-shamir bc-crypto-base)

target_link_libraries(bc-shamir-jni jni-utils)

target_link_libraries(bc-shamir-jni bc-shamir)

# target headers
target_include_directories(bc-shamir-jni PRIVATE ${TARGET_INCLUDE_DIRS})
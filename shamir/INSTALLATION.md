# Installation for Blockchain Commons Shamir Secret Sharing Java library
This document gives the instruction for installing the Blockchain Commons Shamir Secret Sharing Java library.

## Dependencies
> We add utility script for installing all system dependencies, you can find it at `java/scripts/install_deps.sh`
If you want to do it manually by yourself, make sure all of following dependencies are installed correctly. 

[Adopt Open JDK 1.8](https://github.com/AdoptOpenJDK/openjdk8-binaries/releases) is recommended for both MacOS and Linux.

### Linux
> Following packages can be installed via `apt-get`

- automake
- make
- libc++-10-dev (or above)
- libc++abi-10-dev (or above)
- clang-10 (or above)

### MacOS
> Following packages can be installed via `brew`
- automake
- make

## Android
> Working directory: `/android`

### Testing
```console
$ ./gradlew clean connectedDebugAndroidTest
```

### Bundling
```console
$ ./gradlew clean assembleRelease
```

> The `app-release.aar` file would be found in `app/build/outputs/aar`. You can compile it as a library in your project.

> Notice that we distribute a prebuilt aar file at `prebuilt` directory.


## Java (Web app/ Desktop app)
> Working directory: `/java`

### Build native library
Run following command for building the native library.
```console
$ sudo ./scripts/build.sh
```

> The native library file would be found at `src/main/libs`. You need to install it into `java.library.path` for JVM can load it at runtime.

### Testing
The test tasks automatically points JVM `java.library.path` to `src/main/libs` so make sure you already built the native library before executing the test.

Run following command for executing test cases.
```console
$ ./gradlew test --info
```

### Bundling
The `jar` file will be bundled by running
```console
$ ./gradlew assemble
```

> `jar` file just contain all `.class` files for running pure Java, no dynamic library is carried with.
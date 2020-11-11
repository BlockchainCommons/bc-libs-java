#!/bin/bash

source scripts/helper.sh

deps=(automake make)

if ! is_osx; then
  # Additional dependencies for Linux
  deps+=(wget libc++-10-dev libc++abi-10-dev openjdk-8-jdk)
fi

echo "Checking and installing dependencies '${deps[*]}'..."

# Check and install missing dependencies
for dep in "${deps[@]}"; do
  check_dep $dep
done

if is_osx; then
  java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
  if [[ $java_version != "1.8.0_265" ]]; then
    echo "Installing JDK 8..."
    install_java8_mac || exit
  else
    echo "JDK 8 has been installed at $(/usr/libexec/java_home 2>/dev/null)"
  fi
else
  if clang-10 --version 2>/dev/null; then
    echo "clang-10 already installed"
  else
    echo "Installing clang-10..."
    wget https://apt.llvm.org/llvm.sh
    chmod +x llvm.sh
    ./llvm.sh 10 || exit
    rm llvm.sh
  fi
fi

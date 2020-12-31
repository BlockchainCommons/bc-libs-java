#!/bin/bash

source scripts/helper.sh

deps=(automake make)

if ! is_osx; then
  # Additional dependencies for Linux
  deps+=(wget sudo unzip lsb-release software-properties-common apt-transport-https)
fi

echo "Checking and installing dependencies '${deps[*]}'..."

# Check and install missing dependencies
if ! is_osx; then
  apt-get update
fi
for dep in "${deps[@]}"; do
  check_dep $dep
done

# Check for JDK
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ $java_version < "1.8.0" ]]; then
  echo "Installing JDK 8..."
  install_java || exit
else
  echo "JDK 8 has been installed at $JAVA_HOME"
fi

# Check for clang10 on Linux
if ! is_osx; then
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

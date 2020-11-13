#!/bin/bash

check_dep() {
  dep=$1
  echo $dep
  if is_osx; then
    if brew ls --versions "$dep" >/dev/null; then
      echo "Package '$dep' already installed"
    else
      echo "Installing '$dep'..."
      echo y | brew install "$dep"
    fi
  else
    if dpkg -s "$dep" >/dev/null; then
      echo "Package '$dep' already installed"
    else
      echo "Installing '$dep'..."
      echo y | apt-get install "$dep"
    fi
  fi
}

is_osx() {
  [[ "$(uname)" == "Darwin" ]]
}

install_java() {
  FILE=OpenJDK8U-jdk_x64_linux_hotspot_8u265b01.tar.gz
  if is_osx; then
    FILE=OpenJDK8U-jdk_x64_mac_hotspot_8u265b01.pkg
  fi
  wget -O "$FILE" -q "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u265-b01/$FILE"

  if is_osx; then
    sudo installer -pkg $FILE -target /
  else
    sudo mkdir -p /usr/java
    mv $FILE /usr/java
    cd /usr/java
    tar -xzvf $FILE
    sudo update-alternatives --install "/usr/bin/java" "java" "/usr/java/jdk8u265-b01/bin/java" 1
    sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/java/jdk8u265-b01/bin/javac" 1
  fi
  rm -f "$FILE"
}

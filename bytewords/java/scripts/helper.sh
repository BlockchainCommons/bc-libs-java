#!/bin/bash

check_dep() {
  DEP=$1
  echo "$DEP"
  if is_osx; then
    if brew ls --versions "$DEP" >/dev/null; then
      echo "Package '$DEP' already installed"
    else
      echo "Installing '$DEP'..."
      echo y | brew install "$DEP"
    fi
  else
    if dpkg -s "$DEP" >/dev/null; then
      echo "Package '$DEP' already installed"
    else
      echo "Installing '$DEP'..."
      echo y | apt-get install "$DEP"
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
    JAVA_DIR=/usr/local/java
    sudo mkdir -p $JAVA_DIR
    mv $FILE $JAVA_DIR
    cd $JAVA_DIR || exit
    tar -xzvf $FILE
    sudo update-alternatives --install "/usr/bin/java" "java" "$JAVA_DIR/jdk8u265-b01/bin/java" 1
    sudo update-alternatives --install "/usr/bin/javac" "javac" "$JAVA_DIR/jdk8u265-b01/bin/javac" 1
  fi
  rm -f "$FILE"
}

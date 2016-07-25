#!/usr/bin/env bash
#
# In order to test this crypto provider it must be JAR signed - JCE requirement. Classes have to be
# loaded from such JAR.
#
# Currently the easiest way to do this is to install "client" module (containing crypto provider)
# to a local repository. During installation process it is properly signed.
#
# When integration tests are started they use provider in the JAR file. 
#
if [ ! -f "client/main.properties" ]; then
    echo "You need to setup \"client/main.properties\" file in order to build signed provider JAR file"
    echo "Template file is available at client/main.properties.template"
    exit -2
fi

cd client && mvn -DskipTests=true install -P release,client && cd -

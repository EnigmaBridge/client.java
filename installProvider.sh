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
cd client && mvn -DskipTests=true install -P release,client && cd -

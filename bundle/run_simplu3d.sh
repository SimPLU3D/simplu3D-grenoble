#!/bin/bash
java -Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m -jar -Djava.library.path=./lib/native_libraries/linux-amd64 simplu3d-grenoble-1.0-SNAPSHOT-shaded.jar $*

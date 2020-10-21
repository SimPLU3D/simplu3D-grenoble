cd /D "%~dp0"





java -Xms8G -Xmx8G -XX:PermSize=256m -Djava.library.path=./lib/native_libraries/windows-amd64 -XX:MaxPermSize=256m -jar simplu3d-grenoble-1.0-SNAPSHOT-shaded.jar %*
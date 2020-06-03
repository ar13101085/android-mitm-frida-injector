

build jar cmd
./gradlew shadowJar

run jar
java -jar mitm-f.jar *.apk -d -f


-d debug disable
-f frida inject disable
-m mitm disable

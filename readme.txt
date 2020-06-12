

build jar cmd
./gradlew shadowJar

run jar
java -jar mitm-f.jar *.apk -d


-d debug disable
-f frida inject disable
-m mitm disable


/Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home
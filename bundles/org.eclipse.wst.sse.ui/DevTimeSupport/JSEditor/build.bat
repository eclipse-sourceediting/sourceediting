javac -g -classpath C:\java\j2sdkee1.2.1\lib\j2ee.jar  *.java
@if ERRORLEVEL 1 goto done
java  -Djava.compiler= -classpath .;C:\java\j2sdkee1.2.1\lib\j2ee.jar MakeJavaReflectFCRecs
:done

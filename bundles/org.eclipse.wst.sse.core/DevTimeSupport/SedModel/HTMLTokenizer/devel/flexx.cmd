@echo off
PATH=%PATH%;c:\jdk1.4.2_04\bin
java -Xmx470000000 -cp d:\JFlex\lib\JFlex.jar;. JFlex.Main XMLTokenizer.jflex -skel skeleton.sse && rm -f XMLTokenizer.java~ XMLTokenizer~ && copy XMLTokenizer.java ..\..\..\..\..\org.eclipse.wst.sse.core.xml\src\com\ibm\sse\model\xml\internal\parser\XMLTokenizer.java

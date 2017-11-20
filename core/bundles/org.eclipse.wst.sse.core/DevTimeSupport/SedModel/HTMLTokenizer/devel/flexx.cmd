@echo off
PATH=%PATH%;c:\jdk1.4.2_08\bin
java -Xmx470000000 -cp d:\JFlex\1.2.2\lib\JFlex.jar;. JFlex.Main XMLTokenizer.jflex -skel skeleton.sse && rm -f XMLTokenizer.java~ XMLTokenizer~ && copy XMLTokenizer.java ..\..\..\..\..\org.eclipse.wst.xml.core\src\org\eclipse\wst\xml\core\internal\parser\XMLTokenizer.java

@echo off
set JAVA_HOME=d:\jdk6_03
set JFLEX_HOME=D:\JFlex\jflex-1.4.2

%JAVA_HOME%\bin\java -Xmx470M -jar %JFLEX_HOME%\lib\JFlex.jar CSSTokenizer.jflex
move CSSTokenizer.java ..\..\..\..\..\org.eclipse.wst.css.core\src\org\eclipse\wst\css\core\internal\parser\ && del CSSTokenizer.java*

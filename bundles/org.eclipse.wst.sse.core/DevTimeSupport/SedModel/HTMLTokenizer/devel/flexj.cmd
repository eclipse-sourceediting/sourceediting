@echo off
PATH=%PATH%;c:\jdk1.4.2_05\bin
java -Xmx470000000 -cp d:\JFlex\1.2.2\lib\JFlex.jar;. JFlex.Main JSPTokenizer.jflex -skel skeleton.sse && rm -f JSPTokenizer.java~ JSPTokenizer~ && copy JSPTokenizer.java ..\..\..\..\..\org.eclipse.jst.jsp.core\src\org\eclipse\jst\jsp\core\internal\parser\\internal\JSPTokenizer.java

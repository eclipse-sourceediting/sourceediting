@echo off
PATH=%PATH%;c:\jdk1.4.2_04\bin
java -Xmx470000000 -cp d:\JFlex\lib\JFlex.jar;. JFlex.Main JSPTokenizer.jflex -skel skeleton.sse && rm -f JSPTokenizer.java~ JSPTokenizer~ && copy JSPTokenizer.java ..\..\..\..\..\org.eclipse.wst.sse.core.jsp\src\com\ibm\sse\model\jsp\parser\internal\JSPTokenizer.java

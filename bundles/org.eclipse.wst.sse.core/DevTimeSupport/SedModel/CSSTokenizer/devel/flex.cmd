@echo off
set PATH=%PATH%;c:\jdk1.4\bin
java -Xmx470000000 -cp JFlex\lib\sed-jflex.jar;. JFlex.Main JSPTokenizer -skel skeleton.sed
java -Xmx470000000 -cp JFlex\lib\sed-jflex.jar;. JFlex.Main XMLTokenizer -skel skeleton.sed
rm -f JSPTokenizer.java~ JSPTokenizer~ XMLTokenizer.java~ XMLTokenizer~
copy XMLTokenizer.java ..\..\..\..\sedmodel\com\ibm\sed\parser\internal
copy JSPTokenizer.java ..\..\..\..\sedmodel\com\ibm\sed\parser\internal

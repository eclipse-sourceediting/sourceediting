#!/bin/sh
export PATH=$PATH:/opt/IBMJava2-131/bin/:/opt/IBMJava2-13/bin/:/opt/jdk1.4
java -Xmx470000000 -cp JFlex/lib/sed-jflex.jar;. JFlex.Main JSPTokenizer.jflex -skel skeleton.sse
java -Xmx470000000 -cp JFlex/lib/sed-jflex.jar;. JFlex.Main XMLTokenizer.jflex -skel skeleton.sse
rm -f JSPTokenizer.java~ JSPTokenizer~ XMLTokenizer.java~ XMLTokenizer~
cp -v XMLTokenizer.java ../../../../sedmodel/com/ibm/sed/parser/internal
cp -v JSPTokenizer.java ../../../../sedmodel/com/ibm/sed/parser/internal

#!/bin/sh

JAVADIR=C:/App/IBM/Java141/bin
JAVAOPT=-Xmx470000000
CLASSPATH=../../HTMLTokenizer/devel/JFlex/lib/sed-jflex.jar\;.
DESTDIR=../../../../../org.eclipse.wst.sse.core.css/src/com/ibm/sse/model/css/internal/parser

#export PATH=$PATH:/opt/IBMJava2-131/bin/:/opt/IBMJava2-13/bin/:/opt/jdk1.4
#java -Xmx470000000 -cp JFlex/lib/sed-jflex.jar;. JFlex.Main CSSTokenizer -skel skeleton.sse

$JAVADIR/java $JAVAOPT -cp $CLASSPATH JFlex.Main CSSTokenizer.jflex

rm -f CSSTokenizer.java~ CSSTokenizer.jflex~
cp -v CSSTokenizer.java $DESTDIR
#$JAVADIR/javac $DESTDIR/*.java

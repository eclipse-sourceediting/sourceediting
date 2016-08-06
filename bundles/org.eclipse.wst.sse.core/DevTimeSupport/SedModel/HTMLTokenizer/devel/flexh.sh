#!/bin/sh
java -cp $JFLEX:. JFlex.Main HTMLTokenizer.jflex -skel skeleton.sse && cp -v HTMLTokenizer.java ../../../../../org.eclipse.wst.html.core/src/org/eclipse/wst/html/core/internal/parser/HTMLTokenizer.java

#!/bin/sh
java -cp JFlex-1.2.2.jar JFlex.Main HTMLHeadTokenizer.jFlex -skel skeleton && cp -v HTMLHeadTokenizer.java  ../../../../../../web/bundles/org.eclipse.wst.html.core/src/org/eclipse/wst/html/core/internal/contenttype/

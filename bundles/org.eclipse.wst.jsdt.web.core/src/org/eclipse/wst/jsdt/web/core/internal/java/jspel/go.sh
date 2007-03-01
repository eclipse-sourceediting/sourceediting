#!/usr/bin/sh

# Set JAVACC_HOME to the installation directory of javacc 3.2
$JAVACC_HOME/bin/jjtree.bat JSPEL.jjt
$JAVACC_HOME/bin/javacc.bat JSPEL.jj
sed -f fixtm.sed -i.bak JSPELParserTokenManager.java
diff -w JSPELParserTokenManager.java JSPELParserTokenManager.java.bak
sed -f fixparser.sed -i.bak JSPELParser.java
diff -w JSPELParser.java JSPELParser.java.bak

#!/usr/bin/sh
#*******************************************************************************
# Copyright (c) 2005, 2018 IBM Corporation and others.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     IBM Corporation - initial API and implementation
#*******************************************************************************

# Set JAVACC_HOME to the installation directory of javacc 3.2
$JAVACC_HOME/bin/jjtree.bat JSPEL.jjt
$JAVACC_HOME/bin/javacc.bat JSPEL.jj
sed -f fixtm.sed -i.bak JSPELParserTokenManager.java
diff -w JSPELParserTokenManager.java JSPELParserTokenManager.java.bak
sed -f fixparser.sed -i.bak JSPELParser.java
diff -w JSPELParser.java JSPELParser.java.bak

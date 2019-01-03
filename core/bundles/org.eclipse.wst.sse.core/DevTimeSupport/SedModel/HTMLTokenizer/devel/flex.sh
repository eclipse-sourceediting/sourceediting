#!/bin/sh
#*******************************************************************************
# Copyright (c) 2004, 2018 IBM Corporation and others.
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
export PATH=$PATH:/opt/IBMJava2-131/bin/:/opt/IBMJava2-13/bin/:/opt/jdk1.4
java -Xmx470000000 -cp JFlex/lib/sed-jflex.jar;. JFlex.Main JSPTokenizer.jflex -skel skeleton.sse
java -Xmx470000000 -cp JFlex/lib/sed-jflex.jar;. JFlex.Main XMLTokenizer.jflex -skel skeleton.sse
rm -f JSPTokenizer.java~ JSPTokenizer~ XMLTokenizer.java~ XMLTokenizer~
cp -v XMLTokenizer.java ../../../../sedmodel/com/ibm/sed/parser/internal
cp -v JSPTokenizer.java ../../../../sedmodel/com/ibm/sed/parser/internal

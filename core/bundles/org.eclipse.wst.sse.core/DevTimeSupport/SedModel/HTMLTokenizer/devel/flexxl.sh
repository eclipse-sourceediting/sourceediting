#*******************************************************************************
# Copyright (c) 2013, 2018 IBM Corporation and others.
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
java -Xmx512M -cp ~/eclipse.wtp/JFlex-1.2.2/lib/JFlex.jar:. JFlex.Main XMLLineTokenizer.jflex -skel XMLLineTokenizer-skeleton.sse  && cp -v XMLLineTokenizer.java ../../../../../org.eclipse.wst.xml.core/src/org/eclipse/wst/xml/core/internal/parser/

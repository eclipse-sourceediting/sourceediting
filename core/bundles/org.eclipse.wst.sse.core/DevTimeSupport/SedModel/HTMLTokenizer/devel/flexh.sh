#!/bin/sh
#*******************************************************************************
# Copyright (c) 2016, 2018 IBM Corporation and others.
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
java -cp $JFLEX:. JFlex.Main HTMLTokenizer.jflex -skel skeleton.sse && cp -v HTMLTokenizer.java ../../../../../org.eclipse.wst.html.core/src/org/eclipse/wst/html/core/internal/parser/HTMLTokenizer.java

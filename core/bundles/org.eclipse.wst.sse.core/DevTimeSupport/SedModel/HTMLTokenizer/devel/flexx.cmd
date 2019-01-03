@rem ***************************************************************************
@rem Copyright (c) 2004, 2018 IBM Corporation and others.
@rem This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License 2.0
@rem which accompanies this distribution, and is available at
@rem https://www.eclipse.org/legal/epl-2.0/
@rem
@rem SPDX-License-Identifier: EPL-2.0
@rem
@rem Contributors:
@rem     IBM Corporation - initial API and implementation
@rem ***************************************************************************
@echo off
PATH=%PATH%;c:\jdk1.4.2_08\bin
java -Xmx470000000 -cp d:\JFlex\1.2.2\lib\JFlex.jar;. JFlex.Main XMLTokenizer.jflex -skel skeleton.sse && rm -f XMLTokenizer.java~ XMLTokenizer~ && copy XMLTokenizer.java ..\..\..\..\..\org.eclipse.wst.xml.core\src\org\eclipse\wst\xml\core\internal\parser\XMLTokenizer.java
